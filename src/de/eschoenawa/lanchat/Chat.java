package de.eschoenawa.lanchat;

import java.io.*;

public class Chat {

	private static File f = new File("./history.txt");

	public static void println(String text) throws IOException {
		try(FileWriter fw = new FileWriter(f, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println(text);
		}
	}

	public static void load(UI ui) throws IOException {
		if (f.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {
				String line;
				while ((line = br.readLine()) != null) {
					ui.println(line);
				}
			}
		}
	}
}
