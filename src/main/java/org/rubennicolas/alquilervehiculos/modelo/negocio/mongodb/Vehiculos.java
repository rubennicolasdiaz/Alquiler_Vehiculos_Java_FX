package org.rubennicolas.alquilervehiculos.modelo.negocio.mongodb;


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

public class Vehiculos implements IVehiculos {

    private static Vehiculos instancia;
    private final MongoCollection<Document> coleccion;

    private Vehiculos() {
        ConexionMongoDB conexion = new ConexionMongoDB();
        MongoDatabase database = conexion.abrirConexion();
        this.coleccion = database.getCollection("vehiculos");
    }

    public static Vehiculos getInstancia() {
        if (instancia == null) {
            instancia = new Vehiculos();
        }
        return instancia;
    }

    @Override
    public void comenzar() {
        getInstancia();
    }

    @Override
    public List<Vehiculo> getVehiculos() {
        List<Vehiculo> vehiculos = new ArrayList<>();

        for (Document doc : coleccion.find()) {
            Vehiculo vehiculo = documentToVehiculo(doc);
            vehiculos.add(vehiculo);
        }

        return vehiculos.stream()
                .sorted(Comparator.comparing(Vehiculo::getMarca)
                        .thenComparing(Vehiculo::getModelo))
                .toList();
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

        Vehiculo existente = buscarVehiculo(vehiculo);
        if (existente != null) {
            throw new DomainException("Ya existe un vehículo con esa matrícula.");
        }

        Document doc = vehiculoToDocument(vehiculo);
        coleccion.insertOne(doc);
        System.out.println("Vehículo insertado en MongoDB.");
    }

    @Override
    public void modificarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new NullPointerException("No se puede modificar un vehículo nulo.");
        }

        Vehiculo existente = buscarVehiculo(vehiculo);
        if (existente == null) {
            throw new IllegalArgumentException("No existe ningún vehículo con esa matrícula.");
        }

        Document doc = vehiculoToDocument(vehiculo);
        coleccion.replaceOne(Filters.eq("matricula", vehiculo.getMatricula()), doc);
        System.out.println("Vehículo modificado en MongoDB.");
    }

    @Override
    public void borrarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new NullPointerException("No se puede borrar un vehículo nulo.");
        }

        Vehiculo existente = buscarVehiculo(vehiculo);
        if (existente == null) {
            throw new IllegalArgumentException("No existe ningún vehículo con esa matrícula.");
        }

        coleccion.deleteOne(Filters.eq("matricula", vehiculo.getMatricula()));
        System.out.println("Vehículo borrado en MongoDB.");
    }

    private Vehiculo documentToVehiculo(Document doc) {
        String marca = doc.getString("marca");
        String modelo = doc.getString("modelo");
        String matricula = doc.getString("matricula");
        TipoVehiculo tipo = TipoVehiculo.valueOf(doc.getString("tipo_vehiculo"));

        return switch (tipo) {
            case TURISMO -> new Turismo(marca, modelo, matricula, doc.getInteger("cilindrada"));
            case FURGONETA -> new Furgoneta(marca, modelo, matricula,
                    doc.getInteger("plazas"),
                    doc.getInteger("pma"));
            case AUTOBUS -> new Autobus(marca, modelo, matricula, doc.getInteger("plazas"));
        };
    }

    private Document vehiculoToDocument(Vehiculo vehiculo) {
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
    }

    @Override
    public void terminar() {

    }
}
