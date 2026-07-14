package com.example.demo.apps.db;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
public class MySQLDatabaseExample {

    // JDBC URL, username, and password of MySQL server
    private static final String URL = "jdbc:mysql://localhost:3306/registration_02";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @SuppressWarnings("java:S2115")
    void main() {
        String sql = "select id, name, email from user_account";
        try (
                // Establish the connection
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                // Create a statement
                Statement statement = connection.createStatement();
                // Execute a SELECT query
                ResultSet resultSet = statement.executeQuery(sql)
        ) {
            IO.println("Connection to MySQL DB successful!");

            // Process the ResultSet
            while (resultSet.next()) {
                // Retrieve by column name
                int id = resultSet.getInt("id");
                String name = resultSet.getString("first_name");
                String email = resultSet.getString("email");

                // Display values
                IO.println("ID: " + id);
                IO.println("Name: " + name);
                IO.println("Email: " + email);
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching data from MySQL DB", e);
        }
    }
}
