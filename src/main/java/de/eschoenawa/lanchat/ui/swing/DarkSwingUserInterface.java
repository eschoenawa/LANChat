package de.eschoenawa.lanchat.ui.swing;

import de.eschoenawa.lanchat.ui.UserInterface;
import de.eschoenawa.lanchat.util.ErrorHandler;
import de.eschoenawa.lanchat.util.Log;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class DarkSwingUserInterface extends JFrame implements UserInterface {

    private static final String TAG = "DSUI";
    private static final String URL_ATTRIBUTE = "URL";

    private Image windowImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/computer.gif"));
    private Image refreshImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/refresh.png"));

    private JPanel contentPane;
    private JTabbedPane tabbedPane;
    private JPanel panelOnline;
    private JTextArea txtOnline;
    private JButton btnRefreshOnline;
    private JScrollPane scrollOnline;
    private JLabel lblUpdateInfo;

    //region chat panel
    private JPanel panelChat;
    private JScrollPane scrollChat;
    private JTextPane txtChat;
    private StyledDocument docChat;
    private JTextField txtSend;
    //endregion

    //region styles
    private Style redStyle;
    private Style greenStyle;
    private Style normalStyle;
    private Style urlStyle;
    //endregion

    //TODO remove main
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            DarkSwingUserInterface frame = new DarkSwingUserInterface();
            frame.setVisible(true);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
            Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
            int x = (int) rect.getMaxX() - frame.getWidth();
            int y = (int) rect.getMaxY() - frame.getHeight();
            frame.setLocation(x, y);
        });
    }

    public DarkSwingUserInterface() {
        initWindow();
        initChatTab();
        initOnlineTab();
        afterInit();
    }

    //region Initialization

    private void initWindow() {
        initUiColors();
        initFocusListener();
        initJFrameParameters();
        initContentPane();
        initTabbedPane();
        initUpdateInfoLabel();
    }

    private void initChatTab() {
        initChatPanel();
        initChatField();
        initChatStyles();
        initTxtSend();
    }

    private void initOnlineTab() {
        initOnlinePanel();
        initOnlineField();
        initOnlineRefreshButton();
    }

    private void afterInit() {
        initTabBackgrounds();
    }

    private void initUiColors() {
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
        UIManager.put("TabbedPane.borderHighlightColor", ColorUIResource.BLACK);
        UIManager.put("TabbedPane.contentBorderInsets", ColorUIResource.BLACK);
        UIManager.put("TabbedPane.foreground", ColorUIResource.BLACK);
    }

    private void initFocusListener() {
        addWindowFocusListener(new WindowFocusListener() {
            public void windowGainedFocus(WindowEvent arg0) {
                DarkSwingUserInterface.this.txtSend.requestFocus();     //TODO what if other tab is open?
                txtChat.setCaretPosition(docChat.getLength());
            }

            public void windowLostFocus(WindowEvent arg0) {
                //TODO hide UI, maybe visibility cooldown required?
            }
        });
    }

    private void initJFrameParameters() {
        setUndecorated(true);
        setOpacity(0.95f);
        setResizable(false);
        setTitle("LANChat");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(100, 100, 450, 350);
        setIconImage(windowImage);
    }

    //TODO window listener for window closing required?

    private void initContentPane() {
        this.contentPane = new JPanel();
        this.contentPane.setForeground(Color.GREEN);
        this.contentPane.setBackground(Color.BLACK);
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.contentPane.setLayout(null);   //TODO no layout?
        setContentPane(this.contentPane);
    }

    private void initTabbedPane() {
        this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        this.tabbedPane.setBackground(Color.DARK_GRAY);
        this.tabbedPane.setBounds(10, 11, 430, 290);
        this.contentPane.add(this.tabbedPane);
    }

    private void initUpdateInfoLabel() {
        this.lblUpdateInfo = new JLabel("");
        this.lblUpdateInfo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                //TODO start updating
            }
        });
        this.lblUpdateInfo.setFont(new Font("Tahoma", Font.BOLD, 11));
        this.lblUpdateInfo.setForeground(Color.GREEN);
        this.lblUpdateInfo.setBounds(140, 0, 310, 15);
        this.contentPane.add(this.lblUpdateInfo);
    }

    private void initChatPanel() {
        this.panelChat = new JPanel();
        this.panelChat.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent arg0) {
                DarkSwingUserInterface.this.txtSend.requestFocus();     //TODO is this required?
            }
        });
        this.panelChat.setForeground(Color.BLACK);
        this.panelChat.setBackground(Color.DARK_GRAY);
        this.panelChat.setLayout(null);     //TODO no layout?
        this.tabbedPane.addTab("Chat", panelChat);
        this.tabbedPane.setToolTipTextAt(0, "Communicate to broadcast address of connected networks.");
    }

    private void initChatField() {
        this.scrollChat = new JScrollPane();    //TODO why here not the view but with online view I have to?
        this.scrollChat.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.scrollChat.setBounds(0, 0, 425, 231);
        this.scrollChat.getVerticalScrollBar().setUI(new DarkScrollbarUi());
        this.scrollChat.getHorizontalScrollBar().setUI(new DarkScrollbarUi());
        this.panelChat.add(this.scrollChat);

        this.txtChat = new JTextPane();
        this.txtChat.setEditable(false);
        this.txtChat.setFont(new Font("Tahoma", Font.BOLD, 14));
        this.txtChat.setBackground(Color.GRAY);
        this.txtChat.setEditorKit(new WrapEditorKit());
        this.txtChat.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int position = txtChat.viewToModel(e.getPoint());
                Element element = docChat.getCharacterElement(position);
                if (element.getAttributes().containsAttribute(URL_ATTRIBUTE, true)) {
                    try {
                        String url = docChat.getText(element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
                        Log.d(TAG, "Link to " + url + " clicked!");
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
        this.txtChat.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int position = txtChat.viewToModel(e.getPoint());       //TODO deprecated but newer version may be incompatible
                Element element = docChat.getCharacterElement(position);
                if (element.getAttributes().containsAttribute(URL_ATTRIBUTE, true)) {
                    txtChat.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    txtChat.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
        DefaultCaret caret = (DefaultCaret) this.txtChat.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);      //TODO do I need this?
        this.docChat = this.txtChat.getStyledDocument();
        this.scrollChat.setViewportView(this.txtChat);
    }

    private void initChatStyles() {
        normalStyle = txtChat.addStyle("NormalStyle", null);
        StyleConstants.setForeground(normalStyle, Color.WHITE);
        redStyle = txtChat.addStyle("RedStyle", null);
        StyleConstants.setForeground(redStyle, Color.RED);
        greenStyle = txtChat.addStyle("GreenStyle", null);
        StyleConstants.setForeground(greenStyle, Color.GREEN);
        urlStyle = txtChat.addStyle("UrlStyle", null);
        StyleConstants.setForeground(urlStyle, Color.BLUE);
        StyleConstants.setUnderline(urlStyle, true);
        urlStyle.addAttribute(URL_ATTRIBUTE, true);
    }

    private void initTxtSend() {
        this.txtSend = new JTextField();
        this.txtSend.setFont(new Font("Tahoma", Font.BOLD, 12));
        this.txtSend.setForeground(Color.WHITE);
        this.txtSend.addActionListener(e -> {
            //TODO send content of txtSend
        });
        this.txtSend.setBackground(Color.GRAY);
        this.txtSend.setBounds(0, 242, 425, 20);
        this.txtSend.setColumns(10);    //TODO do I need this?
        this.txtSend.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isAltDown() && e.isControlDown() && e.isShiftDown()) {
                    String shoutMessage = JOptionPane.showInputDialog(null, "Enter message:", "Shout",
                            JOptionPane.INFORMATION_MESSAGE);
                    //TODO send shout
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //noop
            }
        });
        this.panelChat.add(this.txtSend);
    }

    private void initOnlinePanel() {
        this.panelOnline = new JPanel();
        this.panelOnline.setBackground(Color.DARK_GRAY);
        this.panelOnline.setLayout(null); //TODO no layout?
        this.tabbedPane.addTab("Online", panelOnline);
        this.tabbedPane.setToolTipTextAt(1, "Show who is online");
    }

    private void initOnlineField() {
        this.txtOnline = new JTextArea();
        this.txtOnline.setFont(new Font("Tahoma", Font.BOLD, 16));
        this.txtOnline.setEditable(false);
        this.txtOnline.setBackground(Color.GRAY);
        this.txtOnline.setBounds(0, 0, 425, 230);
        this.panelOnline.add(this.txtOnline);

        this.scrollOnline = new JScrollPane(txtOnline);     //TODO why view (txtOnline) required here but not for same scrollpane for chat?
        this.scrollOnline.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.scrollOnline.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.scrollOnline.setBounds(0, 0, 425, 231);
        this.scrollOnline.getVerticalScrollBar().setUI(new DarkScrollbarUi());
        this.scrollOnline.getHorizontalScrollBar().setUI(new DarkScrollbarUi());
        this.panelOnline.add(this.scrollOnline);
    }

    private void initOnlineRefreshButton() {
        this.btnRefreshOnline = new JButton("");
        this.btnRefreshOnline
                .setIcon(new ImageIcon(refreshImage));  //TODO validate this works
        this.btnRefreshOnline.setFont(new Font("Tahoma", Font.BOLD, 15));
        this.btnRefreshOnline.addActionListener(actionEvent -> {
            //TODO start discovery
        });
        this.btnRefreshOnline.setBackground(Color.DARK_GRAY);
        this.btnRefreshOnline.setBounds(0, 228, 425, 34);
        this.panelOnline.add(this.btnRefreshOnline);
    }

    private void initTabBackgrounds() {
        this.tabbedPane.setBackgroundAt(0, Color.DARK_GRAY);
        this.tabbedPane.setBackgroundAt(1, Color.DARK_GRAY);
        this.tabbedPane.setForegroundAt(0, Color.WHITE);
        this.tabbedPane.setForegroundAt(1, Color.WHITE);
    }

    //region wrapping helper classes (mainly for URLs)
    static class WrapEditorKit extends StyledEditorKit {
        ViewFactory defaultFactory = new WrapColumnFactory();

        public ViewFactory getViewFactory() {
            return defaultFactory;
        }

    }

    static class WrapColumnFactory implements ViewFactory {
        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                switch (kind) {
                    case AbstractDocument.ContentElementName:
                        return new WrapLabelView(elem);
                    case AbstractDocument.ParagraphElementName:
                        return new ParagraphView(elem);
                    case AbstractDocument.SectionElementName:
                        return new BoxView(elem, View.Y_AXIS);
                    case StyleConstants.ComponentElementName:
                        return new ComponentView(elem);
                    case StyleConstants.IconElementName:
                        return new IconView(elem);
                }
            }

            // default to text display
            return new LabelView(elem);
        }
    }

    static class WrapLabelView extends LabelView {
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

    //endregion

    @Override
    public void setCallback(UserInterfaceCallback callback) {

    }

    @Override
    public void setUserList(List<String> users) {
    }

    @Override
    public void clearUserList() {

    }

    @Override
    public void addDiscoveredUser(String user) {

    }

    @Override
    public void clearHistory() {

    }

    @Override
    public void receiveMessage(String sender, String message, boolean shouted) {

    }

    @Override
    public boolean isOpened() {
        return false;
    }

    @Override
    public void open() {

    }

    @Override
    public void minimize() {

    }
}