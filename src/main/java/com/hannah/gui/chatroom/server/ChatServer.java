/**
 * manage users and server
 * @author longrm
 * @version 1.0
 * @since 2007-12-23
 */

package com.hannah.gui.chatroom.server;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


public class ChatServer extends JFrame {
	// used to read Username and Password from users.data file
	private final String USER_DATA_FILE = "users.data";
	private final String SERVER_LOG_FILE = "Server.log";
	private PrintWriter printLog, printUser;

	// store users and passwords
	private Hashtable<String, String> userPwds = new Hashtable<String, String>();
	private Vector<String> usersOnline = new Vector<String>();
	private Vector<String> usersDisOnline = new Vector<String>();
	//private long clickTime=0;
	
	// Server
	private Server server;

	// GUI component
	JLabel lab = new JLabel("Port:  ");
	JLabel userLab = new JLabel("Name@Pwd  ");
	JTextField userInfo = new JTextField(15);
	JTextField tf = new JTextField(10);
	JTextArea ta = new JTextArea(40, 80);
	JButton startBut = new JButton("Start");
	JButton stopBut = new JButton("Stop");
	JButton clearBut = new JButton("Clear Log");
	JButton disconnBut = new JButton("DisConnect");
	JButton addBut = new JButton("add");
	JButton delBut = new JButton("Delete");
	JList onlineList, disOnlineList;
	
	// initial
	private void initial() {
		try {
			FileWriter fp = new FileWriter(SERVER_LOG_FILE, true);
			printLog = new PrintWriter(new BufferedWriter(fp));
			printLog.println("ChatServer begin!");
		}
		catch(Exception e) {
			debug(e.toString() + "\n");
		}
	}

