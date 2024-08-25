
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
### 4. Compilar el Proyecto
Una vez que Maven esté instalado y hayas clonado el repositorio, navega a la carpeta del proyecto y ejecuta el siguiente comando:
```bash
mvn clean install
```
### 5. Ejecutar la aplicación
Una vez compilado, puedes ejecutar la aplicación con el siguiente comando:

```bash
mvn javafx:run

```

# Methods
### `start(Stage primaryStage)`
This is the starting point of the JavaFX application. It sets up the login or registration interface, showing buttons to log in or register a new account.

- **Usage**: Initializes the main window of the application.

### `showMainMenu(Stage primaryStage)`
Displays the main menu of the application with options for messaging, contacts, and account management. It also includes an icon for notifications.

- **Usage**: Allows the user to access the main functionalities of the application.

### `showMessagingOptions(Stage primaryStage)`
Shows messaging options such as starting a real-time chat, sending messages or files, and joining a group chat.

- **Usage**: Provides access to communication features within the application.

### `iniciarChatGrupalUI(Stage primaryStage)`
Displays a group chat interface where users can send and receive messages in a shared room.

- **Usage**: Enables real-time interaction within a group chat.

### `enviarMensajeGrupal(String mensaje)`
Sends a message to the active group chat if the user is authenticated and connected.

- **Usage**: Facilitates sending messages within a group chat.

### `configureRoom(MultiUserChat chatGrupal)`
Configures the group chat room, joining the room if the user has not already done so.

- **Usage**: Ensures the user is properly joined to the group chat room.

### `showContactOptions(Stage primaryStage)`
Displays options for managing contacts, such as viewing connected users, listing contacts, or adding new ones.

- **Usage**: Manages the user's contacts within the application.

### `agregarContactoUI()`
Displays an interface where the user can add a contact by entering their JID.

- **Usage**: Allows adding contacts to the roster from the graphical interface.

### `enviarArchivoUI()`
Enables the user to select a file from their system to send it to another user in Base64 format.

- **Usage**: Facilitates file selection and sending through the interface.

### `enviarArchivoBase64(String destinatario, File archivo)`
Reads a file, converts it to Base64, and sends it as a message to another user.

- **Usage**: Sends files in Base64 format via XMPP messages.

### `recibirArchivoBase64(String base64FileContent)`
Receives a file in Base64 format, decodes it, and allows the user to save it to their system.

- **Usage**: Decodes and saves received files in Base64 format.

### `enviarArchivo(String destinatario, File archivo)`
Sends a file to another user via direct XMPP file transfer.

- **Usage**: Facilitates direct file transfer between users.

### `recibirArchivo(FileTransferRequest request)`
Accepts an incoming file transfer request and saves the file to the system.

- **Usage**: Handles the reception of files sent via XMPP.

### `eliminarCuentaUI()`
Displays an interface where the user can delete their account on the XMPP server.

- **Usage**: Manages account deletion from the graphical interface.

### `registrarCuentaUI()`
Allows the user to register a new account on the XMPP server by requesting a username and password.

- **Usage**: Facilitates new account registration from the application.

### `registrarCuenta(String domain, String username, String password)`
Registers a new account on the XMPP server using the provided credentials.

- **Usage**: Interacts with the server to create a new account.

### `iniciarSesion(String domain, String username, String password)`
Logs in to the XMPP server using the provided username and password, setting up listeners for messages, presence, and file transfers.

- **Usage**: Manages the user login process and configures event listeners in the application.

### `enviarMensaje(String destinatario, String mensaje)`
Sends a text message to a specific recipient.

- **Usage**: Enables one-to-one communication via text messages.

### `cerrarSesion()`
Logs the user out of the current session, changing their presence status to "unavailable" before disconnecting.

- **Usage**: Manages session logout and notifies the server of the disconnection.

### `eliminarCuenta(String domain, String username, String password)`
Deletes the user's account on the XMPP server.

- **Usage**: Allows account deletion from the server.

### `agregarContacto(String jidStr)`
Adds a new contact to the user's roster.

- **Usage**: Facilitates adding contacts to the user's contact list.

### `checkUserPresence(String userJid)`
Checks and displays the presence (connection status) of a specific user.

- **Usage**: Allows the user to view another user's presence status within the application.

### `verContactos()`
Displays a list of the user's contacts along with their presence status.

- **Usage**: Facilitates viewing the user's contacts and their presence status.

### `actualizarListaDeContactos(Roster roster, Collection<RosterEntry> entries, VBox contactsBox)`
Updates and displays the contact list, including their presence statuses.

- **Usage**: Keeps the contact list updated in the graphical interface.

### `mostrarUsuariosConectados()`
Displays all connected users and their presence status.

- **Usage**: Allows viewing a list of currently connected users.

### `definirMensajePresencia()`
Allows the user to define their presence message and select their availability status (e.g., available, away).

- **Usage**: Manages the user's presence status customization on the server.

### `addNotification(String notification)`
Adds a new notification to the notifications list.

- **Usage**: Manages and stores notifications to display them to the user.

### `createStyledButton(String text, String backgroundColor, String textColor, String borderColor)`
Creates a button with custom styling using the specified colors and text.

- **Usage**: Facilitates the creation of styled buttons in the interface.

### `agregarSubscribeListener()`
Adds a listener that handles contact subscription requests, automatically approving them and adding contacts to the roster.

- **Usage**: Manages incoming subscription requests for contacts.

### `showNotifications(Stage primaryStage)`
Displays the accumulated notifications in a popup over the main interface.

- **Usage**: Allows quick viewing of recent notifications.





mvn install
mvn clean install
mvn compile
Powershell
 mvn exec:java '-Dexec.mainClass="com.example.RegisterAccount"
 mvn exec:java '-Dexec.mainClass="com.example.RegisterAccount"'
