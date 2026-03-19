package app.inventory_management.views;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import app.inventory_management.models.User;
import app.inventory_management.utils.SessionManager;
import app.inventory_management.config.DBConnection;

public class DashboardScreen extends JFrame {


    private static final Font F_TITLE     = new Font("Serif",     Font.BOLD,  22);
    private static final Font F_NAV_HDR   = new Font("SansSerif", Font.BOLD,  11);
    private static final Font F_NAV_BTN   = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font F_KPI_TITLE = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font F_KPI_VAL   = new Font("Serif",     Font.BOLD,  22);
    private static final Font F_SECTION   = new Font("SansSerif", Font.BOLD,  12);
    private static final Font F_TABLE     = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font F_TABLE_HDR = new Font("SansSerif", Font.BOLD,  12);
    private static final Font F_STATUS    = new Font("SansSerif", Font.PLAIN, 10);
    private static final Font F_USER_LBL  = new Font("SansSerif", Font.BOLD,  12);

    // ── State ─────────────────────────────────────────────────────────────────
    private User              currentUser;
    private Connection        dbConn;
    private boolean           isAdmin;

    private JLabel            kpiSalesLbl, kpiStockLbl, kpiOrdersLbl;
    private DefaultTableModel activityModel;

    // ═════════════════════════════════════════════════════════════════════════
    public DashboardScreen() {

        if (!SessionManager.getInstance().isLoggedIn()) {
            new LoginScreen();
            dispose();
            return;
        }

        currentUser = SessionManager.getInstance().getCurrentUser();
        isAdmin     = currentUser.getRoleId() == 1;
        dbConn      = DBConnection.getConnection();

        setTitle("Dashboard");
        setSize(1000, 680);
        setMinimumSize(new Dimension(800, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildMenuBar();
        add(buildSidebar(),   BorderLayout.WEST);
        add(buildMainPanel(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        refreshKPIs();
        refreshActivity();
        setVisible(true);
    }

    // ── Menu Bar — plain default Swing ───────────────────────────────────────
    private void buildMenuBar() {
        JMenuBar mb = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem li = new JMenuItem("Log Out");
        li.addActionListener(e -> doLogout());
        JMenuItem ex = new JMenuItem("Exit");
        ex.addActionListener(e -> System.exit(0));
        file.add(li); file.addSeparator(); file.add(ex);

        JMenu edit = new JMenu("Edit");
        if (isAdmin) {
            JMenuItem mu = new JMenuItem("Manage Users");
            mu.addActionListener(e -> openScreen("Users"));
            edit.add(mu);
        }

        JMenu tools = new JMenu("Tools");
        JMenuItem ri = new JMenuItem("Refresh Dashboard");
        ri.addActionListener(e -> { refreshKPIs(); refreshActivity(); });
        JMenuItem ei = new JMenuItem("Export Report (CSV)");
        ei.addActionListener(e -> openScreen("Report Screen"));
        tools.add(ri); tools.add(ei);

        JMenu help = new JMenu("Help");
        JMenuItem ai = new JMenuItem("About");
        ai.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Smrithi Inventory System v1.0\nJava AWT & Swing  |  MySQL via XAMPP",
                "About", JOptionPane.INFORMATION_MESSAGE));
        help.add(ai);

        mb.add(file); mb.add(edit); mb.add(tools); mb.add(help);
        setJMenuBar(mb);
    }

    // ── Sidebar — same layout as blue version, plain grey colours ────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        // NAVIGATION label
        JLabel navLabel = new JLabel("NAVIGATION", SwingConstants.CENTER);
        navLabel.setFont(F_NAV_HDR);
        navLabel.setAlignmentX(CENTER_ALIGNMENT);
        navLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        navLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 6, 0));
        sidebar.add(navLabel);
        sidebar.add(new JSeparator());
        sidebar.add(Box.createVerticalStrut(6));

