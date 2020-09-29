/**
 * add new user
 * @author longrm
 * @version 1.0
 * @since 2007-12-27
 */

package com.hannah.gui.chatroom.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewUser extends JDialog {
	private ChatServer cs;
	
	JLabel lab1 = new JLabel("        Name：");
	JLabel lab2 = new JLabel("Password：");
	JTextField tf1 = new JTextField(15);
	JPasswordField tf2 = new JPasswordField(15);
	JButton ok = new JButton("Ok");
	JButton cancel = new JButton("Cancel");

	public NewUser(ChatServer cs) {
		super(cs, "New User", true);
		
		// get ChatServer
		this.cs = cs;
		
		// GUI components
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		panel1.add(lab1);
		panel1.add(tf1);
		panel2.add(lab2);
		panel2.add(tf2);
		panel3.add(ok);
		panel3.add(cancel);

		NewListener listener = new NewListener();
		ok.addActionListener(listener);
		cancel.addActionListener(listener);

		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(panel1, BorderLayout.NORTH);
		container.add(panel2, BorderLayout.CENTER);
		container.add(panel3, BorderLayout.SOUTH);

		pack();
		setLocation(new Point(150, 280));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	private class NewListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == cancel)
				NewUser.this.hide();
			else if (e.getSource() == ok) {
				if (tf1.getText().trim().equals(""))
					JOptionPane.showMessageDialog(null, "Please enter name!",
							"Name Lacked", JOptionPane.ERROR_MESSAGE);
				else {
					// add user to hashtable and list
					cs.addUser(new User(tf1.getText().trim(), tf2.getText()));
					NewUser.this.hide();
				}
			}
		}
	};
}
