package app.inventory_management.controllers;

import app.inventory_management.models.Report;
import app.inventory_management.models.Report.ReportItem;
import app.inventory_management.models.Report.ReportPeriod;
import app.inventory_management.models.Report.ReportType;
import app.inventory_management.repository.ReportDAO;

import java.sql.SQLException;
import java.util.List;

/**
 * ReportController.java — Middle layer between the GUI and ReportDAO.
 *
 * The GUI (ReportScreen) never calls ReportDAO directly.
 * It always goes through this controller.
 *
 * This class:
 *   - Instantiates ReportDAO (just like ProductController does for ProductDAO)
 *   - Wraps every SQLException in a RuntimeException so the GUI stays clean
 *   - Orchestrates multi-step operations (e.g. generate KPIs then save report)
 *   - Provides a single generateReport() method the screen calls with two params
 */
public class ReportController {

    ReportDAO reportDAO = new ReportDAO();

    // ══════════════════════════════════════════════════════════════════════════
    //  CORE: GENERATE A REPORT
    //  This is the main method the GUI calls. It:
    //    1. Fetches live KPI totals from the DB
    //    2. Builds a Report object
    //    3. Saves it to the reports table
    //    4. Returns the saved Report (with its new report_id)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Generates and saves a new report. Returns the persisted Report object
     * (with report_id filled in) so the GUI can display the KPI figures.
     *
     * @param type       The kind of report (SALES, INVENTORY, etc.)
     * @param period     The time range (TODAY, THIS_MONTH, etc.)
     * @param generatedBy  The username of the logged-in user
     */
    public Report generateReport(ReportType type, ReportPeriod period, String generatedBy) {
        try {
            // Step 1: compute live KPI totals
            double totalSales      = reportDAO.getTotalSales(period);
            int    totalOrders     = reportDAO.getTotalOrders(period);
            int    totalItems      = reportDAO.getTotalItemsSold(period);
            int    totalCustomers  = reportDAO.getTotalActiveCustomers(period);

            // Step 2: build the Report model
            String title = type.getLabel() + " — " + period.getLabel();
            Report report = new Report(
                    title, type, period, generatedBy,
                    totalSales, totalOrders, totalItems, totalCustomers
            );

            // Step 3: save to DB and get the auto-generated ID back
            int newId = reportDAO.saveReport(report);

            // Step 4: return a fully-populated Report with the new ID
            return reportDAO.findReportById(newId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REPORT ITEM ROWS  (for the JTable in ReportScreen)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Returns the detail rows for the JTable based on report type and period.
     * Routes to the correct DAO method automatically.
     */
    public List<ReportItem> loadReportItems(ReportType type, ReportPeriod period) {
        try {
            switch (type) {
                case SALES:     return reportDAO.generateSalesItems(period);
                case INVENTORY: return reportDAO.generateInventoryItems(period);
                case CUSTOMER:  return reportDAO.generateCustomerItems(period);
                case SUPPLIER:  return reportDAO.generateSupplierItems(period);
                default:        return reportDAO.generateSalesItems(period);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REPORT HISTORY
    // ══════════════════════════════════════════════════════════════════════════

    /** Returns all saved report headers, newest first. */
    public List<Report> loadAllReports() {
        try {
            return reportDAO.loadAllReports();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Finds one saved report by its ID. */
    public Report findReport(int reportId) {
        try {
            return reportDAO.findReportById(reportId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Deletes a saved report by its ID. */
    public void deleteReport(int reportId) {
        try {
            reportDAO.deleteReport(reportId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CHART DATA  (for the bar chart in ReportScreen)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Returns an Object[] with:
     *   [0] String[] labels  — e.g. {"Oct","Nov","Dec","Jan","Feb","Mar"}
     *   [1] double[] sales   — monthly sales totals
     *   [2] double[] orders  — monthly order counts
     *
     * ReportScreen unpacks this to feed the BarChartPanel.
     */
    public Object[] getChartData() {
        try {
            return reportDAO.getMonthlyChartData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  INDIVIDUAL KPI HELPERS
    //  (used if ReportScreen wants to refresh KPIs without a full generate)
    // ══════════════════════════════════════════════════════════════════════════

    public double getKpiTotalSales(ReportPeriod period) {
        try { return reportDAO.getTotalSales(period); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int getKpiTotalOrders(ReportPeriod period) {
        try { return reportDAO.getTotalOrders(period); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int getKpiTotalItems(ReportPeriod period) {
        try { return reportDAO.getTotalItemsSold(period); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int getKpiTotalCustomers(ReportPeriod period) {
        try { return reportDAO.getTotalActiveCustomers(period); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }
}