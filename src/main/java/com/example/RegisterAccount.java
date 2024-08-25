package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smack.util.dns.minidns.MiniDnsResolver;
import org.jivesoftware.smackx.filetransfer.*;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JFileChooser;


import java.util.Base64;


import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jxmpp.jid.parts.Resourcepart;

import org.jivesoftware.smack.*;


import org.jxmpp.jid.DomainBareJid;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jivesoftware.smack.debugger.ConsoleDebugger;

import org.jivesoftware.smack.roster.SubscribeListener;


public class RegisterAccount extends Application {

    private static AbstractXMPPConnection connection;
    private static Chat currentChat;

    private static VBox chatArea;

    private static ObservableList<String> notifications = FXCollections.observableArrayList();
    private String username;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("XMPP Client");

        // Load the background image
        Image backgroundImage = new Image(getClass().getResourceAsStream("/inicio.jpg"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(400);
        backgroundImageView.setFitHeight(400);
        backgroundImageView.setPreserveRatio(true);

        // Create login UI components
        VBox loginBox = new VBox(10);
        loginBox.setPadding(new Insets(20));
        loginBox.setStyle("-fx-background-color: white;");
        loginBox.setAlignment(Pos.CENTER);

        Label loginLabel = new Label("Iniciar sesión o registrar nueva cuenta");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Usuario");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");
        Button loginButton = new Button("Iniciar sesión");
        Button registerButton = new Button("Registrar nueva cuenta");

        loginBox.getChildren().addAll(loginLabel, usernameField, passwordField, loginButton, registerButton);

        VBox loginVBox = new VBox(20);
        loginVBox.setPadding(new Insets(20));
        loginVBox.getChildren().addAll(backgroundImageView, loginBox);
        loginVBox.setAlignment(Pos.CENTER);

        StackPane loginRoot = new StackPane();
        loginRoot.getChildren().add(loginVBox);

        Scene loginScene = new Scene(loginRoot, 800, 600);
        loginScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Set login scene
        primaryStage.setScene(loginScene);
        primaryStage.show();

        // Add login button action
        loginButton.setOnAction(e -> {
            username = usernameField.getText();
            String password = passwordField.getText();
            if (iniciarSesion("alumchat.lol", username, password)) {
                showMainMenu(primaryStage);
            } else {
                // Display error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Inicio de sesión fallido");
                alert.setContentText("Usuario o contraseña incorrectos");
                alert.showAndWait();
            }
        });

        // Add register button action
        registerButton.setOnAction(e -> registrarCuentaUI());
    }

