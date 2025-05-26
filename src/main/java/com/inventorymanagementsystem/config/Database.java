package com.inventorymanagementsystem.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Database {

    private static final String CONFIG_FILE = "application.properties";
    private static volatile Database instance;

    private Database() {}

    public static Database getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = new Database();
                }
            }
        }
        return instance;
    }

    public Connection connectDB() {
        Properties dbConfig = new Properties();
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("Database configuration file not found.");
            }

            dbConfig.load(input);
            Class.forName(dbConfig.getProperty("javafx.jdbc.driver"));

            return DriverManager.getConnection(
                    dbConfig.getProperty("javafx.datasource.url"),
                    dbConfig.getProperty("javafx.datasource.username"),
                    dbConfig.getProperty("javafx.datasource.password")
            );
        } catch (Exception e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }
}
