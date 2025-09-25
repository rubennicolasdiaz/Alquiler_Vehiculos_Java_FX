package org.rubennicolas.alquilervehiculos.vista.texto;

import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.*;
import org.rubennicolas.alquilervehiculos.utilidades.Entrada;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Consola {

    public static final String PATRON_FECHA = "dd/MM/yyyy";
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern(PATRON_FECHA);

    private Consola() {
    }

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

    private static Integer leerEntero(String mensaje) {

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

    public static Cliente insertarCliente() {
        Cliente cliente = new Cliente();

        boolean validado = false;
        String nombreApellidos;
        String dni;
        String telefono;
        String email;

        do {
            try {
                nombreApellidos = Consola.leerNombre();
                cliente.setNombreApellidos(nombreApellidos);

                validado = true;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage() + "\n");

            }
        } while (!validado);

        validado = false;

        do {
            try {
                dni = Consola.leerDni();
                cliente.setDni(dni);

                validado = true;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage() + "\n");

            }
        } while (!validado);

        validado = false;

        do {
            try {
                telefono = Consola.leerTelefono();
                cliente.setTelefono(telefono);

                validado = true;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage() + "\n");

            }
        } while (!validado);

        validado = false;

        do {
            try {
                email = Consola.leerEmail();
                cliente.setEmail(email);

                validado = true;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage() + "\n");

            }
        } while (!validado);
        return cliente;
    }

    public static Vehiculo insertarVehiculo() {

        boolean validado = false;
        String marca;
        String modelo;
        String matricula;
        int cilindrada;
        int numPlazas;
        int pma;

        TipoVehiculo tipoVehiculo = Consola.leerTipoVehiculo();

        if (tipoVehiculo == TipoVehiculo.TURISMO) {

            Turismo turismo = new Turismo();
            do {
                try {
                    marca = Consola.leerMarca();
                    turismo.setMarca(marca);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");

                }
            } while (!validado);

            validado = false;

            do {
                try {
                    modelo = Consola.leerModelo();
                    turismo.setModelo(modelo);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");

                }
            } while (!validado);

            validado = false;

            do {
                try {
                    matricula = Consola.leerMatricula();
                    turismo.setMatricula(matricula);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");
                }
            } while (!validado);

            validado = false;

            do {
                try {
                    cilindrada = Consola.leerCilindrada();
                    turismo.setCilindrada(cilindrada);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");
                }
            } while (!validado);
            return turismo;
        }

        if (tipoVehiculo == TipoVehiculo.FURGONETA) {

            Furgoneta furgoneta = new Furgoneta();
            do {
                try {
                    marca = Consola.leerMarca();
                    furgoneta.setMarca(marca);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");
                }
            } while (!validado);

            validado = false;

            do {
                try {
                    modelo = Consola.leerModelo();
                    furgoneta.setModelo(modelo);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");

                }
            } while (!validado);

            validado = false;

            do {
                try {
                    matricula = Consola.leerMatricula();
                    furgoneta.setMatricula(matricula);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");

                }
            } while (!validado);

            validado = false;

            do {
                try {
                    numPlazas = Consola.leerPlazas();
                    furgoneta.setPlazas(numPlazas);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");

                }
            } while (!validado);

            validado = false;

            do {
                try {
                    pma = Consola.leerPma();
                    furgoneta.setPma(pma);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");
                }
            } while (!validado);
            return furgoneta;
        }

        if (tipoVehiculo == TipoVehiculo.AUTOBUS) {

            Autobus autobus = new Autobus();
            do {
                try {
                    marca = Consola.leerMarca();
                    autobus.setMarca(marca);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");

                }
            } while (!validado);

            validado = false;

            do {
                try {
                    modelo = Consola.leerModelo();
                    autobus.setModelo(modelo);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");

                }
            } while (!validado);

            validado = false;

            do {
                try {
                    matricula = Consola.leerMatricula();
                    autobus.setMatricula(matricula);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");

                }
            } while (!validado);

            validado = false;

            do {
                try {
                    numPlazas = Consola.leerPlazas();
                    autobus.setPlazas(numPlazas);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");

                }
            } while (!validado);
            return autobus;
        }
        return null;
    }

    public static Alquiler insertarAlquiler() {
        boolean validado = false;
        boolean fechaValida;
        Alquiler alquiler;
        Cliente cliente = null;
        Vehiculo vehiculo = null;

        int day = 0;
        int month = 0;
        int year = 0;
        LocalDate fechaAlquiler;

        do {
            try {
                cliente = Controlador.getInstancia().buscarCliente(Consola.leerClienteDni());

                if (cliente == null) {
                    throw new DomainException("El DNI del cliente no se encuentra registrado.");
                }
                validado = true;

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage() + "\n");

            }
        } while (!validado);

        validado = false;

        do {
            try {
                vehiculo = Controlador.getInstancia().buscarVehiculo(Consola.leerVehiculoMatricula());

                if (vehiculo == null) {
                    throw new DomainException("La matrícula del vehículo no se encuentra registrada.");
                }
                validado = true;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage() + "\n");

            }
        } while (!validado);

        validado = false;


        do {
            System.out.println("Por favor, introduzca una fecha válida de alquiler del turismo: Día / Mes / Año: ");
            do {
                try {
                    day = Consola.leerDia();
                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");

                }
            } while (!validado);

            validado = false;

            do {
                try {
                    month = Consola.leerMes();
                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");

                }
            } while (!validado);

            validado = false;

            do {
                try {
                    year = Consola.leerAnio();
                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");
                }
            } while (!validado);

            fechaValida = Consola.comprobarFechaValida(year, month, day);
        } while (!fechaValida);

        fechaAlquiler = LocalDate.of(year, month, day);

        alquiler = new Alquiler(cliente, vehiculo, fechaAlquiler);
        return alquiler;
    }

    public static Cliente modificarCliente() {

        Cliente cliente = Controlador.getInstancia().buscarCliente(Consola.leerClienteDni());

        if (cliente == null) {
            throw new NullPointerException("El DNI del cliente no se encuentra registrado.");
        } else {

            boolean validado = false;
            String nombreApellidos;
            String telefono;
            String email;

            do {
                try {
                    nombreApellidos = Consola.leerNombre();
                    cliente.setNombreApellidos(nombreApellidos);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");

                }
            } while (!validado);

            validado = false;

            do {
                try {
                    telefono = Consola.leerTelefono();
                    cliente.setTelefono(telefono);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");

                }
            } while (!validado);

            validado = false;

            do {
                try {
                    email = Consola.leerEmail();
                    cliente.setEmail(email);

                    validado = true;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage() + "\n");

                }
            } while (!validado);
        }
        return cliente;
    }

    public static Vehiculo modificarVehiculo() {

        Vehiculo vehiculo = Controlador.getInstancia().buscarVehiculo(Consola.leerVehiculoMatricula());

        if (vehiculo == null) {
            throw new NullPointerException("La matrícula del vehículo no se encuentra registrada.");
        } else {

            boolean validado = false;
            String marca;
            String modelo;

            int cilindrada;
            int numPlazas;
            int pma;

            if (vehiculo instanceof Turismo turismo) {

                do {
                    try {
                        marca = Consola.leerMarca();
                        turismo.setMarca(marca);

                        validado = true;
                    } catch (Exception e) {
                        System.err.println("Error: " + e.getMessage() + "\n");

                    }
                } while (!validado);

                validado = false;

                do {
                    try {
                        modelo = Consola.leerModelo();
                        turismo.setModelo(modelo);

                        validado = true;
                    } catch (Exception e) {
                        System.err.println("Error: " + e.getMessage() + "\n");

                    }
                } while (!validado);

                validado = false;

                do {
                    try {
                        cilindrada = Consola.leerCilindrada();
                        turismo.setCilindrada(cilindrada);

                        validado = true;
                    } catch (Exception e) {
                        System.err.println("Error: " + e.getMessage() + "\n");

                    }
                } while (!validado);
                return turismo;
            }

            if (vehiculo instanceof Furgoneta furgoneta) {

                do {
                    try {
                        marca = Consola.leerMarca();
                        furgoneta.setMarca(marca);

                        validado = true;
                    } catch (Exception e) {
                        System.err.println("Error: " + e.getMessage() + "\n");

                    }
                } while (!validado);

                validado = false;

                do {
                    try {
                        modelo = Consola.leerModelo();
                        furgoneta.setModelo(modelo);

                        validado = true;
                    } catch (Exception e) {
                        System.err.println("Error: " + e.getMessage() + "\n");

                    }
                } while (!validado);

                validado = false;

                do {
                    try {
                        numPlazas = Consola.leerPlazas();
                        furgoneta.setPlazas(numPlazas);

                        validado = true;
                    } catch (Exception e) {
                        System.err.println("Error: " + e.getMessage() + "\n");

                    }
                } while (!validado);

                validado = false;

                do {
                    try {
                        pma = Consola.leerPma();
                        furgoneta.setPma(pma);

                        validado = true;
                    } catch (Exception e) {
                        System.err.println("Error: " + e.getMessage() + "\n");

                    }
                } while (!validado);
                return furgoneta;
            }

            if (vehiculo instanceof Autobus autobus) {

                do {
                    try {
                        marca = Consola.leerMarca();
                        autobus.setMarca(marca);

                        validado = true;
                    } catch (Exception e) {
                        System.err.println("Error: " + e.getMessage() + "\n");

                    }
                } while (!validado);

                validado = false;

                do {
                    try {
                        modelo = Consola.leerModelo();
                        autobus.setModelo(modelo);

                        validado = true;
                    } catch (Exception e) {
                        System.err.println("Error: " + e.getMessage() + "\n");

                    }
                } while (!validado);

                validado = false;

                do {
                    try {
                        numPlazas = Consola.leerPlazas();
                        autobus.setPlazas(numPlazas);

                        validado = true;
                    } catch (Exception e) {
                        System.err.println("Error: " + e.getMessage() + "\n");

                    }
                } while (!validado);
                return autobus;
            }
        }
        return null;
    }
}

