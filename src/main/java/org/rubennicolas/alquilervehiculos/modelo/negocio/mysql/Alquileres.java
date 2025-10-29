package org.rubennicolas.alquilervehiculos.modelo.negocio.mysql;

import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.*;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IAlquileres;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.ConexionMySQL;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesAlquiler.comprobarAlquiler;
import static org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesAlquiler.comprobarDevolucion;

public class Alquileres implements IAlquileres {

    private final ConexionMySQL conexionMySQL;
    private final Function<String, Cliente> buscadorCliente;
    private final Function<String, Vehiculo> buscadorVehiculo;

    // --- Constructor principal (producción) ---
    public Alquileres() {
        this(
                new ConexionMySQL(),
                dni -> new Clientes().buscarCliente(new Cliente("tmp", dni, "000000000", "mail@mail.com")),
                matricula -> new Vehiculos().buscarVehiculo(
                        new Turismo("tmp", "tmp", matricula, 1000))
        );
    }

    // --- Constructor completo (para test o inyección manual) ---
    public Alquileres(ConexionMySQL conexionMySQL,
                      Function<String, Cliente> buscadorCliente,
                      Function<String, Vehiculo> buscadorVehiculo) {
        this.conexionMySQL = conexionMySQL;
        this.buscadorCliente = buscadorCliente;
        this.buscadorVehiculo = buscadorVehiculo;
    }

    @Override
    public void comenzar() {
        // Nada que hacer; la conexión se abre en cada operación
    }

