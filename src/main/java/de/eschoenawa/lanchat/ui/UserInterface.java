package de.eschoenawa.lanchat.ui;

public interface UserInterface {
    void setCallback(UserInterfaceCallback callback);

    void clearUserList();

    void addDiscoveredUser(String user, String version, boolean isCurrent);

    void clearHistory();

    void receiveMessage(String sender, String message, boolean shouted);

    boolean isOpened();

    void open();

    void minimize();

    void setInfoText(String infoText);

    interface UserInterfaceCallback {
        void onSendText(String text);

        void onShoutText(String text);

        void onLaunchDiscovery();

        void onInfoTextClicked();
    }
}
