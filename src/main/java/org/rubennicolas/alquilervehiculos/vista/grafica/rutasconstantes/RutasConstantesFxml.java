package org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes;

import java.net.URL;

public class RutasConstantesFxml {

    public static final String VISTA_ADD_ALQUILERES = "/vistasfxml/VistaAddAlquileres.fxml";
    public static final String VISTA_ADD_CLIENTES = "/vistasfxml/VistaAddClientes.fxml";
    public static final String VISTA_ADD_VEHICULOS = "/vistasfxml/VistaAddVehiculos.fxml";
    public static final String VISTA_ALQUILERES = "/vistasfxml/VistaAlquileres.fxml";
    public static final String VISTA_CLIENTE_ALQUILER = "/vistasfxml/VistaClienteAlquiler.fxml";
    public static final String VISTA_CLIENTES = "/vistasfxml/VistaClientes.fxml";
    public static final String VISTA_DEVOLVER_ALQUILERES = "/vistasfxml/VistaDevolverAlquileres.fxml";
    public static final String VISTA_EDIT_CLIENTES = "/vistasfxml/VistaEditClientes.fxml";
    public static final String VISTA_EDIT_VEHICULOS = "/vistasfxml/VistaEditVehiculos.fxml";
    public static final String VISTA_PRINCIPAL = "/vistasfxml/VistaPrincipal.fxml";
    public static final String VISTA_VEHICULO_ALQUILER = "/vistasfxml/VistaVehiculoAlquiler.fxml";
    public static final String VISTA_VEHICULOS = "/vistasfxml/VistaVehiculos.fxml";

    public static URL get(String ruta) {
        URL resource = RutasConstantesFxml.class.getResource(ruta);
        if (resource == null) {
            throw new RuntimeException("[ERROR] No se encontró la vista: " + ruta);
        }
        return resource;
    }
}
