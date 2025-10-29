package org.rubennicolas.alquilervehiculos.vista.grafica.rutasconstantes;

import javafx.scene.image.Image;

import java.io.InputStream;

/**
 * Clase utilitaria para cargar imágenes desde el classpath,
 * compatible tanto con ejecución local (IntelliJ) como dentro de Docker.
 */
public class RutasConstantesImagenes {

    // --- RUTAS RELATIVAS EN EL CLASSPATH DE IMÁGENES ---
    
    public static final String AUTOBUS = "/imagenes/autobus.jpg";
    public static final String COCHE = "/imagenes/coche.jpg";
    public static final String COCHE_ALQUILER = "/imagenes/coche_alquiler.jpeg";
    public static final String FURGONETA = "/imagenes/furgoneta.jpg";

    /**
     * Devuelve una {@link javafx.scene.image.Image} cargada correctamente
     * desde el classpath. Si no se encuentra, lanza un RuntimeException con
     * un mensaje claro.
     * <p>
     * Ejemplo de uso:
     * <pre>
     *     Image img = RutasConstantesImagenes.loadImage(RutasConstantesImagenes.COCHE);
     * </pre>
     */
    public static Image loadImage(String ruta) {
        InputStream in = RutasConstantesImagenes.class.getResourceAsStream(ruta);
        if (in == null) {
            throw new RuntimeException("[ERROR] No se encontró la imagen: " + ruta);
        }
        return new Image(in);
    }

    /**
     * Devuelve la URL externa de una imagen (útil si necesitas usarla en FXML o CSS).
     * <p>
     * Ejemplo:
     * <pre>
     *     ImageView img = new ImageView(new Image(RutasConstantesImagenes.getUrl(RutasConstantesImagenes.COCHE)));
     * </pre>
     */
    public static String getUrl(String ruta) {
        var resource = RutasConstantesImagenes.class.getResource(ruta);
        if (resource == null) {
            throw new RuntimeException("[ERROR] No se encontró la imagen: " + ruta);
        }
        return resource.toExternalForm();
    }
}

