package com.accounting.app.controller;

import com.accounting.app.database.DatabaseUtil;
import com.accounting.app.model.UserHistory;
import com.accounting.app.utils.DateFormatter;
import com.accounting.app.utils.Validator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HistoryController {
    @FXML
    public GridPane dataGridPane;
    public DatePicker fromDatePicker;
    public DatePicker toDatePicker;
    public Button generateGlobalReportButton;

    public void initialize() throws SQLException {
        DateFormatter.format(fromDatePicker);
        DateFormatter.format(toDatePicker);

        Validator.validateDatePickerOnFocusLoss(fromDatePicker);
        Validator.validateDatePickerOnFocusLoss(toDatePicker);

        Validator.validateDateRange(fromDatePicker, toDatePicker);

        List<UserHistory> data = DatabaseUtil.getUserHistory();
        dataGridPane.getChildren().clear();
        dataGridPane.setVgap(10);

        dataGridPane.add(new Label("Number"), 0, 0);
        dataGridPane.add(new Label("Serial Number"), 1, 0);
        dataGridPane.add(new Label("From"), 2, 0);
        dataGridPane.add(new Label("To"), 3, 0);
        dataGridPane.add(new Label("Quantity"), 4, 0);
        dataGridPane.add(new Label("Price"), 5, 0);
        dataGridPane.add(new Label("Total Income"), 6, 0);
        dataGridPane.add(new Label("Total Salary"), 7, 0);
        dataGridPane.add(new Label("Profit"), 8, 0);
        dataGridPane.add(new Label("Actions"), 9, 0);

        for (int row = 0; row < data.size(); row++) {
            UserHistory history = data.get(row);
            dataGridPane.add(new Label(String.valueOf(row + 1)), 0, row + 1);
            dataGridPane.add(new Label(history.getSerialNumber()), 1, row + 1);
            dataGridPane.add(new Label(String.valueOf(history.getFromDate())), 2, row + 1);
            dataGridPane.add(new Label(String.valueOf(history.getToDate())), 3, row+  1);
            dataGridPane.add(new Label(history.getQuantity()), 4, row + 1);
            dataGridPane.add(new Label(String.valueOf(history.getPrice())), 5, row + 1);
            dataGridPane.add(new Label(String.valueOf(history.getTotalSalary())), 6, row + 1);
            dataGridPane.add(new Label(String.valueOf(history.getTotalIncome())), 7, row + 1);
            dataGridPane.add(new Label(String.valueOf(history.getProfit())), 8, row + 1);

            Button deleteButton = new Button("DELETE");
            deleteButton.setOnAction(event -> {
                try {
                    handleDeleteButton(history.getId());
                    initialize();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            dataGridPane.add(deleteButton, 9, row + 1);
        }
    }

    private void handleDeleteButton(int id) throws SQLException {
        DatabaseUtil.deleteUserHistory(id);
    }

    public void generateGlobalReport(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/accounting/app/global-report.fxml"));
        Parent root = loader.load();
        GlobalReportController controller = loader.getController();

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        List<Control> fields = List.of(fromDatePicker, toDatePicker);

        if (Validator.validateFields(fields)) {
            List<UserHistory> filteredData = filterData(fromDate, toDate);

            controller.populateGrid(filteredData);

            double totalSalarySum = calculateTotalSalarySum(filteredData);
            double totalIncomeSum = calculateTotalIncomeSum(filteredData);
            double profitSum = calculateProfitSum(filteredData);
            controller.setSums(totalSalarySum, totalIncomeSum, profitSum);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        }
    }

    private double calculateProfitSum(List<UserHistory> data) {
        double profitSum = 0;
        for (UserHistory userHistory : data) {
            profitSum += userHistory.getProfit();
        }
        return profitSum;
    }

    private double calculateTotalIncomeSum(List<UserHistory> data) {
        double totalIncome = 0;
        for (UserHistory userHistory : data) {
            totalIncome += userHistory.getTotalIncome();
        }
        return totalIncome;
    }

    private double calculateTotalSalarySum(List<UserHistory> data) {
        double totalSalary = 0;
        for (UserHistory userHistory : data) {
            totalSalary += userHistory.getTotalSalary();
        }
        return totalSalary;
    }

    private List<UserHistory> filterData(LocalDate fromDate, LocalDate toDate) {
        List<UserHistory> filteredData = new ArrayList<>();

        for (UserHistory userHistory : DatabaseUtil.getUserHistory()){
            LocalDate userFromDate = userHistory.getFromDate();
            LocalDate userToDate = userHistory.getToDate();
            if ((userFromDate.isEqual(fromDate) || userFromDate.isAfter(fromDate) || userFromDate.isEqual(toDate)) &&
                    (userToDate.isEqual(toDate) || userToDate.isBefore(toDate) || userToDate.isEqual(fromDate))) {
                filteredData.add(userHistory);
            }
        }
        return filteredData;
    }
}
