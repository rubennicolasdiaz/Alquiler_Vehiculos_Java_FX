package org.rubennicolas.alquilervehiculos.modelo.negocio.mysql;

import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Autobus;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Furgoneta;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IVehiculos;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.ConexionMySQL;
import org.rubennicolas.alquilervehiculos.vista.texto.TipoVehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Vehiculos implements IVehiculos {

    private final ConexionMySQL conexionMySQL;

    // --- Constructor de producción ---
    public Vehiculos() {
        this(new ConexionMySQL());
    }

    // --- Constructor alternativo (para inyección o test) ---
    public Vehiculos(ConexionMySQL conexionMySQL) {
        this.conexionMySQL = conexionMySQL;
    }

    @Override
    public void comenzar() {
        // Sin conexión persistente
    }

    @Override
    public List<Vehiculo> getVehiculos() {
        String sql = "SELECT marca, modelo, matricula, tipo_vehiculo, cilindrada, plazas, pma FROM vehiculos";
        List<Vehiculo> vehiculos = new ArrayList<>();

        try (Connection connection = conexionMySQL.abrirConexion();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehiculos.add(resultSetToVehiculo(rs));
            }

            return vehiculos.stream()
                    .sorted(Comparator.comparing(Vehiculo::getMarca)
                            .thenComparing(Vehiculo::getModelo))
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            throw new DomainException("Error al obtener los vehículos: " + e.getMessage());
        }
    }

    @Override
    public Vehiculo buscarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new NullPointerException("No se puede buscar un vehículo nulo.");
        }

        String sql = "SELECT marca, modelo, matricula, tipo_vehiculo, cilindrada, plazas, pma FROM vehiculos WHERE matricula = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setString(1, vehiculo.getMatricula());
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? resultSetToVehiculo(rs) : null;
            }

        } catch (SQLException e) {
            throw new DomainException("Error al buscar el vehículo: " + e.getMessage());
        }
    }

    @Override
    public void insertarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new NullPointerException("No se puede insertar un vehículo nulo.");
        }

        if (buscarVehiculo(vehiculo) != null) {
            throw new DomainException("Ya existe un vehículo con esa matrícula.");
        }

        String sql = "INSERT INTO vehiculos (marca, modelo, matricula, tipo_vehiculo, cilindrada, plazas, pma) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            mapVehiculoToPreparedStatement(vehiculo, pst);
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new DomainException("Error al insertar vehículo: " + e.getMessage());
        }
    }

    @Override
    public void modificarVehiculo(Vehiculo vehiculo) {

        if (vehiculo == null) {
            throw new NullPointerException("No se puede modificar un vehículo nulo.");
        }

        if (buscarVehiculo(vehiculo) == null) {
            throw new DomainException("No existe ningún vehículo con esa matrícula.");
        }

        String sql = "UPDATE vehiculos SET marca = ?, modelo = ?, matricula = ?, tipo_vehiculo = ?, cilindrada = ?, plazas = ?, pma = ? WHERE matricula = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            mapVehiculoToPreparedStatement(vehiculo, pst);
            pst.setString(8, vehiculo.getMatricula());
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new DomainException("Error al modificar vehículo: " + e.getMessage());
        }
    }

    @Override
    public void borrarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new NullPointerException("No se puede borrar un vehículo nulo.");
        }

        if (buscarVehiculo(vehiculo) == null) {
            throw new DomainException("No existe ningún vehículo con esa matrícula.");
        }

        String sql = "DELETE FROM vehiculos WHERE matricula = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setString(1, vehiculo.getMatricula());
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new DomainException("Error al borrar vehículo: " + e.getMessage());
        }
    }

    @Override
    public void terminar() {
        // No se mantiene conexión abierta
    }

    // --- Métodos auxiliares privados ---

    private Vehiculo resultSetToVehiculo(ResultSet rs) throws SQLException {
        String marca = rs.getString("marca");
        String modelo = rs.getString("modelo");
        String matricula = rs.getString("matricula");
        TipoVehiculo tipo = TipoVehiculo.valueOf(rs.getString("tipo_vehiculo"));

        return switch (tipo) {
            case TURISMO -> new Turismo(marca, modelo, matricula, rs.getInt("cilindrada"));
            case FURGONETA -> new Furgoneta(marca, modelo, matricula, rs.getInt("plazas"), rs.getInt("pma"));
            case AUTOBUS -> new Autobus(marca, modelo, matricula, rs.getInt("plazas"));
        };
    }

    private void mapVehiculoToPreparedStatement(Vehiculo vehiculo, PreparedStatement pst) throws SQLException {
        pst.setString(1, vehiculo.getMarca());
        pst.setString(2, vehiculo.getModelo());
        pst.setString(3, vehiculo.getMatricula());
        pst.setString(4, vehiculo.getTipoVehiculo().name().toUpperCase());

        if (vehiculo instanceof Turismo turismo) {
            pst.setInt(5, turismo.getCilindrada());
            pst.setNull(6, Types.INTEGER);
            pst.setNull(7, Types.INTEGER);
        } else if (vehiculo instanceof Furgoneta f) {
            pst.setNull(5, Types.INTEGER);
            pst.setInt(6, f.getPlazas());
            pst.setInt(7, f.getPma());
        } else if (vehiculo instanceof Autobus a) {
            pst.setNull(5, Types.INTEGER);
            pst.setInt(6, a.getPlazas());
            pst.setNull(7, Types.INTEGER);
        }
    }
}