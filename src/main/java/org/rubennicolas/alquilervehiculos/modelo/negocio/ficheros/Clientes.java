package org.rubennicolas.alquilervehiculos.modelo.negocio.ficheros;

import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IClientes;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesFicheros;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Clientes implements IClientes {

    private final List<Cliente> coleccionClientes;
    private final Supplier<List<Cliente>> lector;
    private final Consumer<List<Cliente>> escritor;

    // --- Constructor de producción ---
    public Clientes() {
        this(UtilidadesFicheros::leerXmlClientes, UtilidadesFicheros::escribirXmlClientes);
    }

    // --- Constructor de test (inyección de dependencias) ---
    public Clientes(Supplier<List<Cliente>> lector, Consumer<List<Cliente>> escritor) {
        this.lector = lector;
        this.escritor = escritor;
        this.coleccionClientes = new ArrayList<>();

        try {
            List<Cliente> clientesLeidos = lector.get();
            coleccionClientes.addAll(clientesLeidos);
        } catch (Exception e) {
            throw new DomainException("Error al leer los clientes: " + e.getMessage());
        }
    }

    @Override
    public void comenzar() {
        // No hace nada: ya se inicializa al construir la clase
    }

    @Override
    public List<Cliente> getClientes() {
        return coleccionClientes.stream()
                .sorted(Comparator.comparing(Cliente::getNombreApellidos))
                .toList();
    }

    @Override
    public Cliente buscarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("No se puede buscar un cliente nulo.");
        }

        return coleccionClientes.stream()
                .filter(cli -> cli.equals(cliente))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void insertarCliente(Cliente cliente) {
        try {
            if (cliente == null) {
                throw new NullPointerException("No se puede insertar un cliente nulo.");
            }
            if (coleccionClientes.contains(cliente)) {
                throw new OperationNotSupportedException("Ya existe un cliente con ese DNI.");
            }
            coleccionClientes.add(cliente);
        } catch (NullPointerException | OperationNotSupportedException e) {
            throw new DomainException(e.getMessage());
        }
    }

    @Override
    public void modificarCliente(Cliente cliente) {
        Cliente clienteBuscado = buscarCliente(cliente);

        try {
            if (clienteBuscado == null) {
                throw new NullPointerException("No se puede modificar un cliente nulo.");
            }

            if (!coleccionClientes.contains(clienteBuscado)) {
                throw new OperationNotSupportedException("No existe ningún cliente con ese DNI.");
            }

            if (cliente.getNombreApellidos() != null &&
                    cliente.getTelefono() != null &&
                    cliente.getEmail() != null) {

                clienteBuscado.setNombreApellidos(cliente.getNombreApellidos());
                clienteBuscado.setTelefono(cliente.getTelefono());
                clienteBuscado.setEmail(cliente.getEmail());
            }

        } catch (Exception e) {
            throw new DomainException(e.getMessage());
        }
    }

    @Override
    public void borrarCliente(Cliente cliente) {
        try {
            Cliente clienteBuscado = buscarCliente(cliente);

            if (clienteBuscado == null) {
                throw new NullPointerException("No se puede borrar un cliente nulo.");
            }

            if (!coleccionClientes.remove(clienteBuscado)) {
                throw new OperationNotSupportedException("No existe ningún cliente con ese DNI.");
            }

        } catch (NullPointerException | OperationNotSupportedException e) {
            throw new DomainException(e.getMessage());
        }
    }

    @Override
    public void terminar() {
        try {
            escritor.accept(coleccionClientes);
        } catch (Exception e) {
            throw new DomainException("Error al escribir los clientes: " + e.getMessage());
        }
    }
}
