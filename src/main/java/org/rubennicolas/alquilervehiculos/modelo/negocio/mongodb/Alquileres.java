package org.rubennicolas.alquilervehiculos.modelo.negocio.mongodb;

import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.*;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IAlquileres;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.ConexionMongoDB;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesAlquiler.comprobarAlquiler;
import static org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesAlquiler.comprobarDevolucion;

public class Alquileres implements IAlquileres {

    private final MongoCollection<Document> coleccion;
    private final Function<String, Cliente> buscadorCliente;
    private final Function<String, Vehiculo> buscadorVehiculo;

    public Alquileres() {
        try {
            ConexionMongoDB conexion = new ConexionMongoDB();
            MongoDatabase database = conexion.abrirConexion();
            this.coleccion = database.getCollection("alquileres");

            this.buscadorCliente = dni -> new Clientes().buscarCliente(new Cliente("tmp", dni, "600000000", "mail@mail.com"));
            this.buscadorVehiculo = matricula -> new Vehiculos().buscarVehiculo(new Turismo("tmp", "tmp", matricula, 1000));

        } catch (Exception e) {
            throw new MongoException("Error de conexión a MongoDB: " + e.getMessage());
        }
    }

    // --- Constructor alternativo para test o inyección manual ---
    public Alquileres(MongoCollection<Document> coleccion,
                      Function<String, Cliente> buscadorCliente,
                      Function<String, Vehiculo> buscadorVehiculo) {
        this.coleccion = coleccion;
        this.buscadorCliente = buscadorCliente;
        this.buscadorVehiculo = buscadorVehiculo;
    }

    @Override
    public void comenzar() {

    }

