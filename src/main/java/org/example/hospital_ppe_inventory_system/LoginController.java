package org.example.hospital_ppe_inventory_system;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    @FXML
    private TextField TFUserID;

    @FXML
    private PasswordField PFPassword;

    @FXML
    private Button BtnEnter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        BtnEnter.setOnAction(event -> handleLogin());
    }

    private void handleLogin() {
        String enteredUserId = TFUserID.getText().trim();
        String enteredPassword = PFPassword.getText().trim();

        boolean userFound = false;
        String userType = "";

        // Read users.txt file and search for matching credentials
        String userFilePath = "C:\\Users\\Goh\\Desktop\\Hospital_PPE_Inventory_System\\src\\main\\resources\\org\\example\\hospital_ppe_inventory_system\\users.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(userFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Assume the format: userID,name,password,userType
                String[] tokens = line.split(",");
                if (tokens.length >= 4) {
                    String userId = tokens[0].trim();
                    String password = tokens[2].trim();
                    String type = tokens[3].trim();

                    // Check if entered credentials match
                    if (userId.equals(enteredUserId) && password.equals(enteredPassword)) {
                        userFound = true;
                        userType = type;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("File Error", "Could not read users.txt file.");
            return;
        }

        if (userFound) {
            // Depending on the user type, load the appropriate dashboard
            try {
                FXMLLoader loader = new FXMLLoader();
                Parent root;
                Stage stage = (Stage) BtnEnter.getScene().getWindow();

                if (userType.equalsIgnoreCase("admin")) {
                    loader.setLocation(getClass().getResource("AdminDashboard.fxml"));
                    root = loader.load();
                } else if (userType.equalsIgnoreCase("staff")) {
                    loader.setLocation(getClass().getResource("StaffDashboard.fxml"));
                    root = loader.load();
                } else {
                    showAlert("Login Error", "Invalid user type found.");
                    return;
                }

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Loading Error", "Unable to load dashboard.");
            }
        } else {
            // If credentials do not match, display an error message
            showAlert("Login Failed", "Invalid User ID or Password.");
        }
    }

    // Helper method to display alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
