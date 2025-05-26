package com.inventorymanagementsystem.service;

import com.inventorymanagementsystem.config.Database;
import com.inventorymanagementsystem.entity.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductService {
    public List<Product> getAllProducts() {
        List<Product> productsList = new ArrayList<>();
        Connection connection = Database.getInstance().connectDB();
        String sql = "SELECT * FROM PRODUCTS";

        try (
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
        ) {
            while (resultSet.next()) {
                Product product = new Product.Builder()
                        .id(resultSet.getInt("id"))
                        .itemNumber(resultSet.getString("item_number"))
                        .itemGroup(resultSet.getString("item_group"))
                        .quantity(resultSet.getInt("quantity"))
                        .price(resultSet.getDouble("price"))
                        .build();

                productsList.add(product);
            }
        } catch (Exception err) {
            System.err.println("Veritabanı hatası: " + err.getMessage());
        }

        return productsList;
    }

    public List<String> getAllItemNumbers() {
        List<Product> products = getAllProducts();
        return products.stream()
                .map(Product::getItemNumber)
                .collect(Collectors.toList());
    }

    public Optional<Product> getProductByItemNumber(String itemNumber) {
        return getAllProducts().stream()
                .filter(prod -> prod.getItemNumber().equals(itemNumber))
                .findAny();
    }

    public ObservableList<Product> listProductData() {
        ObservableList<Product> productList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM PRODUCTS";

        try (Connection connection = Database.getInstance().connectDB();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Product product = new Product.Builder()
                        .id(resultSet.getInt("id"))
                        .itemNumber(resultSet.getString("item_number"))
                        .itemGroup(resultSet.getString("item_group"))
                        .quantity(resultSet.getInt("quantity"))
                        .price(resultSet.getDouble("price"))
                        .build();
                productList.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return productList;
    }

    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET item_number = ?, item_group = ?, quantity = ?, price = ? WHERE id = ?";

        try (Connection connection = Database.getInstance().connectDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, product.getItemNumber());
            preparedStatement.setString(2, product.getItemGroup());
            preparedStatement.setInt(3, product.getQuantity());
            preparedStatement.setDouble(4, product.getPrice());
            preparedStatement.setInt(5, product.getId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean decreaseStock(String itemNumber, int quantity) {
        String selectQuery = "SELECT quantity FROM products WHERE item_number = ?";
        String updateQuery = "UPDATE products SET quantity = quantity - ? WHERE item_number = ?";
        String deleteQuery = "DELETE FROM products WHERE item_number = ?";

        try (Connection conn = Database.getInstance().connectDB()) {
            conn.setAutoCommit(false);

            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setString(1, itemNumber);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    int currentQuantity = rs.getInt("quantity");

                    if (currentQuantity < quantity) {
                        conn.rollback();
                        return false;
                    }

                    int newQuantity = currentQuantity - quantity;

                    if (newQuantity == 0) {
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                            deleteStmt.setString(1, itemNumber);
                            deleteStmt.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, quantity);
                            updateStmt.setString(2, itemNumber);
                            updateStmt.executeUpdate();
                        }
                    }

                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false; // ürün bulunamadı
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public int getStockQuantity(String itemNumber) {
        int quantity = -1;

        String query = "SELECT quantity FROM products WHERE item_number = ?";

        try (Connection conn = Database.getInstance().connectDB();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, itemNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    quantity = rs.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quantity;
    }





}
