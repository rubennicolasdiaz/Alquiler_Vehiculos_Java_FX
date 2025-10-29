package org.rubennicolas.alquilervehiculos.modelo.negocio.ficheros;

import org.junit.jupiter.api.*;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlquileresFicherosIT {

    private File ficheroTemporal;
    private Alquileres alquileres;

    private List<Cliente> clientesMock;
    private List<Vehiculo> vehiculosMock;

    private Cliente cliente;
    private Vehiculo vehiculo;

    @BeforeAll
    void prepararEntorno() throws Exception {
        ficheroTemporal = File.createTempFile("alquileres-test", ".xml");
        ficheroTemporal.deleteOnExit();
    }

    @BeforeEach
    void setUp() {
        // Clientes y vehículos "en memoria"
        cliente = new Cliente("Rubén Nicolás", "75722433Q", "600000000", "ruben@mail.com");
        vehiculo = new Turismo("Ford", "Focus", "1111BBB", 1200);

        clientesMock = new ArrayList<>(List.of(cliente));
        vehiculosMock = new ArrayList<>(List.of(vehiculo));

        // Alquileres iniciales vacíos
        List<Alquiler> alquileresIniciales = new ArrayList<>();

        Supplier<List<Alquiler>> lectorSimulado = () -> new ArrayList<>(alquileresIniciales);
        Consumer<List<Alquiler>> escritorSimulado = lista -> {
            try {
                Files.writeString(ficheroTemporal.toPath(), lista.toString());
            } catch (Exception e) {
                throw new DomainException("Error al escribir: " + e.getMessage());
            }
        };

        // Inyectamos mocks mediante un truco:
        alquileres = new Alquileres(lectorSimulado, escritorSimulado);
        alquileres.coleccionClientes = clientesMock;
        alquileres.coleccionVehiculos = vehiculosMock;
    }

    @Test
    @DisplayName("Debería insertar, buscar y persistir un alquiler correctamente")
    void testInsertarBuscarYGuardarAlquiler() {
        Alquiler alquiler = new Alquiler(1, cliente, vehiculo, LocalDate.of(2025, 10, 1));
        alquileres.insertarAlquiler(alquiler);

        assertEquals(1, alquileres.getAlquileres().size());

        Alquiler buscado = alquileres.buscarAlquiler(1);
        assertNotNull(buscado);
        assertEquals(cliente, buscado.getCliente());
        assertEquals(vehiculo, buscado.getVehiculo());

        alquileres.terminar();
        assertTrue(ficheroTemporal.length() > 0, "El fichero temporal no debería estar vacío");
    }

    @Test
    @DisplayName("No debería permitir insertar un alquiler duplicado del mismo vehículo en la misma fecha")
    void testInsertarAlquilerDuplicado() {
        Alquiler alquiler1 = new Alquiler(1, cliente, vehiculo, LocalDate.of(2025, 10, 1));
        alquileres.insertarAlquiler(alquiler1);

        Alquiler alquilerDuplicado = new Alquiler(2, cliente, vehiculo, LocalDate.of(2025, 10, 1));

        assertThrows(DomainException.class, () -> alquileres.insertarAlquiler(alquilerDuplicado),
                "Debe lanzar excepción al intentar insertar alquiler duplicado");
    }

    @Test
    @DisplayName("Debería devolver correctamente un alquiler y registrar fecha de devolución")
    void testDevolverAlquiler() {
        Alquiler alquiler = new Alquiler(1, cliente, vehiculo, LocalDate.of(2025, 10, 1));
        alquileres.insertarAlquiler(alquiler);

        LocalDate fechaDevolucion = LocalDate.of(2025, 10, 15);
        alquileres.devolverAlquiler(alquiler, fechaDevolucion);

        assertEquals(fechaDevolucion, alquiler.getFechaDevolucion());
    }

    @Test
    @DisplayName("Debería borrar correctamente un alquiler existente")
    void testBorrarAlquiler() {
        Alquiler alquiler = new Alquiler(1, cliente, vehiculo, LocalDate.of(2025, 10, 1));
        alquileres.insertarAlquiler(alquiler);

        assertEquals(1, alquileres.getAlquileres().size());

        alquileres.borrarAlquiler(alquiler);
        assertTrue(alquileres.getAlquileres().isEmpty());
    }

    @Test
    @DisplayName("Debería devolver alquileres filtrados por cliente")
    void testGetAlquileresPorCliente() {
        Cliente otroCliente = new Cliente("Laura", "27532496R", "600111222", "laura@mail.com");
        clientesMock.add(otroCliente);

        Vehiculo otroVehiculo = new Turismo("Seat", "Ibiza", "2222BBB", 1000);
        vehiculosMock.add(otroVehiculo);

        alquileres.insertarAlquiler(new Alquiler(1, cliente, vehiculo, LocalDate.of(2025, 10, 1)));
        alquileres.insertarAlquiler(new Alquiler(2, otroCliente, otroVehiculo, LocalDate.of(2025, 10, 5)));

        List<Alquiler> alquileresCliente = alquileres.getAlquileresPorCliente(cliente);

        assertEquals(1, alquileresCliente.size());
        assertEquals(cliente, alquileresCliente.get(0).getCliente());
    }

    @Test
    @DisplayName("Debería devolver alquileres filtrados por vehículo")
    void testGetAlquileresPorVehiculo() {
        Vehiculo otroVehiculo = new Turismo("Seat", "Ibiza", "9999BBB", 1000);
        vehiculosMock.add(otroVehiculo);

        Cliente cliente2 = new Cliente("Laura", "76660310E", "600111222", "laura@mail.com");
        alquileres.insertarAlquiler(new Alquiler(1, cliente2, vehiculo, LocalDate.of(2025, 10, 1)));
        alquileres.insertarAlquiler(new Alquiler(2, cliente, otroVehiculo, LocalDate.of(2025, 10, 5)));

        List<Alquiler> alquileresVehiculo = alquileres.getAlquileresPorVehiculo(vehiculo);

        assertEquals(1, alquileresVehiculo.size());
        assertEquals(vehiculo, alquileresVehiculo.get(0).getVehiculo());
    }
}
