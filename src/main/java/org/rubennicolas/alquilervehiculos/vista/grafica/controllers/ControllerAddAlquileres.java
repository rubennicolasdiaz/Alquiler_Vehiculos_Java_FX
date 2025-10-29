package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.rubennicolas.alquilervehiculos.AppContextThread;
import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerAddAlquileres extends ControllerVistaAlquileres {

    @FXML
    private ChoiceBox<Cliente> selCliente;

    @FXML
    private ChoiceBox<Vehiculo> selVehiculo;

    @FXML
    private DatePicker selFechaAlquiler;

    private Alquiler alquiler;

    private ObservableList<Alquiler> listaAlquileres;

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
        
        List<Cliente> listaClientes = getControlador().getClientes();
        List<Vehiculo> listaVehiculos = getControlador().getVehiculos();

        // Agregar la lista de clientes y vehículos a las ChoiceBox
        selCliente.getItems().addAll(listaClientes);
        selVehiculo.getItems().addAll(listaVehiculos);
    }


    public void initAtributtes(ObservableList<Alquiler> listaAlquileres) {
        this.listaAlquileres = listaAlquileres;
    }

    public Alquiler getAlquiler() {
        return this.alquiler;
    }

    @FXML
    void guardar() {

        try {
            // Se recuperan los datos del alquiler:
            Cliente cliente = this.selCliente.getValue();
            Vehiculo vehiculo = this.selVehiculo.getValue();
            LocalDate fechaAlquiler = selFechaAlquiler.getValue();

            // Se crea el alquiler:
            Alquiler alquiler = new Alquiler(cliente, vehiculo, fechaAlquiler);

            getControlador().insertarAlquiler(alquiler);

            if (!listaAlquileres.contains(alquiler)) {

                if (this.alquiler == null) {

                    // Se inserta el alquiler:
                    this.alquiler = alquiler;

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("Información");
                    alert.setContentText("Se ha añadido el alquiler correctamente");
                    alert.showAndWait();

                    // Cerrar la ventana:
                    Stage stage = (Stage) this.buttonGuardar.getScene().getWindow();
                    stage.close();

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setTitle("Error");
                    alert.setContentText("El Alquiler ya existe");
                    alert.showAndWait();

                    // Cerrar la ventana:
                    Stage stage = (Stage) this.buttonGuardar.getScene().getWindow();
                    stage.close();
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
