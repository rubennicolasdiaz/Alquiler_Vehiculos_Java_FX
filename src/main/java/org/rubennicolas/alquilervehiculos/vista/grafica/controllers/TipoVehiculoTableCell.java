package org.rubennicolas.alquilervehiculos.vista.grafica.controllers;

import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;
import org.rubennicolas.alquilervehiculos.modelo.dominio.*;
import org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes.RutasConstantesImagenes;

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
                icono.setImage(RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE));

            } else if (vehiculo instanceof Furgoneta) {
                icono.setImage(RutasConstantesImagenes.loadImage(RutasConstantesImagenes.FURGONETA));

            } else if (vehiculo instanceof Autobus) {
                icono.setImage(RutasConstantesImagenes.loadImage(RutasConstantesImagenes.AUTOBUS));
            }

            icono.setFitHeight(30);
            icono.setPreserveRatio(true);
            setGraphic(icono);
        }
    }
}



