package org.rubennicolas.alquilervehiculos.modelo.negocio;

import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;

import java.util.List;

public interface IVehiculos {

    void comenzar();

    List<Vehiculo> getVehiculos();

    Vehiculo buscarVehiculo(Vehiculo vehiculo);

    void insertarVehiculo(Vehiculo vehiculo);

    void modificarVehiculo(Vehiculo vehiculo);

    void borrarVehiculo(Vehiculo vehiculo);

    void terminar();
}