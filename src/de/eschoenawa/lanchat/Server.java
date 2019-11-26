package de.eschoenawa.lanchat;

import de.eschoenawa.lanchat.config.LanChatConfig;
import de.eschoenawa.lanchat.plugin.PluginManager;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class Server implements Runnable {

    private UI parent;
    private PluginManager pluginManager;
    private boolean response;
    private DatagramSocket serverSocket;

    public Server(UI parent, PluginManager pluginManager) throws SocketException {
        super();
        this.parent = parent;
        this.pluginManager = pluginManager;
        this.response = true;
        serverSocket = new DatagramSocket(55545);
    }

    @Override
    public void run() {
        try {
            byte[] receiveData = new byte[1400];
            while (response) {
                DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(packet);
                InetAddress ip = packet.getAddress();
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: " + received + " from " + ip.toString());
                if (received.toLowerCase().startsWith(LanChatConfig.get("command_prefix")) && response) {
                    String value = received.split(":")[1];
                    parent.addValue(value, packet.getAddress().getHostAddress());
                    send(ip, LanChatConfig.get("response_prefix") + LanChatConfig.get("name") + " (v" + MiniUI.version + ")");
                } else if (received.toLowerCase().startsWith(LanChatConfig.get("response_prefix")) && response) {
                    String value = received.split(":")[1];
                    parent.addValue(value, packet.getAddress().getHostAddress());
                } else if (received.toLowerCase().startsWith(LanChatConfig.get("update_prefix"))) {
                    if (response)
                        parent.discover();
                } else {
                    received = pluginManager.processReceivedMessage(received);
                    if (received != null) {
                        parent.receive(received);
                    }
                }
            }
            serverSocket.close();
        } catch (Exception e) {
            ErrorHandler.fatalCrash(e);
        }
    }

    public void stopResponse() {
        this.response = false;
    }

    public boolean doesRespond() {
        return this.response;
    }

    public void sendDiscoveryMessage() {
        sendToBroadcast(LanChatConfig.get("command_prefix") + LanChatConfig.get("name") + " (v" + MiniUI.version + ")", true);
    }

    public void sendToBroadcast(String s, boolean isCommand) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback())
                    continue; // Don't want to broadcast to the loopback
                // interface
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null)
                        continue;
                    if (!isCommand) {
                        s = pluginManager.processSendMessage(s);
                    }
                    if (s != null) {
                        send(broadcast, s);
                    }
                }
            }
        } catch (Exception e) {
            ErrorHandler.reportError(e);
        }
    }

    private void send(InetAddress ip, String s) {
        int port = serverSocket.getLocalPort();
        DatagramPacket packet = new DatagramPacket(s.getBytes(), s.getBytes().length, ip, port);
        try {
            serverSocket.send(packet);
            System.out.println("Sent: " + s + " to " + ip.toString());
        } catch (IOException e) {
            ErrorHandler.reportError(e);
        }
    }
}
