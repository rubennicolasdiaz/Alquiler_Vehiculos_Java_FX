# Imagen base con soporte completo para JavaFX y Swing
FROM eclipse-temurin:21-jdk AS build

# Carpeta de trabajo
WORKDIR /app

# Copiar artefacto y configuración
COPY target/alquilervehiculos.jar /app/
COPY config /app/config
COPY data /app/data
COPY start.sh /app/start.sh

# Dar permisos de ejecución al script de arranque
RUN chmod +x /app/start.sh

# Variables de entorno por defecto
ENV APP_CONFIG_FILE=/app/config/app.properties
ENV DISPLAY=:0
ENV TZ=Europe/Madrid

# Exponer el socket gráfico opcionalmente (para depuración)
VOLUME ["/tmp/.X11-unix"]

# Comando de inicio
CMD ["/app/start.sh"]
