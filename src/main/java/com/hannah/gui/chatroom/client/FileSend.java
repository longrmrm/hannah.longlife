/**
 * send files frame
 * @author longrm
 * @version 1.0
 * @since 2008-1-18
 */

package com.hannah.gui.chatroom.client;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class FileSend extends JFrame implements Runnable {
	private ArrayList<File> fileList = new ArrayList<File>();
	private String state = "stop";
	private static int port = 5000;
	private ServerSocket ss;
	private Socket s;
	private byte[] buffer = new byte[1024];
	
	private ChatClient cc;
	
	// sender and receiver
	private String sender;
	private String receiver;
	private Thread t;
	
	JTextArea ta;
	JButton sendBut = new JButton("Send");
	JButton clearBut = new JButton("Clear");
	JButton stopBut = new JButton("Stop");

	public FileSend getInstance() {
		return this;
	}
	
	public FileSend(ChatClient cc, String sender, String receiver) {
		// initial params and frame
		this.cc = cc;
		this.sender = sender;
		this.receiver = receiver;
		initial();
		setTitle(this.sender + " --- FileSend to " + this.receiver);
		setSize(500, 200);
		setLocationRelativeTo(cc);
		setVisible(true);
	}

	// GUI frame
	public void initial() {
		// main area
		ta = new JTextArea();
		ta.setEditable(false);
		new DropTarget(ta, new FileDropListener() );
		JScrollPane sp = new JScrollPane(ta);
		
		// button area
		sendBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// fileList is not empty && s is running
				if(fileList.size()==0)
					return;
				
				if(state.equals("send"))
					return;
				
				// send file list
				cc.sendFileList(fileList, port, sender, receiver);
				
				if(state.equals("stop"))
					start();
			}
		});
		
		// clear file list
		clearBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileList.clear();
				ta.setText("");
			}
		});
		
		stopBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});
		
		JPanel panel = new JPanel();
		panel.add(sendBut);
		panel.add(clearBut);
		panel.add(stopBut);
		
//		 add panels to frame
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
	
	// start thread
	public void start() {
		state = "rest";
		t = new Thread(this);
		t.start();
	}
	
	public void run() {
		try {
			// start file server and get client's socket
			ss = new ServerSocket(port++);
			s = ss.accept();
			
			// get output and input streams
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			DataInputStream din = new DataInputStream(s.getInputStream());
			
			FileInputStream fis;
			DataInputStream dataIn;
			int length = 0;
			
			// infinite send files until socket closes
//			while(true) {
				// get client request of receving files({ReceiveFiles})
				ta.append(din.readUTF() + "\n");
				dout.writeUTF("{SendFiles}");
				
				// send file one by one
				state = "send";
				for(File f : fileList) {
					dout.writeUTF(f.getName());
					fis = new FileInputStream(f);
					dataIn = new DataInputStream(fis);

					// send buffer length(1024 bytes) each time
					ta.append("File:  " + f + " send begin!\n");
					while ((length = dataIn.read(buffer)) != -1) {
						dout.write(buffer, 0, length);
					}
					ta.append("File:  " + f + " send end!\n");
					
					dout.flush();
					fis.close();
					dataIn.close();
					
					// recevie file f end
					ta.append(din.readUTF() + "\n");
				}
				state = "rest";
//			}
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		finally {
			try {
				stop();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stop() {
		// stop serversocket
		try {
			if(ss!=null)
				if(!(ss.isClosed())) {
					ss.close();
				}
			if(ss!=null)
				if(!(ss.isClosed())) {
					s.close();
				}
			cc.removeReceive(sender);
			t.stop();
			state = "stop";
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	// drop listener
	private class FileDropListener implements DropTargetListener {
		public void dragEnter(DropTargetDragEvent event) {
		}

		public void dragExit(DropTargetEvent event) {
		}

		public void dragOver(DropTargetDragEvent event) {
		}

		public void dropActionChanged(DropTargetDragEvent event) {
		}

		public void drop(DropTargetDropEvent event) {
			if(state.equals("send"))
				return;
			if (!isDropAcceptable(event)) {
				event.rejectDrop();
				return;
			}

			event.acceptDrop(DnDConstants.ACTION_COPY);

			// get file list use DataFlavor
			Transferable transferable = event.getTransferable();
			DataFlavor[] flavors = transferable.getTransferDataFlavors();
			for (int i = 0; i < flavors.length; i++) {
				DataFlavor d = flavors[i];
				try {
					if (d.equals(DataFlavor.javaFileListFlavor)) {
						java.util.List<File> files = (java.util.List<File>) transferable.getTransferData(d);
						
						// add files into fileList and display
						for(File f : files) {
							fileList.add(f);
							ta.append(f + "\t" + f.length() + " (Byte)\n");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			event.dropComplete(true);
		}

		public boolean isDragAcceptable(DropTargetDragEvent event) {
			// usually, you check the available data flavors here
			// in this program, we accept all flavors
			return (event.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
		}

		public boolean isDropAcceptable(DropTargetDropEvent event) {
			// usually, you check the available data flavors here
			// in this program, we accept all flavors
			return (event.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
		}
	}

	public static void main(String[] args) {
		new FileSend(null, "longrm", "life");
	}
}
