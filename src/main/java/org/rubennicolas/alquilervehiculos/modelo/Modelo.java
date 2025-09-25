package org.rubennicolas.alquilervehiculos.modelo;

import org.rubennicolas.alquilervehiculos.modelo.dominio.Alquiler;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Cliente;
import org.rubennicolas.alquilervehiculos.modelo.dominio.Vehiculo;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IAlquileres;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IClientes;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IFuenteDatos;
import org.rubennicolas.alquilervehiculos.modelo.negocio.IVehiculos;

import java.time.LocalDate;
import java.util.List;

public abstract class Modelo {

    /**
     * En la clase abstracta modelo tenemos como atributos clientes, alquileres, vehículos y
     * fuenteDatos, que son objetos del tipo de su correspondiente interfaz, siguiendo
     * el patrón factoría abstracta al constructor del modelo se le pasa un parámetro de
     * tipo FactoriaFuenteDatos (MEMORIA ó FICHEROS). Dentro del constructor, invocamos
     * al setFuenteDatos, y dentro de éste seleccionamoscrear del parámetro factoriaFuenteDatos,
     * lo cual creará o modelo de ficheros o modelo de memoria.
     * <p>
     * El resto del cuerpo de la clase contiene una serie de métodos normales y de métodos
     * abstractos. Todos los métodos los heredará la clase hija, ModeloCascada, aunque los métodos
     * abstractos solamente se definen en la clase padre pero tienen que desarrollarse obligatoriamente
     * en la clase hija
     */

    protected IClientes clientes;
    protected IAlquileres alquileres;
    protected IVehiculos vehiculos;
    protected IFuenteDatos fuenteDatos;

    protected Modelo(FactoriaFuenteDatos factoriaFuenteDatos) {
        setFuenteDatos(factoriaFuenteDatos.crear());
    }

    protected IClientes getClientes() {
        return this.clientes;
    }

    protected IVehiculos getVehiculos() {
        return this.vehiculos;
    }

    protected IAlquileres getAlquileres() {
        return this.alquileres;
    }

    protected void setFuenteDatos(IFuenteDatos fuenteDatos) {
        this.fuenteDatos = fuenteDatos;
    }

    /**
     * En el método comenzar, que heredará la clase ModeloCascada, inicializamos los atributos
     * clientes, vehículos y alquileres mediante el atributo fuenteDatos y su método
     * correspondiente. Una vez inicializados los atributos, llamamos al método comenzar de
     * cada uno de ellos, por lo que en el caso de la fuente de datos de memoria se leerán los
     * correspondientes ficheros XML y se volcará la información a los ArrayList
     */

    public void comenzar() {

        clientes = fuenteDatos.crearClientes();
        vehiculos = fuenteDatos.crearVehiculos();
        alquileres = fuenteDatos.crearAlquileres();

        getClientes().comenzar();
        getVehiculos().comenzar();
        getAlquileres().comenzar();
    }

    /**
     * En el método terminar, que heredará la clase ModeloCascada, hacemos que la información
     * contenida en los Arraylist se escriba de nuevo en los ficheros XML correspondientes.
     * También se ejecutará un mensaje de salida de la aplicación y por último, con el método
     * exit de la clase System el programa finalizará
     */

    public void terminar() {

        getClientes().terminar();
        getVehiculos().terminar();
        getAlquileres().terminar();

        System.out.println("La aplicación de alquiler de vehículos va a finalizar, hasta pronto...");
        System.exit(0);
    }

    public abstract void insertar(Cliente cliente);

    public abstract void insertar(Vehiculo vehiculo);

    public abstract void insertar(Alquiler alquiler);

    public abstract Cliente buscar(Cliente cliente);

    public abstract Vehiculo buscar(Vehiculo vehiculo);

    public abstract Alquiler buscar(int id);

    public abstract void modificar(Cliente cliente);

    public abstract void modificar(Vehiculo vehiculo);

    public abstract void devolver(Alquiler alquiler, LocalDate fechaDevolucion);

    public abstract void borrar(Cliente cliente);

    public abstract void borrar(Vehiculo vehiculo);

    public abstract void borrar(Alquiler alquiler);

    public abstract List<Cliente> getListaClientes();

    public abstract List<Vehiculo> getListaVehiculos();

    public abstract List<Alquiler> getListaAlquileres();

    public abstract List<Alquiler> getListaAlquileresPorCliente(Cliente cliente);

    public abstract List<Alquiler> getListaAlquileresPorVehiculo(Vehiculo vehiculo);
}
