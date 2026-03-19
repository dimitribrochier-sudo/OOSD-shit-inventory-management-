package app.inventory_management.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Receipt {

    private int       numericId;
    private String    receiptCode;
    private int       customerId;
    private String    customerName;
    private String    contactNumber;
    private String    address;
    private int       orderId;
    private LocalDate receiptDate;
    private LocalTime receiptTime;
    private double    subtotal;
    private double    vatAmount;
    private double    totalAmount;
    private Status    status;
    private String    generatedBy;

    public static final double VAT_RATE = 0.15;

    public enum Status {
        PAID, PENDING, CANCELLED;
        public String getLabel() {
            switch (this) {
                case PAID:      return "Paid";
                case PENDING:   return "Pending";
                case CANCELLED: return "Cancelled";
                default:        return name();
            }
        }
    }

    // Constructor: CREATE new receipt before DB insert
    public Receipt(String receiptCode, int customerId, String customerName,
                   String contactNumber, String address, int orderId,
                   double subtotal, Status status, String generatedBy) {
        this.receiptCode   = receiptCode;
        this.customerId    = customerId;
        this.customerName  = customerName;
        this.contactNumber = contactNumber;
        this.address       = address;
        this.orderId       = orderId;
        this.subtotal      = subtotal;
        this.vatAmount     = Math.round(subtotal * VAT_RATE * 100.0) / 100.0;
        this.totalAmount   = Math.round((subtotal + vatAmount) * 100.0) / 100.0;
        this.receiptDate   = LocalDate.now();
        this.receiptTime   = LocalTime.now();
        this.status        = status;
        this.generatedBy   = generatedBy;
    }

    // Constructor: READ from DB
    public Receipt(int numericId, String receiptCode, int customerId,
                   String customerName, String contactNumber, String address,
                   int orderId, LocalDate receiptDate, LocalTime receiptTime,
                   double subtotal, double vatAmount, double totalAmount,
                   Status status, String generatedBy) {
        this.numericId     = numericId;
        this.receiptCode   = receiptCode;
        this.customerId    = customerId;
        this.customerName  = customerName;
        this.contactNumber = contactNumber;
        this.address       = address;
        this.orderId       = orderId;
        this.receiptDate   = receiptDate;
        this.receiptTime   = receiptTime;
        this.subtotal      = subtotal;
        this.vatAmount     = vatAmount;
        this.totalAmount   = totalAmount;
        this.status        = status;
        this.generatedBy   = generatedBy;
    }

    public int       getNumericId()     { return numericId; }
    public String    getReceiptCode()   { return receiptCode; }
    public int       getCustomerId()    { return customerId; }
    public String    getCustomerName()  { return customerName; }
    public String    getContactNumber() { return contactNumber; }
    public String    getAddress()       { return address; }
    public int       getOrderId()       { return orderId; }
    public int       getSaleId()        { return orderId; } // backward compat alias
    public LocalDate getReceiptDate()   { return receiptDate; }
    public LocalTime getReceiptTime()   { return receiptTime; }
    public double    getSubtotal()      { return subtotal; }
    public double    getVatAmount()     { return vatAmount; }
    public double    getTotalAmount()   { return totalAmount; }
    public Status    getStatus()        { return status; }
    public String    getGeneratedBy()   { return generatedBy; }

    public void setStatus(Status status)           { this.status = status; }
    public void setReceiptCode(String receiptCode) { this.receiptCode = receiptCode; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
    public void setSubtotal(double subtotal) {
        this.subtotal    = subtotal;
        this.vatAmount   = Math.round(subtotal * VAT_RATE * 100.0) / 100.0;
        this.totalAmount = Math.round((subtotal + vatAmount) * 100.0) / 100.0;
    }

    public String getFormattedDate() {
        if (receiptDate == null) return "";
        return String.format("%02d %s %d",
                receiptDate.getDayOfMonth(),
                receiptDate.getMonth().name(),
                receiptDate.getYear());
    }

    public String getFormattedTime() {
        if (receiptTime == null) return "";
        int hour = receiptTime.getHour();
        int min  = receiptTime.getMinute();
        String ampm = hour >= 12 ? "PM" : "AM";
        int h12 = hour % 12; if (h12 == 0) h12 = 12;
        return String.format("%02d:%02d %s", h12, min, ampm);
    }

    public Object[] toTableRow() {
        return new Object[]{
                receiptCode,
                receiptDate != null ? receiptDate.format(
                        java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "",
                receiptTime != null ? receiptTime.format(
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm")) : "",
                customerName,
                String.format("%.2f", subtotal),
                String.format("%.2f", vatAmount),
                String.format("%.2f", totalAmount),
                status.getLabel()
        };
    }

    // ── Inner class ───────────────────────────────────────────────────────────
    public static class ReceiptItem {

        private int    receiptItemId;
        private int    receiptId;
        private int    productId;
        private String productName;
        private int    quantity;
        private double unitPrice;
        private double lineTotal;

        // Read from DB
        public ReceiptItem(int receiptItemId, int receiptId, int productId,
                           String productName, int quantity,
                           double unitPrice, double lineTotal) {
            this.receiptItemId = receiptItemId;
            this.receiptId     = receiptId;
            this.productId     = productId;
            this.productName   = productName;
            this.quantity      = quantity;
            this.unitPrice     = unitPrice;
            this.lineTotal     = lineTotal;
        }

        // Insert new
        public ReceiptItem(int receiptId, int productId, String productName,
                           int quantity, double unitPrice) {
            this.receiptId   = receiptId;
            this.productId   = productId;
            this.productName = productName;
            this.quantity    = quantity;
            this.unitPrice   = unitPrice;
            this.lineTotal   = Math.round(quantity * unitPrice * 100.0) / 100.0;
        }

        public int    getReceiptItemId() { return receiptItemId; }
        public int    getReceiptId()     { return receiptId; }
        public int    getProductId()     { return productId; }
        public String getProductName()   { return productName; }
        public int    getQuantity()      { return quantity; }
        public double getUnitPrice()     { return unitPrice; }
        public double getLineTotal()     { return lineTotal; }

        public Object[] toTableRow() {
            return new Object[]{
                    "P-" + productId,
                    productName,
                    quantity,
                    String.format("%.2f", unitPrice),
                    String.format("%.2f", lineTotal)
            };
        }
    }
}