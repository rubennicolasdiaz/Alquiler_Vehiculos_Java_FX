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
import org.rubennicolas.alquilervehiculos.AppContextThread;
import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantesFxml;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantesImagenes;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.EstilosConstantes.STYLE_COLUMN;

public class ControllerVistaClientes extends ControllerVistaPrincipal implements Initializable {

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

    private Controlador controlador;

    @Override
    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    public Controlador getControlador() {
        return controlador;
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

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

            listaClientes = FXCollections.observableArrayList();
            listaClientes.setAll(getControlador().getClientes());

            listaClientesVisible = FXCollections.observableArrayList();
            listaClientesVisible.setAll(getControlador().getClientes());

            this.colNombreApellidos.setCellValueFactory(new PropertyValueFactory<>("nombreApellidos"));
            this.colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
            this.colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
            this.colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

            //Estilo de elementos de columnas:
            colNombreApellidos.setStyle(STYLE_COLUMN);
            colDni.setStyle(STYLE_COLUMN);
            colTelefono.setStyle(STYLE_COLUMN);
            colEmail.setStyle(STYLE_COLUMN);

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
            FXMLLoader loader = new FXMLLoader(
                    RutasConstantesFxml.get(RutasConstantesFxml.VISTA_ADD_CLIENTES));
            Parent root = loader.load();

            ControllerAddClientes controlador = loader.getController();
            controlador.initAtributtes(listaClientes);

            Scene scene = new Scene(root);
            Stage stage = new Stage();

            Image icono = RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE_ALQUILER);
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
                FXMLLoader loader = new FXMLLoader(
                        RutasConstantesFxml.get(RutasConstantesFxml.VISTA_EDIT_CLIENTES));

                // Cargo la ventana
                Parent root = loader.load();

                // Se crea una instancia del controlador de EditClientes: 

                ControllerEditClientes controlador = loader.getController();
                controlador.initAtributtes(listaClientes, cliente);

                // Se crean Stage y Scene: 

                Scene scene = new Scene(root);
                Stage stage = new Stage();

                Image icono = RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE_ALQUILER);
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
                    getControlador().modificarCliente(clienteSeleccionado);
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
            FXMLLoader loader = new FXMLLoader(
                    RutasConstantesFxml.get(RutasConstantesFxml.VISTA_CLIENTE_ALQUILER));

            // Cargo la ventana
            Parent root = loader.load();

            // Se crean Stage y Scene:
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            Image icono = RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE_ALQUILER);
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

                getControlador().borrarCliente(cliente);

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