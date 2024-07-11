package com.accounting.app.controller;

import com.accounting.app.model.UserHistory;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.List;

public class GlobalReportController {


    public GridPane globalDataGridPane;
    public TextField totalSalary;
    public TextField totalIncome;
    public TextField totalProfit;

    public void populateGrid(List<UserHistory> filteredData) {
        globalDataGridPane.getChildren().clear();
        globalDataGridPane.setVgap(10);

        globalDataGridPane.add(new Label("Number"), 0, 0);
        globalDataGridPane.add(new Label("Serial Number"), 1, 0);
        globalDataGridPane.add(new Label("From"), 2, 0);
        globalDataGridPane.add(new Label("To"), 3, 0);
        globalDataGridPane.add(new Label("Quantity"), 4, 0);
        globalDataGridPane.add(new Label("Price"), 5, 0);
        globalDataGridPane.add(new Label("Total Income"), 6, 0);
        globalDataGridPane.add(new Label("Total Salary"), 7, 0);
        globalDataGridPane.add(new Label("Profit"), 8, 0);

        for (int row = 0; row < filteredData.size(); row++) {
            UserHistory history = filteredData.get(row);
            globalDataGridPane.add(new Label(String.valueOf(row + 1)), 0, row + 1);
            globalDataGridPane.add(new Label(history.getSerialNumber()), 1, row + 1);
            globalDataGridPane.add(new Label(String.valueOf(history.getFromDate())), 2, row + 1);
            globalDataGridPane.add(new Label(String.valueOf(history.getToDate())), 3, row+  1);
            globalDataGridPane.add(new Label(history.getQuantity()), 4, row + 1);
            globalDataGridPane.add(new Label(String.valueOf(history.getPrice())), 5, row + 1);
            globalDataGridPane.add(new Label(String.valueOf(history.getTotalSalary())), 6, row + 1);
            globalDataGridPane.add(new Label(String.valueOf(history.getTotalIncome())), 7, row + 1);
            globalDataGridPane.add(new Label(String.valueOf(history.getProfit())), 8, row + 1);
        }
    }

    public void setSums(double totalSalarySum, double totalIncomeSum, double profitSum) {
        totalSalary.setText(String.valueOf(totalSalarySum));
        totalIncome.setText(String.valueOf(totalIncomeSum));
        totalProfit.setText(String.valueOf(profitSum));
    }
}
