package com.example.demo.apps.db;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
public class PGSQLDatabaseExample {

    // JDBC URL, username, and password of MySQL server
    private static final String URL = "jdbc:postgresql://localhost:5434/d1";
    private static final String USER = "dmtrius";
    private static final String PASSWORD = "enter";

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
            IO.println("Connection to PostgreSQL DB successful!");

            // Process the ResultSet
            while (resultSet.next()) {
                // Retrieve by column name
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");

                // Display values
                IO.println("ID: " + id);
                IO.println("Name: " + name);
                IO.println("Email: " + email);
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching data from PostgreSQL DB", e);
        }
    }
}
