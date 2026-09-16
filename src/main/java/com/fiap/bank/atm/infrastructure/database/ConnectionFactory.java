package com.fiap.bank.atm.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL = "jdbc:sqlite:atm_database.db";

    private ConnectionFactory() {}

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco de dados SQLite: " + e.getMessage(), e);
        }
    }
}