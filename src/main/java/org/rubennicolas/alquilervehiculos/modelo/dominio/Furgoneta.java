package org.rubennicolas.alquilervehiculos.modelo.dominio;

import org.rubennicolas.alquilervehiculos.vista.texto.TipoVehiculo;

public class Furgoneta extends Vehiculo {

    private static final int FACTOR_PMA = 100;
    private static final int FACTOR_PLAZAS = 1;

    private int pma;
    private int plazas;

    public Furgoneta() {
        tipoVehiculo = TipoVehiculo.FURGONETA;
    }

    public Furgoneta(String marca, String modelo, String matricula, int plazas, int pma) {

        super(marca, modelo, matricula);
        setPlazas(plazas);
        setPma(pma);

        this.tipoVehiculo = TipoVehiculo.FURGONETA;
    }

    public Furgoneta(Furgoneta furgoneta) {

        super(furgoneta.getMarca(), furgoneta.getModelo(), furgoneta.getMatricula());
        setPlazas(furgoneta.getPlazas());
        setPma(furgoneta.getPma());
    }

    public int getPma() {
        return pma;
    }

    public void setPma(Integer pma) {

        if (pma == null) {
            throw new NullPointerException("El PMA no puede ser nulo.");
        }

        if (pma < 1 || pma > 1500) {
            throw new IllegalArgumentException("El PMA debe estar entre 1 y 1500 Kg.");
        }
        this.pma = pma;
    }

    public int getPlazas() {
        return plazas;
    }

    public void setPlazas(Integer plazas) {

        if (plazas == null) {
            throw new NullPointerException("El número de plazas no puede ser nulo");
        }
        if (plazas < 1 || plazas > 10) {
            throw new IllegalArgumentException("El número de plazas no es correcto. Debe estar entre 1 y 10.");
        }
        this.plazas = plazas;
    }

    @Override
    public int getFactorPrecio() {

        return (getPma() / FACTOR_PMA) + (getPlazas() * FACTOR_PLAZAS);
    }

    @Override
    public String toString() {
        return String.format("%s %s - (%s Kg. PMA) - (%s Plazas) - %s", getMarca(), getModelo(), getPma(), getPlazas(), getMatricula());
    }
}
