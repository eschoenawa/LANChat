package de.eschoenawa.lanchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.eschoenawa.lanchat.util.Log;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ConfigImpl implements Config {
    private static final String TAG = "Config";

    private Map<String, Setting> settings = new HashMap<>();
    private Map<String, String> currentTransaction;

    public ConfigImpl() {
        loadSettings();
    }

    private void loadSettings() {
        for (Setting setting : SettingsDefinition.getAllSettings()) {
            this.settings.put(setting.getKey(), setting);
        }
        loadFileSettings();
    }

    private void loadFileSettings() {
        Gson gson = new Gson();
        String path = getString(SettingsDefinition.Setting.CONFIG_PATH, null);
        if (path == null) {
            throw new IllegalStateException("No config path set!");
        }
        File f = new File(path);
        if (!f.exists()) {
            Log.d(TAG, "Config file '" + f.getAbsolutePath() + "' doesn't exist yet. Creating new one now...");
            saveSettings();
            Log.d(TAG, "Config file created!");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String json = br.readLine();
            br.close();
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> settingsFromFile = gson.fromJson(json, type);
            boolean overwriteRequired = false;
            for (String key : settingsFromFile.keySet()) {
                Setting currentSetting = settings.get(key);
                if (currentSetting == null || !currentSetting.isModifiable()) {
                    Log.w(TAG, "Setting '" + key + "' is either not defined or not modifiable and therefore won't be loaded from file.");
                    overwriteRequired = true;
                    continue;
                }
                currentSetting.setValue(settingsFromFile.get(key));
                settings.put(key, currentSetting);
            }
            if (overwriteRequired) {
                Log.w(TAG, "Config file contains invalid or deprecated settings. Re-creating config file...");
                saveSettings();
                Log.d(TAG, "Config file re-created.");
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to read from config file! Using defaults.", e);
        }
    }

    private void saveSettings() {
        Log.d(TAG, "Saving config file...");
        Gson gson = new GsonBuilder().create();
        String path = getString(SettingsDefinition.Setting.CONFIG_PATH, null);
        if (path == null) {
            throw new IllegalStateException("No config path set!");
        }
        try (PrintWriter writer = new PrintWriter(path)) {
            String json = gson.toJson(generateStringSettingsMap());
            writer.print(json);
            Log.d(TAG, "Config file saved!");
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Invalid config path: '" + path + "'!", e);
        }
    }

    private Map<String, String> generateStringSettingsMap() {
        Map<String, String> result = new HashMap<>();
        for (String key : settings.keySet()) {
            Setting setting = settings.get(key);
            if (setting != null && setting.isModifiable()) {
                result.put(key, setting.getValue());
            }
        }
        return result;
    }

    @Override
    public synchronized void beginTransaction() {
        currentTransaction = new HashMap<>();
    }

    @Override
    public synchronized void abortTransaction() {
        currentTransaction = null;
    }

    @Override
    public synchronized void commitTransaction() {
        if (currentTransaction != null) {
            for (String key : currentTransaction.keySet()) {
                Setting setting = settings.get(key);
                if (setting != null && setting.isModifiable()) {
                    String newValue = currentTransaction.get(key);
                    Log.d(TAG, "Changing setting '" + key + "' from '" + setting.getValue() + "' to '" + newValue + "'.");
                    setting.setValue(newValue);
                    settings.put(key, setting);
                } else {
                    Log.w(TAG, "Ignoring changes to setting '" + key + "' (value set to '" + currentTransaction.get(key) + "')! Not registered: " + (setting == null));
                }
            }
            saveSettings();
        } else {
            throw new IllegalStateException("No transaction currently active!");
        }
    }

    @Override
    public String getString(String key, String def) {
        Setting found = settings.get(key);
        return (found == null) ? def : found.getValue();
    }

    @Override
    public int getInt(String key, int def) {
        return Integer.parseInt(getString(key, String.valueOf(def)));
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return Boolean.parseBoolean(getString(key, String.valueOf(def)));
    }

    @Override
    public double getDouble(String key, double def) {
        return Double.parseDouble(getString(key, String.valueOf(def)));
    }

    @Override
    public float getFloat(String key, float def) {
        return Float.parseFloat(getString(key, String.valueOf(def)));
    }

    @Override
    public long getLong(String key, long def) {
        return Long.parseLong(getString(key, String.valueOf(def)));
    }

    @Override
    public char getChar(String key, char def) {
        return getString(key, String.valueOf(def)).charAt(0);
    }

    @Override
    public synchronized void setString(String key, String value) {
        if (currentTransaction != null) {
            currentTransaction.put(key, value);
        } else {
            throw new IllegalStateException("No transaction currently active!");
        }
    }

    @Override
    public synchronized void setInt(String key, int value) {
        setString(key, String.valueOf(value));
    }

    @Override
    public synchronized void setBoolean(String key, boolean value) {
        setString(key, String.valueOf(value));
    }

    @Override
    public synchronized void setDouble(String key, double value) {
        setString(key, String.valueOf(value));
    }

    @Override
    public synchronized void setFloat(String key, float value) {
        setString(key, String.valueOf(value));
    }

    @Override
    public synchronized void setLong(String key, long value) {
        setString(key, String.valueOf(value));
    }

    @Override
    public synchronized void setChar(String key, char value) {
        setString(key, String.valueOf(value));
    }
}