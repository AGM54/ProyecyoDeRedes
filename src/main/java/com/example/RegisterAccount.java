package com.example;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smack.util.dns.minidns.MiniDnsResolver;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jivesoftware.smack.packet.Presence;

import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

public class RegisterAccount {

    private static AbstractXMPPConnection connection;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String domain = "alumchat.lol";
        boolean running = true;

        while (running) {
            System.out.println("Seleccione una opción:");
            System.out.println("1. Registrar nueva cuenta");
            System.out.println("2. Iniciar sesión con cuenta existente");
            System.out.println("3. Enviar mensaje a echobot@alumchat.lol");
            System.out.println("4. Cerrar sesión");
            System.out.println("5. Eliminar cuenta del servidor");
            System.out.println("6. Salir");
            System.out.println("7. Agregar un contacto");
            System.out.println("8. Ver contactos");
            System.out.println("9. Mostrar todos los usuarios conectados y sus mensajes de presencia");
            System.out.println("10. Definir mensaje de presencia");
            int opcion = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            String username, password, mensaje;

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
                    mensaje = scanner.nextLine();
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
                case 6:
                    running = false;
                    System.out.println("Saliendo...");
                    break;
                case 7:
                    if (connection != null && connection.isAuthenticated()) {
                        agregarContacto("gla21299@alumchat.lol");
                    } else {
                        System.out.println("Debe iniciar sesión antes de agregar un contacto.");
                        System.out.println("Ingrese el nombre de usuario:");
                        username = scanner.nextLine();
                        System.out.println("Ingrese la contraseña:");
                        password = scanner.nextLine();
                        if (iniciarSesion(domain, username, password)) {
                            agregarContacto("gla21299@alumchat.lol");
                        }
                    }
                    break;
                case 8:
                    if (connection != null && connection.isAuthenticated()) {
                        verContactos();
                    } else {
                        System.out.println("Debe iniciar sesión antes de ver los contactos.");
                        System.out.println("Ingrese el nombre de usuario:");
                        username = scanner.nextLine();
                        System.out.println("Ingrese la contraseña:");
                        password = scanner.nextLine();
                        if (iniciarSesion(domain, username, password)) {
                            verContactos();
                        }
                    }
                    break;
                case 9:
                    if (connection != null && connection.isAuthenticated()) {
                        mostrarUsuariosConectados();
                    } else {
                        System.out.println("Debe iniciar sesión antes de mostrar todos los usuarios conectados y sus mensajes de presencia.");
                        System.out.println("Ingrese el nombre de usuario:");
                        username = scanner.nextLine();
                        System.out.println("Ingrese la contraseña:");
                        password = scanner.nextLine();
                        if (iniciarSesion(domain, username, password)) {
                            mostrarUsuariosConectados();
                        }
                    }
                    break;
                case 10:
                    definirMensajePresencia();
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
        scanner.close();
        cerrarSesion(); // Cerrar sesión al salir del bucle
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
                System.out.println("Usuarios conectados:");
                for (RosterEntry entry : entries) {
                    Presence presence = roster.getPresence(entry.getJid());
                    String presenceMessage = presence.isAvailable() ? "Conectado" : "Desconectado";
                    System.out.println(entry.getJid() + " está " + presenceMessage);
                    String statusMessage = presence.getStatus() != null ? presence.getStatus() : "Sin mensaje de presencia";
                    System.out.println("Mensaje de presencia: " + statusMessage);
                }
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
                Scanner scanner = new Scanner(System.in);
                System.out.println("Ingrese su mensaje de presencia:");
                String mensaje = scanner.nextLine();
                Presence presence = new Presence(Presence.Type.available);
                presence.setStatus(mensaje);
                connection.sendStanza(presence);
                System.out.println("Mensaje de presencia actualizado.");
            } catch (Exception e) {
                System.out.println("Error al definir el mensaje de presencia: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No has iniciado sesión.");
        }
    }
}
