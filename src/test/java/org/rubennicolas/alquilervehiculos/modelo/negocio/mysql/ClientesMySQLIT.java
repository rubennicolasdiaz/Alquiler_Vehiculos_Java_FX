package org.rubennicolas.alquilervehiculos.modelo.negocio.mysql;

import org.junit.jupiter.api.*;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.ConexionMySQL;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientesMySQLIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("alquiler_vehiculos_test")
                    .withUsername("root")
                    .withPassword("root");

    private Clientes clientes;
    private ConexionMySQL conexion;

    @BeforeAll
    void setUpDatabase() throws SQLException {
        // 1️⃣ Determinar entorno (Docker o local)
        String configPath;
        if (new java.io.File("/app/config/mysql_test.properties").exists()) {
            configPath = "/app/config/mysql_test.properties"; // para Docker o CI
        } else {
            configPath = "src/main/resources/mysql_test.properties"; // para entorno local
        }

        // 2️⃣ Inyectar propiedades para que ConexionMySQL use la BD de test
        System.setProperty("MYSQL_CONFIG_FILE", configPath);
        System.setProperty("MYSQL_SCHEMA", "alquiler_vehiculos_test");

        // 3️⃣ Arrancar contenedor MySQL (testcontainers crea su instancia aislada)
        mysqlContainer.start();

        System.setProperty("MYSQL_HOST", mysqlContainer.getHost());
        System.setProperty("MYSQL_PORT", mysqlContainer.getMappedPort(3306).toString());
        System.setProperty("MYSQL_USER", mysqlContainer.getUsername());
        System.setProperty("MYSQL_PASS", mysqlContainer.getPassword());

        // 4️⃣ Crear la BD si no existe
        try (Connection conn = DriverManager.getConnection(
                String.format("jdbc:mysql://%s:%s/?user=%s&password=%s",
                        mysqlContainer.getHost(),
                        mysqlContainer.getMappedPort(3306),
                        mysqlContainer.getUsername(),
                        mysqlContainer.getPassword()));
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS alquiler_vehiculos_test;");
        }

        // 5️⃣ Conexión y creación de tabla
        conexion = new ConexionMySQL();
        clientes = new Clientes(conexion);

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
        }
    }


    @BeforeEach
    void limpiarTablas() throws Exception {
        try (Connection conn = conexion.abrirConexion();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
            stmt.executeUpdate("DELETE FROM clientes");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
        }
    }


    // ---------------------------------------------------------
    // 🧩 TESTS CRUD
    // ---------------------------------------------------------

    @Test
    @DisplayName("Debe insertar y recuperar un cliente correctamente")
    void testInsertarYBuscarCliente() {
        Cliente cliente = new Cliente("Ruben Nicolas", "75722433Q", "600000000", "ruben@mail.com");

        clientes.insertarCliente(cliente);
        Cliente buscado = clientes.buscarCliente(cliente);

        assertNotNull(buscado);
        assertEquals(cliente.getDni(), buscado.getDni());
        assertEquals(cliente.getNombreApellidos(), buscado.getNombreApellidos());
    }

    @Test
    @DisplayName("Debe listar clientes correctamente")
    void testGetClientes() {
        clientes.insertarCliente(new Cliente("Cliente Antonio", "75722433Q", "600000001", "a@mail.com"));
        clientes.insertarCliente(new Cliente("Cliente Bernardo", "27532496R", "600000002", "b@mail.com"));

        List<Cliente> lista = clientes.getClientes();
        assertEquals(2, lista.size());
        assertEquals("Cliente Antonio", lista.get(0).getNombreApellidos());
    }

    @Test
    @DisplayName("Debe modificar un cliente correctamente")
    void testModificarCliente() {
        Cliente cliente = new Cliente("Ruben Nicolas", "75722433Q", "600000000", "ruben@mail.com");
        clientes.insertarCliente(cliente);

        Cliente clienteMod = new Cliente("Ruben Nicolas Modificado", "75722433Q", "699999999", "nuevo@mail.com");
        clientes.modificarCliente(clienteMod);

        Cliente buscado = clientes.buscarCliente(clienteMod);
        assertEquals("Ruben Nicolas Modificado", buscado.getNombreApellidos());
        assertEquals("699999999", buscado.getTelefono());
        assertEquals("nuevo@mail.com", buscado.getEmail());
    }

    @Test
    @DisplayName("Debe lanzar excepción al insertar un cliente duplicado")
    void testInsertarDuplicado() {
        Cliente cliente = new Cliente("Cliente Duplicado", "75722433Q", "600000000", "dup@mail.com");
        clientes.insertarCliente(cliente);

        assertThrows(DomainException.class,
                () -> clientes.insertarCliente(cliente),
                "Debe lanzar excepción al insertar un cliente con DNI duplicado");
    }

    @Test
    @DisplayName("Debe borrar un cliente correctamente")
    void testBorrarCliente() {
        Cliente cliente = new Cliente("Cliente Borrar", "75722433Q", "600000009", "borrar@mail.com");
        clientes.insertarCliente(cliente);

        clientes.borrarCliente(cliente);
        Cliente buscado = clientes.buscarCliente(cliente);
        assertNull(buscado);
    }

    @AfterAll
    void tearDown() {
        mysqlContainer.stop();
    }
}