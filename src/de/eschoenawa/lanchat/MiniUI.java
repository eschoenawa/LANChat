package de.eschoenawa.lanchat;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import de.eschoenawa.lanchat.updater.Updater;

public class MiniUI extends JFrame implements UI {

	private static final long serialVersionUID = 1L;
	public static String version = "1.07_dev";
	private static String upd = "Newer version available (";
	private JPanel contentPane;
	private TrayIcon trayIcon;
	private SystemTray tray;
	private Server server;
	private JTabbedPane tabbedPane;
	private JPanel panelChat;
	private JPanel panelOnline;
	private JTextField textField;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JButton btnRefresh;
	private JScrollPane scrollPane_1;
	private JTextPane textPane;
	private StyledDocument doc;
	private Style style;
	private MenuItem onlineItem;
	private boolean showNotification;
	private JLabel lblUpdate;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		File f = new File(Updater.updater);
		if (f.exists())
			f.delete();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MiniUI frame = new MiniUI();
					frame.setVisible(true);
					GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
					Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
					int x = (int) rect.getMaxX() - frame.getWidth();
					int y = (int) rect.getMaxY() - frame.getHeight();
					frame.setLocation(x, y);
					//Previously set to true, but this zigzag is required for proper downscoll.
					if (Boolean.parseBoolean(Config.get("minimized")))
						frame.setVisible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MiniUI() {
		UIManager.put("TabbedPane.contentAreaColor ", ColorUIResource.BLACK);
		UIManager.put("TabbedPane.selected", ColorUIResource.GRAY);
		UIManager.put("TabbedPane.background", ColorUIResource.BLACK);
		UIManager.put("TabbedPane.shadow", ColorUIResource.DARK_GRAY);
		UIManager.put("TabbedPane.darkShadow", ColorUIResource.BLACK);
		UIManager.put("TabbedPane.light", ColorUIResource.GRAY);
		UIManager.put("TabbedPane.highlight", ColorUIResource.GRAY);
		UIManager.put("TabbedPane.focus", ColorUIResource.BLACK);
		UIManager.put("TabbedPane.selectHighlight", ColorUIResource.GRAY);
		UIManager.put("TabbedPane.tabAreaBackground", ColorUIResource.BLACK);
		UIManager.put("TabbedPane.borderHightlightColor", ColorUIResource.BLACK);

		UIManager.put("TabbedPane.contentBorderInsets", ColorUIResource.BLACK);
		UIManager.put("TabbedPane.foreground", ColorUIResource.BLACK);
		addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent arg0) {
				MiniUI.this.textField.requestFocus();
				textPane.setCaretPosition(doc.getLength());
			}

