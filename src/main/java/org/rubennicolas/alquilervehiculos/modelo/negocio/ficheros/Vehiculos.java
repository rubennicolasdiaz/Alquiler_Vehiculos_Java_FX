package org.rubennicolas.alquilervehiculos.modelo.negocio.ficheros;

import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Autobus;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Furgoneta;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IVehiculos;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesXml;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Vehiculos implements IVehiculos {

    private static Vehiculos instancia;
    private static List<Vehiculo> coleccionVehiculos;

    private Vehiculos() {

        try {
            coleccionVehiculos = new ArrayList<>();
            coleccionVehiculos = UtilidadesXml.leerXmlVehiculos();
        } catch (Exception e) {
            throw new DomainException(e.getMessage());
        }
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
        return coleccionVehiculos
                .stream()
                .sorted(Comparator.comparing(Vehiculo::getMarca)
                        .thenComparing(Vehiculo::getModelo))
                .toList();
    }

    @Override
    public Vehiculo buscarVehiculo(Vehiculo vehiculo) {

        if (vehiculo == null) {
            throw new NullPointerException("No se puede buscar un vehículo nulo.");
        }
        return coleccionVehiculos.stream()
                .filter(vehi -> vehi.equals(vehiculo))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void insertarVehiculo(Vehiculo vehiculo) {
        try {
            if (vehiculo == null) {
                throw new NullPointerException("No se puede insertar un vehículo nulo.");
            } else if ((coleccionVehiculos.contains(vehiculo))) {
                throw new OperationNotSupportedException("Ya existe un vehículo con esa matrícula.");
            }
            coleccionVehiculos.add(vehiculo);
        } catch (NullPointerException | OperationNotSupportedException e) {
            throw new DomainException(e.getMessage());
        }
    }

    @Override
    public void modificarVehiculo(Vehiculo vehiculo) {

        Vehiculo vehiculoBuscado = buscarVehiculo(vehiculo);

        try {
            if (vehiculoBuscado == null) {
                throw new NullPointerException("No se puede modificar un vehículo nulo.");
            }

            if (!coleccionVehiculos.contains(vehiculoBuscado)) {
                throw new OperationNotSupportedException("No existe ningún vehículo con esa matrícula.");
            }

            if (vehiculoBuscado instanceof Turismo turismo) {

                turismo.setMarca(turismo.getMarca());
                turismo.setModelo(turismo.getModelo());
                turismo.setCilindrada(turismo.getCilindrada());
            }

            if (vehiculoBuscado instanceof Furgoneta furgoneta) {

                furgoneta.setMarca(furgoneta.getMarca());
                furgoneta.setModelo(furgoneta.getModelo());
                furgoneta.setPlazas(furgoneta.getPlazas());
                furgoneta.setPma(furgoneta.getPma());
            }

            if (vehiculoBuscado instanceof Autobus autobus) {

                autobus.setMarca(autobus.getMarca());
                autobus.setModelo(autobus.getModelo());
                autobus.setPlazas(autobus.getPlazas());
            }
        } catch (Exception e) {
            throw new DomainException(e.getMessage());
        }
    }

    @Override
    public void borrarVehiculo(Vehiculo vehiculo) {

        Vehiculo vehiculoBuscado = buscarVehiculo(vehiculo);
        try {
            if (vehiculoBuscado == null) {
                throw new NullPointerException("No se puede borrar un vehículo nulo.");
            }
            if (!coleccionVehiculos.contains(vehiculoBuscado)) {
                throw new OperationNotSupportedException("No existe ningún vehículo con esa matrícula.");
            } else {
                coleccionVehiculos.remove(vehiculoBuscado);
            }
        } catch (NullPointerException | OperationNotSupportedException e) {
            throw new DomainException(e.getMessage());
        }
    }

    @Override
    public void terminar() {
        try {
            UtilidadesXml.escribirXmlVehiculos(coleccionVehiculos);
        } catch (TransformerException | ParserConfigurationException e) {
            throw new DomainException(e.getMessage());
        }
    }
}