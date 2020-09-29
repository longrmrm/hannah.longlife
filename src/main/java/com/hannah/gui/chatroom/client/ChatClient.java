/**
 * chat main frame
 * @author longrm
 * @version 1.0
 * @since 2007-12-21
 */

package com.hannah.gui.chatroom.client;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.Hashtable;

public class ChatClient extends JFrame implements Runnable {
	// Components for the visual display of the chat windows(use JEditorPane)
	JEditorPane displayEp;
	JTextArea chatTa;
	JButton sendBut = new JButton();
	JButton exitBut = new JButton();
	TitledBorder userBorder, chatBorder, sendBorder;

	// show style
	private final int fontsize = 12;
	private final Color selfColor = new Color(100, 100, 0);
	private final Color pubColor = Color.BLUE;
	private final Color privColor = Color.RED;

	// username
	private String username;
	
	// users list
	JList userList;
	JCheckBox allCb = new JCheckBox("", true);;

	// The streams we communicate to the server; these come from the socket
	private Socket socket;
	private DataOutputStream dout;
	private DataInputStream din;
	
    // Mapping from senders to FileReceives
	private Hashtable<String, FileReceive> receives = new Hashtable<String, FileReceive>();

	// language: chinese or english
	private void setLanguage(String lang) {
		if(lang.equals("english")){
			userBorder = BorderFactory.createTitledBorder("User List:");
			chatBorder = BorderFactory.createTitledBorder("Chat Log:");
			sendBorder = BorderFactory.createTitledBorder("Send Message:");
			sendBut.setText("Send");
			exitBut.setText("Exit");
			allCb.setText("send to all");
		}
		else if(lang.equals("chinese")) {
			userBorder = BorderFactory.createTitledBorder("参与者:");
			chatBorder = BorderFactory.createTitledBorder("聊天记录:");
			sendBorder = BorderFactory.createTitledBorder("发送信息:");
			sendBut.setText("发送");
			exitBut.setText("退出");
			allCb.setText("发送给所有人");
		}
	}
	
