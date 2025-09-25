package org.rubennicolas.alquilervehiculos.modelo;

import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;

import java.time.LocalDate;
import java.util.List;

public class ModeloCascada extends Modelo {

    /**
     * La clase hija ModeloCascada hereda de la clase padre Modelo. El constructor de la clase
     * hija no hereda implícitamente el constructor padre, por lo que es necesario llamarlo
     * explícitamente con el método super(). Al método super le pasamos el parámetro de tipo
     * factoriaFuenteDatos. El resto de los métodos los hereda de la clase padre y los
     * sobreescribe. Los métodos abstractos de la clase padre han sido desarrollados en la
     * clase hija
     */

    public ModeloCascada(FactoriaFuenteDatos factoriaFuenteDatos) {
        super(factoriaFuenteDatos);
    }

    public void insertar(Cliente cliente) {
        clientes.insertarCliente(cliente);
    }

    public void insertar(Vehiculo vehiculo) {
        vehiculos.insertarVehiculo(vehiculo);
    }

    public void insertar(Alquiler alquiler) {
        alquileres.insertarAlquiler(alquiler);
    }

    public Cliente buscar(Cliente cliente) {
        return clientes.buscarCliente(cliente);
    }

    public Vehiculo buscar(Vehiculo vehiculo) {
        return vehiculos.buscarVehiculo(vehiculo);
    }

    public Alquiler buscar(int id) {
        return alquileres.buscarAlquiler(id);
    }

    public void modificar(Cliente cliente) {
        clientes.modificarCliente(cliente);
    }

    @Override
    public void modificar(Vehiculo vehiculo) {
        vehiculos.modificarVehiculo(vehiculo);
    }

    @Override
    public void devolver(Alquiler alquiler, LocalDate fechaDevolucion) {
        alquileres.devolverAlquiler(alquiler, fechaDevolucion);
    }

    public void borrar(Cliente cliente) {
        clientes.borrarCliente(cliente);
    }

    public void borrar(Vehiculo vehiculo) {
        vehiculos.borrarVehiculo(vehiculo);
    }

    public void borrar(Alquiler alquiler) {
        alquileres.borrarAlquiler(alquiler);
    }

    @Override
    public List<Cliente> getListaClientes() {
        return clientes.getClientes();
    }

    @Override
    public List<Vehiculo> getListaVehiculos() {
        return vehiculos.getVehiculos();
    }

    @Override
    public List<Alquiler> getListaAlquileres() {
        return alquileres.getAlquileres();
    }

    @Override
    public List<Alquiler> getListaAlquileresPorCliente(Cliente cliente) {
        return alquileres.getAlquileresPorCliente(cliente);
    }

    @Override
    public List<Alquiler> getListaAlquileresPorVehiculo(Vehiculo vehiculo) {
        return alquileres.getAlquileresPorVehiculo(vehiculo);
    }
}
