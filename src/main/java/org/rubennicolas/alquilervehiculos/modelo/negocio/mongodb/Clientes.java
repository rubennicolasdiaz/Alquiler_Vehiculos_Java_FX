package org.rubennicolas.alquilervehiculos.modelo.negocio.mongodb;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IClientes;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.ConexionMongoDB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Clientes implements IClientes {

    private final MongoCollection<Document> coleccion;

    // --- Constructor de producción ---
    public Clientes() {
        try {
            ConexionMongoDB conexion = new ConexionMongoDB();
            MongoDatabase database = conexion.abrirConexion();
            this.coleccion = database.getCollection("clientes");
        } catch (Exception e) {
            throw new MongoException("Error de conexión a MongoDB: " + e.getMessage());
        }
    }

    // --- Constructor para testing (inyectando colección mock) ---
    public Clientes(MongoCollection<Document> coleccion) {
        this.coleccion = coleccion;
    }

    @Override
    public void comenzar() {
        // Nada que hacer, la conexión ya está abierta
    }

    @Override
    public List<Cliente> getClientes() {
        try {
            List<Cliente> clientes = new ArrayList<>();
            for (Document doc : coleccion.find()) {
                clientes.add(documentToCliente(doc));
            }

            return clientes.stream()
                    .sorted(Comparator.comparing(Cliente::getNombreApellidos))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new DomainException("Error al obtener clientes de MongoDB: " + e.getMessage());
        }
    }

    @Override
    public Cliente buscarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("No se puede buscar un cliente nulo.");
        }

        Document doc = coleccion.find(Filters.eq("dni", cliente.getDni())).first();
        return (doc != null) ? documentToCliente(doc) : null;
    }

    @Override
    public void insertarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("No se puede insertar un cliente nulo.");
        }

        if (buscarCliente(cliente) != null) {
            throw new DomainException("Ya existe un cliente con ese DNI.");
        }

        try {
            coleccion.insertOne(clienteToDocument(cliente));
        } catch (Exception e) {
            throw new DomainException("Error al insertar cliente en MongoDB: " + e.getMessage());
        }
    }

    @Override
    public void modificarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("No se puede modificar un cliente nulo.");
        }

        if (buscarCliente(cliente) == null) {
            throw new DomainException("No existe ningún cliente con ese DNI.");
        }

        try {
            coleccion.updateOne(
                    Filters.eq("dni", cliente.getDni()),
                    Updates.combine(
                            Updates.set("nombre_apellidos", cliente.getNombreApellidos()),
                            Updates.set("telefono", cliente.getTelefono()),
                            Updates.set("email", cliente.getEmail())
                    )
            );
        } catch (Exception e) {
            throw new DomainException("Error al modificar cliente en MongoDB: " + e.getMessage());
        }
    }

    @Override
    public void borrarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("No se puede borrar un cliente nulo.");
        }

        if (buscarCliente(cliente) == null) {
            throw new DomainException("No existe ningún cliente con ese DNI.");
        }

        try {
            coleccion.deleteOne(Filters.eq("dni", cliente.getDni()));
        } catch (Exception e) {
            throw new DomainException("Error al borrar cliente en MongoDB: " + e.getMessage());
        }
    }

    private Cliente documentToCliente(Document doc) {
        return new Cliente(
                doc.getString("nombre_apellidos"),
                doc.getString("dni"),
                doc.getString("telefono"),
                doc.getString("email")
        );
    }

    private Document clienteToDocument(Cliente cliente) {
        return new Document("nombre_apellidos", cliente.getNombreApellidos())
                .append("dni", cliente.getDni())
                .append("telefono", cliente.getTelefono())
                .append("email", cliente.getEmail());
    }

    @Override
    public void terminar() {
        // Nada que cerrar (Mongo maneja la conexión automáticamente)
    }
}