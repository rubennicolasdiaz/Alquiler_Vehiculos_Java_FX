package org.rubennicolas.alquilervehiculos.modelo.dominio.UnitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        // DNI 12345678Z corresponde a resto=14 -> letra Z
        cliente = new Cliente("Juan Pérez", "12345678Z", "612345678", "juan.perez@mail.com");
    }

    @Test
    void testConstructorYGetters() {
        assertEquals("Juan Pérez", cliente.getNombreApellidos());
        assertEquals("12345678Z", cliente.getDni());
        assertEquals("612345678", cliente.getTelefono());
        assertEquals("juan.perez@mail.com", cliente.getEmail());
    }

    @Test
    void testConstructorCopia() {
        Cliente copia = new Cliente(cliente);
        assertEquals(cliente, copia);
        assertNotSame(cliente, copia);
    }

    @Test
    void testConstructorCopiaNulo() {
        assertThrows(NullPointerException.class, () -> new Cliente(null));
    }

    // ----------------- Tests nombreApellidos -----------------
    @Test
    void testSetNombreApellidosValido() {
        cliente.setNombreApellidos("Ana López");
        assertEquals("Ana López", cliente.getNombreApellidos());
    }

    @Test
    void testSetNombreApellidosNulo() {
        assertThrows(NullPointerException.class, () -> cliente.setNombreApellidos(null));
    }

    @Test
    void testSetNombreApellidosBlanco() {
        assertThrows(IllegalArgumentException.class, () -> cliente.setNombreApellidos("  "));
    }

    @Test
    void testSetNombreApellidosFormatoIncorrecto() {
        assertThrows(IllegalArgumentException.class, () -> cliente.setNombreApellidos("ana pérez"));
        assertThrows(IllegalArgumentException.class, () -> cliente.setNombreApellidos("Juan Perez Gomez Perez Gomez Extra"));
    }

    // ----------------- Tests DNI -----------------
    @Test
    void testSetDniValido() {
        // 00000000T corresponde a resto=0 -> letra T
        cliente.setDni("00000000T");
        assertEquals("00000000T", cliente.getDni());
    }

    @Test
    void testSetDniNulo() {
        assertThrows(NullPointerException.class, () -> cliente.setDni(null));
    }

    @Test
    void testSetDniBlanco() {
        assertThrows(IllegalArgumentException.class, () -> cliente.setDni(" "));
    }

    @Test
    void testSetDniFormatoIncorrecto() {
        assertThrows(IllegalArgumentException.class, () -> cliente.setDni("1234A678Z"));
    }

    @Test
    void testSetDniLetraIncorrecta() {
        // 12345678 debe tener letra Z, no A
        assertThrows(IllegalArgumentException.class, () -> cliente.setDni("12345678A"));
    }

    // ----------------- Tests telefono -----------------
    @Test
    void testSetTelefonoValido() {
        cliente.setTelefono("712345678");
        assertEquals("712345678", cliente.getTelefono());
    }

    @Test
    void testSetTelefonoNulo() {
        assertThrows(NullPointerException.class, () -> cliente.setTelefono(null));
    }

    @Test
    void testSetTelefonoBlanco() {
        assertThrows(IllegalArgumentException.class, () -> cliente.setTelefono(" "));
    }

    @Test
    void testSetTelefonoFormatoIncorrecto() {
        assertThrows(IllegalArgumentException.class, () -> cliente.setTelefono("512345678"));
        assertThrows(IllegalArgumentException.class, () -> cliente.setTelefono("61234"));
    }

    // ----------------- Tests email -----------------
    @Test
    void testSetEmailValido() {
        cliente.setEmail("nuevo.email@mail.com");
        assertEquals("nuevo.email@mail.com", cliente.getEmail());
    }

    @Test
    void testSetEmailNulo() {
        assertThrows(NullPointerException.class, () -> cliente.setEmail(null));
    }

    @Test
    void testSetEmailBlanco() {
        assertThrows(IllegalArgumentException.class, () -> cliente.setEmail(" "));
    }

    @Test
    void testSetEmailFormatoIncorrecto() {
        assertThrows(IllegalArgumentException.class, () -> cliente.setEmail("correo-invalido"));
    }

    // ----------------- Tests equals y hashCode -----------------
    @Test
    void testEqualsYHashCode() {
        // Mismo DNI -> deben ser iguales
        Cliente otro = new Cliente("Ana López", "12345678Z", "698765432", "ana@mail.com");
        assertEquals(cliente, otro);
        assertEquals(cliente.hashCode(), otro.hashCode());

        // DNI distinto -> no deben ser iguales
        Cliente distinto = new Cliente("Ana López", "87654321X", "698765432", "ana@mail.com");
        assertNotEquals(cliente, distinto);
    }

    // ----------------- Test toString -----------------
    @Test
    void testToString() {
        String esperado = "Juan Pérez - 12345678Z - (612345678) juan.perez@mail.com";
        assertEquals(esperado, cliente.toString());
    }

    // ----------------- Test compareTo -----------------
    @Test
    void testCompareTo() {
        Cliente c1 = new Cliente("Ana López", "00000000T", "612345678", "ana@mail.com");
        Cliente c2 = new Cliente("Juan Pérez", "12345678Z", "612345678", "juan@mail.com");

        assertTrue(c1.compareTo(c2) < 0);
        assertTrue(c2.compareTo(c1) > 0);
        assertEquals(0, c2.compareTo(cliente));
    }
}
