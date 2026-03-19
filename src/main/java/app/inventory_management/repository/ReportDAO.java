package app.inventory_management.repository;

import app.inventory_management.config.DBConnection;
import app.inventory_management.models.Report;
import app.inventory_management.models.Report.ReportItem;
import app.inventory_management.models.Report.ReportPeriod;
import app.inventory_management.models.Report.ReportType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ReportDAO.java — Database layer for Reports.
 *
 * This is the ONLY class that writes SQL for reports.
 * It speaks directly to MySQL via DBConnection.
 *
 * Responsibilities:
 *   - saveReport()        → INSERT a new report header into `reports`
 *   - loadAllReports()    → SELECT all report headers (for a history list)
 *   - findReportById()    → SELECT one report header by ID
 *   - deleteReport()      → DELETE a report header (cascades to items)
 *   - loadReportItems()   → SELECT the detail rows for a given report
 *   - generateSalesItems()     → live query against sales/products tables
 *   - generateInventoryItems() → live query against products table
 *   - generateCustomerItems()  → live query against sales/customers tables
 *   - generateSupplierItems()  → live query against suppliers/products tables
 *   - getMonthlyChartData()    → 6-month aggregated totals for the bar chart
 */
public class ReportDAO {

    Connection connection = DBConnection.getConnection();

