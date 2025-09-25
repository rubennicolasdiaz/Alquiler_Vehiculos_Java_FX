package org.rubennicolas.alquilervehiculos.modelo.negocio.ficheros;

import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IAlquileres;

import javax.naming.OperationNotSupportedException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesAlquiler.comprobarAlquiler;
import static org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesAlquiler.comprobarDevolucion;
import static org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesXml.escribirXmlAlquileres;
import static org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesXml.leerXmlAlquileres;

public class Alquileres implements IAlquileres {

    private static Alquileres instancia;
    private static List<Alquiler> coleccionAlquileres;

    private Alquileres() {

        coleccionAlquileres = new ArrayList<>();
        try {
            coleccionAlquileres = leerXmlAlquileres();
        } catch (DomainException e) {
            throw new DomainException("Error al leer el archivo XML: " + e.getMessage());
        }
    }

    public static Alquileres getInstancia() {
        if (instancia == null) {
            instancia = new Alquileres();
        }
        return instancia;
    }

    @Override
    public void comenzar() {
        getInstancia();
    }

    @Override
    public List<Alquiler> getAlquileres() {

        return coleccionAlquileres.stream()
                .sorted(Comparator.comparing(Alquiler::getFechaAlquiler)
                        .thenComparing(Alquiler::getCliente)
                        .thenComparing(Alquiler::getVehiculo))
                .toList();
    }

    @Override
    public List<Alquiler> getAlquileresPorCliente(Cliente cliente) {
        return coleccionAlquileres.stream()
                .filter(alq -> alq.getCliente().equals(cliente))
                .sorted(Comparator.comparing(Alquiler::getFechaAlquiler)
                        .thenComparing(Alquiler::getCliente)
                        .thenComparing(Alquiler::getVehiculo))
                .toList();
    }

    @Override
    public List<Alquiler> getAlquileresPorVehiculo(Vehiculo vehiculo) {
        return coleccionAlquileres.stream()
                .filter(alq -> alq.getVehiculo().equals(vehiculo))
                .sorted(Comparator.comparing(Alquiler::getFechaAlquiler)
                        .thenComparing(Alquiler::getCliente)
                        .thenComparing(Alquiler::getVehiculo))
                .toList();
    }

    @Override
    public Alquiler buscarAlquiler(int id) {

        return coleccionAlquileres.stream()
                .filter(alquiler -> alquiler.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void insertarAlquiler(Alquiler alquiler) {

        if (alquiler == null) {
            throw new NullPointerException("No se puede insertar un alquiler nulo.");
        }
        comprobarAlquiler(alquiler.getCliente(), alquiler.getVehiculo(),
                alquiler.getFechaAlquiler(), coleccionAlquileres);
        coleccionAlquileres.add(alquiler);
    }

    public void devolverAlquiler(Alquiler alquiler, LocalDate fechaDevolucion) {

        comprobarDevolucion(alquiler, fechaDevolucion, coleccionAlquileres);

        alquiler.setFechaDevolucion(fechaDevolucion);
    }

    @Override
    public void borrarAlquiler(Alquiler alquiler) {

        Alquiler alquilerBuscado = buscarAlquiler(alquiler.getId());
        try {
            if (alquilerBuscado == null) {
                throw new NullPointerException("No se puede borrar un alquiler nulo.");
            }
            if (!coleccionAlquileres.contains(alquilerBuscado)) {
                throw new OperationNotSupportedException("No existe ningún alquiler igual.");
            } else {
                coleccionAlquileres.remove(alquilerBuscado);
            }
        } catch (NullPointerException | OperationNotSupportedException e) {
            throw new DomainException(e.getMessage());
        }
    }

    @Override
    public void terminar() {
        escribirXmlAlquileres(coleccionAlquileres);
    }
}