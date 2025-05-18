package com.inventorymanagementsystem.service;

import com.inventorymanagementsystem.config.Database;
import com.inventorymanagementsystem.entity.Billing;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class BillingService {
    public ObservableList<Billing> getAllBillingData() {
        ObservableList<Billing> billingList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM BILLING";

        try (Connection connection = Database.getInstance().connectDB();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Billing billingData = new Billing(
                        resultSet.getString("item_number"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("price"),
                        resultSet.getDouble("total_amount")
                );
                billingList.add(billingData);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        return billingList;
    }

    public double calculateTotalBillingAmount() {
        double totalAmount = 0;
        String sql = "SELECT SUM(total_amount) AS final_amount FROM billing";

        try (Connection connection = Database.getInstance().connectDB();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                totalAmount = resultSet.getDouble("final_amount");
            }

        } catch (Exception err) {
            err.printStackTrace();
        }

        return totalAmount;
    }

    public boolean addBillingData(String itemNumber, int quantity, int price, int totalAmount) {
        String sql = "INSERT INTO BILLING(item_number, quantity, price, total_amount) VALUES(?, ?, ?, ?)";
        try (Connection connection = Database.getInstance().connectDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, itemNumber);
            preparedStatement.setInt(2, quantity);
            preparedStatement.setInt(3, price);
            preparedStatement.setInt(4, totalAmount);

            int result = preparedStatement.executeUpdate();
            return result > 0;

        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public boolean updateBilling(String itemNumber, int quantity, double price, double totalAmount) {
        String sql = "UPDATE billing SET quantity=?, price=?, total_amount=? WHERE item_number=?";
        try (Connection connection = Database.getInstance().connectDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, quantity);
            preparedStatement.setDouble(2, price);
            preparedStatement.setDouble(3, totalAmount);
            preparedStatement.setString(4, itemNumber);

            return preparedStatement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBillingData(String itemNumber) {
        String sql;
        boolean deleteAll = (itemNumber == null || itemNumber.isBlank());

        if (deleteAll) {
            sql = "DELETE FROM BILLING";
        } else {
            sql = "DELETE FROM BILLING WHERE item_number=?";
        }

        try (Connection connection = Database.getInstance().connectDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (!deleteAll) {
                preparedStatement.setString(1, itemNumber);
            }

            return preparedStatement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
