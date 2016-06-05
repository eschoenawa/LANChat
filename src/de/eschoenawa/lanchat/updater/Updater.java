package de.eschoenawa.lanchat.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.swing.JOptionPane;

public class Updater {

	public static String unset = "[not set]";
	public static String jarloc = "https://dl.dropboxusercontent.com/u/44998952/LANChat/LANChat.jar";
	public static String updloc = "https://dl.dropboxusercontent.com/u/44998952/LANChat/Updater.jar";
	public static String versionloc = "https://dl.dropboxusercontent.com/u/44998952/LANChat/version.txt";
	public static String updater = "Updater.jar";
	public static String jar = "LANChat.jar";

	private static String arg;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			arg = args[0];
		}
		try {
			Updater frame = new Updater();
			if (arg != null) {
				if (frame.updateMain()) {
					Runtime.getRuntime().exec("java -jar " + jar);
					System.exit(0);
				} else {
					JOptionPane.showMessageDialog(null, "Failed to download! Check internet connection!", "Error",
							JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}

			} else {
				frame.updateMain();
				Runtime.getRuntime().exec("java -jar " + jar);
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the frame.
	 */
	public Updater() {

	}

	public boolean updateUpdater() {
		File f = new File(updater);
		if (updloc.equals(unset)) {
			System.out.println("Unset Updater, looking locally...");
			return f.exists();
		} else {
			boolean b = download(updloc, updater);
			return b;
		}
	}

	public boolean updateMain() {
		File f = new File(jar);
		if (jarloc.equals(unset)) {
			System.out.println("Unset Updater, looking locally...");
			return f.exists();
		} else {
			boolean b = download(jarloc, jar);
			return f.exists() && b;
		}
	}

	public static boolean download(String url, String file) {
		try {
			URL website = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(file);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			return true;
		} catch (IOException e) {
			// JOptionPane.showMessageDialog(null, "Failed to download! Check
			// Internet connection!", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
}
