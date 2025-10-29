package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantesImagenes;
import org.rubennicolas.alquilervehiculos.vista.texto.TipoVehiculo;

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
                icono.setImage(RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE));

            } else if (tipoVehiculo == TipoVehiculo.FURGONETA) {
                icono.setImage(RutasConstantesImagenes.loadImage(RutasConstantesImagenes.FURGONETA));

            } else if (tipoVehiculo == TipoVehiculo.AUTOBUS) {
                icono.setImage(RutasConstantesImagenes.loadImage(RutasConstantesImagenes.AUTOBUS));
            }

            icono.setFitHeight(30);
            icono.setPreserveRatio(true);
            setGraphic(icono);
        }
    }
}



