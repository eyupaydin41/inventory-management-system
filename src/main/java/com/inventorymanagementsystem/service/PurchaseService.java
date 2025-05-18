package com.inventorymanagementsystem.service;

import com.inventorymanagementsystem.config.Database;
import com.inventorymanagementsystem.entity.Purchase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class PurchaseService {

    public String getTotalPurchaseAmount() {
        String totalAmount = "0.00";
        String sql = "SELECT SUM(total_amount) as total_purchase_amount FROM purchase";

        try (Connection connection = Database.getInstance().connectDB();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                String result = resultSet.getString("total_purchase_amount");
                if (result != null) {
                    totalAmount = result;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalAmount;
    }

    public String getTotalPurchasedItemCount() {
        String total = "0";
        String sql = "SELECT SUM(total_items) as total_purchase FROM PURCHASE";

        try (Connection connection = Database.getInstance().connectDB();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                total = resultSet.getString("total_purchase");
                if (total == null) {
                    total = "0";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    public ObservableList<Purchase> listPurchaseData() {
        ObservableList<Purchase> purchaseList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Purchase";

        try (Connection connection = Database.getInstance().connectDB();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Purchase purchase = new Purchase(
                        resultSet.getInt("id"),
                        resultSet.getString("invoice"),
                        resultSet.getString("shop_and_address"),
                        resultSet.getInt("total_items"),
                        resultSet.getDouble("total_amount"),
                        resultSet.getString("date_of_purchase")
                );
                purchaseList.add(purchase);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return purchaseList;
    }

    public boolean addPurchase(String invoice, String shopAndAddress, int totalItems, double pricePerItem, LocalDate purchaseDate) throws SQLException {
        String sql = "INSERT INTO PURCHASE(invoice, shop_and_address, total_items, total_amount, date_of_purchase) VALUES (?, ?, ?, ?, ?)";

        double totalAmount = totalItems * pricePerItem;

        try (Connection connection = Database.getInstance().connectDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, invoice);
            preparedStatement.setString(2, shopAndAddress);
            preparedStatement.setInt(3, totalItems);
            preparedStatement.setDouble(4, totalAmount);
            preparedStatement.setDate(5, java.sql.Date.valueOf(purchaseDate));

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean deletePurchaseById(int purchaseId) throws SQLException {
        String sql = "DELETE FROM PURCHASE WHERE id=?";
        try (Connection connection = Database.getInstance().connectDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, purchaseId);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        }
    }
}
