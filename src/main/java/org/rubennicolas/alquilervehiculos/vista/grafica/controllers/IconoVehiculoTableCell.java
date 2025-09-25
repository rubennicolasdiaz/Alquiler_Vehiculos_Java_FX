package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantes;
import org.rubennicolas.alquilervehiculos.vista.texto.TipoVehiculo;

import java.util.Objects;

public class IconoVehiculoTableCell extends TableCell<Vehiculo, TipoVehiculo> {

    @Override
    protected void updateItem(TipoVehiculo tipoVehiculo, boolean empty) {

        super.updateItem(tipoVehiculo, empty);

        if (tipoVehiculo == null || empty) {
            setText(null);
            setGraphic(null);

        } else {
            ImageView icono = new ImageView();

            if (tipoVehiculo == TipoVehiculo.TURISMO) {
                icono.setImage(new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.COCHE)).toExternalForm()));

            } else if (tipoVehiculo == TipoVehiculo.FURGONETA) {
                icono.setImage(new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.FURGONETA)).toExternalForm()));

            } else if (tipoVehiculo == TipoVehiculo.AUTOBUS) {
                icono.setImage(new Image(Objects.requireNonNull(getClass().getResource(RutasConstantes.AUTOBUS)).toExternalForm()));
            }

            icono.setFitHeight(30);
            icono.setPreserveRatio(true);
            setGraphic(icono);
        }
    }
}



