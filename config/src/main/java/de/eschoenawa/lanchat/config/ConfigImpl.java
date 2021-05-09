package de.eschoenawa.lanchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.eschoenawa.lanchat.config.persist.ConfigPersister;
import de.eschoenawa.lanchat.config.persist.SimpleFileConfigPersister;
import de.eschoenawa.lanchat.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigImpl implements Config {
    private static final String TAG = "Config";

    private final ConfigPersister persister;
    private final SettingsDefinition settingsDefinition;
    private final Map<String, Setting> settings = new HashMap<>();
    private Map<String, String> currentTransaction;

    public ConfigImpl(SettingsDefinition settingsDefinition) {
        this.settingsDefinition = settingsDefinition;
        loadSettingsFromDefinition();
        this.persister = new SimpleFileConfigPersister(getString(settingsDefinition.getConfigPathKey(), null));
        loadSettingsFromFile();
        this.settingsDefinition.validateSettings();
    }

    public ConfigImpl(SettingsDefinition settingsDefinition, ConfigPersister persister) {
        this.settingsDefinition = settingsDefinition;
        loadSettingsFromDefinition();
        this.persister = persister;
        loadSettingsFromFile();
        this.settingsDefinition.validateSettings();
    }

    private void loadSettingsFromDefinition() {
        for (Setting setting : settingsDefinition.getAllSettings()) {
            this.settings.put(setting.getKey(), setting);
        }
    }

    private void loadSettingsFromFile() {
        this.persister.load(new ConfigPersister.ConfigPersisterLoadCallback() {
            private boolean overwriteRequired = false;
            @Override
            public void onSettingLoaded(String key, String value) {
                Setting currentSetting = settings.get(key);
                if (currentSetting == null || !currentSetting.isModifiable()) {
                    Log.w(TAG, "Setting '" + key + "' is either not defined or not modifiable and therefore won't be loaded from file.");
                    overwriteRequired = true;
                } else {
                    currentSetting.setValue(value);
                    settings.put(key, currentSetting);
                }
            }

            @Override
            public void onPersistentSettingsNotYetCreated() {
                Log.d(TAG, "Config file doesn't exist yet. Creating new one now...");
                saveSettings();
                Log.d(TAG, "Config file created!");
            }

            @Override
            public void onLoadFailed(String reason) {
                Log.e(TAG, "Unable to read from config file (" + reason + ")! Using defaults.");
            }

            @Override
            public void onLoadDone() {
                if (overwriteRequired) {
                    Log.w(TAG, "Config file contains invalid or deprecated settings. Re-creating config file...");
                    saveSettings();
                    Log.d(TAG, "Config file re-created.");
                }
            }
        });
    }

    private void saveSettings() {
        Log.d(TAG, "Saving config file...");
        this.persister.save(generateModifiableSettingsMap());
        Log.d(TAG, "Config file saved!");
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
        if (isTransactionActive()) {
            for (String key : currentTransaction.keySet()) {
                Setting setting = settings.get(key);
                if (setting != null && setting.isModifiable()) {
                    String newValue = currentTransaction.get(key);
                    Log.d(TAG, "Changing setting '" + key + "' to '" + newValue + "'.");
                    setting.setValue(newValue);
                    settings.put(key, setting);
                } else {
                    Log.w(TAG, "Ignoring changes to setting '" + key + "' (value set to '" + currentTransaction.get(key) + "')! Not registered: " + (setting == null));
                }
            }
            currentTransaction = null;
            saveSettings();
            settingsDefinition.validateSettings();
        } else {
            throw new IllegalStateException("No transaction currently active!");
        }
    }

    @Override
    public synchronized boolean isTransactionActive() {
        return currentTransaction != null;
    }

    @Override
    public synchronized boolean doesTransactionRequireRestart() {
        if (isTransactionActive()) {
            for (String key : currentTransaction.keySet()) {
                Setting setting = settings.get(key);
                if (setting != null && setting.isModifiable() && setting.isRestartRequired()) {
                    return true;
                }
            }
            return false;
        } else {
            throw new IllegalStateException("No transaction currently active!");
        }
    }

    @Override
    public List<Setting> getModifiableSettings() {
        List<Setting> result = new ArrayList<>();
        for (Setting setting : settings.values()) {
            if (setting.isModifiable()) {
                result.add(setting);
            }
        }
        return result;
    }

    @Override
    public String getString(String key, String def) {
        String result = null;
        if (isTransactionActive()) {
            result = currentTransaction.get(key);
        }
        if (result == null) {
            Setting found = settings.get(key);
            result = (found == null) ? def : found.getValue();
        }
        return result;
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
    public String requireString(String key) {
        String result = getString(key, null);
        if (result == null) {
            throw new IllegalStateException("Value for key '" + key + "' is required but not stored in config!");
        }
        return result;
    }

    @Override
    public int requireInt(String key) {
        return Integer.parseInt(requireString(key));
    }

    @Override
    public boolean requireBoolean(String key) {
        return Boolean.parseBoolean(requireString(key));
    }

    @Override
    public double requireDouble(String key) {
        return Double.parseDouble(requireString(key));
    }

    @Override
    public float requireFloat(String key) {
        return Float.parseFloat(requireString(key));
    }

    @Override
    public long requireLong(String key) {
        return Long.parseLong(requireString(key));
    }

    @Override
    public char requireChar(String key) {
        return requireString(key).charAt(0);
    }

    @Override
    public synchronized void setString(String key, String value) {
        if (isTransactionActive()) {
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

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(generateStringSettingsMap());
    }

    private Map<String, Setting> generateModifiableSettingsMap() {
        HashMap<String, Setting> result = new HashMap<>();
        for (String key : settings.keySet()) {
            Setting setting = settings.get(key);
            if (setting.isModifiable()) {
                result.put(key, setting);
            }
        }
        return result;
    }
}
