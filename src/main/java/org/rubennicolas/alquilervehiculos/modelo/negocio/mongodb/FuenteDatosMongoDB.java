package org.rubennicolas.alquilervehiculos.modelo.negocio.mongodb;

import org.rubennicolas.alquilervehiculos.modelo.negocio.IAlquileres;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IClientes;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IFuenteDatos;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IVehiculos;

public class FuenteDatosMongoDB implements IFuenteDatos {

    @Override
    public IClientes crearClientes() {
        return new Clientes();
    }

    @Override
    public IVehiculos crearVehiculos() {
        return new Vehiculos();
    }

    @Override
    public IAlquileres crearAlquileres() {
        return new Alquileres();
    }
}