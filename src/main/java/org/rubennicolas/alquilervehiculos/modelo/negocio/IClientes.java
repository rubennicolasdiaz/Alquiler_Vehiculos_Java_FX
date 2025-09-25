package org.rubennicolas.alquilervehiculos.modelo.negocio;

import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;

import java.util.List;

public interface IClientes {

    void comenzar();

    List<Cliente> getClientes();

    Cliente buscarCliente(Cliente cliente);

    void insertarCliente(Cliente cliente);

    void modificarCliente(Cliente cliente);

    void borrarCliente(Cliente cliente);

    void terminar();
}