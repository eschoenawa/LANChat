package de.eschoenawa.lanchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.eschoenawa.lanchat.ErrorHandler;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LanChatConfig {

    private static LanChatConfig CONFIG;

    private Map<String, String> values;

    private static HashMap<String, String> DEFAULTS = getDefaults();

    public static final String PLUGIN_FOLDER = "./plugins";
    public static final String EXCEPTION_PATH_PREFIX = "./exception_";
    public static final String CRASH_PATH_PREFIX = "./crash_";
    public static final String CRASHLOG_PATH_POSTFIX = ".log";
    public static final int VISIBILITY_COOLDOWN = 100;
    private static final String CONFIG_PATH = "./config.json";
    private static final String CONFIG_FORMAT = "GAMMA";
    private static final String DEFAULT_NAME = "Anonymous";
    private static final String DEFAULT_COMMAND_PREFIX = "cmd:";
    private static final String DEFAULT_RESPONSE_PREFIX = "hello:";
    private static final String DEFAULT_UPDATE_PREFIX = "update:";
    private static final String DEFAULT_AUTOUPDATE = "true";
    private static final String DEFAULT_MINIMIZED = "true";
    private static final String DEFAULT_PLUGINS = "false";

    private LanChatConfig(Map<String, String> values) {
        this.values = values;
    }

    private void setValue(String key, String value) {
        values.put(key, value);
    }

    private String getValue(String key) {
        return values.get(key);
    }

    private Map<String, String> getValues() {
        return values;
    }

    private void export() throws FileNotFoundException {
        Gson gson = new GsonBuilder().create();
        PrintWriter writer = new PrintWriter(CONFIG_PATH);
        String json = gson.toJson(this.getValues());
        writer.print(json);
        writer.close();
    }

    private static HashMap<String, String> getDefaults() {
        HashMap<String, String> m = new HashMap<>();
        m.put("config_format", CONFIG_FORMAT);
        m.put("name", DEFAULT_NAME);
        m.put("command_prefix", DEFAULT_COMMAND_PREFIX);
        m.put("response_prefix", DEFAULT_RESPONSE_PREFIX);
        m.put("update_prefix", DEFAULT_UPDATE_PREFIX);
        m.put("autoupdate", DEFAULT_AUTOUPDATE);
        m.put("minimized", DEFAULT_MINIMIZED);
        m.put("plugins", DEFAULT_PLUGINS);
        return m;
    }

    private static void load() throws IOException {
        Gson gson = new Gson();
        File f = new File(CONFIG_PATH);
        if (!f.exists()) {
            CONFIG = new LanChatConfig(DEFAULTS);
            CONFIG.export();
        }
        BufferedReader br = new BufferedReader(new FileReader(CONFIG_PATH));
        String json = br.readLine();
        br.close();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        LanChatConfig lanChatConfig = new LanChatConfig(gson.fromJson(json, type));
        if (lanChatConfig.getValue("config_format") == null) {
            HashMap<String, String> m = DeprecatedConfig.load().convertToNewFormat();
            lanChatConfig = new LanChatConfig(m);
        }
        if (lanChatConfig.getValue("name").contains(" ")) {
            lanChatConfig.setValue("name", lanChatConfig.getValue("name").replace(" ", ""));
        }
        if (lanChatConfig.getValue("name").length() > 20) {
            lanChatConfig.setValue("name", lanChatConfig.getValue("name").substring(0, 18) + "..");
        }
        if (lanChatConfig.getValue("name").length() < 2) {
            lanChatConfig.setValue("name", DEFAULTS.get("name"));
        }
        CONFIG = lanChatConfig;
        convertToCurrentFormat();
        CONFIG.export();
    }

    private static boolean convertToCurrentFormat() throws FileNotFoundException {
        if (CONFIG.getValue("config_format") != null) {
            if (!CONFIG.getValue("config_format").equals(DEFAULTS.get("config_format"))) {
                System.out.println("New Config format detected! Converting and filling missing values with defaults...");
                for (String k : DEFAULTS.keySet()) {
                    if (!CONFIG.getValues().containsKey(k)) {
                        CONFIG.setValue(k, DEFAULTS.get(k));
                    }
                }
                CONFIG.setValue("config_format", DEFAULTS.get("config_format"));
                CONFIG.export();
                System.out.println("Converted Config!");
                return true;
            }
            return false;
        } else {
            System.out.println("Config Format missing. Assuming Config version 'pre-ALPHA'. Filling in missing values with defaults...");
            for (String k : DEFAULTS.keySet()) {
                if (!CONFIG.getValues().containsKey(k)) {
                    CONFIG.setValue(k, DEFAULTS.get(k));
                }
            }
            CONFIG.export();
            System.out.println("Fixed Config!");
            return true;
        }
    }

    public static void set(String key, String value) throws IOException {
        if (CONFIG == null) {
            load();
        }
        CONFIG.setValue(key, value);
		CONFIG.export();
    }

    public static String get(String key) {
        if (CONFIG == null) {
            try {
                load();
            } catch (IOException e) {
                System.err.println("Failed to get, unable to load! Using defaults...");
                ErrorHandler.reportError(e, false);
                return DEFAULTS.get(key);
            }
        }
        return CONFIG.getValue(key);
    }
}
