package de.eschoenawa.lanchat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Config {
	private static Config CONFIG;

	private HashMap<String, String> values;

	private static HashMap<String, String> DEFAULTS = getDefaults();

	private static final String CONFIG_PATH = "./config.json";
	private static final String CONFIG_FORMAT = "ALPHA";
	private static final String DEFAULT_NAME = "Anonymous";
	private static final String DEFAULT_COMMAND_PREFIX = "cmd:";
	private static final String DEFAULT_RESPONSE_PREFIX = "hello:";
	private static final String DEFAULT_UPDATE_PREFIX = "update:";

	private Config(HashMap<String, String> values) {
		this.values = values;
	}

	private void setValue(String key, String value) {
		values.put(key, value);
	}

	private String getValue(String key) {
		return values.get(key);
	}

	private HashMap<String, String> getValues() {
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
		return m;
	}

	private static void load() throws IOException {
		Gson gson = new Gson();
		File f = new File(CONFIG_PATH);
		if (!f.exists()) {
			CONFIG = new Config(DEFAULTS);
			CONFIG.export();
		}
		BufferedReader br = new BufferedReader(new FileReader(CONFIG_PATH));
		String json = br.readLine();
		br.close();
		Type type = new TypeToken<Map<String, String>>(){}.getType();
		Config config = new Config(gson.fromJson(json, type));
		if (config.getValue("config_format") == null) {
			HashMap<String, String> m = DeprecatedConfig.load().convertToNewFormat();
			config = new Config(m);
		}
		if (config.getValue("name").contains(" ")) {
			config.setValue("name", config.getValue("name").replace(" ", ""));
		}
		if (config.getValue("name").length() > 20) {
			config.setValue("name", config.getValue("name").substring(0, 18) + "..");
		}
		if (config.getValue("name").length() < 2) {
			config.setValue("name", DEFAULTS.get("name"));
		}
		CONFIG = config;
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
				CONFIG.export();
				System.out.println("Converted Config!");
				return true;
			}
			return false;
		}
		else {
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

	public static void set(String key, String value) {
		if (CONFIG == null) {
			try {
				load();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Failed to set, unable to load!");
				return;
			}
		}
		CONFIG.setValue(key, value);
		try {
			CONFIG.export();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Failed to set, unable to export!");
		}
	}

	public static String get(String key) {
		if (CONFIG == null) {
			try {
				load();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Failed to get, unable to load! Using defaults...");
				return DEFAULTS.get(key);
			}
		}
		return CONFIG.getValue(key);
	}
}