	public ChatServer() {
		TitledBorder border;
		
		// user list online and disonline
		onlineList = new JList();
		JScrollPane panel1 = new JScrollPane(onlineList);
		disOnlineList = new JList();
		JScrollPane panel2 = new JScrollPane(disOnlineList);
		onlineList.setForeground(Color.BLUE);
		onlineList.setBackground(new Color(230, 230, 230));
		onlineList.setSelectionForeground(Color.RED);
		onlineList.setToolTipText("在线用户");
		disOnlineList.setForeground(Color.GRAY);
		disOnlineList.setBackground(new Color(230, 230, 230));
		disOnlineList.setToolTipText("离线用户");
		
		// add list listener
		ListSelectionListener listListener = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				String name="";
				if(e.getSource()==onlineList) {
					name = (String)onlineList.getSelectedValue();
				}
				else if(e.getSource()==disOnlineList) {
					name = (String)disOnlineList.getSelectedValue();
				}
				
				// show name and password
				if(name!=null)
					userInfo.setText(name + "@" + userPwds.get(name));
			}
		};
		onlineList.addListSelectionListener(listListener);
		disOnlineList.addListSelectionListener(listListener);
		
		// add buttons to panels
		JPanel tmpPanel1 = new JPanel();
		tmpPanel1.add(disconnBut);
		JPanel onlinePanel = new JPanel();
		onlinePanel.setLayout(new BorderLayout());
		onlinePanel.add(tmpPanel1, BorderLayout.NORTH);
		onlinePanel.add(panel1, BorderLayout.CENTER);
		border = BorderFactory.createTitledBorder("Users Online:");
		onlinePanel.setBorder(border);
		disconnBut.setToolTipText("中断连接");
		addBut.setToolTipText("添加用户");
		delBut.setToolTipText("删除用户");
				
		// disconnect user selected
		disconnBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				// remove connection
				String name = (String)onlineList.getSelectedValue();
				if(name==null)
					return;
				server.removeConnection(new User(name));
			}
		});		
		// add user
		addBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				if(server==null)
					return;
				if(!server.isRunning())
					return;
				JDialog nw = new NewUser(ChatServer.this);
				nw.setVisible(true);
			}
		});
		// delete user
		delBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				String name = (String)disOnlineList.getSelectedValue();
				if(name==null)
					return;
				//delete user and update list
				usersDisOnline.remove(name);
				disOnlineList.setListData(usersDisOnline);
				userPwds.remove(name);
			}
		});

		JPanel tmpPanel2 = new JPanel();
		tmpPanel2.add(addBut);
		tmpPanel2.add(delBut);
		JPanel disOnlinePanel = new JPanel();
		disOnlinePanel.setLayout(new BorderLayout());
		disOnlinePanel.add(tmpPanel2, BorderLayout.NORTH);
		disOnlinePanel.add(panel2, BorderLayout.CENTER);
		border = BorderFactory.createTitledBorder("Users DisOnline:");
		disOnlinePanel.setBorder(border);
		
		// show user name and password
		JPanel userPwdPanel = new JPanel();
		userInfo.setForeground(Color.GRAY);
		userInfo.setBackground(new Color(230, 230, 230));
		userInfo.setEditable(false);
		userInfo.setToolTipText("用户名@密码");
		userPwdPanel.add(userLab);
		userPwdPanel.add(userInfo);
		
		// add two panels to userPanel
		JPanel userPanel = new JPanel();
		userPanel.setLayout(new BorderLayout());
		userPanel.add(disOnlinePanel, BorderLayout.NORTH);
		userPanel.add(onlinePanel, BorderLayout.CENTER);
		userPanel.add(userPwdPanel, BorderLayout.SOUTH);

		// start panel
		JPanel panel = new JPanel();
		tf.setToolTipText("端口");
		panel.add(lab);
		panel.add(tf);
		tf.setText("8001");
		panel.add(startBut);
		panel.add(stopBut);
		panel.add(clearBut);
		startBut.setToolTipText("启动服务");
		stopBut.setToolTipText("停止服务");
		clearBut.setToolTipText("清除日志");

		// add listener
		startBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// start server in port:xxxx
				try {
					int port = Integer.parseInt((tf.getText()).trim());
					// 1000<port<10000
					if (port <= 1000 || port >= 10000) {
						System.out.println("Illegal port: " + port);
						ta.append("\nIllegal port: " + port + "\n");
						return;
					}					
					startServer(port);
				} catch (NumberFormatException e) {
					System.out.println("Illegal port: " + tf.getText());
					ta.append("\nIllegal port: " + tf.getText() + "\n");
				}
			}
		});
		stopBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// stop server
				stopServer();
			}
		});
		clearBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// clear log
				ta.setText("");
			}
		});
		
		// log panel
		JScrollPane taPanel = new JScrollPane(ta);
		ta.setBackground(new Color(110, 220, 220));
		ta.setFont(new Font("Courier New", Font.PLAIN, 12));
		ta.setLineWrap(false);
		ta.setEditable(false);
		border = BorderFactory.createTitledBorder("Server Log:");
		taPanel.setBorder(border);
		
		// add two panels to serverPanel
		JPanel serverPanel = new JPanel();
		serverPanel.setLayout(new BorderLayout());
		serverPanel.add(panel, BorderLayout.NORTH);
		serverPanel.add(taPanel, BorderLayout.CENTER);
		
		// add panels to frame
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(userPanel, BorderLayout.WEST);
		container.add(serverPanel, BorderLayout.CENTER);

		// show frame
		setTitle("ChatServer v1.0 by longrm");
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	// read all user(name and password) from file users.data
	public void readUsers() {
		debug("Reading users from file " + USER_DATA_FILE);
		BufferedReader readUser = null;
		try {
			InputStream is = null;// = new FileInputStream(USER_DATA_FILE);
			File f = new File(USER_DATA_FILE);
			if (f.exists() || f.createNewFile())
				is = new FileInputStream(f);
			else
				throw new RuntimeException(USER_DATA_FILE + " create failed!");
			readUser = new BufferedReader(new InputStreamReader(is));
			String line = readUser.readLine();
			while(line!=null) {
				// get name and password and put them in usePwds
				int i = line.indexOf(":");
				if(i>0) {
					userPwds.put(line.substring(0, i), line.substring(i+1));
					usersDisOnline.addElement(line.substring(0, i));
				}
				else {
					debug("Find illegal user information:\t" + line);
				}
				line = readUser.readLine();
			}
		}
		catch(IOException ioe) {
			debug(ioe.toString() + "\n");
		}
		finally {
			// close bufferedReader
			try {
				readUser.close();
			}
			catch(IOException ioe) {
				debug(ioe.toString() + "\n");
			}
			debug("Reading users finish!");
		}
	}
	
	// add user
	public void addUser(User user) {
		if(usersDisOnline.contains(user.getName())) {
			debug("Add new User failed:\t" + user.getName() + " already exits!\n");
			return;
		}
		usersDisOnline.addElement(user.getName());
		userPwds.put(user.getName(), user.getPassword());
		disOnlineList.setListData(usersDisOnline);
	}
	
	// save users to file users.data
	public void saveUsers() {
		try {
			FileWriter fp = new FileWriter(USER_DATA_FILE, false);
			printUser = new PrintWriter(new BufferedWriter(fp));
			debug("Saving users to file " + USER_DATA_FILE);
			for(Enumeration e = userPwds.keys(); e.hasMoreElements();) {
				String name = (String)e.nextElement();
				String pwd = (String)userPwds.get(name);
				printUser.println(name+":"+pwd);
			}
			debug("Saving uses finish!");
			printUser.close();
		}
		catch(Exception e) {
			debug(e.toString() + "\n");
		}
	}
	
	// start server
	public void startServer(int port) {
		if(server==null)
			server = new Server(this, port);
		if(server.isRunning()) {
			debug("Server already start!\n");
			return;
		}
		initial();
		server.start();
		readUsers();
		onlineList.setListData(usersOnline);
		disOnlineList.setListData(usersDisOnline);
	}
	
	// stop server
	public void stopServer() {
		if(server==null)
			return;
		else if(server.isRunning()) {
			// remove all connections when server stopped
			server.removeAllConnections();
			// save users
			saveUsers();
			server.stop();
		}
		else
			return;
		
		if(printLog!=null) {
			printLog.close();
		}
		// clear list and userPwds
		usersOnline.removeAllElements();
		usersDisOnline.removeAllElements();
		userPwds.clear();
		onlineList.setListData(usersOnline);
		disOnlineList.setListData(usersDisOnline);
		userInfo.setText("");
	}
	
	// check user
	public String checkUser(User user, String oper) {
		String result="";
		String name = user.getName();
		// register
		if(oper.equals("register")) {
			if(userPwds.get(name)!=null)
				result = "repeat";
			else
				result = "success";
		}
		// login
		else if(oper.equals("login")) { 
			// this user is already online
			if(usersOnline.contains(name))
				result = "repeat";
			else if(!usersDisOnline.contains(name))
				result = "failed";
			// login success
			else if( (userPwds.get(name)).equals(user.getPassword()) )
				result = "success";
			else
				result = "failed";
		}
		return result;
	}
	
	// debug running message
	public void debug(Object object) {
		String message = object.toString();
		System.out.println(message);
		ta.append("\n" + message);
		ta.setCaretPosition(ta.getDocument().getLength());
		printLog.println(message);
	}
	
	// update user list
	public void updateUserList(User user, String oper) {
		String name = user.getName();
		if(userPwds.get(name)==null) {
			userPwds.put(user.getName(), user.getPassword());
		}
		if(oper.equals("connect")) {
			usersOnline.addElement(name);
			usersDisOnline.remove(name);
		}
		else if(oper.equals("disconnect")) {
			usersOnline.remove(name);
			usersDisOnline.addElement(name);
		}
		onlineList.setListData(usersOnline);
		disOnlineList.setListData(usersDisOnline);
		
		// send user list online to all users
		server.sendMessage("{userList}" + usersOnline.toString());
	}
	
	// get user list
	public Vector getUserList(boolean isOnline) {
		if(isOnline)
			return usersOnline;
		return usersDisOnline;
	}

	public static void main(String[] args) {
		ChatServer cs = new ChatServer();
	}
}