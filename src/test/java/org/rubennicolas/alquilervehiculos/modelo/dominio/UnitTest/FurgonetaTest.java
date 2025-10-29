package org.rubennicolas.alquilervehiculos.modelo.dominio.UnitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Furgoneta;
import org.rubennicolas.alquilervehiculos.vista.texto.TipoVehiculo;

import static org.junit.jupiter.api.Assertions.*;

class FurgonetaTest {

    private Furgoneta furgoneta;

    @BeforeEach
    void setUp() {
        furgoneta = new Furgoneta("Ford", "Transit", "1234BCD", 3, 1200);
    }

    // ----------------- Constructores y getters -----------------
    @Test
    void testConstructorYGetters() {
        assertEquals("Ford", furgoneta.getMarca());
        assertEquals("Transit", furgoneta.getModelo());
        assertEquals("1234BCD", furgoneta.getMatricula());
        assertEquals(3, furgoneta.getPlazas());
        assertEquals(1200, furgoneta.getPma());
        assertEquals(TipoVehiculo.FURGONETA, furgoneta.getTipoVehiculo());
    }

    @Test
    void testConstructorCopia() {
        Furgoneta copia = new Furgoneta(furgoneta);
        assertEquals(furgoneta.getMarca(), copia.getMarca());
        assertEquals(furgoneta.getModelo(), copia.getModelo());
        assertEquals(furgoneta.getMatricula(), copia.getMatricula());
        assertEquals(furgoneta.getPlazas(), copia.getPlazas());
        assertEquals(furgoneta.getPma(), copia.getPma());
        assertNotSame(furgoneta, copia);
    }

    @Test
    void testConstructorVacio() {
        Furgoneta f = new Furgoneta();
        assertEquals(TipoVehiculo.FURGONETA, f.getTipoVehiculo());
    }

    // ----------------- Setter de plazas -----------------
    @Test
    void testSetPlazasValido() {
        furgoneta.setPlazas(5);
        assertEquals(5, furgoneta.getPlazas());
    }

    @Test
    void testSetPlazasNulo() {
        assertThrows(NullPointerException.class, () -> furgoneta.setPlazas(null));
    }

    @Test
    void testSetPlazasMenorQueUno() {
        assertThrows(IllegalArgumentException.class, () -> furgoneta.setPlazas(0));
    }

    @Test
    void testSetPlazasMayorQueDiez() {
        assertThrows(IllegalArgumentException.class, () -> furgoneta.setPlazas(11));
    }

    // ----------------- Setter de PMA -----------------
    @Test
    void testSetPmaValido() {
        furgoneta.setPma(500);
        assertEquals(500, furgoneta.getPma());
    }

    @Test
    void testSetPmaNulo() {
        assertThrows(NullPointerException.class, () -> furgoneta.setPma(null));
    }

    @Test
    void testSetPmaMenorQueUno() {
        assertThrows(IllegalArgumentException.class, () -> furgoneta.setPma(0));
    }

    @Test
    void testSetPmaMayorQue1500() {
        assertThrows(IllegalArgumentException.class, () -> furgoneta.setPma(1600));
    }

    // ----------------- Factor precio -----------------
    @Test
    void testGetFactorPrecio() {
        // Fórmula: (pma / FACTOR_PMA) + (plazas * FACTOR_PLAZAS)
        // FACTOR_PMA=100, FACTOR_PLAZAS=1
        int esperado = (1200 / 100) + (3 * 1); // 12 + 3 = 15
        assertEquals(esperado, furgoneta.getFactorPrecio());
    }

    // ----------------- toString -----------------
    @Test
    void testToString() {
        String esperado = "Ford Transit - (1200 Kg. PMA) - (3 Plazas) - 1234BCD";
        assertEquals(esperado, furgoneta.toString());
    }

    // ----------------- Herencia de Vehiculo -----------------
    @Test
    void testSetMarcaModeloMatricula() {
        furgoneta.setMarca("Fiat");
        furgoneta.setModelo("Ducato");
        furgoneta.setMatricula("5678XYZ");

        assertEquals("Fiat", furgoneta.getMarca());
        assertEquals("Ducato", furgoneta.getModelo());
        assertEquals("5678XYZ", furgoneta.getMatricula());
    }

    @Test
    void testEqualsYHashCodePorMatricula() {
        Furgoneta otro = new Furgoneta("Mercedes", "Sprinter", "1234BCD", 5, 1000);
        assertEquals(furgoneta, otro);
        assertEquals(furgoneta.hashCode(), otro.hashCode());

        Furgoneta distinto = new Furgoneta("Mercedes", "Sprinter", "5678XYZ", 5, 1000);
        assertNotEquals(furgoneta, distinto);
    }
}
