package org.rubennicolas.alquilervehiculos.modelo.negocio.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientesMongoDBIT {

    @Container
    private static final MongoDBContainer mongoContainer =
            new MongoDBContainer("mongo:6.0") // Usa Mongo 6.0
                    .withExposedPorts(27017);

    private MongoDatabase database;
    private MongoCollection<Document> coleccion;
    private Clientes clientes;

    @BeforeAll
    void setUpContainer() {
        mongoContainer.start();

        // Conectamos al contenedor MongoDB temporal
        com.mongodb.client.MongoClient mongoClient =
                com.mongodb.client.MongoClients.create(mongoContainer.getConnectionString());

        database = mongoClient.getDatabase("alquiler_vehiculos_test");
        coleccion = database.getCollection("clientes");
        clientes = new Clientes(coleccion);
    }

    @BeforeEach
    void limpiarColeccion() {
        coleccion.deleteMany(new Document());
    }

    @Test
    @DisplayName("Debería insertar y buscar un cliente correctamente")
    void testInsertarYBuscarCliente() {
        Cliente cliente = new Cliente("Rubén Nicolás", "75722433Q", "600000000", "ruben@mail.com");

        clientes.insertarCliente(cliente);

        List<Cliente> lista = clientes.getClientes();
        assertEquals(1, lista.size());
        assertEquals("Rubén Nicolás", lista.get(0).getNombreApellidos());

        Cliente buscado = clientes.buscarCliente(cliente);
        assertNotNull(buscado);
        assertEquals("75722433Q", buscado.getDni());
    }

    @Test
    @DisplayName("No debería permitir insertar un cliente duplicado")
    void testInsertarDuplicado() {
        Cliente cliente = new Cliente("Rubén Nicolás", "27532496R", "600000000", "ruben@mail.com");
        clientes.insertarCliente(cliente);

        assertThrows(DomainException.class, () -> clientes.insertarCliente(cliente),
                "Debe lanzar excepción si el cliente ya existe");
    }

    @Test
    @DisplayName("Debería modificar correctamente un cliente existente")
    void testModificarCliente() {
        Cliente cliente = new Cliente("Juan Pérez", "75722433Q", "600111222", "juan@mail.com");
        clientes.insertarCliente(cliente);

        Cliente modificado = new Cliente("Juan Pérez Actualizado", "75722433Q", "699999999", "nuevo@mail.com");
        clientes.modificarCliente(modificado);

        Cliente resultado = clientes.buscarCliente(modificado);

        assertNotNull(resultado);
        assertEquals("Juan Pérez Actualizado", resultado.getNombreApellidos());
        assertEquals("699999999", resultado.getTelefono());
        assertEquals("nuevo@mail.com", resultado.getEmail());
    }

    @Test
    @DisplayName("Debería borrar correctamente un cliente existente")
    void testBorrarCliente() {
        Cliente cliente = new Cliente("Laura Gómez", "75722433Q", "611222333", "laura@mail.com");
        clientes.insertarCliente(cliente);

        assertEquals(1, clientes.getClientes().size());
        clientes.borrarCliente(cliente);
        assertTrue(clientes.getClientes().isEmpty());
    }

    @AfterAll
    void tearDown() {
        mongoContainer.stop();
    }
}
