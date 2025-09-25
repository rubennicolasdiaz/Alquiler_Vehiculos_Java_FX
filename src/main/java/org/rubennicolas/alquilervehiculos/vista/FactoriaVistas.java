package org.rubennicolas.alquilervehiculos.vista;

import org.rubennicolas.alquilervehiculos.vista.grafica.VistaGrafica;
import org.rubennicolas.alquilervehiculos.vista.texto.VistaTexto;

public enum FactoriaVistas {

    TEXTO {
        @Override
        public Vista crear() {
            return new VistaTexto();
        }

    }, GRAFICOS {
        @Override
        public Vista crear() {
            return new VistaGrafica();
        }
    };

    public abstract Vista crear();
}
