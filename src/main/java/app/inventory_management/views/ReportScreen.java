package app.inventory_management.views;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportScreen extends JFrame {

    // ── Colors & Fonts ────────────────────────────────────────────────────────
    private static final Color CHART_BLUE = new Color(184, 207, 229); // Match text selection blue
    private static final Color CHART_GRAY = new Color(200, 200, 200);

    private static final Font F_TITLE     = new Font("Serif",     Font.BOLD,  20);
    private static final Font F_SECTION   = new Font("SansSerif", Font.BOLD,  12);
    private static final Font F_LABEL     = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font F_BODY      = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font F_KPI_VAL   = new Font("Serif",     Font.BOLD,  22);
    private static final Font F_KPI_TITLE = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font F_TABLE     = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font F_TABLE_HDR = new Font("SansSerif", Font.BOLD,  12);
    private static final Font F_STATUS    = new Font("SansSerif", Font.PLAIN, 10);

    // ── State ─────────────────────────────────────────────────────────────────
    private final String      currentUser;
    private final Connection connection;

    private JLabel            kpiSalesLbl, kpiOrdersLbl, kpiProductsLbl, kpiCustomersLbl;
    private JTable            reportTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> reportTypeCombo, periodCombo;
    private JTextField        searchField;
    private JLabel            statusLabel;

    private String[] chartLabels  = {};
    private double[] chartSeries1 = {};
    private double[] chartSeries2 = {};
    private JPanel   chartPanel;

    public ReportScreen(String currentUser, Connection connection) {
        this.currentUser = currentUser;
        this.connection  = connection;

        setTitle("Inventory Report Dashboard");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        buildUI();
        loadReport();
        setVisible(true);
    }

    private void buildUI() {
        add(buildTopBar(),      BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildStatusBar(),   BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel title = new JLabel("Inventory Analytics");
        title.setFont(F_TITLE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        reportTypeCombo = new JComboBox<>(new String[]{"Stock Levels Report"});
        periodCombo = new JComboBox<>(new String[]{"Current Snapshot", "All Time"});

        JButton genBtn = new JButton("Refresh Data");
        genBtn.addActionListener(e -> loadReport());

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> dispose());

        right.add(new JLabel("Type:")); right.add(reportTypeCombo);
        right.add(new JLabel("Period:")); right.add(periodCombo);
        right.add(genBtn); right.add(backBtn);

        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBorder(BorderFactory.createEmptyBorder(12, 14, 10, 14));
        center.add(buildKpiRow(),      BorderLayout.NORTH);
        center.add(buildMainContent(), BorderLayout.CENTER);
        return center;
    }

    private JPanel buildKpiRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setPreferredSize(new Dimension(0, 85));

        kpiSalesLbl     = new JLabel("Rs 0",      SwingConstants.LEFT);
        kpiOrdersLbl    = new JLabel("0",        SwingConstants.LEFT);
        kpiProductsLbl  = new JLabel("0 items", SwingConstants.LEFT);
        kpiCustomersLbl = new JLabel("—",       SwingConstants.LEFT);

        kpiSalesLbl.setFont(F_KPI_VAL);
        kpiOrdersLbl.setFont(F_KPI_VAL);
        kpiProductsLbl.setFont(F_KPI_VAL);
        kpiCustomersLbl.setFont(F_KPI_VAL);

        row.add(buildKpiCard("Total Asset Value", kpiSalesLbl));
        row.add(buildKpiCard("Low Stock Alerts",  kpiOrdersLbl));
        row.add(buildKpiCard("Unique Products",   kpiProductsLbl));
        row.add(buildKpiCard("Supplier Count",    kpiCustomersLbl));
        return row;
    }

    private JPanel buildKpiCard(String title, JLabel valueLbl) {
        JPanel card = new JPanel(new BorderLayout(0, 2));
        card.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title, TitledBorder.LEFT, TitledBorder.TOP, F_KPI_TITLE));
        card.add(valueLbl, BorderLayout.CENTER);
        return card;
    }

    private JSplitPane buildMainContent() {
        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                int padL = 50, padR = 20, padT = 40, padB = 40;
                int chartW = w - padL - padR;
                int chartH = h - padT - padB;

                if (chartLabels == null || chartLabels.length == 0 || chartW <= 0 || chartH <= 0) {
                    g2.drawString("No data to display", w / 2 - 40, h / 2);
                    return;
                }

                double maxVal = 10;
                for (double v : chartSeries1) if (v > maxVal) maxVal = v;
                for (double v : chartSeries2) if (v > maxVal) maxVal = v;
                maxVal *= 1.1;

                // Grid
                g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                for (int i = 0; i <= 5; i++) {
                    int y = padT + chartH - (int) (i * chartH / 5.0);
                    g2.setColor(new Color(240, 240, 240));
                    g2.drawLine(padL, y, padL + chartW, y);
                    g2.setColor(Color.GRAY);
                    g2.drawString(String.format("%.0f", (maxVal * i / 5)), 10, y + 4);
                }

                // Bars
                int n = chartLabels.length;
                int groupW = chartW / n;
                int barW = Math.max(10, (int) (groupW * 0.35));

                for (int i = 0; i < n; i++) {
                    int centerX = padL + i * groupW + groupW / 2;

                    // Stock Bar - Using the requested Blue
                    int h1 = (int) (chartSeries1[i] / maxVal * chartH);
                    g2.setColor(CHART_BLUE);
                    g2.fillRect(centerX - barW, padT + chartH - h1, barW, h1);
                    g2.setColor(CHART_BLUE.darker());
                    g2.drawRect(centerX - barW, padT + chartH - h1, barW, h1);

                    // Reorder Bar - Gray
                    int h2 = (int) (chartSeries2[i] / maxVal * chartH);
                    g2.setColor(CHART_GRAY);
                    g2.fillRect(centerX + 2, padT + chartH - h2, barW, h2);

                    g2.setColor(Color.DARK_GRAY);
                    String lbl = chartLabels[i];
                    if(lbl.length() > 10) lbl = lbl.substring(0, 8) + "..";
                    int lblW = g2.getFontMetrics().stringWidth(lbl);
                    g2.drawString(lbl, centerX - lblW/2, padT + chartH + 18);
                }

                // Legend
                g2.setColor(CHART_BLUE); g2.fillRect(padL, 15, 12, 12);
                g2.setColor(Color.BLACK); g2.drawString("Current Stock", padL + 18, 25);
                g2.setColor(CHART_GRAY); g2.fillRect(padL + 100, 15, 12, 12);
                g2.setColor(Color.BLACK); g2.drawString("Reorder Level", padL + 118, 25);
            }
        };

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Visual Stock Analysis", TitledBorder.LEFT, TitledBorder.TOP, F_SECTION));
        wrap.add(chartPanel, BorderLayout.CENTER);
        wrap.setPreferredSize(new Dimension(450, 400));

        JPanel tableSection = new JPanel(new BorderLayout(0, 5));
        searchField = new JTextField(15);
        searchField.addKeyListener(new KeyAdapter() { @Override public void keyReleased(KeyEvent e) { filterTable(); } });

        JPanel tool = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tool.add(new JLabel("Search Products:")); tool.add(searchField);
        tableSection.add(tool, BorderLayout.NORTH);
        tableSection.add(buildTablePane(), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, wrap, tableSection);
        split.setDividerLocation(450);
        return split;
    }

    private JScrollPane buildTablePane() {
        String[] cols = {"#", "Name", "Category", "Stock", "Price", "Total", "Status"};
        tableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        reportTable = new JTable(tableModel);
        reportTable.setRowHeight(25);
        return new JScrollPane(reportTable);
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        statusLabel = new JLabel(" System Ready");
        statusLabel.setFont(F_STATUS);
        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    private void loadReport() {
        tableModel.setRowCount(0);
        if (connection == null) {
            loadDemo();
        } else {
            loadDB();
        }
        chartPanel.repaint();
    }

    private void loadDemo() {
        chartLabels  = new String[]{"Solar", "Battery", "LED", "Meter", "Hub"};
        chartSeries1 = new double[]{45, 8, 150, 25, 4};
        chartSeries2 = new double[]{10, 15, 40, 10, 10};

        tableModel.addRow(new Object[]{1, "Solar Panel", "Energy", 45, 45000, 2025000, "In Stock"});
        tableModel.addRow(new Object[]{2, "Battery Pack", "Energy", 8, 12000, 96000, "Low Stock"});
        statusLabel.setText(" Loaded Demo Data");
    }

    private void loadDB() {
        List<String> names = new ArrayList<>();
        List<Double> curS = new ArrayList<>();
        List<Double> reS = new ArrayList<>();
        String sql = "SELECT name, category, current_stock, unit_price, reorder_level FROM products";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            int count = 0, low = 0;
            double totalVal = 0;
            while (rs.next()) {
                String n = rs.getString("name");
                double stock = rs.getDouble("current_stock");
                double reorder = rs.getDouble("reorder_level");
                double price = rs.getDouble("unit_price");
                String status = (stock <= reorder) ? "Low Stock" : "In Stock";

                tableModel.addRow(new Object[]{ ++count, n, rs.getString("category"), stock, price, (stock * price), status });

                if (stock <= reorder) low++;
                totalVal += (stock * price);

                if (count <= 10) { names.add(n); curS.add(stock); reS.add(reorder); }
            }
            chartLabels = names.toArray(new String[0]);
            chartSeries1 = curS.stream().mapToDouble(d -> d).toArray();
            chartSeries2 = reS.stream().mapToDouble(d -> d).toArray();

            kpiSalesLbl.setText("Rs " + String.format("%,.0f", totalVal));
            kpiOrdersLbl.setText(String.valueOf(low));
            kpiProductsLbl.setText(count + " items");
            statusLabel.setText(" Data refreshed from Database.");
        } catch (SQLException e) { statusLabel.setText(" DB Error: " + e.getMessage()); }
    }

    private void filterTable() {
        String q = searchField.getText().trim();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        reportTable.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + q));
    }
}