			public void windowLostFocus(WindowEvent arg0) {
				System.out.println("Focus lost!");
				MiniUI.this.setVisible(false);
			}
		});
		setUndecorated(true);
		setOpacity(0.95f);
		setResizable(false);
		setTitle("LANChat");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 350);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				MiniUI.this.setVisible(false);
			}
		});
		this.contentPane = new JPanel();
		this.contentPane.setForeground(Color.GREEN);
		this.contentPane.setBackground(Color.BLACK);
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		this.contentPane.setLayout(null);

		this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.tabbedPane.setBackground(Color.DARK_GRAY);
		this.tabbedPane.setBounds(10, 11, 430, 290);

		panelChat = new JPanel();
		this.panelChat.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				MiniUI.this.textField.requestFocus();
			}
		});
		this.panelChat.setForeground(Color.BLACK);
		this.panelChat.setBackground(Color.DARK_GRAY);
		// panelChat.add();
		tabbedPane.addTab("Chat", panelChat);
		this.tabbedPane.setToolTipTextAt(0, "Communicate to own subnets broadcast.");
		this.panelChat.setLayout(null);

		this.textField = new JTextField();
		this.textField.setFont(new Font("Tahoma", Font.BOLD, 12));
		this.textField.setForeground(Color.WHITE);
		this.textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendText();
			}
		});
		this.textField.setBackground(Color.GRAY);
		this.textField.setBounds(0, 242, 425, 20);
		this.panelChat.add(this.textField);
		this.textField.setColumns(10);

		this.scrollPane = new JScrollPane();
		this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPane.setBounds(0, 0, 425, 231);
		this.scrollPane.getVerticalScrollBar().setUI(new DarkScrollbarUI());
		this.scrollPane.getHorizontalScrollBar().setUI(new DarkScrollbarUI());
		this.panelChat.add(this.scrollPane);

		this.textPane = new JTextPane();
		this.textPane.setEditable(false);
		this.textPane.setFont(new Font("Tahoma", Font.BOLD, 14));
		this.textPane.setBackground(Color.GRAY);
		DefaultCaret caret = (DefaultCaret) textPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		doc = textPane.getStyledDocument();

		style = textPane.addStyle("Style", null);
		StyleConstants.setForeground(style, Color.WHITE);
		this.scrollPane.setViewportView(this.textPane);

		panelOnline = new JPanel();
		this.panelOnline.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				discover();
			}
		});
		this.panelOnline.setBackground(Color.DARK_GRAY);
		// panel2.add(new JButton("Button des zweiten Tabs"));
		tabbedPane.addTab("Online", panelOnline);
		this.tabbedPane.setToolTipTextAt(1, "Show who is online");
		this.panelOnline.setLayout(null);

		this.textArea = new JTextArea();
		this.textArea.setFont(new Font("Tahoma", Font.BOLD, 16));
		this.textArea.setEditable(false);
		this.textArea.setBackground(Color.GRAY);
		this.textArea.setBounds(0, 0, 425, 230);
		this.panelOnline.add(this.textArea);

		this.btnRefresh = new JButton("");
		this.btnRefresh
		.setIcon(new ImageIcon(MiniUI.class.getResource("/com/sun/javafx/scene/web/skin/Redo_16x16_JFX.png")));
		this.btnRefresh.setFont(new Font("Tahoma", Font.BOLD, 15));
		this.btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				discover();
			}
		});
		this.btnRefresh.setBackground(Color.DARK_GRAY);
		this.btnRefresh.setBounds(0, 228, 425, 34);
		this.panelOnline.add(this.btnRefresh);

		this.scrollPane_1 = new JScrollPane(textArea);
		this.scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.scrollPane_1.setBounds(0, 0, 425, 231);
		this.scrollPane_1.getVerticalScrollBar().setUI(new DarkScrollbarUI());
		this.scrollPane_1.getHorizontalScrollBar().setUI(new DarkScrollbarUI());
		this.panelOnline.add(this.scrollPane_1);

		this.tabbedPane.setBackgroundAt(0, Color.DARK_GRAY);
		this.tabbedPane.setBackgroundAt(1, Color.DARK_GRAY);
		this.tabbedPane.setForegroundAt(0, Color.WHITE);
		this.tabbedPane.setForegroundAt(1, Color.WHITE);
		this.contentPane.add(this.tabbedPane);

		this.lblUpdate = new JLabel("");
		this.lblUpdate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (lblUpdate.getText().startsWith(upd)) {
					System.out.println("Updating!");
					update();
				}
			}
		});
		this.lblUpdate.setFont(new Font("Tahoma", Font.BOLD, 11));
		this.lblUpdate.setForeground(Color.GREEN);
		this.lblUpdate.setBounds(140, 0, 310, 15);
		this.contentPane.add(this.lblUpdate);

		// Tray Icon
		if (SystemTray.isSupported()) {
			System.out.println("system tray supported");
			tray = SystemTray.getSystemTray();

			Image image = Toolkit.getDefaultToolkit()
					.getImage(MiniUI.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif"));
			ActionListener exitListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Exiting....");
					server.stopResponse();
					if (SystemTray.isSupported())
						tray.remove(trayIcon);
					server.sendToBroadcast(Config.get("update_prefix"));
					System.exit(0);
				}
			};
			PopupMenu popup = new PopupMenu();
			MenuItem defaultItem = new MenuItem("Open");
			defaultItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(true);
					setExtendedState(JFrame.NORMAL);
				}
			});
			popup.add(defaultItem);
			popup.addSeparator();
			onlineItem = new MenuItem("Hide Notifications");
			onlineItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setShowNotifications(!showNotification);
				}
			});
			popup.add(onlineItem);
			defaultItem = new MenuItem("Settings...");
			defaultItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					settings();
				}
			});
			popup.add(defaultItem);
			/*
			 * defaultItem = new MenuItem("(Un)Register at startup");
			 * defaultItem.addActionListener(new ActionListener() { public void
			 * actionPerformed(ActionEvent e) { File f = new File(getautostart()
			 * + "\\LANChat.bat"); if (!f.exists()) { String path =
			 * GUI.class.getProtectionDomain().getCodeSource().getLocation().
			 * getPath(); if (path.startsWith("/")) { path = path.substring(1);
			 * } String decodedPath = ""; System.out.println(path); try {
			 * decodedPath = URLDecoder.decode(path, "UTF-8"); } catch
			 * (UnsupportedEncodingException e2) { e2.printStackTrace(); } try
			 * (FileWriter fw = new FileWriter(f, true); BufferedWriter bw = new
			 * BufferedWriter(fw); PrintWriter out = new PrintWriter(bw)) {
			 * out.println("javaw -Xmx200m -jar " + decodedPath);
			 * forceNotification("LANChat", "Registered autostart."); } catch
			 * (Exception e1) { e1.printStackTrace(); } } else { f.delete();
			 * forceNotification("LANChat", "Unregistered autostart."); } } });
			 * popup.add(defaultItem);
			 */
			defaultItem = new MenuItem("Archive");
			defaultItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Archiver.archiveHistory();
					reloadHistory();
				}
			});
			popup.add(defaultItem);
			defaultItem = new MenuItem("Update");
			defaultItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					update();
				}
			});
			popup.add(defaultItem);
			popup.addSeparator();
			defaultItem = new MenuItem("About");
			defaultItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(MiniUI.this, "Version: " + version + "\nLANChat by eschoenawa (2016)",
							"About", JOptionPane.INFORMATION_MESSAGE);
				}
			});
			popup.add(defaultItem);
			defaultItem = new MenuItem("Exit");
			defaultItem.addActionListener(exitListener);
			popup.add(defaultItem);
			trayIcon = new TrayIcon(image, "LANChat", popup);
			trayIcon.setImageAutoSize(true);
			try {
				tray.add(trayIcon);
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
		} else {
			System.out.println("system tray not supported");
			JOptionPane.showMessageDialog(null,
					"WARNING: System Tray not supported on this System! LANChat only works on systems supporting the java.awt.SystemTray class.",
					"Fatal Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		// addWindowStateListener(new WindowStateListener() {
		// public void windowStateChanged(WindowEvent e) {
		// if (e.getNewState() == ICONIFIED) {
		// setVisible(false);
		// System.out.println("Unset visible");
		// }
		// if (e.getNewState() == 7) {
		// setVisible(false);
		// System.out.println("Unset visible");
		// }
		// if (e.getNewState() == MAXIMIZED_BOTH) {
		// setVisible(true);
		// System.out.println("Set visible");
		// }
		// if (e.getNewState() == NORMAL) {
		// setVisible(true);
		// System.out.println("Set visible");
		// }
		// }
		// });
		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
					System.out.println("Fired Click!");
					setVisible(true);
					toFront();
				}
			}
		});
		textField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isAltDown() && e.isControlDown() && e.isShiftDown()) {
					System.out.println("Shout!");
					String submsg = JOptionPane.showInputDialog(null, "Enter message:", "Shout",
							JOptionPane.INFORMATION_MESSAGE);
					String msg = Config.get("name") + " shouts" + ": " + submsg;
					if (submsg != null)
						server.sendToBroadcast(msg);
					else {
						server.sendToBroadcast(Config.get("update_prefix"));
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(MiniUI.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));

		this.server = new Server(this);
		Thread t = new Thread(this.server);
		t.start();

		reloadHistory();
		discover();

		showNotification = true;

		// replaced by auto-update at startup
		// updateCheck();

		// auto-update
		if (Boolean.parseBoolean(Config.get("autoupdate"))) {
			String s = getCurrentVersion();
			if (!(s.equals(version)) && !(version.endsWith("dev")) && !(version.endsWith("custom")) && !(s.equals("[unknown]"))) {
				System.out.println("Newer version available, updating...");
				update();
			} else {
				System.out.println("No new Updates.");
				updateCheck();
			}
		}
		else {
			updateCheck();
		}
	}

	protected void update() {
		boolean b = false;
		if (version.endsWith("dev") || version.endsWith("custom")) {
			int i = JOptionPane.showConfirmDialog(MiniUI.this,
					"Updating will overwrite your custom version with the normal one. Are you sure?",
					"Custom or development Version detected!", JOptionPane.YES_NO_OPTION);
			b = (i == 0);
		} else
			b = true;
		if (b) {
			Updater frame = new Updater();
			if (frame.updateUpdater()) {
				System.out.println("Exiting....");
				server.stopResponse();
				if (SystemTray.isSupported())
					tray.remove(trayIcon);
				server.sendToBroadcast(Config.get("update_prefix"));
				try {
					Runtime.getRuntime().exec("java -jar " + Updater.updater + " relaunch");
					System.exit(0);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				frame = null;
				JOptionPane.showMessageDialog(null, "Failed to download new update! Check internet connection!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	protected void sendText() {
		if (!this.textField.getText().equals("")) {
			String msg = Config.get("name") + ": " + this.textField.getText();
			server.sendToBroadcast(msg);
			this.textField.setText("");
			this.textField.requestFocusInWindow();
		}
	}

	private void reloadHistory() {
		this.textPane.setText("");
		Chat.load(this);
	}

	public void setNick() {
		String newNick = JOptionPane.showInputDialog(this, "Please enter your new Nickname", "Enter nickname",
				JOptionPane.QUESTION_MESSAGE);
		if (newNick != null && newNick != "" && newNick.length() > 1 && newNick.length() <= 20
				&& !(newNick.contains(" "))) {
			Config.set("name", newNick);
			discover();
		} else {
			JOptionPane.showMessageDialog(this, "Nick not set.", "Info", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@Override
	public void println(String... line) {
		for (int i = 0; i < line.length; i++) {
			if (!line[i].contains(":")) {
				System.out.println("Recieved invalid message: " + line[i]);
			} else {
				String[] split = line[i].split(":");
				if (split[0].contains(" ")) {
					System.out.println("Received loud Message!");
					try {
						StyleConstants.setForeground(style, Color.RED);
						doc.insertString(doc.getLength(), split[0] + ":", style);
						StyleConstants.setForeground(style, Color.WHITE);
						for (int j = 1; j < split.length; j++) {
							doc.insertString(doc.getLength(), split[j], style);
							if (j + 1 < split.length) {
								doc.insertString(doc.getLength(), ":", style);
							}
						}
						doc.insertString(doc.getLength(), "\n", style);
					} catch (BadLocationException ex) {
						ex.printStackTrace();
					}
				} else {
					try {
						StyleConstants.setForeground(style, Color.GREEN);
						doc.insertString(doc.getLength(), split[0] + ":", style);
						StyleConstants.setForeground(style, Color.WHITE);
						for (int j = 1; j < split.length; j++) {
							doc.insertString(doc.getLength(), split[j], style);
							if (j + 1 < split.length) {
								doc.insertString(doc.getLength(), ":", style);
							}
						}
						doc.insertString(doc.getLength(), "\n", style);
					} catch (BadLocationException ex) {
						ex.printStackTrace();
					}
				}
			}
			textPane.setCaretPosition(doc.getLength());
		}
	}

	@Override
	public void addValue(String value, String ip) {
		if (!(this.textArea.getText().contains(value))) {
			if (value.equals(Config.get("name") + " (v" + version + ")"))
				this.textArea.append("- " + value + " (You)" + "\n");
			else
				this.textArea.append("- " + value + "\n");
		}
	}

	@Override
	public void discover() {
		this.textArea.setText("");
		server.sendDiscoveryMessage();
		this.updateCheck();
	}

	@Override
	public void receive(String received) {
		this.println(received);
		Chat.println(received);
		notification("Received  Message", received, received.split(":")[0].contains(" "));
	}

	private void notification(String title, String message, boolean red) {
		if (!this.isVisible() && showNotification) {
			// deprecated: trayIcon.displayMessage(title, message,
			// TrayIcon.MessageType.INFO);
			Notification.showNotification(message, title, this, red);
		}
	}

	public static String getCurrentVersion() {
		try {
			URL url = new URL(Updater.versionloc);
			InputStream is = url.openStream();
			// For some reason this suppress is required.
			@SuppressWarnings("resource")
			Scanner s = new Scanner(is).useDelimiter("\\A");
			String result = s.hasNext() ? s.next() : "";
			System.out.println("Current version: " + version);
			System.out.println("New version: " + result);
			s.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return "[unknown]";
		}
	}

	public void updateCheck() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String s = getCurrentVersion();
				if (version.endsWith("dev")) {
					lblUpdate.setText("Development Version");
					lblUpdate.setForeground(Color.YELLOW);
				} else if (version.endsWith("custom")) {
					lblUpdate.setText("Custom Version");
					lblUpdate.setForeground(Color.DARK_GRAY);
				} else if (!version.equals(s)) {
					if (s.equals("[unknown]")) {
						lblUpdate.setText("No internet connection!");
						lblUpdate.setForeground(Color.DARK_GRAY);
					} else
						lblUpdate.setText(upd + s + ") Click to update!");
				}
			}

		}).start();
	}
	
	public void settings() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Settings frame = new Settings(MiniUI.this);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void showUI() {
		this.setVisible(true);
		this.toFront();
	}

	@Override
	public boolean isShown() {
		return this.isVisible();
	}

	@Override
	public void setShowNotifications(boolean show) {
		this.showNotification = show;
		if (showNotification) {
			onlineItem.setLabel("Hide Notifications");
		} else {
			onlineItem.setLabel("Show Notifications");
		}
	}

	@Override
	public boolean areNotificationsShown() {
		return this.showNotification;
	}
}
