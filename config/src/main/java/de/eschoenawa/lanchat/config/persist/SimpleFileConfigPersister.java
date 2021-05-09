package de.eschoenawa.lanchat.config.persist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.eschoenawa.lanchat.config.Config;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SimpleFileConfigPersister implements ConfigPersister {

    private final String path;

    public SimpleFileConfigPersister(String path) {
        this.path = path;
    }

    @Override
    public void save(Map<String, Config.Setting> settings) {
        Gson gson = new GsonBuilder().create();
        if (path == null) {
            throw new IllegalStateException("No config path set!");
        }
        try (PrintWriter writer = new PrintWriter(path)) {
            String json = gson.toJson(generateStringSettingsMap(settings));
            writer.print(json);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Invalid config path: '" + path + "'!", e);
        }
    }

    @Override
    public void load(ConfigPersisterLoadCallback callback) {
        Gson gson = new Gson();
        if (path == null) {
            throw new IllegalStateException("No config path set!");
        } else if (callback == null) {
            throw new IllegalArgumentException("No callback provided!");
        }
        File f = new File(path);
        if (!f.exists()) {
            callback.onPersistentSettingsNotYetCreated();
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String json = br.readLine();
            br.close();
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> settingsFromFile = gson.fromJson(json, type);
            for (String key : settingsFromFile.keySet()) {
                callback.onSettingLoaded(key, settingsFromFile.get(key));
            }
        } catch (IOException e) {
            callback.onLoadFailed(e.getMessage());
        }
        callback.onLoadDone();
    }

    private Map<String, String> generateStringSettingsMap(Map<String, Config.Setting> settings) {
        Map<String, String> result = new HashMap<>();
        for (String key : settings.keySet()) {
            Config.Setting setting = settings.get(key);
            if (setting != null) {
                result.put(key, setting.getValue());
            } else {
                throw new IllegalStateException("Setting '" + key + "' is null!");
            }
        }
        return result;
    }
}
