package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.*;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smack.util.dns.minidns.MiniDnsResolver;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;
public class RegisterAccount extends Application {

    private static AbstractXMPPConnection connection;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("XMPP Client");

        // Load the background image
        Image backgroundImage = new Image(getClass().getResourceAsStream("/robot.jpg"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(200); // Ajusta el ancho de la imagen según sea necesario
        backgroundImageView.setFitHeight(200); // Ajusta la altura de la imagen según sea necesario
        backgroundImageView.setPreserveRatio(true);

        // Create UI components
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: white;");

        Label label = new Label("Seleccione una opción:");
        Button registerButton = new Button("Registrar nueva cuenta");
        Button loginButton = new Button("Iniciar sesión con cuenta existente");
        Button sendMessageButton = new Button("Enviar mensaje a un usuario");
        Button logoutButton = new Button("Cerrar sesión");
        Button deleteAccountButton = new Button("Eliminar cuenta del servidor");
        Button exitButton = new Button("Salir");
        Button addContactButton = new Button("Agregar un contacto");
        Button viewContactsButton = new Button("Ver contactos");
        Button showUsersButton = new Button("Mostrar todos los usuarios conectados y sus mensajes de presencia");
        Button setPresenceButton = new Button("Definir mensaje de presencia");

        vbox.getChildren().addAll(label, registerButton, loginButton, sendMessageButton, logoutButton,
                deleteAccountButton, exitButton, addContactButton, viewContactsButton, showUsersButton, setPresenceButton);

        // Create HBox to contain VBox and ImageView
        HBox hbox = new HBox(20); // Espacio entre VBox e ImageView
        hbox.setPadding(new Insets(20));
        hbox.getChildren().addAll(vbox, backgroundImageView);

        StackPane root = new StackPane();
        root.getChildren().add(hbox);

        Scene scene = new Scene(root, 800, 600);

        // Add the stylesheet
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();

        // Add button actions
        registerButton.setOnAction(e -> registrarCuentaUI());
        loginButton.setOnAction(e -> iniciarSesionUI());
        sendMessageButton.setOnAction(e -> enviarMensajeUI());
        logoutButton.setOnAction(e -> cerrarSesion());
        deleteAccountButton.setOnAction(e -> eliminarCuentaUI());
        exitButton.setOnAction(e -> primaryStage.close());
        addContactButton.setOnAction(e -> agregarContactoUI());
        viewContactsButton.setOnAction(e -> verContactos());
        showUsersButton.setOnAction(e -> mostrarUsuariosConectados());
        setPresenceButton.setOnAction(e -> definirMensajePresencia());
    }


    private void registrarCuentaUI() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Registrar nueva cuenta");
        dialog.setHeaderText("Registrar nueva cuenta");
        dialog.setContentText("Ingrese el nombre de usuario:");

        dialog.showAndWait().ifPresent(username -> {
            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setTitle("Registrar nueva cuenta");
            passwordDialog.setHeaderText("Registrar nueva cuenta");
            passwordDialog.setContentText("Ingrese la contraseña:");

            passwordDialog.showAndWait().ifPresent(password -> registrarCuenta("alumchat.lol", username, password));
        });
    }

    private void iniciarSesionUI() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Iniciar sesión");
        dialog.setHeaderText("Iniciar sesión");
        dialog.setContentText("Ingrese el nombre de usuario:");

