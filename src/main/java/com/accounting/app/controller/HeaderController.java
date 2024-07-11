package com.accounting.app.controller;

import com.accounting.app.MainApp;
import javafx.event.ActionEvent;

import java.io.IOException;

public class HeaderController {
    public void openAddNewProductionLineView(ActionEvent actionEvent) throws IOException {
        MainApp.changeScene("production-line-view.fxml");
    }

    public void openHistoryView(ActionEvent actionEvent) throws IOException {
        MainApp.changeScene("history-view.fxml");
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        MainApp.changeScene("login-view.fxml");
    }

    public void openEmployeeView(ActionEvent actionEvent) throws IOException {
        MainApp.changeScene("employee-view.fxml");
    }

    public void openHomeView(ActionEvent actionEvent) throws IOException {
        MainApp.changeScene("home-view.fxml");
    }
}
