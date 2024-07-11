package com.accounting.app.controller;

import com.accounting.app.database.DatabaseUtil;
import com.accounting.app.model.Employee;
import com.accounting.app.utils.Validator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.List;

public class EmployeeController {
    @FXML
    private TextField employeeNameField;
    @FXML
    private TextField employeeSalaryField;
    @FXML
    private GridPane employeeGrid;

    @FXML
    public void initialize() throws SQLException {
        updateEmployees();
        Validator.validateRealNumberField(employeeSalaryField, "The salary must be a number");
    }

    @FXML
    public void saveEmployee(ActionEvent actionEvent) {
        String name = employeeNameField.getText();
        String salaryFieldText = employeeSalaryField.getText();

        List<Control> fields = List.of(employeeNameField, employeeSalaryField);
        if (Validator.validateFields(fields)) {
            try {
                double salary = Double.parseDouble(salaryFieldText);
                DatabaseUtil.saveEmployee(name, salary);

                employeeNameField.clear();
                employeeSalaryField.clear();

                updateEmployees();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateEmployees() throws SQLException {
        employeeGrid.getChildren().clear();
        employeeGrid.setVgap(10);
        List<Employee> employees = DatabaseUtil.getAllEmployees();

        employeeGrid.addRow(0, new Label("Number"), new Label("Employee Name"), new Label("Salary"), new Label("Actions"));

        int rowIndex = 1;
        for (Employee employee : employees) {
            Label nameLabel = new Label(employee.getName());
            Label salaryLabel = new Label(String.valueOf(employee.getSalary()));
            Label number = new Label(String.valueOf(rowIndex));
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(event -> {
                try {
                    DatabaseUtil.deleteEmployee(employee.getId());
                    updateEmployees();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            employeeGrid.addRow(rowIndex++, number, nameLabel, salaryLabel, deleteButton);
        }
    }
}
