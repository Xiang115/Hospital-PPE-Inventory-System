package org.example.hospital_ppe_inventory_system;

public class Item {
    private final String code;
    private final String name;

    public Item(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}