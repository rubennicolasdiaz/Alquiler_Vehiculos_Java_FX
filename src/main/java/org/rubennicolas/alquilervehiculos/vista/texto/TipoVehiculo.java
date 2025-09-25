package org.rubennicolas.alquilervehiculos.vista.texto;

public enum TipoVehiculo {

    TURISMO("Turismo"), AUTOBUS("Autobús"), FURGONETA("Furgoneta");

    private final String nombre;

    TipoVehiculo(String nombre) {
        this.nombre = nombre;
    }

    public static boolean esOrdinalValido(int ordinal) {
        return (ordinal >= 0 && ordinal <= Accion.values().length - 1);
    }

    public static TipoVehiculo get(int ordinal) {

        if (esOrdinalValido(ordinal)) {
            return values()[ordinal];
        } else
            throw new IllegalArgumentException("Ordinal de la opción no válido, elija un valor del 0 al 2.");
    }

    @Override
    public String toString() {
        return switch (this) {
            case TURISMO -> "Turismo";
            case AUTOBUS -> "Autobús";
            case FURGONETA -> "Furgoneta";
        };
    }
}