package org.rubennicolas.alquilervehiculos.modelo.negocio.mysql;

import org.rubennicolas.alquilervehiculos.modelo.negocio.IAlquileres;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IClientes;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IFuenteDatos;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IVehiculos;

public class FuenteDatosMySQL implements IFuenteDatos {

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