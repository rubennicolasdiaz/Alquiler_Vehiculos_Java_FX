package org.rubennicolas.alquilervehiculos.modelo.negocio;

public interface IFuenteDatos {

	IClientes crearClientes();
	
	IVehiculos crearVehiculos();
	
	IAlquileres crearAlquileres();
}