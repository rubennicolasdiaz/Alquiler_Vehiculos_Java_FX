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
import org.rubennicolas.alquilervehiculos.AppContextThread;
import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantesFxml;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantesImagenes;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.EstilosConstantes.STYLE_COLUMN;

public class ControllerVistaAlquileres extends ControllerVistaPrincipal implements Initializable {

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

            listaAlquileres = FXCollections.observableArrayList();
            listaAlquileres.setAll(getControlador().getAlquileres());

            listaAlquileresVisible = FXCollections.observableArrayList();
            listaAlquileresVisible.setAll(getControlador().getAlquileres());

            this.colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
            this.colVehiculo.setCellValueFactory(new PropertyValueFactory<>("vehiculo"));
            this.colFechaAlquiler.setCellValueFactory(new PropertyValueFactory<>("fechaAlquiler"));
            this.colFechaDevolucion.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucion"));
            this.colGetPrecio.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecio()).asObject());

            this.colDibujoVehiculo.setCellValueFactory(new PropertyValueFactory<>("vehiculo"));
            colDibujoVehiculo.setCellValueFactory(new PropertyValueFactory<>("vehiculo"));
            colDibujoVehiculo.setCellFactory(columna -> new TipoVehiculoTableCell());

            //Estilo de elementos de columnas:
            colCliente.setStyle(STYLE_COLUMN);
            colVehiculo.setStyle(STYLE_COLUMN);
            colDibujoVehiculo.setStyle(STYLE_COLUMN);
            colFechaAlquiler.setStyle(STYLE_COLUMN);
            colFechaDevolucion.setStyle(STYLE_COLUMN);
            colGetPrecio.setStyle(STYLE_COLUMN);

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
            FXMLLoader loader = new FXMLLoader(
                    RutasConstantesFxml.get(RutasConstantesFxml.VISTA_ADD_ALQUILERES)
            );

            // Se carga la ventana:
            Parent root = loader.load();

            // Creamos una instancia del controlador de AddClientes:
            ControllerAddAlquileres controlador = loader.getController();
            controlador.initAtributtes(listaAlquileres);

            // Se crea Stage y Scene: 
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            Image icono = RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE_ALQUILER);

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
        FXMLLoader loader = new FXMLLoader(
                RutasConstantesFxml.get(RutasConstantesFxml.VISTA_DEVOLVER_ALQUILERES));
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
        stage.getIcons().add(
                RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE_ALQUILER));
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

                getControlador().borrarAlquiler(alquiler);

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