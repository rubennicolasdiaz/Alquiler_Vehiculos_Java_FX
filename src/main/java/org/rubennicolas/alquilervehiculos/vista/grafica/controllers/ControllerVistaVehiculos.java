package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
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
import org.rubennicolas.alquilervehiculos.modelo.dominio.Autobus;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Furgoneta;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantesFxml;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantesImagenes;
import org.rubennicolas.alquilervehiculos.vista.texto.TipoVehiculo;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.EstilosConstantes.STYLE_COLUMN;

public class ControllerVistaVehiculos extends ControllerVistaPrincipal implements Initializable {

    @FXML
    private TextField campoBuscar;

    @FXML
    private TableColumn<Vehiculo, TipoVehiculo> colTipoVehiculo;

    @FXML
    private TableColumn<Vehiculo, String> colMarca;

    @FXML
    private TableColumn<Vehiculo, String> colModelo;

    @FXML
    private TableColumn<Vehiculo, String> colMatricula;

    @FXML
    private TableColumn<Vehiculo, Integer> colCilindrada;

    @FXML
    private TableColumn<Vehiculo, Integer> colNumPlazas;

    @FXML
    private TableColumn<Vehiculo, Integer> colNumPma;

    @FXML
    private TableView<Vehiculo> tablaVehiculos;

    private ObservableList<Vehiculo> listaVehiculos;

    private ObservableList<Vehiculo> listaVehiculosVisible;

    protected Vehiculo vehiculo;

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

            listaVehiculos = FXCollections.observableArrayList();
            listaVehiculos.setAll(getControlador().getVehiculos());

            listaVehiculosVisible = FXCollections.observableArrayList();
            listaVehiculosVisible.setAll(getControlador().getVehiculos());

            colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
            colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
            colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));

            colCilindrada.setCellValueFactory(cellData -> {
                Vehiculo v = cellData.getValue();
                if (v instanceof Turismo) {
                    return new SimpleIntegerProperty(((Turismo) v).getCilindrada()).asObject();
                }
                return new ReadOnlyObjectWrapper<>(null);
            });

            colNumPma.setCellValueFactory(cellData -> {
                Vehiculo v = cellData.getValue();
                if (v instanceof Furgoneta) {
                    return new SimpleIntegerProperty(((Furgoneta) v).getPma()).asObject();
                }
                return new ReadOnlyObjectWrapper<>(null);
            });

            colNumPlazas.setCellValueFactory(cellData -> {
                Vehiculo v = cellData.getValue();
                if (v instanceof Furgoneta) {
                    return new SimpleIntegerProperty(((Furgoneta) v).getPlazas()).asObject();
                }

                if (v instanceof Autobus) {
                    return new SimpleIntegerProperty(((Autobus) v).getPlazas()).asObject();
                }
                return new ReadOnlyObjectWrapper<>(null);
            });

            this.colTipoVehiculo.setCellValueFactory(new PropertyValueFactory<>("tipoVehiculo"));
            colTipoVehiculo.setCellValueFactory(new PropertyValueFactory<>("tipoVehiculo"));
            colTipoVehiculo.setCellFactory(columna -> new IconoVehiculoTableCell());

            tablaVehiculos.setItems(listaVehiculosVisible);
            tablaVehiculos.refresh();


            //Estilo de elementos de columnas:
            colTipoVehiculo.setStyle(STYLE_COLUMN);
            colMarca.setStyle(STYLE_COLUMN);
            colModelo.setStyle(STYLE_COLUMN);
            colMatricula.setStyle(STYLE_COLUMN);
            colCilindrada.setStyle(STYLE_COLUMN);
            colNumPlazas.setStyle(STYLE_COLUMN);
            colNumPma.setStyle(STYLE_COLUMN);
        } catch (
                DomainException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void addVehiculo() {

        try {
            FXMLLoader loader = new FXMLLoader(RutasConstantesFxml.get(RutasConstantesFxml.VISTA_ADD_VEHICULOS));
            Parent root = loader.load();

            ControllerAddVehiculos controlador = loader.getController();
            controlador.initAtributtes(listaVehiculos);

            Scene scene = new Scene(root);
            Stage stage = new Stage();

            Image icono = RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE_ALQUILER);
            stage.getIcons().add(icono);

            stage.setTitle("Añadir Vehículo");

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            Vehiculo vehiculo = controlador.getVehiculo();

            if (vehiculo != null) {

                listaVehiculos.add(vehiculo);

                if (vehiculo.getModelo().toLowerCase().contains(this.campoBuscar.getText().toLowerCase())) {

                    this.listaVehiculosVisible.add(vehiculo);
                    this.tablaVehiculos.refresh();
                }
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void editVehiculo() {

        Vehiculo vehiculo = this.tablaVehiculos.getSelectionModel().getSelectedItem();

        if (vehiculo == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("No se ha seleccionado ningún vehículo");
            alert.showAndWait();
        } else {

            try {
                // Cargo la vista
                FXMLLoader loader = new FXMLLoader(RutasConstantesFxml.get(RutasConstantesFxml.VISTA_EDIT_VEHICULOS));

                // Cargo la ventana
                Parent root = loader.load();

                // Se crea una instancia del controlador de EditVehiculos:
                ControllerEditVehiculos controlador = loader.getController();


                if (vehiculo instanceof Turismo turismo) {
                    controlador.initAtributtes(listaVehiculos, turismo);
                }

                if (vehiculo instanceof Autobus autobus) {
                    controlador.initAtributtes(listaVehiculos, autobus);
                }

                if (vehiculo instanceof Furgoneta furgoneta) {
                    controlador.initAtributtes(listaVehiculos, furgoneta);
                }

                // Se crean Stage y Scene:
                Scene scene = new Scene(root);
                Stage stage = new Stage();

                Image icono = RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE_ALQUILER);
                stage.getIcons().add(icono);

                stage.setTitle("Modificar Vehículo");

                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.showAndWait();

                Vehiculo vehiculoSeleccionado = controlador.getVehiculo();

                if (vehiculoSeleccionado != null) {
                    getControlador().modificarVehiculo(vehiculoSeleccionado);
                    this.tablaVehiculos.refresh();

                }
            } catch (IOException e) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    void verAlquileres() {

        try {

            // Cargo la vista
            FXMLLoader loader = new FXMLLoader(RutasConstantesFxml.get(RutasConstantesFxml.VISTA_VEHICULO_ALQUILER));

            // Cargo la ventana
            Parent root = loader.load();

            // Se crean Stage y Scene:
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            Image icono = RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE_ALQUILER);
            stage.getIcons().add(icono);

            stage.setTitle("Ver Alquileres Vehículo");

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

        } catch (IOException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void buscar() {

        String filtroNombre = this.campoBuscar.getText();

        if (filtroNombre.isEmpty()) {

            this.tablaVehiculos.setItems(listaVehiculos);

        } else {

            this.listaVehiculosVisible.clear();

            for (Vehiculo vehiculo : this.listaVehiculos) {

                if (vehiculo.getModelo().toLowerCase().contains(filtroNombre.toLowerCase())) {
                    this.listaVehiculosVisible.add(vehiculo);
                }
            }
            this.tablaVehiculos.setItems(listaVehiculosVisible);
        }
    }

    @FXML
    void deleteVehiculo() {

        Vehiculo vehiculo = this.tablaVehiculos.getSelectionModel().getSelectedItem();

        if (vehiculo == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("No se ha seleccionado ningún vehículo");
            alert.showAndWait();

        } else {
            // Personalizar botones
            ButtonType botonConfirmar = new ButtonType("Confirmar");
            ButtonType botonCancelar = new ButtonType("Cancelar", ButtonType.CANCEL.getButtonData());

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setTitle("Info");
            alert.setContentText("¿Está seguro que desea eliminar el vehículo seleccionado?: \n" +
                    vehiculo.getMarca() + " " + vehiculo.getModelo() + " / " + vehiculo.getMatricula());

            alert.getButtonTypes().setAll(botonConfirmar, botonCancelar);

            Optional<ButtonType> resultado = alert.showAndWait();

            if (resultado.isPresent() && resultado.get() == botonConfirmar) {
                this.listaVehiculos.remove(vehiculo);
                this.listaVehiculosVisible.remove(vehiculo);

                getControlador().borrarVehiculo(vehiculo);

                // Crear y mostrar alerta de confirmación
                Alert confirmacion = new Alert(Alert.AlertType.INFORMATION);
                confirmacion.setHeaderText(null);
                confirmacion.setTitle("Info");
                confirmacion.setContentText("Vehículo: " +
                        vehiculo.getMarca() + " " + vehiculo.getModelo() + " / " + vehiculo.getMatricula() + "\nEliminado correctamente");
                confirmacion.showAndWait();

                this.tablaVehiculos.refresh();
            } else {
                alert.close();
            }
        }
    }

    @FXML
    void seleccionar() {
        this.vehiculo = this.tablaVehiculos.getSelectionModel().getSelectedItem();
    }

    @FXML
    void volver(ActionEvent event) {
        Stage escenarioActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        escenarioActual.close();
    }
}