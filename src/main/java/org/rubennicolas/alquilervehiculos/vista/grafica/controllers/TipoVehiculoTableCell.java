package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.rubennicolas.alquilervehiculos.modelo.dominio.*;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantes;

import java.util.Objects;

public class TipoVehiculoTableCell extends TableCell<Alquiler, Vehiculo> {

    @Override
    protected void updateItem(Vehiculo vehiculo, boolean empty) {

        super.updateItem(vehiculo, empty);

        if (vehiculo == null || empty) {
            setText(null);
            setGraphic(null);

        } else {
            ImageView icono = new ImageView();

            if (vehiculo instanceof Turismo) {
                icono.setImage(new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.COCHE)).toExternalForm()));

            } else if (vehiculo instanceof Furgoneta) {
                icono.setImage(new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.FURGONETA)).toExternalForm()));

            } else if (vehiculo instanceof Autobus) {
                icono.setImage(new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.AUTOBUS)).toExternalForm()));
            }
            
            icono.setFitHeight(30);
            icono.setPreserveRatio(true);
            setGraphic(icono);
        }
    }
}



