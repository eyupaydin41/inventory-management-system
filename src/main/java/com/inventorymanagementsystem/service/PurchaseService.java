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
                Purchase purchase = new Purchase.Builder()
                        .id(resultSet.getInt("id"))
                        .invoice(resultSet.getString("invoice"))
                        .shopDetails(resultSet.getString("shop_and_address"))
                        .totalItems(resultSet.getInt("total_items"))
                        .totalAmount(resultSet.getDouble("total_amount"))
                        .dateOfPurchase(resultSet.getString("date_of_purchase"))
                        .build();
                purchaseList.add(purchase);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return purchaseList;
    }

    public boolean addPurchase(String invoice, String shopAndAddress, int totalItems, double pricePerItem, LocalDate purchaseDate) throws SQLException {

        String purchaseSql = "INSERT INTO PURCHASE(invoice, shop_and_address, total_items, total_amount, date_of_purchase) VALUES (?, ?, ?, ?, ?)";
        String selectProductSql = "SELECT quantity FROM PRODUCTS WHERE item_number = ?";
        String updateProductSql = "UPDATE PRODUCTS SET quantity = quantity + ? WHERE item_number = ?";
        String insertProductSql = "INSERT INTO PRODUCTS(item_number, item_group, quantity, price) VALUES (?, ?, ?, ?)";

        double totalAmount = totalItems * pricePerItem;

        try (Connection connection = Database.getInstance().connectDB()) {
            connection.setAutoCommit(false);

            try (PreparedStatement purchaseStmt = connection.prepareStatement(purchaseSql)) {
                purchaseStmt.setString(1, invoice);
                purchaseStmt.setString(2, shopAndAddress);
                purchaseStmt.setInt(3, totalItems);
                purchaseStmt.setDouble(4, totalAmount);
                purchaseStmt.setDate(5, java.sql.Date.valueOf(purchaseDate));
                purchaseStmt.executeUpdate();
            }

            try (PreparedStatement selectStmt = connection.prepareStatement(selectProductSql)) {
                selectStmt.setString(1, invoice);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateProductSql)) {
                        updateStmt.setInt(1, totalItems);
                        updateStmt.setString(2, invoice);
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertProductSql)) {
                        insertStmt.setString(1, invoice);
                        insertStmt.setString(2, shopAndAddress);
                        insertStmt.setInt(3, totalItems);
                        insertStmt.setDouble(4, 0.0);
                        insertStmt.executeUpdate();
                    }
                }
            }
            connection.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
