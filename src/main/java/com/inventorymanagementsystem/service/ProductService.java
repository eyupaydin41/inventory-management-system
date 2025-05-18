package com.inventorymanagementsystem.service;

import com.inventorymanagementsystem.config.Database;
import com.inventorymanagementsystem.entity.Product;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
}
