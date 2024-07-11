package com.accounting.app.controller;

import com.accounting.app.database.DatabaseUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationController {
    @FXML
    private VBox registrationView;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField confirmPasswordField;

    public void register() throws SQLException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (password.equals(confirmPassword)) {
            boolean isRegistered = registerUser(username, password);
            if (isRegistered) {
                showAlert("Registration successful.");
            } else {
                showAlert("Failed to register the user.");
            }
        } else {
            showAlert("Please make sure the passwords match");
        }
    }

    private boolean registerUser(String username, String password) throws SQLException {
        Connection connection = DatabaseUtil.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            statement.setString(1, username);
            statement.setString(2, password);
            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error occurred while registering the user");
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void openLoginView(ActionEvent actionEvent) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/accounting/app/login-view.fxml"));
            Parent loginView = loader.load();
            registrationView.getChildren().setAll(loginView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
