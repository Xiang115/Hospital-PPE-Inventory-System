package org.example.hospital_ppe_inventory_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab tabUserManagement;

    @FXML
    private Tab tabInventoryManagement;

    @FXML
    private Tab tabSupplierManagement;

    @FXML
    private Tab tabHospitalManagement;

    @FXML
    private Tab tabReportingTracking;

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> colUserID;

    @FXML
    private TableColumn<User, String> colUserName;

    @FXML
    private TableColumn<User, String> colUserType;

    // ObservableList to hold the user data
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    private String LastUserID;

    @FXML
    private void handleMenuUserManagement() {
        // Select the first tab (assuming "User Management" is the first one)
        mainTabPane.getSelectionModel().select(tabUserManagement);
    }

    @FXML
    private void handleMenuInventoryManagement() {
        // Select the second tab (assuming "Inventory Management" is the second one)
        mainTabPane.getSelectionModel().select(tabInventoryManagement);
    }

    @FXML
    private void handleMenuSupplierManagement() {
        mainTabPane.getSelectionModel().select(tabSupplierManagement);
    }

    @FXML
    private void handleMenuHospitalManagement() {
        mainTabPane.getSelectionModel().select(tabHospitalManagement);
    }

    @FXML
    private void handleMenuReportingTracking() {
        mainTabPane.getSelectionModel().select(tabReportingTracking);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up TableView columns for User Management
        colUserID.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUserType.setCellValueFactory(new PropertyValueFactory<>("userType"));

        loadUsersFromFile();

        userTable.setItems(userList);

        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            Stage stage = (Stage) mainTabPane.getScene().getWindow();
            if (newTab != null) {
                stage.setTitle(newTab.getText());
            } else {
                stage.setTitle("Hospital PPE Inventory System");
            }
        });
    }

    private void loadUsersFromFile() {
        String fileName = "C:\\Users\\Goh\\Desktop\\Hospital_PPE_Inventory_System\\src\\main\\resources\\org\\example\\hospital_ppe_inventory_system\\users.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) continue;

                // Split the line by commas (assumes format: userId,name,password,userType)
                String[] tokens = line.split(",");
                if (tokens.length >= 4) {
                    String id = tokens[0].trim();
                    String name = tokens[1].trim();
                    String password = tokens[2].trim();
                    String type = tokens[3].trim();

                    // Create a new User object and add it to the list
                    userList.add(new User(id, name, password, type));

                    LastUserID = id;
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
                return;
            }

            User newUser = new User(id, name, password, userType);
            userList.add(newUser);
            saveUsersToFile();

            mainTabPane.getTabs().remove(addUserTab);
            mainTabPane.getSelectionModel().select(tabUserManagement);
        });

        vbox.getChildren().addAll(lblName, tfName, lblPassword, pfPassword, lblUserType, cbUserType, btnSave);
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
        return "U" + String.format("%03d", nextNumber); // Pad to 3 digits (e.g., U001)
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

        vbox.getChildren().addAll(lblUserId, tfUserId, lblNewName, tfNewName, lblNewPassword, pfNewPassword, lblNewUserType, cbNewUserType, btnUpdate);
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
        Button btnBack = new Button("Back"); // Add Back button

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
                saveUsersToFile();  // Update the file
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
        Button btnBack = new Button("Back"); // Add Back button

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

        // Set column widths
        tempColUserID.setPrefWidth(100);
        tempColUserName.setPrefWidth(150);
        tempColUserType.setPrefWidth(100);

        tempTableView.setVisible(false);

        btnBack.setOnAction(e -> {
            userTable.setItems(userList); // Reset to full list
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
        vbox.getChildren().addAll(lblUserId, tfUserId, buttonBox,tempTableView);
        searchUserTab.setContent(vbox);

        mainTabPane.getTabs().add(searchUserTab);
        mainTabPane.getSelectionModel().select(searchUserTab);
    }

    public void handleCreateInventory(ActionEvent actionEvent) {
    }

    public void handleUpdateInventory(ActionEvent actionEvent) {
    }

    public void handleAddSupplier(ActionEvent actionEvent) {
    }

    public void handleUpdateSupplier(ActionEvent actionEvent) {
    }

    public void handleDeleteSupplier(ActionEvent actionEvent) {
    }

    public void handleAddHospital(ActionEvent actionEvent) {
    }

    public void handleUpdateHospital(ActionEvent actionEvent) {
    }

    public void handleDeleteHospital(ActionEvent actionEvent) {
    }

    public void handleSearchReport(ActionEvent actionEvent) {
    }

    // Helper method for showing alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
