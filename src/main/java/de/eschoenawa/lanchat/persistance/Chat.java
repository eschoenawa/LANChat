package de.eschoenawa.lanchat.persistance;

import de.eschoenawa.lanchat.util.ErrorHandler;
import de.eschoenawa.lanchat.util.Log;

import java.io.*;

public class Chat {

	//TODO replace with mockable solution (without error handler & with singleton instance)
	private static File f;
	private static final String SEPARATOR = ":";
	private static final String SHOUTED_PREFIX = "S";
	private static final String NORMAL_PREFIX = "N";
	private static final String TAG = "PERSIST";

	public static void init(String filePath) {
		f = new File(filePath);
	}

	public static void println(String sender, String message, boolean shouted) {
		String text = (shouted ? SHOUTED_PREFIX : NORMAL_PREFIX) + SEPARATOR + sender + SEPARATOR + message;
		try(FileWriter fw = new FileWriter(f, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println(text);
		} catch (IOException ioe) {
			Log.e(TAG, "IOException while writing to history file (" + ioe.getMessage() + ")!");
			throw new IllegalStateException("The provided history path can't be loaded properly!", ioe);
		}
	}

	public static void load(ChatLoaderCallback callback) {
		if (f.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {
				String line;
				boolean loadingError = false;
				String erroneousLine = "";
				while ((line = br.readLine()) != null) {
					String[] components = line.split(SEPARATOR);
					if (!(line.isEmpty()) && components.length < 3) {
						erroneousLine = line;
						loadingError = true;
					}
					StringBuilder messageBuilder = new StringBuilder();
					for (int i = 2; i < components.length; i++) {
						messageBuilder.append(components[i]);
						if (i + 1 < components.length) {
							messageBuilder.append(SEPARATOR);
						}
					}
					callback.println(components[1], messageBuilder.toString(), components[0].equals(SHOUTED_PREFIX));
				}
				if (loadingError) {
					ErrorHandler.showErrorDialog("A line failed to load from the history file:\n\"" + erroneousLine + "\"\nTry deleting the file to recreate it from scratch.");
				}
			} catch (IOException ioe) {
				Log.e(TAG, "IOException while loading from history file (" + ioe.getMessage() + ")!");
				throw new IllegalStateException("The provided history path can't be loaded properly!", ioe);
			}
		}
	}

	public interface ChatLoaderCallback {
		void println(String sender, String message, boolean shouted);
	}
}
