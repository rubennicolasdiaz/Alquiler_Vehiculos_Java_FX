package org.rubennicolas.alquilervehiculos.modelo.negocio.mongodb;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IAlquileres;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.ConexionMongoDB;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesAlquiler.comprobarAlquiler;
import static org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesAlquiler.comprobarDevolucion;

public class Alquileres implements IAlquileres {

    private static Alquileres instancia;
    private final MongoCollection<Document> coleccion;

    private Alquileres() {

        try {
            ConexionMongoDB conexion = new ConexionMongoDB();
            MongoDatabase database = conexion.abrirConexion();
            this.coleccion = database.getCollection("alquileres");
        } catch (Exception e) {
            throw new MongoException("Error de conexión a MongoDB: " + e.getMessage());
        }

    }

    public static Alquileres getInstancia() {

        if (instancia == null) {
            instancia = new Alquileres(); // Crear la instancia sólo si aún no se ha creado
        }
        return instancia;
    }

    @Override
    public void comenzar() {
        getInstancia();
    }

    @Override
    public List<Alquiler> getAlquileres() {
        List<Alquiler> alquileres = new ArrayList<>();

        for (Document doc : coleccion.find()) {
            Alquiler alquiler = documentToAlquiler(doc);
            if (alquiler != null) {
                alquileres.add(alquiler);
            }
        }

        return alquileres.stream()
                .sorted(Comparator.comparing(Alquiler::getFechaAlquiler)
                        .thenComparing(Alquiler::getCliente)
                        .thenComparing(Alquiler::getVehiculo))
                .toList();
    }

    @Override
    public List<Alquiler> getAlquileresPorCliente(Cliente cliente) {
        return getAlquileres().stream()
                .filter(alq -> alq.getCliente().equals(cliente))
                .sorted(Comparator.comparing(Alquiler::getFechaAlquiler)
                        .thenComparing(Alquiler::getVehiculo))
                .toList();
    }

    @Override
    public List<Alquiler> getAlquileresPorVehiculo(Vehiculo vehiculo) {
        return getAlquileres().stream()
                .filter(alq -> alq.getVehiculo().equals(vehiculo))
                .sorted(Comparator.comparing(Alquiler::getFechaAlquiler)
                        .thenComparing(Alquiler::getCliente))
                .toList();
    }

    @Override
    public Alquiler buscarAlquiler(int id) {
        Document doc = coleccion.find(Filters.eq("id", id)).first();
        return (doc != null) ? documentToAlquiler(doc) : null;
    }

    @Override
    public void insertarAlquiler(Alquiler alquiler) {
        if (alquiler == null) {
            throw new NullPointerException("No se puede insertar un alquiler nulo.");
        }

        comprobarAlquiler(alquiler.getCliente(), alquiler.getVehiculo(),
                alquiler.getFechaAlquiler(), getAlquileres());

        if (buscarAlquiler(alquiler.getId()) != null) {
            throw new DomainException("Ya existe un alquiler con ese id.");
        }

        Document doc = alquilerToDocument(alquiler);
        coleccion.insertOne(doc);
        System.out.println("Alquiler insertado en MongoDB.");
    }

    @Override
    public void devolverAlquiler(Alquiler alquiler, LocalDate fechaDevolucion) {

        comprobarDevolucion(alquiler, fechaDevolucion, getAlquileres());

        alquiler.setFechaDevolucion(fechaDevolucion);

        coleccion.updateOne(Filters.eq("id", alquiler.getId()),
                new Document("$set", new Document("fechaDevolucion", fechaDevolucion.toString())));

        System.out.println("Alquiler devuelto en MongoDB.");
    }

    @Override
    public void borrarAlquiler(Alquiler alquiler) {

        if (alquiler == null) {
            throw new DomainException("No existe ningún alquiler con id: " + alquiler.getId());
        }

        try {
            coleccion.deleteOne(Filters.eq("id", alquiler.getId()));
            System.out.println("Alquiler eliminado en MongoDB.");
        } catch (Exception e) {
            throw new DomainException("No se pudo borrar el alquiler en MongoDB.");
        }
    }

    private Alquiler documentToAlquiler(Document doc) {
        int id = doc.getInteger("id");
        String dniCliente = doc.getString("cliente");
        String matriculaVehiculo = doc.getString("vehiculo");

        LocalDate fechaAlquiler = LocalDate.parse(doc.getString("fechaAlquiler"));
        LocalDate fechaDevolucion = (doc.getString("fechaDevolucion") != null)
                ? LocalDate.parse(doc.getString("fechaDevolucion"))
                : null;

        Cliente cliente = Controlador.getInstancia().buscarCliente(new Cliente("Cliente", dniCliente, "900900900", "mail@mail.com"));
        Vehiculo vehiculo = Controlador.getInstancia().buscarVehiculo(
                new org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo("Marca", "Modelo", matriculaVehiculo, 1000)
        );

        if (cliente == null || vehiculo == null) {
            return null;
        }

        return (fechaDevolucion == null)
                ? new Alquiler(id, cliente, vehiculo, fechaAlquiler)
                : new Alquiler(id, cliente, vehiculo, fechaAlquiler, fechaDevolucion);
    }

    private Document alquilerToDocument(Alquiler alquiler) {
        Document doc = new Document("id", alquiler.getId())
                .append("cliente", alquiler.getCliente().getDni())
                .append("vehiculo", alquiler.getVehiculo().getMatricula())
                .append("fechaAlquiler", alquiler.getFechaAlquiler().toString());


        if (alquiler.getFechaDevolucion() != null) {
            doc.append("fechaDevolucion", alquiler.getFechaDevolucion().toString());
        }

        return doc;
    }

    @Override
    public void terminar() {

    }
}