        // All nav buttons — plain default Swing buttons, same as original
        sidebar.add(navBtn("Users",                  "Users"));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navBtn("Suppliers",              "Suppliers"));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navBtn("Products",               "Products"));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navBtn("Sales",                  "Sales"));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navBtn("Customers",              "Customers"));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navBtn("Inventory Transactions", "Inventory Transactions"));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navBtn("Report Screen",          "Report Screen"));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navBtn("Order",                  "Order"));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navBtn("Receipt",                "Receipt"));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navBtn("Analytics",              "Analytics"));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navBtn("Supplier Price List",    "Supplier Price List"));

        // Push Log Out to bottom
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(new JSeparator());
        sidebar.add(Box.createVerticalStrut(4));

        JButton logoutBtn = new JButton("Log Out");
        logoutBtn.setFont(F_NAV_BTN);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        logoutBtn.addActionListener(e -> doLogout());
        sidebar.add(logoutBtn);

        return sidebar;
    }

    private JButton navBtn(String label, String target) {
        JButton btn = new JButton(label);
        btn.setFont(F_NAV_BTN);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        if (target.equals("Users") && !isAdmin) {
            btn.setEnabled(false);
            btn.setToolTipText("Admin access only");
        } else {
            btn.addActionListener(e -> openScreen(target));
        }
        return btn;
    }

    // ── Main Panel — same layout as blue version, plain grey ─────────────────
    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout(0, 10));
        main.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));

        main.add(buildHeader(),   BorderLayout.NORTH);
        main.add(buildContent(),  BorderLayout.CENTER);
        return main;
    }

    // Header: "WELCOME smrithi"  +  "Current User: smrithi"
    private JPanel buildHeader() {
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JLabel welcome = new JLabel("WELCOME " + currentUser.getUsername());
        welcome.setFont(F_TITLE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JLabel userLbl = new JLabel("Current User: " + currentUser.getUsername());
        userLbl.setFont(F_USER_LBL);
        right.add(userLbl);

        hdr.add(welcome, BorderLayout.WEST);
        hdr.add(right,   BorderLayout.EAST);
        return hdr;
    }

    // Content: KPI cards + activity table
    private JPanel buildContent() {
        JPanel c = new JPanel(new BorderLayout(0, 10));
        c.add(buildKpiRow(),        BorderLayout.NORTH);
        c.add(buildActivityPanel(), BorderLayout.CENTER);
        return c;
    }

    // ── KPI Cards — plain etched border, same as original Swing style ─────────
    private JPanel buildKpiRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 10, 0));
        row.setPreferredSize(new Dimension(0, 90));

        kpiSalesLbl  = new JLabel("Rs 0",      SwingConstants.LEFT);
        kpiStockLbl  = new JLabel("0 Items",   SwingConstants.LEFT);
        kpiOrdersLbl = new JLabel("0 Pending", SwingConstants.LEFT);

        kpiSalesLbl .setFont(F_KPI_VAL);
        kpiStockLbl .setFont(F_KPI_VAL);
        kpiOrdersLbl.setFont(F_KPI_VAL);

        row.add(buildKpiCard("Total Sales (Today)", kpiSalesLbl));
        row.add(buildKpiCard("Low Stock Alerts",    kpiStockLbl));
        row.add(buildKpiCard("Active Orders",       kpiOrdersLbl));
        return row;
    }

    private JPanel buildKpiCard(String title, JLabel valueLbl) {
        JPanel card = new JPanel(new BorderLayout(0, 2));
        card.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), title,
                TitledBorder.LEFT, TitledBorder.TOP, F_KPI_TITLE));
        card.add(valueLbl, BorderLayout.CENTER);
        return card;
    }

    // ── Recent Activity Table ─────────────────────────────────────────────────
    private JPanel buildActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Recent Inventory Activity",
                TitledBorder.LEFT, TitledBorder.TOP, F_SECTION));

        String[] cols = {"Date", "Time", "Action", "User", "Product"};
        activityModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(activityModel);
        table.setFont(F_TABLE);
        table.setRowHeight(24);
        table.getTableHeader().setFont(F_TABLE_HDR);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEtchedBorder());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(F_STATUS);
        refreshBtn.addActionListener(e -> refreshActivity());

        panel.add(scroll,     BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);
        return panel;
    }

    // ── Status Bar ────────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));

        JLabel statusLbl = new JLabel("System Status: Online");
        statusLbl.setFont(F_STATUS);

        JLabel dbLbl = new JLabel(dbConn != null ? "Database Connected" : "Database Disconnected");
        dbLbl.setFont(F_STATUS);
        dbLbl.setForeground(dbConn != null ? new Color(0, 130, 0) : Color.RED);

        bar.add(statusLbl, BorderLayout.WEST);
        bar.add(dbLbl,     BorderLayout.EAST);
        return bar;
    }

    // ── Data Refresh ──────────────────────────────────────────────────────────
    private void refreshKPIs() {
        if (dbConn == null) {
            kpiSalesLbl .setText("Rs 15,340");
            kpiStockLbl .setText("7 Items");
            kpiOrdersLbl.setText("3 Pending");
            return;
        }
        try {
            PreparedStatement ps1 = dbConn.prepareStatement(
                    "SELECT COALESCE(SUM(total_amount),0) FROM sales WHERE sale_date = CURDATE()");
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) kpiSalesLbl.setText("Rs " + String.format("%,.0f", rs1.getDouble(1)));

            PreparedStatement ps2 = dbConn.prepareStatement(
                    "SELECT COUNT(*) FROM products WHERE current_stock <= reorder_level");
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) kpiStockLbl.setText(rs2.getInt(1) + " Items");

            PreparedStatement ps3 = dbConn.prepareStatement(
                    "SELECT COUNT(*) FROM sales WHERE status = 'PENDING'");
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) kpiOrdersLbl.setText(rs3.getInt(1) + " Pending");

        } catch (SQLException ex) {
            System.err.println("KPI refresh error: " + ex.getMessage());
        }
    }

    private void refreshActivity() {
        activityModel.setRowCount(0);
        if (dbConn == null) {
            Object[][] demo = {
                    {"22-06-2023","15:24:00","Action",          "smrithi","Product"},
                    {"22-06-2023","15:30:00","Deaforviraded",   "smrithi","Product"},
                    {"22-06-2023","15:29:00","Post for safety", "smrithi","Product"},
                    {"22-06-2023","15:30:00","Automated",       "smrithi","Product"},
                    {"22-06-2023","15:30:00","Polnmaked",       "smrithi","Product"},
                    {"12-06-2023","15:34:00","Prenline",        "smrithi","Product"},
            };
            for (Object[] r : demo) activityModel.addRow(r);
            return;
        }
        String sql =
                "SELECT DATE_FORMAT(transaction_date,'%d-%m-%Y'), " +
                        "       TIME_FORMAT(transaction_time,'%H:%i:%S'), " +
                        "       transaction_type, u.username, p.name " +
                        "FROM inventory_transactions it " +
                        "JOIN users    u ON it.user_id    = u.user_id " +
                        "JOIN products p ON it.product_id = p.product_id " +
                        "ORDER BY transaction_date DESC, transaction_time DESC LIMIT 10";
        try (PreparedStatement ps = dbConn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                activityModel.addRow(new Object[]{
                        rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getString(4), rs.getString(5)});
        } catch (SQLException ex) {
            System.err.println("Activity refresh error: " + ex.getMessage());
        }
    }

    // ── Screen Router ─────────────────────────────────────────────────────────
    private void openScreen(String target) {
        switch (target) {
            case "Users":
                if (!isAdmin) { accessDenied(); return; }
                dispose(); new UserScreen(); break;
            case "Suppliers":
                dispose(); new SupplierScreen(); break;
            case "Products":
                dispose(); new ProductScreen(); break;
            case "Sales":
                new SalesScreen(); break;
            case "Customers":
                dispose(); new CustomerScreen(); break;
            case "Inventory Transactions":
                JOptionPane.showMessageDialog(this, "Inventory Transactions coming soon!", "Coming Soon", JOptionPane.INFORMATION_MESSAGE); break;
            case "Report Screen":
                new ReportScreen(currentUser.getUsername(), dbConn); break;
            case "Order":
                JOptionPane.showMessageDialog(this, "Order screen coming soon!", "Coming Soon", JOptionPane.INFORMATION_MESSAGE); break;
            case "Receipt":
                new ReceiptScreen(currentUser.getUsername(), dbConn); break;
            case "Analytics":
                JOptionPane.showMessageDialog(this, "Analytics coming soon!", "Coming Soon", JOptionPane.INFORMATION_MESSAGE); break;
            case "Supplier Price List":
                JOptionPane.showMessageDialog(this, "Supplier Price List coming soon!", "Coming Soon", JOptionPane.INFORMATION_MESSAGE); break;
            default:
                JOptionPane.showMessageDialog(this, "\"" + target + "\" not implemented yet.",
                        "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void doLogout() {
        SessionManager.getInstance().logout();
        dispose();
        new LoginScreen();
    }

    private void accessDenied() {
        JOptionPane.showMessageDialog(this,
                "Access denied. This section requires Admin privileges.",
                "Access Denied", JOptionPane.WARNING_MESSAGE);
    }
}