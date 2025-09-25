package org.rubennicolas.alquilervehiculos.modelo.dominio;

import org.rubennicolas.alquilervehiculos.vista.texto.TipoVehiculo;

import java.util.Objects;

public abstract class Vehiculo implements Comparable<Vehiculo> {

    protected static final String ER_MATRICULA = "^[0-9]{4}[B-Zb-z]{3}$";

    private String marca;
    private String modelo;
    protected String matricula;
    protected TipoVehiculo tipoVehiculo;

    /* La clase abstracta Vehículo contiene los atributos y métodos comunes
     * que heredarán las 3 clases hijas: Turismo, Autobus, Furgoneta */

    protected Vehiculo() {
    }

    protected Vehiculo(String marca, String modelo, String matricula) {

        setMarca(marca);
        setModelo(modelo);
        setMatricula(matricula);
    }

    protected Vehiculo(Vehiculo vehiculo) {

        setMarca(vehiculo.getMarca());
        setModelo(vehiculo.getModelo());
        setMatricula(vehiculo.getMatricula());
        tipoVehiculo = vehiculo.getTipoVehiculo();
    }

    public abstract int getFactorPrecio();

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {

        if (marca == null) {
            throw new NullPointerException("La marca no puede ser nula.");
        }

        if (marca.isBlank()) {
            throw new IllegalArgumentException("La marca no puede estar en blanco.");
        }
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {

        if (modelo == null) {
            throw new NullPointerException("El modelo no puede ser nulo.");
        }
        if (modelo.isBlank()) {
            throw new IllegalArgumentException("El modelo no puede estar en blanco.");
        }
        this.modelo = modelo;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {

        if (matricula == null) {
            throw new NullPointerException("La matrícula no puede ser nula.");
        }

        if (matricula.isBlank()) {
            throw new IllegalArgumentException("La matrícula no puede estar en blanco.");
        }

        if (!matricula.matches(ER_MATRICULA)) {
            throw new IllegalArgumentException("La matrícula no tiene un formato válido. Se requiere formato: 9999BBB");
        }
        this.matricula = matricula.toUpperCase();
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(matricula);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vehiculo other)) {
            return false;
        }
        return Objects.equals(matricula, other.matricula);
    }

    @Override
    public int compareTo(Vehiculo otroVehiculo) {
        return this.marca.compareToIgnoreCase(otroVehiculo.marca);
    }
}

