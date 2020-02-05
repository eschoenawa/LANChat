
# LANChat

![LANChat Screenshot](/lanchat_screenshot.png)

This is a very small application I threw together for communicating inside LANs using UDP Broadcast messages (I use it as a local chat for communicating with my roommates). Since this is a freetime project of mine, I won't ensure full functionality. Use at your own risk!

**IMPORTANT INFO:** Please note that there are still some issues with the UI and the general way this program works (It should work on Systems supporting the Java SystemTray Class, but I've only tested on Windows 10). If you have a network or system that has to be secure in any form I do not recommend using this program as it can be modified remotely and uses no encryption whatsoever!

*This program broadcasts messages through the entire network. Every participant of a network connected to your client will be able to read the messages you send with LANChat. This is not a program to hold private conversations with, use any chat client with encryption instead (there are hundreds to choose from).*


# Plugin development for LANChat

LANChat version 1.2 and higher comes with support for plugins. This allows you to process incoming / outgoing messages, send notifications or network messages and create buttons, all with your own custom logic. I've created this feature so I can do minor changes to LANChat and use it for debugging other UDP-Broadcast applications (e.g. an Arduino broadcasting sensor data or receiving certain commands via UDP-Broadcasts) without modifying LANChat itself. I made the PluginSDK public so anyone (with java knowledge) can customize this program to their needs.

## A note on security
Plugins are disabled by default for LANChat. Enable them at your own risk and make sure you know a plugin does what it should be doing before installing it. It is very easy to write malicious plugins for LANChat as installed plugins are in no way protected against being replaced and their only unique identifier is the `pluginId`, which every plugin developer can set freely without restrictions.
Plugins can be enabled in the right-click context menu of the tray icon.

## Quick start guide for developing plugins
 1. In order to develop a LANChat plugin, you first have to download the most recent version of LANChat's PluginSDK. This can be found under the 'Releases' tab in github.
 2. Now include the PluginSDK.jar as an external library in your plugin project. How you can do this depends on your IDE.
 3. *This step is optional and depends on your preference.* If your IDE has support for it you should link the sources contained in the PluginSDK.jar to it's binaries so your IDE can provide Javadoc and parameter names correctly and not derived from a decompiled binary.
 4. Create a class that extends the abstract class `Plugin` from the PluginSDK. Override the required methods `onLoad` and `getPluginMetaData`.
 5. In `getPluginMetaData` you should return an instance of `PluginMetaData` that contains the following information about your plugin:
    - `pluginId`: The ID of your plugin. This has to be **unique** to your plugin. To avoid conflicts you should use a domain you control in java-package notation, e.g. `com.example.myplugin`. If two plugins share the same `pluginId`, only one of them will be loaded!
    - `name`: The name of your plugin.
    - `description`: A short description of what your plugin does.
    - `author`: The Author(s) of the plugin
    - `version`: An integer representation of the version of your plugin. Increase this when your plugin gets a new version.
    - `readableVersion`: A human readable String representing your plugins version.
6. In `onLoad` you should return `true` if your plugin loaded successfully, otherwise `false`.
7. Implement the functionality of your plugin. You can get various callbacks for LANChat actions by overriding certain methods of `Plugin`. You can also access the LANChat API with the API-object provided to your plugin in `onLoad`. Please read the PluginSDK Documentation down below or in JavaDoc for the full documentation of the callbacks and the API.
8. Once you're done implementing your plugin it is time to test it. Create a jar-file containing your plugin's binaries. Note that this jar-file doesn't have to be executable. Now put that jar-file into the `plugins`-folder of your LANChat installation and (re)start LANChat to load the plugin.

## Code examples
### Minimal Plugin implementation
```
import de.eschoenawa.lanchat.plugin.api.ApiV1;  
import de.eschoenawa.lanchat.plugin.api.Plugin;  
  
import java.util.ArrayList;  
import java.util.List;  
  
public class MyPlugin extends Plugin {  
  private ApiV1 api;  
  
  @Override  
  public PluginMetaData getPluginMetaData() {  
    return new PluginMetaData("com.example.myplugin", "My Plugin", "A short example for a minimal implementation of a plugin.", "Alice", 1, "1");  
  }  
  
    @Override  
  public boolean onLoad(ApiV1 apiV1) {  
    api = apiV1;
    api.messageUser("MinPlugin", "Hello World", false);
    return true;
  }  
}
```

## PluginSDK Documentation
### Colors
Every time a color is a parameter or a return value Strings are used to represent the color in hexadecimal, e.g. `0xFFFFFF`, `#FFFFFF`, `FFFFFF`, `0xFFFFFFFF`,  `#FFFFFFFF` or `FFFFFFFF`. Please provide colors in one of these formats and accept them in any of them when working with colors and the PluginSDK.

### Classes & Interfaces
The PluginSDK defines the following classes & interfaces:

 - **abstract class Plugin**: Abstract class that should be overridden by a plugin to allow LANChat to detect and load the plugin as well as to receive callbacks for certain events.
     - **static class PluginButton**: Plugins should return a list of `PluginButton`s in `Plugin#getButtons()` to register custom buttons for the Plugin. These buttons will appear in a list under the Plugin-Tab in LANChat.
         - **interface ButtonAction**: This interface contains a  callback that is called when a `PluginButton` is pressed.
     - **static class PluginSetting**: Plugins should return a list of `PluginSetting`s in `Plugin#getSettings()` to register custom settings that will be persisted by LANChat and can be read using the LANChat API. You are not able to read the settings from other plugins with a different `pluginId`.
 - **interface ApiV1**: The first version of LANChat's API. Any subsequent versions will implement this interface to allow backwards compatibility

### Callbacks
Version 1 of the PluginSDK includes the following callbacks for LANChat events.  To receive these callbacks a plugin has to implement the abstract class `Plugin` from the PluginSDK. Callbacks that have to be implemented are marked with a star (*). All other callbacks should only be implemented if they are required for your plugin.

 - ***boolean onLoad(ApiV1 api)**: Is called when the plugin is loaded. Provides an API-Object to the plugin, with which the plugin can access the API of LANChat.
 - ***PluginMetaData getPluginMetaData()**: LANChat uses this method to gain information about the plugin. This method should return an instance of `PluginMetaData` to provide meta data for the plugin.
 - **int getMessageProcessingChainPriority()**: Should return the priority of the plugin in the message processing chain (higher value means higher priority). Plugins with high priority get to modify the incoming / outgoing message later than plugins with low priority. Please note that this means, that plugins with high priority get to process the message last and therefor 'have the last say' in any modification that might occur. This can be used to ensure compatibility with other plugins when changing the message to be sent. This method should only be overridden if the plugin modifies messages (incoming and/or outgoing) or the notification color for incoming messages.
 - **String onMessageSend(String originalMessage, String currentMessage)**: Called when a message will be sent. Use this to process outgoing messages. The message can also be modified by returning the new message to be sent. If null is returned, the message will not be sent (unless this is bypassed by other plugins with a higher priority). This method should only be overridden if the plugin modifies outgoing messages or prevents certain messages to be sent (e.g. commands).  
Note: This method isn't called for messages triggered by plugins.
- **String onMessageReceive(String originalMessage, String currentMessage)**: Called when a message is received and before it is displayed to the user. Use this to process incoming messages. The message can also be modified by returning the new message to be displayed. If null is returned, the Message will not be displayed to the user (unless this is bypassed by other plugins with a higher priority). This method should only be overridden if the plugin modifies incoming messages or prevents certain messages to be displayed to the user (e.g. commands from other clients on the network).
Note: This method will also be called for messages originating from the local LANChat client!
- **String getNotificationColor(String title, String message, String originalColor, String currentColor)**: Called when a notification is shown to the user. By returning a different color than `currentColor` you can set a new color for the notification.
Note: This method isn't called for notifications that were manually triggered by plugins.
- **List\<PluginButton\> getButtons()**: Should return a List of all PluginButtons that should be added to the 'plugin'-Tab of LANChat.
- **List\<PluginSetting\> getSettings()**: A List of all PluginSettings that should be available. These settings are persisted. Use `ApiV1#getPluginSettingValue(String)` to retrieve the current setting.  
Note: These settings cannot be set by the plugin itself. At the moment they have to be modified in the config file for the plugin, but later these will be found in the LANChat settings window.
- **void onEvent(String message)**: Called when an event was triggered. Events can be used to communicate with other plugins. See `ApiV1#postEvent(String)` for how to send an event.
- **onError(String error)**:  Called when LANChat fails any action while executing plugin code. The stacktrace can be viewed in generated bug report file.
Note: This method will not be called if the plugin returns false in `Plugin#onLoad(ApiV1)`.
- **void onStop()**: Called when LANChat stops using the plugin. After this method was called you should not receive any callbacks anymore. Calls to the Api-Object will no longer work. 
Note: This method will also be called when the plugin invalidates the Api using `ApiV1#invalidate()`.

### LANChat API
The LANChat API provides various methods to trigger certain actions in the local LANChat client. An API-object is passed to your plugin with the `Plugin#onLoad(ApiV1)`-callback. Version 1 of the PluginSDK provides the following API methods.
- **int getApiVersion()**: Returns the version of the API. Any subsequent API will implement all previous API-Interfaces. This way you can ensure compatibility of your plugin with multiple LANChat versions.
- **void postEvent(String message)**: Posts a text event that can be received by all other plugins.
- **void sendMessage(String message)**: Sends a message to other LANChat clients in the network. The difference to a message sent by the user is that you can specify the full message (no sender will be added to the message you send with this). A normal LANChat message gets a sender prefix and is composed like this: `[sender]: [message]`. If you add a space inside the `[sender]`-part, LANChat will mark that message in red when received (and post a red notification). This feature can be used for very important messages.
- **String getUsername()**: This method is for retrieving the users name (that was configured in LANChat settings).
- **void messageUser(String sender, String message)**: Use this method to send a message to the user as if he just received a message from the network. The message will not be persisted and will disappear when LANChat loads the history or is restarted. If you wish specify whether the message should be persisted, use `ApiV1#messageUser(String, String, boolean)` instead. If LANChat is not opened when this is called a notification will be shown.
- **void messageUser(String sender, String message, boolean addToHistory)**: Use this method to send a message to the user as if he just received a message from the network. You can specify whether or not this message should be added to the chat history. If LANChat is not opened when this is called a notification will be shown.
- **messageUser(String sender, String message, boolean addToHistory, boolean showNotification)**: Use this method to send a message to the user as if he just received a message from the network. You can specify whether or not this message should be added to the chat history and whether or not a notification should be shown.
- **void showNotification(String title, String message)**: Use this method to show a notification without it showing up in the chat at all.  
Note: For this to work notifications have to be enabled and LANChat has to be minimized (LANChat doesn't show notifications when it is opened).
- **void showNotification(String title, String message, String color)**: Use this method to show a notification without it showing up in the chat at all.  
Note: For this to work notifications have to be enabled and LANChat has to be minimized (LANChat doesn't show notifications when it is opened).
- **List<Plugin.PluginMetaData> getInstalledPlugins()**: This method returns a list of `Plugin.PluginMetaData` objects describing all the plugins installed for this instance of LANChat. Use this if you want to work with other plugins and need to check if they are installed.
- **void reportError(Exception e)**: Use this method for reporting any errors inside your plugin. A dialog will be displayed to the user, where they can choose whether or not a stacktrace should be written to file. These stacktraces can be used to analyze bug reports.
- **void reportError(Exception e, boolean showDialog)**: Use this method for reporting any errors inside your plugin. A dialog can be displayed to the user, where they can choose whether or not a stacktrace should be written to file. These stacktraces can be used to analyze bug reports.
- **String getPluginSettingValue(String key)**: This method allows reading settings that the plugin has defined (settings are defined in the implementation of `Plugin#getSettings()`). These settings cannot be changed by the plugin.
- **void invalidate()**: This method invalidates the API-Object. This makes it impossible to make any further calls to the api and the plugin will be unregistered from any callbacks. This will trigger a call to `Plugin#onStop()`, unless the plugin is already unregistered from callbacks.
- **boolean isInvalidated()**: This method allows the plugin to check whether or not this api was already invalidated.
