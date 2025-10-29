package org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConexionMongoDB {

    private final Properties properties = new Properties();
    private MongoClient mongoClient;

    public ConexionMongoDB() {
        try (InputStream in = getResourceStream()) {
            if (in != null) {
                properties.load(in);
                System.out.println("[INFO] Configuración Mongo cargada desde archivo de propiedades.");
            } else {
                System.out.println("[WARN] No se encontró mongo.properties, se usarán variables de entorno o valores por defecto.");
            }
        } catch (IOException e) {
            throw new MongoException("Error cargando configuración de MongoDB: " + e.getMessage(), e);
        }
    }

    /**
     * Abre la conexión a MongoDB.
     * Prioriza variables de entorno (modo Docker),
     * luego .properties local y finalmente valores por defecto.
     */
    public MongoDatabase abrirConexion() {
        String host = getEnvOrProperty("MONGO_HOST", "db.host", "localhost");
        String port = getEnvOrProperty("MONGO_PORT", "db.port", "27017");
        String user = getEnvOrProperty("MONGO_USER", "db.user", "");
        String pass = getEnvOrProperty("MONGO_PASS", "db.password", "");
        String schema = getEnvOrProperty("MONGO_SCHEMA", "db.schema", "alquiler_vehiculos");

        String connectionString;
        if (!user.isEmpty() && !pass.isEmpty()) {
            connectionString = String.format("mongodb://%s:%s@%s:%s/%s", user, pass, host, port, schema);
        } else {
            connectionString = String.format("mongodb://%s:%s/%s", host, port, schema);
        }

        try {
            mongoClient = MongoClients.create(connectionString);
            MongoDatabase db = mongoClient.getDatabase(schema);
            System.out.printf("[INFO] Conectado a MongoDB en %s:%s (DB: %s)%n", host, port, schema);
            return db;
        } catch (Exception e) {
            throw new MongoException("Error conectando a MongoDB: " + e.getMessage(), e);
        }
    }

    public void cerrarConexion() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("[INFO] Conexión MongoDB cerrada correctamente.");
        }
    }

    /**
     * Intenta obtener el InputStream del archivo de propiedades.
     * 1️⃣ Si existe la variable MONGO_CONFIG_FILE, se usa esa ruta.
     * 2️⃣ Si no, busca mongo.properties en /app/config (modo Docker).
     * 3️⃣ Si no, busca en el classpath (modo local).
     */
    protected InputStream getResourceStream() {
        String envPath = System.getenv("MONGO_CONFIG_FILE");
        try {
            if (envPath != null && !envPath.isBlank()) {
                File f = new File(envPath);
                if (f.exists()) {
                    return new FileInputStream(f);
                }
            }
            File defaultPath = new File("/app/config/mongo.properties");
            if (defaultPath.exists()) {
                return new FileInputStream(defaultPath);
            }
        } catch (IOException ignored) {
        }
        return ConexionMongoDB.class.getClassLoader().getResourceAsStream("mongo.properties");
    }

    /**
     * Devuelve primero la variable de entorno, luego el .properties, o el valor por defecto.
     */
    private String getEnvOrProperty(String envKey, String propKey, String defaultValue) {
        String env = System.getenv(envKey);
        if (env != null && !env.isBlank()) {
            return env;
        }
        return properties.getProperty(propKey, defaultValue);
    }
}