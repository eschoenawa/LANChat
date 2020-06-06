package de.eschoenawa.lanchat.server;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public abstract class ProtocolServerCallback extends ServerCallback {

    Map<String, ProtocolAction> registeredActions = new HashMap<>();

    public abstract String getSeparatorRegex();

    public void onNonProtocolMessageReceived(InetAddress sender, String message) {
        //noop by default
    }

    public boolean nonProtocolMessageShouldBypassReceiveTimeout(InetAddress sender, String message) {
        return false;
    }

    @Override
    public final void onMessageReceived(InetAddress sender, String message) {
        String[] splitMessage = splitMessage(message);
        if (splitMessage.length == 1) {
            onNonProtocolMessageReceived(sender, message);
            return;
        }
        ProtocolAction action = registeredActions.get(splitMessage[0]);
        if (action != null) {
            action.onCommandReceived(sender, splitMessage[1]);
        } else {
            onNonProtocolMessageReceived(sender, message);
        }
    }

    @Override
    public final boolean messageShouldBypassReceiveTimeout(InetAddress sender, String message) {
        String[] splitMessage = splitMessage(message);
        if (splitMessage.length == 1) {
            return nonProtocolMessageShouldBypassReceiveTimeout(sender, message);
        }
        ProtocolAction action = registeredActions.get(splitMessage[0]);
        if (action != null) {
            return action.shouldBypassReceiveTimeout(sender);
        }
        return nonProtocolMessageShouldBypassReceiveTimeout(sender, message);
    }

    public void addCommand(String command, ProtocolAction action) {
        registeredActions.put(command, action);
    }

    public void removeCommand(String command) {
        registeredActions.remove(command);
    }

    public void clearCommands() {
        registeredActions.clear();
    }

    private String[] splitMessage(String message) {
        return message.split(getSeparatorRegex(), 2);
    }

    public abstract static class ProtocolAction {
        public abstract void onCommandReceived(InetAddress sender, String input);

        public boolean shouldBypassReceiveTimeout(InetAddress sender) {
            return false;
        }
    }
}
