package com.accounting.app.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Validator {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.show();
    }

    public static void validateDatePickerOnFocusLoss(DatePicker datePicker) {
        datePicker.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Focus lost
                String text = datePicker.getEditor().getText().trim();
                if (!text.isEmpty() && !text.matches("\\d{2}/\\d{2}/\\d{4}")) {
                    showAlert("Invalid date format. Please use dd/MM/yyyy.");
                    datePicker.getEditor().setText("");
                } else {
                    try {
                        if (!text.isEmpty()) {
                            LocalDate.parse(text, dateFormatter);
                        }
                    } catch (DateTimeParseException e) {
                        showAlert("Invalid date. Please enter a valid date in the format dd/MM/yyyy.");
                        datePicker.getEditor().setText("");
                    }
                }
            }
        });
    }

    public static void validateDateRange(DatePicker fromDatePicker, DatePicker toDatePicker) {
        fromDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && toDatePicker.getValue() != null && newValue.isAfter(toDatePicker.getValue())) {
                showAlert("From date cannot be after To date");
                fromDatePicker.setValue(oldValue);
            }
        });

        toDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && fromDatePicker.getValue() != null && newValue.isBefore(fromDatePicker.getValue())) {
                showAlert("To date cannot be before From date");
                toDatePicker.setValue(oldValue);
            }
        });
    }

    public static void validateTextFieldOnFocusLoss(TextField textField, String regex, String errorMessage) {
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Focus lost
                String text = textField.getText().trim();
                if (!text.matches(regex) && !text.isEmpty()) {
                    showAlert(errorMessage);
                    textField.setText("");
                }
            }
        });
    }

    public static void validateRealNumberField(TextField textField, String errorMessage) {
        validateTextFieldOnFocusLoss(textField, "\\d+(\\.\\d+)?", errorMessage);
    }

    public static boolean validateFields(List<Control> fields) {
        for (Control field : fields) {
            if (field instanceof javafx.scene.control.TextInputControl) {
                if (((javafx.scene.control.TextInputControl) field).getText().isEmpty()) {
                    showAlert("Please populate all fields in order to generate a report");
                    return false;
                }
            } else if (field instanceof javafx.scene.control.DatePicker) {
                if (((javafx.scene.control.DatePicker) field).getValue() == null) {
                    showAlert("Please populate all fields in order to generate a report");
                    return false;
                }
            } else if (field instanceof javafx.scene.control.ListView) {
                if (((javafx.scene.control.ListView) field).getItems().isEmpty()) {
                    showAlert("Please populate all fields in order to generate a report");
                    return false;
                }
            }
        }
        return true;
    }
}
