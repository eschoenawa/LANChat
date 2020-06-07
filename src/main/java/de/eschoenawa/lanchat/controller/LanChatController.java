package de.eschoenawa.lanchat.controller;

import de.eschoenawa.lanchat.communication.LanChatProtocol;
import de.eschoenawa.lanchat.ui.TrayIcon;
import de.eschoenawa.lanchat.ui.UserInterface;

public interface LanChatController extends LanChatProtocol.LanChatProtocolCallback, TrayIcon.TrayIconCallback, UserInterface.UserInterfaceCallback {
    void launch();
}
