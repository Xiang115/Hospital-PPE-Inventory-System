module org.example.hospital_ppe_inventory_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens org.example.hospital_ppe_inventory_system to javafx.fxml;
    exports org.example.hospital_ppe_inventory_system;
}