package org.rubennicolas.alquilervehiculos.modelo.negocio.mysql;

import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IAlquileres;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.ConexionMySQL;

import javax.naming.OperationNotSupportedException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesAlquiler.comprobarAlquiler;
import static org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesAlquiler.comprobarDevolucion;

public class Alquileres implements IAlquileres {

    private static Alquileres instancia;
    private final ConexionMySQL conexionMySQL;

    private Alquileres() {
        conexionMySQL = new ConexionMySQL();
    }

    public static Alquileres getInstancia() {

        if (instancia == null) {
            instancia = new Alquileres(); // Crear la instancia sólo si aún no se ha creado
        }
        return instancia;
    }

    @Override
    public void comenzar() {
        getInstancia();
    }

    @Override
    public List<Alquiler> getAlquileres() {

        List<Alquiler> alquileres = new ArrayList<>();

        String sql = "SELECT id, cliente, vehiculo, fechaAlquiler, fechaDevolucion FROM alquileres";

        try (Connection connection = conexionMySQL.abrirConexion();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                int id = rs.getInt("id");
                String dniCliente = rs.getString("cliente");
                String matriculaVehiculo = rs.getString("vehiculo");
                Date fechaAlquilerSQL = rs.getDate("fechaAlquiler");
                Date fechaDevolucionSQL = rs.getDate("fechaDevolucion");

                LocalDate fechaAlquiler = SQLDateToLocalDate(fechaAlquilerSQL);
                LocalDate fechaDevolucion = SQLDateToLocalDate(fechaDevolucionSQL);

                Cliente clienteBuscado = new Cliente("Cliente", dniCliente, "999999999", "mail@mail.com");
                Cliente cliente = Controlador.getInstancia().buscarCliente(clienteBuscado);

                Vehiculo vehiculoBuscado = new Turismo("Marca", "Modelo", matriculaVehiculo, 1000);
                Vehiculo vehiculo = Controlador.getInstancia().buscarVehiculo(vehiculoBuscado);

                if (fechaDevolucion == null) {
                    Alquiler alquiler = new Alquiler(id, cliente, vehiculo, fechaAlquiler);
                    alquileres.add(alquiler);
                } else {
                    Alquiler alquiler = new Alquiler(id, cliente, vehiculo, fechaAlquiler, fechaDevolucion);
                    alquileres.add(alquiler);
                }


            }
        } catch (SQLException e) {
            throw new DomainException(e.getMessage());
        }
        return alquileres.stream()
                .sorted(Comparator.comparing(Alquiler::getFechaAlquiler)
                        .thenComparing(Alquiler::getCliente)
                        .thenComparing(Alquiler::getVehiculo))
                .toList();
    }


    @Override
    public List<Alquiler> getAlquileresPorCliente(Cliente cliente) {
        return getAlquileres().stream()
                .filter(alq -> alq.getCliente().equals(cliente))
                .sorted(Comparator.comparing(Alquiler::getFechaAlquiler)
                        .thenComparing(Alquiler::getCliente)
                        .thenComparing(Alquiler::getVehiculo))
                .toList();
    }

    @Override
    public List<Alquiler> getAlquileresPorVehiculo(Vehiculo vehiculo) {
        return getAlquileres().stream()
                .filter(alq -> alq.getVehiculo().equals(vehiculo))
                .sorted(Comparator.comparing(Alquiler::getFechaAlquiler)
                        .thenComparing(Alquiler::getCliente)
                        .thenComparing(Alquiler::getVehiculo))
                .toList();
    }

    @Override
    public Alquiler buscarAlquiler(int id) {

        return getAlquileres().stream()
                .filter(alquiler -> alquiler.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void insertarAlquiler(Alquiler alquiler) {

        if (alquiler == null) {
            throw new NullPointerException("No se puede insertar un alquiler nulo.");
        }
        comprobarAlquiler(alquiler.getCliente(), alquiler.getVehiculo(),
                alquiler.getFechaAlquiler(), getAlquileres());

        String sql = "INSERT INTO alquileres (id, cliente, vehiculo, fechaAlquiler) VALUES (?, ?, ?, ?)";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setInt(1, alquiler.getId());
            pst.setString(2, alquiler.getCliente().getDni());
            pst.setString(3, alquiler.getVehiculo().getMatricula());
            pst.setDate(4, LocalDateToSQLDate(alquiler.getFechaAlquiler()));

            int filasInsertadas = pst.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("Alquiler insertado correctamente en la base de datos.");
            }
        } catch (SQLException e) {
            throw new DomainException("No se pudo insertar el alquiler en la base de datos.");
        }
    }

    public void devolverAlquiler(Alquiler alquiler, LocalDate fechaDevolucion) {

        comprobarDevolucion(alquiler, fechaDevolucion, getAlquileres());

        alquiler.setFechaDevolucion(fechaDevolucion);

        String sql = "UPDATE alquileres SET fechaDevolucion = ? WHERE id = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {


            // 1. Nuevo valor para la fechaDevolucion
            pst.setDate(1, LocalDateToSQLDate(fechaDevolucion));

            // 2. Identificador del alquiler que quieres actualizar
            pst.setInt(2, alquiler.getId());

            int filasActualizadas = pst.executeUpdate();

            if (filasActualizadas > 0) {
                System.out.println("Fecha de devolución actualizada correctamente en la base de datos.");
            } else {
                System.out.println("No se encontró ningún alquiler con id: " + alquiler.getId());
            }
        } catch (SQLException e) {
            throw new DomainException("No se pudo actualizar la fecha de devolución en la base de datos.");
        }
    }

    @Override
    public void borrarAlquiler(Alquiler alquiler) {

        try {
            if (alquiler == null) {
                throw new NullPointerException("No se puede borrar un alquiler nulo.");
            }
            if (!getAlquileres().contains(alquiler)) {
                throw new OperationNotSupportedException("No existe ningún alquiler igual.");
            }

            String sql = "DELETE FROM alquileres WHERE id = ?";
            try (Connection connection = conexionMySQL.abrirConexion();
                 PreparedStatement pst = connection.prepareStatement(sql)) {

                pst.setInt(1, alquiler.getId());
                int filasBorradas = pst.executeUpdate();

                if (filasBorradas == 0) {
                    throw new DomainException("No se pudo borrar el alquiler de la base de datos.");
                }
            } catch (SQLException e) {
                throw new DomainException("Fallo al conectar o ejecutar el DELETE en la base de datos.");
            }
        } catch (OperationNotSupportedException e) {
            throw new DomainException(e.getMessage());
        }
    }

    @Override
    public void terminar() {

    }

    private static LocalDate SQLDateToLocalDate(Date sqlDate) {
        return (sqlDate == null) ? null : sqlDate.toLocalDate();
    }

    private static Date LocalDateToSQLDate(LocalDate localDate) {
        return (localDate == null) ? null : Date.valueOf(localDate);
    }
}