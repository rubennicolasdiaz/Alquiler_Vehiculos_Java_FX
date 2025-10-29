package org.rubennicolas.alquilervehiculos.modelo.dominio.UnitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AlquilerTest {

    private Cliente cliente;
    private Vehiculo vehiculo;

    @BeforeEach
    void setUp() {
        // Simulamos dependencias sin necesidad de clases reales
        cliente = Mockito.mock(Cliente.class);
        vehiculo = Mockito.mock(Vehiculo.class);
        Mockito.when(vehiculo.getFactorPrecio()).thenReturn(10);
    }

    @Test
    @DisplayName("Constructor con cliente, vehículo y fecha crea un alquiler válido")
    void constructorBasico_CreaAlquilerValido() {
        LocalDate fecha = LocalDate.now().minusDays(2);
        Alquiler alquiler = new Alquiler(cliente, vehiculo, fecha);

        assertEquals(cliente, alquiler.getCliente());
        assertEquals(vehiculo, alquiler.getVehiculo());
        assertEquals(fecha, alquiler.getFechaAlquiler());
        assertNull(alquiler.getFechaDevolucion());
    }

    @Test
    @DisplayName("setFechaDevolucion lanza excepción si la fecha es futura")
    void setFechaDevolucion_Futura_LanzaExcepcion() {
        LocalDate fechaAlquiler = LocalDate.now().minusDays(3);
        Alquiler alquiler = new Alquiler(cliente, vehiculo, fechaAlquiler);

        LocalDate fechaDevolucionFutura = LocalDate.now().plusDays(1);
        assertThrows(IllegalArgumentException.class,
                () -> alquiler.setFechaDevolucion(fechaDevolucionFutura));
    }

    @Test
    @DisplayName("setFechaDevolucion lanza excepción si es igual a la de alquiler")
    void setFechaDevolucion_Igual_LanzaExcepcion() {
        LocalDate fecha = LocalDate.now().minusDays(2);
        Alquiler alquiler = new Alquiler(cliente, vehiculo, fecha);
        assertThrows(IllegalArgumentException.class,
                () -> alquiler.setFechaDevolucion(fecha));
    }

    @Test
    @DisplayName("getPrecio calcula correctamente el precio según días y factor")
    void getPrecio_CalculaCorrectamente() {
        LocalDate inicio = LocalDate.now().minusDays(5);
        LocalDate fin = LocalDate.now().minusDays(2);
        Alquiler alquiler = new Alquiler(cliente, vehiculo, inicio);
        alquiler.setFechaDevolucion(fin);

        int precioEsperado = (20 + 10) * 3; // PRECIO_DIA=20 + factor=10 * 3 días
        assertEquals(precioEsperado, alquiler.getPrecio());
    }

    @Test
    @DisplayName("getPrecio devuelve 0 si no hay fecha de devolución")
    void getPrecio_SinDevolucion_DevuelveCero() {
        Alquiler alquiler = new Alquiler(cliente, vehiculo, LocalDate.now().minusDays(2));
        assertEquals(0, alquiler.getPrecio());
    }

    @Test
    @DisplayName("solapaCon devuelve true cuando hay solapamiento de fechas")
    void solapaCon_True_SiSolapanFechas() {
        LocalDate inicio1 = LocalDate.now().minusDays(5);
        LocalDate fin1 = LocalDate.now().minusDays(1);
        Alquiler a1 = new Alquiler(cliente, vehiculo, inicio1);
        a1.setFechaDevolucion(fin1);

        LocalDate inicio2 = LocalDate.now().minusDays(3);
        Alquiler a2 = new Alquiler(cliente, vehiculo, inicio2);

        assertTrue(a1.solapaCon(a2));
    }

    @Test
    @DisplayName("solapaCon devuelve false cuando no hay solapamiento")
    void solapaCon_False_SiNoSolapan() {
        LocalDate inicio1 = LocalDate.now().minusDays(10);
        LocalDate fin1 = LocalDate.now().minusDays(7);
        Alquiler a1 = new Alquiler(cliente, vehiculo, inicio1);
        a1.setFechaDevolucion(fin1);

        LocalDate inicio2 = LocalDate.now().minusDays(5);
        Alquiler a2 = new Alquiler(cliente, vehiculo, inicio2);

        assertFalse(a1.solapaCon(a2));
    }

    @Test
    @DisplayName("Constructor copia correctamente un alquiler existente")
    void constructorCopia_CreaNuevoAlquilerConDatosIguales() {
        LocalDate inicio = LocalDate.now().minusDays(5);
        LocalDate fin = LocalDate.now().minusDays(3);
        Alquiler original = new Alquiler(1, cliente, vehiculo, inicio, fin);

        Alquiler copia = new Alquiler(original);
        assertEquals(original.getId(), copia.getId());
        assertEquals(original.getCliente(), copia.getCliente());
        assertEquals(original.getVehiculo(), copia.getVehiculo());
        assertEquals(original.getFechaAlquiler(), copia.getFechaAlquiler());
        assertEquals(original.getFechaDevolucion(), copia.getFechaDevolucion());
    }
}