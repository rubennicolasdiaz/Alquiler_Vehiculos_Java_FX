package org.rubennicolas.alquilervehiculos.modelo.negocio.mysql;

import org.junit.jupiter.api.*;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.ConexionMySQL;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlquileresMySQLIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("alquiler_vehiculos_test")
                    .withUsername("root")
                    .withPassword("root");

    private Alquileres alquileres;
    private ConexionMySQL conexion;
    private Clientes clientes;
    private Vehiculos vehiculos;

    private Cliente cliente;
    private Vehiculo turismo;

    @BeforeAll
    void setUpDatabase() throws Exception {
        // 1️⃣ Determinar entorno
        String configPath;
        if (new java.io.File("/app/config/mysql_test.properties").exists()) {
            configPath = "/app/config/mysql_test.properties";
        } else {
            configPath = "src/main/resources/mysql_test.properties";
        }

        // 2️⃣ Inyectar propiedades
        System.setProperty("MYSQL_CONFIG_FILE", configPath);
        System.setProperty("MYSQL_SCHEMA", "alquiler_vehiculos_test");

        // 3️⃣ Arrancar contenedor
        mysqlContainer.start();

        System.setProperty("MYSQL_HOST", mysqlContainer.getHost());
        System.setProperty("MYSQL_PORT", mysqlContainer.getMappedPort(3306).toString());
        System.setProperty("MYSQL_USER", mysqlContainer.getUsername());
        System.setProperty("MYSQL_PASS", mysqlContainer.getPassword());

        // 4️⃣ Crear BD si no existe
        try (Connection conn = DriverManager.getConnection(
                String.format("jdbc:mysql://%s:%s/?user=%s&password=%s",
                        mysqlContainer.getHost(),
                        mysqlContainer.getMappedPort(3306),
                        mysqlContainer.getUsername(),
                        mysqlContainer.getPassword()));
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS alquiler_vehiculos_test;");
        }

        // 5️⃣ Inicializar conexión
        conexion = new ConexionMySQL();
        clientes = new Clientes(conexion);
        vehiculos = new Vehiculos(conexion);

        // 6️⃣ Crear tablas
        try (Connection conn = conexion.abrirConexion();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS clientes (
                            dni VARCHAR(9) PRIMARY KEY,
                            nombre_apellidos VARCHAR(100) NOT NULL,
                            telefono VARCHAR(20) NOT NULL,
                            email VARCHAR(100) NOT NULL
                        )
                    """);

            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS vehiculos (
                            matricula VARCHAR(10) PRIMARY KEY,
                            marca VARCHAR(50) NOT NULL,
                            modelo VARCHAR(50) NOT NULL,
                            tipo_vehiculo VARCHAR(20) NOT NULL,
                            cilindrada INT,
                            plazas INT,
                            pma INT
                        )
                    """);

            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS alquileres (
                            id INT PRIMARY KEY,
                            cliente VARCHAR(9) NOT NULL,
                            vehiculo VARCHAR(10) NOT NULL,
                            fechaAlquiler DATE NOT NULL,
                            fechaDevolucion DATE,
                            FOREIGN KEY (cliente) REFERENCES clientes(dni) ON DELETE CASCADE,
                            FOREIGN KEY (vehiculo) REFERENCES vehiculos(matricula) ON DELETE CASCADE
                        )
                    """);
        }

        // 7️⃣ Preparar objetos base
        cliente = new Cliente("Antonio Fernández", "27532496R", "600000000", "antonio@mail.com");
        turismo = new Turismo("Fiat", "500", "9998NNM", 2600);
        clientes.insertarCliente(cliente);
        vehiculos.insertarVehiculo(turismo);

        // 8️⃣ Inicializar Alquileres con dependencias
        Function<String, Cliente> buscadorCliente = dni -> clientes.buscarCliente(new Cliente("Tmp", dni, "777777777", "mail@mail.com"));
        Function<String, Vehiculo> buscadorVehiculo = matricula -> vehiculos.buscarVehiculo(new Turismo("Tmp", "Tmp", matricula, 1000));
        alquileres = new Alquileres(conexion, buscadorCliente, buscadorVehiculo);
    }

    @BeforeEach
    void limpiarTablas() throws Exception {
        try (Connection conn = conexion.abrirConexion();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
            stmt.executeUpdate("DELETE FROM alquileres");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    // ---------------------------------------------------------
    // 🧩 TESTS CRUD
    // ---------------------------------------------------------

    @Test
    @DisplayName("Debe insertar y recuperar un alquiler correctamente")
    void testInsertarYBuscarAlquiler() {
        Alquiler alquiler = new Alquiler(1, cliente, turismo, LocalDate.of(2025, 1, 1));
        alquileres.insertarAlquiler(alquiler);

        Alquiler buscado = alquileres.buscarAlquiler(1);
        assertNotNull(buscado);
        assertEquals(alquiler.getCliente().getDni(), buscado.getCliente().getDni());
        assertEquals(alquiler.getVehiculo().getMatricula(), buscado.getVehiculo().getMatricula());
    }

    @Test
    @DisplayName("Debe listar alquileres correctamente")
    void testGetAlquileres() {
        alquileres.insertarAlquiler(new Alquiler(1, cliente, turismo, LocalDate.of(2025, 1, 1)));
        List<Alquiler> lista = alquileres.getAlquileres();

        assertEquals(1, lista.size());
        assertEquals(cliente.getDni(), lista.get(0).getCliente().getDni());
    }

    @Test
    @DisplayName("Debe devolver un alquiler correctamente")
    void testDevolverAlquiler() {
        Alquiler alquiler = new Alquiler(1, cliente, turismo, LocalDate.of(2025, 1, 1));
        alquileres.insertarAlquiler(alquiler);

        alquileres.devolverAlquiler(alquiler, LocalDate.of(2025, 1, 10));

        Alquiler buscado = alquileres.buscarAlquiler(1);
        assertNotNull(buscado.getFechaDevolucion());
        assertEquals(LocalDate.of(2025, 1, 10), buscado.getFechaDevolucion());
    }

    @Test
    @DisplayName("Debe lanzar excepción al insertar alquiler duplicado")
    void testInsertarDuplicado() {
        Alquiler alquiler = new Alquiler(1, cliente, turismo, LocalDate.of(2025, 1, 1));
        alquileres.insertarAlquiler(alquiler);

        assertThrows(DomainException.class,
                () -> alquileres.insertarAlquiler(alquiler),
                "Debe lanzar excepción al insertar alquiler duplicado");
    }

    @Test
    @DisplayName("Debe borrar un alquiler correctamente")
    void testBorrarAlquiler() {
        Alquiler alquiler = new Alquiler(1, cliente, turismo, LocalDate.of(2025, 1, 1));
        alquileres.insertarAlquiler(alquiler);

        alquileres.borrarAlquiler(alquiler);
        Alquiler buscado = alquileres.buscarAlquiler(1);
        assertNull(buscado);
    }

    @AfterAll
    void tearDown() {
        mysqlContainer.stop();
    }
}
