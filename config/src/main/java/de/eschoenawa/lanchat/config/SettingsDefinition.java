package de.eschoenawa.lanchat.config;

import java.util.List;

public interface SettingsDefinition {
    List<Config.Setting> getAllSettings();
    String getConfigPathKey();
    void validateSettings();
}
