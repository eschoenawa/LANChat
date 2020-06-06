package de.eschoenawa.lanchat.server;

import java.net.InetAddress;

public interface Server extends Runnable {
    void sendToBroadcast(String message);
    void send(InetAddress ip, String message);
    boolean isStarted();
    void stop();
}
