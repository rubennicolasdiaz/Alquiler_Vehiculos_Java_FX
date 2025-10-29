package org.rubennicolas.alquilervehiculos;

import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.modelo.FactoriaFuenteDatos;
import org.rubennicolas.alquilervehiculos.modelo.Modelo;
import org.rubennicolas.alquilervehiculos.modelo.ModeloCascada;
import org.rubennicolas.alquilervehiculos.vista.FactoriaVistas;
import org.rubennicolas.alquilervehiculos.vista.Vista;
import org.rubennicolas.alquilervehiculos.vista.grafica.VistaGrafica;

import javax.swing.*;
import java.awt.*;

public class MainApp {

    public static void main(String[] args) {

        FactoriaFuenteDatos fuenteDatos;
        FactoriaVistas fuenteVistas;

        Modelo modelo;
        Vista vista;

        boolean tieneEntornoGrafico = !GraphicsEnvironment.isHeadless();

        if (tieneEntornoGrafico) {

            FactoriaVistas[] tiposVistas = FactoriaVistas.values();
            JComboBox<FactoriaVistas> comboVistas = new JComboBox<>(tiposVistas);


            JOptionPane.showConfirmDialog(
                    null, comboVistas,
                    "Seleccionar la Vista",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );


            fuenteVistas = (FactoriaVistas) comboVistas.getSelectedItem();

            vista = fuenteVistas.crear();

            FactoriaFuenteDatos[] tiposDatos = FactoriaFuenteDatos.values();
            JComboBox<FactoriaFuenteDatos> comboDatos = new JComboBox<>(tiposDatos);

            int opcionDatos = JOptionPane.showConfirmDialog(
                    null, comboDatos,
                    "Seleccionar Fuente de Datos",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (opcionDatos == JOptionPane.CANCEL_OPTION) {
                System.out.println("[INFO] Aplicación cancelada por el usuario.");
                return;
            }

            fuenteDatos = (FactoriaFuenteDatos) comboDatos.getSelectedItem();

            modelo = new ModeloCascada(fuenteDatos);

            Controlador controlador = new Controlador(modelo, vista);

            vista.setControlador(controlador);


            if (vista instanceof VistaGrafica) {

                // 🔹 Guardamos el controlador y modelo en el hilo puente
                AppContextThread.iniciarContexto(controlador, modelo);

              /*  🔹 Lanza JavaFX (que se ejecutará en otro hilo)
                vista.comenzar();    */
            }
            controlador.comenzar();


        } else {
            // ⚙️ MODO HEADLESS
            System.out.println("[INFO] No se detectó entorno gráfico. Modo headless activado.");

            String fuenteDatosEnv = System.getenv().getOrDefault("APP_DATASOURCE", "ficheros").toUpperCase();
            String fuenteVistaEnv = System.getenv().getOrDefault("APP_VIEW", "texto").toUpperCase();

            try {
                fuenteDatos = FactoriaFuenteDatos.valueOf(fuenteDatosEnv);
            } catch (IllegalArgumentException e) {
                System.err.printf("[WARN] Fuente de datos '%s' no válida. Se usará 'FICHEROS'.%n", fuenteDatosEnv);
                fuenteDatos = FactoriaFuenteDatos.FICHEROS;
            }

            try {
                fuenteVistas = FactoriaVistas.valueOf(fuenteVistaEnv);
            } catch (IllegalArgumentException e) {
                System.err.printf("[WARN] Vista '%s' no válida. Se usará 'TEXTO'.%n", fuenteVistaEnv);
                fuenteVistas = FactoriaVistas.TEXTO;
            }

            modelo = new ModeloCascada(fuenteDatos);
            vista = fuenteVistas.crear();

            Controlador controlador = new Controlador(modelo, vista);
            vista.setControlador(controlador);
            controlador.comenzar();
        }
    }
}