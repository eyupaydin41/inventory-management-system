package com.inventorymanagementsystem.service;

import com.inventorymanagementsystem.config.Database;
import com.inventorymanagementsystem.entity.Sales;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class SalesService {

    public String getTotalSalesAmount() {
        String totalSaleAmount = "0.00"; // Varsayılan değer

        String sql = "SELECT SUM(total_amount) as total_sale_amount FROM sales";
        try (Connection connection = Database.getInstance().connectDB()) {
            if (connection == null) {
                throw new Exception("Veritabanı bağlantısı sağlanamadı.");
            }

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    String result = resultSet.getString("total_sale_amount");
                    totalSaleAmount = (result == null) ? "0.00" : result;
                }
            }

        } catch (Exception err) {
            // Hata mesajını UI'da göstermek için exception'ı controller'a iletmek
            throw new RuntimeException("Error fetching total sales amount: " + err.getMessage());
        }

        return totalSaleAmount;
    }

    public String getTotalSoldQuantity() {
        String total = "0";
        String sql = "SELECT SUM(quantity) as total_sale FROM sales";

        try (Connection connection = Database.getInstance().connectDB();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                total = resultSet.getString("total_sale");
                if (total == null) {
                    total = "0";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    public ObservableList<Sales> listSalesData() {
        ObservableList<Sales> salesList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM SALES s INNER JOIN CUSTOMERS c ON s.cust_id = c.id";

        try (Connection connection = Database.getInstance().connectDB()) {
            if (connection == null) {
                throw new Exception("Veritabanı bağlantısı sağlanamadı.");
            }

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                while (resultSet.next()) {
                    Sales sale = new Sales(
                            Integer.parseInt(resultSet.getString("id")),
                            resultSet.getString("inv_num"),
                            Integer.parseInt(resultSet.getString("cust_id")),
                            resultSet.getString("name"),
                            Double.parseDouble(resultSet.getString("price")),
                            Integer.parseInt(resultSet.getString("quantity")),
                            Double.parseDouble(resultSet.getString("total_amount")),
                            resultSet.getString("date"),
                            resultSet.getString("item_number")
                    );
                    salesList.add(sale);
                }
            }

        } catch (Exception err) {
            throw new RuntimeException("Sales data retrieval failed: " + err.getMessage());
        }

        return salesList;
    }

    public String generateNextInvoiceNumber() {
        String nextInvoice = "INV-1";
        String sql = "SELECT MAX(inv_num) AS inv_num FROM sales";

        try (Connection connection = Database.getInstance().connectDB();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                String result = resultSet.getString("inv_num");
                if (result != null) {
                    int invId = Integer.parseInt(result.substring(4));
                    invId++;
                    nextInvoice = "INV-" + invId;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return nextInvoice;
    }

    public String getLatestInvoiceNumber() {
        String sql = "SELECT MAX(inv_num) AS inv_num FROM sales";
        try (Connection connection = Database.getInstance().connectDB();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                return resultSet.getString("inv_num");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean saveInvoiceDetails(String phoneNumber, String invoiceNumber, LocalDate date) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int count = 0;

        try {
            connection = Database.getInstance().connectDB();

            // Get customer ID
            String sql = "SELECT id FROM CUSTOMERS WHERE phone_number=?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, phoneNumber);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String custId = resultSet.getString("id");

                // Get billing details
                String getBillingDetails = "SELECT * FROM BILLING";
                preparedStatement = connection.prepareStatement(getBillingDetails);
                resultSet = preparedStatement.executeQuery();

                // Save invoice details in SALES table
                while (resultSet.next()) {
                    String salesDetailsSQL = "INSERT INTO sales(inv_num, item_number, cust_id, price, quantity, total_amount, date) VALUES(?,?,?,?,?,?,?)";
                    preparedStatement = connection.prepareStatement(salesDetailsSQL);
                    preparedStatement.setString(1, invoiceNumber);
                    preparedStatement.setString(2, resultSet.getString("item_number"));
                    preparedStatement.setInt(3, Integer.parseInt(custId));
                    preparedStatement.setDouble(4, resultSet.getDouble("price"));
                    preparedStatement.setInt(5, resultSet.getInt("quantity"));
                    preparedStatement.setDouble(6, resultSet.getDouble("total_amount"));
                    preparedStatement.setDate(7, java.sql.Date.valueOf(date));
                    preparedStatement.executeUpdate();
                    count++;
                }
                return count > 0;
            } else {
                showAlert(Alert.AlertType.ERROR, "Error Message", "Kindly fill Customer Details correctly.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Double getTotalSalesThisMonth() {
        LocalDate now = LocalDate.now();
        String currentYearMonth = now.getYear() + "-" + String.format("%02d", now.getMonthValue());

        String sql = "SELECT SUM(total_amount) as total_sales_this_month FROM SALES WHERE TO_CHAR(date, 'YYYY-MM')=?";
        try (Connection connection = Database.getInstance().connectDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, currentYearMonth);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("total_sales_this_month");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getTotalItemsSoldThisMonth() {
        LocalDate now = LocalDate.now();
        String currentYearMonth = now.getYear() + "-" + String.format("%02d", now.getMonthValue());

        String sql = "SELECT SUM(quantity) as total_items_sold_this_month FROM SALES WHERE TO_CHAR(date, 'YYYY-MM')=?";
        try (Connection connection = Database.getInstance().connectDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, currentYearMonth);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("total_items_sold_this_month");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
