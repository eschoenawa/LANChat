package de.eschoenawa.lanchat.ui;

public interface TrayIcon {
    void setCallback(TrayIconCallback callback);

    void addToSystemTray();

    void removeFromSystemTray();

    void setDisplayedIcon(IconType iconType);

    void onHideNotificationsChanged(boolean notificationsHidden);

    void onPluginsDisabledChanged(boolean pluginsDisabled);

    void setTooltip(String tooltipText);

    interface TrayIconCallback {
        void onOpenUi();

        void onHideShowNotifications();

        void onDisableEnablePlugins();

        void onOpenSettings();

        void onArchive();

        void onUpdate();

        void onOpenAbout();

        void onExit();
    }

    enum IconType {
        NORMAL, UNREAD, BUSY
    }
}
