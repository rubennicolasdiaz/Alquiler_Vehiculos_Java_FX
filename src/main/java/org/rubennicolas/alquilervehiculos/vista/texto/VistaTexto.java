package org.rubennicolas.alquilervehiculos.vista.texto;

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

public class VistaTexto implements Vista {

    private Controlador controlador;

    public VistaTexto() {
        Accion.setVista(this);
    }

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
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
        this.controlador.terminar();
    }

    public void insertarCliente() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.INSERTAR_CLIENTE);

        try {
            Cliente cliente = crearCliente();

            this.controlador.insertarCliente(cliente);
            System.out.println("\nCliente insertado correctamente: " + cliente);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void insertarVehiculo() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.INSERTAR_VEHICULO);

        try {
            Vehiculo vehiculo = crearVehiculo();

            this.controlador.insertarVehiculo(vehiculo);
            System.out.println("\nVehículo insertado correctamente: " + vehiculo);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void insertarAlquiler() {

        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.INSERTAR_ALQUILER);

        try {
            Alquiler alquiler = crearAlquiler();

            this.controlador.insertarAlquiler(alquiler);
            System.out.println("\nAlquiler insertado correctamente: " + alquiler);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public Cliente buscarCliente() {

        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.BUSCAR_CLIENTE);

        Cliente cliente = null;
        try {
            cliente = this.controlador.buscarCliente(Consola.leerClienteDni());

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
            vehiculo = this.controlador.buscarVehiculo(Consola.leerVehiculoMatricula());

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
            Cliente cliente = editarCliente();

            this.controlador.modificarCliente(cliente);
            System.out.println("\nCliente modificado correctamente: " + cliente);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void modificarVehiculo() {

        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.MODIFICAR_VEHICULO);

        try {
            Vehiculo vehiculo = editarVehiculo();

            this.controlador.modificarVehiculo(vehiculo);
            System.out.println("\nVehículo modificado correctamente: " + vehiculo);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void devolverAlquiler() {

        Consola.mostrarCabecera("\nHa elegido la opción: " + Accion.DEVOLVER_ALQUILER);

        try {
            Alquiler alquiler = this.controlador.buscarAlquiler(Consola.leerIdAlquiler());

            if (alquiler == null) {
                throw new DomainException("El ID del alquiler no se encuentra registrado.");
            }

            if (alquiler.getFechaDevolucion() != null) {
                throw new DomainException("No se puede devolver un alquiler ya devuelto.");
            }

            LocalDate fechaDevolucion = Consola.leerFecha();

            this.controlador.devolverAlquiler(alquiler, fechaDevolucion);
            System.out.println("Alquiler devuelto correctamente: " + this.controlador.buscarAlquiler(alquiler.getId()));

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void borrarCliente() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.BORRAR_CLIENTE);

        try {
            Cliente clienteOptional = Consola.leerClienteDni();

            Cliente clienteBuscado = this.controlador.buscarCliente(clienteOptional);

            if (clienteBuscado != null) {

                this.controlador.getAlquileresPorCliente(clienteBuscado)
                        .forEach(alquiler -> {
                            this.controlador.borrarAlquiler(alquiler);
                        });
                this.controlador.borrarCliente(clienteBuscado);
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

            Vehiculo vehiculoBuscado = this.controlador.buscarVehiculo(vehiculoOptional);

            if (vehiculoBuscado != null) {

                this.controlador.getAlquileresPorVehiculo(vehiculoBuscado)
                        .forEach(alquiler -> {
                            this.controlador.borrarAlquiler(alquiler);
                        });
                this.controlador.borrarVehiculo(vehiculoBuscado);
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

        Alquiler alquilerBuscado = this.controlador.buscarAlquiler(Consola.leerIdAlquiler());

        if (alquilerBuscado != null) {
            this.controlador.borrarAlquiler(alquilerBuscado);
            System.out.println("Alquiler Borrado: " + alquilerBuscado);
        } else {
            System.err.println("Error: El Id de alquiler no existe.");
        }
    }

    public void listarClientes() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.LISTAR_CLIENTES);

        try {
            List<Cliente> clientes = this.controlador.getClientes();
            clientes.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void listarVehiculos() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.LISTAR_VEHICULOS);

        try {
            List<Vehiculo> vehiculos = this.controlador.getVehiculos();
            vehiculos.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void listarAlquileres() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.LISTAR_ALQUILERES);

        try {
            List<Alquiler> alquileres = this.controlador.getAlquileres();
            alquileres.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    public void listarAlquileresPorCliente() {
        Consola.mostrarCabecera("Ha elegido la opción: " + Accion.LISTAR_ALQUILERES_CLIENTE);

        try {
            Cliente cliente = Consola.leerClienteDni();

            List<Alquiler> alquileresCliente = this.controlador
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

            List<Alquiler> alquileresVehiculo = this.controlador
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
            for (Alquiler alquiler : this.controlador.getAlquileres()) {

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

    private Cliente crearCliente() {
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

    private Vehiculo crearVehiculo() {

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

    private Alquiler crearAlquiler() {

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
                cliente = controlador.buscarCliente(Consola.leerClienteDni());

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
                vehiculo = controlador.buscarVehiculo(Consola.leerVehiculoMatricula());

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

    private Cliente editarCliente() {

        Cliente cliente = controlador.buscarCliente(Consola.leerClienteDni());

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

    private Vehiculo editarVehiculo() {

        Vehiculo vehiculo = controlador.buscarVehiculo(Consola.leerVehiculoMatricula());

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