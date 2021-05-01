package de.eschoenawa.lanchat.ui.tray;

public interface TrayIcon {
    void setCallback(TrayIconCallback callback);

    void addToSystemTray();

    void removeFromSystemTray();

    void setDisplayedIcon(IconType iconType);

    void onPluginsDisabledChanged(boolean pluginsDisabled);

    void setTooltip(String tooltipText);

    void showNotification(String title, String text, boolean loud);

    interface TrayIconCallback {
        void onOpenUi(boolean toggle);

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
