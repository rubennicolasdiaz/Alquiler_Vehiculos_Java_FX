package org.rubennicolas.alquilervehiculos.modelo.negocio.ficheros;

import org.junit.jupiter.api.*;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Autobus;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Furgoneta;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehiculosFicherosIT {

    private File ficheroTemporal;
    private Vehiculos vehiculos;

    private Supplier<List<Vehiculo>> lectorSimulado;
    private Consumer<List<Vehiculo>> escritorSimulado;

    @BeforeAll
    void prepararEntorno() throws Exception {
        ficheroTemporal = File.createTempFile("vehiculos-test", ".xml");
        ficheroTemporal.deleteOnExit();
    }

    @BeforeEach
    void setUp() {
        List<Vehiculo> datosIniciales = new ArrayList<>();

        lectorSimulado = () -> new ArrayList<>(datosIniciales);
        escritorSimulado = lista -> {
            try {
                Files.writeString(ficheroTemporal.toPath(), lista.toString());
            } catch (Exception e) {
                throw new DomainException("Error al escribir: " + e.getMessage());
            }
        };

        vehiculos = new Vehiculos(lectorSimulado, escritorSimulado);
    }

    @Test
    @DisplayName("Debería insertar, buscar y persistir vehículos correctamente")
    void testInsertarBuscarYGuardarVehiculo() {
        Vehiculo turismo = new Turismo("Ford", "Focus", "1234BBC", 1200);
        vehiculos.insertarVehiculo(turismo);

        assertEquals(1, vehiculos.getVehiculos().size());

        Vehiculo buscado = vehiculos.buscarVehiculo(turismo);
        assertNotNull(buscado);
        assertEquals("1234BBC", buscado.getMatricula());

        vehiculos.terminar();
        assertTrue(ficheroTemporal.length() > 0, "El fichero temporal no debería estar vacío");
    }

    @Test
    @DisplayName("No debería permitir insertar un vehículo duplicado")
    void testInsertarVehiculoDuplicado() {
        Vehiculo turismo = new Turismo("Ford", "Focus", "1234BBC", 1200);
        vehiculos.insertarVehiculo(turismo);

        DomainException ex = assertThrows(DomainException.class, () -> vehiculos.insertarVehiculo(turismo));
        assertTrue(ex.getMessage().contains("Ya existe un vehículo"));
    }

    @Test
    @DisplayName("Debería modificar correctamente un vehículo existente (Turismo)")
    void testModificarVehiculoTurismo() {
        Turismo turismo = new Turismo("Ford", "Focus", "1234BBC", 1200);
        vehiculos.insertarVehiculo(turismo);

        Turismo turismoModificado = new Turismo("Ford", "Focus ST", "1234BBC", 1600);
        vehiculos.modificarVehiculo(turismoModificado);

        Vehiculo resultado = vehiculos.buscarVehiculo(turismoModificado);

        assertTrue(resultado instanceof Turismo);
        assertEquals(1600, ((Turismo) resultado).getCilindrada());
        assertEquals("Focus ST", resultado.getModelo());
    }

    @Test
    @DisplayName("Debería modificar correctamente una Furgoneta")
    void testModificarVehiculoFurgoneta() {
        Furgoneta furgoneta = new Furgoneta("Renault", "Kangoo", "9876XYZ", 5, 500);
        vehiculos.insertarVehiculo(furgoneta);

        Furgoneta modificada = new Furgoneta("Renault", "Kangoo Maxi", "9876XYZ", 7, 800);
        vehiculos.modificarVehiculo(modificada);

        Vehiculo resultado = vehiculos.buscarVehiculo(modificada);

        assertTrue(resultado instanceof Furgoneta);
        Furgoneta resultFurgo = (Furgoneta) resultado;
        assertEquals(7, resultFurgo.getPlazas());
        assertEquals(800, resultFurgo.getPma());
    }

    @Test
    @DisplayName("Debería borrar correctamente un vehículo existente")
    void testBorrarVehiculo() {
        Vehiculo autobus = new Autobus("Mercedes", "Sprinter", "5555BBB", 20);
        vehiculos.insertarVehiculo(autobus);

        assertEquals(1, vehiculos.getVehiculos().size());
        vehiculos.borrarVehiculo(autobus);
        assertTrue(vehiculos.getVehiculos().isEmpty());
    }
}
