package de.eschoenawa.lanchat.ui;

import de.eschoenawa.lanchat.helper.Texts;
import de.eschoenawa.lanchat.util.Log;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TrayIconImpl implements TrayIcon {

    private static final String TAG = "TrayIcon";

    private TrayIconCallback callback;
    private java.awt.TrayIcon trayIcon;
    private MenuItem notificationItem;
    private MenuItem pluginItem;

    private Image normalImage = Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/computer.gif"));
    private Image unreadImage = Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/warning.png"));
    private Image busyImage = Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/harddrive.gif"));

    public TrayIconImpl() {

        trayIcon = new java.awt.TrayIcon(normalImage, Texts.TrayIcon.TOOLTIP, createPopupMenu());
        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //TODO remove the following
                Log.d(TAG, "Click on tray! isPrimaryButton: " + (e.getButton() == MouseEvent.BUTTON1) + "; clicks: " + e.getClickCount());
                //TODO do I need e.getClickCount() == 1 here?
                if (e.getButton() == MouseEvent.BUTTON1 && callback != null) {
                    callback.onOpenUi();
                    //TODO I removed the return to the normal image here, so when messages are read manually set icon.
                }
            }
        });
    }

    private PopupMenu createPopupMenu() {
        PopupMenu popup = new PopupMenu();
        MenuItem currentMenuItem = new MenuItem(Texts.TrayIcon.PopupMenu.OPEN);
        currentMenuItem.addActionListener(e -> {
            if (callback != null) callback.onOpenUi();
        });
        popup.add(currentMenuItem);

        popup.addSeparator();

        notificationItem = new MenuItem(Texts.TrayIcon.PopupMenu.UNINITIALIZED_POPUP_ITEM);
        notificationItem.addActionListener(e -> {
            if (callback != null) callback.onHideShowNotifications();
        });
        popup.add(notificationItem);

        pluginItem = new MenuItem(Texts.TrayIcon.PopupMenu.UNINITIALIZED_POPUP_ITEM);
        pluginItem.addActionListener(e -> {
            if (callback != null) callback.onDisableEnablePlugins();
        });
        popup.add(pluginItem);

        currentMenuItem = new MenuItem(Texts.TrayIcon.PopupMenu.SETTINGS);
        currentMenuItem.addActionListener(e -> {
            if (callback != null) callback.onOpenSettings();
        });
        popup.add(currentMenuItem);

        currentMenuItem = new MenuItem(Texts.TrayIcon.PopupMenu.ARCHIVE);
        currentMenuItem.addActionListener(e -> {
            if (callback != null) callback.onArchive();
        });
        popup.add(currentMenuItem);

        currentMenuItem = new MenuItem(Texts.TrayIcon.PopupMenu.UPDATE);
        currentMenuItem.addActionListener(e -> {
            if (callback != null) callback.onUpdate();
        });
        popup.add(currentMenuItem);

        popup.addSeparator();

        currentMenuItem = new MenuItem(Texts.TrayIcon.PopupMenu.ABOUT);
        currentMenuItem.addActionListener(e -> {
            if (callback != null) callback.onOpenAbout();
        });
        popup.add(currentMenuItem);

        currentMenuItem = new MenuItem(Texts.TrayIcon.PopupMenu.EXIT);
        currentMenuItem.addActionListener(e -> {
            if (callback != null) callback.onExit();
        });
        popup.add(currentMenuItem);

        return popup;
    }

    @Override
    public void setCallback(TrayIconCallback callback) {
        this.callback = callback;
    }

    @Override
    public void addToSystemTray() {
        if (!SystemTray.isSupported()) {
            throw new IllegalStateException("System Tray not supported!");
        }
        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            throw new IllegalStateException("Failed to add tray icon!", e);
        }
    }

    @Override
    public void removeFromSystemTray() {
        SystemTray.getSystemTray().remove(trayIcon);
    }

    @Override
    public void setDisplayedIcon(IconType iconType) {
        switch (iconType) {
            case NORMAL:
            default:
                trayIcon.setImage(normalImage);
                break;
            case UNREAD:
                trayIcon.setImage(unreadImage);
                break;
            case BUSY:
                trayIcon.setImage(busyImage);
                break;
        }
    }

    @Override
    public void onHideNotificationsChanged(boolean notificationsHidden) {
        notificationItem.setLabel(notificationsHidden ? Texts.TrayIcon.PopupMenu.SHOW_NOTIFICATIONS : Texts.TrayIcon.PopupMenu.HIDE_NOTIFICATIONS);
    }

    @Override
    public void onPluginsDisabledChanged(boolean pluginsDisabled) {
        pluginItem.setLabel(pluginsDisabled ? Texts.TrayIcon.PopupMenu.ENABLE_PLUGINS : Texts.TrayIcon.PopupMenu.DISABLE_PLUGINS);
    }

    @Override
    public void setTooltip(String tooltipText) {
        if (tooltipText == null) {
            trayIcon.setToolTip(Texts.TrayIcon.TOOLTIP);
        } else {
            trayIcon.setToolTip(tooltipText);
        }
    }
}
