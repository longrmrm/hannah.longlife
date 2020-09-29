package com.hannah.gui.chatroom.p2p;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class P2PServer extends JFrame implements Runnable {
	private java.util.List<File> fileList;
	private String state = "rest";
	private final int port = 5000;
	private ServerSocket ss;
	private Socket s;
	private byte[] buffer = new byte[1024];// 定义一byte类型的缓冲区
	
	JTextArea ta;
	JButton sendBut = new JButton("Send");
	JButton exitBut = new JButton("Exit");

	public P2PServer() {
		initial();
		setTitle("P2PServer");
		setSize(500, 200);
		setLocation(150, 400);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		listen();
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
				if(fileList==null)
					return;
				if(fileList.size()==0)
					return;
				
				// send files
				if(state.equals("send"))
					return;
				
				start();
			}
		});
		
		exitBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		JPanel panel = new JPanel();
		panel.add(sendBut);
		panel.add(exitBut);
		
//		 add panels to frame
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(sp, BorderLayout.CENTER);
		container.add(panel, BorderLayout.SOUTH);		
	}

	// listen
	public void listen() {
		try {
			ss = new ServerSocket(port);
			while (true) {
				s = ss.accept();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// start thread
	public void start() {
		new Thread(this).start();
	}
	
	public void run() {
		try {
			// get out and in stream
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			DataInputStream din = new DataInputStream(s.getInputStream());
			
			// send filelist and filelengths
			// set protocol: filepath*length?filepath*length?....
			String str="{FileList}";
			String name;
			for(File fl : fileList) {
				int i = fl.toString().lastIndexOf("\\");
				name = fl.toString().substring(i+1);
				str += name + "*" + fl.length() + "?";
			}
			dout.writeUTF(str);
			FileInputStream fis;
			DataInputStream dataIn;
			int length = 0;
			ta.append(din.readUTF() + "\n");
			state = "send";
			
			// send file one by one
			for(File f : fileList) {
				name = f.toString();
				name = name.substring(name.lastIndexOf("\\")+1);
				dout.writeUTF(name);
				fis = new FileInputStream(f);
				dataIn = new DataInputStream(fis);

				// send buffer length once
				ta.append("File:  " + f + " send begin!\n");
				while ((length = dataIn.read(buffer)) != -1) {
					dout.write(buffer, 0, length);
				}
				ta.append("File:  " + f + " send end!\n");
				
				dout.flush();
				fis.close();
				dataIn.close();
				
				ta.append(din.readUTF() + "\n");
			}
//			din.close();
//			dout.close();
//			s.close();

			state = "rest";
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
						fileList = (java.util.List<File>) transferable.getTransferData(d);
						ta.setText("");
			            for (File f : fileList) {  
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
		new P2PServer();
	}
}
