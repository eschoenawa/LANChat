package de.eschoenawa.lanchat.config.persist;

import de.eschoenawa.lanchat.config.Config;

import java.util.Map;

public interface ConfigPersister {
    void save(Map<String, Config.Setting> settings);
    void load(ConfigPersisterLoadCallback callback);

    interface ConfigPersisterLoadCallback {
        void onSettingLoaded(String key, String value);
        void onPersistentSettingsNotYetCreated();
        void onLoadFailed(String reason);
        void onLoadDone();
    }
}
