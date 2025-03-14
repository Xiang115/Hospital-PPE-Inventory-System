package org.example.hospital_ppe_inventory_system;

import java.time.LocalDateTime;

public class Transaction {
    private final String type;
    private final String itemCode;
    private final String partnerCode;
    private final int quantity;
    private final LocalDateTime timestamp;

    public Transaction(String type, String itemCode, String partnerCode, int quantity, LocalDateTime timestamp) {
        this.type = type;
        this.itemCode = itemCode;
        this.partnerCode = partnerCode;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

