package de.eschoenawa.lanchat.plugin.api;

import java.util.List;

public interface ApiV1 {
    /**
     * Returns the version of the API. Any subsequent API will implement all previous API-Interfaces. This way you
     * can ensure compatibility of your plugin with multiple LANChat versions.
     * @return The version of this Api
     */
    public int getApiVersion();

    /**
     * Posts a text event that can be received by all other plugins.
     * @param message The text to be sent as an event.
     */
    public void postEvent(String message);

    /**
     * Sends a message to other LANChat clients in the network. The difference to a message sent by the user is that you
     * can specify the full message (no sender will be added to the message you send with this). A normal LANChat
     * message gets a sender prefix and is composed like this: '[sender]: [message]'. If you add a space inside the
     * [sender]-part, LANChat will mark that message in red when received (and post a red notification). This feature
     * can be used for very important messages.
     * @param message The message to be sent
     */
    public void sendMessage(String message);

    /**
     * This method is for retrieving the users name (that was configured in LANChat settings).
     * @return The displayed name of the user
     */
    public String getUsername();

    /**
     * Use this method to send a message to the user as if he just received a message from the network. The message will
     * not be persisted and will disappear when LANChat loads the history or is restarted. If you wish specify whether
     * the message should be persisted, use {@link ApiV1#messageUser(String, String, boolean)} instead.
     * If LANChat is not opened when this is called a notification will be shown.
     * @param sender The sender to display
     * @param message The message to display
     */
    public void messageUser(String sender, String message);

    /**
     * Use this method to send a message to the user as if he just received a message from the network. You can specify
     * whether or not this message should be added to the chat history.
     * If LANChat is not opened when this is called a notification will be shown.
     * @param sender The sender to display
     * @param message The message to display
     * @param addToHistory true if the message should be added to the chat history, otherwise false
     */
    public void messageUser(String sender, String message, boolean addToHistory);

    /**
     * Use this method to send a message to the user as if he just received a message from the network. You can specify
     * whether or not this message should be added to the chat history and whether or not a notification should be shown.
     * @param sender The sender to display
     * @param message The message to display
     * @param addToHistory true if the message should be added to the chat history, otherwise false
     * @param showNotification true if the message should trigger a notification, otherwise false
     */
    public void messageUser(String sender, String message, boolean addToHistory, boolean showNotification);

    /**
     * Use this method to show a notification without it showing up in the chat at all.
     * Note: For this to work notifications have to be enabled and LANChat has to be minimized (LANChat doesn't show
     * notifications when it is opened).
     * @param title The title of the notification
     * @param message The message of the notification
     */
    public void showNotification(String title, String message);

    /**
     * Use this method to show a notification without it showing up in the chat at all.
     * Note: For this to work notifications have to be enabled and LANChat has to be minimized (LANChat doesn't show
     * notifications when it is opened).
     * @param title The title of the notification
     * @param message The message of the notification
     * @param color The color of the notification (use hex)
     */
    public void showNotification(String title, String message, String color);

    /**
     * This method returns a list of {@link de.eschoenawa.lanchat.plugin.api.Plugin.PluginMetaData} objects describing
     * all the plugins installed for this instance of LANChat. Use this if you want to work with other plugins and need
     * to check if they are installed.
     * @return A list containing meta data for all installed plugins
     */
    public List<Plugin.PluginMetaData> getInstalledPlugins();

    /**
     * Use this method for reporting any errors inside your plugin. A dialog will be displayed to the user, where
     * they can choose whether or not a stacktrace should be written to file. These stacktraces can be used to analyze
     * bug reports.
     * @param e The Exception to report
     */
    public void reportError(Exception e);

    /**
     * Use this method for reporting any errors inside your plugin. A dialog can be displayed to the user, where
     * they can choose whether or not a stacktrace should be written to file. These stacktraces can be used to analyze
     * bug reports.
     * @param e The Exception to report
     * @param showDialog Whether or not a dialog should be displayed to the user. If false the stacktrace will be
     *                   written to a file by default
     */
    public void reportError(Exception e, boolean showDialog);

    /**
     * This method allows reading settings that the plugin has defined (settings are defined in the implementation of
     * {@link Plugin#getSettings()}). These settings cannot be changed by the plugin.
     * @param key The key for this setting
     * @return The value of the setting. null, if the setting wasn't found
     */
    public String getPluginSettingValue(String key);

    /**
     * This method invalidates the API-Object. This makes it impossible to make any further calls to the api and
     * the plugin will be unregistered from any callbacks. This will trigger a call to {@link Plugin#onStop()}, unless
     * the plugin is already unregistered from callbacks.
     */
    public void invalidate();

    /**
     * This method allows the plugin to check whether or not this api was already invalidated.
     * @return true, if the api is invalidated, otherwise false
     */
    public boolean isInvalidated();
}