    // ══════════════════════════════════════════════════════════════════════════
    //  REPORT HEADER CRUD
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Saves a new report header row and returns the auto-generated report_id.
     * Call this after computing KPI totals so you can store them.
     */
    public int saveReport(Report report) throws SQLException {
        String sql = "INSERT INTO reports " +
                "(title, report_type, period, generated_date, generated_by, " +
                " total_sales, total_orders, total_items_sold, total_active_customers) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, report.getTitle());
        ps.setString(2, report.getReportType().name());
        ps.setString(3, report.getPeriod().name());
        ps.setDate  (4, Date.valueOf(report.getGeneratedDate()));
        ps.setString(5, report.getGeneratedBy());
        ps.setDouble(6, report.getTotalSales());
        ps.setInt   (7, report.getTotalOrders());
        ps.setInt   (8, report.getTotalItemsSold());
        ps.setInt   (9, report.getTotalActiveCustomers());

        ps.executeUpdate();

        // Return the auto-generated primary key
        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) return keys.getInt(1);
        return -1;
    }

    /**
     * Loads all report header rows ordered by most recent first.
     * Used to populate a "Report History" table.
     */
    public List<Report> loadAllReports() throws SQLException {
        List<Report> list = new ArrayList<>();

        String sql = "SELECT report_id, title, report_type, period, " +
                "generated_date, generated_by, total_sales, " +
                "total_orders, total_items_sold, total_active_customers " +
                "FROM reports ORDER BY generated_date DESC, report_id DESC";

        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new Report(
                    rs.getInt   ("report_id"),
                    rs.getString("title"),
                    ReportType  .valueOf(rs.getString("report_type").toUpperCase()),
                    ReportPeriod.valueOf(rs.getString("period").toUpperCase()),
                    rs.getDate  ("generated_date").toLocalDate(),
                    rs.getString("generated_by"),
                    rs.getDouble("total_sales"),
                    rs.getInt   ("total_orders"),
                    rs.getInt   ("total_items_sold"),
                    rs.getInt   ("total_active_customers")
            ));
        }
        return list;
    }

    /**
     * Finds a single report header by its ID.
     */
    public Report findReportById(int reportId) throws SQLException {
        String sql = "SELECT report_id, title, report_type, period, " +
                "generated_date, generated_by, total_sales, " +
                "total_orders, total_items_sold, total_active_customers " +
                "FROM reports WHERE report_id = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, reportId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Report(
                    rs.getInt   ("report_id"),
                    rs.getString("title"),
                    ReportType  .valueOf(rs.getString("report_type").toUpperCase()),
                    ReportPeriod.valueOf(rs.getString("period").toUpperCase()),
                    rs.getDate  ("generated_date").toLocalDate(),
                    rs.getString("generated_by"),
                    rs.getDouble("total_sales"),
                    rs.getInt   ("total_orders"),
                    rs.getInt   ("total_items_sold"),
                    rs.getInt   ("total_active_customers")
            );
        }
        return null;
    }

    /**
     * Deletes a report header. Set CASCADE in MySQL so report_items
     * are deleted automatically. See SQL schema notes at bottom of file.
     */
    public void deleteReport(int reportId) throws SQLException {
        String sql = "DELETE FROM reports WHERE report_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, reportId);
        ps.executeUpdate();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LIVE REPORT ITEM GENERATION
    //  These queries run against your existing tables (sales, products, etc.)
    //  and return ReportItem lists ready for the JTable.
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Generates Sales Report rows for a given period.
     * Joins: sale_items → products → categories → sales
     */
    public List<ReportItem> generateSalesItems(ReportPeriod period) throws SQLException {
        List<ReportItem> items = new ArrayList<>();

        String dateFilter = period.toSQLFilter("s.sale_date");
        String sql =
                "SELECT si.sale_item_id, s.sale_id, " +
                        "       DATE_FORMAT(s.sale_date, '%d-%m-%Y') AS item_date, " +
                        "       p.name AS item_name, " +
                        "       p.category AS category, " +
                        "       si.quantity, si.unit_price, si.line_total, s.status " +
                        "FROM sale_items si " +
                        "JOIN sales    s ON si.sale_id    = s.sale_id " +
                        "JOIN products p ON si.product_id = p.product_id " +
                        "WHERE " + dateFilter +
                        " ORDER BY s.sale_date DESC";

        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            items.add(new ReportItem(
                    rs.getInt   ("sale_item_id"),
                    rs.getInt   ("sale_id"),
                    rs.getString("item_date"),
                    rs.getString("item_name"),
                    rs.getString("category"),
                    rs.getInt   ("quantity"),
                    rs.getDouble("unit_price"),
                    rs.getDouble("line_total"),
                    rs.getString("status")
            ));
        }
        return items;
    }

    /**
     * Generates Inventory Report rows — current stock snapshot.
     * Period filter is ignored for inventory (always shows current state)
     * but kept as a parameter for API consistency.
     */
    public List<ReportItem> generateInventoryItems(ReportPeriod period) throws SQLException {
        List<ReportItem> items = new ArrayList<>();

        String sql =
                "SELECT p.product_id, p.product_id AS report_id, " +
                        "       DATE_FORMAT(CURDATE(), '%d-%m-%Y') AS item_date, " +
                        "       p.name AS item_name, " +
                        "       p.category, " +
                        "       p.current_stock AS quantity, " +
                        "       p.unit_price, " +
                        "       (p.current_stock * p.unit_price) AS line_total, " +
                        "       CASE WHEN p.current_stock <= p.reorder_level " +
                        "            THEN 'Low Stock' ELSE 'In Stock' END AS status " +
                        "FROM products p " +
                        "ORDER BY p.name ASC";

        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            items.add(new ReportItem(
                    rs.getInt   ("product_id"),
                    rs.getInt   ("report_id"),
                    rs.getString("item_date"),
                    rs.getString("item_name"),
                    rs.getString("category"),
                    rs.getInt   ("quantity"),
                    rs.getDouble("unit_price"),
                    rs.getDouble("line_total"),
                    rs.getString("status")
            ));
        }
        return items;
    }

    /**
     * Generates Customer Report rows for a given period.
     * Groups by customer, counts their orders and total spend.
     */
    public List<ReportItem> generateCustomerItems(ReportPeriod period) throws SQLException {
        List<ReportItem> items = new ArrayList<>();

        String dateFilter = period.toSQLFilter("s.sale_date");
        String sql =
                "SELECT c.customer_id, c.customer_id AS report_id, " +
                        "       DATE_FORMAT(MAX(s.sale_date), '%d-%m-%Y') AS item_date, " +
                        "       c.name AS item_name, " +
                        "       'Customer' AS category, " +
                        "       COUNT(DISTINCT s.sale_id) AS quantity, " +
                        "       AVG(s.total_amount) AS unit_price, " +
                        "       SUM(s.total_amount) AS line_total, " +
                        "       'Active' AS status " +
                        "FROM customers c " +
                        "JOIN sales s ON c.customer_id = s.customer_id " +
                        "WHERE " + dateFilter +
                        " GROUP BY c.customer_id, c.name " +
                        "ORDER BY line_total DESC";

        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            items.add(new ReportItem(
                    rs.getInt   ("customer_id"),
                    rs.getInt   ("report_id"),
                    rs.getString("item_date"),
                    rs.getString("item_name"),
                    rs.getString("category"),
                    rs.getInt   ("quantity"),
                    rs.getDouble("unit_price"),
                    rs.getDouble("line_total"),
                    rs.getString("status")
            ));
        }
        return items;
    }

    /**
     * Generates Supplier Report rows for a given period.
     * Shows each supplier and the total value of products they supply.
     */
    public List<ReportItem> generateSupplierItems(ReportPeriod period) throws SQLException {
        List<ReportItem> items = new ArrayList<>();

        String sql =
                "SELECT s.supplier_id, s.supplier_id AS report_id, " +
                        "       DATE_FORMAT(CURDATE(), '%d-%m-%Y') AS item_date, " +
                        "       s.name AS item_name, " +
                        "       'Supplier' AS category, " +
                        "       COUNT(p.product_id) AS quantity, " +
                        "       AVG(p.unit_price) AS unit_price, " +
                        "       SUM(p.current_stock * p.unit_price) AS line_total, " +
                        "       'Active' AS status " +
                        "FROM suppliers s " +
                        "LEFT JOIN products p ON s.supplier_id = p.supplier_id " +
                        "GROUP BY s.supplier_id, s.name " +
                        "ORDER BY line_total DESC";

        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            items.add(new ReportItem(
                    rs.getInt   ("supplier_id"),
                    rs.getInt   ("report_id"),
                    rs.getString("item_date"),
                    rs.getString("item_name"),
                    rs.getString("category"),
                    rs.getInt   ("quantity"),
                    rs.getDouble("unit_price"),
                    rs.getDouble("line_total"),
                    rs.getString("status")
            ));
        }
        return items;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CHART DATA — last 6 months monthly totals
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Returns two parallel double[] arrays for the bar chart:
     *   result[0] = monthly sales totals  (last 6 months)
     *   result[1] = monthly order counts  (last 6 months)
     * result[2] is a String[] of month labels ("Jan", "Feb" …)
     *
     * Returned as Object[] so the controller can unpack it.
     */
    public Object[] getMonthlyChartData() throws SQLException {
        double[] sales  = new double[6];
        double[] orders = new double[6];
        String[] labels = new String[6];

        String sql =
                "SELECT DATE_FORMAT(sale_date, '%b') AS month_label, " +
                        "       SUM(total_amount)             AS total_sales, " +
                        "       COUNT(sale_id)                AS total_orders " +
                        "FROM sales " +
                        "WHERE sale_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
                        "GROUP BY YEAR(sale_date), MONTH(sale_date) " +
                        "ORDER BY YEAR(sale_date) ASC, MONTH(sale_date) ASC " +
                        "LIMIT 6";

        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        int i = 0;
        while (rs.next() && i < 6) {
            labels[i] = rs.getString("month_label");
            sales [i] = rs.getDouble("total_sales");
            orders[i] = rs.getDouble("total_orders");
            i++;
        }

        // Fill any missing months with zeros so the chart always has 6 bars
        for (int j = i; j < 6; j++) labels[j] = "-";

        return new Object[]{labels, sales, orders};
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  KPI AGGREGATES  (used by ReportController to fill KPI cards)
    // ══════════════════════════════════════════════════════════════════════════

    public double getTotalSales(ReportPeriod period) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM sales WHERE "
                + period.toSQLFilter("sale_date");
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getDouble(1) : 0;
    }

    public int getTotalOrders(ReportPeriod period) throws SQLException {
        String sql = "SELECT COUNT(*) FROM sales WHERE "
                + period.toSQLFilter("sale_date");
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    public int getTotalItemsSold(ReportPeriod period) throws SQLException {
        String sql = "SELECT COALESCE(SUM(si.quantity), 0) " +
                "FROM sale_items si JOIN sales s ON si.sale_id = s.sale_id WHERE "
                + period.toSQLFilter("s.sale_date");
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    public int getTotalActiveCustomers(ReportPeriod period) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT customer_id) FROM sales WHERE "
                + period.toSQLFilter("sale_date");
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }
}

/*
 * ── REQUIRED MySQL TABLE ────────────────────────────────────────────────────
 *
 * Run this once in phpMyAdmin to create the reports table:
 *
 * CREATE TABLE reports (
 *   report_id              INT          AUTO_INCREMENT PRIMARY KEY,
 *   title                  VARCHAR(150) NOT NULL,
 *   report_type            VARCHAR(20)  NOT NULL,
 *   period                 VARCHAR(20)  NOT NULL,
 *   generated_date         DATE         NOT NULL,
 *   generated_by           VARCHAR(80)  NOT NULL,
 *   total_sales            DOUBLE       DEFAULT 0,
 *   total_orders           INT          DEFAULT 0,
 *   total_items_sold       INT          DEFAULT 0,
 *   total_active_customers INT          DEFAULT 0
 * );
 *
 * No separate report_items table is needed — items are generated live
 * from your existing sales / products / customers / suppliers tables.
 */