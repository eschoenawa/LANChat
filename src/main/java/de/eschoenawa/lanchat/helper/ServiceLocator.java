package de.eschoenawa.lanchat.helper;

import de.eschoenawa.lanchat.config.Config;
import de.eschoenawa.lanchat.config.ConfigImpl;
import de.eschoenawa.lanchat.definition.LanChatSettingsDefinition;

public class ServiceLocator {
    private static class InstanceHolder {
        static Config CONFIG_INSTANCE = new ConfigImpl(new LanChatSettingsDefinition());
    }

    public static Config getConfig() {
        return InstanceHolder.CONFIG_INSTANCE;
    }
}
