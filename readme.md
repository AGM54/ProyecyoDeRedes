
# XMPP Client Project

Este proyecto es una aplicación de mensajería utilizando el protocolo XMPP (Extensible Messaging and Presence Protocol). El proyecto está desarrollado en Java utilizando JavaFX para la interfaz gráfica y Smack para la gestión de la mensajería.

## Requisitos

- **Java 8 o superior**
- **Apache Maven**
- **Internet** (para resolver dependencias y conectarse al servidor XMPP)

## Instalación

### 1. Clonar el repositorio

Primero, clona este repositorio en tu máquina local:

```bash
git clone https://github.com/AGM54/ProyectoDeRedes
cd ProyectoDeRedes

```

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

```

### 3. Instalar Java
Si no tienes Java instalado ,sigue estos pasos:
### En Windows

1. Descarga el JDK desde el sitio oficial de Oracle: Descargar JDK.
2. Instala el archivo descargado siguiendo las instrucciones del instalador.  
3. Configura las variables de entorno de tu sistema para incluir la ruta de java y javac. Esto se hace añadiendo el JAVA_HOME y actualizando la variable PATH para incluir el directorio bin dentro del directorio del JDK.


### En Linux (Debian/Ubuntu)
```bash
sudo apt-get update
sudo apt-get install default-jdk
```



mvn install
mvn clean install
mvn compile
Powershell
 mvn exec:java '-Dexec.mainClass="com.example.RegisterAccount"
 mvn exec:java '-Dexec.mainClass="com.example.RegisterAccount"'
