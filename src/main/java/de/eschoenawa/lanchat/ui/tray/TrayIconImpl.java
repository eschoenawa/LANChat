package de.eschoenawa.lanchat.ui.tray;

import de.eschoenawa.lanchat.helper.Texts;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TrayIconImpl implements TrayIcon {
    private TrayIconCallback callback;
    private IconType currentIcon;
    private final java.awt.TrayIcon trayIcon;

    private final Image normalImage = Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/computer.gif"));
    private final Image unreadImage = Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/warning.png"));
    private final Image busyImage = Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/harddrive.gif"));

    public TrayIconImpl() {
        currentIcon = IconType.NORMAL;
        trayIcon = new java.awt.TrayIcon(normalImage, Texts.TrayIcon.TOOLTIP, createPopupMenu());
        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && callback != null) {
                    callback.onOpenUi(true);
                }
            }
        });
        trayIcon.addActionListener(e -> callback.onOpenUi(false));
    }

    private PopupMenu createPopupMenu() {
        PopupMenu popup = new PopupMenu();
        MenuItem currentMenuItem = new MenuItem(Texts.TrayIcon.PopupMenu.OPEN);
        currentMenuItem.addActionListener(e -> {
            if (callback != null) callback.onOpenUi(true);
        });
        popup.add(currentMenuItem);

        popup.addSeparator();

        currentMenuItem = new MenuItem(Texts.TrayIcon.PopupMenu.ENABLE_PLUGINS);
        currentMenuItem.addActionListener(e -> {
            if (callback != null) callback.onDisableEnablePlugins();
        });
        currentMenuItem.setEnabled(false);
        popup.add(currentMenuItem);

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

        currentMenuItem = new MenuItem(Texts.TrayIcon.PopupMenu.DUMP_LOG);
        currentMenuItem.addActionListener(e -> {
            if (callback != null) callback.onDumpLog();
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
        currentIcon = iconType;
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
    public IconType getDisplayedIcon() {
        return currentIcon;
    }

    @Override
    public void onPluginsDisabledChanged(boolean pluginsDisabled) {
        // TODO re-enable when plugins are coming
        // pluginItem.setLabel(pluginsDisabled ? Texts.TrayIcon.PopupMenu.ENABLE_PLUGINS : Texts.TrayIcon.PopupMenu.DISABLE_PLUGINS);
    }

    @Override
    public void setTooltip(String tooltipText) {
        if (tooltipText == null) {
            trayIcon.setToolTip(Texts.TrayIcon.TOOLTIP);
        } else {
            trayIcon.setToolTip(tooltipText);
        }
    }

    @Override
    public void showNotification(String title, String text, boolean loud) {
        trayIcon.displayMessage(title, text, (loud ? java.awt.TrayIcon.MessageType.WARNING : java.awt.TrayIcon.MessageType.NONE));
    }
}
