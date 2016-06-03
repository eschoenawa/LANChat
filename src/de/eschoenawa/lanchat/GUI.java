package de.eschoenawa.lanchat;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class GUI extends JFrame implements UI {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblOnline;
	private JLabel lblChat;
	private JTextArea textArea;
	private JButton btnSend;
	private JTextField textField;
	private JScrollPane scrollPane;
	private Server server;
	private JTextArea textAreaOnline;
	private JScrollPane scrollPane_1;
	private TrayIcon trayIcon;
	private SystemTray tray;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					if (SystemTray.isSupported()) {
						frame.setVisible(false);
						frame.setExtendedState(JFrame.ICONIFIED);
					} else {
						frame.setVisible(true);
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
	public GUI() {
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(GUI.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.out.println("Sending goodbye...");
				server.stopResponse();
				tray.remove(trayIcon);
				try {
					server.sendToBroadcast(Config.load().getUpdatePrefix());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		setTitle("LANChat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 500);
		setResizable(true);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		this.contentPane.setLayout(null);
		this.setResizable(false);

		this.lblOnline = new JLabel("Online");
		this.lblOnline.setBounds(308, 11, 126, 14);
		this.contentPane.add(this.lblOnline);

		this.lblChat = new JLabel("Chat");
		this.lblChat.setBounds(10, 11, 46, 14);
		this.contentPane.add(this.lblChat);

		this.textArea = new JTextArea();
		textArea.setEditable(false);
		this.textArea.setBounds(10, 36, 282, 186);
		textArea.setWrapStyleWord(true);
		this.textArea.setLineWrap(true);
		this.contentPane.add(this.textArea);

		this.btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendText();
			}
		});
		this.btnSend.setBounds(203, 439, 89, 23);
		this.contentPane.add(this.btnSend);

		this.textField = new JTextField();
		this.textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sendText();
			}
		});
		this.textField.setBounds(10, 440, 183, 20);
		this.contentPane.add(this.textField);
		this.textField.setColumns(10);

		this.scrollPane = new JScrollPane(textArea);
		this.scrollPane.setBounds(10, 36, 282, 392);
		this.contentPane.add(this.scrollPane);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				discover();
			}
		});
		btnRefresh.setBounds(308, 438, 117, 25);
		contentPane.add(btnRefresh);

		textAreaOnline = new JTextArea();
		textAreaOnline.setEditable(false);
		textAreaOnline.setBounds(308, 37, 117, 183);
		contentPane.add(textAreaOnline);

		scrollPane_1 = new JScrollPane(textAreaOnline);
		scrollPane_1.setBounds(308, 37, 117, 390);
		contentPane.add(scrollPane_1);

		// Tray Icon
		if (SystemTray.isSupported()) {
			System.out.println("system tray supported");
			tray = SystemTray.getSystemTray();

			Image image = Toolkit.getDefaultToolkit()
					.getImage(GUI.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif"));
			ActionListener exitListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Exiting....");
					server.stopResponse();
					tray.remove(trayIcon);
					try {
						server.sendToBroadcast(Config.load().getUpdatePrefix());
					} catch (IOException io) {
						io.printStackTrace();
					}
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
			defaultItem = new MenuItem("Set nickname...");
			defaultItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setNick();
				}
			});
			popup.add(defaultItem);
			/*
			defaultItem = new MenuItem("(Un)Register at startup");
			defaultItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File f = new File(getautostart() + "\\LANChat.bat");
					if (!f.exists()) {
						String path = GUI.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						if (path.startsWith("/")) {
							path = path.substring(1);
						}
						String decodedPath = "";
						System.out.println(path);
						try {
							decodedPath = URLDecoder.decode(path, "UTF-8");
						} catch (UnsupportedEncodingException e2) {
							e2.printStackTrace();
						}
						try (FileWriter fw = new FileWriter(f, true);
								BufferedWriter bw = new BufferedWriter(fw);
								PrintWriter out = new PrintWriter(bw)) {
							out.println("javaw -Xmx200m -jar " + decodedPath);
							forceNotification("LANChat", "Registered autostart.");
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} else {
						f.delete();
						forceNotification("LANChat", "Unregistered autostart.");
					}
				}
			});
			popup.add(defaultItem);
			*/
			defaultItem = new MenuItem("Exit");
			defaultItem.addActionListener(exitListener);
			popup.add(defaultItem);
			trayIcon = new TrayIcon(image, "LANChat", popup);
			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(true);
					setExtendedState(JFrame.NORMAL);
				}
			});
			try {
				tray.add(trayIcon);
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
		} else {
			System.out.println("system tray not supported");
		}
		addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
				if (e.getNewState() == ICONIFIED) {
					setVisible(false);
					System.out.println("Unset visible");
				}
				if (e.getNewState() == 7) {
					setVisible(false);
					System.out.println("Unset visible");
				}
				if (e.getNewState() == MAXIMIZED_BOTH) {
					setVisible(true);
					System.out.println("Set visible");
				}
				if (e.getNewState() == NORMAL) {
					setVisible(true);
					System.out.println("Set visible");
				}
			}
		});
		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					setVisible(true);
					setExtendedState(JFrame.NORMAL);
				}
			}
		});
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(GUI.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));

		this.server = new Server(this);
		Thread t = new Thread(this.server);
		t.start();

		reloadHistory();
		discover();

	}

	public void println(String... text) {
		for (int i = 0; i < text.length; i++) {
			textArea.append(text[i] + "\n");
		}
	}

	public void receive(String text) {
		this.println(text);
		this.println();
		Chat.println(text);
		Chat.println("");
		notification("Received  Message", text);
	}

	public void reloadHistory() {
		this.textArea.setText("");
		Chat.load(this);
	}

	public void addValue(String value) {
		try {
			if (!(this.textAreaOnline.getText().contains(value))) {
				if (value.equals(Config.load().getName()))
					this.textAreaOnline.append(value + " (You)" + "\n");
				else
					this.textAreaOnline.append(value + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			this.textAreaOnline.setText("Config loading failed!");
		}
	}

	public void discover() {
		this.textAreaOnline.setText("");
		server.sendDiscoveryMessage();
	}

	public void sendText() {
		if (!this.textField.getText().equals("")) {
			try {
				String msg = Config.load().getName() + ": " + this.textField.getText();
				server.sendToBroadcast(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.textField.setText("");
			this.textField.requestFocusInWindow();
		}
	}

	public void notification(String title, String message) {
		if (!this.isVisible()) {
			trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
		}
	}

	public void forceNotification(String title, String message) {
		trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
	}

	public static String getautostart() {
		return System.getProperty("java.io.tmpdir").replace("Local\\Temp\\",
				"Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup");
	}

	public void setNick() {
		String newNick = JOptionPane.showInputDialog(this, "Please enter your new Nickname", "Enter nickname",
				JOptionPane.QUESTION_MESSAGE);
		if (newNick != null) {
			try {
				Config c = new Config(newNick, Config.load().getCommandPrefix(), Config.load().getResponsePrefix(),
						Config.load().getUpdatePrefix());
				c.export();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			discover();
		}
	}
}
