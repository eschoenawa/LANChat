package de.eschoenawa.lanchat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Chat {

	private static File f = new File("./history.txt");

	public static void println(String text) {
		try(FileWriter fw = new FileWriter(f, true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println(text);
			} catch (IOException e) {
			    e.printStackTrace();
			}
	}

	public static void load(GUI ui) {
		if (f.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			    String line;
			    while ((line = br.readLine()) != null) {
			       ui.println(line);
			    }
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
