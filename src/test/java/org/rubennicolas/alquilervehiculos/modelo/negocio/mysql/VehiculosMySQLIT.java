package org.rubennicolas.alquilervehiculos.modelo.negocio.mysql;

import org.junit.jupiter.api.*;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Autobus;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Furgoneta;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.ConexionMySQL;
import org.rubennicolas.alquilervehiculos.vista.texto.TipoVehiculo;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehiculosMySQLIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("alquiler_vehiculos_test")
                    .withUsername("root")
                    .withPassword("root");

    private Vehiculos vehiculos;
    private ConexionMySQL conexion;

    @BeforeAll
    void setUpDatabase() throws Exception {

        System.setProperty("MYSQL_CONFIG_FILE", "src/main/resources/mysql_test.properties");
        System.setProperty("MYSQL_SCHEMA", "alquiler_vehiculos_test");
        System.setProperty("MYSQL_CONFIG_FILE", "/app/config/mysql_test.properties");

        mysqlContainer.start();

        // Configurar propiedades que lee ConexionMySQL
        System.setProperty("MYSQL_HOST", mysqlContainer.getHost());
        System.setProperty("MYSQL_PORT", mysqlContainer.getMappedPort(3306).toString());
        System.setProperty("MYSQL_SCHEMA", mysqlContainer.getDatabaseName());
        System.setProperty("MYSQL_USER", mysqlContainer.getUsername());
        System.setProperty("MYSQL_PASS", mysqlContainer.getPassword());

        conexion = new ConexionMySQL();
        vehiculos = new Vehiculos(conexion);

        // Crear tabla de test
        try (Connection conn = conexion.abrirConexion();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS vehiculos (
                            matricula VARCHAR(10) PRIMARY KEY,
                            marca VARCHAR(50) NOT NULL,
                            modelo VARCHAR(50) NOT NULL,
                            tipo_vehiculo ENUM('TURISMO','AUTOBUS','FURGONETA') NOT NULL,
                            cilindrada INT NULL,
                            plazas INT NULL,
                            pma INT NULL
                        )
                    """);
        }
    }

    @BeforeEach
    void limpiarTablas() throws Exception {
        try (Connection conn = conexion.abrirConexion();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
            stmt.executeUpdate("DELETE FROM vehiculos");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    // ---------------------------------------------------------
    // 🧩 TESTS CRUD
    // ---------------------------------------------------------

    @Test
    @DisplayName("Debe insertar y buscar un turismo correctamente")
    void testInsertarYBuscarTurismo() {
        Vehiculo turismo = new Turismo("Seat", "Ibiza", "1234BBC", 1600);

        vehiculos.insertarVehiculo(turismo);
        Vehiculo buscado = vehiculos.buscarVehiculo(turismo);

        assertNotNull(buscado);
        assertEquals(turismo.getMatricula(), buscado.getMatricula());
        assertTrue(buscado instanceof Turismo);
    }

    @Test
    @DisplayName("Debe insertar y listar varios tipos de vehículos")
    void testGetVehiculos() {
        vehiculos.insertarVehiculo(new Turismo("Opel", "Corsa", "1111BBB", 1200));
        vehiculos.insertarVehiculo(new Furgoneta("Citroen", "Berlingo", "2222BBB", 3, 500));
        vehiculos.insertarVehiculo(new Autobus("Mercedes", "Sprinter", "3333CCC", 20));

        List<Vehiculo> lista = vehiculos.getVehiculos();
        assertEquals(3, lista.size());
        assertEquals("Citroen", lista.get(0).getMarca()); // Ordenado por marca
    }

    @Test
    @DisplayName("Debe modificar un vehículo correctamente")
    void testModificarVehiculo() {
        // Insertamos el vehículo original
        Turismo turismo = new Turismo("Seat", "Ibiza", "1234BBC", 1600);
        vehiculos.insertarVehiculo(turismo);

        // Modificamos el mismo objeto
        turismo.setModelo("Leon");
        turismo.setCilindrada(2000);
        turismo.setTipoVehiculo(TipoVehiculo.TURISMO);

        vehiculos.modificarVehiculo(turismo);

        Vehiculo buscado = vehiculos.buscarVehiculo(turismo);
        assertEquals("Leon", buscado.getModelo());
        assertEquals(2000, ((Turismo) buscado).getCilindrada());
    }

    @Test
    @DisplayName("Debe lanzar excepción al insertar vehículo duplicado")
    void testInsertarDuplicado() {
        Vehiculo furgoneta = new Furgoneta("Ford", "Transit", "9999ZZZ", 3, 700);
        vehiculos.insertarVehiculo(furgoneta);

        assertThrows(DomainException.class,
                () -> vehiculos.insertarVehiculo(furgoneta),
                "Debe lanzar excepción al insertar matrícula duplicada");
    }

    @Test
    @DisplayName("Debe borrar un vehículo correctamente")
    void testBorrarVehiculo() {
        Vehiculo autobus = new Autobus("Volvo", "9700", "5555YYY", 50);
        vehiculos.insertarVehiculo(autobus);

        vehiculos.borrarVehiculo(autobus);
        Vehiculo buscado = vehiculos.buscarVehiculo(autobus);
        assertNull(buscado);
    }

    @AfterAll
    void tearDown() {
        mysqlContainer.stop();
    }
}
