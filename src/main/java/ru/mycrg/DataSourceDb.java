package ru.mycrg;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

public class DataSourceDb {

    private final DataSource dataSource;

    public DataSourceDb() {
        dataSource = initDataSource();
    }

    private DataSource initDataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();

        driverManagerDataSource.setDriverClassName("org.postgresql.Driver");
        driverManagerDataSource.setUrl("jdbc:postgresql://localhost:5434/***");
        driverManagerDataSource.setUsername("***");
        driverManagerDataSource.setPassword("***");

        return driverManagerDataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
