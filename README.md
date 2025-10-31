# 🚗 Alquiler de Vehículos - JavaFX
[![Build Status](https://github.com/rubennicolasdiaz/Alquiler_Vehiculos_Java_FX/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/rubennicolasdiaz/Alquiler_Vehiculos_Java_FX/actions/workflows/build.yml)
[![Tests](https://img.shields.io/github/actions/workflow/status/rubennicolasdiaz/Alquiler_Vehiculos_Java_FX/tests.yml?label=Tests&logo=pytest&logoColor=white)](https://github.com/rubennicolasdiaz/Alquiler_Vehiculos_Java_FX/actions)
![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blueviolet?logo=java)
![MySQL](https://img.shields.io/badge/MySQL-Supported-orange?logo=mysql)
![MongoDB](https://img.shields.io/badge/MongoDB-Supported-brightgreen?logo=mongodb)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?logo=docker)
![Status](https://img.shields.io/badge/Status-Active-success)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)

---

## 📚 Índice

- [📖 Descripción](#-descripción)
- [⚙️ Tecnologías y Herramientas](#️-tecnologías-y-herramientas)
- [🏛️ Arquitectura del Proyecto](#%EF%B8%8F-arquitectura-del-proyecto)
- [🧠 Funcionamiento](#-funcionamiento)
- [🏗️ Patrón MVC y Factorías](#️-patrón-mvc-y-factorías)
- [🧪 Tests de Integración](#-tests-de-integración)
- [🐳 Dockerización y Ejecución](#-dockerización-y-ejecución)
- [🎥 Vídeo explicativo en YouTube](#-vídeo-explicativo-en-youtube)
- [🧾 Licencia](#-licencia)
- [🧑‍💻 Autor](#%E2%80%8D-autor)

---

## 📖 Descripción

**Alquiler de Vehículos JavaFX** es una aplicación de escritorio desarrollada en **Java 21** con **JavaFX**, diseñada para gestionar alquileres de vehículos (clientes, vehículos y sus relaciones).  
El proyecto implementa una **arquitectura MVC** y un **sistema de factorías** que permite elegir dinámicamente la **vista** (texto o gráfica) y la **fuente de datos** (MySQL o MongoDB).

---

## ⚙️ Tecnologías y Herramientas

- ☕ **Java 21**
- 🎨 **JavaFX 21**
- 🧱 **Maven**
- 💾 **MySQL** y **MongoDB** (seleccionables)
- 🧪 **JUnit 5 / Testcontainers** (tests de integración sobre Docker)
- 🐳 **Docker / Docker Compose**
- 🧰 **GitHub Actions** (CI/CD)
- 🖥️ **Scene Builder** (para la UI JavaFX)

---

## 🏛️ Arquitectura del Proyecto

```text
 AlquilerVehiculosJava/
├── config/                                 # Archivos de configuración
│   ├── app.properties
│   ├── ficheros.properties
│   ├── mongo.properties
│   ├── mongo_test.properties
│   ├── mysql.properties
│   └── mysql_test.properties
│
├── data/                                   # Datos en formato XML
│   └── xml/
│       ├── alquileres.xml
│       ├── clientes.xml
│       └── vehiculos.xml
│
├── src/
│   └── main/
│       ├── java/org/rubennicolas/alquilervehiculos/
│       │   ├── controlador/
│       │   │   └── Controlador.java
│       │   │
│       │   ├── excepciones/
│       │   │   └── DomainException.java
│       │   │
│       │   ├── modelo/
│       │   │   ├── dominio/
│       │   │   │   ├── Alquiler.java
│       │   │   │   ├── Autobus.java
│       │   │   │   ├── Cliente.java
│       │   │   │   ├── Furgoneta.java
│       │   │   │   ├── Turismo.java
│       │   │   │   └── Vehiculo.java
│       │   │   │
│       │   │   ├── negocio/
│       │   │   │   ├── ficheros/
│       │   │   │   │   ├── Alquileres.java
│       │   │   │   │   ├── Clientes.java
│       │   │   │   │   ├── FuenteDatosFicheros.java
│       │   │   │   │   └── Vehiculos.java
│       │   │   │   │
│       │   │   │   ├── mongodb/
│       │   │   │   │   ├── Alquileres.java
│       │   │   │   │   ├── Clientes.java
│       │   │   │   │   ├── FuenteDatosMongoDB.java
│       │   │   │   │   └── Vehiculos.java
│       │   │   │   │
│       │   │   │   ├── mysql/
│       │   │   │   │   ├── Alquileres.java
│       │   │   │   │   ├── Clientes.java
│       │   │   │   │   ├── FuenteDatosMySQL.java
│       │   │   │   │   └── Vehiculos.java
│       │   │   │   │
│       │   │   │   └── utilidades/
│       │   │   │       ├── ConexionMongoDB.java
│       │   │   │       ├── ConexionMySQL.java
│       │   │   │       ├── UtilidadesAlquiler.java
│       │   │   │       └── UtilidadesFicheros.java
│       │   │   │
│       │   │   ├── IAlquileres.java
│       │   │   ├── IClientes.java
│       │   │   ├── IFuenteDatos.java
│       │   │   ├── IVehiculos.java
│       │   │   ├── FactoriaFuenteDatos.java
│       │   │   ├── Modelo.java
│       │   │   └── ModeloCascada.java
│       │   │
│       │   ├── utilidades/
│       │   │   └── Entrada.java
│       │   │
│       │   ├── vista/
│       │   │   ├── grafica/
│       │   │   │   ├── controllers/
│       │   │   │   │   ├── ControllerAddAlquileres.java
│       │   │   │   │   ├── ControllerAddClientes.java
│       │   │   │   │   ├── ControllerAddVehiculos.java
│       │   │   │   │   ├── ControllerDevolverAlquileres.java
│       │   │   │   │   ├── ControllerEditClientes.java
│       │   │   │   │   ├── ControllerEditVehiculos.java
│       │   │   │   │   ├── ControllerVistaAlquileres.java
│       │   │   │   │   ├── ControllerVistaClienteAlquiler.java
│       │   │   │   │   ├── ControllerVistaClientes.java
│       │   │   │   │   ├── ControllerVistaPrincipal.java
│       │   │   │   │   ├── ControllerVistaVehiculoAlquiler.java
│       │   │   │   │   ├── ControllerVistaVehiculos.java
│       │   │   │   │   ├── IconoVehiculoTableCell.java
│       │   │   │   │   └── TipoVehiculoTableCell.java
│       │   │   │   │
│       │   │   │   ├── rutasconstantes/
│       │   │   │   │   ├── EstilosConstantes.java
│       │   │   │   │   ├── RutasConstantesFxml.java
│       │   │   │   │   └── RutasConstantesImagenes.java
│       │   │   │   │
│       │   │   │   └── VistaGrafica.java
│       │   │   │
│       │   │   ├── texto/
│       │   │   │   ├── Accion.java
│       │   │   │   ├── Consola.java
│       │   │   │   ├── TipoVehiculo.java
│       │   │   │   ├── VistaTexto.java
│       │   │   │   ├── FactoriaVistas.java
│       │   │   │   └── Vista.java
│       │   │   │
│       │   │   ├── AppContextThread.java
│       │   │   └── MainApp.java
│       │   │
│       │   └── module-info.java
│       │
│       └── resources/
│           ├── imagenes/
│           │   ├── autobus.jpg
│           │   ├── coche.jpg
│           │   ├── coche_alquiler.jpeg
│           │   └── furgoneta.jpg
│           │
│           └── vistasfxml/
│               ├── VistaAddAlquileres.fxml
│               ├── VistaAddClientes.fxml
│               ├── VistaAddVehiculos.fxml
│               ├── VistaAlquileres.fxml
│               ├── VistaClienteAlquiler.fxml
│               ├── VistaClientes.fxml
│               ├── VistaDevolverAlquileres.fxml
│               ├── VistaEditClientes.fxml
│               ├── VistaEditVehiculos.fxml
│               ├── VistaPrincipal.fxml
│               ├── VistaVehiculoAlquiler.fxml
│               └── VistaVehiculos.fxml
│
├── test/
│   └── java/org/rubennicolas/alquilervehiculos/modelo/
│       ├── dominio.UnitTest/
│       │   ├── AlquilerTest.java
│       │   ├── AutobusTest.java
│       │   ├── ClienteTest.java
│       │   ├── FurgonetaTest.java
│       │   ├── TurismoTest.java
│       │   └── VehiculoTest.java
│       │
│       └── negocio/
│           ├── ficheros/
│           │   ├── AlquileresFicherosIT.java
│           │   ├── ClientesFicherosIT.java
│           │   └── VehiculosFicherosIT.java
│           │
│           ├── mongodb/
│           │   ├── AlquileresMongoDBIT.java
│           │   ├── ClientesMongoDBIT.java
│           │   └── VehiculosMongoDBIT.java
│           │
│           └── mysql/
│               ├── AlquileresMySQLIT.java
│               ├── ClientesMySQLIT.java
│               └── VehiculosMySQLIT.java
│
├── target/
├── .gitignore
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── README.md
└── start.sh

```

## 🧠 Funcionamiento

La aplicación gestiona tres entidades principales:

### 👤 Clientes
Permite registrar, modificar, listar y eliminar clientes.

### 🚗 Vehículos
Soporta distintos tipos: **Turismo**, **Furgoneta** y **Autobús**.

### 📅 Alquileres
Vinculan un cliente con un vehículo durante un periodo de tiempo.

---

## 🔒 Reglas de negocio

- Un **cliente** no puede alquilar más de un vehículo al mismo tiempo.  
- Un **vehículo** no puede estar alquilado por más de un cliente en el mismo periodo.  
- No se permiten **fechas de alquiler solapadas**.

Estas reglas se validan mediante excepciones controladas para ofrecer mensajes de error claros tanto en la vista de texto como en la interfaz JavaFX.

---

## 🏗️ Patrón MVC y Factorías

El proyecto aplica el patrón **Modelo–Vista–Controlador (MVC)**, separando de forma clara:

- **Modelo** → Lógica de negocio y persistencia (MySQL, MongoDB o Memoria).  
- **Vista** → Interfaz **JavaFX** o modo texto.  
- **Controlador** → Mediador entre la vista y el modelo.

A través de las **factorías**, la app permite elegir en tiempo de ejecución:

- El tipo de **vista** (gráfica o consola).  
- La **fuente de datos** (MySQL, MongoDB o en memoria).

---

## 🧪 Tests de Integración

Los tests utilizan **JUnit 5** y **Testcontainers**, ejecutándose dentro de contenedores Docker temporales:

- 🧩 `ClientesMySQLIT`, `VehiculosMySQLIT`, `AlquileresMySQLIT`
- 🍃 `ClientesMongoDBIT`, `VehiculosMongoDBIT`, `AlquileresMongoDBIT`

Cada test:

1. Levanta su propio contenedor **MySQL o MongoDB**.  
2. Crea la base de datos de test (`alquiler_vehiculos_test`).  
3. Limpia los datos antes de cada ejecución (`DELETE` o `deleteMany`).  
4. Se apaga y elimina automáticamente al finalizar.

✅ Esto garantiza que los tests **no afecten a tu entorno local** ni a las bases de datos reales.

---

## 🐳 Dockerización y Ejecución

El proyecto está **totalmente dockerizado**, tanto para la aplicación como para las bases de datos.

### 🔧 Configuración en `docker-compose.yml`

```yaml
services:
  app:
    build: .
    depends_on:
      - mysql
      - mongo
    environment:
      MYSQL_HOST: mysql
      MYSQL_PORT: 3306
      MONGO_HOST: mongo
      MONGO_PORT: 27017
    ports:
      - "8080:8080"

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: alquiler_vehiculos
    ports:
      - "3306:3306"

  mongo:
    image: mongo:6.0
    ports:
      - "27017:27017"
```

## ▶️ Ejecución local

```bash
docker compose up --build
```
Esto levantará:

La aplicación JavaFX

Contenedores de MySQL y MongoDB configurados automáticamente

## 🎥 Vídeo explicativo en YouTube
▶️ [Gestión de Alquileres de Vehículos en JavaFX - Arquitectura MVC](https://www.youtube.com/watch?v=uTrjASCOCXE)  

## 🧾 Licencia

Este proyecto se distribuye bajo licencia MIT.

## 🧑‍💻 Autor

**Rubén Nicolás Díaz**

🌐 [Portafolio](https://www.rubennicolasdiaz.me)  
💼 [LinkedIn](https://linkedin.com/in/rubennicolasdiaz)  
📫 [Email](mailto:ruben.nicolasdiaz@yahoo.com)

&copy; 2025 Rubén Nicolás Díaz
