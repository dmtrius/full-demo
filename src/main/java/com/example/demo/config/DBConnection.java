package com.example.demo.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBConnection {

    private Connection dbConnection;

    public void getDBConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        dbConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/jcg", "root", "");
    }

    public int executeQuery(String query) throws SQLException {
        try (PreparedStatement statement = dbConnection.prepareStatement(query)) {
            return statement.executeUpdate();
        }
    }
}
