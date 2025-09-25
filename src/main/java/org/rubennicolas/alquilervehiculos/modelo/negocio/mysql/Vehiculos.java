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

public class Vehiculos implements IVehiculos {

    private static Vehiculos instancia;
    private final ConexionMySQL conexionMySQL;

    private Vehiculos() {
        conexionMySQL = new ConexionMySQL();
    }

    public static Vehiculos getInstancia() {

        if (instancia == null) {
            instancia = new Vehiculos(); // Crear la instancia sólo si aún no se ha creado
        }
        return instancia;
    }

    @Override
    public void comenzar() {
        getInstancia();
    }

    @Override
    public List<Vehiculo> getVehiculos() {
        List<Vehiculo> vehiculos = new ArrayList<>();

        String sql = "SELECT marca, modelo, matricula, tipo_vehiculo, cilindrada, plazas, pma FROM vehiculos";

        try (Connection connection = conexionMySQL.abrirConexion();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            Vehiculo vehiculo = null;

            String marca;
            String modelo;
            String matricula;
            TipoVehiculo tipoVehiculo;
            int cilindrada;
            int plazas;
            int pma;

            while (rs.next()) {
                marca = rs.getString("marca");
                modelo = rs.getString("modelo");
                matricula = rs.getString("matricula");

                tipoVehiculo = TipoVehiculo.valueOf(rs.getString("tipo_vehiculo"));
                cilindrada = rs.getInt("cilindrada");
                plazas = rs.getInt("plazas");
                pma = rs.getInt("pma");

                if (tipoVehiculo == TipoVehiculo.TURISMO) {
                    vehiculo = new Turismo(marca, modelo, matricula, cilindrada);
                }

                if (tipoVehiculo == TipoVehiculo.FURGONETA) {
                    vehiculo = new Furgoneta(marca, modelo, matricula, plazas, pma);
                }

                if (tipoVehiculo == TipoVehiculo.AUTOBUS) {
                    vehiculo = new Autobus(marca, modelo, matricula, plazas);
                }

                vehiculos.add(vehiculo);
            }
        } catch (SQLException e) {
            throw new DomainException(e.getMessage());
        }
        return vehiculos.stream().sorted
                (Comparator.comparing(Vehiculo::getMarca)
                        .thenComparing(Vehiculo::getModelo)).toList();
    }