    private void showMainMenu(Stage primaryStage) {
        // Load the background image
        Image backgroundImage = new Image(getClass().getResourceAsStream("/inicio.jpg"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(400);
        backgroundImageView.setFitHeight(450);
        backgroundImageView.setPreserveRatio(true);

        // Create welcome label
        Label welcomeLabel = new Label("Bienvenido, " + username);
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");
        welcomeLabel.setPadding(new Insets(20));

        // Create bell image for notifications
        Image bellImage = new Image(getClass().getResourceAsStream("/bell.png"));
        ImageView bellImageView = new ImageView(bellImage);
        bellImageView.setFitWidth(30);
        bellImageView.setFitHeight(30);
        bellImageView.setPreserveRatio(true);
        bellImageView.setOnMouseClicked(e -> showNotifications(primaryStage));

        // Create UI components
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #67e9ff");
        vbox.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setAlignment(Pos.CENTER);

        // Button for Mensajería
        Button messagingButton = new Button("Mensajería");
        messagingButton.setPrefSize(350, 150);
        messagingButton.setOnAction(e -> showMessagingOptions(primaryStage));

        // Button for Contactos
        Button contactsButton = new Button("Contactos");
        contactsButton.setPrefSize(200, 50);
        contactsButton.setOnAction(e -> showContactOptions(primaryStage));

        // Button for Account Management
        Button accountButton = new Button("Cuenta");
        accountButton.setPrefSize(200, 50);
        accountButton.setOnAction(e -> showAccountOptions(primaryStage));

        VBox rightBox = new VBox(10);
        rightBox.getChildren().addAll(contactsButton, accountButton);
        rightBox.setAlignment(Pos.CENTER);

        buttonBox.getChildren().addAll(messagingButton, rightBox);

        HBox mainContent = new HBox(10);
        mainContent.setPadding(new Insets(20));
        mainContent.getChildren().addAll(backgroundImageView, welcomeLabel, bellImageView);
        mainContent.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(mainContent, buttonBox);

        StackPane root = new StackPane();
        root.getChildren().add(vbox);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
    }
    

    private void showMessagingOptions(Stage primaryStage) {
    Stage optionsStage = new Stage();
    optionsStage.setTitle("Opciones de Mensajería");

    VBox optionsBox = new VBox(15);
    optionsBox.setPadding(new Insets(20));
    optionsBox.setAlignment(Pos.CENTER);
    optionsBox.setStyle("-fx-background-color: #000000; -fx-border-radius: 10px; -fx-background-radius: 10px;");

    // Crear botones con estilos personalizados
    Button chatButton = createStyledButton("Chat en tiempo real", "#4CAF50", "white", "#388E3C");
    Button sendMessageButton = createStyledButton("Enviar mensaje a un usuario", "#FF9800", "white", "#F57C00");
    Button sendFileButton = createStyledButton("Enviar archivo", "#2196F3", "white", "#1976D2");
    Button groupChatButton = createStyledButton("Chat Grupal", "#9C27B0", "white", "#7B1FA2");

    // Asignar acciones a los botones
    chatButton.setOnAction(e -> iniciarChatUI(primaryStage));
    sendMessageButton.setOnAction(e -> enviarMensajeUI());
    sendFileButton.setOnAction(e -> enviarArchivoUI());
    groupChatButton.setOnAction(e -> iniciarChatGrupalUI(primaryStage)); // Aquí llamas a la UI del chat grupal

    // Añadir los botones al VBox
    optionsBox.getChildren().addAll(chatButton, sendMessageButton, sendFileButton, groupChatButton);

    Scene optionsScene = new Scene(optionsBox, 400, 250);
    optionsStage.setScene(optionsScene);
    optionsStage.show();
}
private void iniciarChatGrupalUI(Stage primaryStage) {
    Stage groupChatStage = new Stage();
    groupChatStage.setTitle("Chat Grupal");

    VBox groupChatBox = new VBox(10);
    groupChatBox.setPadding(new Insets(20));
    groupChatBox.setAlignment(Pos.CENTER);

    chatArea = new VBox();
    chatArea.setStyle("-fx-background-color: #000000;");
    chatArea.setPrefHeight(400);

    ScrollPane scrollPane = new ScrollPane(chatArea);
    scrollPane.setFitToWidth(true);
    scrollPane.setStyle("-fx-background: #000000;");

    TextField messageField = new TextField();
    messageField.setPromptText("Escribe tu mensaje...");

    Button sendButton = new Button("Enviar");

    HBox messageBox = new HBox(10, messageField, sendButton);
    messageBox.setAlignment(Pos.CENTER);
    groupChatBox.getChildren().addAll(scrollPane, messageBox);

    Scene groupChatScene = new Scene(groupChatBox, 500, 500);
    groupChatStage.setScene(groupChatScene);
    groupChatStage.show();

    // Configurar el chat grupal
    seleccionarSalaChatGrupal();

    // Añadir el listener de envío de mensajes
    sendButton.setOnAction(e -> {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            enviarMensajeGrupal(message);
            agregarMensaje("Tú: " + message, true);
            messageField.clear();
        }
    });
}
private MultiUserChat chatGrupal;
private String nombreSalaActual;  // Variable para almacenar el nombre de la sala actual

private void seleccionarSalaChatGrupal() {
    // Crear un cuadro de diálogo para ingresar el nombre de la sala
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Seleccionar sala de chat grupal");
    dialog.setHeaderText("Unirse o crear una sala de chat grupal");
    dialog.setContentText("Ingrese el nombre de la sala:");

    // Esperar a que el usuario ingrese el nombre
    dialog.showAndWait().ifPresent(nombreSala -> {
        try {
            // Almacenar el nombre de la sala ingresado por el usuario
            nombreSalaActual = nombreSala;

            // Crear el JID completo de la sala basado en el nombre ingresado
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            EntityBareJid salaJid = JidCreate.entityBareFrom(nombreSalaActual + "@conference.alumchat.lol");

            // Obtener la sala de chat grupal o crearla si no existe
            chatGrupal = manager.getMultiUserChat(salaJid);

            // Unirse a la sala si aún no lo has hecho
            if (!chatGrupal.isJoined()) {
                chatGrupal.join(Resourcepart.from(username));
            }

            // Mostrar un mensaje confirmando la unión a la sala
            agregarMensaje("Te has unido a la sala: " + nombreSalaActual, false);

            // Listener para recibir mensajes en el chat grupal
            chatGrupal.addMessageListener(message -> {
                Platform.runLater(() -> {
                    if (message.getBody() != null) {
                        agregarMensaje(message.getFrom() + ": " + message.getBody(), false);
                    }
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    });

private void configureRoom(MultiUserChat chatGrupal) {
    try {
    
        if (!chatGrupal.isJoined()) {
            chatGrupal.join(Resourcepart.from(username));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}


private void enviarMensajeGrupal(String mensaje) {
    try {
        // Verificar que el usuario esté autenticado y conectado a un chat grupal
        if (chatGrupal != null && connection.isAuthenticated()) {
            // Enviar el mensaje al grupo
            chatGrupal.sendMessage(mensaje);

            // Mostrar el mensaje en la interfaz gráfica del usuario
            agregarMensaje("Tú: " + mensaje, true);
        } else {
            System.out.println("No has iniciado sesión o no estás en un chat grupal.");
        }
    } catch (Exception e) {
        System.out.println("Error al enviar mensaje grupal: " + e.getMessage());
        e.printStackTrace();
    }
}



    

    private void showContactOptions(Stage primaryStage) {
        Stage optionsStage = new Stage();
        optionsStage.setTitle("Opciones de Contactos");
    
        VBox optionsBox = new VBox(15);
        optionsBox.setPadding(new Insets(20));
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setStyle("-fx-background-color: #000000; -fx-border-radius: 10px; -fx-background-radius: 10px;");
    
        // Crear botones con estilos personalizados
        Button showUsersButton = createStyledButton("Mostrar todos los usuarios conectados", "#4CAF50", "white", "#388E3C");
        Button viewContactsButton = createStyledButton("Ver contactos", "#2196F3", "white", "#1976D2");
        Button addContactButton = createStyledButton("Agregar un contacto", "#FF9800", "white", "#F57C00");
    
        // Asignar acciones a los botones
        showUsersButton.setOnAction(e -> mostrarUsuariosConectados());
        viewContactsButton.setOnAction(e -> verContactos());
        addContactButton.setOnAction(e -> agregarContactoUI());
    
        // Añadir los botones al VBox
        optionsBox.getChildren().addAll(showUsersButton, viewContactsButton, addContactButton);
    
        Scene optionsScene = new Scene(optionsBox, 400, 250);
        optionsStage.setScene(optionsScene);
        optionsStage.show();
    }
    

    private void showAccountOptions(Stage primaryStage) {
        Stage optionsStage = new Stage();
        optionsStage.setTitle("Opciones de Cuenta");
    
        VBox optionsBox = new VBox(15);
        optionsBox.setPadding(new Insets(20));
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 10px; -fx-background-radius: 10px;");
    
        // Crear botones con estilos personalizados
        Button registerButton = createStyledButton("Registrar nueva cuenta", "#4CAF50", "white", "#388E3C");
        Button deleteAccountButton = createStyledButton("Eliminar cuenta del servidor", "#F44336", "white", "#D32F2F");
        Button logoutButton = createStyledButton("Cerrar sesión", "#FF9800", "white", "#F57C00");
        Button setPresenceMessageButton = createStyledButton("Definir mensaje de presencia", "#2196F3", "white", "#1976D2");
    
        // Asignar acciones a los botones
        registerButton.setOnAction(e -> registrarCuentaUI());
        deleteAccountButton.setOnAction(e -> eliminarCuentaUI());
        logoutButton.setOnAction(e -> cerrarSesion());
        setPresenceMessageButton.setOnAction(e -> definirMensajePresencia());
    
        // Añadir los botones al VBox
        optionsBox.getChildren().addAll(registerButton, deleteAccountButton, logoutButton, setPresenceMessageButton);
    
        Scene optionsScene = new Scene(optionsBox, 300, 250);
        optionsStage.setScene(optionsScene);
        optionsStage.show();
    }
    

    private void agregarMensaje(String mensaje, boolean esEnviado) {
        Platform.runLater(() -> {
            Label messageLabel = new Label(mensaje);
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(300);

            // Estilo para los mensajes
            if (esEnviado) {
                messageLabel.setStyle("-fx-background-color: #800080; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 10px;");
            } else {
                messageLabel.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: black; -fx-padding: 10px; -fx-background-radius: 10px;");
            }

            HBox messageBox = new HBox();
            messageBox.setAlignment(esEnviado ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            messageBox.getChildren().add(messageLabel);
            messageBox.setPadding(new Insets(5));

            chatArea.getChildren().add(messageBox);
        });
    }

    private void iniciarChatUI(Stage primaryStage) {
        Stage chatStage = new Stage();
        chatStage.setTitle("Chat en tiempo real");

        VBox chatBox = new VBox(10);
        chatBox.setPadding(new Insets(20));
        chatBox.setAlignment(Pos.CENTER);

        chatArea = new VBox();
        chatArea.setStyle("-fx-background-color: #000000;");
        chatArea.setPrefHeight(400);

        ScrollPane scrollPane = new ScrollPane(chatArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #000000;");

        TextField messageField = new TextField();
        messageField.setPromptText("Escribe tu mensaje...");

        Button sendButton = new Button("Enviar");

        HBox messageBox = new HBox(10, messageField, sendButton);
        messageBox.setAlignment(Pos.CENTER);
        chatBox.getChildren().addAll(scrollPane, messageBox);

        Scene chatScene = new Scene(chatBox, 500, 500);
        chatStage.setScene(chatScene);
        chatStage.show();

        // Add send button action
        sendButton.setOnAction(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                enviarMensaje(currentChat.getXmppAddressOfChatPartner().toString(), message);
                agregarMensaje("Tú: " + message, true);
                messageField.clear();
            }
        });

        seleccionarUsuarioChat();
    }

    private void seleccionarUsuarioChat() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Seleccionar usuario");
        dialog.setHeaderText("Seleccionar usuario");
        dialog.setContentText("Ingrese el JID del usuario con el que desea chatear:");

        dialog.showAndWait().ifPresent(destinatario -> {
            try {
                EntityBareJid jid = JidCreate.entityBareFrom(destinatario);
                currentChat = ChatManager.getInstanceFor(connection).chatWith(jid);
                agregarMensaje("Chateando con: " + destinatario, false);

                // Escuchar mensajes entrantes
                ChatManager.getInstanceFor(connection).addIncomingListener((from, message, chat) -> {
                    if (chat.equals(currentChat)) {
                        agregarMensaje(from.toString() + ": " + message.getBody(), false);
                    }
                });

            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }
        });
    }

    private void enviarMensajeUI() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enviar mensaje");
        dialog.setHeaderText("Enviar mensaje");
        dialog.setContentText("Ingrese el JID del destinatario:");

        dialog.showAndWait().ifPresent(destinatario -> {
            TextInputDialog messageDialog = new TextInputDialog();
            messageDialog.setTitle("Enviar mensaje");
            messageDialog.setHeaderText("Enviar mensaje");
            messageDialog.setContentText("Ingrese el mensaje:");

            messageDialog.showAndWait().ifPresent(mensaje -> enviarMensaje(destinatario, mensaje));
        });
    }

   private void enviarArchivoUI() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Seleccionar archivo para enviar");
    File file = fileChooser.showOpenDialog(null);
    
    if (file != null && file.exists()) {  // Asegurarse de que el archivo exista
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enviar archivo");
        dialog.setHeaderText("Enviar archivo en Base64");
        dialog.setContentText("Ingrese el JID del destinatario:");

        dialog.showAndWait().ifPresent(destinatario -> enviarArchivoBase64(destinatario, file));
    } else {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Archivo no encontrado");
        alert.setContentText("El archivo seleccionado no existe o no se puede acceder.");
        alert.showAndWait();
    }
}



public static void enviarArchivoBase64(String destinatario, File archivo) {
    try {
        if (connection != null && connection.isAuthenticated()) {
            System.out.println("Conexión autenticada.");
            System.out.println("Archivo existe: " + archivo.getAbsolutePath());

            // Leer el archivo y codificarlo en Base64
            byte[] fileBytes = Files.readAllBytes(archivo.toPath());
            String encodedFile = Base64.getEncoder().encodeToString(fileBytes);

            System.out.println("Archivo codificado en Base64: " + encodedFile);

            // Enviar el archivo como mensaje en Base64
            enviarMensaje(destinatario, encodedFile);

            System.out.println("Archivo enviado a " + destinatario + " en formato Base64.");
        } else {
            System.out.println("No has iniciado sesión.");
        }
    } catch (Exception e) {
        System.out.println("Error al enviar archivo en Base64: " + e.getMessage());
        e.printStackTrace();
    }
}

public static void recibirArchivoBase64(String base64FileContent) {
    try {
    
        byte[] decodedBytes = Base64.getDecoder().decode(base64FileContent);

       
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccione la ubicación para guardar el archivo");

     
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

  
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
         
            File fileToSave = fileChooser.getSelectedFile();
            Path outputPath = fileToSave.toPath();

           
            Files.createDirectories(outputPath.getParent());

      
            Files.write(outputPath, decodedBytes);

          
            System.out.println("Archivo guardado en: " + outputPath.toString());

            addNotification("Archivo recibido y guardado en: " + outputPath.toString());
        } else {
            System.out.println("Guardado cancelado por el usuario.");
        }

    } catch (Exception e) {
        // Manejar cualquier error que ocurra durante la decodificación o escritura del archivo
        System.out.println("Error al recibir y guardar el archivo: " + e.getMessage());
        e.printStackTrace();
    }

}


    public static void enviarArchivo(String destinatario, File archivo) {
    try {
        if (connection != null && connection.isAuthenticated()) {
            System.out.println("Conexión autenticada.");
            System.out.println("Archivo existe: " + archivo.getAbsolutePath());

            // Verificar la presencia del destinatario con reintentos
            Roster roster = Roster.getInstanceFor(connection);
            EntityBareJid bareJid = JidCreate.entityBareFrom(destinatario);
            boolean isAvailable = false;

     

            EntityFullJid fullJid = JidCreate.entityFullFrom(destinatario + "/Smack");
            System.out.println("JID del destinatario creado: " + fullJid.toString());

            FileTransferManager manager = FileTransferManager.getInstanceFor(connection);
            OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(fullJid);

            System.out.println("Iniciando la transferencia del archivo: " + archivo.getAbsolutePath());
            transfer.sendFile(archivo, "Aquí tienes el archivo");

            // Verificar el estado de la transferencia
            while (!transfer.isDone()) {
                System.out.println("Estado de la transferencia: " + transfer.getStatus());
                if (transfer.getStatus().equals(FileTransfer.Status.in_progress)) {
                    System.out.println("Transferencia en progreso...");
                } else if (transfer.getStatus().equals(FileTransfer.Status.error)) {
                    System.out.println("Error durante la transferencia: " + transfer.getError() + " - " + transfer.getException());
                    break; // Salir del bucle si hay un error
                }
                Thread.sleep(1000); // Esperar 1 segundo antes de verificar el estado nuevamente
            }

            if (transfer.getStatus().equals(FileTransfer.Status.complete)) {
                System.out.println("Archivo enviado a " + destinatario);
            } else {
                System.out.println("Error al enviar archivo: " + transfer.getStatus() + " - " + transfer.getError() + " - " + transfer.getException());
            }
        } else {
            System.out.println("No has iniciado sesión.");
        }
    } catch (Exception e) {
        System.out.println("Error al enviar archivo: " + e.getMessage());
        e.printStackTrace();
    }
}


  public static void recibirArchivo(FileTransferRequest request) {
        try {
            System.out.println("Aceptando la solicitud de archivo de " + request.getRequestor());
            IncomingFileTransfer transfer = request.accept();
            File archivo = new File("recibido_" + request.getFileName());
            System.out.println("Recibiendo archivo: " + archivo.getAbsolutePath());
            transfer.receiveFile(archivo);
            System.out.println("Archivo recibido: " + archivo.getAbsolutePath());
    
            // Añadir notificación
            addNotification("Archivo recibido de " + request.getRequestor() + ": " + archivo.getName());
        } catch (Exception e) {
            System.out.println("Error al recibir archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    


    private void eliminarCuentaUI() {
        Stage stage = new Stage();
        stage.setTitle("Eliminar cuenta");
    
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #2c3e50; -fx-border-radius: 10px; -fx-background-radius: 10px;");
    
        Label label = new Label("Eliminar cuenta");
        label.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Usuario");
        usernameField.setStyle("-fx-background-color: #34495e; -fx-text-fill: #ecf0f1; -fx-prompt-text-fill: #bdc3c7; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");
        passwordField.setStyle("-fx-background-color: #34495e; -fx-text-fill: #ecf0f1; -fx-prompt-text-fill: #bdc3c7; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        
        Button deleteButton = createStyledButton("Eliminar", "#F44336", "white", "#D32F2F");
    
        vbox.getChildren().addAll(label, usernameField, passwordField, deleteButton);
    
        Scene scene = new Scene(vbox, 300, 250);
        stage.setScene(scene);
        stage.show();
    
        deleteButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            eliminarCuenta("alumchat.lol", username, password);
            stage.close();
        });
    }
    

    private void agregarContactoUI() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar contacto");
        dialog.setHeaderText("Agregar contacto");
        dialog.setContentText("Ingrese el JID del contacto:");
    
        // Cargar la imagen personalizada
        ImageView customIcon = new ImageView(new Image(getClass().getResourceAsStream("/icono.png")));
        customIcon.setFitWidth(30);
        customIcon.setFitHeight(30);
    
        // Reemplazar el icono por defecto
        dialog.setGraphic(customIcon);
    
        // Estilo del diálogo
        dialog.getDialogPane().setStyle("-fx-background-color: #2c3e50; -fx-border-radius: 10px; -fx-background-radius: 10px;");
    
        // Estilo del header
        dialog.getDialogPane().lookup(".header-panel").setStyle("-fx-background-color: #34495e; -fx-text-fill: #ecf0f1;");
    
        // Estilo del contenido
        dialog.getDialogPane().lookup(".content").setStyle("-fx-text-fill: #ecf0f1;");
    
        // Estilo del campo de texto
        dialog.getEditor().setStyle("-fx-background-color: #34495e; -fx-text-fill: #ecf0f1; -fx-prompt-text-fill: #7f8c8d;");
    
        // Estilo de los botones
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        okButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
    
        dialog.showAndWait().ifPresent(contacto -> agregarContacto(contacto + "@alumchat.lol"));
    }
    
    

    private void registrarCuentaUI() {
        Stage stage = new Stage();
        stage.setTitle("Registrar nueva cuenta");
    
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #2c3e50; -fx-border-radius: 10px; -fx-background-radius: 10px;");
    
        Label label = new Label("Registrar nueva cuenta");
        label.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Usuario");
        usernameField.setStyle("-fx-background-color: #34495e; -fx-text-fill: #ecf0f1; -fx-prompt-text-fill: #bdc3c7; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");
        passwordField.setStyle("-fx-background-color: #34495e; -fx-text-fill: #ecf0f1; -fx-prompt-text-fill: #bdc3c7; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        
        Button registerButton = createStyledButton("Registrar", "#4CAF50", "white", "#388E3C");
        
        vbox.getChildren().addAll(label, usernameField, passwordField, registerButton);
    
        Scene scene = new Scene(vbox, 300, 250);
        stage.setScene(scene);
        stage.show();
    
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            registrarCuenta("alumchat.lol", username, password);
            stage.close();
        });
    }
    
    public static void registrarCuenta(String domain, String username, String password) {
        try {
            Localpart localpart = Localpart.from(username);

            // Configurar el DNS resolver
            DNSUtil.setDNSResolver(MiniDnsResolver.getInstance());

            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(domain)
                    .setHost(domain)
                    .setPort(5222)
                    .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                    .build();

            connection = new XMPPTCPConnection(config);

            try {
                connection.connect();
                System.out.println("Conectado al servidor");

                AccountManager accountManager = AccountManager.getInstance(connection);
                accountManager.sensitiveOperationOverInsecureConnection(true);

                if (accountManager.supportsAccountCreation()) {
                    accountManager.createAccount(localpart, password);
                    System.out.println("Cuenta registrada exitosamente");
                } else {
                    System.out.println("El servidor no soporta la creación de cuentas");
                }

            } catch (SmackException | IOException | XMPPException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }
public static boolean iniciarSesion(String domain, String username, String password) {
    try {
        // Configurar el DNS resolver
        DNSUtil.setDNSResolver(MiniDnsResolver.getInstance());
        System.out.println("DNS resolver configurado.");

        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setXmppDomain(domain)
                .setHost(domain)
                .setPort(5222)
                .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                .setDebuggerFactory(ConsoleDebugger::new)  // Habilitar depuración en consola
                .build();
        System.out.println("Configuración de la conexión establecida.");

        connection = new XMPPTCPConnection(config);

        try {
            connection.connect();
            System.out.println("Conexión al servidor establecida.");

            connection.login(username, password);
            System.out.println("Sesión iniciada exitosamente con el usuario: " + username);
            agregarSubscribeListener();

            // Mostrar las características del servidor
            ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(connection);
            Jid serverJid = JidCreate.from(domain);
            DiscoverInfo info = discoManager.discoverInfo(serverJid);
            System.out.println("Características del servidor:");
            for (DiscoverInfo.Feature feature : info.getFeatures()) {
                System.out.println(" - " + feature.getVar());
            }

            // Añadir el listener para mensajes entrantes
            ChatManager chatManager = ChatManager.getInstanceFor(connection);
            chatManager.addIncomingListener((from, message, chat) -> {
               // System.out.println("Mensaje recibido de " + from + ": " + message.getBody());
            
                // Crear una instancia de RegisterAccount para llamar al método no estático
                RegisterAccount registerAccount = new RegisterAccount();
                
                // Mostrar el mensaje en la interfaz gráfica (chatArea)
                Platform.runLater(() -> registerAccount.agregarMensaje(from.toString() + ": " + message.getBody(), false));
            
                // Añadir el mensaje a la lista de notificaciones
                addNotification("Nuevo mensaje de " + from + ": " + message.getBody());

                if (message.getBody() != null) {
                    System.out.println("Mensaje recibido de " + from + ": " + message.getBody());
            
                    // Verificar si el mensaje contiene un archivo en Base64
                    if (message.getBody().startsWith("BASE64FILE:")) {
                        // Si el mensaje comienza con "BASE64FILE:", extraer el contenido Base64
                        String base64Content = message.getBody().substring("BASE64FILE:".length());
                        String outputFilePath = "C:\\Users\\marce\\Desktop";  // Definir ruta para guardar el archivo
                        recibirArchivoBase64(base64Content);
                    } else if (message.getBody().matches("^[A-Za-z0-9+/=\\s]+$")) {
                        // Si el mensaje parece ser una cadena Base64 (sin prefijo)
                        
                        String outputFilePath = "C:\\Users\\marce\\Desktop";  // Definir ruta para guardar el archivo
        recibirArchivoBase64(message.getBody());
                    } else {
                        registerAccount.agregarMensaje(from.toString() + ": " + message.getBody(), false);
                        addNotification("Nuevo mensaje de " + from + ": " + message.getBody());
                    }
                }
            });
            
            System.out.println("Listener de mensajes entrantes añadido.");

            // Añadir listener para cambios en la presencia
            Roster roster = Roster.getInstanceFor(connection);
            roster.addRosterListener(new RosterListener() {
                @Override
                public void entriesAdded(Collection<Jid> addresses) {}

                @Override
                public void entriesUpdated(Collection<Jid> addresses) {}

                @Override
                public void entriesDeleted(Collection<Jid> addresses) {}

                @Override
                public void presenceChanged(Presence presence) {
                    Platform.runLater(() -> {
                      String statusMessage = presence.getStatus() != null ? presence.getStatus() : "Sin mensaje de presencia";
            System.out.println("Cambio en la presencia: " + presence.getFrom() + " - " + statusMessage);
            addNotification("Cambio en la presencia: " + presence.getFrom() + " - " + statusMessage);
          
                        
       
        });
                    
                }
            });
            System.out.println("Listener de presencia añadido.");

            // Añadir listener para solicitudes de transferencia de archivos
            FileTransferManager manager = FileTransferManager.getInstanceFor(connection);
            manager.addFileTransferListener(request -> {
                System.out.println("Solicitud de transferencia de archivo recibida de " + request.getRequestor());
                Platform.runLater(() -> recibirArchivo(request));
            });
            System.out.println("Listener de transferencia de archivos añadido.");

            // Establecer presencia
            Presence presence = new Presence(Presence.Type.available);
            connection.sendStanza(presence);
            System.out.println("Presencia establecida como disponible.");

        
            // Intentar enviar un archivo
            File archivo = new File("C:\\Users\\marce\\Downloads\\icono.png");
            if (archivo.exists()) {
                System.out.println("El archivo existe: " + archivo.getAbsolutePath());
               // enviarArchivo(destinatario, archivo);
            } else {
                System.out.println("El archivo no existe: " + archivo.getAbsolutePath());
            }

            return true;

        } catch (SmackException | IOException | XMPPException | InterruptedException e) {
            System.out.println("Error al iniciar sesión: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    } catch (Exception e) {
        System.out.println("Error general: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

public boolean isBase64(String str) {
    try {
        // Decodificar y verificar si la cadena está en Base64
        byte[] decodedBytes = Base64.getDecoder().decode(str);
        return true;
    } catch (IllegalArgumentException e) {
        // Si no es Base64, devolver false
        return false;
    }
}
    

    public static void enviarMensaje(String destinatario, String mensaje) {
        try {
            if (connection != null && connection.isAuthenticated()) {
                ChatManager chatManager = ChatManager.getInstanceFor(connection);
                EntityBareJid jid = JidCreate.entityBareFrom(destinatario);
                Chat chat = chatManager.chatWith(jid);

                Message message = new Message(jid, Message.Type.chat);
                message.setBody(mensaje);
                chat.send(message);

                System.out.println("Mensaje enviado a " + destinatario);
            } else {
                System.out.println("No has iniciado sesión.");
            }
        } catch (Exception e) {
            System.out.println("Error al enviar mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }
public static void cerrarSesion() {
    if (connection != null && connection.isConnected()) {
        try {
            // Enviar presencia 'unavailable' antes de desconectarse
            Presence presence = new Presence(Presence.Type.unavailable);
            connection.sendStanza(presence);  // Notifica al servidor que te desconectas
            System.out.println("Presencia establecida como 'unavailable'.");

            // Desconectar la conexión
            connection.disconnect();
            System.out.println("Sesión cerrada exitosamente.");
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            System.out.println("Error al cerrar la sesión: " + e.getMessage());
            e.printStackTrace();
        }
    } else {
        System.out.println("No hay ninguna sesión activa.");
    }
}


    public static void eliminarCuenta(String domain, String username, String password) {
        try {
            // Configurar el DNS resolver
            DNSUtil.setDNSResolver(MiniDnsResolver.getInstance());

            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(domain)
                    .setHost(domain)
                    .setPort(5222)
                    .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                    .build();

            connection = new XMPPTCPConnection(config);

            try {
                connection.connect();
                connection.login(username, password);
                System.out.println("Sesión iniciada exitosamente");

                AccountManager accountManager = AccountManager.getInstance(connection);
                accountManager.deleteAccount();
                System.out.println("Cuenta eliminada exitosamente.");

            } catch (SmackException | IOException | XMPPException | InterruptedException e) {
                System.out.println("Error al eliminar la cuenta: " + e.getMessage());
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void agregarContacto(String jidStr) {
        if (connection != null && connection.isAuthenticated()) {
            try {
                EntityBareJid jid = JidCreate.entityBareFrom(jidStr);
                Roster roster = Roster.getInstanceFor(connection);
                roster.createEntry(jid, jidStr, null);
                System.out.println("Contacto agregado exitosamente.");
            } catch (Exception e) {
                System.out.println("Error al agregar contacto: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No has iniciado sesión.");
        }
    }


  public void checkUserPresence(String userJid) {
    try {
        // Verificar que la conexión esté autenticada
        if (connection != null && connection.isAuthenticated()) {
            // Crear el JID del usuario del cual deseas obtener el estado de presencia
            EntityBareJid jid = JidCreate.entityBareFrom(userJid);

            // Obtener el roster (lista de contactos)
            Roster roster = Roster.getInstanceFor(connection);

            // Obtener la presencia del usuario
            Presence presence = roster.getPresence(jid);

            // Mostrar el tipo de presencia y el mensaje personalizado
            System.out.println("Tipo de presencia de " + userJid + ": " + presence.getType());
            System.out.println("Mensaje de presencia: " + (presence.getStatus() != null ? presence.getStatus() : "Sin mensaje de presencia"));
        } else {
            System.out.println("No has iniciado sesión.");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}



    
    public static void verContactos() {
        
        if (connection != null && connection.isAuthenticated()) {
            try {
                Roster roster = Roster.getInstanceFor(connection);
                Collection<RosterEntry> entries = roster.getEntries();
    
                Stage contactosStage = new Stage();
                contactosStage.setTitle("Lista de Contactos");
    
                VBox contactsBox = new VBox(10);
                contactsBox.setPadding(new Insets(20));
                contactsBox.setAlignment(Pos.CENTER);
                contactsBox.setStyle("-fx-background-color: #2c3e50; -fx-border-radius: 10px; -fx-background-radius: 10px;");
    
                // Llama al método para actualizar la lista de contactos
                actualizarListaDeContactos(roster, entries, contactsBox);
    
                // Crear un ScrollPane para el VBox en caso de que haya muchos contactos
                ScrollPane scrollPane = new ScrollPane(contactsBox);
                scrollPane.setFitToWidth(true);
                scrollPane.setStyle("-fx-background-color: #2c3e50;");
    
                Scene scene = new Scene(scrollPane, 400, 300);
                contactosStage.setScene(scene);
                contactosStage.show();
    
                // Añadir el listener para cambios en la presencia
                roster.addRosterListener(new RosterListener() {
                    @Override
                    public void entriesAdded(Collection<Jid> addresses) {}
    
                    @Override
                    public void entriesUpdated(Collection<Jid> addresses) {}
    
                    @Override
                    public void entriesDeleted(Collection<Jid> addresses) {}
    
                    @Override
                    public void presenceChanged(Presence presence) {
                        Platform.runLater(() -> {
                            // Actualiza la lista de contactos cuando cambia la presencia
                            contactsBox.getChildren().clear();
                            actualizarListaDeContactos(roster, entries, contactsBox);
                        });
                    }
                });
    
            } catch (Exception e) {
                System.out.println("Error al obtener la lista de contactos: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No has iniciado sesión.");
        }
    }
    
    private static void actualizarListaDeContactos(Roster roster, Collection<RosterEntry> entries, VBox contactsBox) {
        for (RosterEntry entry : entries) {
            Presence presence = roster.getPresence(entry.getJid());
    
            // Aquí es donde obtienes la presencia actualizada
            String status = presence.isAvailable() ? "Conectado" : "Desconectado";
            String presenceMessage = presence.getStatus() != null ? presence.getStatus() : "Sin mensaje de presencia";
    
            HBox contactItem = new HBox(10);
            contactItem.setPadding(new Insets(10));
            contactItem.setAlignment(Pos.CENTER_LEFT);
            contactItem.setStyle("-fx-background-color: #34495e; -fx-border-radius: 5px; -fx-background-radius: 5px;");
    
            Label contactLabel = new Label(entry.getJid().toString());
            contactLabel.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-font-weight: bold;");
    
            Label statusLabel = new Label(status);
            statusLabel.setStyle(status.equals("Conectado") ? "-fx-text-fill: #2ecc71; -fx-font-size: 14px;" : "-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
    
            Label messageLabel = new Label(presenceMessage);
            messageLabel.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 12px;");
    
            contactItem.getChildren().addAll(contactLabel, statusLabel, messageLabel);
            contactsBox.getChildren().add(contactItem);
        }
    }
    

    
    public static void mostrarUsuariosConectados() {
        if (connection != null && connection.isAuthenticated()) {
            try {
                Roster roster = Roster.getInstanceFor(connection);
                Collection<RosterEntry> entries = roster.getEntries();
    
                // Crear un nuevo Stage para mostrar los usuarios conectados
                Stage usuariosStage = new Stage();
                usuariosStage.setTitle("Lista de Usuarios Conectados");
    
                VBox usuariosBox = new VBox(10);
                usuariosBox.setPadding(new Insets(20));
                usuariosBox.setAlignment(Pos.CENTER);
                usuariosBox.setStyle("-fx-background-color: #2c3e50; -fx-border-radius: 10px; -fx-background-radius: 10px;");
    
                // Añadir los usuarios conectados al VBox
                for (RosterEntry entry : entries) {
                    Presence presence = roster.getPresence(entry.getJid());
                    String status = presence.isAvailable() ? "Conectado" : "Desconectado";
                    String presenceMessage = presence.getStatus() != null ? presence.getStatus() : "Sin mensaje de presencia";
    
                    HBox userItem = new HBox(10);
                    userItem.setPadding(new Insets(10));
                    userItem.setAlignment(Pos.CENTER_LEFT);
                    userItem.setStyle("-fx-background-color: #34495e; -fx-border-radius: 5px; -fx-background-radius: 5px;");
    
                    Label userLabel = new Label(entry.getJid().toString());
                    userLabel.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-font-weight: bold;");
    
                    Label statusLabel = new Label(status);
                    statusLabel.setStyle(status.equals("Conectado") ? "-fx-text-fill: #2ecc71; -fx-font-size: 14px;" : "-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
    
                    Label messageLabel = new Label(presenceMessage);
                    messageLabel.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 12px;");
    
                    userItem.getChildren().addAll(userLabel, statusLabel, messageLabel);
                    usuariosBox.getChildren().add(userItem);
                }
    
                // Crear un ScrollPane para el VBox en caso de que haya muchos usuarios
                ScrollPane scrollPane = new ScrollPane(usuariosBox);
                scrollPane.setFitToWidth(true);
                scrollPane.setStyle("-fx-background-color: #2c3e50;");
    
                Scene scene = new Scene(scrollPane, 400, 300);
                usuariosStage.setScene(scene);
                usuariosStage.show();
    
            } catch (Exception e) {
                System.out.println("Error al obtener la lista de usuarios conectados: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No has iniciado sesión.");
        }
    }
    

    public static void definirMensajePresencia() {
        if (connection != null && connection.isAuthenticated()) {
            try {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Definir mensaje de presencia");
                dialog.setHeaderText("Definir mensaje de presencia");
                dialog.setContentText("Ingrese su mensaje de presencia:");

                dialog.showAndWait().ifPresent(mensaje -> {
                    Presence presence = new Presence(Presence.Type.available);
                    presence.setStatus(mensaje);
                    try {
                        connection.sendStanza(presence);
                    } catch (SmackException.NotConnectedException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Mensaje de presencia actualizado.");
                });
            } catch (Exception e) {
                System.out.println("Error al definir el mensaje de presencia: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No has iniciado sesión.");
        }
    }

    private static void addNotification(String notification) {
        notifications.add(notification);
    }
    private Button createStyledButton(String text, String backgroundColor, String textColor, String borderColor) {
    Button button = new Button(text);
    button.setStyle(
        "-fx-background-color: " + backgroundColor + "; " +
        "-fx-text-fill: " + textColor + "; " +
        "-fx-border-color: " + borderColor + "; " +
        "-fx-border-width: 2px; " +
        "-fx-font-size: 14px; " +
        "-fx-padding: 10px 20px; " +
        "-fx-background-radius: 5px; " +
        "-fx-border-radius: 5px;"
    );
    button.setPrefWidth(200);
    button.setAlignment(Pos.CENTER_LEFT);
    return button;
}




private static void agregarSubscribeListener() {
    Roster roster = Roster.getInstanceFor(connection);
    roster.addSubscribeListener((from, subscribeRequest) -> {
        try {
            roster.createEntry(from.asEntityBareJidIfPossible(), from.asEntityBareJidIfPossible().toString(), null);
            Presence subscribe = new Presence(Presence.Type.subscribe);
            subscribe.setTo(from);
            connection.sendStanza(subscribe);
            return SubscribeListener.SubscribeAnswer.Approve;
        } catch (Exception e) {
            e.printStackTrace();
            return SubscribeListener.SubscribeAnswer.Deny;
        }
    });
}


    private void showNotifications(Stage primaryStage) {
        ListView<String> notificationListView = new ListView<>(notifications);
        VBox notificationBox = new VBox(10, new Label("Notificaciones"), notificationListView);
        notificationBox.setPadding(new Insets(10));
        notificationBox.setStyle("-fx-background-color: white;");

        Popup popup = new Popup();
        popup.getContent().add(notificationBox);
        popup.setAutoHide(true);
        popup.show(primaryStage);
    }
} 