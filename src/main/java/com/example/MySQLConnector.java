package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnector {

    // Database credentials
    private static final String JDBC = System.getenv("JDBC");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");


    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection(JDBC, DB_USER, DB_PASSWORD)) {

            System.out.println("Successfully connected to the database!");

            // Example query
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {

                if (rs.next()) {
                    System.out.println("Query result: " + rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
