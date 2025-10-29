package org.rubennicolas.alquilervehiculos.modelo.negocio.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Autobus;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Furgoneta;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehiculosMongoDBIT {

    @Container
    private static final MongoDBContainer mongoContainer =
            new MongoDBContainer("mongo:6.0")
                    .withExposedPorts(27017);

    private MongoDatabase database;
    private MongoCollection<Document> coleccion;
    private Vehiculos vehiculos;

    @BeforeAll
    void setUpContainer() {
        mongoContainer.start();

        var mongoClient = com.mongodb.client.MongoClients.create(
                mongoContainer.getConnectionString()
        );
        database = mongoClient.getDatabase("alquiler_vehiculos_test");
        coleccion = database.getCollection("vehiculos");
        vehiculos = new Vehiculos(coleccion);
    }

    @BeforeEach
    void limpiarColeccion() {
        coleccion.deleteMany(new Document());
    }

    // ---- TESTS ----

    @Test
    @DisplayName("Debe insertar y recuperar un Turismo correctamente")
    void testInsertarYBuscarTurismo() {
        Vehiculo turismo = new Turismo("Toyota", "Corolla", "1234BBC", 1600);

        vehiculos.insertarVehiculo(turismo);

        List<Vehiculo> lista = vehiculos.getVehiculos();
        assertEquals(1, lista.size());
        assertTrue(lista.get(0) instanceof Turismo);
        assertEquals("Toyota", lista.get(0).getMarca());

        Vehiculo buscado = vehiculos.buscarVehiculo(turismo);
        assertNotNull(buscado);
        assertEquals("1234BBC", buscado.getMatricula());
    }

    @Test
    @DisplayName("Debe insertar y recuperar una Furgoneta correctamente")
    void testInsertarYBuscarFurgoneta() {
        Vehiculo furgoneta = new Furgoneta("Renault", "Kangoo", "2222BBB", 5, 800);
        vehiculos.insertarVehiculo(furgoneta);

        Vehiculo buscado = vehiculos.buscarVehiculo(furgoneta);
        assertNotNull(buscado);
        assertTrue(buscado instanceof Furgoneta);
        assertEquals(800, ((Furgoneta) buscado).getPma());
    }

    @Test
    @DisplayName("Debe insertar y recuperar un Autobus correctamente")
    void testInsertarYBuscarAutobus() {
        Vehiculo autobus = new Autobus("Mercedes", "Sprinter", "3333CCC", 30);
        vehiculos.insertarVehiculo(autobus);

        Vehiculo buscado = vehiculos.buscarVehiculo(autobus);
        assertNotNull(buscado);
        assertTrue(buscado instanceof Autobus);
        assertEquals(30, ((Autobus) buscado).getPlazas());
    }

    @Test
    @DisplayName("Debe modificar correctamente un vehículo existente")
    void testModificarVehiculo() {
        Vehiculo turismo = new Turismo("Toyota", "Corolla", "4444DDD", 1600);
        vehiculos.insertarVehiculo(turismo);

        Vehiculo turismoMod = new Turismo("Toyota", "Corolla Hybrid", "4444DDD", 1800);
        vehiculos.modificarVehiculo(turismoMod);

        Vehiculo buscado = vehiculos.buscarVehiculo(turismo);
        assertNotNull(buscado);
        assertTrue(buscado instanceof Turismo);
        assertEquals(1800, ((Turismo) buscado).getCilindrada());
        assertEquals("Corolla Hybrid", buscado.getModelo());
    }

    @Test
    @DisplayName("Debe lanzar excepción al insertar un vehículo duplicado")
    void testInsertarDuplicado() {
        Vehiculo turismo = new Turismo("Seat", "Ibiza", "5555EEE", 1200);
        vehiculos.insertarVehiculo(turismo);

        assertThrows(DomainException.class,
                () -> vehiculos.insertarVehiculo(turismo),
                "Debe lanzar excepción si la matrícula ya existe");
    }

    @Test
    @DisplayName("Debe borrar correctamente un vehículo existente")
    void testBorrarVehiculo() {
        Vehiculo turismo = new Turismo("Ford", "Focus", "6666FFF", 1600);
        vehiculos.insertarVehiculo(turismo);

        assertEquals(1, vehiculos.getVehiculos().size());

        vehiculos.borrarVehiculo(turismo);
        assertTrue(vehiculos.getVehiculos().isEmpty());
    }

    @AfterAll
    void tearDown() {
        mongoContainer.stop();
    }
}
