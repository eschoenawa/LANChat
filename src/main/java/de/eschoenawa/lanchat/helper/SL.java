package de.eschoenawa.lanchat.helper;

import de.eschoenawa.lanchat.config.Config;
import de.eschoenawa.lanchat.config.ConfigImpl;
import de.eschoenawa.lanchat.controller.LanChatController;
import de.eschoenawa.lanchat.controller.LanChatControllerImpl;
import de.eschoenawa.lanchat.server.Server;
import de.eschoenawa.lanchat.server.ServerCallback;
import de.eschoenawa.lanchat.ui.TrayIcon;
import de.eschoenawa.lanchat.ui.TrayIconImpl;

import java.net.SocketException;

public class SL {
    public static TrayIcon getTrayIcon() {
        return new TrayIconImpl();
    }

    public static LanChatController getLanChatController(Config config) {
        return new LanChatControllerImpl(config);
    }

    public static Server getServer(ServerCallback serverCallback) throws SocketException {
        //TODO
        return null;
    }

    public static Config getConfig() {
        return new ConfigImpl();
    }
}
