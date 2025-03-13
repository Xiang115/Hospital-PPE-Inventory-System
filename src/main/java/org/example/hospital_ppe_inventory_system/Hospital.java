package org.example.hospital_ppe_inventory_system;

import javafx.beans.property.SimpleStringProperty;

public class Hospital {
    private final SimpleStringProperty HospitalID;
    private final SimpleStringProperty HospitalName;
    private final SimpleStringProperty HospitalContact;
    private final SimpleStringProperty HospitalAddress;

    public  Hospital(String HospitalID, String HospitalName, String HospitalContact, String HospitalAddress){
        this.HospitalID = new SimpleStringProperty(HospitalID);
        this.HospitalName = new SimpleStringProperty(HospitalName);
        this.HospitalContact = new SimpleStringProperty(HospitalContact);
        this.HospitalAddress = new SimpleStringProperty(HospitalAddress);
    }

    public String getHospitalID() {
        return HospitalID.get();
    }

    public String getHospitalName() {
        return HospitalName.get();
    }

    public String getHospitalContact() {
        return HospitalContact.get();
    }

    public String getHospitalAddress() {
        return HospitalAddress.get();
    }

    public void setHospitalID(String HospitalID) {
        this.HospitalID.set(HospitalID);
    }

    public void setHospitalName(String HospitalName) {
        this.HospitalName.set(HospitalName);
    }

    public void setHospitalContact(String HospitalContact) {
        this.HospitalContact.set(HospitalContact);
    }

    public void setHospitalAddress(String HospitalAddress) {
        this.HospitalAddress.set(HospitalAddress);
    }
}
