package app.inventory_management.models;

import java.time.LocalDate;

/**
 * Report.java — Model (POJO)
 *
 * Represents one aggregated report row fetched from the database.
 * Two constructors mirror the Product pattern:
 *   - one for inserting/generating a new report record
 *   - one for reading an existing report record back from the DB
 *
 * A Report summarises sales/inventory data for a given period and type.
 * The line-item detail (individual products sold) is handled separately
 * by ReportItem (inner class below) and loaded by ReportDAO.
 */
public class Report {

    // ── Fields ─────────────────────────────────────────────────────────────────
    private int    reportId;
    private String title;
    private ReportType reportType;
    private ReportPeriod period;
    private LocalDate generatedDate;
    private String generatedBy;       // username of whoever ran the report

    // Aggregate figures stored on the report record itself
    private double totalSales;
    private int    totalOrders;
    private int    totalItemsSold;
    private int    totalActiveCustomers;

    // ── Enums ──────────────────────────────────────────────────────────────────

    public enum ReportType {
        SALES,
        INVENTORY,
        CUSTOMER,
        SUPPLIER;

        /** Pretty label for combo-boxes */
        public String getLabel() {
            switch (this) {
                case SALES:     return "Sales Report";
                case INVENTORY: return "Inventory Report";
                case CUSTOMER:  return "Customer Report";
                case SUPPLIER:  return "Supplier Report";
                default:        return name();
            }
        }
    }

    public enum ReportPeriod {
        TODAY,
        THIS_WEEK,
        THIS_MONTH,
        LAST_3_MONTHS,
        LAST_6_MONTHS,
        THIS_YEAR,
        ALL_TIME;

        public String getLabel() {
            switch (this) {
                case TODAY:          return "Today";
                case THIS_WEEK:      return "This Week";
                case THIS_MONTH:     return "This Month";
                case LAST_3_MONTHS:  return "Last 3 Months";
                case LAST_6_MONTHS:  return "Last 6 Months";
                case THIS_YEAR:      return "This Year";
                case ALL_TIME:       return "All Time";
                default:             return name();
            }
        }

        /** Returns the MySQL WHERE clause fragment for this period */
        public String toSQLFilter(String dateColumn) {
            switch (this) {
                case TODAY:         return dateColumn + " = CURDATE()";
                case THIS_WEEK:     return dateColumn + " >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
                case THIS_MONTH:    return "MONTH(" + dateColumn + ") = MONTH(CURDATE()) " +
                        "AND YEAR(" + dateColumn + ") = YEAR(CURDATE())";
                case LAST_3_MONTHS: return dateColumn + " >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)";
                case LAST_6_MONTHS: return dateColumn + " >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)";
                case THIS_YEAR:     return "YEAR(" + dateColumn + ") = YEAR(CURDATE())";
                case ALL_TIME:      return "1=1";
                default:            return "1=1";
            }
        }
    }

    // ── Constructor: used when GENERATING and saving a new report ──────────────
    public Report(String title, ReportType reportType, ReportPeriod period,
                  String generatedBy, double totalSales,
                  int totalOrders, int totalItemsSold, int totalActiveCustomers) {
        this.title                = title;
        this.reportType           = reportType;
        this.period               = period;
        this.generatedBy          = generatedBy;
        this.generatedDate        = LocalDate.now();
        this.totalSales           = totalSales;
        this.totalOrders          = totalOrders;
        this.totalItemsSold       = totalItemsSold;
        this.totalActiveCustomers = totalActiveCustomers;
    }

    // ── Constructor: used when READING an existing report back from the DB ─────
    public Report(int reportId, String title, ReportType reportType, ReportPeriod period,
                  LocalDate generatedDate, String generatedBy,
                  double totalSales, int totalOrders,
                  int totalItemsSold, int totalActiveCustomers) {
        this.reportId             = reportId;
        this.title                = title;
        this.reportType           = reportType;
        this.period               = period;
        this.generatedDate        = generatedDate;
        this.generatedBy          = generatedBy;
        this.totalSales           = totalSales;
        this.totalOrders          = totalOrders;
        this.totalItemsSold       = totalItemsSold;
        this.totalActiveCustomers = totalActiveCustomers;
    }

