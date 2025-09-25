package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import com.mongodb.MongoException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.vista.grafica.VistaGrafica;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantes;

import java.io.IOException;
import java.util.Objects;

public class ControllerVistaPrincipal extends VistaGrafica {

    public void setStage(Stage stage) {

        stage.setOnCloseRequest(event -> {
            event.consume(); // evita cierre automático
            terminar();
        });
    }

    @FXML
    void gestionarClientes() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                    "/vistasfxml/VistaClientes.fxml"));
            Parent raiz;

            raiz = fxmlLoader.load();
            Scene escena = new Scene(raiz);

            // Creamos el escenario
            Stage nuevoEscenario = new Stage();
            nuevoEscenario.initModality(Modality.APPLICATION_MODAL);

            Image icono = new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.COCHE_ALQUILER)).toExternalForm());
            nuevoEscenario.getIcons().add(icono);
            nuevoEscenario.setTitle("Gestión de Clientes");

            // Establecemos la escena
            nuevoEscenario.setScene(escena);
            nuevoEscenario.show();
        } catch (IOException | MongoException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Error: " + "Fallo al conectar a la base de datos. Comprobar la conexión y si el servidor está disponible.");
            alert.showAndWait();
        }
    }

    @FXML
    void gestionarVehiculos() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                    "/vistasfxml/VistaVehiculos.fxml"));
            Parent raiz;

            raiz = fxmlLoader.load();
            Scene escena = new Scene(raiz);

            // Creamos el escenario
            Stage nuevoEscenario = new Stage();
            nuevoEscenario.initModality(Modality.APPLICATION_MODAL);

            Image icono = new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.COCHE_ALQUILER)).toExternalForm());
            nuevoEscenario.getIcons().add(icono);
            nuevoEscenario.setTitle("Gestión de Vehículos");

            // Establecemos la escena
            nuevoEscenario.setScene(escena);
            nuevoEscenario.show();
        } catch (IOException | MongoException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Error: " + "Fallo al conectar a la base de datos. Comprobar la conexión y si el servidor está disponible.");
            alert.showAndWait();
        }
    }

    @FXML
    void gestionarAlquileres() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                    "/vistasfxml/VistaAlquileres.fxml"));
            Parent raiz;

            raiz = fxmlLoader.load();

            Scene escena = new Scene(raiz);

            // Creamos el escenario
            Stage nuevoEscenario = new Stage();
            nuevoEscenario.initModality(Modality.APPLICATION_MODAL);

            Image icono = new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.COCHE_ALQUILER)).toExternalForm());
            nuevoEscenario.getIcons().add(icono);

            nuevoEscenario.setTitle("Gestión de Alquileres");
            nuevoEscenario.setScene(escena);
            nuevoEscenario.show();

        } catch (IOException | MongoException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Error: " + "Fallo al conectar a la base de datos. Comprobar la conexión y si el servidor está disponible.");
            alert.showAndWait();
        }
    }

    @FXML
    public void terminar() {

        Image icono = new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.COCHE_ALQUILER)).toExternalForm());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        stage.getIcons().add(icono);
        alert.setHeaderText(null);
        alert.setTitle("Información");
        alert.setContentText("La aplicación de alquiler de vehículos va a finalizar, hasta pronto...");

        // Crear un objeto ImageView con la imagen que deseas mostrar
        ImageView imageView = new ImageView(icono);
        imageView.setFitHeight(50); // ajustar la altura del icono
        imageView.setFitWidth(50); // ajustar el ancho del icono

        // Asignar la imagen a la ventana de alerta
        alert.setGraphic(imageView);
        alert.showAndWait();
        Controlador.getInstancia().terminar();
    }
}
