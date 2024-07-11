package com.accounting.app.controller;

import com.accounting.app.model.ProductionLine;
import com.accounting.app.database.DatabaseUtil;
import com.accounting.app.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class ProductionLineController {
    @FXML
    public ScrollPane employeesScrollPane;
    @FXML
    public ListView<ProductionLine> fetchedProductionLineListView;
    @FXML
    public TextField numberOfPeopleField;
    @FXML
    public Label totalSalaryProductionLineFieldLabel;
    @FXML
    public Label numberOfPeopleFieldLabel;
    @FXML
    public Label totalSalaryForUnknownEmployeesLabel;
    @FXML
    public TextField totalSalaryForUnknownEmployees;
    @FXML
    public VBox optionalFieldsContainer;
    @FXML
    public VBox addNewProductionLineView;
    @FXML
    private TextField totalSalaryProductionLineField;
    @FXML
    private TextField productionLineNameLabel;
    @FXML
    private ListView<Employee> selectedEmployeesListView;
    @FXML
    private ListView<Employee> fetchedEmployeesListView;
    @FXML
    private CheckBox insertTotalSumCheckBox;

    private ObservableList<Employee> selectedEmployees;
    private ObservableList<Employee> fetchedEmployees;

    public void initialize() throws SQLException {
        optionalFieldsContainer.setVisible(false);
        initializeProductionLines();
        initializeEmployees();

        insertTotalSumCheckBox.setOnAction(event -> handleCheckBoxAction());
        totalSalaryProductionLineField.setText("0");

        fetchedEmployeesListView.setOnMouseClicked(event -> {
            handleEmployeeSelection(fetchedEmployeesListView, selectedEmployeesListView);
        });

        selectedEmployeesListView.setOnMouseClicked(event -> {
            handleEmployeeSelection(selectedEmployeesListView, fetchedEmployeesListView);
        });

        fetchedProductionLineListView.setOnMouseClicked(event -> {
            try {
                handleProductionLineSelection(fetchedProductionLineListView);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleProductionLineSelection(ListView<ProductionLine> fetchedProductionLinesListView) throws SQLException {
        ProductionLine selectedProductionLine = fetchedProductionLinesListView.getSelectionModel().getSelectedItem();

        if (selectedProductionLine != null) {
            productionLineNameLabel.setText(selectedProductionLine.getName());
            numberOfPeopleField.setText(String.valueOf(selectedProductionLine.getNumberOfPeople()));
            totalSalaryProductionLineField.setText(String.valueOf(selectedProductionLine.getTotalSalary()));
            totalSalaryForUnknownEmployees.setText(String.valueOf(selectedProductionLine.getTotalSalary()));
            initializeEmployees();
            initializeSelectedEmployeesFromAProductionLine(selectedProductionLine.getId());
        }
    }

    public void newProductionLineButtonAction(ActionEvent actionEvent) throws SQLException {
        ProductionLine selectedProductionLine = fetchedProductionLineListView.getSelectionModel().getSelectedItem();
        if (selectedProductionLine != null) {
            fetchedProductionLineListView.getSelectionModel().clearSelection();
        }
        productionLineNameLabel.setText("");
        numberOfPeopleField.setText("");
        totalSalaryProductionLineField.setText("");
        totalSalaryForUnknownEmployees.setText("");
        initializeEmployees();
    }

    private void initializeSelectedEmployeesFromAProductionLine(int id) throws SQLException {
        List<Employee> employees = DatabaseUtil.getAllEmployeesForSpecificProductionLine(id);
        selectedEmployees.addAll(employees);
    }

    private void initializeEmployees() throws SQLException {
        selectedEmployees = FXCollections.observableArrayList();
        fetchedEmployees = FXCollections.observableArrayList();

        selectedEmployeesListView.setItems(selectedEmployees);
        fetchedEmployeesListView.setItems(fetchedEmployees);

        List<Employee> fetchedEmployeesList = DatabaseUtil.getAllEmployees();
        fetchedEmployees.addAll(fetchedEmployeesList);
    }

    private void initializeProductionLines() throws SQLException {
        ObservableList<ProductionLine> fetchedProductionLines = FXCollections.observableArrayList();
        fetchedProductionLineListView.setItems(fetchedProductionLines);
        List<ProductionLine> fetchedProductionLinesList = DatabaseUtil.getAllProductionLines();
        fetchedProductionLines.addAll(fetchedProductionLinesList);
    }

    private void handleEmployeeSelection(ListView<Employee> sourceListView, ListView<Employee> targetListView) {
        Employee selectedEmployee = sourceListView.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null) {
            sourceListView.getItems().remove(selectedEmployee);
            targetListView.getItems().add(selectedEmployee);

            int currentTotalSalary = Integer.parseInt(totalSalaryProductionLineField.getText());
            if (sourceListView == fetchedEmployeesListView) {
                currentTotalSalary += selectedEmployee.getSalary();
            } else {
                currentTotalSalary -= selectedEmployee.getSalary();
            }

            totalSalaryProductionLineField.setText(String.valueOf(currentTotalSalary));
        }
    }

    private void handleCheckBoxAction() {
        boolean checkBoxSelected = insertTotalSumCheckBox.isSelected();

        optionalFieldsContainer.setVisible(checkBoxSelected);

        employeesScrollPane.setVisible(!checkBoxSelected);
        totalSalaryProductionLineField.setVisible(!checkBoxSelected);
        totalSalaryProductionLineFieldLabel.setVisible(!checkBoxSelected);
    }

    public void createChannelAndSave(ActionEvent actionEvent) throws SQLException {
        boolean checkBoxSelected = insertTotalSumCheckBox.isSelected();
        String productionLineNameText = productionLineNameLabel.getText();
        int numberOfEmployees = checkBoxSelected ? Integer.parseInt(numberOfPeopleField.getText()) : selectedEmployees.size();
        String totalSalary = checkBoxSelected ? totalSalaryForUnknownEmployees.getText() : totalSalaryProductionLineField.getText();
        ObservableList<Employee> employees = checkBoxSelected ? null : selectedEmployees;

        ProductionLine selectedProductionLine = fetchedProductionLineListView.getSelectionModel().getSelectedItem();
        if (selectedProductionLine != null) {
            selectedProductionLine.setName(productionLineNameText);
            selectedProductionLine.setTotalSalary(Integer.parseInt(checkBoxSelected ? totalSalaryForUnknownEmployees.getText() : totalSalaryProductionLineField.getText()));
            selectedProductionLine.setNumberOfPeople(numberOfEmployees);

            DatabaseUtil.updateExistingProductionLine(selectedProductionLine, employees);
        } else {
            DatabaseUtil.createProductionLine(productionLineNameText, totalSalary, numberOfEmployees, employees);
        }
        initializeProductionLines();
    }

    public void deleteProductionLineAction(ActionEvent actionEvent) throws SQLException {
        ProductionLine selectedProductionLine = fetchedProductionLineListView.getSelectionModel().getSelectedItem();
        if (selectedProductionLine != null) {
            DatabaseUtil.deleteProductionLine(selectedProductionLine);
            initializeProductionLines();
        }
    }
}
