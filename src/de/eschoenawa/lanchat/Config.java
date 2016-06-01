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
	
	private String name;

	public Config(String name) {
		super();
		this.name = name;
	}

	public Config() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
			Config defaultConfig = new Config(DEFAULT_NAME);
			writer.print(gson.toJson(defaultConfig));
			writer.close();
		}
		BufferedReader br = new BufferedReader(new FileReader(CONFIG_PATH));
		String json = br.readLine();
		br.close();
		return gson.fromJson(json, Config.class);
	}

}
