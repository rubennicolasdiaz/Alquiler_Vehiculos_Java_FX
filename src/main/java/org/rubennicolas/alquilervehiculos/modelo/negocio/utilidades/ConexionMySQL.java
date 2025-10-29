package org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades;

import org.rubennicolas.alquilervehiculos.excepciones.DomainException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionMySQL {

    private final Properties properties = new Properties();

    public ConexionMySQL() {
        // Cargar propiedades desde archivo o entorno
        try (InputStream in = getResourceStream()) {
            if (in != null) {
                properties.load(in);
                System.out.println("[INFO] Configuración MySQL cargada desde archivo de propiedades.");
            } else {
                System.out.println("[WARN] No se encontró mysql.properties, se usarán variables de entorno o valores por defecto.");
            }
        } catch (IOException e) {
            throw new DomainException("Error cargando configuración MySQL: " + e.getMessage());
        }
    }

    /**
     * Abre una conexión MySQL, priorizando:
     * 1️⃣ Variables de entorno (modo Docker)
     * 2️⃣ Archivo de propiedades
     * 3️⃣ Valores por defecto
     */
    public Connection abrirConexion() throws SQLException {
        String host = getEnvOrProperty("MYSQL_HOST", "db.host", "localhost");
        String port = getEnvOrProperty("MYSQL_PORT", "db.port", "3306");
        String schema = getEnvOrProperty("MYSQL_SCHEMA", "db.schema", "alquiler_vehiculos");
        String user = getEnvOrProperty("MYSQL_USER", "db.user", "root");
        String password = getEnvOrProperty("MYSQL_PASS", "db.password", "root");

        // Parámetros opcionales del JDBC
        String useSSL = getEnvOrProperty("MYSQL_USE_SSL", "db.useSSL", "false");
        String allowPublicKeyRetrieval = getEnvOrProperty("MYSQL_ALLOW_PUBLIC_KEY_RETRIEVAL", "db.allowPublicKeyRetrieval", "true");
        String timezone = getEnvOrProperty("MYSQL_SERVER_TIMEZONE", "db.serverTimezone", "UTC");

        String urlConexion = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=%s&allowPublicKeyRetrieval=%s&serverTimezone=%s",
                host, port, schema, useSSL, allowPublicKeyRetrieval, timezone
        );

        try {
            Connection conn = DriverManager.getConnection(urlConexion, user, password);
            System.out.printf("[INFO] Conectado a MySQL en %s:%s (DB: %s)%n", host, port, schema);
            return conn;
        } catch (SQLException e) {
            throw new SQLException("Error conectando a MySQL: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene primero la variable de entorno, luego el valor del .properties, o el valor por defecto.
     */
    private String getEnvOrProperty(String envKey, String propKey, String defaultValue) {
        String env = System.getenv(envKey);
        if (env != null && !env.isBlank()) {
            return env;
        }
        return properties.getProperty(propKey, defaultValue);
    }

    /**
     * Busca el archivo de configuración de MySQL:
     * 1️⃣ Si existe la variable MYSQL_CONFIG_FILE, usa esa ruta.
     * 2️⃣ Si no, busca /app/config/mysql.properties (modo Docker).
     * 3️⃣ Si no, intenta cargarlo desde el classpath (modo local).
     */
    private InputStream getResourceStream() {
        String configPath = System.getenv("MYSQL_CONFIG_FILE");
        try {
            if (configPath != null && !configPath.isBlank()) {
                File f = new File(configPath);
                if (f.exists()) {
                    return new FileInputStream(f);
                }
            }

            File defaultPath = new File("/app/config/mysql.properties");
            if (defaultPath.exists()) {
                return new FileInputStream(defaultPath);
            }
        } catch (IOException ignored) {
        }

        // fallback al classpath
        return ConexionMySQL.class.getClassLoader().getResourceAsStream("mysql.properties");
    }
}