    @Override
    public List<Alquiler> getAlquileres() {

        String sql = """
                    SELECT 
                        a.id,
                        a.fechaAlquiler,
                        a.fechaDevolucion,
                        c.dni,
                        c.nombre_apellidos,
                        c.telefono,
                        c.email,
                        v.matricula,
                        v.marca,
                        v.modelo,
                        v.tipo_vehiculo,
                        v.cilindrada,
                        v.plazas,
                        v.pma
                    FROM alquileres a
                    JOIN clientes  c ON a.cliente  = c.dni
                    JOIN vehiculos v ON a.vehiculo = v.matricula
                """;


        List<Alquiler> alquileres = new ArrayList<>();

        try (Connection connection = conexionMySQL.abrirConexion();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Alquiler alquiler = resultSetToAlquilerJoin(rs);
                if (alquiler != null) alquileres.add(alquiler);
            }

            return alquileres.stream()
                    .sorted(Comparator.comparing(Alquiler::getFechaAlquiler)
                            .thenComparing(a -> a.getCliente().getNombreApellidos())
                            .thenComparing(a -> a.getVehiculo().getMatricula()))
                    .toList();

        } catch (SQLException e) {
            throw new DomainException("Error al obtener los alquileres: " + e.getMessage());
        }
    }

    @Override
    public List<Alquiler> getAlquileresPorCliente(Cliente cliente) {
        return getAlquileres().stream()
                .filter(a -> a.getCliente().equals(cliente))
                .toList();
    }

    @Override
    public List<Alquiler> getAlquileresPorVehiculo(Vehiculo vehiculo) {
        return getAlquileres().stream()
                .filter(a -> a.getVehiculo().equals(vehiculo))
                .toList();
    }

    @Override
    public Alquiler buscarAlquiler(int id) {
        String sql = "SELECT * FROM alquileres WHERE id = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            return rs.next() ? resultSetToAlquiler(rs) : null;

        } catch (SQLException e) {
            throw new DomainException("Error al buscar el alquiler: " + e.getMessage());
        }
    }

    @Override
    public void insertarAlquiler(Alquiler alquiler) {
        if (alquiler == null)
            throw new NullPointerException("No se puede insertar un alquiler nulo.");

        comprobarAlquiler(alquiler.getCliente(), alquiler.getVehiculo(),
                alquiler.getFechaAlquiler(), getAlquileres());

        String sql = "INSERT INTO alquileres (id, cliente, vehiculo, fechaAlquiler) VALUES (?, ?, ?, ?)";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setInt(1, alquiler.getId());
            pst.setString(2, alquiler.getCliente().getDni());
            pst.setString(3, alquiler.getVehiculo().getMatricula());
            pst.setDate(4, Date.valueOf(alquiler.getFechaAlquiler()));
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new DomainException("Error al insertar alquiler en MySQL: " + e.getMessage());
        }
    }

    @Override
    public void devolverAlquiler(Alquiler alquiler, LocalDate fechaDevolucion) {
        comprobarDevolucion(alquiler, fechaDevolucion, getAlquileres());
        alquiler.setFechaDevolucion(fechaDevolucion);

        String sql = "UPDATE alquileres SET fechaDevolucion = ? WHERE id = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setDate(1, Date.valueOf(fechaDevolucion));
            pst.setInt(2, alquiler.getId());
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new DomainException("Error al actualizar devolución en MySQL: " + e.getMessage());
        }
    }

    @Override
    public void borrarAlquiler(Alquiler alquiler) {
        if (alquiler == null)
            throw new DomainException("No se puede borrar un alquiler nulo.");

        String sql = "DELETE FROM alquileres WHERE id = ?";

        try (Connection connection = conexionMySQL.abrirConexion();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            pst.setInt(1, alquiler.getId());
            int filas = pst.executeUpdate();

            if (filas == 0)
                throw new DomainException("No existe ningún alquiler con ese ID.");

        } catch (SQLException e) {
            throw new DomainException("Error al borrar alquiler en MySQL: " + e.getMessage());
        }
    }

    @Override
    public void terminar() {
        // No mantenemos conexiones abiertas
    }

    // --- Métodos auxiliares ---

    private Alquiler resultSetToAlquiler(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            String dniCliente = rs.getString("cliente");
            String matriculaVehiculo = rs.getString("vehiculo");
            LocalDate fechaAlquiler = rs.getDate("fechaAlquiler").toLocalDate();
            Date fechaDevSQL = rs.getDate("fechaDevolucion");
            LocalDate fechaDevolucion = (fechaDevSQL != null) ? fechaDevSQL.toLocalDate() : null;

            Cliente cliente = buscadorCliente.apply(dniCliente);
            Vehiculo vehiculo = buscadorVehiculo.apply(matriculaVehiculo);

            if (cliente == null || vehiculo == null)
                return null;

            return (fechaDevolucion == null)
                    ? new Alquiler(id, cliente, vehiculo, fechaAlquiler)
                    : new Alquiler(id, cliente, vehiculo, fechaAlquiler, fechaDevolucion);

        } catch (SQLException e) {
            throw new DomainException("Error al mapear ResultSet a Alquiler: " + e.getMessage());
        }
    }

    private Alquiler resultSetToAlquilerJoin(ResultSet rs) {
        try {
            int id = rs.getInt("id");

            // Fechas
            LocalDate fechaAlquiler = rs.getDate("fechaAlquiler").toLocalDate();
            Date fechaDevSQL = rs.getDate("fechaDevolucion");
            LocalDate fechaDevolucion = (fechaDevSQL != null) ? fechaDevSQL.toLocalDate() : null;

            // Cliente completo
            Cliente cliente = new Cliente(
                    rs.getString("nombre_apellidos"),
                    rs.getString("dni"),
                    rs.getString("telefono"),
                    rs.getString("email")
            );

            // Vehículo completo (según tipo)
            String tipo = rs.getString("tipo_vehiculo");
            Vehiculo vehiculo;

            if (tipo == null) {
                throw new DomainException("El tipo de vehículo no puede ser nulo para el alquiler ID " + id);
            }

            switch (tipo.toUpperCase()) {
                case "TURISMO" -> vehiculo = new Turismo(
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getString("matricula"),
                        rs.getInt("cilindrada")
                );

                case "FURGONETA" -> vehiculo = new Furgoneta(
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getString("matricula"),
                        rs.getInt("plazas"),
                        rs.getInt("pma")
                );

                case "AUTOBUS" -> vehiculo = new Autobus(
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getString("matricula"),
                        rs.getInt("plazas")
                );

                default -> throw new DomainException("Tipo de vehículo desconocido: " + tipo);
            }

            // Crear Alquiler
            return (fechaDevolucion == null)
                    ? new Alquiler(id, cliente, vehiculo, fechaAlquiler)
                    : new Alquiler(id, cliente, vehiculo, fechaAlquiler, fechaDevolucion);

        } catch (SQLException e) {
            throw new DomainException("Error al convertir ResultSet en Alquiler: " + e.getMessage());
        }
    }

}
