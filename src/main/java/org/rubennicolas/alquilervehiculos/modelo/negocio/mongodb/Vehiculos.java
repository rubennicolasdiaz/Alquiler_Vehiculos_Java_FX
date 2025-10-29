package org.rubennicolas.alquilervehiculos.modelo.negocio.mongodb;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Autobus;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Furgoneta;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IVehiculos;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.ConexionMongoDB;
import org.rubennicolas.alquilervehiculos.vista.texto.TipoVehiculo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Vehiculos implements IVehiculos {

    private final MongoCollection<Document> coleccion;

    // --- Constructor de producción (usa conexión real) ---
    public Vehiculos() {
        try {
            ConexionMongoDB conexion = new ConexionMongoDB();
            MongoDatabase database = conexion.abrirConexion();
            this.coleccion = database.getCollection("vehiculos");
        } catch (Exception e) {
            throw new MongoException("Error al conectar con MongoDB: " + e.getMessage());
        }
    }

    // --- Constructor alternativo para testing (colección mock) ---
    public Vehiculos(MongoCollection<Document> coleccion) {
        this.coleccion = coleccion;
    }

    @Override
    public void comenzar() {
        // Nada que hacer; conexión lista
    }

    @Override
    public List<Vehiculo> getVehiculos() {
        try {
            List<Vehiculo> vehiculos = new ArrayList<>();
            for (Document doc : coleccion.find()) {
                vehiculos.add(documentToVehiculo(doc));
            }

            return vehiculos.stream()
                    .sorted(Comparator.comparing(Vehiculo::getMarca)
                            .thenComparing(Vehiculo::getModelo))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new DomainException("Error al obtener vehículos de MongoDB: " + e.getMessage());
        }
    }

    @Override
    public Vehiculo buscarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new NullPointerException("No se puede buscar un vehículo nulo.");
        }

        Document doc = coleccion.find(Filters.eq("matricula", vehiculo.getMatricula())).first();
        return (doc != null) ? documentToVehiculo(doc) : null;
    }

    @Override
    public void insertarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new NullPointerException("No se puede insertar un vehículo nulo.");
        }

        if (buscarVehiculo(vehiculo) != null) {
            throw new DomainException("Ya existe un vehículo con esa matrícula.");
        }

        try {
            coleccion.insertOne(vehiculoToDocument(vehiculo));
        } catch (Exception e) {
            throw new DomainException("Error al insertar vehículo en MongoDB: " + e.getMessage());
        }
    }

    @Override
    public void modificarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new NullPointerException("No se puede modificar un vehículo nulo.");
        }

        if (buscarVehiculo(vehiculo) == null) {
            throw new DomainException("No existe ningún vehículo con esa matrícula.");
        }

        try {
            coleccion.replaceOne(
                    Filters.eq("matricula", vehiculo.getMatricula()),
                    vehiculoToDocument(vehiculo)
            );
        } catch (Exception e) {
            throw new DomainException("Error al modificar vehículo en MongoDB: " + e.getMessage());
        }
    }

    @Override
    public void borrarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new NullPointerException("No se puede borrar un vehículo nulo.");
        }

        if (buscarVehiculo(vehiculo) == null) {
            throw new DomainException("No existe ningún vehículo con esa matrícula.");
        }

        try {
            coleccion.deleteOne(Filters.eq("matricula", vehiculo.getMatricula()));
        } catch (Exception e) {
            throw new DomainException("Error al borrar vehículo en MongoDB: " + e.getMessage());
        }
    }

    private Vehiculo documentToVehiculo(Document doc) {
        try {
            String marca = doc.getString("marca");
            String modelo = doc.getString("modelo");
            String matricula = doc.getString("matricula");
            TipoVehiculo tipo = TipoVehiculo.valueOf(doc.getString("tipo_vehiculo"));

            return switch (tipo) {
                case TURISMO -> new Turismo(marca, modelo, matricula, doc.getInteger("cilindrada"));
                case FURGONETA -> new Furgoneta(
                        marca, modelo, matricula,
                        doc.getInteger("plazas"),
                        doc.getInteger("pma")
                );
                case AUTOBUS -> new Autobus(marca, modelo, matricula, doc.getInteger("plazas"));
            };
        } catch (Exception e) {
            throw new DomainException("Error al convertir documento a vehículo: " + e.getMessage());
        }
    }

    private Document vehiculoToDocument(Vehiculo vehiculo) {
        try {
            Document doc = new Document("marca", vehiculo.getMarca())
                    .append("modelo", vehiculo.getModelo())
                    .append("matricula", vehiculo.getMatricula())
                    .append("tipo_vehiculo", vehiculo.getTipoVehiculo().name());

            if (vehiculo instanceof Turismo turismo) {
                doc.append("cilindrada", turismo.getCilindrada());
            } else if (vehiculo instanceof Furgoneta furgoneta) {
                doc.append("plazas", furgoneta.getPlazas())
                        .append("pma", furgoneta.getPma());
            } else if (vehiculo instanceof Autobus autobus) {
                doc.append("plazas", autobus.getPlazas());
            }

            return doc;
        } catch (Exception e) {
            throw new DomainException("Error al convertir vehículo a documento: " + e.getMessage());
        }
    }

    @Override
    public void terminar() {
        // No es necesario cerrar conexión (MongoDriver lo gestiona)
    }
}