    // ── Getters ────────────────────────────────────────────────────────────────
    public int         getReportId()             { return reportId; }
    public String      getTitle()                { return title; }
    public ReportType  getReportType()           { return reportType; }
    public ReportPeriod getPeriod()              { return period; }
    public LocalDate   getGeneratedDate()        { return generatedDate; }
    public String      getGeneratedBy()          { return generatedBy; }
    public double      getTotalSales()           { return totalSales; }
    public int         getTotalOrders()          { return totalOrders; }
    public int         getTotalItemsSold()       { return totalItemsSold; }
    public int         getTotalActiveCustomers() { return totalActiveCustomers; }

    // ── Setters ────────────────────────────────────────────────────────────────
    public void setTitle(String title)                           { this.title = title; }
    public void setReportType(ReportType reportType)             { this.reportType = reportType; }
    public void setPeriod(ReportPeriod period)                   { this.period = period; }
    public void setGeneratedBy(String generatedBy)               { this.generatedBy = generatedBy; }
    public void setTotalSales(double totalSales)                 { this.totalSales = totalSales; }
    public void setTotalOrders(int totalOrders)                  { this.totalOrders = totalOrders; }
    public void setTotalItemsSold(int totalItemsSold)            { this.totalItemsSold = totalItemsSold; }
    public void setTotalActiveCustomers(int v)                   { this.totalActiveCustomers = v; }

    @Override
    public String toString() {
        return "Report{id=" + reportId + ", type=" + reportType +
                ", period=" + period + ", date=" + generatedDate +
                ", totalSales=" + totalSales + "}";
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  INNER CLASS — ReportItem
    //  Represents one detail row inside a report
    //  (e.g. one product line in a Sales Report).
    // ══════════════════════════════════════════════════════════════════════════
    public static class ReportItem {

        private int    reportItemId;
        private int    reportId;        // FK → reports.report_id
        private String itemDate;        // formatted dd-MM-yyyy
        private String itemName;        // product / customer / supplier name
        private String category;
        private int    quantity;
        private double unitPrice;
        private double lineTotal;
        private String status;

        // Constructor for reading from DB
        public ReportItem(int reportItemId, int reportId, String itemDate,
                          String itemName, String category, int quantity,
                          double unitPrice, double lineTotal, String status) {
            this.reportItemId = reportItemId;
            this.reportId     = reportId;
            this.itemDate     = itemDate;
            this.itemName     = itemName;
            this.category     = category;
            this.quantity     = quantity;
            this.unitPrice    = unitPrice;
            this.lineTotal    = lineTotal;
            this.status       = status;
        }

        // Constructor for building a row to insert
        public ReportItem(int reportId, String itemDate, String itemName,
                          String category, int quantity,
                          double unitPrice, double lineTotal, String status) {
            this.reportId  = reportId;
            this.itemDate  = itemDate;
            this.itemName  = itemName;
            this.category  = category;
            this.quantity  = quantity;
            this.unitPrice = unitPrice;
            this.lineTotal = lineTotal;
            this.status    = status;
        }

        public int    getReportItemId() { return reportItemId; }
        public int    getReportId()     { return reportId; }
        public String getItemDate()     { return itemDate; }
        public String getItemName()     { return itemName; }
        public String getCategory()     { return category; }
        public int    getQuantity()     { return quantity; }
        public double getUnitPrice()    { return unitPrice; }
        public double getLineTotal()    { return lineTotal; }
        public String getStatus()       { return status; }

        /** Returns a String array ready to drop into a JTable row */
        public Object[] toTableRow(int rowNumber) {
            return new Object[]{
                    rowNumber,
                    itemDate,
                    itemName,
                    category,
                    quantity,
                    String.format("%.2f", unitPrice),
                    String.format("%.2f", lineTotal),
                    status
            };
        }
    }
}