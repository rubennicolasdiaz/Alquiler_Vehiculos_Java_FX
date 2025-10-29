package org.rubennicolas.alquilervehiculos.modelo.negocio.ficheros;

import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IAlquileres;
import org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesFicheros;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesAlquiler.comprobarAlquiler;
import static org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades.UtilidadesAlquiler.comprobarDevolucion;

public class Alquileres implements IAlquileres {

    public List<Cliente> coleccionClientes;
    public List<Vehiculo> coleccionVehiculos;

    private final List<Alquiler> coleccionAlquileres;
    private final Supplier<List<Alquiler>> lector;  // 👈 Inyectamos el proveedor de datos
    private final Consumer<List<Alquiler>> escritor; // 👈 Inyectamos el guardado

    // --- Constructor de producción (lee desde XML real) ---
    public Alquileres() {
        this(UtilidadesFicheros::leerXmlAlquileres, UtilidadesFicheros::escribirXmlAlquileres);
        coleccionClientes = UtilidadesFicheros.leerXmlClientes();
        coleccionVehiculos = UtilidadesFicheros.leerXmlVehiculos();
    }

    // --- Constructor alternativo para TESTS ---
    public Alquileres(Supplier<List<Alquiler>> lector, Consumer<List<Alquiler>> escritor) {

        coleccionClientes = UtilidadesFicheros.leerXmlClientes();
        coleccionVehiculos = UtilidadesFicheros.leerXmlVehiculos();

        this.lector = lector;
        this.escritor = escritor;
        this.coleccionAlquileres = new ArrayList<>();

        try {
            this.coleccionAlquileres.addAll(lector.get());
        } catch (DomainException e) {
            throw new DomainException("Error al leer los alquileres: " + e.getMessage());
        }
    }

    @Override
    public void comenzar() {

    }

    @Override
    public List<Alquiler> getAlquileres() {

        return coleccionAlquileres.stream()
                .map(alquiler -> {
                    Cliente clienteReal = coleccionClientes.stream()
                            .filter(c -> c.equals(alquiler.getCliente()))
                            .findFirst()
                            .orElse(alquiler.getCliente());

                    Vehiculo vehiculoReal = coleccionVehiculos.stream()
                            .filter(v -> v.equals(alquiler.getVehiculo()))
                            .findFirst()
                            .orElse(alquiler.getVehiculo());

                    alquiler.setCliente(clienteReal);
                    alquiler.setVehiculo(vehiculoReal);
                    return alquiler;
                })
                .sorted(Comparator.comparing(Alquiler::getFechaAlquiler)
                        .thenComparing(Alquiler::getCliente)
                        .thenComparing(Alquiler::getVehiculo))
                .toList();
    }

    @Override
    public List<Alquiler> getAlquileresPorCliente(Cliente cliente) {

        return getAlquileres().stream()
                .filter(alq -> alq.getCliente().equals(cliente))
                .sorted(Comparator.comparing(Alquiler::getFechaAlquiler))
                .toList();
    }

    @Override
    public List<Alquiler> getAlquileresPorVehiculo(Vehiculo vehiculo) {

        return getAlquileres().stream()
                .filter(alq -> alq.getVehiculo().equals(vehiculo))
                .sorted(Comparator.comparing(Alquiler::getFechaAlquiler))
                .toList();
    }

    @Override
    public Alquiler buscarAlquiler(int id) {
        return getAlquileres().stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void insertarAlquiler(Alquiler alquiler) {
        if (alquiler == null)
            throw new NullPointerException("No se puede insertar un alquiler nulo.");

        comprobarAlquiler(alquiler.getCliente(), alquiler.getVehiculo(),
                alquiler.getFechaAlquiler(), coleccionAlquileres);

        coleccionAlquileres.add(alquiler);
    }

    public void devolverAlquiler(Alquiler alquiler, LocalDate fechaDevolucion) {
        comprobarDevolucion(alquiler, fechaDevolucion, coleccionAlquileres);
        alquiler.setFechaDevolucion(fechaDevolucion);
    }

    @Override
    public void borrarAlquiler(Alquiler alquiler) {
        if (alquiler == null)
            throw new NullPointerException("No se puede borrar un alquiler nulo.");

        Alquiler alquilerBuscado = buscarAlquiler(alquiler.getId());
        if (alquilerBuscado == null)
            throw new DomainException("No existe ningún alquiler igual.");

        coleccionAlquileres.remove(alquilerBuscado);
    }

    @Override
    public void terminar() {
        escritor.accept(coleccionAlquileres);
    }
}