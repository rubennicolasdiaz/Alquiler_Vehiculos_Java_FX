package org.rubennicolas.alquilervehiculos.controlador;

import org.rubennicolas.alquilervehiculos.modelo.FactoriaFuenteDatos;
import org.rubennicolas.alquilervehiculos.modelo.Modelo;
import org.rubennicolas.alquilervehiculos.modelo.ModeloCascada;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.vista.FactoriaVistas;
import org.rubennicolas.alquilervehiculos.vista.Vista;

import java.time.LocalDate;
import java.util.List;

public class Controlador {

    private final Modelo modelo;
    private final Vista vista;

    private static Controlador instancia;

    public Controlador(FactoriaFuenteDatos fuenteDatos, FactoriaVistas factoriaVistas) {

        this.modelo = new ModeloCascada(fuenteDatos);
        this.vista = factoriaVistas.crear();

        instancia = this;
    }

    public static Controlador getInstancia() {
        return instancia;
    }

    public void comenzar() {
        modelo.comenzar();
        vista.comenzar();
    }

    public void terminar() {
        modelo.terminar();
    }

    public void insertarCliente(Cliente cliente) {
        modelo.insertar(cliente);
    }

    public void insertarVehiculo(Vehiculo vehiculo) {
        modelo.insertar(vehiculo);
    }

    public void insertarAlquiler(Alquiler alquiler) {
        modelo.insertar(alquiler);
    }

    public Cliente buscarCliente(Cliente cliente) {
        return modelo.buscar(cliente);
    }

    public Vehiculo buscarVehiculo(Vehiculo vehiculo) {
        return modelo.buscar(vehiculo);
    }

    public Alquiler buscarAlquiler(int id) {
        return modelo.buscar(id);
    }

    public void modificarCliente(Cliente cliente) {
        modelo.modificar(cliente);
    }

    public void modificarVehiculo(Vehiculo vehiculo) {
        this.modelo.modificar(vehiculo);
    }

    public void devolverAlquiler(Alquiler alquiler, LocalDate fechaDevolucion) {
        modelo.devolver(alquiler, fechaDevolucion);
    }

    public void borrarCliente(Cliente cliente) {
        modelo.borrar(cliente);
    }

    public void borrarVehiculo(Vehiculo vehiculo) {
        modelo.borrar(vehiculo);
    }

    public void borrarAlquiler(Alquiler alquiler) {
        modelo.borrar(alquiler);
    }

    public List<Cliente> getClientes() {
        return modelo.getListaClientes();
    }

    public List<Vehiculo> getVehiculos() {
        return modelo.getListaVehiculos();
    }

    public List<Alquiler> getAlquileres() {
        return modelo.getListaAlquileres();
    }

    public List<Alquiler> getAlquileresPorCliente(Cliente cliente) {
        return modelo.getListaAlquileresPorCliente(cliente);
    }

    public List<Alquiler> getAlquileresPorVehiculo(Vehiculo vehiculo) {
        return modelo.getListaAlquileresPorVehiculo(vehiculo);
    }
}