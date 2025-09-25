package org.rubennicolas.alquilervehiculos.modelo.negocio.mongodb;

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

public class Clientes implements IClientes {

    private static Clientes instancia;
    private final MongoCollection<Document> coleccion;

    private Clientes() {
        // Usa tu clase de conexión MongoDB
        ConexionMongoDB conexion = new ConexionMongoDB();
        MongoDatabase database = conexion.abrirConexion();
        this.coleccion = database.getCollection("clientes");
    }

    public static Clientes getInstancia() {
        if (instancia == null) {
            instancia = new Clientes();
        }
        return instancia;
    }

    @Override
    public void comenzar() {
        getInstancia();
    }

    @Override
    public List<Cliente> getClientes() {
        List<Cliente> clientes = new ArrayList<>();

        for (Document doc : coleccion.find()) {
            Cliente cliente = new Cliente(
                    doc.getString("nombre_apellidos"),
                    doc.getString("dni"),
                    doc.getString("telefono"),
                    doc.getString("email")
            );
            clientes.add(cliente);
        }

        return clientes.stream()
                .sorted(Comparator.comparing(Cliente::getNombreApellidos))
                .toList();
    }

    @Override
    public Cliente buscarCliente(Cliente cliente) {

        if (cliente == null) {
            throw new NullPointerException("No se puede buscar un cliente nulo.");
        }

        Cliente clienteBuscado = new Cliente();

        Document doc = coleccion.find(Filters.eq("dni", cliente.getDni())).first();

        if (doc != null) {

            clienteBuscado.setNombreApellidos(doc.getString("nombre_apellidos"));
            clienteBuscado.setDni(doc.getString("dni"));
            clienteBuscado.setTelefono(doc.getString("telefono"));
            clienteBuscado.setEmail(doc.getString("email"));

            return clienteBuscado;
        } else {
            return null;
        }
    }

    @Override
    public void insertarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("No se puede insertar un cliente nulo.");
        }

        if (buscarCliente(cliente) != null) {
            throw new DomainException("Ya existe un cliente con ese DNI.");
        }

        Document doc = new Document("nombre_apellidos", cliente.getNombreApellidos())
                .append("dni", cliente.getDni())
                .append("telefono", cliente.getTelefono())
                .append("email", cliente.getEmail());

        coleccion.insertOne(doc);
        System.out.println("Cliente insertado correctamente en MongoDB.");
    }

    @Override
    public void modificarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("No se puede modificar un cliente nulo.");
        }

        if (buscarCliente(cliente) == null) {
            throw new IllegalArgumentException("No existe ningún cliente con ese DNI.");
        }

        coleccion.updateOne(
                Filters.eq("dni", cliente.getDni()),
                Updates.combine(
                        Updates.set("nombre_apellidos", cliente.getNombreApellidos()),
                        Updates.set("telefono", cliente.getTelefono()),
                        Updates.set("email", cliente.getEmail())
                )
        );
        System.out.println("Cliente modificado correctamente en MongoDB.");
    }

    @Override
    public void borrarCliente(Cliente cliente) {

        Cliente clienteBuscado = buscarCliente(cliente);

        if (clienteBuscado == null) {
            throw new NullPointerException("No se puede borrar un cliente nulo.");
        }

        if (buscarCliente(clienteBuscado) == null) {
            throw new IllegalArgumentException("No existe ningún cliente con ese DNI.");
        }

        coleccion.deleteOne(Filters.eq("dni", cliente.getDni()));
        System.out.println("Cliente eliminado correctamente de MongoDB.");
    }

    @Override
    public void terminar() {

    }
}
