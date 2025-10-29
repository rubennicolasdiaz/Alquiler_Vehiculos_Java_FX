package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.rubennicolas.alquilervehiculos.AppContextThread;
import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import static org.rubennicolas.alquilervehiculos.vista.grafica.controllers.ControllerVistaAlquileres.FORMATO_FECHA;
import static org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.EstilosConstantes.STYLE_COLUMN;

public class ControllerVistaVehiculoAlquiler extends ControllerVistaVehiculos {

    @FXML
    private ChoiceBox<Vehiculo> selVehiculo;

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

    protected Vehiculo vehiculo;

    protected Alquiler registro;

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

        // Obtener la lista de vehículos:
        List<Vehiculo> listaVehiculos = getControlador().getVehiculos();

        // Agregar la lista de Vehículos a la ChoiceBox:
        selVehiculo.getItems().addAll(listaVehiculos);

        listaAlquileres = FXCollections.observableArrayList();

        listaAlquileresVisible = FXCollections.observableArrayList();

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
    }

    @FXML
    void filtrarAlquilerVehiculo() {
        this.vehiculo = selVehiculo.getValue();
        listaAlquileres.setAll(getControlador().getAlquileresPorVehiculo(vehiculo));
        listaAlquileresVisible.setAll(listaAlquileres);

        this.tablaAlquileres.setItems(listaAlquileresVisible);
        this.tablaAlquileres.refresh();
    }

    @FXML
    void seleccionar() {
        this.registro = this.tablaAlquileres.getSelectionModel().getSelectedItem();
    }

}