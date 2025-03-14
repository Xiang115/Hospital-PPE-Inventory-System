package org.example.hospital_ppe_inventory_system;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class InventoryItem {
    private final String itemCode;
    private final String itemName;
    private final String supplierCode;
    private final String supplierName;
    private IntegerProperty quantity;

    public InventoryItem(String itemCode, String itemName, String supplierCode, String supplierName, int quantity) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.supplierCode = supplierCode;
        this.supplierName = supplierName;
        this.quantity = new SimpleIntegerProperty(quantity);
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }
}