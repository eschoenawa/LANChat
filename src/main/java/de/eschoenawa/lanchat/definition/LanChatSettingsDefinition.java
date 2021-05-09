package de.eschoenawa.lanchat.definition;

import de.eschoenawa.lanchat.config.Config;
import de.eschoenawa.lanchat.config.SettingsDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LanChatSettingsDefinition implements SettingsDefinition {
    private final List<Config.Setting> settings = new ArrayList<>();

    public static class SettingKeys {
        //internal
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
        public static final String HISTORY_PATH = "history_path";


        //modifiable
        public static final String NAME = "name";
        public static final String MINIMIZED = "minimized";
        public static final String AUTO_UPDATE = "auto_update";
        public static final String PLUGINS_ENABLED = "plugins";
        public static final String SHOW_NOTIFICATIONS = "notifications";
        public static final String AUTOSTART = "autostart";
    }

    // TODO add priority for sorting in settings window

    public LanChatSettingsDefinition() {
        defineInternalSettings();
        defineModifiableSettings();
    }

    private void defineInternalSettings() {
        settings.add(new Config.Setting(SettingKeys.CONFIG_PATH, "./config.json", "", Config.SettingType.RAW, false, false));
        settings.add(new Config.Setting(SettingKeys.PORT, "55545", "", Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(SettingKeys.LOG_LEVEL, "t", "", Config.SettingType.RAW, false, false));
        settings.add(new Config.Setting(SettingKeys.DISCOVERY_PREFIX, "discover", "", Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(SettingKeys.UPDATE_PREFIX, "update", "", Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(SettingKeys.DISCOVERY_RESPONSE_PREFIX, "hello", "", Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(SettingKeys.MESSAGE_PREFIX, "msg", "", Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(SettingKeys.SHOUT_PREFIX, "shout", "", Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(SettingKeys.PLUGIN_PATH, "./plugins", "", Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(SettingKeys.EXCEPTION_PATH_PREFIX, "./exception_", "", Config.SettingType.RAW, false, false));
        settings.add(new Config.Setting(SettingKeys.CRASH_PATH_PREFIX, "./exception_", "", Config.SettingType.RAW, false, false));
        settings.add(new Config.Setting(SettingKeys.UI_VISIBILITY_COOLDOWN, "100", "", Config.SettingType.RAW, false, false));
        settings.add(new Config.Setting(SettingKeys.HISTORY_PATH, "./history.txt", "", Config.SettingType.RAW, false, true));
    }

    private void defineModifiableSettings() {
        settings.add(new Config.Setting(SettingKeys.NAME, "Anonymous", "Name", Config.SettingType.RAW, true, false));
        settings.add(new Config.Setting(SettingKeys.MINIMIZED, "true", "Start LANChat minimized", Config.SettingType.BOOLEAN, true, false));
        settings.add(new Config.Setting(SettingKeys.AUTO_UPDATE, "true", "Get automatic updates", Config.SettingType.BOOLEAN, true, false));
        settings.add(new Config.Setting(SettingKeys.SHOW_NOTIFICATIONS, "true", "Show notifications", Config.SettingType.BOOLEAN, true, false));
        settings.add(new Config.Setting(SettingKeys.AUTOSTART, "false", "Start LANChat on Windows startup", Config.SettingType.BOOLEAN, true, false));
        //TODO make plugins setting modifiable again when adding plugins
        settings.add(new Config.Setting(SettingKeys.PLUGINS_ENABLED, "false", "Use plugins", Config.SettingType.BOOLEAN, false, true));
    }

    @Override
    public @NotNull List<Config.Setting> getAllSettings() {
        return settings;
    }

    @Override
    public @NotNull String getConfigPathKey() {
        return SettingKeys.CONFIG_PATH;
    }

    @Override
    public void validateSettings() {
        //TODO check settings for validity (max name length etc.) and correct false stuff
    }
}
