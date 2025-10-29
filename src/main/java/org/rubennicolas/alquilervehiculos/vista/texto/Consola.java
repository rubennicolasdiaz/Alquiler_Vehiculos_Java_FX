package org.rubennicolas.alquilervehiculos.vista.texto;

import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.utilidades.Entrada;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Consola {

    public static final String PATRON_FECHA = "dd/MM/yyyy";
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern(PATRON_FECHA);

    public static void mostrarCabecera(String mensaje) {

        String str = "-";
        String repeated = str.repeat(mensaje.length());

        System.out.println(mensaje + "\n" + repeated);
    }

    public static void mostrarMenuAcciones() {

        System.out.println();
        mostrarCabecera("MENÚ PRINCIPAL - APLICACIÓN DE ALQUILER DE VEHÍCULOS");

        for (Accion accion : Accion.values()) {
            System.out.println(accion);
        }
    }

    public static Accion elegirAccion() {
        int ordinalOpcion = 0;
        boolean error;

        do {
            try {
                ordinalOpcion = leerEntero("Elige una opción: ");
                return Accion.get(ordinalOpcion);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage() + "\n");
                error = true;
            }
        } while (error);

        return null;
    }

    private static String leerCadena(String mensaje) {

        System.out.println(mensaje);
        return Entrada.cadena();
    }

    public static Integer leerEntero(String mensaje) {

        System.out.println(mensaje);
        return Entrada.entero();
    }

    public static Cliente leerClienteDni() {

        String dni = leerCadena("Introduzca el DNI del cliente");
        return new Cliente("Cliente",
                dni, "900900900", "cliente@mail.com");
    }

    public static String leerNombre() {
        return leerCadena("Introduzca el nombre del cliente");
    }

    public static String leerDni() {
        return leerCadena("Introduzca el DNI del cliente");
    }

    public static String leerTelefono() {
        return leerCadena("Introduzca el teléfono del cliente");
    }

    public static String leerEmail() {
        return leerCadena("Introduzca el email del cliente");
    }

    public static String leerMarca() {
        return leerCadena("Introduzca la marca del vehículo");
    }

    public static String leerModelo() {
        return leerCadena("Introduzca el modelo del vehículo");
    }

    public static String leerMatricula() {
        return leerCadena("Introduzca la matrícula del vehículo");
    }

    public static int leerCilindrada() {
        return leerEntero("Introduzca la cilindrada del vehículo");
    }

    public static int leerPlazas() {
        return leerEntero("Introduzca el número de plazas del vehículo");
    }

    public static int leerPma() {
        return leerEntero("Introduzca el PMA del vehículo");
    }

    public static TipoVehiculo leerTipoVehiculo() {

        TipoVehiculo tipoVehiculo;
        mostrarMenuTiposVehiculos();

        tipoVehiculo = elegirTipoVehiculo();
        return tipoVehiculo;
    }

    private static void mostrarMenuTiposVehiculos() {

        System.out.println();
        mostrarCabecera("TIPOS DE VEHÍCULOS");

        for (TipoVehiculo tipoVehiculo : TipoVehiculo.values()) {
            System.out.println(tipoVehiculo);
        }
    }

    private static TipoVehiculo elegirTipoVehiculo() {

        int ordinalOpcion = 0;

        try {
            ordinalOpcion = leerEntero("\nElige una opción: 0-Turismo | 1-Autobús | 2-Furgoneta");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
        return TipoVehiculo.get(ordinalOpcion);
    }

    public static Vehiculo leerVehiculoMatricula() {

        String matricula = null;

        try {
            matricula = leerCadena("Introduzca la matrícula del vehículo en formato: 1111BBB");
        } catch (Exception e) {
            throw new DomainException(e.getMessage());
        }
        return new Turismo("Seat", "León", matricula, 1500);
    }

    public static LocalDate leerFecha() {

        LocalDate fecha = null;
        boolean fechaValida;

        do {
            int day = leerDia();
            int month = leerMes();
            int year = leerAnio();

            fechaValida = comprobarFechaValida(year, month, day);

            if (fechaValida) {
                fecha = LocalDate.of(year, month, day);
            }
        } while (!fechaValida);

        return fecha;
    }

    public static int leerDia() {

        int day;

        do {
            try {
                day = leerEntero("Por favor, indique el día (1-31): ");
            } catch (Exception e) {
                throw new DomainException(e.getMessage());
            }
        } while (day < 1 || day > 31);

        return day;
    }

    public static int leerMes() {

        int month;

        do {
            try {
                month = leerEntero("Por favor, indique el mes (1-12): ");
            } catch (Exception e) {
                throw new DomainException(e.getMessage());
            }
        } while (month < 1 || month > 12);

        return month;
    }

    public static int leerAnio() {
        int year;
        do {
            try {
                year = leerEntero("Por favor, indique el año (2020-2030): ");
            } catch (Exception e) {
                throw new DomainException(e.getMessage());
            }
        } while (year < 2020 || year > 2030);
        return year;
    }

    public static int leerIdAlquiler() {
        return leerEntero("Por favor, introduzca el Id del Alquiler: ");
    }

    public static boolean comprobarFechaValida(int year, int month, int day) {
        try {
            LocalDate.of(year, month, day);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}