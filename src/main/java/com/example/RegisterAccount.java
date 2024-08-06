package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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

public class RegisterAccount extends Application {

    private static AbstractXMPPConnection connection;
    private TextArea logArea;
    private TextField userField;
    private PasswordField passField;
    private TextField recipientField;
    private TextArea messageField;
    private TextField contactField;
    private TextField statusField;
    private String domain = "alumchat.lol";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("XMPP Client");

        // Create UI elements
        userField = new TextField();
        passField = new PasswordField();
        recipientField = new TextField();
        messageField = new TextArea();
        contactField = new TextField();
        statusField = new TextField();
        logArea = new TextArea();
        logArea.setEditable(false);

        Button registerButton = new Button("Registrar nueva cuenta");
        registerButton.setOnAction(e -> registrarCuenta());

        Button loginButton = new Button("Iniciar sesión");
        loginButton.setOnAction(e -> iniciarSesion());

        Button sendButton = new Button("Enviar mensaje");
        sendButton.setOnAction(e -> enviarMensaje());

        Button addContactButton = new Button("Agregar contacto");
        addContactButton.setOnAction(e -> agregarContacto());

        Button viewContactsButton = new Button("Ver contactos");
        viewContactsButton.setOnAction(e -> verContactos());

        Button setPresenceButton = new Button("Definir presencia");
        setPresenceButton.setOnAction(e -> definirMensajePresencia());

        Button deleteAccountButton = new Button("Eliminar cuenta");
        deleteAccountButton.setOnAction(e -> eliminarCuenta());

        Button logoutButton = new Button("Cerrar sesión");
        logoutButton.setOnAction(e -> cerrarSesion());

        Button viewStatusButton = new Button("Mostrar estado");
        viewStatusButton.setOnAction(e -> mostrarUsuariosConectados());

        // Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Adding UI elements to layout
        grid.add(new Label("Usuario:"), 0, 0);
        grid.add(userField, 1, 0);
        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(passField, 1, 1);
        grid.add(registerButton, 2, 0);
        grid.add(loginButton, 2, 1);

        grid.add(new Label("Destinatario:"), 0, 2);
        grid.add(recipientField, 1, 2);
        grid.add(new Label("Mensaje:"), 0, 3);
        grid.add(messageField, 1, 3);
        grid.add(sendButton, 2, 3);

        grid.add(new Label("Contacto:"), 0, 4);
        grid.add(contactField, 1, 4);
        grid.add(addContactButton, 2, 4);

        grid.add(new Label("Estado:"), 0, 5);
        grid.add(statusField, 1, 5);
        grid.add(setPresenceButton, 2, 5);
        grid.add(viewContactsButton, 2, 6);
        grid.add(viewStatusButton, 2, 7);
        grid.add(deleteAccountButton, 2, 8);
        grid.add(logoutButton, 2, 9);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(grid, new Label("Log:"), logArea);

