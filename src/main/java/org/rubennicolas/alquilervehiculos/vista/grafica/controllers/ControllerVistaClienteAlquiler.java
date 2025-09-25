package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import static org.rubennicolas.alquilervehiculos.vista.grafica.controllers.ControllerVistaAlquileres.FORMATO_FECHA;

public class ControllerVistaClienteAlquiler implements Initializable {

    @FXML
    ChoiceBox<Cliente> selCliente;

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

    protected Cliente cliente;

    protected Alquiler registro;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        List<Cliente> listaClientes = Controlador.getInstancia().getClientes();

        // Agregar la lista de clientes a la ChoiceBox: 
        selCliente.getItems().addAll(listaClientes);

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
    }

    @FXML
    void filtrarAlquilerCliente() {

        this.cliente = (Cliente) selCliente.getValue();
        listaAlquileres.setAll(Controlador.getInstancia().getAlquileresPorCliente(cliente));
        listaAlquileresVisible.setAll(listaAlquileres);

        this.tablaAlquileres.setItems(listaAlquileresVisible);

        this.tablaAlquileres.refresh();
    }

    @FXML
    void seleccionar() {

        this.registro = this.tablaAlquileres.getSelectionModel().getSelectedItem();
    }

    @FXML
    void volver(ActionEvent event) {

        Stage escenarioActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        escenarioActual.close();
    }
}
