package org.rubennicolas.alquilervehiculos.modelo.negocio.ficheros;

import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Autobus;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Furgoneta;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IVehiculos;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesFicheros;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Vehiculos implements IVehiculos {

    private final List<Vehiculo> coleccionVehiculos;
    private final Supplier<List<Vehiculo>> lector;
    private final Consumer<List<Vehiculo>> escritor;

    // --- Constructor de producción (usa XML real) ---
    public Vehiculos() {
        this(UtilidadesFicheros::leerXmlVehiculos, UtilidadesFicheros::escribirXmlVehiculos);
    }

    // --- Constructor alternativo para TESTS ---
    public Vehiculos(Supplier<List<Vehiculo>> lector, Consumer<List<Vehiculo>> escritor) {
        this.lector = lector;
        this.escritor = escritor;
        this.coleccionVehiculos = new ArrayList<>();

        try {
            List<Vehiculo> vehiculosLeidos = lector.get();
            coleccionVehiculos.addAll(vehiculosLeidos);
        } catch (Exception e) {
            throw new DomainException("Error al leer los vehículos: " + e.getMessage());
        }
    }

    @Override
    public void comenzar() {
        // nada: ya se carga en el constructor
    }

    @Override
    public List<Vehiculo> getVehiculos() {
        return coleccionVehiculos.stream()
                .sorted(Comparator.comparing(Vehiculo::getMarca)
                        .thenComparing(Vehiculo::getModelo))
                .toList();
    }

    @Override
    public Vehiculo buscarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null)
            throw new NullPointerException("No se puede buscar un vehículo nulo.");

        return coleccionVehiculos.stream()
                .filter(v -> v.equals(vehiculo))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void insertarVehiculo(Vehiculo vehiculo) {
        try {
            if (vehiculo == null) {
                throw new NullPointerException("No se puede insertar un vehículo nulo.");
            }
            if (coleccionVehiculos.contains(vehiculo)) {
                throw new OperationNotSupportedException("Ya existe un vehículo con esa matrícula.");
            }
            coleccionVehiculos.add(vehiculo);
        } catch (Exception e) {
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

            if (vehiculoBuscado instanceof Turismo turismo && vehiculo instanceof Turismo nuevoTurismo) {
                turismo.setMarca(nuevoTurismo.getMarca());
                turismo.setModelo(nuevoTurismo.getModelo());
                turismo.setCilindrada(nuevoTurismo.getCilindrada());
            } else if (vehiculoBuscado instanceof Furgoneta furgoneta && vehiculo instanceof Furgoneta nuevaFurgoneta) {
                furgoneta.setMarca(nuevaFurgoneta.getMarca());
                furgoneta.setModelo(nuevaFurgoneta.getModelo());
                furgoneta.setPlazas(nuevaFurgoneta.getPlazas());
                furgoneta.setPma(nuevaFurgoneta.getPma());
            } else if (vehiculoBuscado instanceof Autobus autobus && vehiculo instanceof Autobus nuevoAutobus) {
                autobus.setMarca(nuevoAutobus.getMarca());
                autobus.setModelo(nuevoAutobus.getModelo());
                autobus.setPlazas(nuevoAutobus.getPlazas());
            }

        } catch (Exception e) {
            throw new DomainException(e.getMessage());
        }
    }

    @Override
    public void borrarVehiculo(Vehiculo vehiculo) {
        try {
            Vehiculo vehiculoBuscado = buscarVehiculo(vehiculo);
            if (vehiculoBuscado == null)
                throw new NullPointerException("No se puede borrar un vehículo nulo.");

            if (!coleccionVehiculos.remove(vehiculoBuscado)) {
                throw new OperationNotSupportedException("No existe ningún vehículo con esa matrícula.");
            }
        } catch (Exception e) {
            throw new DomainException(e.getMessage());
        }
    }

    @Override
    public void terminar() {
        try {
            escritor.accept(coleccionVehiculos);
        } catch (Exception e) {
            throw new DomainException("Error al guardar los vehículos: " + e.getMessage());
        }
    }
}
