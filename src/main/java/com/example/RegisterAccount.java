package com.example;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smack.util.dns.minidns.MiniDnsResolver;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.Scanner;

public class RegisterAccount {

    private static AbstractXMPPConnection connection;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Seleccione una opción:");
        System.out.println("1. Registrar nueva cuenta");
        System.out.println("2. Iniciar sesión con cuenta existente");
        System.out.println("3. Enviar mensaje a echobot@alumchat.lol");
        System.out.println("4. Cerrar sesión");
        System.out.println("5. Eliminar cuenta del servidor");
        int opcion = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        String domain = "alumchat.lol";
        String username, password;

        switch (opcion) {
            case 1:
                System.out.println("Ingrese el nombre de usuario para registrar:");
                username = scanner.nextLine();
                System.out.println("Ingrese la contraseña:");
                password = scanner.nextLine();
                registrarCuenta(domain, username, password);
                break;
            case 2:
                System.out.println("Ingrese el nombre de usuario:");
                username = scanner.nextLine();
                System.out.println("Ingrese la contraseña:");
                password = scanner.nextLine();
                iniciarSesion(domain, username, password);
                break;
            case 3:
                System.out.println("Ingrese el nombre de usuario:");
                username = scanner.nextLine();
                System.out.println("Ingrese la contraseña:");
                password = scanner.nextLine();
                System.out.println("Ingrese el mensaje:");
                String mensaje = scanner.nextLine();
                enviarMensaje(domain, username, password, "echobot@alumchat.lol", mensaje);
                break;
            case 4:
                cerrarSesion();
                break;
            case 5:
                System.out.println("Ingrese el nombre de usuario:");
                username = scanner.nextLine();
                System.out.println("Ingrese la contraseña:");
                password = scanner.nextLine();
                eliminarCuenta(domain, username, password);
                break;
            default:
                System.out.println("Opción no válida.");
        }

        scanner.close();
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

    public static void iniciarSesion(String domain, String username, String password) {
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

                // Mantener la conexión abierta para recibir mensajes
                System.out.println("Presione Enter para cerrar la sesión...");
                new Scanner(System.in).nextLine();

            } catch (SmackException | IOException | XMPPException | InterruptedException e) {
                System.out.println("Error al iniciar sesión: " + e.getMessage());
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
}
