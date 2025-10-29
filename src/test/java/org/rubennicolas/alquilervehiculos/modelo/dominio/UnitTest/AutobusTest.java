package org.rubennicolas.alquilervehiculos.modelo.dominio.UnitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Autobus;
import org.rubennicolas.alquilervehiculos.vista.texto.TipoVehiculo;

import static org.junit.jupiter.api.Assertions.*;

class AutobusTest {

    private Autobus autobus;

    @BeforeEach
    void setUp() {
        autobus = new Autobus("Mercedes", "Sprinter", "1234BCD", 30);
    }

    // ----------------- Constructor y getters -----------------
    @Test
    void testConstructorYGetters() {
        assertEquals("Mercedes", autobus.getMarca());
        assertEquals("Sprinter", autobus.getModelo());
        assertEquals("1234BCD", autobus.getMatricula());
        assertEquals(30, autobus.getPlazas());
        assertEquals(TipoVehiculo.AUTOBUS, autobus.getTipoVehiculo());
    }

    @Test
    void testConstructorCopia() {
        Autobus copia = new Autobus(autobus);
        assertEquals(autobus.getMarca(), copia.getMarca());
        assertEquals(autobus.getModelo(), copia.getModelo());
        assertEquals(autobus.getMatricula(), copia.getMatricula());
        assertEquals(autobus.getPlazas(), copia.getPlazas());
        assertNotSame(autobus, copia);
    }

    @Test
    void testConstructorVacio() {
        Autobus a = new Autobus();
        assertEquals(TipoVehiculo.AUTOBUS, a.getTipoVehiculo());
    }

    // ----------------- Setter de plazas -----------------
    @Test
    void testSetPlazasValido() {
        autobus.setPlazas(25);
        assertEquals(25, autobus.getPlazas());
    }

    @Test
    void testSetPlazasNulo() {
        assertThrows(NullPointerException.class, () -> autobus.setPlazas(null));
    }

    @Test
    void testSetPlazasMenorQueUno() {
        assertThrows(IllegalArgumentException.class, () -> autobus.setPlazas(0));
    }

    @Test
    void testSetPlazasMayorQueCincuenta() {
        assertThrows(IllegalArgumentException.class, () -> autobus.setPlazas(51));
    }

    // ----------------- Factor precio -----------------
    @Test
    void testGetFactorPrecio() {
        // FACTOR_PLAZAS = 2
        assertEquals(30 * 2, autobus.getFactorPrecio());
    }

    // ----------------- toString -----------------
    @Test
    void testToString() {
        String esperado = "Mercedes Sprinter - (30 Plazas) - 1234BCD";
        assertEquals(esperado, autobus.toString());
    }

    // ----------------- Herencia de Vehiculo -----------------
    @Test
    void testSetMarcaModeloMatricula() {
        autobus.setMarca("Volvo");
        autobus.setModelo("9700");
        autobus.setMatricula("5678XYZ");

        assertEquals("Volvo", autobus.getMarca());
        assertEquals("9700", autobus.getModelo());
        assertEquals("5678XYZ", autobus.getMatricula());
    }

    @Test
    void testEqualsYHashCodePorMatricula() {
        Autobus otro = new Autobus("Scania", "Irizar", "1234BCD", 40);
        assertEquals(autobus, otro);
        assertEquals(autobus.hashCode(), otro.hashCode());

        Autobus distinto = new Autobus("Scania", "Irizar", "5678XYZ", 40);
        assertNotEquals(autobus, distinto);
    }
}
