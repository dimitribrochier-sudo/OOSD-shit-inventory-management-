package app.inventory_management.repository;

import app.inventory_management.config.DBConnection;
import app.inventory_management.models.Receipt;
import app.inventory_management.models.Receipt.ReceiptItem;
import app.inventory_management.models.Receipt.Status;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ReceiptDAO — aligned to your exact receipts table:
 * receipts(receipt_id VARCHAR, order_id INT, subtotal, tax_amount, total_amount, receipt_date TIMESTAMP)
 * NO status column on receipts — status comes from orders.order_status
 */
public class ReceiptDAO {

    private final Connection connection = DBConnection.getConnection();

    // ── 1. SAVE HEADER ────────────────────────────────────────────────────────
    // receipts table has NO status column — omitted from INSERT
    public int saveReceipt(Receipt receipt) throws SQLException {
        String sql =
                "INSERT INTO receipts (receipt_id, order_id, subtotal, tax_amount, total_amount, receipt_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString   (1, receipt.getReceiptCode());
            ps.setInt      (2, receipt.getOrderId());
            ps.setDouble   (3, receipt.getSubtotal());
            ps.setDouble   (4, receipt.getVatAmount());
            ps.setDouble   (5, receipt.getTotalAmount());
            ps.setTimestamp(6, Timestamp.valueOf(
                    receipt.getReceiptDate().atTime(receipt.getReceiptTime())));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    // ── 2. SAVE ITEM ──────────────────────────────────────────────────────────
    public void saveReceiptItem(ReceiptItem item) throws SQLException {
        String sql =
                "INSERT INTO receipt_items (receipt_id, product_id, product_name, quantity, unit_price, line_total) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(item.getReceiptId())); // receipt_id is VARCHAR
            ps.setInt   (2, item.getProductId());
            ps.setString(3, item.getProductName());
            ps.setInt   (4, item.getQuantity());
            ps.setDouble(5, item.getUnitPrice());
            ps.setDouble(6, item.getLineTotal());
            ps.executeUpdate();
        }
    }

