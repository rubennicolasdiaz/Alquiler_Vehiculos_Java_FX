package org.rubennicolas.alquilervehiculos.vista.texto;

public enum TipoVehiculo {

    TURISMO("TURISMO"), AUTOBUS("AUTOBUS"), FURGONETA("FURGONETA");

    private final String nombre;

    TipoVehiculo(String nombre) {
        this.nombre = nombre;
    }

    public static boolean esOrdinalValido(int ordinal) {
        return (ordinal >= 0 && ordinal <= TipoVehiculo.values().length - 1);
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
            case TURISMO -> "TURISMO";
            case AUTOBUS -> "AUTOBUS";
            case FURGONETA -> "FURGONETA";
        };
    }
}