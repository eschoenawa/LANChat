package de.eschoenawa.lanchat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

public class Chat {

	private static File f = new File("./history.txt");

	public static void println(String text) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(f);
			pw.append(text + "\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void load(GUI ui) {
		if (f.exists()) {
			FileReader in;
			try {
				in = new FileReader(f);
				BufferedReader br = new BufferedReader(in);
				while (br.ready()) {
					ui.println(br.readLine());
				}
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
