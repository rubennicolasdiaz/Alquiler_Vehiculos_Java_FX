package org.rubennicolas.alquilervehiculos.modelo.negocio.utilidades;

import org.rubennicolas.alquilervehiculos.modelo.dominio.*;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UtilidadesFicheros {

    // Formato fecha:
    private static final String PATRON_FECHA = "dd/MM/yyyy";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern(PATRON_FECHA);

    // Nombres de los ficheros:
    private static final String ALQUILERES_FILE = "alquileres.xml";
    private static final String CLIENTES_FILE = "clientes.xml";
    private static final String VEHICULOS_FILE = "vehiculos.xml";

    // Etiquetas XML:
    private static final String RAIZ_ALQUILERES = "Alquileres";
    private static final String ALQUILER = "Alquiler";
    private static final String DNI_CLIENTE = "Dni";
    private static final String MATRICULA_VEHICULO = "Matricula";
    private static final String FECHA_ALQUILER = "FechaAlquiler";
    private static final String FECHA_DEVOLUCION = "FechaDevolucion";
    private static final String FORMATO = "Formato";
    private static final String TIPO_DATO = "TipoDato";
    private static final String RAIZ_CLIENTES = "Clientes";
    private static final String CLIENTE = "Cliente";
    private static final String NOMBRE = "Nombre";
    private static final String TELEFONO = "Telefono";
    private static final String EMAIL = "Email";
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

    private static final Properties config = new Properties();

    static {
        try (InputStream in = getResourceStream()) {
            if (in != null) {
                config.load(in);
            } else {
                System.out.println("[INFO] No se encontró ficheros.properties, se usarán rutas por defecto.");
            }
        } catch (IOException e) {
            System.err.println("[WARN] No se pudo cargar ficheros.properties: " + e.getMessage());
        }
    }

    // ======== MÉTODOS PÚBLICOS DE LECTURA/ESCRITURA ======== //

    public static List<Alquiler> leerXmlAlquileres() {
        return leerListaXml(ALQUILERES_FILE, RAIZ_ALQUILERES, UtilidadesFicheros::elementToAlquiler);
    }

    public static void escribirXmlAlquileres(List<Alquiler> alquileres) {
        escribirListaXml(ALQUILERES_FILE, RAIZ_ALQUILERES, alquileres, UtilidadesFicheros::alquilerToElement);
    }

    public static List<Cliente> leerXmlClientes() {
        return leerListaXml(CLIENTES_FILE, RAIZ_CLIENTES, UtilidadesFicheros::elementToCliente);
    }

    public static void escribirXmlClientes(List<Cliente> clientes) {
        escribirListaXml(CLIENTES_FILE, RAIZ_CLIENTES, clientes, UtilidadesFicheros::clienteToElement);
    }

    public static List<Vehiculo> leerXmlVehiculos() {
        return leerListaXml(VEHICULOS_FILE, RAIZ_VEHICULOS, UtilidadesFicheros::elementToVehiculo);
    }

    public static void escribirXmlVehiculos(List<Vehiculo> vehiculos) {
        escribirListaXml(VEHICULOS_FILE, RAIZ_VEHICULOS, vehiculos, UtilidadesFicheros::vehiculoToElement);
    }

    // ======== MÉTODOS GENÉRICOS ======== //

    private static <T> List<T> leerListaXml(String fileName, String raiz, ElementParser<T> parser) {
        List<T> lista = new ArrayList<>();
        File file = getFile(fileName);

        if (!file.exists()) {
            System.out.println("[INFO] No se encontró " + file.getAbsolutePath() + ". Se devuelve lista vacía.");
            return lista;
        }

        try {
            Document doc = xmlToDom(file.getAbsolutePath());
            NodeList nodos = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodos.getLength(); i++) {
                Node nodo = nodos.item(i);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    lista.add(parser.parse((Element) nodo));
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error leyendo XML " + fileName + ": " + e.getMessage());
        }
        return lista;
    }

    private static <T> void escribirListaXml(String fileName, String raiz, List<T> elementos, ElementWriter<T> writer) {
        File file = getFile(fileName);
        try {
            File dir = file.getParentFile();
            if (!dir.exists()) dir.mkdirs();

            Document doc = crearDomVacio(raiz);
            for (T elem : elementos) {
                writer.write(doc, elem);
            }
            domToXml(doc, file.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo escribir XML " + fileName + ": " + e.getMessage());
        }
    }

    // ======== CONFIGURACIÓN ======== //

    private static String getBaseDir() {
        // 1️⃣ Primero variable de entorno (modo Docker)
        String envPath = System.getenv("XML_BASE_PATH");
        if (envPath != null && !envPath.isBlank()) {
            return envPath;
        }

        // 2️⃣ Luego fichero de propiedades (modo local)
        String propPath = config.getProperty("xml.path");
        if (propPath != null && !propPath.isBlank()) {
            return propPath;
        }

        // 3️⃣ Por último, valor por defecto coherente con ambos entornos
        return "data/xml";
    }

    private static File getFile(String fileName) {
        return new File(getBaseDir(), fileName);
    }

    private static InputStream getResourceStream() {
        // 1️⃣ Variable de entorno (modo Docker)
        String external = System.getenv("FICHEROS_CONFIG_FILE");
        try {
            if (external != null && !external.isBlank()) {
                File f = new File(external);
                if (f.exists()) return new FileInputStream(f);
            }

            // 2️⃣ Ruta por defecto de Docker (por si no se pasó variable)
            File defaultDocker = new File("/app/config/ficheros.properties");
            if (defaultDocker.exists()) return new FileInputStream(defaultDocker);

        } catch (IOException ignored) {
        }

        // 3️⃣ Fallback: classpath (modo local)
        return UtilidadesFicheros.class.getClassLoader().getResourceAsStream("ficheros.properties");
    }


    // ======== DOM Y PARSERS ======== //

    private static Document xmlToDom(String rutaXML) throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return db.parse(rutaXML);
    }

    private static Document crearDomVacio(String raiz) throws ParserConfigurationException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = db.newDocument();
        document.appendChild(document.createElement(raiz));
        return document;
    }

    private static void domToXml(Document dom, String rutaXml) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(new DOMSource(dom), new StreamResult(new File(rutaXml)));
    }

    // ======== PARSERS ORIGINALES (ADAPTADOS) ======== //

    private static Alquiler elementToAlquiler(Element elemento) {
        if (elemento == null) {
            throw new NullPointerException("El elemento de alquiler no puede ser nulo");
        }

        String dni = elemento.getAttribute(DNI_CLIENTE);
        String matricula = elemento.getAttribute(MATRICULA_VEHICULO);
        String cadenaAlquiler = elemento.getElementsByTagName(FECHA_ALQUILER).item(0).getTextContent();
        String cadenaDevolucion = elemento.getElementsByTagName(FECHA_DEVOLUCION).item(0).getTextContent();

        // Reconstrucción mínima de objetos (sin depender de repositorios)
        Cliente cliente = new Cliente("Nombre", dni, "600000000", "nombre@mail.com");
        Vehiculo vehiculo = new Turismo("Marca", "Modelo", matricula, 1500);

        LocalDate fechaAlquiler = LocalDate.parse(cadenaAlquiler, FORMATO_FECHA);
        LocalDate fechaDevolucion = (cadenaDevolucion == null || cadenaDevolucion.isBlank())
                ? null : LocalDate.parse(cadenaDevolucion, FORMATO_FECHA);

        Alquiler alquiler = new Alquiler(cliente, vehiculo, fechaAlquiler);
        if (fechaDevolucion != null) {
            alquiler.setFechaDevolucion(fechaDevolucion);
        }
        return alquiler;
    }


    private static void alquilerToElement(Document dom, Alquiler alquiler) {
        Element raiz = dom.getDocumentElement();

        Element elementoAlquiler = dom.createElement(ALQUILER);
        elementoAlquiler.setAttribute(DNI_CLIENTE, alquiler.getCliente().getDni());
        elementoAlquiler.setAttribute(MATRICULA_VEHICULO, alquiler.getVehiculo().getMatricula());

        Element fechaAlquilerElement = dom.createElement(FECHA_ALQUILER);
        fechaAlquilerElement.setAttribute(FORMATO, Consola.PATRON_FECHA);
        fechaAlquilerElement.setTextContent(alquiler.getFechaAlquiler().format(FORMATO_FECHA));
        elementoAlquiler.appendChild(fechaAlquilerElement);

        Element fechaDevolucionElement = dom.createElement(FECHA_DEVOLUCION);
        fechaDevolucionElement.setAttribute(TIPO_DATO, "String");
        fechaDevolucionElement.setTextContent(
                alquiler.getFechaDevolucion() != null
                        ? alquiler.getFechaDevolucion().format(FORMATO_FECHA)
                        : ""
        );
        elementoAlquiler.appendChild(fechaDevolucionElement);

        raiz.appendChild(elementoAlquiler);
    }

    private static Cliente elementToCliente(Element elemento) {
        String dni = elemento.getAttribute(DNI_CLIENTE);
        String nombre = elemento.getElementsByTagName(NOMBRE).item(0).getTextContent();
        String telefono = elemento.getElementsByTagName(TELEFONO).item(0).getTextContent();
        String email = elemento.getElementsByTagName(EMAIL).item(0).getTextContent();
        return new Cliente(nombre, dni, telefono, email);
    }

    private static void clienteToElement(Document dom, Cliente cliente) {
        Element raiz = dom.getDocumentElement();
        Element elementoCliente = dom.createElement(CLIENTE);
        elementoCliente.setAttribute(DNI_CLIENTE, cliente.getDni());

        Element eNombre = dom.createElement(NOMBRE);
        eNombre.setAttribute(TIPO_DATO, "String");
        eNombre.setTextContent(cliente.getNombreApellidos());
        elementoCliente.appendChild(eNombre);

        Element eTelefono = dom.createElement(TELEFONO);
        eTelefono.setAttribute(TIPO_DATO, "String");
        eTelefono.setTextContent(cliente.getTelefono());
        elementoCliente.appendChild(eTelefono);

        Element eEmail = dom.createElement(EMAIL);
        eEmail.setAttribute(TIPO_DATO, "String");
        eEmail.setTextContent(cliente.getEmail());
        elementoCliente.appendChild(eEmail);

        raiz.appendChild(elementoCliente);
    }

    private static Vehiculo elementToVehiculo(Element elemento) {
        String marca = elemento.getElementsByTagName(MARCA).item(0).getTextContent();
        String modelo = elemento.getElementsByTagName(MODELO).item(0).getTextContent();
        String matricula = elemento.getAttribute(MATRICULA);
        String tipo = elemento.getAttribute(TIPO);

        return switch (tipo) {
            case TURISMO -> new Turismo(marca, modelo, matricula,
                    Integer.parseInt(elemento.getElementsByTagName(CILINDRADA).item(0).getTextContent()));
            case AUTOBUS -> new Autobus(marca, modelo, matricula,
                    Integer.parseInt(elemento.getElementsByTagName(PLAZAS).item(0).getTextContent()));
            case FURGONETA -> new Furgoneta(marca, modelo, matricula,
                    Integer.parseInt(elemento.getElementsByTagName(PLAZAS).item(0).getTextContent()),
                    Integer.parseInt(elemento.getElementsByTagName(PMA).item(0).getTextContent()));
            default -> null;
        };
    }

    private static void vehiculoToElement(Document dom, Vehiculo vehiculo) {
        Element raiz = dom.getDocumentElement();

        Element elementoVehiculo = dom.createElement(VEHICULO);
        elementoVehiculo.setAttribute(MATRICULA, vehiculo.getMatricula());
        elementoVehiculo.setAttribute(TIPO, vehiculo.getClass().getSimpleName());

        Element marcaElement = dom.createElement(MARCA);
        marcaElement.setAttribute(TIPO_DATO, "String");
        marcaElement.setTextContent(vehiculo.getMarca());
        elementoVehiculo.appendChild(marcaElement);

        Element modeloElement = dom.createElement(MODELO);
        modeloElement.setAttribute(TIPO_DATO, "String");
        modeloElement.setTextContent(vehiculo.getModelo());
        elementoVehiculo.appendChild(modeloElement);

        if (vehiculo instanceof Turismo t) {
            Element turismoElement = dom.createElement(TURISMO);
            Element cilindradaElement = dom.createElement(CILINDRADA);
            cilindradaElement.setAttribute(TIPO_DATO, "Integer");
            cilindradaElement.setTextContent(String.valueOf(t.getCilindrada()));
            turismoElement.appendChild(cilindradaElement);
            elementoVehiculo.appendChild(turismoElement);
        } else if (vehiculo instanceof Autobus a) {
            Element autobusElement = dom.createElement(AUTOBUS);
            Element plazasElement = dom.createElement(PLAZAS);
            plazasElement.setAttribute(TIPO_DATO, "Integer");
            plazasElement.setTextContent(String.valueOf(a.getPlazas()));
            autobusElement.appendChild(plazasElement);
            elementoVehiculo.appendChild(autobusElement);
        } else if (vehiculo instanceof Furgoneta f) {
            Element furgonetaElement = dom.createElement(FURGONETA);
            Element pmaElement = dom.createElement(PMA);
            pmaElement.setAttribute(TIPO_DATO, "Integer");
            pmaElement.setTextContent(String.valueOf(f.getPma()));
            Element plazasElement = dom.createElement(PLAZAS);
            plazasElement.setAttribute(TIPO_DATO, "Integer");
            plazasElement.setTextContent(String.valueOf(f.getPlazas()));
            furgonetaElement.appendChild(pmaElement);
            furgonetaElement.appendChild(plazasElement);
            elementoVehiculo.appendChild(furgonetaElement);
        }

        raiz.appendChild(elementoVehiculo);
    }

    // ======== INTERFACES FUNCIONALES ======== //
    @FunctionalInterface
    private interface ElementParser<T> {
        T parse(Element element);
    }

    @FunctionalInterface
    private interface ElementWriter<T> {
        void write(Document doc, T element);
    }
}
