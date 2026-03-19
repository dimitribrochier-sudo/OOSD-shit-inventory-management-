package app.inventory_management.views;

import app.inventory_management.controllers.ReceiptController;
import app.inventory_management.models.Receipt;
import app.inventory_management.models.Receipt.ReceiptItem;
import app.inventory_management.models.Receipt.Status;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ReceiptScreen extends JFrame {

    private static final Font F_TITLE     = new Font("Serif",     Font.BOLD,  20);
    private static final Font F_BODY      = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font F_SMALL     = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font F_TABLE     = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font F_TABLE_HDR = new Font("SansSerif", Font.BOLD,  12);
    private static final Font F_STATUS    = new Font("SansSerif", Font.PLAIN, 10);

    private final String     currentUser;
    private final Connection connection;

    private JTable            receiptTable;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JLabel            statusLabel;

    // Keeps the numeric orderId per row so we can load items by orderId
    private java.util.List<Integer> orderIdList = new java.util.ArrayList<>();

    public ReceiptScreen(String currentUser, Connection connection) {
        this.currentUser = currentUser;
        this.connection  = connection;

        setTitle("Receipt Manager");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        buildUI();
        loadReceipts();
        setVisible(true);
    }

    private void buildUI() {
        add(buildTopBar(),     BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildStatusBar(),  BorderLayout.SOUTH);
    }

    // ── Top Bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JLabel title = new JLabel("Receipt Manager");
        title.setFont(F_TITLE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(F_SMALL);
        searchField = new JTextField(20);
        searchField.setFont(F_BODY);
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { filterTable(); }
        });

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(F_BODY);
        refreshBtn.addActionListener(e -> loadReceipts());

        JButton backBtn = new JButton("Back");
        backBtn.setFont(F_BODY);
        backBtn.addActionListener(e -> dispose());

        right.add(searchLbl); right.add(searchField);
        right.add(refreshBtn); right.add(backBtn);
        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    // ── Table Panel ───────────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 14, 10, 14));

        JLabel subtitle = new JLabel("All Transactions — click any row to view full receipt");
        subtitle.setFont(F_SMALL);
        subtitle.setForeground(new Color(100, 100, 100));
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        String[] columns = {
                "Receipt ID", "Date", "Time", "Customer",
                "Subtotal (Rs)", "VAT (Rs)", "Total (Rs)", "Status"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        receiptTable = new JTable(tableModel);
        receiptTable.setFont(F_TABLE);
        receiptTable.setRowHeight(26);
        receiptTable.setShowGrid(true);
        receiptTable.setGridColor(Color.LIGHT_GRAY);
        receiptTable.setSelectionBackground(new Color(210, 225, 245));
        receiptTable.setSelectionForeground(Color.BLACK);
        receiptTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JTableHeader hdr = receiptTable.getTableHeader();
        hdr.setFont(F_TABLE_HDR);
        hdr.setBackground(new Color(220, 225, 235));
        hdr.setForeground(Color.BLACK);
        hdr.setReorderingAllowed(false);

        receiptTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setFont(F_TABLE);
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                setHorizontalAlignment(col == 0 || col == 3 ? LEFT : CENTER);
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                    setForeground(Color.BLACK);
                }
                if (col == 7 && val != null) {
                    String s = val.toString();
                    setForeground(!sel
                            ? (s.equalsIgnoreCase("Paid")      ? new Color(0, 130, 0)
                            : s.equalsIgnoreCase("Pending")   ? new Color(180, 100, 0)
                            : Color.RED)
                            : Color.WHITE);
                    setFont(new Font("SansSerif", Font.BOLD, 11));
                }
                return this;
            }
        });

        int[] ws = {85, 95, 70, 150, 100, 90, 100, 85};
        for (int i = 0; i < ws.length; i++)
            receiptTable.getColumnModel().getColumn(i).setPreferredWidth(ws[i]);

        receiptTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = receiptTable.getSelectedRow();
                    if (row >= 0) openReceiptDetail(row);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(receiptTable);
        scroll.setBorder(BorderFactory.createEtchedBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(subtitle, BorderLayout.NORTH);
        panel.add(scroll,   BorderLayout.CENTER);
        return panel;
    }

    // ── Status Bar ────────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));

        statusLabel = new JLabel("Loading…");
        statusLabel.setFont(F_STATUS);

        JLabel userLbl = new JLabel("Current User: " + currentUser);
        userLbl.setFont(F_STATUS);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(userLbl,     BorderLayout.EAST);
        return bar;
    }

    // ── Load Receipts ─────────────────────────────────────────────────────────
    private void loadReceipts() {
        tableModel.setRowCount(0);
        orderIdList.clear();

        if (connection == null) {
            // Demo data
            Object[][] sample = {
                    {"RC-1001","12-03-2026","09:15","Alice Morel",  "5200.00","780.00","5980.00","Paid"},
                    {"RC-1002","13-03-2026","11:30","Jean-Paul L.", "8500.00","1275.00","9775.00","Paid"},
                    {"RC-1003","14-03-2026","14:05","Priya Sharma", "3200.00","480.00","3680.00","Paid"},
                    {"RC-1004","17-03-2026","10:00","Dev Kumar",    "12000.00","1800.00","13800.00","Pending"},
                    {"RC-1005","18-03-2026","22:42","smrithi",      "102000.00","15300.00","117300.00","Paid"},
            };
            for (Object[] r : sample) {
                tableModel.addRow(r);
                orderIdList.add(-1); // no real orderId in demo mode
            }
            statusLabel.setText("Showing " + sample.length + " receipts  (demo mode)");
            return;
        }

        // Live DB — joins receipts → orders → customers
        // Note: receipts table has no status column — we use order_status from orders
        String sql =
                "SELECT r.receipt_id, r.order_id, r.subtotal, r.tax_amount, " +
                        "       r.total_amount, r.receipt_date, " +
                        "       o.order_status, " +
                        "       c.name AS customer_name " +
                        "FROM receipts r " +
                        "JOIN orders o    ON r.order_id    = o.order_id " +
                        "JOIN customers c ON o.customer_id = c.customer_id " +
                        "GROUP BY r.receipt_id " +
                        "ORDER BY r.receipt_date DESC";

        try (java.sql.Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int count = 0;
            while (rs.next()) {
                java.sql.Timestamp ts = rs.getTimestamp("receipt_date");
                String date = ts != null
                        ? ts.toLocalDateTime().toLocalDate().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "";
                String time = ts != null
                        ? ts.toLocalDateTime().toLocalTime().format(
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm")) : "";

                double sub = rs.getDouble("subtotal");
                double tax = rs.getDouble("tax_amount");
                if (tax == 0) tax = Math.round(sub * 0.15 * 100.0) / 100.0;

                tableModel.addRow(new Object[]{
                        rs.getString("receipt_id"),
                        date, time,
                        rs.getString("customer_name"),
                        String.format("%.2f", sub),
                        String.format("%.2f", tax),
                        String.format("%.2f", sub + tax),
                        rs.getString("order_status")   // from orders table
                });
                orderIdList.add(rs.getInt("order_id"));
                count++;
            }
            statusLabel.setText("Showing " + count + " receipts");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTable() {
        String q = searchField.getText().trim();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        receiptTable.setRowSorter(sorter);
        sorter.setRowFilter(q.isEmpty() ? null : RowFilter.regexFilter("(?i)" + q));
    }

    private void openReceiptDetail(int viewRow) {
        int modelRow  = receiptTable.convertRowIndexToModel(viewRow);
        String receiptId = tableModel.getValueAt(modelRow, 0).toString();
        String date      = tableModel.getValueAt(modelRow, 1).toString();
        String time      = tableModel.getValueAt(modelRow, 2).toString();
        String customer  = tableModel.getValueAt(modelRow, 3).toString();
        String subtotal  = tableModel.getValueAt(modelRow, 4).toString();
        String vat       = tableModel.getValueAt(modelRow, 5).toString();
        String total     = tableModel.getValueAt(modelRow, 6).toString();
        int    orderId   = orderIdList.get(modelRow);

        new ReceiptDetailWindow(this, receiptId, date, time,
                customer, subtotal, vat, total, orderId, connection);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  INNER CLASS — Receipt Detail Window
    // ════════════════════════════════════════════════════════════════════════
    static class ReceiptDetailWindow extends JDialog {

        private static final Font F_HDR_TITLE = new Font("Serif",     Font.BOLD,  16);
        private static final Font F_CARD_HDR  = new Font("SansSerif", Font.BOLD,  11);
        private static final Font F_KEY       = new Font("SansSerif", Font.BOLD,  12);
        private static final Font F_VAL       = new Font("SansSerif", Font.PLAIN, 12);
        private static final Font F_TABLE     = new Font("SansSerif", Font.PLAIN, 12);
        private static final Font F_TABLE_HDR = new Font("SansSerif", Font.BOLD,  12);
        private static final Font F_SUM_LBL   = new Font("SansSerif", Font.PLAIN, 13);
        private static final Font F_SUM_TOTAL = new Font("Serif",     Font.BOLD,  15);
        private static final Font F_BTN       = new Font("SansSerif", Font.BOLD,  12);

        private final Connection      connection;
        private final int             orderId;
        private       DefaultTableModel itemModel;
        private       JLabel          contactLbl, addressLbl;

        ReceiptDetailWindow(JFrame parent, String receiptId, String date, String time,
                            String customer, String subtotal, String vat, String total,
                            int orderId, Connection connection) {
            super(parent, "Receipt — " + receiptId, true);
            this.connection = connection;
            this.orderId    = orderId;

            setSize(580, 700);
            setLocationRelativeTo(parent);
            setResizable(false);
            setLayout(new BorderLayout());

            add(buildHeader(receiptId, date + "  " + time), BorderLayout.NORTH);
            add(buildBody(customer, subtotal, vat, total),  BorderLayout.CENTER);
            add(buildFooter(),                              BorderLayout.SOUTH);

            loadLineItems();
            loadCustomerDetails(customer);
            setVisible(true);
        }

        // ── Header ───────────────────────────────────────────────────────────
        private JPanel buildHeader(String id, String dateTime) {
            JPanel hdr = new JPanel(new BorderLayout());
            hdr.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)));

            JPanel stack = new JPanel(new GridLayout(3, 1, 0, 3));
            stack.setOpaque(false);

            JLabel official = new JLabel("OFFICIAL RECEIPT", SwingConstants.CENTER);
            official.setFont(new Font("SansSerif", Font.PLAIN, 11));
            official.setForeground(Color.GRAY);

            JLabel title = new JLabel("INVENTORY SYSTEM", SwingConstants.CENTER);
            title.setFont(F_HDR_TITLE);

            JLabel idLine = new JLabel("TRANSACTION ID: #" + id + "   |   DATE: " + dateTime,
                    SwingConstants.CENTER);
            idLine.setFont(new Font("SansSerif", Font.BOLD, 11));

            stack.add(official); stack.add(title); stack.add(idLine);
            hdr.add(stack, BorderLayout.CENTER);
            return hdr;
        }

        // ── Body ─────────────────────────────────────────────────────────────
        private JScrollPane buildBody(String customer, String subtotal,
                                      String vat, String total) {
            JPanel body = new JPanel();
            body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
            body.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));

            // Customer info
            JPanel custCard = sectionCard("Customer Information");
            infoRow(custCard, "CUSTOMER :", customer);
            contactLbl = infoRow(custCard, "CONTACT  :", "Loading…");
            addressLbl = infoRow(custCard, "ADDRESS  :", "Loading…");
            body.add(custCard);
            body.add(Box.createVerticalStrut(10));

            // Items table
            JPanel itemCard = sectionCard("Purchased Items");
            String[] cols = {"PRODUCT ID", "PRODUCT NAME", "QTY",
                    "UNIT PRICE (Rs)", "LINE TOTAL (Rs)"};
            itemModel = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable itemTable = new JTable(itemModel);
            itemTable.setFont(F_TABLE);
            itemTable.setRowHeight(26);
            itemTable.setShowGrid(true);
            itemTable.setGridColor(Color.LIGHT_GRAY);
            itemTable.getTableHeader().setFont(F_TABLE_HDR);
            itemTable.getTableHeader().setBackground(new Color(220, 225, 235));
            JScrollPane tableScroll = new JScrollPane(itemTable);
            tableScroll.setPreferredSize(new Dimension(520, 150));
            tableScroll.setBorder(BorderFactory.createEtchedBorder());
            itemCard.add(tableScroll);
            body.add(itemCard);
            body.add(Box.createVerticalStrut(10));

            // Summary
            JPanel sumCard = sectionCard("Summary");
            sumCard.setLayout(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(3, 6, 3, 6);

            addSummaryRow(sumCard, gc, 0, "SUBTOTAL:",      "Rs " + subtotal, false);
            addSummaryRow(sumCard, gc, 1, "TAX (15% VAT):", "Rs " + vat,      false);
            gc.gridy = 2; gc.gridx = 0; gc.gridwidth = 2;
            sumCard.add(new JSeparator(), gc);
            gc.gridwidth = 1;
            addSummaryRow(sumCard, gc, 3, "TOTAL AMOUNT:", "Rs " + total, true);
            body.add(sumCard);

            JScrollPane scroll = new JScrollPane(body);
            scroll.setBorder(null);
            return scroll;
        }

        // ── Footer ────────────────────────────────────────────────────────────
        private JPanel buildFooter() {
            JPanel foot = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
            foot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));

            JButton printBtn = new JButton("PRINT RECEIPT");
            printBtn.setFont(F_BTN);
            printBtn.addActionListener(e -> printReceipt());

            JButton closeBtn = new JButton("BACK TO DASHBOARD");
            closeBtn.setFont(F_BTN);
            closeBtn.addActionListener(e -> dispose());

            foot.add(printBtn);
            foot.add(closeBtn);
            return foot;
        }

        // ── Load Line Items ───────────────────────────────────────────────────
        // Reads directly from orders + products — this is where your data lives.
        // Your orders table has: order_id, product_id, quantity, unit_price, total_price
        private void loadLineItems() {
            if (connection == null) {
                itemModel.addRow(new Object[]{"P-1","Dell Inspiron 15 Laptop",   2,"45000.00","90000.00"});
                itemModel.addRow(new Object[]{"P-2","Logitech Wireless Mouse",  1,"1200.00","1200.00"});
                return;
            }
            String sql =
                    "SELECT o.product_id, p.name, o.quantity, o.unit_price, o.total_price " +
                            "FROM orders o " +
                            "JOIN products p ON o.product_id = p.product_id " +
                            "WHERE o.order_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    itemModel.addRow(new Object[]{
                            "P-" + rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            String.format("%.2f", rs.getDouble("unit_price")),
                            String.format("%.2f", rs.getDouble("total_price"))
                    });
                }
                if (itemModel.getRowCount() == 0) {
                    itemModel.addRow(new Object[]{"–", "No items found for this order", "–", "–", "–"});
                }
            } catch (SQLException ex) {
                itemModel.addRow(new Object[]{"–", "Error: " + ex.getMessage(), "–", "–", "–"});
            }
        }

        // ── Load Customer Details ─────────────────────────────────────────────
        // Reads contact_number and address from customers table via the order's customer_id.
        // Also uses customer_number and customer_address from orders as fallback.
        private void loadCustomerDetails(String customerName) {
            if (connection == null) {
                contactLbl.setText("+230 54770199");
                addressLbl.setText("La Caverne, Vacoas-Phoenix");
                return;
            }
            // Join orders → customers using orderId so we get the exact customer for this order
            String sql =
                    "SELECT c.contact_number, c.address, " +
                            "       o.customer_number, o.customer_address " +
                            "FROM orders o " +
                            "JOIN customers c ON o.customer_id = c.customer_id " +
                            "WHERE o.order_id = ? LIMIT 1";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    // Prefer customers table contact_number, fall back to orders.customer_number
                    String contact = rs.getString("contact_number");
                    if (contact == null || contact.isEmpty())
                        contact = rs.getString("customer_number");
                    String address = rs.getString("address");
                    if (address == null || address.isEmpty())
                        address = rs.getString("customer_address");

                    contactLbl.setText(contact  != null ? contact  : "N/A");
                    addressLbl.setText(address  != null ? address  : "N/A");
                } else {
                    contactLbl.setText("N/A");
                    addressLbl.setText("N/A");
                }
            } catch (SQLException ex) {
                contactLbl.setText("N/A");
                addressLbl.setText("N/A");
            }
        }

        // ── Print ─────────────────────────────────────────────────────────────
        private void printReceipt() {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                Graphics2D g2 = (Graphics2D) graphics;
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                double scale = Math.min(
                        pageFormat.getImageableWidth()  / getWidth(),
                        pageFormat.getImageableHeight() / getHeight());
                g2.scale(scale, scale);
                getContentPane().printAll(g2);
                return Printable.PAGE_EXISTS;
            });
            if (job.printDialog()) {
                try { job.print(); }
                catch (PrinterException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Print failed: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        // ── UI Helpers ────────────────────────────────────────────────────────
        private JPanel sectionCard(String title) {
            JPanel c = new JPanel();
            c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
            c.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(), title,
                    TitledBorder.LEFT, TitledBorder.TOP, F_CARD_HDR));
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            return c;
        }

        private JLabel infoRow(JPanel card, String key, String val) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            JLabel k = new JLabel(key);
            k.setFont(F_KEY);
            k.setPreferredSize(new Dimension(95, 20));
            JLabel v = new JLabel(val);
            v.setFont(F_VAL);
            row.add(k); row.add(v);
            card.add(row);
            return v;
        }

        private void addSummaryRow(JPanel panel, GridBagConstraints gc,
                                   int row, String label, String value, boolean bold) {
            gc.gridy = row; gc.gridx = 0; gc.weightx = 1;
            gc.anchor = GridBagConstraints.WEST;
            JLabel lbl = new JLabel(label);
            lbl.setFont(bold ? F_SUM_TOTAL : F_SUM_LBL);
            panel.add(lbl, gc);
            gc.gridx = 1; gc.weightx = 0;
            gc.anchor = GridBagConstraints.EAST;
            JLabel val = new JLabel(value);
            val.setFont(bold ? F_SUM_TOTAL : F_SUM_LBL);
            panel.add(val, gc);
            gc.anchor = GridBagConstraints.CENTER;
        }
    }
}