package de.eschoenawa.lanchat.server;

import java.net.InetAddress;
import java.net.SocketException;

public interface Server extends Runnable {
    void sendToBroadcast(String message);

    void send(InetAddress ip, String message);

    void setCallback(ServerCallback callback);

    boolean isStarted();

    void stop();

    interface Builder {

        Builder setPort(int port);

        Builder setTimeout(int timeout);

        Builder setDoubleReceivePreventionTimeout(int receiveBlacklistTimeout);

        Builder setReceiveSentMessages(boolean receiveSentMessages);

        Builder setCallback(ServerCallback callback);

        Server build() throws SocketException;
    }
}
