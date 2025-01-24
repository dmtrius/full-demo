package com.example.demo.apps.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MySQLDatabaseExample {

    // JDBC URL, username and password of MySQL server
    private static final String URL = "jdbc:mysql://localhost:3306/registration_02";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String... args) {
        String sql = "select * from user_account";
        try (
                // Establish the connection
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                // Create a statement
                Statement statement = connection.createStatement();
                // Execute a SELECT query
                ResultSet resultSet = statement.executeQuery(sql)
        ) {
            System.out.println("Connection to MySQL DB successful!");

            // Process the ResultSet
            while (resultSet.next()) {
                // Retrieve by column name
                int id = resultSet.getInt("id");
                String name = resultSet.getString("first_name");
                String email = resultSet.getString("email");

                // Display values
                System.out.println("ID: " + id);
                System.out.println("Name: " + name);
                System.out.println("Email: " + email);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
