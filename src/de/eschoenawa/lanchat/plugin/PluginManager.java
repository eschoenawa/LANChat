package de.eschoenawa.lanchat.plugin;

import de.eschoenawa.lanchat.ErrorHandler;
import de.eschoenawa.lanchat.MiniUI;
import de.eschoenawa.lanchat.config.LanChatConfig;
import de.eschoenawa.lanchat.plugin.api.ApiV1;
import de.eschoenawa.lanchat.plugin.api.Plugin;
import de.eschoenawa.lanchat.plugin.implementations.ApiV1Impl;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class PluginManager {

    public PluginManager(MiniUI userInterface) {
        this.userInterface = userInterface;
    }

    private Map<String, ApiV1> apiMap = new HashMap<>();
    private Map<String, Plugin> plugins = new HashMap<>();
    private MiniUI userInterface;

    public synchronized void loadPlugins() {
        File pluginFolder = new File(LanChatConfig.PLUGIN_FOLDER);
        if (!pluginFolder.exists()) {
            if (pluginFolder.mkdirs()) {
                System.out.println("Created plugin folder");
            }
        }
        File[] files = pluginFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        ArrayList<URL> urls = new ArrayList<>();
        ArrayList<String> classes = new ArrayList<>();
        if (files != null) {
            Arrays.stream(files).forEach(file -> {
                try {
                    JarFile jarFile = new JarFile(file);
                    urls.add(new URL("jar:file:" + LanChatConfig.PLUGIN_FOLDER + "/" + file.getName() + "!/"));
                    jarFile.stream().forEach(jarEntry -> {
                        if (jarEntry.getName().endsWith(".class")) {
                            classes.add(jarEntry.getName());
                        }
                    });
                } catch (IOException e) {
                    ErrorHandler.reportError(e, true);
                }
            });
            URLClassLoader pluginLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
            classes.forEach(s -> {
                try {
                    Class classs = pluginLoader.loadClass(s.replaceAll("/", ".").replace(".class", ""));
                    if (Plugin.class.isAssignableFrom(classs)) {
                        Plugin plugin = (Plugin) classs.newInstance();
                        new Thread(() -> {
                            try {
                                String pluginId = plugin.getPluginMetaData().pluginId;
                                String pluginConfigPath = LanChatConfig.PLUGIN_FOLDER + "/" + pluginId + "_config.json";
                                ApiV1 api = new ApiV1Impl(this, pluginId, PluginConfig.fromPluginSettings(plugin.getSettings(), pluginConfigPath));
                                if (plugin.onLoad(api)) {
                                    System.out.println("Loaded plugin '" + classs.getCanonicalName() + "' successfully");
                                    plugins.put(pluginId, plugin);
                                    apiMap.put(pluginId, api);
                                    sortPluginsByPriority();
                                    try {
                                        userInterface.addPluginButtons(new ArrayList<>(plugin.getButtons()));
                                    } catch (Exception e) {
                                        plugin.onError("Failed to get plugin buttons! " + e.getMessage());
                                        ErrorHandler.reportError(e, true);
                                    }
                                } else {
                                    System.err.println("Plugin '" + classs.getCanonicalName() + "' failed to load!");
                                    ErrorHandler.showErrorDialog("Failed to load plugin!", "The Plugin '" + plugin.getPluginMetaData().name + "' reported that it failed to load!");
                                    api.invalidate();
                                }
                            } catch (Exception e) {
                                System.err.println("Error while loading plugin '" + classs.getCanonicalName() + "'!");
                                ErrorHandler.reportError(e, true, "An error has occurred while loading the plugin '" + classs.getCanonicalName() + ":\n");
                                plugin.onError("Error while loading plugin: " + e.getMessage());
                            }
                        }).start();
                    }
                } catch (Exception e) {
                    System.err.println("Internal error while loading plugin '" + s + "'!");
                    ErrorHandler.reportError(e, true, "An internal error has occurred while loading the plugin '" + s + ":\n");
                }
            });
        } else {
            System.out.println("No plugins can be loaded!");
            plugins = new HashMap<>();
        }
        /*
        List<Plugin.PluginButton> mockPluginButtons = new ArrayList<>();
        mockPluginButtons.add(new Plugin.PluginButton("Test", new Plugin.PluginButton.ButtonAction() {
            @Override
            public void onButtonPress() {
                System.out.println("YAY!");
            }
        }));
        userInterface.addPluginButtons(mockPluginButtons);
         */
    }

    private synchronized void sortPluginsByPriority() {
        try {
            plugins = plugins.entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(e -> e.getValue().getMessageProcessingChainPriority()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));
        } catch (Exception e) {
            ErrorHandler.reportError(e, true);
        }
    }

    public Collection<Plugin> getPlugins() {
        return plugins.values();
    }

    public synchronized void onApiInvalidated(String pluginId) {
        Plugin plugin = plugins.remove(pluginId);
        apiMap.remove(pluginId);
        if (plugin != null) {
            try {
                plugin.onStop();
            } catch (Exception e) {
                plugin.onError("Failed to notify of stop! " + e.getMessage());
                ErrorHandler.reportError(e, false);
            }
        }
    }

    public synchronized void unloadAllPlugins() {
        Iterator<String> iterator = apiMap.keySet().iterator();
        while (iterator.hasNext()) {
            String pluginId = iterator.next();
            apiMap.get(pluginId).invalidate();
        }
    }

    public void postEvent(String message) {
        plugins.values().forEach(plugin -> plugin.onEvent(message));
    }

    public synchronized String processReceivedMessage(String message) {
        String newMessage = message;
        for (Plugin plugin : plugins.values()) {
            try {
                newMessage = plugin.onMessageReceive(message, newMessage);
            } catch (Exception e) {
                plugin.onError("Failed to process received message! " + e.getMessage());
                ErrorHandler.reportError(e, true);
            }
        }
        return newMessage;
    }

    public synchronized String processSendMessage(String message) {
        String newMessage = message;
        for (Plugin plugin : plugins.values()) {
            try {
                newMessage = plugin.onMessageSend(message, newMessage);
            } catch (Exception e) {
                plugin.onError("Failed to process send message! " + e.getMessage());
                ErrorHandler.reportError(e, true);
            }
        }
        return newMessage;
    }

    public void sendMessage(String message) {
        userInterface.sendMessage(message);
    }

    public void messageUser(String sender, String message, boolean addToHistory, boolean showNotification) {
        userInterface.receive(sender + ": " + message, addToHistory, showNotification);
    }

    public void notification(String title, String message, String colorCode) {
        Color color = null;
        if (!("default".equalsIgnoreCase(colorCode))) {
            try {
                color = Color.decode(colorCode);
            } catch (Exception e) {
                ErrorHandler.reportError(e, false, "An error has occurred while parsing the notification color '" + colorCode + "':\n");
            }
            userInterface.notification(title, message, color);
        }
    }

    public Color getNotificationColor(String title, String message, String color) {
        String newColor = color;
        Color result = Color.DARK_GRAY;
        for (Plugin plugin : plugins.values()) {
            try {
                newColor = plugin.getNotificationColor(title, message, color, newColor);
                newColor = newColor.replace("#", "");
                newColor = newColor.replace("0x", "");
                if (newColor.length() == 6) {
                    result = new Color(Integer.valueOf(newColor.substring(0,2), 16),
                            Integer.valueOf(newColor.substring(2,4), 16),
                            Integer.valueOf(newColor.substring(4,6), 16));
                } else if (newColor.length() == 8) {
                    result = new Color(Integer.valueOf(newColor.substring(0,2), 16),
                            Integer.valueOf(newColor.substring(2,4), 16),
                            Integer.valueOf(newColor.substring(4,6), 16),
                            Integer.valueOf(newColor.substring(6,8), 16));
                } else {
                    plugin.onError("Invalid notification color format!");
                }
            } catch (Exception e) {
                plugin.onError("Failed to process notification color! " + e.getMessage());
                ErrorHandler.reportError(e, true);
            }
        }
        return result;
    }
}
