package com.hannah.gui.chatroom.p2p;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;

public class P2PClient extends JFrame implements Runnable {
	private Socket socket;
	private DataOutputStream dout;
	private DataInputStream din;
	private String[] list;
	private String state = "rest";
	private File path;
	private final String server = "127.0.0.1";
	private final int port = 5000;
	private byte[] buffer = new byte[1024];
	
	JFileChooser chooser = new JFileChooser();
	JTextArea ta;
	JButton recBut = new JButton("Receive");
	JButton exitBut = new JButton("Exit");

	public P2PClient() {
		initial();
		setTitle("P2PClient");
		setSize(500, 200);
		setLocation(450, 320);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		connect();
	}

	// GUI frame
	public void initial() {
		// main area
		ta = new JTextArea();
		ta.setEditable(false);
		JScrollPane sp = new JScrollPane(ta);
		
		// button area
		recBut.addActionListener(new SaveFileListener());

		exitBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JPanel panel = new JPanel();
		panel.add(recBut);
		panel.add(exitBut);
		
//		 add panels to frame
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(sp, BorderLayout.CENTER);
		container.add(panel, BorderLayout.SOUTH);		
	}
	
	// connect to server
	public void connect() {
		try {
			socket = new Socket(server, port);
			dout = new DataOutputStream(socket.getOutputStream());
			din = new DataInputStream(socket.getInputStream());
			
			// read file list
			String str = din.readUTF();
			int i = str.indexOf("}");
			if(str.substring(0, i+1).equals("{FileList}")) {
				list = str.substring(i+1).split("\\?");
				ta.setText("");
				for(i=0; i<list.length; i++) {
					int j = list[i].indexOf("*");
					ta.append(list[i].substring(0,j) + "\t" +
							list[i].substring(j+1) + " (Byte)\n");
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// start thread
	public void start() {
		new Thread(this).start();
	}
	
	public void run() {
		receiveFile();
	}

	// receive file
	public void receiveFile() {
		state="receive";
		
		try {
			dout.writeUTF("{ReceiveFiles}");

			// receive each file through one new socket
			for(int i=0; i<list.length; i++) {
				// read file name
				String str = din.readUTF();
				
				// create file output
				File f = new File(path.getPath() + "\\" + str);
				if(!f.exists())
					f.createNewFile();
				FileOutputStream fos = new FileOutputStream(f);

				// get filesize and determine read count 
				int count=0;
				long fileSize=0;
				for(int j=0; j<list.length; j++) {
					int k = list[j].indexOf("*");
					String tmp = list[j].substring(0, k);
					if(tmp.equals(str)) {
						fileSize = Long.parseLong(list[j].substring(k+1));
						break;
					}
				}
				Double d = fileSize/(double)buffer.length;
				int dInt = d.intValue();
				double dec = d-dInt;
				if(dec==0.00)
					count = dInt;
				else
					count = dInt+1;				
				
				// read file buffer and write it into file f
				ta.append("File:  " + f + " receive begin!\n");
				int length=0;
				for(dInt=0; dInt<count; dInt++) {
					if ((length = din.read(buffer)) != -1) {
						fos.write(buffer, 0, length);
					}
				}
				
				fos.close();
				dout.writeUTF("File:  " + f + " receive end!");
				ta.append("File:  " + f + " receive end!\n");
			}
//			din.close();
//			socket.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		state="rest";
	}
	
	// save file listener
	private class SaveFileListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(list==null)
				return;
			if(list.length==0)
				return;
			if(socket==null)
				return;
			if(socket.isClosed())
				return;
			if(state.equals("receive"))
				return;
			// choose a director to save files
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int c = chooser.showSaveDialog(null);
			if(c == JFileChooser.APPROVE_OPTION) {
				path = chooser.getSelectedFile();
				start();
			}
		}
	}
	
	public static void main(String[] args) {
		new P2PClient();
	}
}
