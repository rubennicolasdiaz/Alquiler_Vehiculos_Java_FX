package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantes;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class ControllerVistaClientes implements Initializable {

    @FXML
    private TextField campoBuscar;

    @FXML
    private TableColumn<Cliente, String> colNombreApellidos;

    @FXML
    private TableColumn<Cliente, String> colDni;

    @FXML
    private TableColumn<Cliente, String> colTelefono;

    @FXML
    private TableColumn<Cliente, String> colEmail;

    @FXML
    protected TableView<Cliente> tablaClientes;

    private ObservableList<Cliente> listaClientes;

    private ObservableList<Cliente> listaClientesVisible;

    protected Cliente registro;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        try {
            listaClientes = FXCollections.observableArrayList();
            listaClientes.setAll(Controlador.getInstancia().getClientes());

            listaClientesVisible = FXCollections.observableArrayList();
            listaClientesVisible.setAll(Controlador.getInstancia().getClientes());

            this.colNombreApellidos.setCellValueFactory(new PropertyValueFactory<>("nombreApellidos"));
            this.colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
            this.colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
            this.colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

            //Estilo de elementos de columnas:
            colNombreApellidos.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
            colDni.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
            colTelefono.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
            colEmail.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

            this.tablaClientes.setItems(listaClientesVisible);
            this.tablaClientes.refresh();

        } catch (DomainException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void addCliente() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistasfxml/VistaAddClientes.fxml"));
            Parent root = loader.load();

            ControllerAddClientes controlador = loader.getController();
            controlador.initAtributtes(listaClientes);

            Scene scene = new Scene(root);
            Stage stage = new Stage();

            Image icono = new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.COCHE_ALQUILER)).toExternalForm());
            stage.getIcons().add(icono);

            stage.setTitle("Añadir Cliente");

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            // Se recupera el cliente devuelto: 
            Cliente cliente = controlador.getCliente();

            if (cliente != null) {

                listaClientes.add(cliente);

                if (cliente.getNombreApellidos().toLowerCase().contains(this.campoBuscar.getText().toLowerCase())) {
                    this.listaClientesVisible.add(cliente);
                }
                this.tablaClientes.refresh();
            }

        } catch (IOException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void buscar() {

        String filtroNombre = this.campoBuscar.getText();

        if (filtroNombre.isEmpty()) {

            this.tablaClientes.setItems(listaClientes);

        } else {

            this.listaClientesVisible.clear();

            for (Cliente cliente : this.listaClientes) {

                if (cliente.getNombreApellidos().toLowerCase().contains(filtroNombre.toLowerCase())) {

                    this.listaClientesVisible.add(cliente);
                }
            }
            this.tablaClientes.setItems(listaClientesVisible);
        }
    }

    @FXML
    void editCliente() {

        Cliente cliente = this.tablaClientes.getSelectionModel().getSelectedItem();

        if (cliente == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("No se ha seleccionado ningún cliente");
            alert.showAndWait();

        } else {

            try {

                // Cargo la vista
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistasfxml/VistaEditClientes.fxml"));

                // Cargo la ventana
                Parent root = loader.load();

                // Se crea una instancia del controlador de EditClientes: 

                ControllerEditClientes controlador = loader.getController();
                controlador.initAtributtes(listaClientes, cliente);

                // Se crean Stage y Scene: 

                Scene scene = new Scene(root);
                Stage stage = new Stage();

                Image icono = new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.COCHE_ALQUILER)).toExternalForm());
                stage.getIcons().add(icono);

                stage.setTitle("Modificar Cliente");

                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.showAndWait();

                // Se recupera el cliente devuelto:
                Cliente clienteSeleccionado = controlador.getCliente();

                if (clienteSeleccionado != null) {

                    this.tablaClientes.refresh();

                    if (!clienteSeleccionado.getNombreApellidos().toLowerCase().contains(this.campoBuscar.getText().toLowerCase())) {

                        this.listaClientesVisible.remove(clienteSeleccionado);
                    }

                    Controlador.getInstancia().modificarCliente(clienteSeleccionado);
                }

            } catch (IOException e) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Error");
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    void verAlquileres() {

        try {
            // Cargo la vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/vistasfxml/VistaClienteAlquiler.fxml"));

            // Cargo la ventana
            Parent root = loader.load();

            // Se crean Stage y Scene:

            Scene scene = new Scene(root);
            Stage stage = new Stage();

            Image icono = new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.COCHE_ALQUILER)).toExternalForm());
            stage.getIcons().add(icono);

            stage.setTitle("Ver Alquileres Cliente");

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void deleteCliente() {

        Cliente cliente = this.tablaClientes.getSelectionModel().getSelectedItem();

        if (cliente == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("No se ha seleccionado ningún cliente");
            alert.showAndWait();

        } else {

            // Personalizar botones
            ButtonType botonConfirmar = new ButtonType("Confirmar");
            ButtonType botonCancelar = new ButtonType("Cancelar", ButtonType.CANCEL.getButtonData());

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setTitle("Info");
            alert.setContentText("¿Está seguro que desea eliminar el cliente seleccionado?: \n" +
                    cliente.getNombreApellidos() + " " + cliente.getDni());

            alert.getButtonTypes().setAll(botonConfirmar, botonCancelar);

            Optional<ButtonType> resultado = alert.showAndWait();

            if (resultado.isPresent() && resultado.get() == botonConfirmar) {
                this.listaClientes.remove(cliente);
                this.listaClientesVisible.remove(cliente);

                Controlador.getInstancia().borrarCliente(cliente);

                // Crear y mostrar alerta de confirmación
                Alert confirmacion = new Alert(Alert.AlertType.INFORMATION);
                confirmacion.setHeaderText(null);
                confirmacion.setTitle("Info");
                confirmacion.setContentText("Cliente: " + cliente.getNombreApellidos() + " " + cliente.getDni() + "\nEliminado correctamente");
                confirmacion.showAndWait();

                this.tablaClientes.refresh();
            } else {
                alert.close();
            }
        }
    }

    @FXML
    void seleccionar() {
        this.registro = this.tablaClientes.getSelectionModel().getSelectedItem();
    }

    @FXML
    void volver(ActionEvent event) {

        Stage escenarioActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        escenarioActual.close();
    }
}