    @Override
    public Vehiculo buscarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new NullPointerException("No se puede buscar un vehículo nulo.");
        }

        Vehiculo vehiculoBuscado = null;
        String sql = "SELECT marca, modelo, tipo_vehiculo, cilindrada, plazas, pma " +
                "FROM vehiculos WHERE matricula = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setString(1, vehiculo.getMatricula());

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String marca = rs.getString("marca");
                    String modelo = rs.getString("modelo");

                    // Convertimos tipo_vehiculo a enum
                    String tipoStr = rs.getString("tipo_vehiculo");
                    TipoVehiculo tipoVehiculo = TipoVehiculo.valueOf(tipoStr.toUpperCase());

                    int cilindrada = rs.getInt("cilindrada");
                    int plazas = rs.getInt("plazas");
                    int pma = rs.getInt("pma");

                    switch (tipoVehiculo) {
                        case TURISMO:
                            vehiculoBuscado = new Turismo(marca, modelo, vehiculo.getMatricula(), cilindrada);
                            break;
                        case FURGONETA:
                            vehiculoBuscado = new Furgoneta(marca, modelo, vehiculo.getMatricula(), plazas, pma);
                            break;
                        case AUTOBUS:
                            vehiculoBuscado = new Autobus(marca, modelo, vehiculo.getMatricula(), plazas);
                            break;
                    }
                }
            }

        } catch (SQLException e) {
            throw new DomainException(e.getMessage());
        }
        return vehiculoBuscado;
    }


    @Override
    public void insertarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new NullPointerException("No se puede insertar un vehículo nulo.");
        }

        // Comprobar si ya existe un vehículo con la misma matrícula
        Vehiculo vehiculoExistente = buscarVehiculo(vehiculo);
        if (vehiculoExistente != null) {
            throw new DomainException("Ya existe un vehículo con esa matrícula.");
        }

        String sql = "INSERT INTO vehiculos (marca, modelo, matricula, tipo_vehiculo, cilindrada, plazas, pma) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setString(1, vehiculo.getMarca());
            pst.setString(2, vehiculo.getModelo());
            pst.setString(3, vehiculo.getMatricula());
            pst.setString(4, vehiculo.getTipoVehiculo().name()); // tipo_vehiculo como String

            // Inicializamos campos según tipo de vehículo
            if (vehiculo instanceof Turismo turismo) {
                pst.setInt(5, turismo.getCilindrada());
                pst.setNull(6, java.sql.Types.INTEGER); // plazas no aplican
                pst.setNull(7, java.sql.Types.INTEGER); // pma no aplican
            }

            if (vehiculo instanceof Furgoneta furgoneta) {
                pst.setNull(5, java.sql.Types.INTEGER); // cilindrada no aplica
                pst.setInt(6, furgoneta.getPlazas());
                pst.setInt(7, furgoneta.getPma());
            }

            if (vehiculo instanceof Autobus autobus) {
                pst.setNull(5, java.sql.Types.INTEGER); // cilindrada no aplica
                pst.setInt(6, autobus.getPlazas());
                pst.setNull(7, java.sql.Types.INTEGER); // pma no aplica
            }

            int filasInsertadas = pst.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("Vehículo insertado correctamente en la base de datos.");
            }

        } catch (SQLException e) {
            throw new DomainException("No se pudo insertar el vehículo en la base de datos.");
        }
    }


    @Override
    public void modificarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new NullPointerException("No se puede modificar un vehículo nulo.");
        }

        // Comprobar si el vehículo existe
        Vehiculo vehiculoExistente = buscarVehiculo(vehiculo);
        if (vehiculoExistente == null) {
            throw new IllegalArgumentException("No existe ningún vehículo con esa matrícula.");
        }

        String sql = "UPDATE vehiculos SET marca = ?, modelo = ?, tipo_vehiculo = ?, cilindrada = ?, plazas = ?, pma = ? " +
                "WHERE matricula = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setString(1, vehiculo.getMarca());
            pst.setString(2, vehiculo.getModelo());
            pst.setString(3, vehiculo.getTipoVehiculo().name());

            // Inicializamos campos según tipo de vehículo
            if (vehiculo instanceof Turismo turismo) {
                pst.setInt(4, turismo.getCilindrada());
                pst.setNull(5, java.sql.Types.INTEGER); // plazas no aplican
                pst.setNull(6, java.sql.Types.INTEGER); // pma no aplican
            } else if (vehiculo instanceof Furgoneta furgoneta) {
                pst.setNull(4, java.sql.Types.INTEGER); // cilindrada no aplica
                pst.setInt(5, furgoneta.getPlazas());
                pst.setInt(6, furgoneta.getPma());
            } else if (vehiculo instanceof Autobus autobus) {
                pst.setNull(4, java.sql.Types.INTEGER); // cilindrada no aplica
                pst.setInt(5, autobus.getPlazas());
                pst.setNull(6, java.sql.Types.INTEGER); // pma no aplica
            } else {
                throw new DomainException("Tipo de vehículo no reconocido.");
            }

            pst.setString(7, vehiculo.getMatricula());

            int filasActualizadas = pst.executeUpdate();
            if (filasActualizadas > 0) {
                System.out.println("Vehículo modificado correctamente en la base de datos.");
            } else {
                throw new RuntimeException("No se pudo modificar el vehículo.");
            }

        } catch (SQLException e) {
            throw new DomainException("No se pudo modificar el vehículo en la base de datos.");
        }
    }

    @Override
    public void borrarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new NullPointerException("No se puede borrar un vehículo nulo.");
        }

        // Comprobar si el vehículo existe
        Vehiculo vehiculoExistente = buscarVehiculo(vehiculo);
        if (vehiculoExistente == null) {
            throw new IllegalArgumentException("No existe ningún vehículo con esa matrícula.");
        }

        String sql = "DELETE FROM vehiculos WHERE matricula = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setString(1, vehiculo.getMatricula());

            int filasEliminadas = pst.executeUpdate();
            if (filasEliminadas > 0) {
                System.out.println("Vehículo eliminado correctamente de la base de datos.");
            } else {
                throw new RuntimeException("No se pudo eliminar el vehículo.");
            }

        } catch (SQLException e) {
            throw new DomainException("No se pudo eliminar el vehículo en la base de datos.");
        }
    }

    @Override
    public void terminar() {

    }
}