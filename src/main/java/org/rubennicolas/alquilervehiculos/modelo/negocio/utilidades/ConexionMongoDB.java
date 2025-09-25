package org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConexionMongoDB {

    private final Properties properties = new Properties();
    private MongoClient mongoClient;

    public ConexionMongoDB() {
        try (InputStream in = ConexionMongoDB.class.getClassLoader().getResourceAsStream("mongo.properties")) {
            if (in == null) {
                throw new IOException("No se encontró mongo.properties en el classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new MongoException("Error cargando configuración DB: " + e.getMessage());
        }
    }

    public MongoDatabase abrirConexion() {

        String host = properties.getProperty("db.host", "localhost");
        String port = properties.getProperty("db.port", "27017");
        String schema = properties.getProperty("db.schema", "alquiler_vehiculos");
        //String user = properties.getProperty("db.user");
        //String password = properties.getProperty("db.password");

        // Construir cadena de conexión
        String connectionString = String.format("mongodb://%s:%s", host, port);

        // Crear cliente Mongo
        mongoClient = MongoClients.create(connectionString);

        // Devolver la base de datos solicitada
        return mongoClient.getDatabase(schema);
    }

    public void cerrarConexion() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
