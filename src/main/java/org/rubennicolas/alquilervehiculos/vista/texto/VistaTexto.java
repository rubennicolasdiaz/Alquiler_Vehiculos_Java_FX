package org.rubennicolas.alquilervehiculos.vista.texto;

import javafx.stage.Stage;
import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.*;
import org.rubennicolas.alquilervehiculos.vista.Vista;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VistaTexto extends Vista {

    public VistaTexto() {
        Accion.setVista(this);
    }

    public void comenzar() {
        boolean salir = false;

        do {
            Consola.mostrarMenuAcciones();
            System.out.println();
            Accion accion = Consola.elegirAccion();
            System.out.println();
            assert accion != null;
            accion.ejecutar();
            System.out.println();

            if (accion.equals(Accion.SALIR)) {
                salir = true;
            }
        } while (!salir);
    }

    public void terminar() {
        Controlador.getInstancia().terminar();
    }

    public void insertarCliente() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.INSERTAR_CLIENTE);

        try {
            Cliente cliente = Consola.insertarCliente();

            Controlador.getInstancia().insertarCliente(cliente);
            System.out.println("\nCliente insertado correctamente: " + cliente);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void insertarVehiculo() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.INSERTAR_VEHICULO);

        try {
            Vehiculo vehiculo = Consola.insertarVehiculo();

            Controlador.getInstancia().insertarVehiculo(vehiculo);
            System.out.println("\nVehículo insertado correctamente: " + vehiculo);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void insertarAlquiler() {

        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.INSERTAR_ALQUILER);

        try {
            Alquiler alquiler = Consola.insertarAlquiler();

            Controlador.getInstancia().insertarAlquiler(alquiler);
            System.out.println("\nAlquiler insertado correctamente: " + alquiler);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public Cliente buscarCliente() {

        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.BUSCAR_CLIENTE);

        Cliente cliente = null;
        try {
            cliente = Controlador.getInstancia().buscarCliente(Consola.leerClienteDni());

            if (cliente != null) {
                System.out.println(cliente);
            } else {
                throw new DomainException("El DNI del cliente no se encuentra registrado.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
        return cliente;
    }

    public Vehiculo buscarVehiculo() {

        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.BUSCAR_VEHICULO);
        Vehiculo vehiculo = null;
        try {
            vehiculo = Controlador.getInstancia().buscarVehiculo(Consola.leerVehiculoMatricula());

            if (vehiculo != null) {
                System.out.println(vehiculo);
            } else {
                throw new DomainException("La matrícula del vehículo no se encuentra registrada.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
        return vehiculo;
    }

    public void modificarCliente() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.MODIFICAR_CLIENTE);

        try {
            Cliente cliente = Consola.modificarCliente();

            Controlador.getInstancia().modificarCliente(cliente);
            System.out.println("\nCliente modificado correctamente: " + cliente);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void modificarVehiculo() {

        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.MODIFICAR_VEHICULO);

        try {
            Vehiculo vehiculo = Consola.modificarVehiculo();

            Controlador.getInstancia().modificarVehiculo(vehiculo);
            System.out.println("\nVehículo modificado correctamente: " + vehiculo);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void devolverAlquiler() {

        Consola.mostrarCabecera("\nHa elegido la opción: " + Accion.DEVOLVER_ALQUILER);

        try {
            Alquiler alquiler = Controlador.getInstancia().buscarAlquiler(Consola.leerIdAlquiler());

            if (alquiler == null) {
                throw new DomainException("El ID del alquiler no se encuentra registrado.");
            }

            if (alquiler.getFechaDevolucion() != null) {
                throw new DomainException("No se puede devolver un alquiler ya devuelto.");
            }

            LocalDate fechaDevolucion = Consola.leerFecha();

            Controlador.getInstancia().devolverAlquiler(alquiler, fechaDevolucion);
            System.out.println("Alquiler devuelto correctamente: " + Controlador.getInstancia().buscarAlquiler(alquiler.getId()));

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void borrarCliente() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.BORRAR_CLIENTE);

        try {
            Cliente clienteOptional = Consola.leerClienteDni();

            Cliente clienteBuscado = Controlador.getInstancia().buscarCliente(clienteOptional);

            if (clienteBuscado != null) {

                Controlador.getInstancia().getAlquileresPorCliente(clienteBuscado)
                        .forEach(alquiler -> {
                            Controlador.getInstancia().borrarAlquiler(alquiler);
                        });
                Controlador.getInstancia().borrarCliente(clienteBuscado);
                System.out.println("Cliente Borrado: " + clienteBuscado);
            } else {
                System.err.println("Error: El DNI del cliente no está registrado.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void borrarVehiculo() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.BORRAR_VEHICULO);

        try {
            Vehiculo vehiculoOptional = Consola.leerVehiculoMatricula();

            Vehiculo vehiculoBuscado = Controlador.getInstancia().buscarVehiculo(vehiculoOptional);

            if (vehiculoBuscado != null) {

                Controlador.getInstancia().getAlquileresPorVehiculo(vehiculoBuscado)
                        .forEach(alquiler -> {
                            Controlador.getInstancia().borrarAlquiler(alquiler);
                        });
                Controlador.getInstancia().borrarVehiculo(vehiculoBuscado);
                System.out.println("Vehículo Borrado: " + vehiculoBuscado);
            } else {
                System.err.println("La matrícula del vehículo no se encuentra registrada.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void borrarAlquiler() {

        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.BORRAR_ALQUILER);

        Alquiler alquilerBuscado = Controlador.getInstancia().buscarAlquiler(Consola.leerIdAlquiler());

        if (alquilerBuscado != null) {
            Controlador.getInstancia().borrarAlquiler(alquilerBuscado);
            System.out.println("Alquiler Borrado: " + alquilerBuscado);
        } else {
            System.err.println("Error: El Id de alquiler no existe.");
        }
    }

    public void listarClientes() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.LISTAR_CLIENTES);

        try {
            List<Cliente> clientes = Controlador.getInstancia().getClientes();
            clientes.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void listarVehiculos() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.LISTAR_VEHICULOS);

        try {
            List<Vehiculo> vehiculos = Controlador.getInstancia().getVehiculos();
            vehiculos.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void listarAlquileres() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.LISTAR_ALQUILERES);

        try {
            List<Alquiler> alquileres = Controlador.getInstancia().getAlquileres();
            alquileres.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void listarAlquileresPorCliente() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.LISTAR_ALQUILERES_CLIENTE);

        try {
            Cliente cliente = Consola.leerClienteDni();

            List<Alquiler> alquileresCliente = Controlador.getInstancia()
                    .getAlquileres()
                    .stream()
                    .filter(alquiler -> alquiler.getCliente().equals(cliente))
                    .toList();

            alquileresCliente.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void listarAlquileresPorVehiculo() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.LISTAR_ALQUILERES_VEHICULO);

        try {
            Vehiculo vehiculo = Consola.leerVehiculoMatricula();

            List<Alquiler> alquileresVehiculo = Controlador.getInstancia()
                    .getAlquileres()
                    .stream()
                    .filter(alquiler -> alquiler.getVehiculo().equals(vehiculo))
                    .toList();

            alquileresVehiculo.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void mostrarEstadisticasMensualesTipoVehiculo() {

        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.MOSTRAR_ESTADISTICAS_MENSUALES);
        Map<TipoVehiculo, Integer> estadisticas = inicializarEstadisticas();

        Month mes = Month.of(Consola.leerMes());

        Year anio = Year.of(Consola.leerAnio());

        int contadorTurismos = 0;
        int contadorAutobuses = 0;
        int contadorFurgonetas = 0;

        try {
            for (Alquiler alquiler : Controlador.getInstancia().getAlquileres()) {

                if (alquiler.getVehiculo() instanceof Turismo
                        && alquiler.getFechaAlquiler().getMonth().equals(mes)
                        && Year.of(alquiler.getFechaAlquiler().getYear()).equals(anio)) {

                    contadorTurismos++;
                    estadisticas.put(TipoVehiculo.TURISMO, contadorTurismos);

                } else if (alquiler.getVehiculo() instanceof Autobus && alquiler.getFechaAlquiler().getMonth().equals(mes)
                        && Year.of(alquiler.getFechaAlquiler().getYear()).equals(anio)) {

                    contadorAutobuses++;
                    estadisticas.put(TipoVehiculo.AUTOBUS, contadorAutobuses);

                } else if (alquiler.getVehiculo() instanceof Furgoneta && alquiler.getFechaAlquiler().getMonth().equals(mes)
                        && Year.of(alquiler.getFechaAlquiler().getYear()).equals(anio)) {

                    contadorFurgonetas++;
                    estadisticas.put(TipoVehiculo.FURGONETA, contadorFurgonetas);
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
        System.out.println("Las estadísticas de alquileres por tipo de vehículo durante "
                + "el mes de " + mes.getDisplayName(TextStyle.FULL, Locale.getDefault())
                + " del año " + anio
                + " son "
                + "de: " + "\n" + "\n" + estadisticas);
    }

    private Map<TipoVehiculo, Integer> inicializarEstadisticas() {

        Map<TipoVehiculo, Integer> mapaVehiculos = new EnumMap<>(TipoVehiculo.class);

        mapaVehiculos.put(TipoVehiculo.AUTOBUS, 0);
        mapaVehiculos.put(TipoVehiculo.FURGONETA, 0);
        mapaVehiculos.put(TipoVehiculo.TURISMO, 0);

        return mapaVehiculos;
    }

    @Override
    public void start(Stage args) {
    }
}