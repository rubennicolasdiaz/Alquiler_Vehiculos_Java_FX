# Imagen base con soporte completo para JavaFX y Swing
FROM bellsoft/liberica-openjfx:21
=======
# ================================================
# 🏗️ Etapa 1: Build del proyecto con Maven
# ================================================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el resto del código
COPY src ./src

# Compilar y empaquetar
RUN mvn clean package -DskipTests

# ================================================
# 🚀 Etapa 2: Imagen runtime con soporte JavaFX
# ================================================
FROM eclipse-temurin:21-jdk

# Instalar OpenJFX (necesario para apps JavaFX)
RUN apt-get update && apt-get install -y openjfx && rm -rf /var/lib/apt/lists/*

# Carpeta de trabajo
WORKDIR /app

# Copiar el JAR generado en la etapa de build
COPY --from=build /app/target/*.jar /app/alquilervehiculos.jar

# Copiar configuración y recursos
COPY config /app/config
COPY data /app/data
COPY start.sh /app/start.sh

# Dar permisos de ejecución al script de arranque
RUN chmod +x /app/start.sh

# Variables de entorno por defecto
ENV APP_CONFIG_FILE=/app/config/app.properties
ENV DISPLAY=:0
ENV TZ=Europe/Madrid

# Exponer el socket gráfico (para ejecutar JavaFX en entorno gráfico opcional)
VOLUME ["/tmp/.X11-unix"]

# Comando de inicio
CMD ["/app/start.sh"]
