package org.example.hospital_ppe_inventory_system;

import javafx.beans.property.SimpleStringProperty;

public class Supplier {
    private final SimpleStringProperty supplierID;
    private final SimpleStringProperty supplierName;
    private final SimpleStringProperty supplierContact;
    private final SimpleStringProperty supplierAddress;

    public Supplier(String supplierID, String supplierName, String supplierContact, String supplierAddress) {
        this.supplierID = new SimpleStringProperty(supplierID);
        this.supplierName = new SimpleStringProperty(supplierName);
        this.supplierContact = new SimpleStringProperty(supplierContact);
        this.supplierAddress = new SimpleStringProperty(supplierAddress);
    }

    public String getSupplierID() {
        return supplierID.get();
    }

    public String getSupplierName() {
        return supplierName.get();
    }

    public String getSupplierContact() {
        return supplierContact.get();
    }

    public String getSupplierAddress() {
        return supplierAddress.get();
    }

    public void setSupplierID(String supplierID) {
        this.supplierID.set(supplierID);
    }

    public void setSupplierName(String supplierName) {
        this.supplierName.set(supplierName);
    }

    public void setSupplierContact(String supplierContact) {
        this.supplierContact.set(supplierContact);
    }
    public void setSupplierAddress(String supplierAddress) {
        this.supplierAddress.set(supplierAddress);
    }
}
