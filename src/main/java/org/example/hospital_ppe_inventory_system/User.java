package org.example.hospital_ppe_inventory_system;

import javafx.beans.property.SimpleStringProperty;

public class User {
    private final SimpleStringProperty userId;
    private final SimpleStringProperty name;
    private final SimpleStringProperty password;
    private final SimpleStringProperty userType;

    public User(String userId, String name, String password, String userType) {
        this.userId = new SimpleStringProperty(userId);
        this.name = new SimpleStringProperty(name);
        this.password = new SimpleStringProperty(password);
        this.userType = new SimpleStringProperty(userType);
    }

    public String getUserId() {
        return userId.get();
    }

    public void setUserId(String id) {
        userId.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getUserType() {
        return userType.get();
    }

    public void setUserType(String type) {
        this.userType.set(type);
    }
}
