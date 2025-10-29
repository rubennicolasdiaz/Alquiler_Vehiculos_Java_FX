package org.rubennicolas.alquilervehiculos.modelo.negocio.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlquileresMongoDBIT {

    @Container
    private static final MongoDBContainer mongoContainer =
            new MongoDBContainer("mongo:6.0")
                    .withExposedPorts(27017);

    private MongoDatabase database;
    private MongoCollection<Document> colAlquileres;
    private MongoCollection<Document> colClientes;
    private MongoCollection<Document> colVehiculos;
    private Alquileres alquileres;

    private Cliente cliente;
    private Turismo turismo;

    @BeforeAll
    void setUp() {
        mongoContainer.start();

        var mongoClient = com.mongodb.client.MongoClients.create(
                mongoContainer.getConnectionString()
        );
        database = mongoClient.getDatabase("alquiler_vehiculos_test");

        colClientes = database.getCollection("clientes");
        colVehiculos = database.getCollection("vehiculos");
        colAlquileres = database.getCollection("alquileres");

        // Limpieza inicial
        colClientes.deleteMany(new Document());
        colVehiculos.deleteMany(new Document());
        colAlquileres.deleteMany(new Document());

        // Insertar datos base
        cliente = new Cliente("Ruben Nicolas", "75722433Q", "600000000", "ruben@mail.com");
        turismo = new Turismo("Seat", "Ibiza", "1111BBB", 1600);

        colClientes.insertOne(new Document()
                .append("nombre_apellidos", cliente.getNombreApellidos())
                .append("dni", cliente.getDni())
                .append("telefono", cliente.getTelefono())
                .append("email", cliente.getEmail()));

        colVehiculos.insertOne(new Document()
                .append("marca", turismo.getMarca())
                .append("modelo", turismo.getModelo())
                .append("matricula", turismo.getMatricula())
                .append("tipo_vehiculo", turismo.getTipoVehiculo().name())
                .append("cilindrada", turismo.getCilindrada()));

        // Funciones buscadoras simuladas
        Function<String, Cliente> buscadorCliente = dni -> {
            Document doc = colClientes.find(new Document("dni", dni)).first();
            return (doc != null)
                    ? new Cliente(doc.getString("nombre_apellidos"), doc.getString("dni"),
                    doc.getString("telefono"), doc.getString("email"))
                    : null;
        };

        Function<String, Vehiculo> buscadorVehiculo = matricula -> {
            Document doc = colVehiculos.find(new Document("matricula", matricula)).first();
            return (doc != null)
                    ? new Turismo(doc.getString("marca"), doc.getString("modelo"),
                    doc.getString("matricula"), doc.getInteger("cilindrada"))
                    : null;
        };

        alquileres = new Alquileres(colAlquileres, buscadorCliente, buscadorVehiculo);
    }

    @BeforeEach
    void limpiarColecciones() {
        colAlquileres.deleteMany(new Document());
    }

    // ---------------------------------------------------------
    // 🧩 TESTS
    // ---------------------------------------------------------

    @Test
    @DisplayName("Debe insertar y recuperar un alquiler correctamente con lookup")
    void testInsertarYRecuperarAlquiler() {
        Alquiler alquiler = new Alquiler(1, cliente, turismo, LocalDate.now());
        alquileres.insertarAlquiler(alquiler);

        List<Alquiler> lista = alquileres.getAlquileres();
        assertEquals(1, lista.size());

        Alquiler recuperado = lista.get(0);
        assertEquals(cliente.getDni(), recuperado.getCliente().getDni());
        assertEquals(turismo.getMatricula(), recuperado.getVehiculo().getMatricula());
        assertNull(recuperado.getFechaDevolucion());
    }

    @Test
    @DisplayName("Debe devolver un alquiler correctamente (setear fechaDevolucion)")
    void testDevolverAlquiler() {
        Alquiler alquiler = new Alquiler(2, cliente, turismo, LocalDate.now().minusDays(3));
        alquileres.insertarAlquiler(alquiler);

        LocalDate fechaDev = LocalDate.now();
        alquileres.devolverAlquiler(alquiler, fechaDev);

        Alquiler actualizado = alquileres.buscarAlquiler(2);
        assertNotNull(actualizado);
        assertEquals(fechaDev, actualizado.getFechaDevolucion());
    }

    @Test
    @DisplayName("Debe filtrar alquileres por cliente correctamente")
    void testGetAlquileresPorCliente() {

        Cliente otroCliente = new Cliente("Ruben Nicolas", "27532496R", "600000000", "ruben@mail.com");
        Turismo otroTurismo = new Turismo("Seat", "Ibiza", "1112BBB", 1600);

        Alquiler a1 = new Alquiler(10, cliente, turismo, LocalDate.of(2025, 1, 1));
        Alquiler a2 = new Alquiler(11, otroCliente, otroTurismo, LocalDate.of(2025, 2, 1));

        alquileres.insertarAlquiler(a1);
        alquileres.insertarAlquiler(a2);

        List<Alquiler> lista = alquileres.getAlquileresPorCliente(cliente);

        // ✅ Esperamos sólo 1 alquiler del cliente original
        assertEquals(1, lista.size());
        assertTrue(lista.stream().allMatch(a -> a.getCliente().getDni().equals(cliente.getDni())));
    }

    @Test
    @DisplayName("Debe eliminar correctamente un alquiler existente")
    void testBorrarAlquiler() {
        Alquiler a = new Alquiler(5, cliente, turismo, LocalDate.now());
        alquileres.insertarAlquiler(a);
        assertEquals(1, alquileres.getAlquileres().size());

        alquileres.borrarAlquiler(a);
        assertTrue(alquileres.getAlquileres().isEmpty());
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta insertar un alquiler duplicado")
    void testInsertarDuplicado() {
        Alquiler a = new Alquiler(7, cliente, turismo, LocalDate.now());
        alquileres.insertarAlquiler(a);

        assertThrows(DomainException.class,
                () -> alquileres.insertarAlquiler(a),
                "Debe lanzar DomainException por ID duplicado");
    }

    @AfterAll
    void tearDown() {
        mongoContainer.stop();
    }
}