        Scene scene = new Scene(layout, 600, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void registrarCuenta() {
        String username = userField.getText();
        String password = passField.getText();

        try {
            Localpart localpart = Localpart.from(username);

            DNSUtil.setDNSResolver(MiniDnsResolver.getInstance());

            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(domain)
                    .setHost(domain)
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();

            connection = new XMPPTCPConnection(config);

            connection.connect();
            logArea.appendText("Conectado al servidor\n");

            AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.sensitiveOperationOverInsecureConnection(true);

            if (accountManager.supportsAccountCreation()) {
                accountManager.createAccount(localpart, password);
                logArea.appendText("Cuenta registrada exitosamente\n");
            } else {
                logArea.appendText("El servidor no soporta la creación de cuentas\n");
            }
            connection.disconnect();

        } catch (Exception e) {
            logArea.appendText("Error al registrar cuenta: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private void iniciarSesion() {
        String username = userField.getText();
        String password = passField.getText();

        try {
            DNSUtil.setDNSResolver(MiniDnsResolver.getInstance());

            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(domain)
                    .setHost(domain)
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();

            connection = new XMPPTCPConnection(config);

            connection.connect();
            connection.login(username, password);
            logArea.appendText("Sesión iniciada exitosamente\n");

            ChatManager chatManager = ChatManager.getInstanceFor(connection);
            chatManager.addIncomingListener((from, message, chat) -> {
                logArea.appendText("Mensaje recibido de " + from + ": " + message.getBody() + "\n");
            });

            agregarPresenceListener();
            agregarSubscribeListener();

        } catch (Exception e) {
            logArea.appendText("Error al iniciar sesión: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private void enviarMensaje() {
        if (connection != null && connection.isAuthenticated()) {
            try {
                String destinatario = recipientField.getText();
                String mensaje = messageField.getText();
                EntityBareJid jid = JidCreate.entityBareFrom(destinatario);
                ChatManager chatManager = ChatManager.getInstanceFor(connection);
                Chat chat = chatManager.chatWith(jid);

                Message message = new Message(jid, Message.Type.chat);
                message.setBody(mensaje);
                chat.send(message);

                logArea.appendText("Mensaje enviado a " + destinatario + "\n");

            } catch (Exception e) {
                logArea.appendText("Error al enviar mensaje: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        } else {
            logArea.appendText("Debe iniciar sesión antes de enviar un mensaje.\n");
        }
    }

    private void agregarContacto() {
        if (connection != null && connection.isAuthenticated()) {
            try {
                String contacto = contactField.getText();
                EntityBareJid jid = JidCreate.entityBareFrom(contacto);
                Roster roster = Roster.getInstanceFor(connection);
                roster.createEntry(jid, contacto, null);
                logArea.appendText("Contacto agregado exitosamente.\n");

                Presence subscribe = new Presence(Presence.Type.subscribe);
                subscribe.setTo(jid);
                connection.sendStanza(subscribe);

            } catch (Exception e) {
                logArea.appendText("Error al agregar contacto: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        } else {
            logArea.appendText("Debe iniciar sesión antes de agregar un contacto.\n");
        }
    }

    private void verContactos() {
        if (connection != null && connection.isAuthenticated()) {
            try {
                Roster roster = Roster.getInstanceFor(connection);
                Collection<RosterEntry> entries = roster.getEntries();
                logArea.appendText("Contactos:\n");
                for (RosterEntry entry : entries) {
                    Presence presence = roster.getPresence(entry.getJid());
                    String status = presence.isAvailable() ? "Conectado" : "Desconectado";
                    String presenceMessage = presence.getStatus() != null ? presence.getStatus() : "Sin mensaje de presencia";
                    logArea.appendText(entry.getJid() + " - " + status + " - Mensaje de presencia: " + presenceMessage + "\n");
                }
            } catch (Exception e) {
                logArea.appendText("Error al obtener la lista de contactos: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        } else {
            logArea.appendText("Debe iniciar sesión antes de ver los contactos.\n");
        }
    }

    private void mostrarUsuariosConectados() {
        if (connection != null && connection.isAuthenticated()) {
            try {
                Roster roster = Roster.getInstanceFor(connection);
                Collection<RosterEntry> entries = roster.getEntries();
                logArea.appendText("Usuarios:\n");
                for (RosterEntry entry : entries) {
                    Presence presence = roster.getPresence(entry.getJid());
                    String presenceMessage = presence.isAvailable() ? "Conectado" : "Desconectado";
                    String statusMessage = presence.getStatus() != null ? presence.getStatus() : "Sin mensaje de presencia";
                    logArea.appendText(entry.getJid() + " está " + presenceMessage + " - Mensaje de presencia: " + statusMessage + "\n");
                }

                Presence ownPresence = roster.getPresence(connection.getUser().asBareJid());
                String ownPresenceMessage = ownPresence.isAvailable() ? "Conectado" : "Desconectado";
                String ownStatusMessage = ownPresence.getStatus() != null ? ownPresence.getStatus() : "Sin mensaje de presencia";
                logArea.appendText(connection.getUser().asBareJid() + " está " + ownPresenceMessage + " - Mensaje de presencia: " + ownStatusMessage + "\n");

            } catch (Exception e) {
                logArea.appendText("Error al obtener la lista de usuarios conectados: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        } else {
            logArea.appendText("Debe iniciar sesión antes de mostrar todos los usuarios conectados y sus mensajes de presencia.\n");
        }
    }

    private void definirMensajePresencia() {
        if (connection != null && connection.isAuthenticated()) {
            try {
                String mensaje = statusField.getText();
                Presence presence = new Presence(Presence.Type.available);
                presence.setStatus(mensaje);
                connection.sendStanza(presence);
                logArea.appendText("Mensaje de presencia actualizado.\n");
            } catch (Exception e) {
                logArea.appendText("Error al definir el mensaje de presencia: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        } else {
            logArea.appendText("Debe iniciar sesión antes de definir el mensaje de presencia.\n");
        }
    }

    private void eliminarCuenta() {
        String username = userField.getText();
        String password = passField.getText();

        try {
            DNSUtil.setDNSResolver(MiniDnsResolver.getInstance());

            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(domain)
                    .setHost(domain)
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();

            connection = new XMPPTCPConnection(config);

            connection.connect();
            connection.login(username, password);
            logArea.appendText("Sesión iniciada exitosamente\n");

            AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.deleteAccount();
            logArea.appendText("Cuenta eliminada exitosamente.\n");
            connection.disconnect();

        } catch (Exception e) {
            logArea.appendText("Error al eliminar la cuenta: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private void cerrarSesion() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
            logArea.appendText("Sesión cerrada exitosamente.\n");
        } else {
            logArea.appendText("No hay ninguna sesión activa.\n");
        }
    }

    private void agregarPresenceListener() {
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
                logArea.appendText("Cambio en la presencia: " + presence.getFrom() + " - " + statusMessage + "\n");
            }
        });
    }

    private void agregarSubscribeListener() {
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
}
