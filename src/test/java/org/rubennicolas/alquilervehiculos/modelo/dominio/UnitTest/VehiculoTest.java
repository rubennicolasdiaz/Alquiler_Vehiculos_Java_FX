package org.rubennicolas.alquilervehiculos.modelo.dominio.UnitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;

import static org.junit.jupiter.api.Assertions.*;

class VehiculoTest {

    private Vehiculo vehiculo;

    // Subclase de prueba para instanciar Vehiculo
    private static class VehiculoTestImpl extends Vehiculo {
        public VehiculoTestImpl(String marca, String modelo, String matricula) {
            super(marca, modelo, matricula);
        }

        @Override
        public int getFactorPrecio() {
            return 1; // Dummy implementation
        }

        @Override
        public Vehiculo clonar() {
            return null;
        }
    }

    @BeforeEach
    void setUp() {
        vehiculo = new VehiculoTestImpl("Toyota", "Corolla", "1234BCD");
    }

    @Test
    void testConstructorYGetters() {
        assertEquals("Toyota", vehiculo.getMarca());
        assertEquals("Corolla", vehiculo.getModelo());
        assertEquals("1234BCD", vehiculo.getMatricula());
    }

    @Test
    void testSetMarcaValida() {
        vehiculo.setMarca("Honda");
        assertEquals("Honda", vehiculo.getMarca());
    }

    @Test
    void testSetMarcaNula() {
        assertThrows(NullPointerException.class, () -> vehiculo.setMarca(null));
    }

    @Test
    void testSetMarcaEnBlanco() {
        assertThrows(IllegalArgumentException.class, () -> vehiculo.setMarca("  "));
    }

    @Test
    void testSetModeloValido() {
        vehiculo.setModelo("Civic");
        assertEquals("Civic", vehiculo.getModelo());
    }

    @Test
    void testSetModeloNulo() {
        assertThrows(NullPointerException.class, () -> vehiculo.setModelo(null));
    }

    @Test
    void testSetModeloEnBlanco() {
        assertThrows(IllegalArgumentException.class, () -> vehiculo.setModelo(""));
    }

    @Test
    void testSetMatriculaValida() {
        vehiculo.setMatricula("5678XYZ");
        assertEquals("5678XYZ", vehiculo.getMatricula());
    }

    @Test
    void testSetMatriculaNula() {
        assertThrows(NullPointerException.class, () -> vehiculo.setMatricula(null));
    }

    @Test
    void testSetMatriculaEnBlanco() {
        assertThrows(IllegalArgumentException.class, () -> vehiculo.setMatricula(" "));
    }

    @Test
    void testSetMatriculaFormatoIncorrecto() {
        assertThrows(IllegalArgumentException.class, () -> vehiculo.setMatricula("123ABC"));
        assertThrows(IllegalArgumentException.class, () -> vehiculo.setMatricula("12345BCD"));
    }

    @Test
    void testEqualsYHashCode() {
        Vehiculo vehiculo2 = new VehiculoTestImpl("Ford", "Focus", "1234BCD");
        assertEquals(vehiculo, vehiculo2);
        assertEquals(vehiculo.hashCode(), vehiculo2.hashCode());

        Vehiculo vehiculo3 = new VehiculoTestImpl("Ford", "Focus", "9999ZZZ");
        assertNotEquals(vehiculo, vehiculo3);
    }

    @Test
    void testCompareTo() {
        Vehiculo vehiculo2 = new VehiculoTestImpl("BMW", "X5", "1111BBB");
        assertTrue(vehiculo.compareTo(vehiculo2) > 0); // Toyota > BMW lexicográficamente
    }
}
