package com.example.proyectocrud.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL =
            "jdbc:postgresql://localhost:5432/bdventas";

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    private ConexionDB() {
    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
