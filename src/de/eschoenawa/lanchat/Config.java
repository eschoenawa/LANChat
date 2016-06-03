package de.eschoenawa.lanchat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;

public class Config {

	public static String CONFIG_PATH = "./config.json";
	public static String DEFAULT_NAME = "Anonymous";
	public static String DEFAULT_COMMAND_PREFIX = "cmd:";
	public static String DEFAULT_RESPONSE_PREFIX = "hello:";
	public static String DEFAULT_UPDATE_PREFIX = "update:";
	
	private String name;
	private String commandPrefix;
	private String responsePrefix;
	private String updatePrefix;

	public Config() {
		
	}
	
	public Config(String name, String commandPrefix, String responsePrefix, String updatePrefix) {
		super();
		this.commandPrefix = commandPrefix;
		this.name = name;
		this.responsePrefix = responsePrefix;
		this.updatePrefix = updatePrefix;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getCommandPrefix() {
		return commandPrefix;
	}

	public void setCommandPrefix(String commandPrefix) {
		this.commandPrefix = commandPrefix;
	}

	public String getResponsePrefix() {
		return responsePrefix;
	}

	public void setResponsePrefix(String responsePrefix) {
		this.responsePrefix = responsePrefix;
	}

	public String getUpdatePrefix() {
		return updatePrefix;
	}

	public void setUpdatePrefix(String updatePrefix) {
		this.updatePrefix = updatePrefix;
	}

	public void export() throws FileNotFoundException, UnsupportedEncodingException {
		Gson gson = new Gson();
		PrintWriter writer = new PrintWriter(CONFIG_PATH);
		writer.print(gson.toJson(this));
		writer.close();
	}
	
	public static Config load() throws IOException {
		Gson gson = new Gson();
		File f = new File(CONFIG_PATH);
		if (!f.exists()) {
			PrintWriter writer = new PrintWriter(CONFIG_PATH);
			Config defaultConfig = new Config(DEFAULT_NAME, DEFAULT_COMMAND_PREFIX, DEFAULT_RESPONSE_PREFIX, DEFAULT_UPDATE_PREFIX);
			writer.print(gson.toJson(defaultConfig));
			writer.close();
		}
		BufferedReader br = new BufferedReader(new FileReader(CONFIG_PATH));
		String json = br.readLine();
		br.close();
		Config confg = gson.fromJson(json, Config.class);
		if (confg.getName().contains(" ")) {
			confg.setName(confg.getName().replace(" ", ""));
			confg.export();
		}
		if (confg.getName().length() > 20) {
			confg.setName(confg.getName().substring(0, 18) + "..");
			confg.export();
		}
		if (confg.getName().length() < 2) {
			confg.setName(DEFAULT_NAME);
			confg.export();
		}
		return confg;
	}

}
