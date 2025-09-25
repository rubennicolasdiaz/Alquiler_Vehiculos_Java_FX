package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import javafx.beans.property.SimpleDoubleProperty;
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
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantes;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class ControllerVistaAlquileres implements Initializable {

    @FXML
    private TextField campoBuscar;

    @FXML
    private TableColumn<Alquiler, Cliente> colCliente;

    @FXML
    private TableColumn<Alquiler, Vehiculo> colVehiculo;

    @FXML
    private TableColumn<Alquiler, Vehiculo> colDibujoVehiculo;

    @FXML
    private TableColumn<Alquiler, LocalDate> colFechaAlquiler;

    @FXML
    private TableColumn<Alquiler, LocalDate> colFechaDevolucion;

    @FXML
    private TableColumn<Alquiler, Double> colGetPrecio;

    @FXML
    protected TableView<Alquiler> tablaAlquileres;

    protected ObservableList<Alquiler> listaAlquileres;

    protected ObservableList<Alquiler> listaAlquileresVisible;

    protected Alquiler registro;

    protected static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        try {
            listaAlquileres = FXCollections.observableArrayList();
            listaAlquileres.setAll(Controlador.getInstancia().getAlquileres());

            listaAlquileresVisible = FXCollections.observableArrayList();
            listaAlquileresVisible.setAll(Controlador.getInstancia().getAlquileres());

            this.colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
            this.colVehiculo.setCellValueFactory(new PropertyValueFactory<>("vehiculo"));
            this.colFechaAlquiler.setCellValueFactory(new PropertyValueFactory<>("fechaAlquiler"));
            this.colFechaDevolucion.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucion"));
            this.colGetPrecio.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecio()).asObject());

            this.colDibujoVehiculo.setCellValueFactory(new PropertyValueFactory<>("vehiculo"));
            colDibujoVehiculo.setCellValueFactory(new PropertyValueFactory<>("vehiculo"));
            colDibujoVehiculo.setCellFactory(columna -> new TipoVehiculoTableCell());

            //Estilo de elementos de columnas:
            colCliente.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
            colVehiculo.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
            colDibujoVehiculo.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
            colFechaAlquiler.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
            colFechaDevolucion.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
            colGetPrecio.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

            this.colFechaAlquiler.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(LocalDate fecha, boolean empty) {
                    super.updateItem(fecha, empty);
                    if (empty || fecha == null) {
                        setText(null);
                    } else {
                        setText(FORMATO_FECHA.format(fecha));
                    }
                }
            });

            this.colFechaDevolucion.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(LocalDate fecha, boolean empty) {
                    super.updateItem(fecha, empty);
                    if (empty || fecha == null) {
                        setText(null);
                    } else {
                        setText(FORMATO_FECHA.format(fecha));
                    }
                }
            });

            this.tablaAlquileres.setItems(listaAlquileresVisible);
            this.tablaAlquileres.refresh();

        } catch (DomainException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void addAlquiler() {

        try {
            // Se carga la vista:
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistasfxml/VistaAddAlquileres.fxml"));

            // Se carga la ventana:
            Parent root = loader.load();

            // Creamos una instancia del controlador de AddClientes:
            ControllerAddAlquileres controlador = loader.getController();
            controlador.initAtributtes(listaAlquileres);

            // Se crea Stage y Scene: 
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            Image icono = new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.COCHE_ALQUILER)).toExternalForm());
            stage.getIcons().add(icono);

            stage.setTitle("Añadir Alquiler");

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            // Se recupera el alquiler:
            Alquiler alquiler = controlador.getAlquiler();

            if (alquiler != null) {
                listaAlquileres.add(alquiler);

                if (alquiler.getCliente().getNombreApellidos().toLowerCase().contains(this.campoBuscar.getText().toLowerCase())) {
                    this.listaAlquileresVisible.add(alquiler);
                }
                this.tablaAlquileres.refresh();
            }
        } catch (Exception e) {

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
            this.tablaAlquileres.setItems(listaAlquileres);

        } else {
            this.listaAlquileresVisible.clear();

            for (Alquiler alquiler : this.listaAlquileres) {
                if (alquiler.getCliente().getNombreApellidos().toLowerCase().contains(filtroNombre.toLowerCase())) {

                    this.listaAlquileresVisible.add(alquiler);
                }
            }
            this.tablaAlquileres.setItems(listaAlquileresVisible);
        }
    }

    @FXML
    void devolver() {
        Alquiler alquiler = this.tablaAlquileres.getSelectionModel().getSelectedItem();

        if (alquiler == null) {
            showError("No se ha seleccionado ningún alquiler");
            return;
        }

        if (alquiler.getFechaDevolucion() != null) {
            showError("No se puede devolver un alquiler ya devuelto");
            return;
        }

        // Cargar vista devolver alquiler
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistasfxml/VistaDevolverAlquileres.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ControllerDevolverAlquileres controlador = loader.getController();
        controlador.initAtributtes(listaAlquileres, alquiler);

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.COCHE_ALQUILER)).toExternalForm()));
        stage.setTitle("Devolver Alquiler");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();

        // El alquiler ya ha sido devuelto desde la ventana modal, así que solo refrescamos
        this.tablaAlquileres.refresh();
    }

    @FXML
    void seleccionar() {
        this.registro = this.tablaAlquileres.getSelectionModel().getSelectedItem();
    }

    @FXML
    void deleteAlquiler() {

        Alquiler alquiler = this.tablaAlquileres.getSelectionModel().getSelectedItem();

        if (alquiler == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("No se ha seleccionado ningún alquiler");
            alert.showAndWait();

        } else {

            // Personalizar botones
            ButtonType botonConfirmar = new ButtonType("Confirmar");
            ButtonType botonCancelar = new ButtonType("Cancelar", ButtonType.CANCEL.getButtonData());

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setTitle("Info");
            alert.setContentText("¿Está seguro que desea eliminar el alquiler seleccionado?: \n" +
                    alquiler.getCliente() + " " + alquiler.getVehiculo());

            alert.getButtonTypes().setAll(botonConfirmar, botonCancelar);

            Optional<ButtonType> resultado = alert.showAndWait();

            if (resultado.isPresent() && resultado.get() == botonConfirmar) {
                this.listaAlquileres.remove(alquiler);
                this.listaAlquileresVisible.remove(alquiler);

                Controlador.getInstancia().borrarAlquiler(alquiler);

                // Crear y mostrar alerta de confirmación
                Alert confirmacion = new Alert(Alert.AlertType.INFORMATION);
                confirmacion.setHeaderText(null);
                confirmacion.setTitle("Info");
                confirmacion.setContentText("Alquiler eliminado correctamente");
                confirmacion.showAndWait();

                this.tablaAlquileres.refresh();
            } else {
                alert.close();
            }
        }
    }

    @FXML
    void volver(ActionEvent event) {

        Stage escenarioActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        escenarioActual.close();
    }

    private void showError(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        alert.setContentText(error);
        alert.showAndWait();
    }
}
