module org.rubennicolas.alquilervehiculos {
    // 🔹 Dependencias del módulo (las “librerías” que usas)
    requires java.naming;
    requires java.xml;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires java.desktop; // necesario para Swing, AWT, JOptionPane
    requires com.google.common;
    requires jdk.compiler;
    
    // 🔹 “opens” permite acceso por reflexión (FXML, serialización, etc.)
    opens org.rubennicolas.alquilervehiculos.modelo.dominio to javafx.base;
    opens org.rubennicolas.alquilervehiculos.vista.grafica.controllers to javafx.fxml;
    opens org.rubennicolas.alquilervehiculos.vista.grafica to javafx.fxml;

    // 🔹 “exports” permite a otros módulos usar tus clases públicamente
    exports org.rubennicolas.alquilervehiculos.controlador;
    exports org.rubennicolas.alquilervehiculos.modelo.dominio;
    exports org.rubennicolas.alquilervehiculos.modelo;
    exports org.rubennicolas.alquilervehiculos.vista;
    exports org.rubennicolas.alquilervehiculos.vista.grafica.controllers;
    exports org.rubennicolas.alquilervehiculos.vista.grafica;
    exports org.rubennicolas.alquilervehiculos.vista.texto;
    exports org.rubennicolas.alquilervehiculos.modelo.negocio;
}

