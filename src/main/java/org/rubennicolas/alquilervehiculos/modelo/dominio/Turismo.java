package org.rubennicolas.alquilervehiculos.modelo.dominio;

import org.rubennicolas.alquilervehiculos.vista.texto.TipoVehiculo;

public class Turismo extends Vehiculo {

    private static final int FACTOR_CILINDRADA = 10;

    private int cilindrada;

    public Turismo() {
        tipoVehiculo = TipoVehiculo.TURISMO;
    }

    public Turismo(String marca, String modelo, String matricula, int cilindrada) {

        super(marca, modelo, matricula);
        setCilindrada(cilindrada);

        this.tipoVehiculo = TipoVehiculo.TURISMO;
    }

    public Turismo(Turismo turismo) {
        this(turismo.getMarca(), turismo.getModelo(), turismo.getMatricula(), turismo.getCilindrada());
        this.tipoVehiculo = TipoVehiculo.TURISMO;
    }

    public int getCilindrada() {
        return cilindrada;
    }

    public void setCilindrada(Integer cilindrada) {

        if (cilindrada == null) {
            throw new NullPointerException("La cilindrada no puede ser nula");
        }

        if (cilindrada < 1 || cilindrada > 5000) {
            throw new IllegalArgumentException("La cilindrada no es correcta. Debe estar entre 1 y 5000.");
        }
        this.cilindrada = cilindrada;
    }

    @Override
    public Vehiculo clonar() {
        return new Turismo(this);
    }

    @Override
    public int getFactorPrecio() {
        return getCilindrada() / FACTOR_CILINDRADA;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s CC) - %s", getMarca(), getModelo(), getCilindrada(), getMatricula());
    }
}