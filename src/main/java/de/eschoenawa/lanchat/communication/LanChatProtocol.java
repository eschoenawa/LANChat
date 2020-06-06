package de.eschoenawa.lanchat.communication;

import de.eschoenawa.lanchat.server.ProtocolServerCallback;
import de.eschoenawa.lanchat.util.ErrorHandler;
import de.eschoenawa.lanchat.util.Log;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class LanChatProtocol extends ProtocolServerCallback {
    private static String TAG = "LC-COMM";

    private LanChatProtocolCallback callback;

    private LanChatProtocol(LanChatProtocolCallback callback, String discoveryCommand, String discoveryResponse, String messageCommand, String shoutCommand, Map<String, ProtocolAction> pluginCommands) {
        this.callback = callback;
        setupCommands(discoveryCommand, discoveryResponse, messageCommand, shoutCommand, pluginCommands);
    }

    private void setupCommands(String discoveryCommand, String discoveryResponse, String messageCommand, String shoutCommand, Map<String, ProtocolAction> pluginCommands) {
        for (String command : pluginCommands.keySet()) {
            addCommand(command, pluginCommands.get(command));
        }
        addCommand(discoveryCommand, new ProtocolAction() {
            @Override
            public void onCommandReceived(InetAddress sender, String input) {
                    callback.onDiscoveryCommandReceived(input);
            }

            @Override
            public boolean shouldBypassReceiveTimeout(InetAddress sender) {
                return true;
            }
        });
        addCommand(discoveryResponse, new ProtocolAction() {
            @Override
            public void onCommandReceived(InetAddress sender, String input) {
                callback.onDiscoveryResponseReceived(input);
            }

            @Override
            public boolean shouldBypassReceiveTimeout(InetAddress sender) {
                return true;
            }
        });
        addCommand(messageCommand, new ProtocolAction() {
            @Override
            public void onCommandReceived(InetAddress sender, String input) {
                processReceivedMessage(input, false);
            }
        });
        addCommand(shoutCommand, new ProtocolAction() {
            @Override
            public void onCommandReceived(InetAddress sender, String input) {
                processReceivedMessage(input, true);
            }
        });
    }

    public void setCallback(LanChatProtocolCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onLogCommunication(String tag, String message) {
        Log.d(tag, message);
    }

    @Override
    public void onLogStatus(String tag, String message) {
        Log.d(tag, message);
    }

    @Override
    public void onError(Exception e) {
        Log.e(TAG, "A server error occurred", e);
        ErrorHandler.reportError(e);
    }

    @Override
    public void onFatalError(Exception e) {
        ErrorHandler.fatalCrash(e);
    }

    @Override
    public String getSeparatorRegex() {
        return ":";
    }

    @Override
    public void onNonProtocolMessageReceived(InetAddress sender, String message) {
        Log.d(TAG, "Received non protocol message '" + message + "' from '" + sender.toString() + "', ignoring...");
    }

    private void processReceivedMessage(String messageWithSender, boolean shouted) {
        if (messageWithSender == null || !messageWithSender.contains(":")) {
            Log.w(TAG, "Received invalid message: " + messageWithSender);
            return;
        }
        String[] split = messageWithSender.split(":");
        StringBuilder messageBuilder = new StringBuilder();
        for (int j = 1; j < split.length; j++) {
            messageBuilder.append(split[j]);
            if (j + 1 < split.length) {
                messageBuilder.append(":");
            }
        }
        String message = messageBuilder.toString();
        callback.onMessageReceived(split[0], message, shouted);
    }

    public interface LanChatProtocolCallback {
        void onDiscoveryCommandReceived(String name);

        void onDiscoveryResponseReceived(String name);

        void onMessageReceived(String sender, String message, boolean shouted);
    }

    public static class Builder {
        private LanChatProtocolCallback callback;
        private String discoveryCommand = "cmd";
        private String discoveryResponseCommand = "hello";
        private String messageCommand = "msg";
        private String shoutCommand = "shout";
        private Map<String, ProtocolAction> pluginCommands = new HashMap<>();

        public Builder() {
            this.callback = null;
        }

        public Builder setCallback(LanChatProtocolCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder setDiscoveryCommand(String discoveryCommand) {
            this.discoveryCommand = discoveryCommand;
            return this;
        }

        public Builder setDiscoveryResponseCommand(String discoveryResponseCommand) {
            this.discoveryResponseCommand = discoveryResponseCommand;
            return this;
        }

        public Builder setMessageCommand(String messageCommand) {
            this.messageCommand = messageCommand;
            return this;
        }

        public Builder setShoutCommand(String shoutCommand) {
            this.shoutCommand = shoutCommand;
            return this;
        }

        public Builder setPluginCommands(Map<String, ProtocolAction> pluginCommands) {
            this.pluginCommands = pluginCommands;
            return this;
        }

        public Builder addPluginCommand(String command, ProtocolAction protocolAction) {
            this.pluginCommands.put(command, protocolAction);
            return this;
        }

        public Builder addPluginCommands(Map<String, ProtocolAction> pluginCommands) {
            this.pluginCommands.putAll(pluginCommands);
            return this;
        }

        public LanChatProtocol build() {
            return new LanChatProtocol(callback, discoveryCommand, discoveryResponseCommand, messageCommand, shoutCommand, pluginCommands);
        }
    }
}
