package app.inventory_management.controllers;

import app.inventory_management.models.Receipt;
import app.inventory_management.models.Receipt.ReceiptItem;
import app.inventory_management.models.Receipt.Status;
import app.inventory_management.repository.ReceiptDAO;

import java.sql.SQLException;
import java.util.List;

public class ReceiptController {

    ReceiptDAO receiptDAO = new ReceiptDAO();

    public List<Receipt> loadAllReceipts() {
        try { return receiptDAO.loadAllReceipts(); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Receipt findReceipt(int orderId) {
        try { return receiptDAO.findReceiptById(orderId); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Receipt findReceiptByCode(String receiptCode) {
        try { return receiptDAO.findReceiptByCode(receiptCode); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Receipt createReceipt(Receipt receipt, List<ReceiptItem> items) {
        try {
            int newId = receiptDAO.saveReceipt(receipt);
            for (ReceiptItem item : items) {
                receiptDAO.saveReceiptItem(new ReceiptItem(
                        newId,
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ));
            }
            return receiptDAO.findReceiptById(newId);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<ReceiptItem> loadReceiptItems(int orderId) {
        try { return receiptDAO.loadReceiptItems(orderId); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void updateStatus(int orderId, Status newStatus) {
        try { receiptDAO.updateReceiptStatus(orderId, newStatus); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void deleteReceipt(int orderId) {
        try { receiptDAO.deleteReceipt(orderId); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public String getNextReceiptCode() {
        try { return receiptDAO.getNextReceiptCode(); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public String[] getCustomerContact(int customerId) {
        try { return receiptDAO.getCustomerContact(customerId); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }
}