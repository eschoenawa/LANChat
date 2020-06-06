package de.eschoenawa.lanchat.config;

import java.util.ArrayList;
import java.util.List;

public class SettingsDefinition {
    private static List<Config.Setting> settings = new ArrayList<>();

    public static class Setting {
        //internal
        public static final String VERSION = "version";
        public static final String CONFIG_PATH = "config_path";
        public static final String PORT = "port";
        public static final String LOG_LEVEL = "log_level";
        public static final String CMD_PREFIX = "command_prefix";
        public static final String UPD_PREFIX = "update_prefix";
        public static final String RSP_PREFIX = "response_prefix";
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

    static {
        defineInternalSettings();
        defineModifiableSettings();
    }

    private static void defineInternalSettings() {
        settings.add(new Config.Setting(Setting.VERSION, "v2.0.0_dev", null, Config.SettingType.RAW, false, false));
        //TODO config path should be old path, is changed for testing
        settings.add(new Config.Setting(Setting.CONFIG_PATH, "./new_config.json", null, Config.SettingType.RAW, false, false));
        settings.add(new Config.Setting(Setting.PORT, "55545", null, Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(Setting.LOG_LEVEL, "t", null, Config.SettingType.RAW, false, false));
        settings.add(new Config.Setting(Setting.CMD_PREFIX, "cmd:", null, Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(Setting.UPD_PREFIX, "update:", null, Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(Setting.RSP_PREFIX, "hello:", null, Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(Setting.PLUGIN_PATH, "./plugins", null, Config.SettingType.RAW, false, true));
        settings.add(new Config.Setting(Setting.EXCEPTION_PATH_PREFIX, "./exception_", null, Config.SettingType.RAW, false, false));
        settings.add(new Config.Setting(Setting.CRASH_PATH_PREFIX, "./exception_", null, Config.SettingType.RAW, false, false));
        settings.add(new Config.Setting(Setting.UI_VISIBILITY_COOLDOWN, "100", null, Config.SettingType.RAW, false, false));
    }

    private static void defineModifiableSettings() {
        settings.add(new Config.Setting(Setting.NAME, "Anonymous", "Name", Config.SettingType.NAME, true, false));
        settings.add(new Config.Setting(Setting.MINIMIZED, "true", "Start LANChat minimized", Config.SettingType.BOOLEAN, true, false));
        settings.add(new Config.Setting(Setting.AUTO_UPDATE, "true", "Get automatic updates", Config.SettingType.BOOLEAN, true, false));
        settings.add(new Config.Setting(Setting.PLUGINS_ENABLED, "false", "Use plugins", Config.SettingType.BOOLEAN, true, true));
    }

    public static List<Config.Setting> getAllSettings() {
        return settings;
    }
}
