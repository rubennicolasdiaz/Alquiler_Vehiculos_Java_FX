package org.rubennicolas.alquilervehiculos.vista.grafica;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.FactoriaFuenteDatos;
import org.rubennicolas.alquilervehiculos.vista.Vista;
import org.rubennicolas.alquilervehiculos.vista.grafica.controllers.ControllerVistaPrincipal;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantesFxml;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantesImagenes;

import javax.swing.*;

public class VistaGrafica extends Application implements Vista {

    @Override
    public void setControlador(Controlador controlador) {

    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    RutasConstantesFxml.get(RutasConstantesFxml.VISTA_PRINCIPAL)
            );

            Parent root = loader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Gestión de Alquiler de Vehículos");
            stage.getIcons().add(RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE_ALQUILER));


            ControllerVistaPrincipal controllerVista = loader.getController();

            controllerVista.setStage(stage);

            stage.show();


        } catch (Exception e) {
            throw new DomainException("Error al iniciar la aplicación: " + e.getMessage());
        }
    }


    private FactoriaFuenteDatos obtenerFuenteDeDatosConJOptionPane() {

        // 1️⃣ Elegir tipo de datos
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
            System.exit(0);
        }

        return (FactoriaFuenteDatos) comboDatos.getSelectedItem();
    }

    @Override
    public void comenzar() {
        launch();
    }

    @Override
    public void terminar() {

    }
}