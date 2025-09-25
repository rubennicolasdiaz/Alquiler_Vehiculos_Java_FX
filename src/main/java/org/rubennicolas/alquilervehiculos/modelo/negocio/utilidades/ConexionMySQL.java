package org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades;

import org.rubennicolas.alquilervehiculos.excepciones.DomainException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionMySQL {

    private final Properties properties = new Properties();

    public ConexionMySQL() {
        try (InputStream in = ConexionMySQL.class.getClassLoader().getResourceAsStream("mysql.properties")) {
            if (in == null) {
                throw new IOException("No se encontró mysql.properties en el classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new DomainException("Error cargando configuración DB: " + e.getMessage());
        }
    }

    public Connection abrirConexion() throws SQLException {
        String host = properties.getProperty("db.host", "localhost");
        String port = properties.getProperty("db.port", "3306");
        String schema = properties.getProperty("db.schema");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");

        String urlConexion = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, schema
        );

        return DriverManager.getConnection(urlConexion, user, password);
    }
}
