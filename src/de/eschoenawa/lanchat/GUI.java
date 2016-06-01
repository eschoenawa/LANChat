package de.eschoenawa.lanchat;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class GUI extends JFrame {

	private JPanel contentPane;
	private JList list;
	private JLabel lblChatroom;
	private JLabel lblChat;
	private JTextArea textArea;
	private JButton btnSend;
	private JTextField textField;
	private JScrollPane scrollPane;

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
		
		this.list = new JList();
		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.list.setBounds(308, 36, 126, 186);
		this.contentPane.add(this.list);
		
		this.lblChatroom = new JLabel("Chatroom");
		this.lblChatroom.setBounds(308, 11, 126, 14);
		this.contentPane.add(this.lblChatroom);
		
		this.lblChat = new JLabel("Chat");
		this.lblChat.setBounds(10, 11, 46, 14);
		this.contentPane.add(this.lblChat);
		
		this.textArea = new JTextArea();
		this.textArea.setBounds(10, 36, 282, 186);
		this.textArea.setLineWrap(true);
		this.contentPane.add(this.textArea);
		
		this.btnSend = new JButton("Send");
		this.btnSend.setBounds(203, 233, 89, 23);
		this.contentPane.add(this.btnSend);
		
		this.textField = new JTextField();
		this.textField.setBounds(10, 233, 183, 20);
		this.contentPane.add(this.textField);
		this.textField.setColumns(10);
		
		this.scrollPane = new JScrollPane(textArea);
		this.scrollPane.setBounds(10, 36, 282, 186);
		this.contentPane.add(this.scrollPane);
	}
}
