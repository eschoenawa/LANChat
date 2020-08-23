package de.eschoenawa.lanchat.controller;

import de.eschoenawa.lanchat.communication.LanChatProtocol;
import de.eschoenawa.lanchat.config.Config;
import de.eschoenawa.lanchat.definition.LanChatSettingsDefinition;
import de.eschoenawa.lanchat.server.Server;
import de.eschoenawa.lanchat.server.ServerBuilderFactory;
import de.eschoenawa.lanchat.ui.tray.TrayIcon;
import de.eschoenawa.lanchat.ui.tray.TrayIconImpl;
import de.eschoenawa.lanchat.ui.UserInterface;
import de.eschoenawa.lanchat.util.Log;

import java.net.SocketException;

public class LanChatController implements TrayIcon.TrayIconCallback, UserInterface.UserInterfaceCallback, LanChatProtocol.LanChatProtocolCallback {

    private static final String TAG = "LC Controller";

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
    //TODO launch minimized

    public LanChatController(Config config) {
        this(config, ServerBuilderFactory.getServerBuilder(), new TrayIconImpl());
    }

    public LanChatController(Config config, Server.Builder serverBuilder, TrayIcon trayIcon) {
        this.config = config;
        this.trayIcon = trayIcon;
        this.userInterface = null; //TODO implement UI
        int port = this.config.requireInt(LanChatSettingsDefinition.SettingKeys.PORT);
        if (port < 1 || port > 65535) {
            throw new IllegalStateException("LANChat port from config (" + port + ") is not a valid port!");
        } else if (port < 1024) {
            Log.w(TAG, "LANChat port from config (" + port + ") is a port reserved for system services! This may cause significant compatibility issues.");
        }
        LanChatProtocol protocol = new LanChatProtocol.Builder()
                .setCallback(this)
                .setDiscoveryCommand(config.requireString(LanChatSettingsDefinition.SettingKeys.DISCOVERY_PREFIX))
                .setDiscoveryResponseCommand(config.requireString(LanChatSettingsDefinition.SettingKeys.DISCOVERY_RESPONSE_PREFIX))
                .setMessageCommand(config.requireString(LanChatSettingsDefinition.SettingKeys.MESSAGE_PREFIX))
                .setShoutCommand(config.requireString(LanChatSettingsDefinition.SettingKeys.SHOUT_PREFIX))
                //TODO Plugins
                .build();
        try {
            this.server = serverBuilder
                    .setPort(port)
                    .setCallback(protocol)
                    .build();
        } catch (SocketException e) {
            throw new IllegalStateException("Unable to launch the LANChat server!", e);
        }
    }

    public void launch() {
        this.trayIcon.setDisplayedIcon(TrayIcon.IconType.BUSY);
        this.trayIcon.setTooltip("Launching...");
        this.trayIcon.setCallback(this);
        this.trayIcon.addToSystemTray();

        this.userInterface.setCallback(this);
        //TODO launch UI
        //TODO launch server
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
