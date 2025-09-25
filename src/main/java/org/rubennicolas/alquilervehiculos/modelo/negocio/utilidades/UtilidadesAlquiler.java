package org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades;

import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;

import java.time.LocalDate;
import java.util.List;

public class UtilidadesAlquiler {

    private UtilidadesAlquiler() {
    }

    public static void comprobarAlquiler(Cliente cliente, Vehiculo vehiculo, LocalDate fechaAlquiler, List<Alquiler> coleccionAlquileres) {

        Alquiler nuevoAlquiler = new Alquiler(cliente, vehiculo, fechaAlquiler);

        coleccionAlquileres.forEach(alquiler -> {
            if (alquiler.getFechaDevolucion() == null) {

                if (alquiler.getCliente().equals(cliente)) {
                    throw new DomainException("El cliente tiene otro alquiler sin devolver.");
                }

                if (alquiler.getVehiculo().equals(vehiculo)) {
                    throw new DomainException("El vehículo está actualmente alquilado.");
                }
            } else {

                if (alquiler.solapaCon(nuevoAlquiler) && alquiler.getCliente().equals(cliente)
                        && alquiler.getVehiculo().equals(vehiculo)) {
                    throw new DomainException("El cliente tenía alquilado ese vehículo en ese periodo.");
                }

                if (alquiler.solapaCon(nuevoAlquiler) && alquiler.getCliente().equals(cliente)) {
                    throw new DomainException("El cliente tenía un alquiler en ese periodo.");
                }

                if (alquiler.solapaCon(nuevoAlquiler) && alquiler.getVehiculo().equals(vehiculo)) {
                    throw new DomainException("El vehículo estaba alquilado en ese periodo.");
                }
            }
        });
    }

    public static void comprobarDevolucion(Alquiler alquiler, LocalDate fechaDevolucion, List<Alquiler> coleccionAlquileres) {

        coleccionAlquileres
                .stream()
                .filter(alq -> !alq.equals(alquiler))
                .filter(alq -> alq.getFechaAlquiler().isAfter(alquiler.getFechaAlquiler()))
                .toList()
                .forEach(alq -> { // Lista filtrada, ya no está incluido el propio alquiler ni los anteriores

                    if (fechaDevolucion.isEqual(alq.getFechaAlquiler()) || fechaDevolucion.isAfter(alq.getFechaAlquiler())) {
                        if (alq.getVehiculo().equals(alquiler.getVehiculo()) || alq.getCliente().equals(alquiler.getCliente())) {

                            if (alquiler.getCliente().equals(alq.getCliente())
                                    && alquiler.getVehiculo().equals(alq.getVehiculo())) {
                                throw new DomainException("Solapamiento de fechas. El cliente tuvo alquilado ese vehículo en un periodo posterior.");
                            }

                            if (alquiler.getCliente().equals(alq.getCliente())) {
                                throw new DomainException("Solapamiento de fechas. El cliente tuvo un alquiler en un periodo posterior.");
                            }

                            if (alquiler.getVehiculo().equals(alq.getVehiculo())) {
                                throw new DomainException("Solapamiento de fechas. El vehículo estuvo alquilado en un periodo posterior.");
                            }
                        }
                    }
                });
    }
}