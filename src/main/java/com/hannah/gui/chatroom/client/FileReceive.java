/**
 * receive files frame
 * @author longrm
 * @version 1.0
 * @since 2008-1-18
 */

package com.hannah.gui.chatroom.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;

public class FileReceive extends JFrame implements Runnable {
	private Socket socket;
	private DataOutputStream dout;
	private DataInputStream din;
	private String[] list;
	private String state = "stop";
	private File path;
	private String server = "127.0.0.1";
	private int port = 5000;
	private byte[] buffer = new byte[1024];
	
	// sender and receiver
	private String sender;
	private String receiver;
	private Thread t;

	JFileChooser chooser = new JFileChooser();
	JTextArea ta;
	JButton recBut = new JButton("Receive");
	JButton cancelBut = new JButton("Cancel");

	public FileReceive() {
		initFrame();
		setSize(500, 200);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public FileReceive getInstance() {
		return this;
	}

	// initial GUI frame
	public void initFrame() {
		// main area
		ta = new JTextArea();
		ta.setEditable(false);
		JScrollPane sp = new JScrollPane(ta);
		
		// button area
		recBut.addActionListener(new SaveFileListener());

		cancelBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});

		JPanel panel = new JPanel();
		panel.add(recBut);
		panel.add(cancelBut);
		
		// add panels to frame
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(sp, BorderLayout.CENTER);
		container.add(panel, BorderLayout.SOUTH);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				stop();
				getInstance().dispose();
			}
		});
	}
	
	/**
	 * initial params
	 * @author longrm
	 * @param serPort
	 * @param sender
	 * @param receiver
	 */
	public void initParams(String serPort, String sender, String receiver) {
		// initial params
		if(!serPort.equalsIgnoreCase("")) {
			int i = serPort.indexOf(":");
			server = serPort.substring(0, i);
			port = Integer.parseInt(serPort.substring(i+1));
		}
		if(!sender.equalsIgnoreCase(""))
			this.sender = sender;
		if(!receiver.equalsIgnoreCase(""))
			this.receiver = receiver;
		
		setTitle(this.receiver + " --- FileReceive from " + this.sender);
	}
	
	/**
	 * @author longrm
	 * @param list
	 */
	public void setList(String[] list) {
		if(list==null)
			return;
		
		this.list = list;
		// show file list and file length
		ta.setText("");
		for(int i=0; i<list.length; i++) {
			int j = list[i].indexOf("*");
			ta.append(list[i].substring(0,j) + "\t" +
					list[i].substring(j+1) + " (Byte)\n");
		}
	}
	
	public String[] getList() {
		return list;
	}
	
	// start thread
	public void start() {
		connect();
		t = new Thread(this);
		t.start();
	}

	// connect to file server
	public void connect() {
		try {
			socket = new Socket(server, port);
			dout = new DataOutputStream(socket.getOutputStream());
			din = new DataInputStream(socket.getInputStream());
			state = "rest";
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			while(true) {
				// get return message({SendFiles})
				System.out.println(din.readUTF());

				// receive each file through one new socket
				state = "receive";
				for(int i=0; i<list.length; i++) {
					// read file name
					String str = din.readUTF();
					
					// create file output
					File f = new File(path.getPath() + "\\" + str);
					if(!f.exists())
						f.createNewFile();
					FileOutputStream fos = new FileOutputStream(f);

					// get file length 
					int count=0;
					long fileLen=0;
					for(int j=0; j<list.length; j++) {
						int k = list[j].indexOf("*");
						String tmp = list[j].substring(0, k);
						if(tmp.equals(str)) {
							fileLen = Long.parseLong(list[j].substring(k+1));
							break;
						}
					}
					
					// calculate read count
					Double d = fileLen/(double)buffer.length;
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
					dout.writeUTF("File:  " + f.getName() + " receive end!");
					ta.append("File:  " + f + " receive end!\n");
				}
				state = "rest";
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally{
			stop();
		}		
	}
	
	public void stop() {
		// stop socket
		try {
			if(socket!=null)
				if(!(socket.isClosed())) {
					socket.close();
				}
			t.stop();
			state = "stop";
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
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
				// start receiving files
				try {
					dout.writeUTF("{ReceiveFiles}");
				}
				catch(Exception exc) {
					exc.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		FileReceive f = new FileReceive();
		f.initParams("", "longrm", "life");
	}
}
