package de.eschoenawa.lanchat.definition;

import de.eschoenawa.lanchat.config.Config;
import de.eschoenawa.lanchat.config.SettingsDefinition;

import java.util.ArrayList;
import java.util.List;

public class LanChatSettingsDefinition implements SettingsDefinition {
    private List<Config.Setting> settings = new ArrayList<>();

    public static class SettingKeys {
        //internal
        public static final String VERSION = "version";
        public static final String CONFIG_PATH = "config_path";
        public static final String PORT = "port";
        public static final String LOG_LEVEL = "log_level";
        public static final String DISCOVERY_PREFIX = "command_prefix";
        public static final String UPDATE_PREFIX = "update_prefix";
        public static final String DISCOVERY_RESPONSE_PREFIX = "response_prefix";
        public static final String MESSAGE_PREFIX = "message_prefix";
        public static final String SHOUT_PREFIX = "shout_prefix";
        public static final String PLUGIN_PATH = "plugin_path";
        public static final String EXCEPTION_PATH_PREFIX = "exception_path_prefix";
        public static final String CRASH_PATH_PREFIX = "crash_path_prefix";
        public static final String UI_VISIBILITY_COOLDOWN = "ui_visibility_cooldown";


        //modifiable
        public static final String NAME = "name";
        public static final String MINIMIZED = "minimized";
        public static final String AUTO_UPDATE = "auto_update";
        public static final String PLUGINS_ENABLED = "plugins";
    }

    public LanChatSettingsDefinition() {
        defineInternalSettings();
        defineModifiableSettings();
    }

    private void defineInternalSettings() {
        settings.add(new Config.Setting(SettingKeys.VERSION, "v2.0.0_dev", null, Config.SettingType.RAW, false, false));
        settings.add(new Config.Setting(SettingKeys.CONFIG_PATH, "./config.json", null, Config.SettingType.RAW, false, false));
        settings.add(new Config.Setting(SettingKeys.PORT, "55545", null, Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(SettingKeys.LOG_LEVEL, "t", null, Config.SettingType.RAW, false, false));
        settings.add(new Config.Setting(SettingKeys.DISCOVERY_PREFIX, "discover", null, Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(SettingKeys.UPDATE_PREFIX, "update", null, Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(SettingKeys.DISCOVERY_RESPONSE_PREFIX, "hello", null, Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(SettingKeys.MESSAGE_PREFIX, "msg", null, Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(SettingKeys.SHOUT_PREFIX, "shout", null, Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(SettingKeys.PLUGIN_PATH, "./plugins", null, Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(SettingKeys.EXCEPTION_PATH_PREFIX, "./exception_", null, Config.SettingType.RAW, false, false));
        settings.add(new Config.Setting(SettingKeys.CRASH_PATH_PREFIX, "./exception_", null, Config.SettingType.RAW, false, false));
        settings.add(new Config.Setting(SettingKeys.UI_VISIBILITY_COOLDOWN, "100", null, Config.SettingType.RAW, false, false));
    }

    private void defineModifiableSettings() {
        settings.add(new Config.Setting(SettingKeys.NAME, "Anonymous", "Name", Config.SettingType.RAW, true, false));
        settings.add(new Config.Setting(SettingKeys.MINIMIZED, "true", "Start LANChat minimized", Config.SettingType.BOOLEAN, true, false));
        settings.add(new Config.Setting(SettingKeys.AUTO_UPDATE, "true", "Get automatic updates", Config.SettingType.BOOLEAN, true, false));
        settings.add(new Config.Setting(SettingKeys.PLUGINS_ENABLED, "false", "Use plugins", Config.SettingType.BOOLEAN, true, true));
    }

    @Override
    public List<Config.Setting> getAllSettings() {
        return settings;
    }

    @Override
    public String getConfigPathKey() {
        return SettingKeys.CONFIG_PATH;
    }

    @Override
    public void validateSettings() {
        //TODO check settings for validity (max name length etc.)
    }
}
