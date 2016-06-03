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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
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

public class MiniUI extends JFrame implements UI {

	private static final long serialVersionUID = 1L;
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

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
		this.panelChat.setForeground(Color.BLACK);
		this.panelChat.setBackground(Color.DARK_GRAY);
		// panelChat.add();
		tabbedPane.addTab("Chat", panelChat);
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
		this.textPane.setFont(new Font("Tahoma", Font.BOLD, 14));
		this.textPane.setEditable(false);
		this.textPane.setBackground(Color.GRAY);
		DefaultCaret caret = (DefaultCaret) textPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		doc = textPane.getStyledDocument();

		style = textPane.addStyle("Style", null);
		StyleConstants.setForeground(style, Color.WHITE);
		this.scrollPane.setViewportView(this.textPane);

		panelOnline = new JPanel();
		this.panelOnline.setBackground(Color.DARK_GRAY);
		// panel2.add(new JButton("Button des zweiten Tabs"));
		tabbedPane.addTab("Online", panelOnline);
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
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(GUI.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));

		this.server = new Server(this);
		Thread t = new Thread(this.server);
		t.start();

		reloadHistory();
		discover();
	}

	protected void sendText() {
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

	private void reloadHistory() {
		this.textPane.setText("");
		Chat.load(this);
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

	@Override
	public void println(String... line) {
		for (int i = 0; i < line.length; i++) {
			String[] split = line[i].split(":");
			try {
				StyleConstants.setForeground(style, Color.GREEN);
				doc.insertString(doc.getLength(), split[0] + ":", style);
				StyleConstants.setForeground(style, Color.WHITE);
				doc.insertString(doc.getLength(), split[1] + "\n", style);
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void addValue(String value) {
		try {
			if (!(this.textArea.getText().contains(value))) {
				if (value.equals(Config.load().getName()))
					this.textArea.append("- " + value + " (You)" + "\n");
				else
					this.textArea.append("- " + value + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			this.textArea.setText("Config loading failed!");
		}
	}

	@Override
	public void discover() {
		this.textArea.setText("");
		server.sendDiscoveryMessage();
	}

	@Override
	public void receive(String received) {
		this.println(received);
		Chat.println(received);
		notification("Received  Message", received);
	}

	private void notification(String title, String message) {
		if (!this.isVisible()) {
			trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
		}
	}
}