	// Constructor
	public ChatClient(String username, Socket socket) {
		setLanguage("chinese");

		// initial parameters
		this.username = username;
		this.socket = socket;
		try {
			// grab streams
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		// user list
		String[] str = {username};
		userList = new JList(str);
		userList.setForeground(selfColor);
		userList.setBackground(new Color(240, 240, 240));
		userList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
		           if (e.getClickCount() == 2) {
		                // show send file frame
		        	   showFileSend();
		            }
		       }
		});

		// user panel
		JScrollPane tmpPane = new JScrollPane(userList);
		tmpPane.setBorder(userBorder);

		JPanel userPanel = new JPanel();
		userPanel.setLayout(new BorderLayout());
		userPanel.add(tmpPane, BorderLayout.CENTER);
		userPanel.add(allCb, BorderLayout.SOUTH);
		
		// display area
		displayEp = new JEditorPane();
		displayEp.setEditable(false);
		displayEp.setContentType("text/rtf");
		JScrollPane displayPane = new JScrollPane();
		JViewport vp = displayPane.getViewport();
		vp.add(displayEp);
		displayPane.setBorder(chatBorder);
		
		// chat area
		chatTa = new JTextArea(5, 30);
		chatTa.setLineWrap(true);
		chatTa.setEditable(true);
		JScrollPane chatPane = new JScrollPane(chatTa);

		// send message
		sendBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processMessage(chatTa.getText());
			}
		});

		// exit chat room 
		exitBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		// button panel
		JPanel butPanel = new JPanel();
		butPanel.add(sendBut);
		butPanel.add(exitBut);

		// chat panel
		JPanel chatPanel = new JPanel();
		chatPanel.setLayout(new BorderLayout());
		chatPanel.add(chatPane, BorderLayout.CENTER);
		chatPanel.add(butPanel, BorderLayout.SOUTH);
		chatPanel.setBorder(sendBorder);
		
		// message area
		JPanel mesPanel = new JPanel();
		mesPanel.setLayout(new BorderLayout());
		mesPanel.add(displayPane, BorderLayout.CENTER);
		mesPanel.add(chatPanel, BorderLayout.SOUTH);
		
		// add panels to frame
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(userPanel, BorderLayout.WEST);
		container.add(mesPanel, BorderLayout.CENTER);

		// show frame
		setTitle(username + " --- ChatClient v1.0 by longrm");
		setSize(500, 440);
		setLocationRelativeTo(null);
		//repaint();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Start a background thread for receiving messages
		new Thread(this).start();
	}

	// send file frame
	private void showFileSend() {
 	   String receiver = (String)userList.getSelectedValue();
	   if(receiver==null)
		   return;
	   else if(receiver.equals(username))
		   return;
	   new FileSend(this, username, receiver);
	}

	// format message and send it to server
	private void processMessage(String message) {
		// can not send message to self
		if(!allCb.isSelected()) {
			String name = (String)userList.getSelectedValue();
			if(name==null)
				return;
			else if(name!=null)
				if(name.equals(username))
					return;
		}

		try {
			// format message
			if(allCb.isSelected())
				message = "{message from " + username + " to @all}" + message;
			else {
				String toUser = (String)userList.getSelectedValue();
				// user list is not selected, select the first item 
				if(toUser==null) {
					userList.setSelectedIndex(0);
					toUser = (String)userList.getSelectedValue();
				}
				// user list is empty, return
				if(userList==null)
					return;
				message = "{message from " + username + " to " + toUser + "}" + message;
			}
			// Send message to the server
			dout.writeUTF(message);
			chatTa.setText("");
		}
		catch (IOException ie) {
			ie.printStackTrace();
			setDocs("\n\t丢失连接！", Color.RED, true, 12);
		}
	}
	
	// send file list and file length
	public void sendFileList(java.util.List<File> fileList, int port, String sender, String receiver) {
		// send filelist and filelengths
		// set protocol: {fileList from longrm:7001 to life}file1*length1?file2*length2?..
		String str="{fileList from " + sender + ":" + port + " to " + receiver + "}";
		for(File fl : fileList)
			str += fl.getName() + "*" + fl.length() + "?";
		
		// send filelist to server
		try {
			dout.writeUTF(str);
		}
		catch(Exception e) {
			e.printStackTrace();
			setDocs("\n\t丢失连接！", Color.RED, true, 12);
		}
	}

	// insert message into displayEp
	private void insert(String str, AttributeSet attrSet) {
		Document doc = displayEp.getDocument();
		try {
			doc.insertString(doc.getLength(), str, attrSet);
			// autoscroll to latest information
			displayEp.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			System.out.println("BadLocationException: " + e);
		}
	}

	// set font color, size, style
	private void setDocs(String str, Color col, boolean bold, int fontSize) {
		SimpleAttributeSet attrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(attrSet, col);
		if (bold == true) {
			StyleConstants.setBold(attrSet, true);
		}
		StyleConstants.setFontSize(attrSet, fontSize);
		insert(str, attrSet);
	}

	// Background thread runs this: show messages from other window
	public void run() {
		// show date first
		String dateStr = (new Date()).toLocaleString();
		setDocs(username + " join into chatroom at " + dateStr, Color.BLACK, true, 12);
		try {
			// Receive messages one-by-one, forever
			while (true) {
			    // Get the next message
				String message = din.readUTF();
				
				// process message
				int i = message.indexOf("}");
				String type = message.substring(1, i);
				
				// chat message
				if(type.indexOf("message")!=-1) {
					// {public message from longrm at 2007-11-3 10:47:39}message
					// {private message from longrm to life at 2007-11-3 10:47:39}message
					String chatMes = message.substring(i+1);
					String[] str = type.split(" ");
					// chatInfo = "longrm 2007-11-3 10:47:39"
					String chatInfo = str[3] + " ";
					// public message
					if(str[0].equals("public")) {
						chatInfo += str[5] + " " + str[6];
						// self message
						if(username.equals(str[3]))
							setDocs("\n"+chatInfo, selfColor, false, fontsize);
						else
							setDocs("\n"+chatInfo, pubColor, false, fontsize);
					}
					// private message
					else if(str[0].equals("private")) {
						chatInfo += str[4] + " " + str[5] + " " + str[7] + " " + str[8];
						if(username.equals(str[3]))
							setDocs("\n"+chatInfo, selfColor, true, fontsize);
						else
							setDocs("\n"+chatInfo, privColor, true, fontsize);
					}
					setDocs("\n"+chatMes, Color.BLACK, false, fontsize);
				}
				
				// user list
				else if(type.equals("userList")) {
					// clear old user list
					// usersOnline.clear();
					
					// get new user list like {userList}[sa, life, longrm]
					i = message.indexOf("[");
					int j = message.indexOf("]");
					String[] list = (message.substring(i+1, j)).split(", ");
					refreshUserList(list);
				}
				
				// file list like: {fileList from longrm@10.48.202.4:8001 to life}file1*length1?file2*length2?..
				else if(type.indexOf("fileList")!=-1) {
					String[] list = message.substring(i+1).split("\\?");
					String[] str = type.split(" ");
					int k = str[2].indexOf("@");
					String sender = str[2].substring(0, k);
					// get FileReceive from mapping receives, if it not exists, add it
					FileReceive receive = (FileReceive)receives.get(sender);
					if(receive==null) {
						receive = new FileReceive();
						receive.requestFocus();
						receive.setList(list);
						receive.initParams(str[2].substring(k+1), sender, username);
						receive.start();						
						receives.put(sender, receive);
					}
					else {
						receive.setList(list);
						receive.initParams(str[2].substring(k+1), sender, username);
						receive.requestFocus();
					}
				}
			}
		} catch (IOException ie) {
			ie.printStackTrace();
			setDocs("\n\t丢失连接！", Color.RED, true, 12);
		}
	}
	
	// refresh user list
	private void refreshUserList(String[] list) {
		String str = (String)userList.getSelectedValue();
		userList.setListData(list);
		if(str!=null)
			userList.setSelectedValue(str, true);
	}
	
	// remove file receive
	public void removeReceive(String sender) {
		receives.remove(sender);
	}
	
	// test frame
	public static void main(String[] args) {
		ChatClient cc = new ChatClient("longrm", null);
	}
}
