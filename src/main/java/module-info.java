module org.rubennicolas.alquilervehiculos {
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
    requires java.desktop;
    requires com.google.common;
    requires jdk.compiler;

    opens org.rubennicolas.alquilervehiculos.modelo.dominio to javafx.base;
    opens org.rubennicolas.alquilervehiculos.vista.grafica.controllers to javafx.fxml;
    opens org.rubennicolas.alquilervehiculos.vista.grafica to javafx.fxml;

    exports org.rubennicolas.alquilervehiculos.modelo.dominio;
    exports org.rubennicolas.alquilervehiculos.vista.grafica.controllers;
    exports org.rubennicolas.alquilervehiculos.vista.grafica;
    exports org.rubennicolas.alquilervehiculos.vista.texto;
}
