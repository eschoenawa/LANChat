package de.eschoenawa.lanchat.updater;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Updater extends JFrame {

	private static final long serialVersionUID = 1L;
	public static String unset = "[not set]";
	public static String jarloc = "https://dl.dropboxusercontent.com/u/44998952/LANChat/LANChat.jar";
	public static String updloc = unset;
	public static String updater = "Updater.jar";
	public static String jar = "LANChat.jar";

	private static String arg;
	private JPanel contentPane;
	private JLabel lblPleaseWaitWhile;
	private JLabel lblDownloadFrom;
	private JLabel lblDownloadMainFrom;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			arg = args[0];
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Updater frame = new Updater();
					frame.setVisible(true);
					if (arg != null) {
						if (frame.updateMain()) {
							if (arg.equals("relaunch")) {
								Runtime.getRuntime().exec("javaw " + jar);
								System.exit(0);
							}
							else {
								JOptionPane.showMessageDialog(null, "Failed to download! Check internet connection!", "Error", JOptionPane.ERROR_MESSAGE);
								System.exit(1);
							}
						}
						
					}
					else {
						frame.updateMain();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Updater() {
		setTitle("Downloading update...");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 359, 120);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		this.contentPane.setLayout(null);
		
		this.lblPleaseWaitWhile = new JLabel("Please wait while LANChat downloads the most recent version...");
		this.lblPleaseWaitWhile.setBounds(10, 11, 333, 14);
		this.contentPane.add(this.lblPleaseWaitWhile);
		
		this.lblDownloadFrom = new JLabel("Download Updater from: " + updloc);
		this.lblDownloadFrom.setBounds(10, 36, 333, 14);
		this.contentPane.add(this.lblDownloadFrom);
		
		this.lblDownloadMainFrom = new JLabel("Download Main from: " + jarloc);
		this.lblDownloadMainFrom.setBounds(10, 61, 333, 14);
		this.contentPane.add(this.lblDownloadMainFrom);
	}
	
	public boolean updateUpdater() {
		File f = new File(updater);
		if (updloc.equals(unset)) {
			System.out.println("Unset Updater, looking locally...");
			return f.exists();
		}
		else {
			boolean b = download(updloc, updater);
			return f.exists() && b;
		}
	}
	
	public boolean updateMain() {
		File f = new File(jar);
		if (jarloc.equals(unset)) {
			System.out.println("Unset Updater, looking locally...");
			return f.exists();
		}
		else {
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
			JOptionPane.showMessageDialog(null, "Failed to download! Check Internet connection!", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
}
