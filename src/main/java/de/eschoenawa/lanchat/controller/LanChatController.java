package de.eschoenawa.lanchat.controller;

import de.eschoenawa.lanchat.communication.LanChatProtocol;
import de.eschoenawa.lanchat.config.Config;
import de.eschoenawa.lanchat.definition.LanChatSettingsDefinition;
import de.eschoenawa.lanchat.definition.Version;
import de.eschoenawa.lanchat.helper.ServiceLocator;
import de.eschoenawa.lanchat.launcher.Launcher;
import de.eschoenawa.lanchat.persistance.Chat;
import de.eschoenawa.lanchat.server.Server;
import de.eschoenawa.lanchat.server.ServerBuilderFactory;
import de.eschoenawa.lanchat.ui.UserInterface;
import de.eschoenawa.lanchat.ui.settings.SimpleSettingsUi;
import de.eschoenawa.lanchat.ui.tray.TrayIcon;
import de.eschoenawa.lanchat.ui.tray.TrayIconImpl;
import de.eschoenawa.lanchat.util.Blacklist;
import de.eschoenawa.lanchat.util.Downloader;
import de.eschoenawa.lanchat.util.ErrorHandler;
import de.eschoenawa.lanchat.util.Log;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;

public class LanChatController implements TrayIcon.TrayIconCallback, UserInterface.UserInterfaceCallback, LanChatProtocol.LanChatProtocolCallback {

    private static final String TAG = "LC Controller";

    private final Config config;
    private final TrayIcon trayIcon;
    private final UserInterface userInterface;
    private final LanChatProtocol protocol;
    private final Server server;
    private final Blacklist receiveBlacklist = new Blacklist();

    private final String updateProviderUrl;
    private final String discoveryCommand;
    private final String messageCommand;
    private final String shoutCommand;

    public LanChatController(Config config, String updateProviderUrl) {
        this(config, ServerBuilderFactory.getServerBuilder(), new TrayIconImpl(), ServiceLocator.getNewUserInterfaceInstance(), updateProviderUrl);
    }

    public LanChatController(Config config, Server.Builder serverBuilder, TrayIcon trayIcon, UserInterface userInterface, String updateProviderUrl) {
        this.config = config;
        this.trayIcon = trayIcon;
        this.userInterface = userInterface;
        int port = this.config.requireInt(LanChatSettingsDefinition.SettingKeys.PORT);
        if (port < 1 || port > 65535) {
            throw new IllegalStateException("LANChat port from config (" + port + ") is not a valid port!");
        } else if (port < 1024) {
            Log.w(TAG, "LANChat port from config (" + port + ") is a port reserved for system services! This may cause significant compatibility issues.");
        }
        this.updateProviderUrl = updateProviderUrl;
        this.discoveryCommand = config.requireString(LanChatSettingsDefinition.SettingKeys.DISCOVERY_PREFIX);
        String discoveryResponseCommand = config.requireString(LanChatSettingsDefinition.SettingKeys.DISCOVERY_RESPONSE_PREFIX);
        this.messageCommand = config.requireString(LanChatSettingsDefinition.SettingKeys.MESSAGE_PREFIX);
        this.shoutCommand = config.requireString(LanChatSettingsDefinition.SettingKeys.SHOUT_PREFIX);
        this.protocol = new LanChatProtocol.Builder()
                .setCallback(this)
                .setDiscoveryCommand(this.discoveryCommand)
                .setDiscoveryResponseCommand(discoveryResponseCommand)
                .setMessageCommand(this.messageCommand)
                .setShoutCommand(this.shoutCommand)
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
        // Class init
        Chat.init(config.requireString(LanChatSettingsDefinition.SettingKeys.HISTORY_PATH));

        // TrayIcon init
        this.trayIcon.setDisplayedIcon(TrayIcon.IconType.BUSY);
        this.trayIcon.setTooltip("Launching...");
        this.trayIcon.setCallback(this);
        this.trayIcon.addToSystemTray();

        // UI init
        this.userInterface.setCallback(this);

        // Server init
        Thread serverThread = new Thread(this.server);
        serverThread.start();

        // Load history
        Chat.load(this.userInterface::receiveMessage);

        // Finalize launch
        this.trayIcon.setDisplayedIcon(TrayIcon.IconType.NORMAL);
        this.trayIcon.setTooltip("LANChat");
        if (!config.requireBoolean(LanChatSettingsDefinition.SettingKeys.MINIMIZED)) {
            this.userInterface.open();
        }
        onLaunchDiscovery();
    }

    @Override
    public void onSendText(String text) {
        //TODO plugin processing
        this.receiveBlacklist.addToList(text);
        this.server.sendToBroadcast(this.messageCommand
                + this.protocol.getSeparatorRegex()
                + config.requireString(LanChatSettingsDefinition.SettingKeys.NAME)
                + this.protocol.getSeparatorRegex() + text);
    }

    @Override
    public void onShoutText(String text) {
        //TODO plugin processing
        this.receiveBlacklist.addToList(text);
        this.server.sendToBroadcast(this.shoutCommand
                + this.protocol.getSeparatorRegex()
                + config.requireString(LanChatSettingsDefinition.SettingKeys.NAME)
                + this.protocol.getSeparatorRegex()
                + text);
    }

