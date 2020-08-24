package de.eschoenawa.lanchat.ui;

import de.eschoenawa.lanchat.config.Config;

import java.util.List;

public interface UserInterface {
    void setCallback(UserInterfaceCallback callback);

    void setUserList(List<String> users);   //TODO when do I need this? How do I know which one is current (no config in UI)

    void clearUserList();

    void addDiscoveredUser(String user, boolean isCurrent);

    void clearHistory();

    void receiveMessage(String sender, String message, boolean shouted);

    boolean isOpened();

    void open();

    void minimize();

    void openSettings(Config config);

    void setInfoText(String infoText);

    interface UserInterfaceCallback {
        void onSendText(String text);

        void onShoutText(String text);

        void onLaunchDiscovery();

        void onUpdateInfoClicked();
    }
}
