package org.rubennicolas.alquilervehiculos.modelo.dominio;

import java.util.Objects;

public class Cliente implements Comparable<Cliente> {

    private static final String ER_NOMBRE = "^([A-ZÁÉÍÓÚÑ][a-záéíóúñ]+(-[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+)?)(\\s[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+(-[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+)?){0,4}$";
    private static final String ER_DNI = "\\d{8}[A-Za-z]";
    private static final String ER_TELEFONO = "[6789]\\d{8}";
    private static final String ER_EMAIL = "^[a-z0-9._%-]+@[a-z0-9.-]+\\.[a-z]{2,}$";

    private String nombreApellidos;
    private String dni;
    private String telefono;
    private String email;

    public Cliente() {
    }

    public Cliente(String nombreApellidos, String dni, String telefono, String email) {

        setNombreApellidos(nombreApellidos);
        setDni(dni);
        setTelefono(telefono);
        setEmail(email);
    }

    public Cliente(Cliente cliente) {

        if (cliente == null) {
            throw new NullPointerException("No es posible copiar un cliente nulo.");
        }

        setNombreApellidos(cliente.getNombreApellidos());
        setDni(cliente.getDni());
        setTelefono(cliente.getTelefono());
        setEmail(cliente.getEmail());
    }

    public String getNombreApellidos() {
        return nombreApellidos;
    }

    public void setNombreApellidos(String nombreApellidos) {

        if (nombreApellidos == null) {
            throw new NullPointerException("El nombre y apellidos no pueden ser nulos.");
        }
        if (nombreApellidos.isBlank()) {
            throw new IllegalArgumentException("El nombre y apellidos no pueden estar en blanco.");
        }
        if (!nombreApellidos.matches(ER_NOMBRE)) {
            throw new IllegalArgumentException("El nombre y apellidos no tienen un formato válido. " +
                    "Primera letra mayúscula y máximo 5 palabras.");
        }
        this.nombreApellidos = nombreApellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        if (dni == null) {
            throw new NullPointerException("El DNI no puede ser nulo.");
        }
        if (dni.isBlank()) {
            throw new IllegalArgumentException("El DNI no puede estar en blanco.");
        }
        if (!dni.matches(ER_DNI)) {
            throw new IllegalArgumentException("El DNI no tiene un formato válido. " +
                    "Debe ser 8 dígitos y letra seguidos, sin guión ni ningún espacio. ");
        }
        if (!comprobarLetraDni(dni)) {
            throw new IllegalArgumentException("La letra del DNI no es correcta.");
        }
        this.dni = dni.toUpperCase();
    }

    private static boolean comprobarLetraDni(String dni) {

        String dniConFormato = dni.toUpperCase();
        int numeroDni = Integer.parseInt(dniConFormato.substring(0, 8));
        int resto = (numeroDni % 23);
        char letraDni = dniConFormato.charAt(8);

        if (dniConFormato.matches(ER_DNI)
                && (resto == 0) && letraDni == 'T') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 1) && letraDni == 'R') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 2) && letraDni == 'W') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 3) && letraDni == 'A') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 4) && letraDni == 'G') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 5) && letraDni == 'M') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 6) && letraDni == 'Y') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 7) && letraDni == 'F') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 8) && letraDni == 'P') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 9) && letraDni == 'D') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 10) && letraDni == 'X') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 11) && letraDni == 'B') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 12) && letraDni == 'N') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 13) && letraDni == 'J') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 14) && letraDni == 'Z') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 15) && letraDni == 'S') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 16) && letraDni == 'Q') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 17) && letraDni == 'V') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 18) && letraDni == 'H') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 19) && letraDni == 'L') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 20) && letraDni == 'C') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 21) && letraDni == 'K') {
            return true;
        } else if (dniConFormato.matches(ER_DNI)
                && (resto == 22) && letraDni == 'E') {
            return true;
        } else return false;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        if (telefono == null) {
            throw new NullPointerException("El teléfono no puede ser nulo.");
        }
        if (telefono.isBlank()) {
            throw new IllegalArgumentException("El teléfono no puede estar en blanco");
        }
        if (!telefono.matches(ER_TELEFONO)) {
            throw new IllegalArgumentException("El teléfono no tiene un formato válido. Deben ser 9 números " +
                    "que empiecen por 6, 7, 8 ó 9");
        }
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null) {
            throw new NullPointerException("El email no puede ser nulo.");
        }
        if (email.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar en blanco.");
        }
        if (!email.matches(ER_EMAIL)) {
            throw new IllegalArgumentException("El email no tiene un formato correcto. Ej: cliente@mail.com");
        }
        this.email = email;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Cliente other = (Cliente) obj;
        return Objects.equals(dni, other.dni);
    }

    @Override
    public String toString() {
        return String.format("%s - %s - (%s) %s", getNombreApellidos(), getDni(), getTelefono(), getEmail());
    }

    @Override
    public int compareTo(Cliente otroCliente) {
        return this.nombreApellidos.compareToIgnoreCase(otroCliente.nombreApellidos);
    }
}