    @Override
    public void onLaunchDiscovery() {
        //TODO plugin processing?
        String message = this.discoveryCommand
                + this.protocol.getSeparatorRegex()
                + config.requireString(LanChatSettingsDefinition.SettingKeys.NAME)
                + this.protocol.getSeparatorRegex()
                + Version.VERSION_STRING
                + this.protocol.getSeparatorRegex()
                + Version.VERSION_ID;
        if (this.updateProviderUrl != null) {
            message = message
                    + this.protocol.getSeparatorRegex()
                    + this.updateProviderUrl;
        }
        this.server.sendToBroadcast(message);
    }

    @Override
    public void onInfoTextClicked() {
        /*noop, will be useful in the future*/
    }

    @Override
    public void onOpenUi(boolean toggle) {
        if (toggle && this.userInterface.isOpened()) {
            this.userInterface.minimize();
        } else {
            this.userInterface.open();
            this.trayIcon.setDisplayedIcon(TrayIcon.IconType.NORMAL);
        }
    }

    @Override
    public void onDisableEnablePlugins() {
        //TODO (plugins will be added back at a later date)
    }

    @Override
    public void onOpenSettings() {
        SimpleSettingsUi frame = new SimpleSettingsUi(this.config);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    public void onArchive() {
        //TODO archiving function
        JOptionPane.showMessageDialog(null, "Archiving not supported yet");
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onOpenAbout() {
        JOptionPane.showMessageDialog(null,
                "Version: " + Version.VERSION_STRING + "\nLANChat by Emil Schoenawa (@eschoenawa)\n2016-2021",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void onExit() {
        this.server.stop();
        this.trayIcon.removeFromSystemTray();
        Log.i(TAG, "LANChat was terminated.");
        System.exit(0);
    }

    @Override
    public void onDiscoveryCommandReceived(String message) {
        //TODO plugin processing?
        newDiscoveredUser(message);
    }

    @Override
    public void onDiscoveryResponseReceived(String message) {
        //TODO plugin processing?
        newDiscoveredUser(message);
    }

    @Override
    public void onMessageReceived(String sender, String message, boolean shouted) {
        if (!this.receiveBlacklist.isInBlacklist(message)) {
            if (!this.userInterface.isOpened()) {
                this.trayIcon.setDisplayedIcon(TrayIcon.IconType.UNREAD);
                this.trayIcon.showNotification(sender, message, shouted);
            }
            Chat.println(sender, message, shouted);
            this.userInterface.receiveMessage(sender, message, shouted);
        } else {
            Log.d(TAG, "Skipped receiving '" + message + "' because we already received it within the last second.");
        }
    }

    private void newDiscoveredUser(String message) {
        String currentName = config.requireString(LanChatSettingsDefinition.SettingKeys.NAME);
        String[] components = message.split(this.protocol.getSeparatorRegex());
        if (components.length < 3) {
            Log.w(TAG, "Invalid discovery message received ('" + message + "')! Ignoring...");
            return;
        }
        String name = components[0];
        String version = components[1];
        int versionId = Integer.parseInt(components[2]);
        if (versionId > Version.VERSION_ID && components.length >= 4 && config.requireBoolean(LanChatSettingsDefinition.SettingKeys.AUTO_UPDATE)) {
            StringBuilder urlBuilder = new StringBuilder();
            for (int i = 3; i < components.length; i++) {
                urlBuilder.append(components[i]);
                if (i + 1 < components.length) {
                    urlBuilder.append(":");
                }
            }
            updateFlow(urlBuilder.toString());
        }
        userInterface.addDiscoveredUser(name, version, name.equals(currentName));
    }

    private void updateFlow(String updateUrl) {
        Log.d(TAG, "Received new version from another user on the network!");
        File noUpdate = new File(Launcher.NO_UPDATE_FILE_LOCATION);
        if (noUpdate.exists()) {
            Log.d(TAG, "No-update-policy defined by environment. Ignoring update.");
            return;
        }
        Log.d(TAG, "Downloading update from '" + updateUrl + "'...");
        if (!Downloader.download(updateUrl, Launcher.NEW_JAR_LOCATION)) {
            ErrorHandler.reportError(new Exception("Unable to download update! Check log for details."), true, "Does LANChat have write access?");
            return;
        }
        Log.d(TAG, "Download done. Creating file to instruct next LANChat instance to overwrite the old version with the new version...");
        File updateCopyStepFile = new File(Launcher.COPY_STEP_FILE_LOCATION);
        try {
            if (!updateCopyStepFile.createNewFile()) {
                ErrorHandler.reportError(new Exception("Unable to create overwrite instruction file!"), true, "Does LANChat have write access?");
                return;
            }
        } catch (IOException e) {
            ErrorHandler.reportError(e, true, "Unable to create overwrite instruction file!");
            return;
        }
        Log.d(TAG, "Overwrite instruction file created.");
        Log.d(TAG, "Applying update...");
        Log.d(TAG, "Shutting down server...");
        this.server.stop();
        this.trayIcon.removeFromSystemTray();
        Log.d(TAG, "Shutdown complete.");
        Log.d(TAG, "Executing downloaded jar and exiting JVM...");
        try {
            Runtime.getRuntime().exec("java -jar " + Launcher.NEW_JAR_LOCATION);
            System.exit(0);
        } catch (IOException e) {
            Log.e(TAG, "Failed to launch '" + Launcher.NEW_JAR_LOCATION + "'. Skipping update.");
            ErrorHandler.reportError(e, true, "Can't launch downloaded jar!");
        }
    }
}
