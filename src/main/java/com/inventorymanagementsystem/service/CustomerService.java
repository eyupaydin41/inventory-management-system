package com.inventorymanagementsystem.service;

import com.inventorymanagementsystem.config.Database;
import com.inventorymanagementsystem.entity.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;

public class CustomerService {
    public ObservableList<Customer> listCustomerData() {
        ObservableList<Customer> customersList = FXCollections.observableArrayList();
        try (Connection connection = Database.getInstance().connectDB();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Customers")) {

            while (resultSet.next()) {
                Customer customer = new Customer.Builder()
                        .id(resultSet.getInt("id"))
                        .name(resultSet.getString("name"))
                        .phoneNumber(resultSet.getString("phone_number"))
                        .build();

                customersList.add(customer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return customersList;
    }

    public boolean saveCustomerDetails(String name, String phoneNumber) {
        if (name.isBlank() || phoneNumber.isBlank()) {
            showAlert(Alert.AlertType.INFORMATION, "Message", "Kindly Fill Customer Name and Phone number.");
            return false;
        }

        try (Connection connection = Database.getInstance().connectDB()) {
            String checkSql = "SELECT * FROM CUSTOMERS WHERE phone_number=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(checkSql)) {
                preparedStatement.setString(1, phoneNumber);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    showAlert(Alert.AlertType.INFORMATION, "Message", "Customer Data is already present in customer table. Proceeding further to save invoice.");
                    return true;
                }
            }

            String insertSql = "INSERT INTO CUSTOMERS(name, phone_number) VALUES(?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, phoneNumber);
                int result = preparedStatement.executeUpdate();
                if (result > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Message", "Customer Data saved successfully.");
                    return true;
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Message", "Customer Data not saved. Please fill name and phone number correctly.");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isCustomerPhoneAvailable(String phoneNumber) {
        String sql = "SELECT * FROM CUSTOMERS WHERE phone_number=?";
        try (Connection connection = Database.getInstance().connectDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, phoneNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            return !resultSet.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addCustomer(String name, String phone) {
        String sql = "INSERT INTO CUSTOMERS(name, phone_number) VALUES(?, ?)";
        try (Connection connection = Database.getInstance().connectDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phone);
            int result = preparedStatement.executeUpdate();
            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCustomer(String name, String phone) {
        String sql = "UPDATE CUSTOMERS SET name=? WHERE phone_number=?";
        String sql2 = "UPDATE CUSTOMERS SET phone_number=? WHERE name=?";

        try (Connection connection = Database.getInstance().connectDB()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, phone);
                int result = preparedStatement.executeUpdate();
                if (result > 0) {
                    return true;
                }
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql2)) {
                preparedStatement.setString(1, phone);
                preparedStatement.setString(2, name);
                int result = preparedStatement.executeUpdate();
                return result > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCustomerFromSales(int customerId) {
        String sql = "DELETE FROM sales WHERE cust_id=?";
        try (Connection connection = Database.getInstance().connectDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, customerId);
            int result = preparedStatement.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCustomer(int customerId, String phoneNumber) {
        String sql = "DELETE FROM CUSTOMERS WHERE phone_number=?";
        try (Connection connection = Database.getInstance().connectDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, phoneNumber);
            int result = preparedStatement.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
