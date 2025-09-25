package org.rubennicolas.alquilervehiculos.vista.texto;

public enum Accion {
    INSERTAR_CLIENTE("Insertar Cliente") {
        @Override
        public void ejecutar() {
            vista.insertarCliente();
        }
    },
    INSERTAR_VEHICULO("Insertar Vehículo") {
        @Override
        public void ejecutar() {
            vista.insertarVehiculo();
        }
    },
    INSERTAR_ALQUILER("Insertar Alquiler") {
        @Override
        public void ejecutar() {
            vista.insertarAlquiler();
        }
    },
    BUSCAR_CLIENTE("Buscar Cliente") {
        @Override
        public void ejecutar() {
            vista.buscarCliente();
        }
    },
    BUSCAR_VEHICULO("Buscar Vehículo") {
        @Override
        public void ejecutar() {
            vista.buscarVehiculo();
        }
    },
    MODIFICAR_CLIENTE("Modificar Cliente") {
        @Override
        public void ejecutar() {
            vista.modificarCliente();
        }
    },
    MODIFICAR_VEHICULO("Modificar Vehículo") {
        @Override
        public void ejecutar() {
            vista.modificarVehiculo();
        }
    },
    DEVOLVER_ALQUILER("Devolver Alquiler") {
        @Override
        public void ejecutar() {
            vista.devolverAlquiler();
        }
    },
    BORRAR_CLIENTE("Borrar Cliente") {
        @Override
        public void ejecutar() {
            vista.borrarCliente();
        }
    },
    BORRAR_VEHICULO("Borrar Vehículo") {
        @Override
        public void ejecutar() {
            vista.borrarVehiculo();
        }
    },
    BORRAR_ALQUILER("Borrar Alquiler") {
        @Override
        public void ejecutar() {
            vista.borrarAlquiler();
        }
    },
    LISTAR_CLIENTES("Listar Clientes") {
        @Override
        public void ejecutar() {
            vista.listarClientes();
        }
    },
    LISTAR_VEHICULOS("Listar Vehículos") {
        @Override
        public void ejecutar() {
            vista.listarVehiculos();
        }
    },
    LISTAR_ALQUILERES("Listar Alquileres") {
        @Override
        public void ejecutar() {
            vista.listarAlquileres();
        }
    },
    LISTAR_ALQUILERES_CLIENTE("Listar Alquileres por Cliente") {
        @Override
        public void ejecutar() {
            vista.listarAlquileresPorCliente();
        }
    },
    LISTAR_ALQUILERES_VEHICULO("Listar Alquileres por Vehículo") {
        @Override
        public void ejecutar() {
            vista.listarAlquileresPorVehiculo();
        }
    },
    MOSTRAR_ESTADISTICAS_MENSUALES("Mostrar Estadisticas Mensuales") {
        @Override
        public void ejecutar() {

            vista.mostrarEstadisticasMensualesTipoVehiculo();
        }
    },
    SALIR("Salir") {
        @Override
        public void ejecutar() {
            vista.terminar();
        }
    };

    private static VistaTexto vista;

    private final String texto;

    Accion(String texto) {
        this.texto = texto;
    }

    public abstract void ejecutar();

    protected static void setVista(VistaTexto vista) {
        Accion.vista = vista;
    }

    protected static boolean esOrdinalValido(int ordinal) {
        return (ordinal >= 0 && ordinal <= Accion.values().length - 1);
    }

    public static Accion get(int ordinal) {

        if (esOrdinalValido(ordinal)) {
            return values()[ordinal];

        } else {
            throw new IllegalArgumentException("Ordinal de la opción no válido. Elija una opción del 0 al 17.");
        }
    }

    public String toString() {
        return String.format("%d.- %s", ordinal(), texto);
    }
}