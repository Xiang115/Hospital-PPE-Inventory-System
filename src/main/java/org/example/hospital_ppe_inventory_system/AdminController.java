package org.example.hospital_ppe_inventory_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController extends StaffController implements Initializable {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab tabUserManagement;

    @FXML
    private Tab tabSupplierManagement;

    @FXML
    private Tab tabHospitalManagement;

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableView<Supplier> supplierTable;

    @FXML
    private TableColumn<Supplier, String> colSupplierID;

    @FXML
    private TableColumn<Supplier, String> colSupplierName;

    @FXML
    private TableColumn<Supplier, String> colSupplierContact;

    @FXML
    private TableColumn<Supplier, String> colSupplierAddress;

    @FXML
    private TableView<Hospital> hospitalTable;

    @FXML
    private TableColumn<Hospital, String> colHospitalID;

    @FXML
    private TableColumn<Hospital, String> colHospitalName;

    @FXML
    private TableColumn<Hospital, String> colHospitalContact;

    @FXML
    private TableColumn<Hospital, String> colHospitalAddress;

    @FXML
    private TableColumn<User, String> colUserID;

    @FXML
    private TableColumn<User, String> colUserName;

    @FXML
    private TableColumn<User, String> colUserPassword;

    @FXML
    private TableColumn<User, String> colUserType;

    private final ObservableList<User> userList = FXCollections.observableArrayList();

    private final ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    private final ObservableList<Hospital> hospitalList = FXCollections.observableArrayList();

    private int maxNumericId = 0;

    private  String LastUserID;

    @FXML
    private void handleMenuUserManagement() {
        mainTabPane.getSelectionModel().select(tabUserManagement);
    }

    @FXML
    private void handleMenuSupplierManagement() {
        mainTabPane.getSelectionModel().select(tabSupplierManagement);
    }

    @FXML
    private void handleMenuHospitalManagement() {
        mainTabPane.getSelectionModel().select(tabHospitalManagement);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUserTable();
        loadSupplierTable();
        loadHospitalTable();
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


    private void loadUserTable() {
        colUserID.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUserPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colUserType.setCellValueFactory(new PropertyValueFactory<>("userType"));

        loadUsersFromFile();

        userTable.setItems(userList);
    }

    private void loadSupplierTable() {
        colSupplierID.setCellValueFactory(new PropertyValueFactory<>("supplierID"));
        colSupplierName.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colSupplierContact.setCellValueFactory(new PropertyValueFactory<>("supplierContact"));
        colSupplierAddress.setCellValueFactory(new PropertyValueFactory<>("supplierAddress"));

        loadSuppliersFromFile();

        supplierTable.setItems(supplierList);
    }

    private void loadHospitalTable() {
        colHospitalID.setCellValueFactory(new PropertyValueFactory<>("hospitalID"));
        colHospitalName.setCellValueFactory(new PropertyValueFactory<>("hospitalName"));
        colHospitalContact.setCellValueFactory(new PropertyValueFactory<>("hospitalContact"));
        colHospitalAddress.setCellValueFactory(new PropertyValueFactory<>("hospitalAddress"));

        loadHospitalsFromFile();

        hospitalTable.setItems(hospitalList);
    }

    public void loadHospitalsFromFile() {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadUsersFromFile() {
        String fileName = "C:\\Users\\Goh\\Desktop\\Hospital_PPE_Inventory_System\\src\\main\\resources\\org\\example\\hospital_ppe_inventory_system\\users.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] tokens = line.split(",");
                if (tokens.length >= 4) {
                    String id = tokens[0].trim();
                    String name = tokens[1].trim();
                    String password = tokens[2].trim();
                    String type = tokens[3].trim();

                    userList.add(new User(id, name, password, type));

                    int numericId = Integer.parseInt(id.substring(1));
                    if (numericId > maxNumericId) {
                        maxNumericId = numericId;
                    }
                }
            }

            if (maxNumericId > 0) {
                LastUserID = "U" + String.format("%03d", maxNumericId);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUsersToFile() {
        String fileName = "C:\\Users\\Goh\\Desktop\\Hospital_PPE_Inventory_System\\src\\main\\resources\\org\\example\\hospital_ppe_inventory_system\\users.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (User u : userList) {
                bw.write(u.getUserId() + "," + u.getName() + "," + u.getPassword() + "," + u.getUserType());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("File Error", "Unable to save to users.txt");
        }
    }

    private void saveSuppliersToFile() {
        String fileName = "C:\\Users\\Goh\\Desktop\\Hospital_PPE_Inventory_System\\src\\main\\resources\\org\\example\\hospital_ppe_inventory_system\\suppliers.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Supplier u : supplierList) {
                bw.write(u.getSupplierID() + "," + u.getSupplierName() + "," + u.getSupplierContact() + "," + u.getSupplierAddress());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("File Error", "Unable to save to users.txt");
        }
    }

    private void saveHospitalToFile() {
        String fileName = "C:\\Users\\Goh\\Desktop\\Hospital_PPE_Inventory_System\\src\\main\\resources\\org\\example\\hospital_ppe_inventory_system\\hospitals.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Hospital u : hospitalList) {
                bw.write(u.getHospitalID() + "," + u.getHospitalName() + "," + u.getHospitalContact() + "," + u.getHospitalAddress());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("File Error", "Unable to save to users.txt");
        }
    }

    public void handleAddUser(ActionEvent actionEvent) {
        Tab addUserTab = new Tab("Add User");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label lblName = new Label("Name:");
        TextField tfName = new TextField();
        Label lblPassword = new Label("Password:");
        PasswordField pfPassword = new PasswordField();
        Label lblUserType = new Label("User Type:");
        ComboBox<String> cbUserType = new ComboBox<>();
        cbUserType.getItems().addAll("admin", "staff");

        Button btnSave = new Button("Save");
        Button btnBack = new Button("Back");

        btnBack.setOnAction(e -> {
            mainTabPane.getTabs().remove(addUserTab);
            mainTabPane.getSelectionModel().select(tabUserManagement);
        });

        btnSave.setOnAction(e -> {
            String id = GenerateUserID(LastUserID);
            String name = tfName.getText().trim();
            String password = pfPassword.getText().trim();
            String userType = cbUserType.getValue();

            if (id.isEmpty() || name.isEmpty() || password.isEmpty() || userType == null) {
                showAlert("Input Error", "Please fill in all fields.");
                return;
            }

            boolean exists = userList.stream().anyMatch(u -> u.getUserId().equals(id));
            if (exists) {
                showAlert("Input Error", "User ID already exists.");
                System.out.println("Duplicate User ID " + id);
                return;
            }

            User newUser = new User(id, name, password, userType);
            userList.add(newUser);
            LastUserID = id;

            saveUsersToFile();

            mainTabPane.getTabs().remove(addUserTab);
            mainTabPane.getSelectionModel().select(tabUserManagement);
        });

        HBox buttonBox = new HBox(10, btnSave, btnBack);
        vbox.getChildren().addAll(lblName, tfName, lblPassword, pfPassword, lblUserType, cbUserType, buttonBox);
        addUserTab.setContent(vbox);

        mainTabPane.getTabs().add(addUserTab);
        mainTabPane.getSelectionModel().select(addUserTab);
    }

    private String GenerateUserID(String id) {
        if (id == null || id.isEmpty()) {
            return "U001";
        }

        String numericPart = id.substring(1);
        int nextNumber = Integer.parseInt(numericPart) + 1;

        return "U" + String.format("%03d", nextNumber);
    }

    public void handleUpdateUser(ActionEvent actionEvent) {
        Tab updateUserTab = new Tab("Update User");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label lblUserId = new Label("Enter User ID to update:");
        TextField tfUserId = new TextField();
        Label lblNewName = new Label("New Name:");
        TextField tfNewName = new TextField();
        Label lblNewPassword = new Label("New Password:");
        PasswordField pfNewPassword = new PasswordField();
        Label lblNewUserType = new Label("New User Type:");
        ComboBox<String> cbNewUserType = new ComboBox<>();
        cbNewUserType.getItems().addAll("admin", "staff");
        Button btnUpdate = new Button("Update");
        Button btnBack = new Button("Back");

        btnBack.setOnAction(e -> {
            mainTabPane.getTabs().remove(updateUserTab);
            mainTabPane.getSelectionModel().select(tabUserManagement);
        });

        btnUpdate.setOnAction(e -> {
            String id = tfUserId.getText().trim();
            String newName = tfNewName.getText().trim();
            String newPassword = pfNewPassword.getText().trim();
            String newUserType = cbNewUserType.getValue();

            if (id.isEmpty()) {
                showAlert("Input Error", "Please enter a User ID.");
                return;
            }

            User userToUpdate = null;
            for (User u : userList) {
                if (u.getUserId().equals(id)) {
                    userToUpdate = u;
                    break;
                }
            }
            if (userToUpdate == null) {
                showAlert("Not Found", "User not found.");
                return;
            }

            if (!newName.isEmpty()) {
                userToUpdate.setName(newName);
            }
            if (!newPassword.isEmpty()) {
                userToUpdate.setPassword(newPassword);
            }
            if (newUserType != null) {
                userToUpdate.setUserType(newUserType);
            }
            saveUsersToFile();
            userTable.refresh();
            mainTabPane.getTabs().remove(updateUserTab);
            mainTabPane.getSelectionModel().select(tabUserManagement);
        });

        HBox buttonBox = new HBox(10, btnUpdate, btnBack);
        vbox.getChildren().addAll(lblUserId, tfUserId, lblNewName, tfNewName, lblNewPassword, pfNewPassword, lblNewUserType, cbNewUserType, buttonBox);
        updateUserTab.setContent(vbox);

        mainTabPane.getTabs().add(updateUserTab);
        mainTabPane.getSelectionModel().select(updateUserTab);
    }

    public void handleDeleteUser(ActionEvent actionEvent) {
        Tab deleteUserTab = new Tab("Delete User");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label lblUserId = new Label("Enter User ID to delete:");
        TextField tfUserId = new TextField();
        Button btnDelete = new Button("Delete");
        Button btnBack = new Button("Back");

        btnBack.setOnAction(e -> {
            mainTabPane.getTabs().remove(deleteUserTab);
            mainTabPane.getSelectionModel().select(tabUserManagement);
        });

        btnDelete.setOnAction(e -> {
            String userId = tfUserId.getText().trim();

            if (userId.isEmpty()) {
                showAlert("Input Error", "Please enter a User ID.");
                return;
            }

            boolean removed = userList.removeIf(user -> user.getUserId().equals(userId));

            if (removed) {
                saveUsersToFile();
                showAlert("Success", "User deleted successfully.");
                mainTabPane.getTabs().remove(deleteUserTab);
                mainTabPane.getSelectionModel().select(tabUserManagement);
            } else {
                showAlert("Not Found", "User ID not found.");
            }
        });

        HBox buttonBox = new HBox(10, btnDelete, btnBack);
        vbox.getChildren().addAll(lblUserId, tfUserId, buttonBox);
        deleteUserTab.setContent(vbox);

        mainTabPane.getTabs().add(deleteUserTab);
        mainTabPane.getSelectionModel().select(deleteUserTab);
    }

    public void handleSearchUser(ActionEvent actionEvent) {
        Tab searchUserTab = new Tab("Search User");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label lblUserId = new Label("Enter User ID to search:");
        TextField tfUserId = new TextField();
        Button btnSearch = new Button("Search");
        Button btnBack = new Button("Back");

        TableView<User> tempTableView = new TableView<>();
        tempTableView.getStyleClass().add("temp-table-view");
        TableColumn<User, String> tempColUserID = new TableColumn<>("User ID");
        TableColumn<User, String> tempColUserName = new TableColumn<>("Name");
        TableColumn<User, String> tempColUserType = new TableColumn<>("User Type");

        tempColUserID.setCellValueFactory(new PropertyValueFactory<>("userId"));
        tempColUserName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tempColUserType.setCellValueFactory(new PropertyValueFactory<>("userType"));

        tempTableView.getColumns().addAll(tempColUserID, tempColUserName, tempColUserType);

        tempTableView.setPrefHeight(50);
        tempTableView.setPrefWidth(400);

        tempColUserID.setPrefWidth(100);
        tempColUserName.setPrefWidth(150);
        tempColUserType.setPrefWidth(100);

        tempTableView.setVisible(false);

        btnBack.setOnAction(e -> {
            userTable.setItems(userList);
            mainTabPane.getTabs().remove(searchUserTab);
            mainTabPane.getSelectionModel().select(tabUserManagement);
        });

        btnSearch.setOnAction(e -> {
            String userId = tfUserId.getText().trim();

            if (userId.isEmpty()) {
                showAlert("Input Error", "Please enter a User ID.");
                return;
            }

            ObservableList<User> filteredList = userList.filtered(user ->
                    user.getUserId().equals(userId)
            );

            if (filteredList.isEmpty()) {
                showAlert("Not Found", "User ID not found.");
            } else {
                tempTableView.setItems(filteredList); // Show results in temporary TableView
                tempTableView.setVisible(true);
            }
        });

        searchUserTab.setOnClosed(event -> {
            userTable.setItems(userList);
        });

        HBox buttonBox = new HBox(10, btnSearch, btnBack);
        vbox.getChildren().addAll(lblUserId, tfUserId, buttonBox, tempTableView);
        searchUserTab.setContent(vbox);

        mainTabPane.getTabs().add(searchUserTab);
        mainTabPane.getSelectionModel().select(searchUserTab);
    }

    public void handleUpdateSupplier(ActionEvent actionEvent) {
        Tab updateSupplierTab = new Tab("Update Supplier");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label lblSupplierId = new Label("Enter Supplier ID to update:");
        TextField tfSupplierId = new TextField();
        Label lblNewName = new Label("New Supplier Name:");
        TextField tfNewName = new TextField();
        Label lblNewContact = new Label("New Supplier Contact:");
        TextField tfNewContact = new TextField();
        Label lblNewAddress = new Label("New Supplier Address:");
        TextField tfNewAddress = new TextField();
        Button btnUpdate = new Button("Update");
        Button btnBack = new Button("Back");

        btnBack.setOnAction(e -> {
            mainTabPane.getTabs().remove(updateSupplierTab);
            mainTabPane.getSelectionModel().select(tabSupplierManagement);
        });

        btnUpdate.setOnAction(e -> {
            String id = tfSupplierId.getText().trim();
            String newName = tfNewName.getText().trim();
            String newContact = tfNewContact.getText().trim();
            String newAddress = tfNewAddress.getText().trim();

            if (id.isEmpty()) {
                showAlert("Input Error", "Please enter a User ID.");
                return;
            }

            Supplier supplierToUpdate = null;
            for (Supplier u : supplierList) {
                if (u.getSupplierID().equals(id)) {
                    supplierToUpdate = u;
                    break;
                }
            }
            if (supplierToUpdate == null) {
                showAlert("Not Found", "User not found.");
                return;
            }

            if (!newName.isEmpty()) {
                supplierToUpdate.setSupplierName(newName);
            }
            if (!newContact.isEmpty()) {
                supplierToUpdate.setSupplierContact(newContact);
            }
            if (!newAddress.isEmpty()) {
                supplierToUpdate.setSupplierAddress(newAddress);
            }
            saveSuppliersToFile();
            supplierTable.refresh();
            mainTabPane.getTabs().remove(updateSupplierTab);
            mainTabPane.getSelectionModel().select(tabSupplierManagement);
        });

        HBox buttonBox = new HBox(10, btnUpdate, btnBack);
        vbox.getChildren().addAll(lblSupplierId, tfSupplierId, lblNewName, tfNewName, lblNewContact, tfNewContact, lblNewAddress, tfNewAddress, buttonBox);
        updateSupplierTab.setContent(vbox);

        mainTabPane.getTabs().add(updateSupplierTab);
        mainTabPane.getSelectionModel().select(updateSupplierTab);
    }

    public void handleUpdateHospital(ActionEvent actionEvent) {
        Tab updateHospitalTab = new Tab("Update Hospital");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label lblHospitalId = new Label("Enter Hospital ID to update:");
        TextField tfHospitalId = new TextField();
        Label lblNewName = new Label("New Hospital Name:");
        TextField tfNewName = new TextField();
        Label lblNewContact = new Label("New Hospital Contact:");
        TextField tfNewContact = new TextField();
        Label lblNewAddress = new Label("New Hospital Address:");
        TextField tfNewAddress = new TextField();
        Button btnUpdate = new Button("Update");
        Button btnBack = new Button("Back");

        btnBack.setOnAction(e -> {
            mainTabPane.getTabs().remove(updateHospitalTab);
            mainTabPane.getSelectionModel().select(tabHospitalManagement);
        });

        btnUpdate.setOnAction(e -> {
            String id = tfHospitalId.getText().trim();
            String newName = tfNewName.getText().trim();
            String newContact = tfNewContact.getText().trim();
            String newAddress = tfNewAddress.getText().trim();

            if (id.isEmpty()) {
                showAlert("Input Error", "Please enter a User ID.");
                return;
            }

            Hospital hospitalToUpdate = null;
            for (Hospital u : hospitalList) {
                if (u.getHospitalID().equals(id)) {
                    hospitalToUpdate = u;
                    break;
                }
            }
            if (hospitalToUpdate == null) {
                showAlert("Not Found", "User not found.");
                return;
            }

            if (!newName.isEmpty()) {
                hospitalToUpdate.setHospitalName(newName);
            }
            if (!newContact.isEmpty()) {
                hospitalToUpdate.setHospitalContact(newContact);
            }
            if (!newAddress.isEmpty()) {
                hospitalToUpdate.setHospitalAddress(newAddress);
            }
            saveHospitalToFile();
            hospitalTable.refresh();
            mainTabPane.getTabs().remove(updateHospitalTab);
            mainTabPane.getSelectionModel().select(tabHospitalManagement);
        });

        HBox buttonBox = new HBox(10, btnUpdate, btnBack);
        vbox.getChildren().addAll(lblHospitalId, tfHospitalId, lblNewName, tfNewName, lblNewContact, tfNewContact, lblNewAddress, tfNewAddress, buttonBox);
        updateHospitalTab.setContent(vbox);

        mainTabPane.getTabs().add(updateHospitalTab);
        mainTabPane.getSelectionModel().select(updateHospitalTab);
    }
}
