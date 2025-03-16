package org.example.hospital_ppe_inventory_system;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StaffController implements Initializable {
    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab tabInventoryManagement;

    @FXML
    private Tab tabReportingTracking;

    @FXML
    private MenuBar menuBar;

    @FXML
    private TableView<InventoryItem> inventoryTable;

    @FXML
    private TableColumn<InventoryItem, String> colItemCode;

    @FXML
    private TableColumn<InventoryItem, String> colItemName;

    @FXML
    private TableColumn<InventoryItem, String> colInventorySupplierCode;

    @FXML
    private TableColumn<InventoryItem, String> colInventorySupplierName;

    @FXML
    private TableColumn<InventoryItem, Integer> colQuantity;

    @FXML
    private ComboBox<String> cbFilter;

    @FXML
    private ComboBox<String> cbItemList;

    @FXML
    private DatePicker dpStartDate;

    @FXML
    private DatePicker dpEndDate;

    @FXML
    private TableView<Transaction> reportTable;

    @FXML
    private TableColumn<Transaction, String> colReportDetailCode;

    @FXML
    private TableColumn<Transaction, String> colReportDetailName;

    @FXML
    private TableColumn<Transaction, Integer> colReportQuantity;

    @FXML
    private TableColumn<Transaction, String> colReportDate;

    private final ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    private final ObservableList<Hospital> hospitalList = FXCollections.observableArrayList();

    private final ObservableList<Item> itemList = FXCollections.observableArrayList();

    private final ObservableList<InventoryItem> inventoryList = FXCollections.observableArrayList();

    private final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    boolean ReadSupplier = false;

    boolean ReadHospital = false;

    @FXML
    private void handleMenuInventoryManagement() {
        mainTabPane.getSelectionModel().select(tabInventoryManagement);
    }

    @FXML
    private void handleMenuReportingTracking() {
        mainTabPane.getSelectionModel().select(tabReportingTracking);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadSuppliersFromFile();
        loadHospitalsFromFile();
        loadItemsFromFile();
        loadInventoryTable();
        loadReportTable();

        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            Stage stage = (Stage) mainTabPane.getScene().getWindow();
            if (newTab != null) {
                stage.setTitle(newTab.getText());
            } else {
                stage.setTitle("Hospital PPE Inventory System");
            }
        });
    }

    public void  loadReportTable() {
        cbFilter.getItems().addAll("Received", "Distributed");

        cbItemList.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String code) {
                return itemList.stream()
                        .filter(i -> i.getCode().equals(code))
                        .findFirst()
                        .map(i -> code + " - " + i.getName())
                        .orElse(code);
            }

            @Override
            public String fromString(String string) {
                return string.split(" - ")[0];
            }
        });
        cbItemList.getItems().addAll(itemList.stream().map(Item::getCode).collect(Collectors.toList()));

        loadTransactions();

        colReportDetailCode.setCellValueFactory(new PropertyValueFactory<>("partnerCode"));
        colReportDetailName.setCellValueFactory(cellData -> {
            Transaction t = cellData.getValue();
            if(t.getType().equals("0")) {
                return new SimpleStringProperty(getSupplierName(t.getPartnerCode()));
            } else {
                return new SimpleStringProperty(getHospitalName(t.getPartnerCode()));
            }
        });
        colReportQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colReportDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatDateTime(cellData.getValue().getTimestamp()))
        );
        reportTable.setItems(transactionList.filtered(t -> true)); // Show all initially
    }

    public void loadInventoryTable() {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colInventorySupplierCode.setCellValueFactory(new PropertyValueFactory<>("supplierCode"));
        colInventorySupplierName.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colQuantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());

        if(!ReadSupplier && !ReadHospital) {
            loadSuppliersFromFile();
            loadHospitalsFromFile();
        }

        loadInventoryFromFile();

        inventoryTable.setItems(inventoryList);
    }

    private void loadInventoryFromFile() {
        String fileName = "C:\\Users\\Goh\\Desktop\\Hospital_PPE_Inventory_System\\src\\main\\resources\\org\\example\\hospital_ppe_inventory_system\\ppe.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 3) {
                    String itemCode = tokens[0].trim();
                    String supplierCode = tokens[1].trim();
                    int quantity = Integer.parseInt(tokens[2].trim());

                    String itemName = itemList.stream()
                            .filter(item -> item.getCode().equals(itemCode))
                            .findFirst()
                            .map(Item::getName)
                            .orElse("Unknown Item");

                    String supplierName = supplierList.stream()
                            .filter(supplier -> supplier.getSupplierID().equals(supplierCode))
                            .findFirst()
                            .map(Supplier::getSupplierName)
                            .orElse("Unknown Supplier");

                    inventoryList.add(new InventoryItem(
                            itemCode, itemName, supplierCode, supplierName, quantity
                    ));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void loadItemsFromFile() {
        String fileName = "C:\\Users\\Goh\\Desktop\\Hospital_PPE_Inventory_System\\src\\main\\resources\\org\\example\\hospital_ppe_inventory_system\\Items.txt"; // Adjust path
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 2) {
                    String code = tokens[0].trim();
                    String name = tokens[1].trim();
                    itemList.add(new Item(code, name));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSuppliersFromFile() {
        String fileName = "C:\\Users\\Goh\\Desktop\\Hospital_PPE_Inventory_System\\src\\main\\resources\\org\\example\\hospital_ppe_inventory_system\\suppliers.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] tokens = line.split(",");
                if (tokens.length >= 4) {
                    String id = tokens[0].trim();
                    String name = tokens[1].trim();
                    String contact = tokens[2].trim();

                    StringBuilder address = new StringBuilder();
                    for (int i = 3; i < tokens.length; i++) {
                        address.append(tokens[i]);
                    }

                    supplierList.add(new Supplier(id, name, contact, address.toString()));
                }
            }
            ReadSupplier = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHospitalsFromFile() {
        String fileName = "C:\\Users\\Goh\\Desktop\\Hospital_PPE_Inventory_System\\src\\main\\resources\\org\\example\\hospital_ppe_inventory_system\\hospitals.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] tokens = line.split(",");
                if (tokens.length >= 4) {
                    String id = tokens[0].trim();
                    String name = tokens[1].trim();
                    String contact = tokens[2].trim();

                    StringBuilder address = new StringBuilder();
                    for (int i = 3; i < tokens.length; i++) {
                        address.append(tokens[i]);
                    }

                    hospitalList.add(new Hospital(id, name, contact, address.toString()));
                }
            }
            ReadHospital = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updatePpeQuantity(String itemCode, String supplierCode, int quantityChange, String hospitalCode) {
        for (InventoryItem item : inventoryList) {
            if (item.getItemCode().equals(itemCode) && item.getSupplierCode().equals(supplierCode)) {
                int newQuantity = item.getQuantity() + quantityChange;

                if (newQuantity > item.getQuantity()) {
                    logTransaction("0", itemCode, supplierCode, quantityChange);
                } else {
                    logTransaction("1", itemCode, hospitalCode, -1 * quantityChange);
                }

                item.setQuantity(newQuantity);
                System.out.println("Updated quantity: " + item.getQuantity());
                break;
            }
        }
        savePpeFile();
    }

    private void savePpeFile() {
        String filePPEpath = "C:\\Users\\Goh\\Desktop\\Hospital_PPE_Inventory_System\\src\\main\\resources\\org\\example\\hospital_ppe_inventory_system\\ppe.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePPEpath))) {
            for (InventoryItem item : inventoryList) {
                bw.write(String.format("%s,%s,%d",
                        item.getItemCode(),
                        item.getSupplierCode(),
                        item.getQuantity()));
                bw.newLine();
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to save PPE data");
        }
    }

    private void logTransaction(String type, String itemCode, String partnerCode, int quantity) {
        String fileTransactionPath = "C:\\Users\\Goh\\Desktop\\Hospital_PPE_Inventory_System\\src\\main\\resources\\org\\example\\hospital_ppe_inventory_system\\transactions.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileTransactionPath, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            bw.write(String.format("%s,%s,%s,%d,%s",
                    type, itemCode, partnerCode, quantity, timestamp));
            bw.newLine();
        } catch (IOException e) {
            showAlert("Error", "Failed to log transaction");
        }
    }


    public void handleCreateInventory(ActionEvent actionEvent) {
        for (InventoryItem item : inventoryList) {
            item.setQuantity(100);
        }

        savePpeFile();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("transactions.txt"))) {
            bw.write("");
        } catch (IOException e) {
            showAlert("Error", "Failed to clear transactions");
        }

        inventoryTable.refresh();
    }

    public void handleReceiveInventory(ActionEvent actionEvent) {
        Tab receiveTab = new Tab("Receive Inventory");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        ComboBox<String> itemCombo = new ComboBox<>();
        itemCombo.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String code) {
                return itemList.stream()
                        .filter(i -> i.getCode().equals(code))
                        .findFirst()
                        .map(i -> code + " - " + i.getName())
                        .orElse(code);
            }

            @Override
            public String fromString(String string) {
                return string.split(" - ")[0];
            }
        });
        itemCombo.getItems().addAll(itemList.stream().map(Item::getCode).collect(Collectors.toList()));

        ComboBox<String> supplierCombo = new ComboBox<>();
        supplierCombo.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String code) {
                return supplierList.stream()
                        .filter(s -> s.getSupplierID().equals(code))
                        .findFirst()
                        .map(s -> code + " - " + s.getSupplierName())
                        .orElse(code);
            }

            @Override
            public String fromString(String string) {
                return string.split(" - ")[0];
            }
        });
        supplierCombo.getItems().addAll(supplierList.stream().map(Supplier::getSupplierID).collect(Collectors.toList()));

        TextField quantityField = new TextField();
        Button btnSubmit = new Button("Submit");
        Button btnBack = new Button("Back");

        btnBack.setOnAction(e -> {
            mainTabPane.getTabs().remove(receiveTab);
            mainTabPane.getSelectionModel().select(tabInventoryManagement);
        });

        btnSubmit.setOnAction(e -> {
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity <= 0) throw new NumberFormatException();

                String itemCode = itemCombo.getValue();
                String supplierCode = supplierCombo.getValue();

                boolean combinationExists = inventoryList.stream()
                        .anyMatch(item -> item.getItemCode().equals(itemCode) && item.getSupplierCode().equals(supplierCode));

                if (!combinationExists) {
                    showAlert("Invalid Combination", "The selected item and supplier combination does not exist.");
                    return;
                }

                updatePpeQuantity(itemCode, supplierCode, quantity, null);

                inventoryTable.refresh();
                mainTabPane.getTabs().remove(receiveTab);
                mainTabPane.getSelectionModel().select(tabInventoryManagement);
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter a valid positive integer quantity");
            }
        });

        HBox buttonBox = new HBox(10, btnSubmit, btnBack);
        vbox.getChildren().addAll(
                new Label("Item:"), itemCombo,
                new Label("Supplier:"), supplierCombo,
                new Label("Quantity:"), quantityField,
                buttonBox
        );

        receiveTab.setContent(vbox);
        mainTabPane.getTabs().add(receiveTab);
        mainTabPane.getSelectionModel().select(receiveTab);
    }

    public void handleDistributeInventory(ActionEvent actionEvent) {
        Tab distributeTab = new Tab("Distribute Inventory");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        ComboBox<String> itemCombo = new ComboBox<>();
        itemCombo.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String code) {
                return itemList.stream()
                        .filter(i -> i.getCode().equals(code))
                        .findFirst()
                        .map(i -> code + " - " + i.getName())
                        .orElse(code);
            }

            @Override
            public String fromString(String string) {
                return string.split(" - ")[0];
            }
        });
        itemCombo.getItems().addAll(itemList.stream().map(Item::getCode).collect(Collectors.toList()));

        ComboBox<String> hospitalCombo = new ComboBox<>();
        hospitalCombo.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String code) {
                return hospitalList.stream()
                        .filter(h -> h.getHospitalID().equals(code))
                        .findFirst()
                        .map(h -> code + " - " + h.getHospitalName())
                        .orElse(code);
            }

            @Override
            public String fromString(String string) {
                return string.split(" - ")[0];
            }
        });
        hospitalCombo.getItems().addAll(hospitalList.stream().map(Hospital::getHospitalID).collect(Collectors.toList()));

        TextField quantityField = new TextField();
        Button btnSubmit = new Button("Submit");
        Button btnBack = new Button("Back");

        btnBack.setOnAction(e -> {
            mainTabPane.getTabs().remove(distributeTab);
            mainTabPane.getSelectionModel().select(tabInventoryManagement);
        });

        btnSubmit.setOnAction(e -> {
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity <= 0) throw new NumberFormatException();

                String itemCode = itemCombo.getValue();
                String hospitalCode = hospitalCombo.getValue();

                InventoryItem item = inventoryList.stream()
                        .filter(i -> i.getItemCode().equals(itemCode))
                        .findFirst()
                        .orElse(null);

                if (item == null || item.getQuantity() < quantity) {
                    showAlert("Insufficient Stock", "Not enough inventory to distribute");
                    return;
                }

                updatePpeQuantity(itemCode, item.getSupplierCode(), -quantity, hospitalCode);

                inventoryTable.refresh();
                mainTabPane.getTabs().remove(distributeTab);
                mainTabPane.getSelectionModel().select(tabInventoryManagement);
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter a valid positive integer quantity");
            }
        });

        HBox buttonBox = new HBox(10, btnSubmit, btnBack);
        vbox.getChildren().addAll(
                new Label("Item:"), itemCombo,
                new Label("Hospital:"), hospitalCombo,
                new Label("Quantity:"), quantityField,
                buttonBox
        );

        distributeTab.setContent(vbox);
        mainTabPane.getTabs().add(distributeTab);
        mainTabPane.getSelectionModel().select(distributeTab);
    }

    private void loadTransactions() {
        String filePath = "C:\\Users\\Goh\\Desktop\\Hospital_PPE_Inventory_System\\src\\main\\resources\\org\\example\\hospital_ppe_inventory_system\\transactions.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts.length >= 5) {
                    LocalDateTime date = LocalDateTime.parse(parts[4], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    transactionList.add(new Transaction(
                            parts[0],
                            parts[1],
                            parts[2],
                            Integer.parseInt(parts[3]),
                            date
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private String getSupplierName(String code) {
        return supplierList.stream()
                .filter(s -> s.getSupplierID().equals(code))
                .findFirst()
                .map(Supplier::getSupplierName)
                .orElse("Unknown Supplier");
    }

    private String getHospitalName(String code) {
        return hospitalList.stream()
                .filter(h -> h.getHospitalID().equals(code))
                .findFirst()
                .map(Hospital::getHospitalName)
                .orElse("Unknown Hospital");
    }


    public void handleSearchReport(ActionEvent actionEvent) {
        String filter = cbFilter.getValue();
        String itemCode = cbItemList.getValue();
        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();

        Predicate<Transaction> filterPredicate = t -> {
            boolean dateValid = true;
            if(startDate != null && endDate != null) {
                LocalDate transDate = t.getTimestamp().toLocalDate();
                dateValid = !transDate.isBefore(startDate) && !transDate.isAfter(endDate);
            }

            boolean typeValid = filter == null ||
                    (filter.equals("Received") && t.getType().equals("0")) ||
                    (filter.equals("Distributed") && t.getType().equals("1"));

            boolean itemValid = itemCode == null || itemCode.isEmpty() ||
                    t.getItemCode().equals(itemCode);

            return dateValid && typeValid && itemValid;
        };

        if(filter != null) {
            if(filter.equals("Received")) {
                colReportDetailCode.setText("Supplier Code");
                colReportDetailName.setText("Supplier Name");
            } else {
                colReportDetailCode.setText("Hospital Code");
                colReportDetailName.setText("Hospital Name");
            }
        }
        reportTable.setItems(transactionList.filtered(filterPredicate));
    }

    public void handleMenuLogOut(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log Out");
        alert.setHeaderText("Are you sure you want to log out?");
        alert.setContentText("Click OK to confirm.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Node sourceNode = menuBar;
                Stage stage = (Stage) sourceNode.getScene().getWindow();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load login screen.");
            }
        }
    }
}
