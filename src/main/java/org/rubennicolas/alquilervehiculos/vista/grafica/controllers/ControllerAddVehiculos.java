package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.rubennicolas.alquilervehiculos.AppContextThread;
import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.excepciones.DomainException;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Autobus;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Furgoneta;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerAddVehiculos extends ControllerVistaVehiculos {

    @FXML
    private TextField campoMarca;

    @FXML
    private TextField campoModelo;

    @FXML
    private TextField campoMatricula;

    @FXML
    private TextField campoCilindrada;

    @FXML
    private TextField campoNumPlazas;

    @FXML
    private TextField campoPma;

    @FXML
    private ChoiceBox<String> seleccionarTipo;

    protected Vehiculo vehiculo;

    protected Turismo turismo;

    protected Furgoneta furgoneta;

    protected Autobus autobus;

    protected ObservableList<Vehiculo> listaVehiculos;

    @FXML
    private Button buttonGuardar;

    private final String[] vehiculosTipos = {"Turismo", "Furgoneta", "Autobus"};

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

        // Cargar la lista de valores en el ChoiceBox
        this.campoCilindrada.setDisable(true);
        this.campoNumPlazas.setDisable(true);
        this.campoPma.setDisable(true);

        this.seleccionarTipo.getItems().addAll(vehiculosTipos);
        this.seleccionarTipo.setOnAction(this::seleccionarTipoVehiculo);
    }

    public void initAtributtes(ObservableList<Vehiculo> listaVehiculos) {

        this.listaVehiculos = listaVehiculos;

        if (vehiculo instanceof Turismo) {

            this.campoMarca.setText(this.turismo.getMarca());
            this.campoModelo.setText(this.turismo.getModelo());
            this.campoMatricula.setText(this.turismo.getMarca());

            this.campoCilindrada.setText(Integer.toString(this.turismo.getCilindrada()));
            this.campoPma.setText(null);
            this.campoNumPlazas.setText(null);
        }

        if (vehiculo instanceof Autobus) {

            this.campoMarca.setText(this.autobus.getMarca());
            this.campoModelo.setText(this.autobus.getModelo());
            this.campoMatricula.setText(this.autobus.getMarca());

            this.campoCilindrada.setText(null);
            this.campoPma.setText(null);
            this.campoNumPlazas.setText(Integer.toString(this.autobus.getPlazas()));
        }

        if (vehiculo instanceof Furgoneta) {

            this.campoMarca.setText(this.furgoneta.getMarca());
            this.campoModelo.setText(this.furgoneta.getModelo());
            this.campoMatricula.setText(this.furgoneta.getMarca());

            this.campoCilindrada.setText(null);
            this.campoNumPlazas.setText(Integer.toString(this.furgoneta.getPlazas()));
            this.campoPma.setText(Integer.toString(this.furgoneta.getPma()));

        }
    }

    void seleccionarTipoVehiculo(ActionEvent event) {

        String tipoVehiculo = this.seleccionarTipo.getValue();

        switch (tipoVehiculo) {
            case "Turismo" -> {

                this.campoCilindrada.setDisable(false);
                this.campoPma.setDisable(true);
                this.campoNumPlazas.setDisable(true);
            }
            case "Furgoneta" -> {

                this.campoCilindrada.setDisable(true);
                this.campoPma.setDisable(false);
                this.campoNumPlazas.setDisable(false);
            }
            case "Autobus" -> {

                this.campoCilindrada.setDisable(true);
                this.campoPma.setDisable(true);
                this.campoNumPlazas.setDisable(false);
            }
        }
    }

    public Vehiculo getVehiculo() {
        return this.vehiculo;
    }

    @FXML
    void guardar() {
        try {
            Vehiculo vehiculoNuevo;

            String tipo = this.seleccionarTipo.getValue();
            if (tipo == null || tipo.isBlank()) {
                throw new DomainException("Debe seleccionar un tipo de vehículo.");
            }

            String marca = this.campoMarca.getText();
            String modelo = this.campoModelo.getText();
            String matricula = this.campoMatricula.getText();

            switch (tipo) {
                case "Turismo" -> {
                    String textoCilindrada = this.campoCilindrada.getText();
                    if (textoCilindrada == null || textoCilindrada.isBlank()) {
                        throw new DomainException("La cilindrada no puede estar vacía.");
                    }

                    int cilindrada;
                    try {
                        cilindrada = Integer.parseInt(textoCilindrada);
                    } catch (NumberFormatException e) {
                        throw new DomainException("La cilindrada debe ser un número entero.");
                    }

                    vehiculoNuevo = new Turismo(marca, modelo, matricula, cilindrada);
                }

                case "Furgoneta" -> {
                    String textoPma = this.campoPma.getText();
                    String textoPlazas = this.campoNumPlazas.getText();

                    if (textoPma == null || textoPma.isBlank()) {
                        throw new DomainException("El PMA no puede estar vacío.");
                    }
                    if (textoPlazas == null || textoPlazas.isBlank()) {
                        throw new DomainException("El número de plazas no puede estar vacío.");
                    }

                    int pma, numPlazas;
                    try {
                        pma = Integer.parseInt(textoPma);
                        numPlazas = Integer.parseInt(textoPlazas);
                    } catch (NumberFormatException e) {
                        throw new DomainException("PMA y número de plazas deben ser números enteros.");
                    }

                    vehiculoNuevo = new Furgoneta(marca, modelo, matricula, numPlazas, pma);
                }

                case "Autobus" -> {
                    String textoPlazas = this.campoNumPlazas.getText();
                    if (textoPlazas == null || textoPlazas.isBlank()) {
                        throw new DomainException("El número de plazas no puede estar vacío.");
                    }

                    int numPlazas;
                    try {
                        numPlazas = Integer.parseInt(textoPlazas);
                    } catch (NumberFormatException e) {
                        throw new DomainException("El número de plazas debe ser un número entero.");
                    }

                    vehiculoNuevo = new Autobus(marca, modelo, matricula, numPlazas);
                }

                default -> throw new DomainException("Tipo de vehículo no reconocido.");
            }

            // Comprobamos si el vehículo ya existe
            if (listaVehiculos.contains(vehiculoNuevo)) {
                throw new DomainException("Ya existe un vehículo con esa matrícula.");
            }

            // Insertamos el vehículo
            getControlador().insertarVehiculo(vehiculoNuevo);
            this.vehiculo = vehiculoNuevo;

            // Mostramos alerta de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Información");
            alert.setContentText("Se ha añadido el vehículo correctamente.");
            alert.showAndWait();

            // Cerramos ventana
            Stage stage = (Stage) this.buttonGuardar.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
