package com.accounting.app.model;

import java.time.LocalDate;
import java.util.Date;

public class UserHistory {
    private int id;
    private String serialNumber;
    private String quantity;
    private double price;
    private double totalSalary;
    private double totalIncome;
    private double profit;
    private LocalDate fromDate;
    private LocalDate toDate;

    public UserHistory(int id, String serialNumber, String quantity, double price, double totalSalary, double totalIncome, double profit, LocalDate fromDate, LocalDate toDate) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.quantity = quantity;
        this.price = price;
        this.totalSalary = totalSalary;
        this.totalIncome = totalIncome;
        this.profit = profit;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(double totalSalary) {
        this.totalSalary = totalSalary;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
}
