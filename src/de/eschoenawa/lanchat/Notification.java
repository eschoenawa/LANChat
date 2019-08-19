package de.eschoenawa.lanchat;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Notification extends JFrame {

	private static final long serialVersionUID = 1L;
	private static Notification n = null;
	private JPanel contentPane;
	private JLabel lblNewMessage;
	private JLabel lblMsg;
	private JLabel lblX;
	private UI parent;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		showNotification("Main Method of Notification", "TEST", null, false);
	}

	/**
	 * Create the frame.
	 */
	private Notification(String message, String title, UI parent) {
		this.parent = parent;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				setOpacity(1);
				if (parent.isShown())
					Notification.this.dispose();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setOpacity(0.5f);
				if (parent.isShown())
					Notification.this.dispose();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				click();
			}
		});
		setUndecorated(true);
		setOpacity(0.5f);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setFocusableWindowState(false);
		setAlwaysOnTop(true);
		setResizable(false);
		setBounds(100, 100, 350, 80);
		this.contentPane = new JPanel();
		this.contentPane.setBackground(Color.DARK_GRAY);
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		this.contentPane.setLayout(null);

		this.lblNewMessage = new JLabel("New LANChat message!");
		this.lblNewMessage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				click();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				setOpacity(1);
				if (parent.isShown())
					Notification.this.dispose();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setOpacity(0.5f);
				if (parent.isShown())
					Notification.this.dispose();
			}
		});
		this.lblNewMessage.setForeground(Color.WHITE);
		this.lblNewMessage.setFont(new Font("Tahoma", Font.BOLD, 14));
		this.lblNewMessage.setBounds(10, 11, 430, 22);
		this.contentPane.add(this.lblNewMessage);

		this.lblMsg = new JLabel(message);
		this.lblMsg.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				click();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				setOpacity(1);
				if (parent.isShown())
					Notification.this.dispose();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setOpacity(0.5f);
				if (parent.isShown())
					Notification.this.dispose();
			}
		});
		this.lblMsg.setForeground(Color.WHITE);
		this.lblMsg.setFont(new Font("Tahoma", Font.PLAIN, 14));
		this.lblMsg.setBounds(10, 44, 330, 22);
		this.contentPane.add(this.lblMsg);

		this.lblX = new JLabel("x");
		this.lblX.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				setOpacity(1);
				if (parent.isShown())
					Notification.this.dispose();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setOpacity(0.5f);
				if (parent.isShown())
					Notification.this.dispose();
			}
		});
		this.lblX.setForeground(Color.LIGHT_GRAY);
		this.lblX.setFont(new Font("Tahoma", Font.BOLD, 14));
		this.lblX.setBackground(Color.WHITE);
		this.lblX.setBounds(336, 0, 14, 14);
		this.contentPane.add(this.lblX);
	}
	
	private void click() {
		this.dispose();
		parent.showUI();
	}

	public static void showNotification(String msg, String title, UI parent, boolean red) {
		if (n != null) {
			n.dispose();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Notification frame = new Notification(msg, title, parent);
					n = frame;
					frame.setVisible(true);
					GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
					Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
					int x = (int) rect.getMaxX() - frame.getWidth();
					int y = (int) rect.getMaxY() - frame.getHeight() - 100;
					frame.setLocation(x, y);
					frame.toFront();
					if (red)
						frame.getContentPane().setBackground(Color.RED);
					new Timer().schedule(new java.util.TimerTask() {
						@Override
						public void run() {
							if (frame.isVisible()) {
								frame.dispose();
								n = null;
							}
						}
					}, 5000);
				} catch (Exception e) {
					ErrorHandler.reportError(e, false);
				}
			}
		});
	}
}
