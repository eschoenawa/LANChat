package de.eschoenawa.lanchat.ui;

import java.util.List;

public interface UserInterface {
    void setCallback(UserInterfaceCallback callback);

    void setUserList(List<String> users);

    void clearUserList();

    void addDiscoveredUser(String user);

    void clearHistory();

    void receiveMessage(String sender, String message, boolean shouted);

    boolean isOpened();

    void open();

    void minimize();

    void setPluginButtons();     //TODO change once pluginSDK is in V2

    interface UserInterfaceCallback {
        void onSendText(String text);

        void onShoutText(String text);

        void onLaunchDiscovery();
    }
}