        dialog.showAndWait().ifPresent(username -> {
            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setTitle("Iniciar sesión");
            passwordDialog.setHeaderText("Iniciar sesión");
            passwordDialog.setContentText("Ingrese la contraseña:");

            passwordDialog.showAndWait().ifPresent(password -> iniciarSesion("alumchat.lol", username, password));
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

            messageDialog.showAndWait().ifPresent(mensaje -> enviarMensaje("alumchat.lol", "username", "password", destinatario, mensaje));
        });
    }

    private void eliminarCuentaUI() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Eliminar cuenta");
        dialog.setHeaderText("Eliminar cuenta");
        dialog.setContentText("Ingrese el nombre de usuario:");

        dialog.showAndWait().ifPresent(username -> {
            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setTitle("Eliminar cuenta");
            passwordDialog.setHeaderText("Eliminar cuenta");
            passwordDialog.setContentText("Ingrese la contraseña:");

            passwordDialog.showAndWait().ifPresent(password -> eliminarCuenta("alumchat.lol", username, password));
        });
    }

    private void agregarContactoUI() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar contacto");
        dialog.setHeaderText("Agregar contacto");
        dialog.setContentText("Ingrese el JID del contacto:");

        dialog.showAndWait().ifPresent(contacto -> agregarContacto(contacto + "@alumchat.lol"));
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
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();

            connection = new XMPPTCPConnection(config);

            try {
                connection.connect();  // Conectar al servidor
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

            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(domain)
                    .setHost(domain)
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();

            connection = new XMPPTCPConnection(config);

            try {
                connection.connect();  // Conectar al servidor
                connection.login(username, password);  // Iniciar sesión
                System.out.println("Sesión iniciada exitosamente");

                // Añadir el listener para mensajes entrantes
                ChatManager chatManager = ChatManager.getInstanceFor(connection);
                chatManager.addIncomingListener((from, message, chat) -> {
                    System.out.println("Mensaje recibido de " + from + ": " + message.getBody());
                });

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
                        String statusMessage = presence.getStatus() != null ? presence.getStatus() : "Sin mensaje de presencia";
                        System.out.println("Cambio en la presencia: " + presence.getFrom() + " - " + statusMessage);
                    }
                });

                return true;

            } catch (SmackException | IOException | XMPPException | InterruptedException e) {
                System.out.println("Error al iniciar sesión: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void enviarMensaje(String domain, String username, String password, String destinatario, String mensaje) {
        try {
            // Configurar el DNS resolver
            DNSUtil.setDNSResolver(MiniDnsResolver.getInstance());

            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(domain)
                    .setHost(domain)
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();

            connection = new XMPPTCPConnection(config);

            try {
                connection.connect();  // Conectar al servidor
                connection.login(username, password);  // Iniciar sesión
                System.out.println("Sesión iniciada exitosamente");

                ChatManager chatManager = ChatManager.getInstanceFor(connection);
                EntityBareJid jid = JidCreate.entityBareFrom(destinatario);
                Chat chat = chatManager.chatWith(jid);

                Message message = new Message(jid, Message.Type.chat);
                message.setBody(mensaje);
                chat.send(message);

                System.out.println("Mensaje enviado a " + destinatario);

                // Añadir el listener para mensajes entrantes
                chatManager.addIncomingListener((from, incomingMessage, chat1) -> {
                    System.out.println("Mensaje recibido de " + from + ": " + incomingMessage.getBody());
                });

                // Mantener la conexión abierta para recibir mensajes
                System.out.println("Presione Enter para cerrar la sesión...");
                new Scanner(System.in).nextLine();

            } catch (SmackException | IOException | XMPPException | InterruptedException e) {
                System.out.println("Error al enviar mensaje: " + e.getMessage());
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cerrarSesion() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
            System.out.println("Sesión cerrada exitosamente.");
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
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();

            connection = new XMPPTCPConnection(config);

            try {
                connection.connect();  // Conectar al servidor
                connection.login(username, password);  // Iniciar sesión
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

    public static void verContactos() {
        if (connection != null && connection.isAuthenticated()) {
            try {
                Roster roster = Roster.getInstanceFor(connection);
                Collection<RosterEntry> entries = roster.getEntries();
                System.out.println("Contactos:");
                for (RosterEntry entry : entries) {
                    Presence presence = roster.getPresence(entry.getJid());
                    String status = presence.isAvailable() ? "Conectado" : "Desconectado";
                    String presenceMessage = presence.getStatus() != null ? presence.getStatus() : "Sin mensaje de presencia";
                    System.out.println(entry.getJid() + " - " + status + " - Mensaje de presencia: " + presenceMessage);
                }
            } catch (Exception e) {
                System.out.println("Error al obtener la lista de contactos: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No has iniciado sesión.");
        }
    }

    public static void mostrarUsuariosConectados() {
        if (connection != null && connection.isAuthenticated()) {
            try {
                Roster roster = Roster.getInstanceFor(connection);
                Collection<RosterEntry> entries = roster.getEntries();
                System.out.println("Usuarios:");
                for (RosterEntry entry : entries) {
                    Presence presence = roster.getPresence(entry.getJid());
                    String presenceMessage = presence.isAvailable() ? "Conectado" : "Desconectado";
                    String statusMessage = presence.getStatus() != null ? presence.getStatus() : "Sin mensaje de presencia";
                    System.out.println(entry.getJid() + " está " + presenceMessage + " - Mensaje de presencia: " + statusMessage);
                }
                
                // Mostrar tu propio estado y mensaje de presencia
                Presence ownPresence = roster.getPresence(connection.getUser().asBareJid());
                String ownPresenceMessage = ownPresence.isAvailable() ? "Conectado" : "Desconectado";
                String ownStatusMessage = ownPresence.getStatus() != null ? ownPresence.getStatus() : "Sin mensaje de presencia";
                System.out.println(connection.getUser().asBareJid() + " está " + ownPresenceMessage + " - Mensaje de presencia: " + ownStatusMessage);

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
}
