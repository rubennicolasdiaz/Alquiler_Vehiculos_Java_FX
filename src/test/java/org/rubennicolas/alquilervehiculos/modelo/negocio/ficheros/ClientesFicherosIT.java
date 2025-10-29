package org.rubennicolas.alquilervehiculos.modelo.negocio.ficheros;

import org.junit.jupiter.api.*;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientesFicherosIT {

    private File ficheroTemporal;
    private Clientes clientes;

    // Simulan la lectura y escritura reales, pero en un fichero temporal
    private Supplier<List<Cliente>> lectorSimulado;
    private Consumer<List<Cliente>> escritorSimulado;

    @BeforeAll
    void prepararEntorno() throws Exception {
        ficheroTemporal = File.createTempFile("clientes-test", ".xml");
        ficheroTemporal.deleteOnExit();
    }

    @BeforeEach
    void setUp() {
        // Inicialmente, sin datos
        List<Cliente> datosIniciales = new ArrayList<>();

        lectorSimulado = () -> new ArrayList<>(datosIniciales);
        escritorSimulado = lista -> {
            try {
                // simular persistencia en fichero XML (sin depender de UtilidadesFicheros)
                Files.writeString(ficheroTemporal.toPath(), lista.toString());
            } catch (Exception e) {
                throw new DomainException("Error al escribir: " + e.getMessage());
            }
        };

        clientes = new Clientes(lectorSimulado, escritorSimulado);
    }

    @Test
    @DisplayName("Debería insertar, buscar y persistir clientes correctamente")
    void testInsertarBuscarYGuardarCliente() {
        Cliente cliente = new Cliente("Rubén Nicolás", "75722433Q", "600000000", "ruben@mail.com");

        clientes.insertarCliente(cliente);
        assertEquals(1, clientes.getClientes().size());

        Cliente buscado = clientes.buscarCliente(cliente);
        assertNotNull(buscado);
        assertEquals("75722433Q", buscado.getDni());

        // Persistimos en "fichero"
        clientes.terminar();

        // Comprobamos que el fichero temporal tiene contenido
        assertTrue(ficheroTemporal.length() > 0, "El fichero temporal no debería estar vacío");
    }

    @Test
    @DisplayName("No debería permitir insertar un cliente duplicado")
    void testInsertarDuplicadoLanzaExcepcion() {
        Cliente cliente = new Cliente("Rubén Nicolás", "75722433Q", "600000000", "ruben@mail.com");
        clientes.insertarCliente(cliente);

        DomainException ex = assertThrows(DomainException.class, () -> clientes.insertarCliente(cliente));
        assertTrue(ex.getMessage().contains("Ya existe un cliente con ese DNI"));
    }

    @Test
    @DisplayName("Debería modificar correctamente un cliente existente")
    void testModificarCliente() {
        Cliente cliente = new Cliente("Juan Pérez", "75722433Q", "600000001", "juan@mail.com");
        clientes.insertarCliente(cliente);

        Cliente clienteModificado = new Cliente("Juan Pérez Actualizado", "75722433Q", "600999999", "nuevo@mail.com");
        clientes.modificarCliente(clienteModificado);

        Cliente resultado = clientes.buscarCliente(clienteModificado);

        assertEquals("Juan Pérez Actualizado", resultado.getNombreApellidos());
        assertEquals("nuevo@mail.com", resultado.getEmail());
    }

    @Test
    @DisplayName("Debería borrar correctamente un cliente existente")
    void testBorrarCliente() {
        Cliente cliente = new Cliente("Laura García", "75722433Q", "666555444", "laura@mail.com");
        clientes.insertarCliente(cliente);
        assertEquals(1, clientes.getClientes().size());

        clientes.borrarCliente(cliente);
        assertTrue(clientes.getClientes().isEmpty());
    }
}
