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
                Product product = new Product(
                        Integer.parseInt(resultSet.getString("id")),
                        resultSet.getString("item_number"),
                        resultSet.getString("item_group"),
                        Integer.parseInt(resultSet.getString("quantity")),
                        Double.parseDouble(resultSet.getString("price"))
                );
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
                Product product = new Product(
                        resultSet.getInt("id"),
                        resultSet.getString("item_number"),
                        resultSet.getString("item_group"),
                        Integer.parseInt(resultSet.getString("quantity")),
                        Double.parseDouble(resultSet.getString("price"))
                );
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



}
