package org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades;

import org.rubennicolas.alquilervehiculos.modelo.dominio.*;
import org.rubennicolas.alquilervehiculos.modelo.negocio.ficheros.Clientes;
import org.rubennicolas.alquilervehiculos.modelo.negocio.ficheros.Vehiculos;
import org.rubennicolas.alquilervehiculos.vista.texto.Consola;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UtilidadesXml {

    private static final String RUTA_FICHERO_ALQUILERES = "src/main/resources/datos/alquileres.xml";
    private static final DateTimeFormatter FORMATO_FECHA = Consola.FORMATO_FECHA;
    private static final String RAIZ_ALQUILERES = "Alquileres";
    private static final String ALQUILER = "Alquiler";
    private static final String DNI_CLIENTE = "Dni";
    private static final String MATRICULA_VEHICULO = "Matricula";
    private static final String FECHA_ALQUILER = "FechaAlquiler";
    private static final String FECHA_DEVOLUCION = "FechaDevolucion";
    private static final String FORMATO = "Formato";
    private static final String TIPO_DATO = "TipoDato";

    private static final String RUTA_FICHERO_CLIENTES = "src/main/resources/datos/clientes.xml";
    private static final String RAIZ_CLIENTES = "Clientes";
    private static final String CLIENTE = "Cliente";
    private static final String NOMBRE = "Nombre";
    private static final String TELEFONO = "Telefono";
    private static final String EMAIL = "Email";

    private static final String RUTA_FICHERO_VEHICULOS = "src/main/resources/datos/vehiculos.xml";
    private static final String RAIZ_VEHICULOS = "Vehiculos";
    private static final String VEHICULO = "Vehiculo";
    private static final String MARCA = "Marca";
    private static final String MODELO = "Modelo";
    private static final String MATRICULA = "Matricula";
    private static final String CILINDRADA = "Cilindrada";
    private static final String PLAZAS = "Plazas";
    private static final String PMA = "Pma";
    private static final String TIPO = "Tipo";
    private static final String TURISMO = "Turismo";
    private static final String AUTOBUS = "Autobus";
    private static final String FURGONETA = "Furgoneta";

    public static List<Alquiler> leerXmlAlquileres() {

        List<Alquiler> alquileres = new ArrayList<>();
        try {
            Document documento = UtilidadesXml.xmlToDom(RUTA_FICHERO_ALQUILERES);
            NodeList listaNodos = documento.getDocumentElement().getChildNodes();
            for (int i = 0; i < listaNodos.getLength(); i++) {
                Node nodo = listaNodos.item(i);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;
                    Alquiler alquiler = elementToAlquiler(elemento);
                    alquileres.add(alquiler);
                }
            }
        } catch (NullPointerException e) {
            e.getMessage();
        }
        return alquileres;
    }

    public static void escribirXmlAlquileres(List<Alquiler> alquileres) {
        try {
            Document documento = UtilidadesXml.crearDomVacio(RAIZ_ALQUILERES);

            for (Alquiler alquiler : alquileres) {
                alquilerToElement(documento, alquiler);
            }
            domToXml(documento, RUTA_FICHERO_ALQUILERES);

        } catch (Exception e) {
            System.err.println("Error al escribir el archivo XML: " + e.getMessage());
        }
    }

    public static List<Cliente> leerXmlClientes() {

        List<Cliente> clientes = new ArrayList<>();

        Document documento = UtilidadesXml.xmlToDom(RUTA_FICHERO_CLIENTES);

        if (documento == null) {
            throw new NullPointerException("El fichero XML es nulo");
        }

        NodeList listaNodos = documento.getDocumentElement().getChildNodes();

        for (int i = 0; i < listaNodos.getLength(); i++) {
            Node nodo = listaNodos.item(i);

            if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                Element elemento = (Element) nodo;
                Cliente cliente = elementToCliente(elemento);
                clientes.add(cliente);
            }
        }
        return clientes;
    }

    public static void escribirXmlClientes(List<Cliente> clientes) throws ParserConfigurationException, TransformerException {
        Document documento = UtilidadesXml.crearDomVacio(RAIZ_CLIENTES);

        for (Cliente cliente : clientes) {
            clienteToElement(documento, cliente);
        }
        UtilidadesXml.domToXml(documento, RUTA_FICHERO_CLIENTES);
    }

    public static List<Vehiculo> leerXmlVehiculos() {

        List<Vehiculo> vehiculos = new ArrayList<>();

        Document documento = UtilidadesXml.xmlToDom(RUTA_FICHERO_VEHICULOS);
        if (documento == null) {
            throw new NullPointerException("El fichero XML es nulo");
        } else {
            NodeList listaNodos = documento.getDocumentElement().getChildNodes();
            for (int i = 0; i < listaNodos.getLength(); i++) {
                Node nodo = listaNodos.item(i);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;
                    Vehiculo vehiculo = elementToVehiculo(elemento);
                    vehiculos.add(vehiculo);
                }
            }
        }
        return vehiculos;
    }

    public static void escribirXmlVehiculos(List<Vehiculo> vehiculos) throws TransformerException, ParserConfigurationException {

        Document documento = UtilidadesXml.crearDomVacio(RAIZ_VEHICULOS);

        for (Vehiculo vehiculo : vehiculos) {

            vehiculoToElement(documento, vehiculo);
        }
        UtilidadesXml.domToXml(documento, RUTA_FICHERO_VEHICULOS);
    }

    private static void alquilerToElement(Document dom, Alquiler alquiler) {
        Element raiz = dom.getDocumentElement();

        String dni = alquiler.getCliente().getDni();
        String matricula = alquiler.getVehiculo().getMatricula();
        String cadenaAlquiler = alquiler.getFechaAlquiler().format(FORMATO_FECHA);
        String cadenaDevolucion = null;

        if (alquiler.getFechaDevolucion() != null) {
            cadenaDevolucion = alquiler.getFechaDevolucion().format(FORMATO_FECHA);
        }

        Element elementoAlquiler = dom.createElement(ALQUILER);
        elementoAlquiler.setAttribute(DNI_CLIENTE, dni);
        elementoAlquiler.setAttribute(MATRICULA_VEHICULO, matricula);

        Element fechaAlquilerElement = dom.createElement(FECHA_ALQUILER);
        fechaAlquilerElement.setAttribute(FORMATO, Consola.PATRON_FECHA);
        fechaAlquilerElement.setTextContent(cadenaAlquiler);
        elementoAlquiler.appendChild(fechaAlquilerElement);

        Element fechaDevolucionElement = dom.createElement(FECHA_DEVOLUCION);
        fechaDevolucionElement.setAttribute(TIPO_DATO, "String");
        fechaDevolucionElement.setTextContent(cadenaDevolucion);
        elementoAlquiler.appendChild(fechaDevolucionElement);
        raiz.appendChild(elementoAlquiler);
    }

    private static Document xmlToDom(String rutaXML) {

        Document documento = null;
        try {
            // 1º Creamos una nueva instancia de un fábrica de constructores de documentos.
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // 2º A partir de la instancia anterior, fabricamos un constructor de documentos, que procesará el XML.
            DocumentBuilder db = dbf.newDocumentBuilder();
            // 3º Procesamos el documento (almacenado en un archivo) y lo convertimos en un árbol DOM.
            documento = db.parse(rutaXML);

        } catch (Exception e) {
            System.out.println("¡Error! No se ha podido cargar el documento XML.");
        }
        return documento;
    }

    private static Alquiler elementToAlquiler(Element elemento) {
        Alquiler alquiler;

        if (elemento == null) {
            throw new NullPointerException("El fichero XML no puede ser nulo");
        }

        String dni = elemento.getAttribute(DNI_CLIENTE);
        String matricula = elemento.getAttribute(MATRICULA_VEHICULO);
        String cadenaAlquiler = elemento.getElementsByTagName(FECHA_ALQUILER).item(0).getTextContent();
        String cadenaDevolucion = elemento.getElementsByTagName(FECHA_DEVOLUCION).item(0).getTextContent();

        Cliente clienteInicial = new Cliente("Nombre", dni, "900900900", "nombre@mail.com");
        Cliente cliente = Clientes.getInstancia().buscarCliente(clienteInicial);

        Vehiculo vehiculoInicial = new Turismo("Seat", "León", matricula, 2000);
        Vehiculo vehiculo = Vehiculos.getInstancia().buscarVehiculo(vehiculoInicial);

        LocalDate fechaAlquiler = null;
        LocalDate fechaDevolucion = null;

        if (!Objects.equals(cadenaAlquiler, "")) {
            fechaAlquiler = LocalDate.parse(cadenaAlquiler, FORMATO_FECHA);
        }

        if (!Objects.equals(cadenaDevolucion, "")) {
            fechaDevolucion = LocalDate.parse(cadenaDevolucion, FORMATO_FECHA);
        }

        alquiler = new Alquiler(cliente, vehiculo, fechaAlquiler);

        if (fechaDevolucion != null) {
            alquiler.setFechaDevolucion(fechaDevolucion);
        }
        return alquiler;
    }

    private static void domToXml(Document dom, String rutaXml) throws TransformerException {
        // 1º Creamos una instancia de la clase File para acceder al archivo donde guardaremos el XML.
        File file = new File(rutaXml);

        //2º Creamos una nueva instancia del transformador a través de la fábrica de transformadores.
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        //3º Establecemos algunas opciones de salida, como por ejemplo, la codificación de salida.
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        //4º Creamos el StreamResult, que intermediará entre el transformador y el archivo de destino.
        StreamResult result = new StreamResult(file);

        //5º Creamos el DOMSource, que intermediará entre el transformador y el árbol DOM.
        DOMSource source = new DOMSource(dom);

        //6º Realizamos la transformación.
        transformer.transform(source, result);
    }

    private static Document crearDomVacio(String raiz) throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document document;

        db = dbf.newDocumentBuilder();
        document = db.newDocument();
        document.appendChild(document.createElement(raiz));

        return document;
    }

    private static Cliente elementToCliente(Element elemento) throws IllegalArgumentException {
        Cliente cliente;

        String dni = elemento.getAttribute(DNI_CLIENTE);
        Element nombre = (Element) elemento.getElementsByTagName(NOMBRE).item(0);
        Element telefono = (Element) elemento.getElementsByTagName(TELEFONO).item(0);
        Element email = (Element) elemento.getElementsByTagName(EMAIL).item(0);

        cliente = new Cliente(nombre.getTextContent(), dni, telefono.getTextContent(), email.getTextContent());

        return cliente;
    }

    private static void clienteToElement(Document dom, Cliente cliente) {
        Element elementoCliente = dom.createElement(CLIENTE);
        elementoCliente.setAttribute(DNI_CLIENTE, cliente.getDni());

        Element elementoNombre = dom.createElement(NOMBRE);
        elementoNombre.setAttribute(TIPO_DATO, "String");
        elementoNombre.appendChild(dom.createTextNode(cliente.getNombreApellidos()));
        elementoCliente.appendChild(elementoNombre);

        Element elementoTelefono = dom.createElement(TELEFONO);
        elementoTelefono.setAttribute(TIPO_DATO, "String");
        elementoTelefono.appendChild(dom.createTextNode(cliente.getTelefono()));
        elementoCliente.appendChild(elementoTelefono);

        Element elementoEmail = dom.createElement(EMAIL);
        elementoEmail.setAttribute(TIPO_DATO, "String");
        elementoEmail.appendChild(dom.createTextNode(cliente.getEmail()));
        elementoCliente.appendChild(elementoEmail);

        Element raiz = dom.getDocumentElement();
        raiz.appendChild(elementoCliente);
    }

    private static void vehiculoToElement(Document dom, Vehiculo vehiculo) {

        Element raiz = dom.getDocumentElement();

        String matricula = vehiculo.getMatricula();
        String tipo = vehiculo.getClass().getSimpleName();
        String marca = vehiculo.getMarca();
        String modelo = vehiculo.getModelo();

        Element elementoVehiculo = dom.createElement(VEHICULO);
        elementoVehiculo.setAttribute(MATRICULA, matricula);
        elementoVehiculo.setAttribute(TIPO, tipo);

        Element marcaElement = dom.createElement(MARCA);
        marcaElement.setAttribute(TIPO_DATO, "String");
        marcaElement.setTextContent(marca);
        elementoVehiculo.appendChild(marcaElement);

        Element modeloElement = dom.createElement(MODELO);
        modeloElement.setAttribute(TIPO_DATO, "String");
        modeloElement.setTextContent(modelo);
        elementoVehiculo.appendChild(modeloElement);

        switch (tipo) {
            case TURISMO -> {

                Turismo turismo = (Turismo) vehiculo;
                int cilindrada = turismo.getCilindrada();
                Element turismoElement = dom.createElement(TURISMO);
                Element cilindradaElement = dom.createElement(CILINDRADA);
                cilindradaElement.setAttribute(TIPO_DATO, "Integer");
                cilindradaElement.setTextContent(String.valueOf(cilindrada));
                turismoElement.appendChild(cilindradaElement);
                elementoVehiculo.appendChild(turismoElement);
                raiz.appendChild(elementoVehiculo);
            }
            case AUTOBUS -> {

                Autobus autobus = (Autobus) vehiculo;
                int plazas = autobus.getPlazas();
                Element autobusElement = dom.createElement(AUTOBUS);
                Element plazasBusElement = dom.createElement(PLAZAS);
                plazasBusElement.setAttribute(TIPO_DATO, "Integer");
                plazasBusElement.setTextContent(String.valueOf(plazas));
                autobusElement.appendChild(plazasBusElement);
                elementoVehiculo.appendChild(autobusElement);
                raiz.appendChild(elementoVehiculo);
            }
            case FURGONETA -> {

                Furgoneta furgoneta = (Furgoneta) vehiculo;
                int pma = furgoneta.getPma();
                int plazas = furgoneta.getPlazas();
                Element furgonetaElement = dom.createElement(FURGONETA);
                Element pmaElement = dom.createElement(PMA);
                Element plazasFurgoElement = dom.createElement(PLAZAS);
                pmaElement.setAttribute(TIPO_DATO, "Integer");
                pmaElement.setTextContent(String.valueOf(pma));
                plazasFurgoElement.setAttribute(TIPO_DATO, "Integer");
                plazasFurgoElement.setTextContent(String.valueOf(plazas));
                furgonetaElement.appendChild(pmaElement);
                furgonetaElement.appendChild(plazasFurgoElement);
                elementoVehiculo.appendChild(furgonetaElement);
                raiz.appendChild(elementoVehiculo);
            }
        }
    }

    private static Vehiculo elementToVehiculo(Element elemento) {
        Vehiculo vehiculo = null;

        String marca = elemento.getElementsByTagName(MARCA).item(0).getTextContent();
        String modelo = elemento.getElementsByTagName(MODELO).item(0).getTextContent();
        String matricula = elemento.getAttribute(MATRICULA);
        String tipo = elemento.getAttribute(TIPO);

        switch (tipo) {
            case TURISMO -> {
                int cilindrada = Integer.parseInt(elemento.getElementsByTagName(CILINDRADA).item(0).getTextContent());
                vehiculo = new Turismo(marca, modelo, matricula, cilindrada);
                return vehiculo;
            }
            case AUTOBUS -> {
                int plazasAutobus = Integer.parseInt(elemento.getElementsByTagName(PLAZAS).item(0).getTextContent());
                vehiculo = new Autobus(marca, modelo, matricula, plazasAutobus);
                return vehiculo;
            }
            case FURGONETA -> {
                int pma = Integer.parseInt(elemento.getElementsByTagName(PMA).item(0).getTextContent());
                int plazasFurgoneta = Integer.parseInt(elemento.getElementsByTagName(PLAZAS).item(0).getTextContent());
                vehiculo = new Furgoneta(marca, modelo, matricula, plazasFurgoneta, pma);

                return vehiculo;
            }
        }
        return vehiculo;
    }
}