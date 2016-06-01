package de.eschoenawa.lanchat;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Inet4Address;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GUI extends JFrame {

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

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
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
		setTitle("LANChat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setResizable(false);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		this.contentPane.setLayout(null);
		
		this.lblOnline = new JLabel("Online");
		this.lblOnline.setBounds(308, 11, 126, 14);
		this.contentPane.add(this.lblOnline);
		
		this.lblChat = new JLabel("Chat");
		this.lblChat.setBounds(10, 11, 46, 14);
		this.contentPane.add(this.lblChat);
		
		this.textArea = new JTextArea();
		textArea.setEditable(false);
		this.textArea.setBounds(10, 36, 282, 186);
		this.textArea.setLineWrap(true);
		
		textArea.getDocument().addDocumentListener(new DocumentListener() {

	        @Override
	        public void removeUpdate(DocumentEvent e) {
	        	//TODO
	        }

	        @Override
	        public void insertUpdate(DocumentEvent e) {
	        	//TODO Notification
	        }

	        @Override
	        public void changedUpdate(DocumentEvent arg0) {
	        }
	    });
		this.contentPane.add(this.textArea);
		
		this.btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendText();
			}
		});
		this.btnSend.setBounds(203, 233, 89, 23);
		this.contentPane.add(this.btnSend);
		
		this.textField = new JTextField();
		this.textField.setBounds(10, 233, 183, 20);
		this.contentPane.add(this.textField);
		this.textField.setColumns(10);
		
		this.scrollPane = new JScrollPane(textArea);
		this.scrollPane.setBounds(10, 36, 282, 186);
		this.contentPane.add(this.scrollPane);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				discover();
			}
		});
		btnRefresh.setBounds(308, 232, 117, 25);
		contentPane.add(btnRefresh);
		
		textAreaOnline = new JTextArea();
		textAreaOnline.setEditable(false);
		textAreaOnline.setBounds(308, 37, 117, 183);
		contentPane.add(textAreaOnline);
		
		scrollPane_1 = new JScrollPane(textAreaOnline);
		scrollPane_1.setBounds(308, 37, 117, 183);
		contentPane.add(scrollPane_1);
		
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
		Chat.println(text);
	}
	
	public void reloadHistory() {
		this.textArea.setText("");
		Chat.load(this);
	}
	
	public void addValue(String value) {
		if (!(this.textAreaOnline.getText().contains(value)))
			this.textAreaOnline.append(value + "\n");
	}
	
	public void discover() {
		this.textAreaOnline.setText("");
		server.sendDiscoveryMessage();
	}
	
	public void sendText() {
		if (!this.textField.getText().equals("")) {
			try {
				server.sendToBroadcast(Config.load().getName() + ": " + this.textField.getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.textField.setText("");
		}
	}
}
