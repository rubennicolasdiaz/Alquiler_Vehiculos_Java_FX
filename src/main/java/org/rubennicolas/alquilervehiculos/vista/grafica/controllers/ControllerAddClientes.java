package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.rubennicolas.alquilervehiculos.AppContextThread;
import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerAddClientes extends ControllerVistaClientes {

    @FXML
    private TextField campoNombreApellidos;

    @FXML
    private TextField campoDni;

    @FXML
    private TextField campoTelefono;

    @FXML
    private TextField campoEmail;

    private Cliente cliente;

    private ObservableList<Cliente> listaClientes;

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

    public void initAtributtes(ObservableList<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }

    public Cliente getCliente() {
        return this.cliente;
    }

    @FXML
    void guardar() {

        try {
            String nombre = this.campoNombreApellidos.getText();
            String dni = this.campoDni.getText();
            String telefono = this.campoTelefono.getText();
            String email = this.campoEmail.getText();

            // Se crea el cliente:
            Cliente cliente = new Cliente(nombre, dni, telefono, email);

            // Se comprueba si el cliente existe:
            if (!listaClientes.contains(cliente)) {

                if (this.cliente == null) {

                    // Se inserta el cliente:
                    this.cliente = cliente;

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("Información");
                    alert.setContentText("Se ha añadido el cliente correctamente.");
                    alert.showAndWait();
                }

                // Cerrar la ventana
                Stage stage = (Stage) this.buttonGuardar.getScene().getWindow();
                stage.close();
            }

            getControlador().insertarCliente(cliente);

        } catch (Exception e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
