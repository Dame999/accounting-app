package com.accounting.app.controller;

import com.accounting.app.database.DatabaseUtil;
import com.accounting.app.manager.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private VBox loginView;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    public void login() throws SQLException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        boolean isAuthenticated = authenticateUser(username, password);

        if (isAuthenticated) {
            System.out.println("Login successfully");
            openHomeView();
        } else {
            showAlert("Invalid username or password");
            System.out.println("error");
        }
    }

    private boolean authenticateUser(String username, String password) throws SQLException {
        Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("id");
                SessionManager.setUserId(userId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error occurred while logging in!");
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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void openRegistrationPage(ActionEvent actionEvent) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/accounting/app/registration-view.fxml"));

            URL resource = getClass().getResource("/com/accounting/app/registration-view.fxml");
            System.out.println("Resource URL: " + resource);

            Parent registrationView = loader.load();
            loginView.getChildren().setAll(registrationView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openHomeView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/accounting/app/home-view.fxml"));
            Parent homeView = loader.load();
            loginView.getChildren().setAll(homeView);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
