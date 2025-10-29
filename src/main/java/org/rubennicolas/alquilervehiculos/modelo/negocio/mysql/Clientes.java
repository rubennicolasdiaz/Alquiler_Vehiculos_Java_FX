package org.rubennicolas.alquilervehiculos.modelo.negocio.mysql;

import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IClientes;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.ConexionMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Clientes implements IClientes {

    private final ConexionMySQL conexionMySQL;

    // --- Constructor principal (producción) ---
    public Clientes() {
        this(new ConexionMySQL());
    }

    // --- Constructor alternativo (para test o inyección manual) ---
    public Clientes(ConexionMySQL conexionMySQL) {
        this.conexionMySQL = conexionMySQL;
    }

    @Override
    public void comenzar() {
        // No mantiene conexiones persistentes
    }

    @Override
    public List<Cliente> getClientes() {
        String sql = "SELECT nombre_apellidos, dni, telefono, email FROM clientes";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection connection = conexionMySQL.abrirConexion();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(resultSetToCliente(rs));
            }

            return clientes.stream()
                    .sorted(Comparator.comparing(Cliente::getNombreApellidos))
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            throw new DomainException("Error al obtener los clientes: " + e.getMessage());
        }
    }

    @Override
    public Cliente buscarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("No se puede buscar un cliente nulo.");
        }

        String sql = "SELECT nombre_apellidos, dni, telefono, email FROM clientes WHERE dni = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setString(1, cliente.getDni());
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? resultSetToCliente(rs) : null;
            }

        } catch (SQLException e) {
            throw new DomainException("Error al buscar cliente: " + e.getMessage());
        }
    }

    @Override
    public void insertarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("No se puede insertar un cliente nulo.");
        }

        if (buscarCliente(cliente) != null) {
            throw new DomainException("Ya existe un cliente con ese DNI.");
        }

        String sql = "INSERT INTO clientes (nombre_apellidos, dni, telefono, email) VALUES (?, ?, ?, ?)";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setString(1, cliente.getNombreApellidos());
            pst.setString(2, cliente.getDni());
            pst.setString(3, cliente.getTelefono());
            pst.setString(4, cliente.getEmail());
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new DomainException("Error al insertar cliente en la base de datos: " + e.getMessage());
        }
    }

    @Override
    public void modificarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("No se puede modificar un cliente nulo.");
        }

        if (buscarCliente(cliente) == null) {
            throw new DomainException("No existe ningún cliente con ese DNI.");
        }

        String sql = "UPDATE clientes SET nombre_apellidos = ?, telefono = ?, email = ? WHERE dni = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setString(1, cliente.getNombreApellidos());
            pst.setString(2, cliente.getTelefono());
            pst.setString(3, cliente.getEmail());
            pst.setString(4, cliente.getDni());
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new DomainException("Error al modificar cliente en la base de datos: " + e.getMessage());
        }
    }

    @Override
    public void borrarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new NullPointerException("No se puede borrar un cliente nulo.");
        }

        if (buscarCliente(cliente) == null) {
            throw new DomainException("No existe ningún cliente con ese DNI.");
        }

        String sql = "DELETE FROM clientes WHERE dni = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setString(1, cliente.getDni());
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new DomainException("Error al borrar cliente en la base de datos: " + e.getMessage());
        }
    }

    @Override
    public void terminar() {
        // Sin conexiones persistentes, nada que cerrar
    }

    // --- Método auxiliar ---
    private Cliente resultSetToCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getString("nombre_apellidos"),
                rs.getString("dni"),
                rs.getString("telefono"),
                rs.getString("email")
        );
    }
}