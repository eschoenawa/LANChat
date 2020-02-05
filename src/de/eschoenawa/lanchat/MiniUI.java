package de.eschoenawa.lanchat;

import de.eschoenawa.lanchat.config.LanChatConfig;
import de.eschoenawa.lanchat.plugin.PluginManager;
import de.eschoenawa.lanchat.plugin.api.Plugin;
import de.eschoenawa.lanchat.updater.Updater;
import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;
import org.nibor.autolink.Span;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Scanner;

public class MiniUI extends JFrame implements UI {

    private static final long serialVersionUID = 1L;
    public static String version = "1.3.1_dev";
    private static String upd = "Newer version available (";
    private JPanel contentPane;
    private TrayIcon trayIcon;
    private SystemTray tray;
    private Server server;
    private JTabbedPane tabbedPane;
    private JPanel panelChat;
    private JPanel panelOnline;
    private JPanel panelPlugins;
    private JPanel panelPluginButtons;
    private final JPanel buttonPanel;
    private JTextField textField;
    private JScrollPane scrollPane;
    private JTextArea textArea;
    private JButton btnRefresh;
    private JScrollPane scrollPane_1;
    private JScrollPane scrollPane_2;
    private JTextPane textPane;
    private StyledDocument doc;
    private Style redStyle;
    private Style greenStyle;
    private Style normalStyle;
    private Style urlStyle;
    private MenuItem onlineItem;
    private boolean showNotification;
    private JLabel lblUpdate;
    private long lastVisibilityChangeTimestamp = 0;
    private PluginManager pluginManager;
    private java.util.List<Plugin.PluginButton> pluginButtons = new ArrayList<>();

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
                    if (Boolean.parseBoolean(LanChatConfig.get("minimized")))
                        frame.setVisible(false);
                } catch (Exception e) {
                    ErrorHandler.fatalCrash(e);
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
                if (System.currentTimeMillis() - lastVisibilityChangeTimestamp > LanChatConfig.VISIBILITY_COOLDOWN) {
                    lastVisibilityChangeTimestamp = System.currentTimeMillis();
                    MiniUI.this.setVisible(false);
                } else {
                    MiniUI.this.textField.requestFocus();
                    textPane.setCaretPosition(doc.getLength());
                }
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
        this.textPane.setEditorKit(new WrapEditorKit());
        this.textPane.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int position = textPane.viewToModel(e.getPoint());
                Element element = doc.getCharacterElement(position);
                if (element.getAttributes().containsAttribute("URL", true)) {
                    try {
                        String url = doc.getText(element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
                        System.out.println("Link to " + url + " clicked!");
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (BadLocationException | IOException | URISyntaxException ex) {
                        ErrorHandler.reportError(ex);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //noop
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //noop
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //noop
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //noop
            }
        });
        this.textPane.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int position = textPane.viewToModel(e.getPoint());
                Element element = doc.getCharacterElement(position);
                if (element.getAttributes().containsAttribute("URL", true)) {
                    textPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    textPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
        DefaultCaret caret = (DefaultCaret) textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        doc = textPane.getStyledDocument();

        //region Styles
        normalStyle = textPane.addStyle("NormalStyle", null);
        StyleConstants.setForeground(normalStyle, Color.WHITE);
        redStyle = textPane.addStyle("RedStyle", null);
        StyleConstants.setForeground(redStyle, Color.RED);
        greenStyle = textPane.addStyle("GreenStyle", null);
        StyleConstants.setForeground(greenStyle, Color.GREEN);
        urlStyle = textPane.addStyle("UrlStyle", null);
        StyleConstants.setForeground(urlStyle, Color.BLUE);
        StyleConstants.setUnderline(urlStyle, true);
        urlStyle.addAttribute("URL", true);
        //endregion

        this.scrollPane.setViewportView(this.textPane);

        panelOnline = new JPanel();
        this.panelOnline.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent arg0) {
                discover();
            }
        });
        this.panelOnline.setBackground(Color.DARK_GRAY);
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

        panelPlugins = new JPanel();
        this.panelPlugins.setBackground(Color.DARK_GRAY);
        if (Boolean.parseBoolean(LanChatConfig.get("plugins"))) {
            tabbedPane.addTab("Plugins", panelPlugins);
            this.tabbedPane.setToolTipTextAt(2, "Functions added by plugins");
            this.tabbedPane.setBackgroundAt(2, Color.DARK_GRAY);
            this.tabbedPane.setForegroundAt(2, Color.WHITE);
        }
        this.panelPlugins.setLayout(null);

        panelPluginButtons = new JPanel();
        panelPluginButtons.setBounds(0, 0, 425, 230);
        panelPluginButtons.setBackground(Color.GRAY);
        panelPluginButtons.setLayout(new BorderLayout());
        panelPlugins.add(panelPluginButtons);

        buttonPanel = new JPanel(new GridBagLayout());
        panelPluginButtons.add(buttonPanel, BorderLayout.PAGE_START);

        this.scrollPane_2 = new JScrollPane(panelPluginButtons);
        this.scrollPane_2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.scrollPane_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.scrollPane_2.setBounds(0, 0, 425, 231);
        this.scrollPane_2.getVerticalScrollBar().setUI(new DarkScrollbarUI());
        this.scrollPane_2.getHorizontalScrollBar().setUI(new DarkScrollbarUI());
        this.panelPlugins.add(this.scrollPane_2);

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
                    server.sendToBroadcast(LanChatConfig.get("update_prefix"), true);
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
            MenuItem pluginItem = new MenuItem();
            pluginItem.setLabel(Boolean.parseBoolean(LanChatConfig.get("plugins")) ? "Disable Plugins" : "Enable Plugins");
            pluginItem.addActionListener(e -> {
                try {
                    boolean newValue = !Boolean.parseBoolean(LanChatConfig.get("plugins"));
                    LanChatConfig.set("plugins", String.valueOf(newValue));
                    if (newValue) {
                        new Thread(() -> pluginManager.loadPlugins()).start();
                        pluginItem.setLabel("Disable Plugins");
                        tabbedPane.addTab("Plugins", panelPlugins);
                        this.tabbedPane.setToolTipTextAt(2, "Functions added by plugins");
                        this.tabbedPane.setBackgroundAt(2, Color.DARK_GRAY);
                        this.tabbedPane.setForegroundAt(2, Color.WHITE);
                    } else {
                        this.buttonPanel.removeAll();
                        this.tabbedPane.removeTabAt(2);
                        new Thread(() -> pluginManager.unloadAllPlugins()).start();
                        pluginItem.setLabel("Enable Plugins");
                    }
                } catch (IOException ex) {
                    ErrorHandler.reportError(ex, true);
                }
            });
            popup.add(pluginItem);
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
                    try {
                        Archiver.archiveHistory();
                        reloadHistory();
                    } catch (IOException ex) {
                        ErrorHandler.reportError(ex);
                    }
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
                    JOptionPane.showMessageDialog(MiniUI.this, "Version: " + version + "\nLANChat by eschoenawa (2016-2019)",
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
                System.err.println("Unable to add tray icon!");
                ErrorHandler.fatalCrash(e1);
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
                    if (System.currentTimeMillis() - lastVisibilityChangeTimestamp > LanChatConfig.VISIBILITY_COOLDOWN) {
                        setVisible(true);
                        toFront();
                        lastVisibilityChangeTimestamp = System.currentTimeMillis();
                        trayIcon.setImage(Toolkit.getDefaultToolkit()
                                .getImage(MiniUI.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
                    }
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
                    String msg = LanChatConfig.get("name") + " shouts" + ": " + submsg;
                    if (submsg != null)
                        sendMessage(msg);
                    else {
                        server.sendToBroadcast(LanChatConfig.get("update_prefix"), true);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        setIconImage(Toolkit.getDefaultToolkit()
                .getImage(MiniUI.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));

        pluginManager = new PluginManager(this);

        try {
            this.server = new Server(this, pluginManager);
        } catch (SocketException e) {
            ErrorHandler.fatalCrash(e);
        }
        Thread t = new Thread(this.server);
        t.start();

        reloadHistory();
        discover();

        showNotification = true;

        // replaced by auto-update at startup
        // updateCheck();

        // auto-update
        new Thread(() -> {
            if (Boolean.parseBoolean(LanChatConfig.get("autoupdate"))) {
                String s = getCurrentVersion();
                if (!(s.equals(version)) && !(version.endsWith("dev")) && !(version.endsWith("custom")) && !(s.equals("[unknown]"))) {
                    System.out.println("Newer version available, updating...");
                    update();
                } else {
                    System.out.println("No new Updates.");
                    updateCheck();
                }
            } else {
                updateCheck();
            }
        }).start();

        if (Boolean.parseBoolean(LanChatConfig.get("plugins"))) {
            new Thread(() -> pluginManager.loadPlugins()).start();
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
                server.sendToBroadcast(LanChatConfig.get("update_prefix"), true);
                try {
                    Runtime.getRuntime().exec("java -jar " + Updater.updater + " relaunch");
                    System.exit(0);
                } catch (IOException e1) {
                    ErrorHandler.fatalCrash(e1);
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
            String msg = LanChatConfig.get("name") + ": " + this.textField.getText();
            sendMessage(msg);
            this.textField.setText("");
            this.textField.requestFocusInWindow();
        }
    }

    public void sendMessage(String text) {
        server.sendToBroadcast(text, false);
    }

    private void reloadHistory() {
        try {
            this.textPane.setText("");
            Chat.load(this);
        } catch (IOException e) {
            ErrorHandler.reportError(e);
        }
    }

    @Override
    public void println(String... line) {
        for (int i = 0; i < line.length; i++) {
            if (!line[i].contains(":")) {
                System.out.println("Received invalid message: " + line[i]);
            } else {
                String[] split = line[i].split(":");
                if (split[0].contains(" ")) {
                    System.out.println("Received loud Message!");
                    printlnWithStyle(split, redStyle);
                } else {
                    printlnWithStyle(split, greenStyle);
                }
            }
            textPane.setCaretPosition(doc.getLength());
        }
    }

    private void printlnWithStyle(String[] split, Style descriptorStyle) {
        try {
            doc.insertString(doc.getLength(), split[0] + ":", descriptorStyle);
            StringBuilder messageBuilder = new StringBuilder();
            for (int j = 1; j < split.length; j++) {
                messageBuilder.append(split[j]);
                if (j + 1 < split.length) {
                    messageBuilder.append(":");
                }
            }
            String message = messageBuilder.toString();
            LinkExtractor linkExtractor = LinkExtractor.builder()
                    .linkTypes(EnumSet.of(LinkType.URL, LinkType.WWW))
                    .build();
            Iterable<Span> spans = linkExtractor.extractSpans(message);
            for (Span span : spans) {
                if (span instanceof LinkSpan) {
                    doc.insertString(doc.getLength(), message.substring(span.getBeginIndex(), span.getEndIndex()), urlStyle);
                } else {
                    doc.insertString(doc.getLength(), message.substring(span.getBeginIndex(), span.getEndIndex()), normalStyle);
                }
            }
            doc.insertString(doc.getLength(), "\n", normalStyle);
        } catch (BadLocationException ex) {
            ErrorHandler.reportError(ex);
        }
    }

    @Override
    public void addValue(String value, String ip) {
        if (!(this.textArea.getText().contains(value))) {
            if (value.equals(LanChatConfig.get("name") + " (v" + version + ")"))
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
        receive(received, true, true);
    }

    public void receive(String received, boolean addToHistory, boolean showNotification) {
        if (!this.isVisible()) {
            trayIcon.setImage(Toolkit.getDefaultToolkit()
                    .getImage(MiniUI.class.getResource("/javax/swing/plaf/metal/icons/ocean/warning.png")));
        }
        this.println(received);
        if (addToHistory) {
            try {
                Chat.println(received);
            } catch (IOException e) {
                ErrorHandler.reportError(e);
            }
        }
        if (showNotification) {
            String title = "Received Message";
            Color color = received.split(":")[0].contains(" ") ? Color.RED : Color.DARK_GRAY;
            StringBuilder hex = new StringBuilder(Integer.toHexString(color.getRGB()));
            while (hex.length() < 8) {
                hex.insert(0, "0");
            }
            color = pluginManager.getNotificationColor(title, received, hex.toString());
            notification("Received  Message", received, color);
        }
    }

    public void notification(String title, String message, Color color) {
        if (!this.isVisible() && showNotification) {
            // deprecated: trayIcon.displayMessage(title, message,
            // TrayIcon.MessageType.INFO);
            Notification.showNotification(message, title, this, color);
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
            ErrorHandler.reportError(e, false);
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
                    ErrorHandler.reportError(e);
                }
            }
        });
    }

    public void addPluginButtons(java.util.List<Plugin.PluginButton> pluginButtons) {
        this.pluginButtons.addAll(pluginButtons);

        for (Plugin.PluginButton pb : pluginButtons) {
            JButton btn = new JButton(pb.text);
            btn.setFont(new Font("Tahoma", Font.BOLD, 15));
            btn.setForeground(Color.WHITE);
            btn.addActionListener(ae -> pb.action.onButtonPress());
            btn.setBackground(Color.DARK_GRAY);
            btn.setMinimumSize(new Dimension(panelPluginButtons.getWidth(), 30));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;

            this.buttonPanel.add(btn, gbc);
        }
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
    }

    @Override
    public void showUI() {
        this.setVisible(true);
        this.toFront();
        trayIcon.setImage(Toolkit.getDefaultToolkit()
                .getImage(MiniUI.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
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

    //region Wrapping helper classes
    class WrapEditorKit extends StyledEditorKit {
        ViewFactory defaultFactory = new WrapColumnFactory();

        public ViewFactory getViewFactory() {
            return defaultFactory;
        }

    }

    class WrapColumnFactory implements ViewFactory {
        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                if (kind.equals(AbstractDocument.ContentElementName)) {
                    return new WrapLabelView(elem);
                } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                    return new ParagraphView(elem);
                } else if (kind.equals(AbstractDocument.SectionElementName)) {
                    return new BoxView(elem, View.Y_AXIS);
                } else if (kind.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                } else if (kind.equals(StyleConstants.IconElementName)) {
                    return new IconView(elem);
                }
            }

            // default to text display
            return new LabelView(elem);
        }
    }

    class WrapLabelView extends LabelView {
        public WrapLabelView(Element elem) {
            super(elem);
        }

        public float getMinimumSpan(int axis) {
            switch (axis) {
                case View.X_AXIS:
                    return 0;
                case View.Y_AXIS:
                    return super.getMinimumSpan(axis);
                default:
                    throw new IllegalArgumentException("Invalid axis: " + axis);
            }
        }

    }
    //endregion
}
