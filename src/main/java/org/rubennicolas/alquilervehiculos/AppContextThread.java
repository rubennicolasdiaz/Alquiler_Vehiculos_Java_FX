package org.rubennicolas.alquilervehiculos;

import org.rubennicolas.alquilervehiculos.controlador.Controlador;
import org.rubennicolas.alquilervehiculos.modelo.Modelo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppContextThread {

    private static final ExecutorService contextThread = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "AppContextThread");
        t.setDaemon(true);
        return t;
    });

    private static volatile Controlador controlador;
    private static volatile Modelo modelo;

    public static void iniciarContexto(Controlador c, Modelo m) {
        controlador = c;
        modelo = m;
    }

    public static void ejecutar(Runnable tarea) {
        contextThread.submit(tarea);
    }

    public static Controlador getControlador() {
        return controlador;
    }

    public static Modelo getModelo() {
        return modelo;
    }
}
