/**
 * login frame to enter chatroom
 * @author longrm
 * @version 1.0
 * @since 2007-11-20
 */

package com.hannah.gui.chatroom.client;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class LoginFrame extends JFrame {
	// server and port
	private JLabel serverLb = new JLabel();
	private JLabel portLb = new JLabel();
	private JTextField serverTf = new JTextField(10);
	private JTextField portTf = new JTextField(4);
	
	// name and password
	private JLabel nameLb = new JLabel();
	private JLabel pwdLb = new JLabel();
	private JTextField nameTf = new JTextField(10);
	private JPasswordField pwdTf = new JPasswordField(10);

	// login and cancel button
	private JButton loginBut = new JButton();
	private JButton registerBut = new JButton();
	private TitledBorder serverborder, userborder;
	
	private final int DEFAULT_WIDTH = 320;
	private final int DEFAULT_HEIGHT = 220;

	// The socket connecting us to the server
	private Socket socket;

	// The streams we communicate to the server; these come from the socket
	private DataOutputStream dout;
	private DataInputStream din;

	public LoginFrame() {
		initial();
		// pack();
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		// request focus
		nameTf.requestFocus();
	}

	private void initial() {
		setLanguage("Chinese");
		serverTf.setText("127.0.0.1");
		portTf.setText("8001");

		// add listener
		loginBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		registerBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				register();
			}
		});

		// server and port panel
		JPanel serverPanel = new JPanel();
		serverPanel.add(serverLb);
		serverPanel.add(serverTf);
		serverPanel.add(portLb);
		serverPanel.add(portTf);
		serverPanel.setBorder(serverborder);

		// user and password panel
		JPanel userPanel = new JPanel();
		JPanel pwdPanel = new JPanel();
		userPanel.add(nameLb);
		userPanel.add(nameTf);
		pwdPanel.add(pwdLb);
		pwdPanel.add(pwdTf);

		// userInformation panel
		JPanel inforPanel = new JPanel();
		inforPanel.setLayout(new BorderLayout());
		inforPanel.add(userPanel, BorderLayout.NORTH);
		inforPanel.add(pwdPanel, BorderLayout.SOUTH);
		inforPanel.setBorder(userborder);

		// login panel
		JPanel loginPanel = new JPanel();
		loginPanel.add(loginBut);
		loginPanel.add(registerBut);

		// add all panel to frame
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(serverPanel, BorderLayout.NORTH);
		contentPane.add(inforPanel, BorderLayout.CENTER);
		contentPane.add(loginPanel, BorderLayout.SOUTH);
	}

	// set language
	private void setLanguage(String language) {
		if (language.equals("Chinese")) {
			setTitle("聊天室登陆");
			serverLb.setText("主机：");
			portLb.setText("端口：");
			nameLb.setText("姓名：");
			pwdLb.setText("密码：");
			loginBut.setText("登陆");
			registerBut.setText("注册");
			serverborder = BorderFactory.createTitledBorder("主机信息：");
			userborder = BorderFactory.createTitledBorder("用户信息：");
		} else if (language.equals("English")) {
			setTitle("Chat Room Login");
			serverLb.setText("Server:");
			portLb.setText("Port:");
			nameLb.setText("name:");
			pwdLb.setText("password:");
			loginBut.setText("Login");
			registerBut.setText("Register");
			serverborder = BorderFactory.createTitledBorder("Server Information:");
			userborder = BorderFactory.createTitledBorder("User Information:");
		}
	}

	// register
	private void register() {
		try {
			// Initiate the connection
			socket = new Socket(InetAddress.getByName(serverTf.getText()),
					Integer.parseInt(portTf.getText()));
			System.out.println("connected to " + socket);

			// Let's grab the streams and create DataInput/Output streams from them
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());

			// register with name and password
			dout.writeUTF("{register}" + nameTf.getText() + "@" + pwdTf.getText());
			String message = din.readUTF();
			System.out.println(message);
			if (message.equals("success")) {
				JOptionPane.showMessageDialog(null, "注册成功", "Check...",
						JOptionPane.INFORMATION_MESSAGE);
				this.dispose();
				ChatClient client = new ChatClient(nameTf.getText(), socket);
			}
			else if (message.equals("repeat")) {
				System.out.println("Close connection to " + socket);
				socket.close();
				JOptionPane.showMessageDialog(null, "该用户名已经使用", "Check...",
						JOptionPane.WARNING_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "注册失败", "Register...",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// login
	private void login() {
		try {
			// Initiate the connection
			socket = new Socket(InetAddress.getByName(serverTf.getText()),
					Integer.parseInt(portTf.getText()));
			System.out.println("connected to " + socket);

			// Let's grab the streams and create DataInput/Output streams from them
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());

			// check name and password
			dout.writeUTF("{login}" + nameTf.getText() + "@" + pwdTf.getText());
			String message = din.readUTF();
			System.out.println(message);
			if (message.equals("success")) {
				this.dispose();
				ChatClient client = new ChatClient(nameTf.getText(), socket);
			} else if (message.equals("failed")) {
				System.out.println("Close connection to " + socket);
				socket.close();
				JOptionPane.showMessageDialog(null, "你输入的用户名或密码错误，请重新输入",
						"Check...", JOptionPane.ERROR_MESSAGE);
			} else if (message.equals("repeat")) {
				System.out.println("Close connection to " + socket);
				socket.close();
				JOptionPane.showMessageDialog(null, "该用户已经登陆", "Check...",
						JOptionPane.WARNING_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "登陆失败", "Login...",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		new LoginFrame();
	}
}