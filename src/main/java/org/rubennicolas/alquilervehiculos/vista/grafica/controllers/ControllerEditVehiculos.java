package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Autobus;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Furgoneta;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Turismo;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerEditVehiculos implements Initializable {

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void initAtributtes(ObservableList<Vehiculo> listaVehiculos, Turismo turismo) {

        this.listaVehiculos = listaVehiculos;
        this.turismo = turismo;

        this.campoMarca.setText(this.turismo.getMarca());
        this.campoModelo.setText(this.turismo.getModelo());
        this.campoMatricula.setText(this.turismo.getMatricula());

        this.seleccionarTipo.setValue("Turismo");

        this.campoCilindrada.setText(Integer.toString(this.turismo.getCilindrada()));
        this.campoPma.cancelEdit();
        this.campoNumPlazas.cancelEdit();

        this.campoPma.setDisable(true);
        this.campoNumPlazas.setDisable(true);
        this.campoMatricula.setDisable(true);
        this.seleccionarTipo.setDisable(true);
    }

    public void initAtributtes(ObservableList<Vehiculo> listaVehiculos, Furgoneta furgoneta) {

        this.listaVehiculos = listaVehiculos;
        this.furgoneta = furgoneta;

        this.campoMarca.setText(this.furgoneta.getMarca());
        this.campoModelo.setText(this.furgoneta.getModelo());
        this.campoMatricula.setText(this.furgoneta.getMatricula());

        this.seleccionarTipo.setValue("Furgoneta");

        this.campoCilindrada.cancelEdit();
        this.campoPma.setText(Integer.toString(this.furgoneta.getPma()));
        this.campoNumPlazas.setText(Integer.toString(this.furgoneta.getPlazas()));

        this.campoCilindrada.setDisable(true);
        this.campoMatricula.setDisable(true);
        this.seleccionarTipo.setDisable(true);
    }

    public void initAtributtes(ObservableList<Vehiculo> listaVehiculos, Autobus autobus) {

        this.listaVehiculos = listaVehiculos;
        this.autobus = autobus;

        this.campoMarca.setText(this.autobus.getMarca());
        this.campoModelo.setText(this.autobus.getModelo());
        this.campoMatricula.setText(this.autobus.getMatricula());


        this.seleccionarTipo.setValue("Autobus");

        this.campoCilindrada.cancelEdit();
        this.campoPma.cancelEdit();
        this.campoNumPlazas.setText(Integer.toString(this.autobus.getPlazas()));

        this.campoCilindrada.setDisable(true);
        this.campoPma.setDisable(true);
        this.campoMatricula.setDisable(true);
        this.seleccionarTipo.setDisable(true);
    }

    public Vehiculo getVehiculo() { // Devuelve el vehículo a la vista de vehículos
        return this.vehiculo;
    }

    @FXML
    void guardar() {
        try {
            String marca = this.campoMarca.getText();
            String modelo = this.campoModelo.getText();
            String matricula = this.campoMatricula.getText();

            if (this.turismo != null) {
                int cilindrada = Integer.parseInt(this.campoCilindrada.getText());

                // Se crea el Turismo:
                Turismo turismo = new Turismo(marca, modelo, matricula, cilindrada);

                // Se comprueba si existe el Turismo:
                if (listaVehiculos.contains(turismo)) {

                    // Modificar el Turismo:
                    if (this.turismo != null) {
                        this.turismo.setMarca(marca);
                        this.turismo.setModelo(modelo);
                        this.turismo.setMatricula(matricula);
                        this.turismo.setCilindrada(cilindrada);

                        this.vehiculo = turismo;
                    }
                }
            }

            if (this.furgoneta != null) {
                int pma = Integer.parseInt(this.campoPma.getText());
                int numPlazas = Integer.parseInt(this.campoNumPlazas.getText());

                // Se crea la Furgoneta:
                Furgoneta furgoneta = new Furgoneta(marca, modelo, matricula, numPlazas, pma);

                // Se comprueba si existe la Furgoneta:
                if (listaVehiculos.contains(furgoneta)) {

                    // Modificar la Furgoneta:
                    if (this.furgoneta != null) {
                        this.furgoneta.setMarca(marca);
                        this.furgoneta.setModelo(modelo);
                        this.furgoneta.setMatricula(matricula);
                        this.furgoneta.setPlazas(numPlazas);
                        this.furgoneta.setPma(pma);

                        this.vehiculo = furgoneta;
                    }
                }
            }

            if (this.autobus != null) {
                int numPlazas = Integer.parseInt(this.campoNumPlazas.getText());

                // Se crea al Autobús:
                Autobus autobus = new Autobus(marca, modelo, matricula, numPlazas);

                // Se comprueba si existe el Autobús:
                if (listaVehiculos.contains(autobus)) {

                    // Modificar el Autobús:
                    if (this.autobus != null) {
                        this.autobus.setMarca(marca);
                        this.autobus.setModelo(modelo);
                        this.autobus.setMatricula(matricula);
                        this.autobus.setPlazas(numPlazas);

                        this.vehiculo = autobus;
                    }
                }
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Información");
            alert.setContentText("Se ha modificado el vehículo correctamente");
            alert.showAndWait();

            Stage stage = (Stage) this.buttonGuardar.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error:");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