    @Override
    public List<Alquiler> getAlquileres() {
        try {
            // ⚙️ Usamos aggregation con $lookup para hacer "joins"
            AggregateIterable<Document> resultados = coleccion.aggregate(Arrays.asList(
                    Aggregates.lookup("clientes", "cliente", "dni", "clienteInfo"),
                    Aggregates.lookup("vehiculos", "vehiculo", "matricula", "vehiculoInfo")
            ));

            List<Alquiler> alquileres = new ArrayList<>();
            for (Document doc : resultados) {
                Alquiler alquiler = documentToAlquilerJoin(doc);
                if (alquiler != null) alquileres.add(alquiler);
            }

            return alquileres.stream()
                    .sorted(Comparator.comparing(Alquiler::getFechaAlquiler)
                            .thenComparing(a -> a.getCliente().getNombreApellidos())
                            .thenComparing(a -> a.getVehiculo().getMatricula()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new DomainException("Error al obtener alquileres de MongoDB: " + e.getMessage());
        }
    }

    @Override
    public List<Alquiler> getAlquileresPorCliente(Cliente cliente) {
        return getAlquileres().stream()
                .filter(a -> a.getCliente().equals(cliente))
                .collect(Collectors.toList());
    }

    @Override
    public List<Alquiler> getAlquileresPorVehiculo(Vehiculo vehiculo) {
        return getAlquileres().stream()
                .filter(a -> a.getVehiculo().equals(vehiculo))
                .collect(Collectors.toList());
    }

    @Override
    public Alquiler buscarAlquiler(int id) {
        Document doc = coleccion.find(Filters.eq("id", id)).first();
        return (doc != null) ? documentToAlquilerSimple(doc) : null;
    }

    @Override
    public void insertarAlquiler(Alquiler alquiler) {
        if (alquiler == null) throw new NullPointerException("No se puede insertar un alquiler nulo.");

        comprobarAlquiler(alquiler.getCliente(), alquiler.getVehiculo(), alquiler.getFechaAlquiler(), getAlquileres());

        if (buscarAlquiler(alquiler.getId()) != null) {
            throw new DomainException("Ya existe un alquiler con ese id.");
        }

        try {
            coleccion.insertOne(alquilerToDocument(alquiler));
        } catch (Exception e) {
            throw new DomainException("Error al insertar alquiler en MongoDB: " + e.getMessage());
        }
    }

    @Override
    public void devolverAlquiler(Alquiler alquiler, LocalDate fechaDevolucion) {
        comprobarDevolucion(alquiler, fechaDevolucion, getAlquileres());
        alquiler.setFechaDevolucion(fechaDevolucion);

        try {
            coleccion.updateOne(
                    Filters.eq("id", alquiler.getId()),
                    new Document("$set", new Document("fechaDevolucion", fechaDevolucion.toString()))
            );
        } catch (Exception e) {
            throw new DomainException("Error al actualizar devolución en MongoDB: " + e.getMessage());
        }
    }

    @Override
    public void borrarAlquiler(Alquiler alquiler) {
        if (alquiler == null) throw new DomainException("No se puede borrar un alquiler nulo.");
        coleccion.deleteOne(Filters.eq("id", alquiler.getId()));
    }

    // =====================================================
    // 🔍 Conversores optimizados
    // =====================================================

    /**
     * Convierte documento con clienteInfo y vehiculoInfo
     */
    private Alquiler documentToAlquilerJoin(Document doc) {
        try {
            int id = doc.getInteger("id");
            LocalDate fechaAlquiler = LocalDate.parse(doc.getString("fechaAlquiler"));
            LocalDate fechaDevolucion = (doc.getString("fechaDevolucion") != null)
                    ? LocalDate.parse(doc.getString("fechaDevolucion"))
                    : null;

            // Extraer datos embebidos del $lookup
            List<Document> clienteInfo = doc.getList("clienteInfo", Document.class, List.of());
            List<Document> vehiculoInfo = doc.getList("vehiculoInfo", Document.class, List.of());

            if (clienteInfo.isEmpty() || vehiculoInfo.isEmpty()) return null;

            Document c = clienteInfo.get(0);
            Cliente cliente = new Cliente(
                    c.getString("nombre_apellidos"),
                    c.getString("dni"),
                    c.getString("telefono"),
                    c.getString("email")
            );

            Document v = vehiculoInfo.get(0);
            Vehiculo vehiculo = null;

            if (v.getString("tipo_vehiculo").equals("TURISMO")) {
                vehiculo = new Turismo(
                        v.getString("marca"),
                        v.getString("modelo"),
                        v.getString("matricula"),
                        v.getInteger("cilindrada")
                );
            }

            if (v.getString("tipo_vehiculo").equals("FURGONETA")) {
                vehiculo = new Furgoneta(
                        v.getString("marca"),
                        v.getString("modelo"),
                        v.getString("matricula"),
                        v.getInteger("plazas"),
                        v.getInteger("pma")
                );
            }

            if (v.getString("tipo_vehiculo").equals("AUTOBUS")) {
                vehiculo = new Autobus(
                        v.getString("marca"),
                        v.getString("modelo"),
                        v.getString("matricula"),
                        v.getInteger("plazas")
                );
            }

            return (fechaDevolucion == null)
                    ? new Alquiler(id, cliente, vehiculo, fechaAlquiler)
                    : new Alquiler(id, cliente, vehiculo, fechaAlquiler, fechaDevolucion);

        } catch (Exception e) {
            throw new DomainException("Error al convertir documento (join): " + e.getMessage());
        }
    }

    /**
     * Convierte documento simple (sin lookup, para búsquedas directas)
     */
    private Alquiler documentToAlquilerSimple(Document doc) {
        try {
            int id = doc.getInteger("id");
            String dniCliente = doc.getString("cliente");
            String matriculaVehiculo = doc.getString("vehiculo");
            LocalDate fechaAlquiler = LocalDate.parse(doc.getString("fechaAlquiler"));
            LocalDate fechaDevolucion = (doc.getString("fechaDevolucion") != null)
                    ? LocalDate.parse(doc.getString("fechaDevolucion"))
                    : null;

            Cliente cliente = buscadorCliente.apply(dniCliente);
            Vehiculo vehiculo = buscadorVehiculo.apply(matriculaVehiculo);

            if (cliente == null || vehiculo == null) return null;

            return (fechaDevolucion == null)
                    ? new Alquiler(id, cliente, vehiculo, fechaAlquiler)
                    : new Alquiler(id, cliente, vehiculo, fechaAlquiler, fechaDevolucion);

        } catch (Exception e) {
            throw new DomainException("Error al convertir documento simple: " + e.getMessage());
        }
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