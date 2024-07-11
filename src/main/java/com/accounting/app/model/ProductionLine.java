package com.accounting.app.model;

public class ProductionLine {
    private int id;
    private int createdBy;
    private String name;
    private int totalSalary;
    private int numberOfPeople;

    public ProductionLine(int id, int createdBy, String name, int totalSalary, int numberOfPeople) {
        this.id = id;
        this.createdBy = createdBy;
        this.name = name;
        this.totalSalary = totalSalary;
        this.numberOfPeople = numberOfPeople;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(int totalSalary) {
        this.totalSalary = totalSalary;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", " + "Total salary: " + totalSalary + ", Number of people: " + numberOfPeople;
    }
}
