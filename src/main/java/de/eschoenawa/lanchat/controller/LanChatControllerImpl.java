package de.eschoenawa.lanchat.controller;

import de.eschoenawa.lanchat.communication.LanChatProtocol;
import de.eschoenawa.lanchat.config.Config;
import de.eschoenawa.lanchat.server.Server;
import de.eschoenawa.lanchat.ui.TrayIcon;
import de.eschoenawa.lanchat.ui.UserInterface;

public class LanChatControllerImpl implements LanChatController {

    private Config config;
    private TrayIcon trayIcon;
    private UserInterface userInterface;
    private Server server;

    //TODO do processing of incoming messages (write to file with Chat class and run through plugins(?)), Update UI and handle UI events
    //TODO add TrayIcon
    //TODO add NotificationManager and callbacks for that
    //TODO create settings view (add sorting order number to settings class for this (so order is consistent in settings view))
    //TODO launch update check
    //TODO delete update jar
    //TODO hide trayicon as fast as possible when autoupdate on launch

    public LanChatControllerImpl(Config config, TrayIcon trayIcon, UserInterface userInterface, Server server) {
        this.config = config;
        this.trayIcon = trayIcon;
        this.userInterface = userInterface;
        this.server = server;
    }

    @Override
    public void launch() {
        this.trayIcon.setDisplayedIcon(TrayIcon.IconType.BUSY);
        this.trayIcon.setTooltip("Launching...");
        this.trayIcon.setCallback(this);
        this.trayIcon.addToSystemTray();

        this.userInterface.setCallback(this);
        //TODO launch UI

        LanChatProtocol protocol = new LanChatProtocol.Builder()
                .setCallback(this)
                .build();
        this.server.setCallback(protocol);
    }

    @Override
    public void onSendText(String text) {

    }

    @Override
    public void onShoutText(String text) {

    }

    @Override
    public void onLaunchDiscovery() {

    }

    @Override
    public void onOpenUi() {

    }

    @Override
    public void onHideShowNotifications() {

    }

    @Override
    public void onDisableEnablePlugins() {

    }

    @Override
    public void onOpenSettings() {

    }

    @Override
    public void onArchive() {

    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onOpenAbout() {

    }

    @Override
    public void onExit() {

    }

    @Override
    public void onDiscoveryCommandReceived(String name) {

    }

    @Override
    public void onDiscoveryResponseReceived(String name) {

    }

    @Override
    public void onMessageReceived(String sender, String message, boolean shouted) {

    }
}
