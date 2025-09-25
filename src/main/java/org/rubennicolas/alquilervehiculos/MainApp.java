package org.rubennicolas.alquilervehiculos;

import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.modelo.FactoriaFuenteDatos;
import org.rubennicolas.alquilervehiculos.vista.FactoriaVistas;

import javax.swing.*;

public class MainApp {

    public static void main(String[] args) {

        FactoriaFuenteDatos[] tiposDatos = FactoriaFuenteDatos.values();
        JComboBox<FactoriaFuenteDatos> comboDatos = new JComboBox<>(tiposDatos);

        FactoriaVistas[] tiposVistas = FactoriaVistas.values();
        JComboBox<FactoriaVistas> comboVistas = new JComboBox<>(tiposVistas);

        JOptionPane.showConfirmDialog(
                null,
                comboDatos,
                "Seleccionar Fuente de Datos",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        JOptionPane.showConfirmDialog(
                null,
                comboVistas,
                "Seleccionar la Vista",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        FactoriaFuenteDatos fuenteDatos = (FactoriaFuenteDatos) comboDatos.getSelectedItem();
        FactoriaVistas fuenteVistas = (FactoriaVistas) comboVistas.getSelectedItem();

        Controlador controlador = new Controlador(fuenteDatos, fuenteVistas);

        Controlador.getInstancia().comenzar();
    }
}


