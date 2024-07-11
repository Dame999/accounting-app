package com.accounting.app.controller;

import com.accounting.app.database.DatabaseUtil;
import com.accounting.app.model.ProductionLine;
import com.accounting.app.utils.Validator;
import com.accounting.app.utils.DateFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class HomeController {
    @FXML
    public DatePicker fromDatePicker;
    @FXML
    public DatePicker toDatePicker;
    @FXML
    public TextField productSerialNumber;
    @FXML
    public TextField quantity;
    @FXML
    public Button generateReportButton;
    @FXML
    public ListView<ProductionLine> selectedProductionLinesListView;
    @FXML
    public ListView<ProductionLine> fetchedProductionLinesListView;
    @FXML
    public TextField priceForEachProduct;
    @FXML
    public ScrollPane employeesScrollPane;
    @FXML
    public TextField totalSalaryForReport;

    public void initialize() throws SQLException {
        initializeProductionLines();
        totalSalaryForReport.setText("0");

        fetchedProductionLinesListView.setOnMouseClicked(event -> handleProductionLineSelection(fetchedProductionLinesListView, selectedProductionLinesListView));
        selectedProductionLinesListView.setOnMouseClicked(event -> handleProductionLineSelection(selectedProductionLinesListView, fetchedProductionLinesListView));

        DateFormatter.format(fromDatePicker);
        DateFormatter.format(toDatePicker);

        Validator.validateDatePickerOnFocusLoss(fromDatePicker);
        Validator.validateDatePickerOnFocusLoss(toDatePicker);

        Validator.validateDateRange(fromDatePicker, toDatePicker);
        Validator.validateTextFieldOnFocusLoss(quantity, "\\d*", "Quantity must be a number");
        Validator.validateRealNumberField(priceForEachProduct, "The price must be a number");
    }

    private void handleProductionLineSelection(ListView<ProductionLine> sourceListView, ListView<ProductionLine> targetListView) {
        ProductionLine selectedProductionLine = sourceListView.getSelectionModel().getSelectedItem();

        if (selectedProductionLine != null) {
            sourceListView.getItems().remove(selectedProductionLine);
            targetListView.getItems().add(selectedProductionLine);

            int currentTotalSalary = Integer.parseInt(totalSalaryForReport.getText());
            if (sourceListView == fetchedProductionLinesListView) {
                currentTotalSalary += selectedProductionLine.getTotalSalary();
            } else {
                currentTotalSalary -= selectedProductionLine.getTotalSalary();
            }

            totalSalaryForReport.setText(String.valueOf(currentTotalSalary));
        }
    }

    private void initializeProductionLines() throws SQLException {
        ObservableList<ProductionLine> selectedProductionLines = FXCollections.observableArrayList();
        ObservableList<ProductionLine> fetchedProductionLines = FXCollections.observableArrayList();

        selectedProductionLinesListView.setItems(selectedProductionLines);
        fetchedProductionLinesListView.setItems(fetchedProductionLines);

        List<ProductionLine> fetchedProductionLinesList = DatabaseUtil.getAllProductionLines();
        fetchedProductionLines.addAll(fetchedProductionLinesList);
    }

    public void generateReport(ActionEvent actionEvent) {
        System.out.println("Generating report");
        List<Control> fields = List.of(fromDatePicker, toDatePicker, productSerialNumber, quantity, priceForEachProduct, selectedProductionLinesListView);
        if (Validator.validateFields(fields)) {
            DatabaseUtil.generateReport(fromDatePicker.getValue(), toDatePicker.getValue(),
                    productSerialNumber.getText(), quantity.getText(), priceForEachProduct.getText(),
                    selectedProductionLinesListView, totalSalaryForReport.getText());
        }
    }
}
