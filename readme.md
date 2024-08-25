
# XMPP Client Project

Este proyecto es una aplicación de mensajería utilizando el protocolo XMPP (Extensible Messaging and Presence Protocol). El proyecto está desarrollado en Java utilizando JavaFX para la interfaz gráfica y Smack para la gestión de la mensajería.

## Requisitos

- **Java 8 o superior**
- **Apache Maven**
- **Internet** (para resolver dependencias y conectarse al servidor XMPP)

## Instalación

### 1. Clonar el repositorio

Primero, clona este repositorio en tu máquina local:




### 2. Instalar Maven

Si no tienes Maven instalado, sigue estos pasos:

### En Windows

1. Descarga Maven desde su sitio oficial: [Maven Downloads](https://maven.apache.org/download.cgi).
2. Descomprime el archivo descargado.
3. Añade la ruta de la carpeta `bin` de Maven a las variables de entorno.

### En Linux/macOS

Puedes instalar Maven usando `apt-get` o `brew`:

```bash
sudo apt-get install maven  # Linux (Debian/Ubuntu)
brew install maven          # macOS




mvn install
mvn clean install
mvn compile
Powershell
 mvn exec:java '-Dexec.mainClass="com.example.RegisterAccount"
 mvn exec:java '-Dexec.mainClass="com.example.RegisterAccount"'