    // ── 3. LOAD ALL (for main JTable) ─────────────────────────────────────────
    // Joins receipts → orders → customers
    // Status comes from orders.order_status (enum: pending, paid, shipped, cancelled)
    public List<Receipt> loadAllReceipts() throws SQLException {
        List<Receipt> list = new ArrayList<>();
        String sql =
                "SELECT r.receipt_id, r.order_id, r.subtotal, r.tax_amount, " +
                        "       r.total_amount, r.receipt_date, " +
                        "       o.order_status, " +
                        "       c.customer_id, c.name AS customer_name, " +
                        "       c.contact_number, c.address " +
                        "FROM receipts r " +
                        "JOIN orders o    ON r.order_id    = o.order_id " +
                        "JOIN customers c ON o.customer_id = c.customer_id " +
                        "ORDER BY r.receipt_date DESC";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── 4. FIND BY ORDER ID ───────────────────────────────────────────────────
    public Receipt findReceiptById(int orderId) throws SQLException {
        String sql =
                "SELECT r.receipt_id, r.order_id, r.subtotal, r.tax_amount, " +
                        "       r.total_amount, r.receipt_date, " +
                        "       o.order_status, " +
                        "       c.customer_id, c.name AS customer_name, " +
                        "       c.contact_number, c.address " +
                        "FROM receipts r " +
                        "JOIN orders o    ON r.order_id    = o.order_id " +
                        "JOIN customers c ON o.customer_id = c.customer_id " +
                        "WHERE r.order_id = ? LIMIT 1";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    // ── 5. FIND BY RECEIPT CODE e.g. "RC-2" ──────────────────────────────────
    public Receipt findReceiptByCode(String receiptCode) throws SQLException {
        String sql =
                "SELECT r.receipt_id, r.order_id, r.subtotal, r.tax_amount, " +
                        "       r.total_amount, r.receipt_date, " +
                        "       o.order_status, " +
                        "       c.customer_id, c.name AS customer_name, " +
                        "       c.contact_number, c.address " +
                        "FROM receipts r " +
                        "JOIN orders o    ON r.order_id    = o.order_id " +
                        "JOIN customers c ON o.customer_id = c.customer_id " +
                        "WHERE r.receipt_id = ? LIMIT 1";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, receiptCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    // ── 6. LOAD LINE ITEMS ────────────────────────────────────────────────────
    // Reads from orders + products directly (source of truth)
    public List<ReceiptItem> loadReceiptItems(int orderId) throws SQLException {
        List<ReceiptItem> items = new ArrayList<>();
        String sql =
                "SELECT o.product_id, p.name, o.quantity, o.unit_price, o.total_price " +
                        "FROM orders o " +
                        "JOIN products p ON o.product_id = p.product_id " +
                        "WHERE o.order_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new ReceiptItem(
                            0, orderId,
                            rs.getInt   ("product_id"),
                            rs.getString("name"),
                            rs.getInt   ("quantity"),
                            rs.getDouble("unit_price"),
                            rs.getDouble("total_price")
                    ));
                }
            }
        }
        return items;
    }

    // ── 7. UPDATE STATUS ──────────────────────────────────────────────────────
    // Updates orders.order_status since receipts has no status column
    public void updateReceiptStatus(int orderId, Status status) throws SQLException {
        String statusVal;
        switch (status) {
            case PAID:      statusVal = "paid";      break;
            case CANCELLED: statusVal = "cancelled"; break;
            default:        statusVal = "pending";   break;
        }
        String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, statusVal);
            ps.setInt   (2, orderId);
            ps.executeUpdate();
        }
    }

    // ── 8. DELETE ─────────────────────────────────────────────────────────────
    public void deleteReceipt(int orderId) throws SQLException {
        String sql = "DELETE FROM receipts WHERE order_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }

    // ── 9. GET NEXT RECEIPT CODE ──────────────────────────────────────────────
    // receipt_id is VARCHAR like "RC-2", extract the number and increment
    public String getNextReceiptCode() throws SQLException {
        String sql =
                "SELECT COALESCE(MAX(CAST(SUBSTRING(receipt_id, 4) AS UNSIGNED)), 0) + 1 " +
                        "FROM receipts";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return "RC-" + rs.getInt(1);
        }
        return "RC-1";
    }

    // ── 10. GET CUSTOMER CONTACT ──────────────────────────────────────────────
    public String[] getCustomerContact(int customerId) throws SQLException {
        String sql = "SELECT contact_number, address FROM customers WHERE customer_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            rs.getString("contact_number"),
                            rs.getString("address")
                    };
                }
            }
        }
        return new String[]{"N/A", "N/A"};
    }

    // ── PRIVATE MAPPER ────────────────────────────────────────────────────────
    private Receipt mapRow(ResultSet rs) throws SQLException {
        Timestamp ts       = rs.getTimestamp("receipt_date");
        double    subtotal = rs.getDouble("subtotal");
        double    tax      = rs.getDouble("tax_amount");
        if (tax == 0) tax  = Math.round(subtotal * 0.15 * 100.0) / 100.0;
        double    total    = subtotal + tax;

        // Map lowercase enum values to Receipt.Status
        String rawStatus = rs.getString("order_status");
        Status status;
        if      (rawStatus == null)                           status = Status.PENDING;
        else if (rawStatus.equalsIgnoreCase("paid"))          status = Status.PAID;
        else if (rawStatus.equalsIgnoreCase("shipped"))       status = Status.PAID;
        else if (rawStatus.equalsIgnoreCase("cancelled"))     status = Status.CANCELLED;
        else                                                  status = Status.PENDING;

        return new Receipt(
                0,
                rs.getString("receipt_id"),
                rs.getInt   ("customer_id"),
                rs.getString("customer_name"),
                rs.getString("contact_number"),
                rs.getString("address"),
                rs.getInt   ("order_id"),
                ts.toLocalDateTime().toLocalDate(),
                ts.toLocalDateTime().toLocalTime(),
                subtotal, tax, total,
                status,
                "System"
        );
    }
}