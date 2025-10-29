package org.rubennicolas.alquilervehiculos.modelo.dominio;

import com.google.common.collect.Range;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Alquiler {

    protected static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int PRECIO_DIA = 20;

    private static int contador = 1; // empieza en 1 o el último id leído del fichero
    private int id;

    private Cliente cliente;
    private Vehiculo vehiculo;
    private LocalDate fechaAlquiler;
    private LocalDate fechaDevolucion;

    public Alquiler() {
    }

    public Alquiler(int id, Cliente cliente, Vehiculo vehiculo, LocalDate fechaAlquiler, LocalDate fechaDevolucion) {
        setId(id);
        setCliente(cliente);
        setVehiculo(vehiculo);
        setFechaAlquiler(fechaAlquiler);
        setFechaDevolucion(fechaDevolucion);
    }

    public Alquiler(int id, Cliente cliente, Vehiculo vehiculo, LocalDate fechaAlquiler) {

        setId(id);
        setCliente(cliente);
        setVehiculo(vehiculo);
        setFechaAlquiler(fechaAlquiler);
    }

    public Alquiler(Cliente cliente, Vehiculo vehiculo, LocalDate fechaAlquiler) {

        id = contador++;
        setCliente(cliente);
        setVehiculo(vehiculo);
        setFechaAlquiler(fechaAlquiler);
    }

    public Alquiler(Alquiler alquiler) {

        if (alquiler == null) {
            throw new NullPointerException("No es posible copiar un alquiler nulo.");
        }

        setId(alquiler.getId());
        setCliente(alquiler.getCliente());
        setVehiculo(alquiler.getVehiculo());
        setFechaAlquiler(alquiler.getFechaAlquiler());
        setFechaDevolucion(alquiler.getFechaDevolucion());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {

        if (cliente == null) {
            throw new NullPointerException("El cliente no puede ser nulo.");
        }
        this.cliente = cliente;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {

        if (vehiculo == null) {
            throw new NullPointerException("El vehículo no puede ser nulo.");
        }
        this.vehiculo = vehiculo;
    }

    public LocalDate getFechaAlquiler() {
        return fechaAlquiler;
    }

    private void setFechaAlquiler(LocalDate fechaAlquiler) {

        if (fechaAlquiler == null) {
            throw new NullPointerException("La fecha de alquiler no puede ser nula.");

        } else if (fechaAlquiler.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de alquiler no puede ser futura.");

        } else if (fechaAlquiler.isBefore(LocalDate.now()) || fechaAlquiler.isEqual(LocalDate.now())) {
            this.fechaAlquiler = fechaAlquiler;
        }
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {

        if (fechaDevolucion == null) {
            throw new NullPointerException("La fecha de devolución no puede ser nula.");
        }

        if (fechaDevolucion.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de devolución no puede ser futura.");
        }

        if (fechaDevolucion.isEqual(this.fechaAlquiler)) {
            throw new IllegalArgumentException("La fecha de devolución no puede ser el mismo día que la fecha de alquiler.");
        }

        if (!fechaDevolucion.isAfter(fechaAlquiler)) {
            throw new IllegalArgumentException("La fecha de devolución debe ser posterior a la fecha de alquiler.");
        }
        this.fechaDevolucion = fechaDevolucion;
    }

    /* El precio que cobra por alquiler un turismo es el siguiente:
     *
     * (precioDia + factorCilindrada) * numDias. El precioDia es 20, el factorCilindrada
     * depende de la cilindrada del turismo alquilada y es igual a la cilindrada del turismo / 10,
     * y numDias son los días transcurridos entre la fecha de alquiler y la de devolución */

    public int getPrecio() {
        if (getFechaDevolucion() == null) {
            return 0;
        }

        int factorPrecio = vehiculo.getFactorPrecio();
        int numDias = (int) ChronoUnit.DAYS.between(getFechaAlquiler(), getFechaDevolucion());

        return (PRECIO_DIA + factorPrecio) * numDias;
    }

    /**
     * Comprueba si este alquiler se solapa en fechas con otro.
     */
    public boolean solapaCon(Alquiler alquiler) {

        boolean solape = false;

        Range<LocalDate> periodo = Range.closed(this.fechaAlquiler, this.fechaDevolucion);

        if (this.fechaDevolucion != null) {
            if (periodo.contains(alquiler.getFechaAlquiler())) {
                solape = true;
            }
        }
        return solape;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Alquiler alquiler)) return false;
        return id == alquiler.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format(
                "Alquiler ID: %d, Cliente: %s <---> Vehículo: %s, Fecha Alquiler: %s, Fecha Devolución: %s, Precio: %d€",
                getId(),
                getCliente(),
                getVehiculo(),
                getFechaAlquiler().format(FORMATO_FECHA),
                (getFechaDevolucion() == null) ? "Aún no devuelto" : getFechaDevolucion().format(FORMATO_FECHA),
                getPrecio()
        );
    }

}

