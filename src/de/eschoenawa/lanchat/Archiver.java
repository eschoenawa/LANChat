package de.eschoenawa.lanchat;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

public class Archiver {
	public static void archiveHistory() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
		File dir = new File("./archive/");
		dir.mkdirs();
		File f = new File("./archive/" + timeStamp + "_bak.txt");
		File history = new File("history.txt");
		if (!f.exists() && history.exists()) {
			InputStream inStream = null;
			OutputStream outStream = null;
				inStream = new FileInputStream(history);
				outStream = new FileOutputStream(f);

				byte[] buffer = new byte[1024];

				int length;
				// copy the file content in bytes
				while ((length = inStream.read(buffer)) > 0) {

					outStream.write(buffer, 0, length);

				}

				inStream.close();
				outStream.close();

				// delete the original file
				history.delete();

				JOptionPane.showMessageDialog(null, "Created history!", "Success", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(null, "Did not create history!", "Error", JOptionPane.ERROR_MESSAGE);
		}

	}
}
