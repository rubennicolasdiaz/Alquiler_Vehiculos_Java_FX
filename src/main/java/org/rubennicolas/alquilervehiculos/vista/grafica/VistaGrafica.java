package org.rubennicolas.alquilervehiculos.vista.grafica;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.vista.Vista;
import org.rubennicolas.alquilervehiculos.vista.grafica.controllers.ControllerVistaPrincipal;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantes;

import java.io.IOException;
import java.util.Objects;

public class VistaGrafica extends Vista {

    public VistaGrafica() {

    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/vistasfxml/VistaPrincipal.fxml"));
            Parent raiz = fxmlLoader.load();
            Scene escena = new Scene(raiz);

            Image icono = new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.COCHE_ALQUILER)).toExternalForm());
            stage.getIcons().add(icono);

            stage.setTitle("Aplicación Gestión de Alquiler de Vehículos");

            ControllerVistaPrincipal controller = fxmlLoader.getController();
            controller.setStage(stage);

            stage.setScene(escena);
            stage.show();

        } catch (IllegalStateException | IllegalArgumentException | IOException e) {
            throw new DomainException(e.getMessage());
        }
    }

    @Override
    public void comenzar() {
        launch();
        Stage stage = new Stage();
        start(stage);
    }

    @Override
    public void terminar() {
    }
}

