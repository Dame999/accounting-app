package com.accounting.app.database;

import com.accounting.app.manager.SessionManager;
import com.accounting.app.model.ProductionLine;
import com.accounting.app.model.UserHistory;
import com.accounting.app.model.Employee;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DatabaseUtil {
    private static final Logger logger = Logger.getLogger(DatabaseUtil.class.getName());
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:nefi-database.db");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void initializeDatabase() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String usersSql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL)";
            statement.executeUpdate(usersSql);
            logger.info("Users table created successfully.");

            String productionLinesSql = "CREATE TABLE IF NOT EXISTS production_line " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "created_by INTEGER, " +
                    "name TEXT, " +
                    "total_salary INTEGER, " +
                    "number_of_people INTEGER, " +
                    "FOREIGN KEY (created_by) REFERENCES users(id))";
            statement.executeUpdate(productionLinesSql);
            logger.info("ProductionLine table created successfully.");

            String employeeSql = "CREATE TABLE IF NOT EXISTS employees " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "salary REAL," +
                    "created_by INTEGER, " +
                    "production_line_id INTEGER, " +
                    "FOREIGN KEY (created_by) REFERENCES users(id))";
            statement.executeUpdate(employeeSql);
            logger.info("Employees table created successfully");

            String userHistorySql = "CREATE TABLE IF NOT EXISTS user_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "created_by INTEGER, order_id TEXT, " +
                    "quantity INTEGER, " +
                    "price REAL, " +
                    "total_salary REAL, " +
                    "total_income REAL, " +
                    "profit REAL, " +
                    "from_date TEXT, " +
                    "to_date TEXT, " +
                    "FOREIGN KEY (created_by) REFERENCES users(id))";
            statement.executeUpdate(userHistorySql);
            logger.info("User history table created successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void saveEmployee(String name, double salary) throws SQLException {
        int userId = SessionManager.getUserId();
        logger.info("user id is " + userId);
        String sql = "INSERT INTO employees (name, salary, created_by) VALUES (?, ?, ?)";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            System.out.println("Starting the creation of the Employee: " + name);
            System.out.println(preparedStatement.getMetaData());
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, String.valueOf(salary));
            preparedStatement.setString(3, String.valueOf(userId));
            preparedStatement.executeUpdate();
            logger.info("Employee saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<ProductionLine> getAllProductionLines() throws SQLException {
        logger.info("Fetching all employees");
        int userId = SessionManager.getUserId();

        List<ProductionLine> productionLines = new ArrayList<>();

        String sql = "SELECT * FROM production_line WHERE created_by=?";
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(userId));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int createdBy = resultSet.getInt("created_by");
                String name = resultSet.getString("name");
                int totalSalary = resultSet.getInt("total_salary");
                int numberOfPeople = resultSet.getInt("number_of_people");

                productionLines.add(new ProductionLine(id, createdBy, name, totalSalary, numberOfPeople));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }

        logger.info("Fetched ProductionLines successfully");
        return productionLines;
    }

    public static List<Employee> getAllEmployeesForSpecificProductionLine(int productionLineId) throws SQLException {
        logger.info("Fetching all employees for");
        int userId = SessionManager.getUserId();
        List<Employee> employees = new ArrayList<>();

        String sql = "SELECT * FROM employees WHERE created_by=? AND production_line_id=?";
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(userId));
            preparedStatement.setString(2, String.valueOf(productionLineId));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double salary = resultSet.getDouble("salary");
                employees.add(new Employee(id, name, salary));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        logger.info("Employees fetched successfully.");
        return employees;
    }

    public static List<Employee> getAllEmployees() throws SQLException {
        logger.info("Fetching all employees");

        int userId = SessionManager.getUserId();
        List<Employee> employees = new ArrayList<>();

        String sql = "SELECT * FROM employees WHERE created_by=?";
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(userId));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double salary = resultSet.getDouble("salary");
                employees.add(new Employee(id, name, salary));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        System.out.println("Employees fetched successfully.");
        return employees;
    }

    public static void updateExistingProductionLine(ProductionLine productionLine, ObservableList<Employee> employees) {
        logger.info("Updating production line with name " + productionLine.getName());

        String sql = "UPDATE production_line SET name=?, total_salary=?, number_of_people=? WHERE id=?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, productionLine.getName());
            preparedStatement.setString(2, String.valueOf(productionLine.getTotalSalary()));
            preparedStatement.setString(3, String.valueOf(employees.size() > 0 ? employees.size() : productionLine.getNumberOfPeople()));
            preparedStatement.setString(4, String.valueOf(productionLine.getId()));
            preparedStatement.executeUpdate();

            List<Employee> employeesInTheCurrentProductionLine = getAllEmployeesForSpecificProductionLine(productionLine.getId());
            if (employeesInTheCurrentProductionLine.size() > 0) {
                for (Employee employee : employeesInTheCurrentProductionLine) {
                    updateEmployeeProductionLine(0, employee, connection);
                }
            }

            if (employees.size() > 0) {
                setEmployeesProductionLine(employees, productionLine.getId(), connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createProductionLine(String productionLineName, String totalSalary, int numberOfEmployees, ObservableList<Employee> employees) throws SQLException {
        logger.info("Saving production line with a name: " + productionLineName + " and total salary of: " + totalSalary);
        int userId = SessionManager.getUserId();

        String sql = "INSERT INTO production_line (created_by, name, total_salary, number_of_people) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(userId));
            preparedStatement.setString(2, productionLineName);
            preparedStatement.setString(3, totalSalary);
            preparedStatement.setString(4, String.valueOf(numberOfEmployees));
            preparedStatement.executeUpdate();
            System.out.println("ProductionLine " + productionLineName + " saved successfully.");

            int productionLineId = 0;
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    productionLineId = generatedKeys.getInt(1);
                    System.out.println("Production Line " + productionLineName + " saved successfully with ID: " + productionLineId);
                } else {
                    throw new SQLException("Failed to retrieve the generated ProductionLine ID");
                }
            }
            if (employees.size() > 0 && productionLineId != 0) {
                setEmployeesProductionLine(employees, productionLineId, connection);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void setEmployeesProductionLine(ObservableList<Employee> employees, int productionLineId, Connection connection) throws SQLException {
        for (Employee employee : employees) {
            updateEmployeeProductionLine(productionLineId, employee, connection);
        }
    }

    public static void updateEmployeeProductionLine(int productionLineId, Employee employee, Connection connection) throws SQLException {
        if (connection == null) {
            connection = getConnection();
        }
        String sql = "UPDATE employees SET production_line_id=? WHERE id=?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(productionLineId));
            preparedStatement.setString(2, String.valueOf(employee.getId()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void generateReport(LocalDate fromDate, LocalDate toDate, String serialNumber,
                                      String quantity, String pricePerProduct,
                                      ListView<ProductionLine> selectedProductionLinesListView, String totalSalary) {
        int userId = SessionManager.getUserId();
        double totalIncome = Double.parseDouble(pricePerProduct) * Integer.parseInt(quantity);
        String sql = "INSERT INTO user_history (created_by, order_id, quantity, price, total_salary, total_income, profit, from_date, to_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, String.valueOf(userId));
            statement.setString(2, serialNumber);
            statement.setString(3, quantity);
            statement.setString(4, pricePerProduct);
            statement.setString(5, totalSalary);
            statement.setString(6, String.valueOf(totalIncome));
            statement.setString(7, String.valueOf(totalIncome - Double.parseDouble(totalSalary)));
            statement.setString(8, String.valueOf(fromDate));
            statement.setString(9, String.valueOf(toDate));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<UserHistory> getUserHistory() {
        int userId = SessionManager.getUserId();
        String sql = "SELECT * FROM user_history WHERE created_by=?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, String.valueOf(userId));
            ResultSet resultSet = statement.executeQuery();

            List<UserHistory> userHistories = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String serialNumber = resultSet.getString("order_id");
                String quantity = resultSet.getString("quantity");
                double price = resultSet.getDouble("price");
                double totalSalary = resultSet.getDouble("total_salary");
                double totalIncome = resultSet.getDouble("total_income");
                double profit = resultSet.getDouble("profit");
                String fromDateString = resultSet.getString("from_date");
                String toDateString = resultSet.getString("to_date");
                LocalDate fromDate = LocalDate.parse(fromDateString);
                LocalDate toDate = LocalDate.parse(toDateString);
                userHistories.add(new UserHistory(id, serialNumber, quantity, price, totalSalary, totalIncome, profit, fromDate, toDate));
            }
            return userHistories;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteUserHistory(int id) {
        logger.info("Deleting user history");
        String sql = "DELETE FROM user_history WHERE id=?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, String.valueOf(id));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteEmployee(int id) {
        logger.info("Deleting employee with id " + id);
        String sql = "DELETE FROM employees WHERE id=?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, String.valueOf(id));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        logger.info("Successfully deleted employee with id " + id);
    }

    public static void deleteProductionLine(ProductionLine selectedProductionLine) {
        logger.info("Deleting ProductionLine with name " + selectedProductionLine.getName());
        String sql = "DELETE FROM production_line WHERE id=?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, String.valueOf(selectedProductionLine.getId()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        logger.info("Successfully deleted ProductionLine with name " + selectedProductionLine.getName() + " and id " + selectedProductionLine.getId());
    }
}
