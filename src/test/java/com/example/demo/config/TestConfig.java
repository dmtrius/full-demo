package com.example.demo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

@TestConfiguration
@Profile("test")
public class TestConfig {

//    @Bean
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.h2.Driver");
//        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
//        dataSource.setUsername("sa");
//        dataSource.setPassword("password");
//        return dataSource;
//    }

    @Bean
//    @Primary
    public DataSource dataSource() {
        try (PostgreSQLContainer<?> postgresContainer
                = new PostgreSQLContainer<>("postgres:16")) {
            postgresContainer.start();

            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(postgresContainer.getDriverClassName());
            dataSource.setUrl(postgresContainer.getJdbcUrl());
            dataSource.setUsername(postgresContainer.getUsername());
            dataSource.setPassword(postgresContainer.getPassword());

            return dataSource;
        }
    }
}
