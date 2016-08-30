package de.eschoenawa.lanchat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class Settings extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblLanchatSettings;
	private JCheckBox chckbxHideNotifications;
	private JCheckBox chckbxStartMinimized;
	private JCheckBox chckbxUpdate;
	private JLabel lblNickname;
	private JTextField txtName;
	private JLabel lblError;
	private JButton btnOk;
	private JButton btnCancel;
	private UI parent;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Settings frame = new Settings(null);
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
	public Settings(UI parent) {
		addWindowFocusListener(new WindowFocusListener() {
			public void windowLostFocus(WindowEvent arg0) {
				cancel();
			}

			@Override
			public void windowGainedFocus(WindowEvent arg0) {
				//Nothing
			}
		});
		this.parent = parent;
		setTitle("LANChat Settings");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 315, 241);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		this.contentPane.setLayout(null);
		
		this.lblLanchatSettings = new JLabel("LANChat Settings");
		this.lblLanchatSettings.setFont(new Font("Tahoma", Font.PLAIN, 14));
		this.lblLanchatSettings.setBounds(10, 11, 414, 23);
		this.contentPane.add(this.lblLanchatSettings);
		
		this.chckbxHideNotifications = new JCheckBox("Hide Notifications (Resets after restart)");
		this.chckbxHideNotifications.setBounds(10, 41, 260, 23);
		if (parent != null)
			this.chckbxHideNotifications.setSelected(!parent.areNotificationsShown());
		this.contentPane.add(this.chckbxHideNotifications);
		
		this.chckbxStartMinimized = new JCheckBox("Start minimized");
		this.chckbxStartMinimized.setBounds(10, 67, 120, 23);
		this.chckbxStartMinimized.setSelected(Boolean.valueOf(Config.get("minimized")));
		this.contentPane.add(this.chckbxStartMinimized);
		
		this.chckbxUpdate = new JCheckBox("Update automatically");
		this.chckbxUpdate.setBounds(10, 93, 160, 23);
		this.chckbxUpdate.setSelected(Boolean.valueOf(Config.get("autoupdate")));
		this.contentPane.add(this.chckbxUpdate);
		
		this.lblNickname = new JLabel("Nickname:");
		this.lblNickname.setBounds(15, 123, 70, 14);
		this.contentPane.add(this.lblNickname);
		
		this.txtName = new JTextField();
		this.txtName.setBounds(95, 120, 175, 20);
		this.contentPane.add(this.txtName);
		this.txtName.setText(Config.get("name"));
		this.txtName.setColumns(10);
		
		this.lblError = new JLabel("");
		this.lblError.setFont(new Font("Tahoma", Font.BOLD, 11));
		this.lblError.setForeground(Color.RED);
		this.lblError.setBounds(10, 148, 240, 14);
		this.contentPane.add(this.lblError);
		
		this.btnOk = new JButton("OK");
		this.btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		this.btnOk.setBounds(10, 173, 89, 23);
		this.contentPane.add(this.btnOk);
		
		this.btnCancel = new JButton("Cancel");
		this.btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		this.btnCancel.setBounds(106, 173, 89, 23);
		this.contentPane.add(this.btnCancel);
	}
	
	public void save() {
		if (this.txtName.getText().contains(" ")) {
			this.lblError.setText("Please don't use spaces in the name!");
			Border border = BorderFactory.createLineBorder(Color.red);
			this.txtName.setBorder(border);
		}
		else if (this.txtName.getText().length() > 20) {
			this.lblError.setText("Name too long (max 20 characters)!");
			Border border = BorderFactory.createLineBorder(Color.red);
			this.txtName.setBorder(border);
		}
		else if (this.txtName.getText().length() < 2) {
			this.lblError.setText("Name too short (min 2 characters)!");
			Border border = BorderFactory.createLineBorder(Color.red);
			this.txtName.setBorder(border);
		}
		else {
			if (parent != null)
				parent.setShowNotifications(!this.chckbxHideNotifications.isSelected());
			Config.set("minimized", new Boolean(this.chckbxStartMinimized.isSelected()).toString());
			Config.set("autoupdate", new Boolean(this.chckbxUpdate.isSelected()).toString());
			Config.set("name", this.txtName.getText());
			this.dispose();
		}
	}
	
	public void cancel() {
		this.dispose();
	}
}
