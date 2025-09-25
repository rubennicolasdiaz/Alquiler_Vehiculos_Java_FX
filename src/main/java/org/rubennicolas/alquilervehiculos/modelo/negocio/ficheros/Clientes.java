package org.rubennicolas.alquilervehiculos.modelo.negocio.ficheros;

import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IClientes;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesXml;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Clientes implements IClientes {

    private static Clientes instancia;
    private static List<Cliente> coleccionClientes;

    private Clientes() {

        coleccionClientes = new ArrayList<>();
        coleccionClientes = UtilidadesXml.leerXmlClientes();
    }

    public static Clientes getInstancia() {

        if (instancia == null) {
            instancia = new Clientes(); // Crear la instancia sólo si aún no se ha creado
        }
        return instancia;
    }

    @Override
    public void comenzar() {
        getInstancia();
    }

    @Override
    public List<Cliente> getClientes() {
        return coleccionClientes
                .stream()
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
            } else if (coleccionClientes.contains(cliente)) {
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

            if (cliente.getNombreApellidos() != null && cliente.getTelefono() != null && cliente.getEmail() != null) {
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

            int indice = coleccionClientes.indexOf(clienteBuscado);

            if (indice == -1) {
                throw new OperationNotSupportedException("No existe ningún cliente con ese DNI.");
            } else {
                coleccionClientes.remove(clienteBuscado);
            }
        } catch (NullPointerException | OperationNotSupportedException e) {
            throw new DomainException(e.getMessage());
        }
    }

    @Override
    public void terminar() {
        try {
            UtilidadesXml.escribirXmlClientes(coleccionClientes);
        } catch (ParserConfigurationException | TransformerException e) {
            throw new DomainException(e.getMessage());
        }
    }
}