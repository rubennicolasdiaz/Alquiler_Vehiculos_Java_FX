package org.rubennicolas.alquilervehiculos.vista;

import org.rubennicolas.alquilervehiculos.controlador.Controlador;

public interface Vista {

    void comenzar();

    void setControlador(Controlador controlador);

    void terminar();
}