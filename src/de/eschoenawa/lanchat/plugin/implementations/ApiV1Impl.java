package de.eschoenawa.lanchat.plugin.implementations;

import de.eschoenawa.lanchat.ErrorHandler;
import de.eschoenawa.lanchat.config.LanChatConfig;
import de.eschoenawa.lanchat.plugin.PluginConfig;
import de.eschoenawa.lanchat.plugin.PluginManager;
import de.eschoenawa.lanchat.plugin.api.ApiV1;
import de.eschoenawa.lanchat.plugin.api.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ApiV1Impl implements ApiV1 {
    private boolean invalidated = false;
    private PluginManager pluginManager;
    private String pluginId;
    private PluginConfig pluginConfig;

    public ApiV1Impl(PluginManager pluginManager, String pluginId, PluginConfig pluginConfig) {
        this.pluginManager = pluginManager;
        this.pluginId = pluginId;
        this.pluginConfig = pluginConfig;
    }

    @Override
    public int getApiVersion() {
        return 1;
    }

    @Override
    public void postEvent(String message) {
        if (!invalidated) {
            pluginManager.postEvent(message);
        }
    }

    @Override
    public void sendMessage(String message) {
        if (!invalidated) {
            pluginManager.sendMessage(message);
        }
    }

    @Override
    public String getUsername() {
        if (!invalidated) {
            return LanChatConfig.get("name");
        } else {
            return null;
        }
    }

    @Override
    public void messageUser(String sender, String message) {
        messageUser(sender, message, false);
    }

    @Override
    public void messageUser(String sender, String message, boolean addToHistory) {
        messageUser(sender, message, addToHistory, true);
    }

    @Override
    public void messageUser(String sender, String message, boolean addToHistory, boolean showNotification) {
        if (!invalidated) {
            pluginManager.messageUser(sender, message, addToHistory, showNotification);
        }
    }

    @Override
    public void showNotification(String title, String message) {
        showNotification(title, message, "default");
    }

    @Override
    public void showNotification(String title, String message, String color) {
        if (!invalidated) {
            pluginManager.notification(title, message, color);
        }
    }

    @Override
    public List<Plugin.PluginMetaData> getInstalledPlugins() {
        if (!invalidated) {
            Collection<Plugin> plugins = pluginManager.getPlugins();
            List<Plugin.PluginMetaData> pluginDataList = new ArrayList<>();
            plugins.forEach(plugin -> pluginDataList.add(plugin.getPluginMetaData()));
            return pluginDataList;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void reportError(Exception e) {
        reportError(e, true);
    }

    @Override
    public void reportError(Exception e, boolean showDialog) {
        if (!invalidated) {
            ErrorHandler.reportError(e, showDialog);
        }
    }

    @Override
    public String getPluginSettingValue(String key) {
        if (!invalidated) {
            if (pluginConfig == null) {
                ErrorHandler.reportError(new NullPointerException("No config for plugin available!"), false);
                return null;
            }
            return pluginConfig.getValue(key);
        } else {
            return null;
        }
    }

    @Override
    public void invalidate() {
        invalidated = true;
        pluginManager.onApiInvalidated(pluginId);
    }

    @Override
    public boolean isInvalidated() {
        return invalidated;
    }
}
