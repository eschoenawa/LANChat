package de.eschoenawa.lanchat.server;

import java.net.InetAddress;

public abstract class ServerCallback {
    public abstract void onMessageReceived(InetAddress sender, String message);

    public abstract void onFatalError(Exception e);

    public void onError(Exception e) {
        e.printStackTrace();
    }

    public void onLogCommunication(String tag, String message) {
        System.out.println("[" + tag + "] " + message);
    }

    public void onLogStatus(String tag, String message) {
        System.out.println("[" + tag + "] " + message);
    }

    public boolean messageShouldBypassReceiveTimeout(InetAddress sender, String message) {
        return false;
    }
}
