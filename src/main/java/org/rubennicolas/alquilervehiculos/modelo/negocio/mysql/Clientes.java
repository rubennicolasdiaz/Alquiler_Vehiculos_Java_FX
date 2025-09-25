package org.rubennicolas.alquilervehiculos.modelo.negocio.mysql;

import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IClientes;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.ConexionMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Clientes implements IClientes {

    private static Clientes instancia;
    private final ConexionMySQL conexionMySQL;

    private Clientes() {
        conexionMySQL = new ConexionMySQL();
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
        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT nombre_apellidos, dni, telefono, email FROM clientes";

        try (Connection connection = conexionMySQL.abrirConexion();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getString("nombre_apellidos"),
                        rs.getString("dni"),
                        rs.getString("telefono"),
                        rs.getString("email")
                );
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            throw new DomainException(e.getMessage());
        }
        return clientes.stream().sorted(Comparator.comparing(Cliente::getNombreApellidos)).toList();
    }

    @Override
    public Cliente buscarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("No se puede buscar un cliente nulo.");
        }

        Cliente clienteBuscado = null;
        String sql = "SELECT nombre_apellidos, dni, telefono, email FROM clientes WHERE dni = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setString(1, cliente.getDni());

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    clienteBuscado = new Cliente(
                            rs.getString("nombre_apellidos"),
                            rs.getString("dni"),
                            rs.getString("telefono"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DomainException(e.getMessage());
        }

        return clienteBuscado;
    }

    @Override
    public void insertarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("No se puede insertar un cliente nulo.");
        }

        // Comprobar si ya existe un cliente con el mismo DNI
        Cliente clienteExistente = buscarCliente(cliente);
        if (clienteExistente != null) {
            throw new DomainException("Ya existe un cliente con ese DNI.");
        }

        String sql = "INSERT INTO clientes (nombre_apellidos, dni, telefono, email) VALUES (?, ?, ?, ?)";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setString(1, cliente.getNombreApellidos());
            pst.setString(2, cliente.getDni());
            pst.setString(3, cliente.getTelefono());
            pst.setString(4, cliente.getEmail());

            int filasInsertadas = pst.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("Cliente insertado correctamente en la base de datos.");
            }

        } catch (SQLException e) {
            throw new DomainException("No se pudo insertar el cliente en la base de datos.");
        }
    }

    @Override
    public void modificarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("No se puede modificar un cliente nulo.");
        }

        // Comprobar si el cliente existe
        Cliente clienteExistente = buscarCliente(cliente);
        if (clienteExistente == null) {
            throw new IllegalArgumentException("No existe ningún cliente con ese DNI.");
        }

        String sql = "UPDATE clientes SET nombre_apellidos = ?, telefono = ?, email = ? WHERE dni = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setString(1, cliente.getNombreApellidos());
            pst.setString(2, cliente.getTelefono());
            pst.setString(3, cliente.getEmail());
            pst.setString(4, cliente.getDni());

            int filasActualizadas = pst.executeUpdate();
            if (filasActualizadas > 0) {
                System.out.println("Cliente modificado correctamente en la base de datos.");
            } else {
                throw new IllegalArgumentException("No se pudo modificar el cliente porque no existe.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo modificar el cliente en la base de datos.", e);
        }
    }

    @Override
    public void borrarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("No se puede borrar un cliente nulo.");
        }

        // Comprobar si el cliente existe
        Cliente clienteExistente = buscarCliente(cliente);
        if (clienteExistente == null) {
            throw new IllegalArgumentException("No existe ningún cliente con ese DNI.");
        }

        String sql = "DELETE FROM clientes WHERE dni = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setString(1, cliente.getDni());

            int filasEliminadas = pst.executeUpdate();
            if (filasEliminadas > 0) {
                System.out.println("Cliente eliminado correctamente de la base de datos.");
            } else {
                throw new IllegalArgumentException("No se pudo eliminar el cliente porque no existe.");
            }

        } catch (SQLException e) {
            throw new DomainException("No se pudo eliminar el cliente en la base de datos.");
        }
    }

    @Override
    public void terminar() {

    }
}