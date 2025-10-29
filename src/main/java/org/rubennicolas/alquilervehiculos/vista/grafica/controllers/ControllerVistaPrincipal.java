package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import com.mongodb.MongoException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.rubennicolas.alquilervehiculos.AppContextThread;
import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.vista.grafica.VistaGrafica;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantesFxml;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantesImagenes;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerVistaPrincipal extends VistaGrafica implements Initializable {

    private Controlador controlador;

    @Override
    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    public Controlador getControlador() {
        return controlador;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            Controlador controladorOriginal = AppContextThread.getControlador();
            if (controladorOriginal == null) {
                System.err.println("[ERROR] No se pudo obtener el controlador desde AppContextThread.");
            } else {
                this.controlador = controladorOriginal;

                this.controlador.setVista(this);


                controlador.getVista().setControlador(controlador);

                controlador.getModelo().comenzar();
                System.out.println("[INFO] Controlador inyectado correctamente en ControllerVistaPrincipal.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void setStage(Stage stage) {

        stage.setOnCloseRequest(event -> {
            event.consume(); // evita cierre automático
            terminar();
        });
    }

    @FXML
    void gestionarClientes() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(RutasConstantesFxml.VISTA_CLIENTES));
            Parent raiz = fxmlLoader.load();

            Scene escena = new Scene(raiz);

            // Creamos el escenario
            Stage nuevoEscenario = new Stage();
            nuevoEscenario.initModality(Modality.APPLICATION_MODAL);

            Image icono = RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE_ALQUILER);
            nuevoEscenario.getIcons().add(icono);
            nuevoEscenario.setTitle("Gestión de Clientes");

            // Establecemos la escena
            nuevoEscenario.setScene(escena);
            nuevoEscenario.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void gestionarVehiculos() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(RutasConstantesFxml.VISTA_VEHICULOS));
            Parent raiz = fxmlLoader.load();
            Scene escena = new Scene(raiz);

            // Creamos el escenario
            Stage nuevoEscenario = new Stage();
            nuevoEscenario.initModality(Modality.APPLICATION_MODAL);

            Image icono = RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE_ALQUILER);
            nuevoEscenario.getIcons().add(icono);
            nuevoEscenario.setTitle("Gestión de Vehículos");

            // Establecemos la escena
            nuevoEscenario.setScene(escena);
            nuevoEscenario.show();
        } catch (IOException | MongoException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void gestionarAlquileres() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(RutasConstantesFxml.VISTA_ALQUILERES));
            Parent raiz = fxmlLoader.load();

            Scene escena = new Scene(raiz);

            // Creamos el escenario
            Stage nuevoEscenario = new Stage();
            nuevoEscenario.initModality(Modality.APPLICATION_MODAL);

            Image icono = RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE_ALQUILER);
            nuevoEscenario.getIcons().add(icono);

            nuevoEscenario.setTitle("Gestión de Alquileres");
            nuevoEscenario.setScene(escena);
            nuevoEscenario.show();

        } catch (IOException | MongoException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void terminar() {

        Image icono = RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE_ALQUILER);

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
        getControlador().terminar();
    }
}