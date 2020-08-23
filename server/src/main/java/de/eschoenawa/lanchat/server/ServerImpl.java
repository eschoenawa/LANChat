package de.eschoenawa.lanchat.server;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

class ServerImpl implements Server {
    private static final String TAG = "Server";

    private static final int DEFAULT_TIMEOUT = 1000;
    private static final int DEFAULT_RECEIVE_BLACKLIST_TIMEOUT = 1000;
    private static final boolean DEFAULT_RECEIVE_SENT_MESSAGES = true;

    private DatagramSocket serverSocket;
    private ServerCallback callback;
    private TimeoutStrikeBlacklist receiveBlacklist;

    private boolean started;

    ServerImpl(int port, int timeout, int receiveBlacklistTimeout, boolean receiveSentMessages) throws SocketException {
        this.serverSocket = new DatagramSocket(port);
        this.serverSocket.setSoTimeout(timeout);
        this.started = false;
        int maxStrikes = receiveSentMessages ? 0 : -1;
        this.receiveBlacklist = new TimeoutStrikeBlacklist(receiveBlacklistTimeout, maxStrikes);
    }

    ServerImpl(int port, int timeout, int receiveBlacklistTimeout, boolean receiveSentMessages, ServerCallback callback) throws SocketException {
        this.serverSocket = new DatagramSocket(port);
        this.serverSocket.setSoTimeout(timeout);
        this.started = false;
        int maxStrikes = receiveSentMessages ? 0 : -1;
        this.receiveBlacklist = new TimeoutStrikeBlacklist(receiveBlacklistTimeout, maxStrikes);
        this.callback = callback;
    }

    //region Receiving
    @Override
    public void run() {
        started = true;
        byte[] receiveData = new byte[1400];
        try {
            callback.onLogStatus(TAG, "Server is now listening.");
            while (started) {
                DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
                try {
                    serverSocket.receive(packet);
                    if (started) {
                        processPacket(packet);
                    }
                } catch (SocketTimeoutException ste) {
                    //noop
                }
            }
        } catch (Exception e) {
            callback.onFatalError(e);
        } finally {
            serverSocket.close();
            callback.onLogStatus(TAG, "Server socket has been closed.");
        }
    }

    private void processPacket(DatagramPacket received) {
        InetAddress ip = received.getAddress();
        String receivedText = new String(received.getData(), 0, received.getLength());
        callback.onLogCommunication(TAG + " <--", "Received '" + receivedText + "' from '" + ip.toString() + "'");
        if (receiveBlacklist.isInBlacklist(receivedText)) {
            if (callback.messageShouldBypassReceiveTimeout(ip, receivedText)) {
                callback.onLogCommunication(TAG, "Message '" + receivedText + "' from '" + ip.toString() + "' can bypass the receive timeout.");
                callback.onMessageReceived(ip, receivedText);
            } else {
                callback.onLogCommunication(TAG, "Suppressed message '" + receivedText + "' from '" + ip.toString() + "' since it was already received.");
            }
        } else {
            callback.onMessageReceived(ip, receivedText);
        }
    }
    //endregion

    //region Sending
    @Override
    public void sendToBroadcast(String message) {
        if (message == null) {
            throw new IllegalArgumentException("The message cannot be null!");
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback()) {
                    continue; // Don't broadcast to loopback
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }
                    send(broadcast, message);
                }
            }
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    @Override
    public void send(InetAddress ip, String message) {
        receiveBlacklist.addToList(message);
        int port = serverSocket.getLocalPort();
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, ip, port);
        try {
            serverSocket.send(packet);
            callback.onLogCommunication(TAG + " -->", "Sent '" + message + "' to '" + ip.toString() + "'");
        } catch (IOException e) {
            callback.onError(e);
        }
    }
    //endregion


    @Override
    public void setCallback(ServerCallback callback) {
        this.callback = callback;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void stop() {
        started = false;
        callback.onLogStatus(TAG, "Stop has been requested.");
    }

    static class BuilderImpl implements Server.Builder {
        private int port = -1;
        private int timeout = DEFAULT_TIMEOUT;
        private int receiveBlacklistTimeout = DEFAULT_RECEIVE_BLACKLIST_TIMEOUT;
        private boolean receiveSentMessages = DEFAULT_RECEIVE_SENT_MESSAGES;
        private ServerCallback callback = null;

        BuilderImpl(){}

        @Override
        public Builder setPort(int port) {
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("Port '" + port + "' is not a valid port!");
            }
            this.port = port;
            return this;
        }

        @Override
        public Builder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        @Override
        public Builder setDoubleReceivePreventionTimeout(int receiveBlacklistTimeout) {
            this.receiveBlacklistTimeout = receiveBlacklistTimeout;
            return this;
        }

        @Override
        public Builder setReceiveSentMessages(boolean receiveSentMessages) {
            this.receiveSentMessages = receiveSentMessages;
            return this;
        }

        @Override
        public Builder setCallback(ServerCallback callback) {
            this.callback = callback;
            return this;
        }

        @Override
        public Server build() throws SocketException {
            if (this.port == -1) {
                throw new IllegalStateException("Port wasn't set!");
            }
            if (this.callback == null) {
                return new ServerImpl(port, timeout, receiveBlacklistTimeout, receiveSentMessages);
            } else {
                return new ServerImpl(port, timeout, receiveBlacklistTimeout, receiveSentMessages, callback);
            }
        }
    }
}
