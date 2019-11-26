package de.eschoenawa.lanchat.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.eschoenawa.lanchat.ErrorHandler;
import de.eschoenawa.lanchat.plugin.api.Plugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginConfig {

    private Map<String, String> defaults;
    private Map<String, String> values;
    private String configPath;

    public static PluginConfig fromPluginSettings(List<Plugin.PluginSetting> pluginSettings, String configPath) {
        Map<String, String> defaults = new HashMap<>();
        pluginSettings.forEach(pluginSetting -> defaults.put(pluginSetting.key, pluginSetting.defaultValue));
        PluginConfig pluginConfig = new PluginConfig(defaults, configPath);
        pluginConfig.load();
        return pluginConfig;
    }

    public PluginConfig(Map<String, String> defaults, String configPath) {
        this.defaults = defaults;
        this.values = new HashMap<>();
        this.configPath = configPath;
    }

    public void setValue(String key, String value) {
        load();
        values.put(key, value);
        export();
    }

    public String getValue(String key) {
        load();
        return values.get(key);
    }

    private void export() {
        Gson gson = new GsonBuilder().create();
        PrintWriter writer;
        try {
            writer = new PrintWriter(configPath);
            String json = gson.toJson(values);
            writer.print(json);
            writer.close();
        } catch (FileNotFoundException e) {
            ErrorHandler.reportError(e, false);
        }
    }

    private void load() {
        Gson gson = new Gson();
        File f = new File(configPath);
        if (!f.exists()) {
            this.values = this.defaults;
            export();
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(configPath));
            String json = br.readLine();
            br.close();
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            this.values = gson.fromJson(json, type);
        } catch (IOException e) {
            ErrorHandler.reportError(e, false);
            this.values = this.defaults;
        }
    }
}
