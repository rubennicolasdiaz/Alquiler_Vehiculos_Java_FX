package org.rubennicolas.alquilervehiculos.modelo.negocio;

import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;

import java.time.LocalDate;
import java.util.List;

public interface IAlquileres {

    void comenzar();

    List<Alquiler> getAlquileres();

    List<Alquiler> getAlquileresPorCliente(Cliente cliente);

    List<Alquiler> getAlquileresPorVehiculo(Vehiculo vehiculo);

    Alquiler buscarAlquiler(int id);

    void insertarAlquiler(Alquiler alquiler);

    void devolverAlquiler(Alquiler alquiler, LocalDate fechaDevolucion);

    void borrarAlquiler(Alquiler alquiler);

    void terminar();
}