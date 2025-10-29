package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.rubennicolas.alquilervehiculos.AppContextThread;
import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ControllerDevolverAlquileres extends ControllerVistaAlquileres {

    @FXML
    private TextField campoCliente;

    @FXML
    private TextField campoVehiculo;

    @FXML
    private TextField campoFechaAlquiler;

    @FXML
    private DatePicker campoFechaDevolucion;

    private Alquiler alquiler;

    @FXML
    private Button buttonGuardar;

    private Controlador controlador;

    @Override
    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    public Controlador getControlador() {
        return controlador;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

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
    }

    public void initAtributtes(ObservableList<Alquiler> listaAlquileres, Alquiler alquiler) {

        this.listaAlquileres = listaAlquileres;
        this.alquiler = alquiler;

        // Se cargan los datos del alquiler: 
        this.campoCliente.setText(alquiler.getCliente().toString());
        this.campoVehiculo.setText(alquiler.getVehiculo().toString());
        this.campoFechaAlquiler.setText(alquiler.getFechaAlquiler().toString());

        this.campoCliente.setDisable(true);
        this.campoVehiculo.setDisable(true);
        this.campoFechaAlquiler.setDisable(true);

        this.campoFechaDevolucion.setValue(alquiler.getFechaDevolucion());
    }

    public Alquiler getAlquiler() {
        return this.alquiler;
    }

    @FXML
    void guardar() {
        try {
            LocalDate fechaDevolucion = this.campoFechaDevolucion.getValue();

            if (fechaDevolucion == null) {
                showError("La fecha de devolución no puede ser nula");
                return;
            }
            // Se valida y devuelve el alquiler
            getControlador().devolverAlquiler(this.alquiler, fechaDevolucion);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Información");
            alert.setContentText("Se ha finalizado el alquiler correctamente.");
            alert.showAndWait();

            // Cerrar ventana
            Stage stage = (Stage) this.buttonGuardar.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void showError(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        alert.setContentText("Error: " + error);
        alert.showAndWait();
    }
}