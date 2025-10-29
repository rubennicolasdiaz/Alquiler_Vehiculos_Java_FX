#!/bin/bash
set -e

echo "[INFO] Detectando entorno gráfico..."

# Si ya existe DISPLAY y el socket X11 está montado, lo usamos
if [ -n "$DISPLAY" ] && [ -e /tmp/.X11-unix/X0 ]; then
  echo "[INFO] Entorno X11 detectado en Linux. DISPLAY=$DISPLAY"
  exec java -jar /app/alquilervehiculos.jar
  exit 0
fi

# Detectar si está en WSL (Windows Docker Desktop)
if grep -qi microsoft /proc/version 2>/dev/null; then
  export DISPLAY="host.docker.internal:0.0"
  echo "[INFO] Ejecutando en Docker Desktop (Windows). DISPLAY=$DISPLAY"
  exec java -jar /app/alquilervehiculos.jar
  exit 0
fi

# Detectar si está en Mac (Docker Desktop)
if uname -s | grep -qi "darwin"; then
  export DISPLAY="host.docker.internal:0"
  echo "[INFO] Ejecutando en Docker Desktop (macOS). DISPLAY=$DISPLAY"
  exec java -jar /app/alquilervehiculos.jar
  exit 0
fi

echo "[ERROR] No se detectó entorno gráfico. Asegúrate de compartir DISPLAY o usar VcXsrv/XQuartz."
exit 1
