package de.eschoenawa.lanchat.helper;

import de.eschoenawa.lanchat.config.Config;
import de.eschoenawa.lanchat.config.ConfigImpl;
import de.eschoenawa.lanchat.controller.LanChatController;
import de.eschoenawa.lanchat.controller.LanChatControllerImpl;
import de.eschoenawa.lanchat.definition.LanChatSettingsDefinition;
import de.eschoenawa.lanchat.server.Server;
import de.eschoenawa.lanchat.server.ServerCallback;
import de.eschoenawa.lanchat.ui.TrayIcon;
import de.eschoenawa.lanchat.ui.TrayIconImpl;

import java.net.SocketException;

public class ServiceLocator {
    private static class InstanceHolder {
        static TrayIcon TRAY_ICON_INSTANCE = new TrayIconImpl();
        static Config CONFIG_INSTANCE = new ConfigImpl(new LanChatSettingsDefinition());
    }

    public static TrayIcon getTrayIcon() {
        return InstanceHolder.TRAY_ICON_INSTANCE;
    }

    public static LanChatController getLanChatController(Config config) {
        return new LanChatControllerImpl(config);
    }

    public static Server getServer(ServerCallback serverCallback) throws SocketException {
        //TODO
        return null;
    }

    public static Config getConfig() {
        return InstanceHolder.CONFIG_INSTANCE;
    }
}
