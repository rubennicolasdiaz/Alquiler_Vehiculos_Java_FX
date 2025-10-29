package org.rubennicolas.alquilervehiculos.modelo.dominio;

import org.rubennicolas.alquilervehiculos.vista.texto.TipoVehiculo;

public class Autobus extends Vehiculo {

    private static final int FACTOR_PLAZAS = 2;
    private int plazas;

    public Autobus() {
        tipoVehiculo = TipoVehiculo.AUTOBUS;
    }

    public Autobus(String marca, String modelo, String matricula, int plazas) {

        super(marca, modelo, matricula);
        setPlazas(plazas);

        this.tipoVehiculo = TipoVehiculo.AUTOBUS;
    }

    public Autobus(Autobus autobus) {

        this(autobus.getMarca(), autobus.getModelo(), autobus.getMatricula(), autobus.getPlazas());
        this.tipoVehiculo = TipoVehiculo.AUTOBUS;
    }

    public int getPlazas() {
        return plazas;
    }

    public void setPlazas(Integer plazas) {

        if (plazas == null) {
            throw new NullPointerException("El número de plazas no puede ser nulo.");
        }

        if (plazas < 1 || plazas > 50) {
            throw new IllegalArgumentException("El número de plazas no es correcto. Debe estar entre 1 y 50.");
        }
        this.plazas = plazas;
    }

    @Override
    public Vehiculo clonar() {
        return new Autobus(this);
    }

    @Override
    public int getFactorPrecio() {
        return getPlazas() * FACTOR_PLAZAS;
    }

    @Override
    public String toString() {
        return String.format("%s %s - (%s Plazas) - %s", getMarca(), getModelo(), getPlazas(), getMatricula());
    }
}
