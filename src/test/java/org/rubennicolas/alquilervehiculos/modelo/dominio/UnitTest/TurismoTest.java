package org.rubennicolas.alquilervehiculos.modelo.dominio.UnitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.vista.texto.TipoVehiculo;

import static org.junit.jupiter.api.Assertions.*;

class TurismoTest {

    private Turismo turismo;

    @BeforeEach
    void setUp() {
        turismo = new Turismo("Toyota", "Corolla", "1234BCD", 1800);
    }

    // ----------------- Constructor y getters -----------------
    @Test
    void testConstructorYGetters() {
        assertEquals("Toyota", turismo.getMarca());
        assertEquals("Corolla", turismo.getModelo());
        assertEquals("1234BCD", turismo.getMatricula());
        assertEquals(1800, turismo.getCilindrada());
        assertEquals(TipoVehiculo.TURISMO, turismo.getTipoVehiculo());
    }

    @Test
    void testConstructorCopia() {
        Turismo copia = new Turismo(turismo);
        assertEquals(turismo.getMarca(), copia.getMarca());
        assertEquals(turismo.getModelo(), copia.getModelo());
        assertEquals(turismo.getMatricula(), copia.getMatricula());
        assertEquals(turismo.getCilindrada(), copia.getCilindrada());
        assertNotSame(turismo, copia);
    }

    @Test
    void testConstructorVacio() {
        Turismo t = new Turismo();
        assertEquals(TipoVehiculo.TURISMO, t.getTipoVehiculo());
    }

    // ----------------- Setter de cilindrada -----------------
    @Test
    void testSetCilindradaValida() {
        turismo.setCilindrada(2000);
        assertEquals(2000, turismo.getCilindrada());
    }

    @Test
    void testSetCilindradaNula() {
        assertThrows(NullPointerException.class, () -> turismo.setCilindrada(null));
    }

    @Test
    void testSetCilindradaMenorQueUno() {
        assertThrows(IllegalArgumentException.class, () -> turismo.setCilindrada(0));
    }

    @Test
    void testSetCilindradaMayorQue5000() {
        assertThrows(IllegalArgumentException.class, () -> turismo.setCilindrada(6000));
    }

    // ----------------- Factor precio -----------------
    @Test
    void testGetFactorPrecio() {
        // Fórmula: cilindrada / FACTOR_CILINDRADA
        int esperado = 1800 / 10; // 180
        assertEquals(esperado, turismo.getFactorPrecio());
    }

    // ----------------- toString -----------------
    @Test
    void testToString() {
        String esperado = "Toyota Corolla (1800 CC) - 1234BCD";
        assertEquals(esperado, turismo.toString());
    }

    // ----------------- Herencia de Vehiculo -----------------
    @Test
    void testSetMarcaModeloMatricula() {
        turismo.setMarca("Honda");
        turismo.setModelo("Civic");
        turismo.setMatricula("5678XYZ");

        assertEquals("Honda", turismo.getMarca());
        assertEquals("Civic", turismo.getModelo());
        assertEquals("5678XYZ", turismo.getMatricula());
    }

    @Test
    void testEqualsYHashCodePorMatricula() {
        Turismo otro = new Turismo("Ford", "Focus", "1234BCD", 1600);
        assertEquals(turismo, otro);
        assertEquals(turismo.hashCode(), otro.hashCode());

        Turismo distinto = new Turismo("Ford", "Focus", "5678XYZ", 1600);
        assertNotEquals(turismo, distinto);
    }
}
