package com.example.demo.apps;

import com.example.demo.config.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

class DBConnectionTest {

    @InjectMocks
    private DBConnection dbConnection;
    @Mock
    private Connection mockConnection;
    @Mock private Statement mockStatement;

    private AutoCloseable closeable;

    @BeforeEach 
    public void openMocks() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable. close();
    }

//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }

    @Test
    void testMockDBConnection() throws Exception {
        Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);
        Mockito.when(mockConnection.createStatement().executeUpdate(
                Mockito.any())).thenReturn(1);
        int value = dbConnection.executeQuery("");
        assertEquals(1, value);
        Mockito.verify(
                mockConnection.createStatement(),
                Mockito.times(1)).executeQuery(anyString());
    }
}
