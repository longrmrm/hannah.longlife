/**
 * serverthread to manage message from clients
 * @author longrm
 * @version 1.0
 * @since 2007-12-22
 */

package com.hannah.gui.chatroom.server;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.Socket;

// Multi-thread
public class ServerThread extends Thread {
	
	private static final String CHAT_LOG_FILE = "Chat.log";
	
	private Server server;
	private Socket socket;
	private User user; // username
	private PrintWriter printMes;

	public ServerThread(Server server, Socket socket, User user) {
		// Initial parameters
		this.server = server;
		this.socket = socket;
		this.user = user;

		try {
			// Write message to .log
			FileWriter writerConnToFile = new FileWriter(CHAT_LOG_FILE, true);
			printMes = new PrintWriter(new BufferedWriter(writerConnToFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		start();
	}

	public void run() {
		try {
			DataInputStream din = new DataInputStream(socket.getInputStream());

			// Infinite recurrence, read message of clients and handle them
			while (true) {
				String message = din.readUTF();
				System.out.println(message);
				printMes.println(socket);
				printMes.println(message);

				// send message
				server.sendMessage(message);
			}
		} catch (Exception ie) {
			ie.printStackTrace();
		} finally {
			printMes.println();
			printMes.close();
			// The connection is closed for one reason or another,
			// so have the server dealing with it
			if(server.isRunning() && !socket.isClosed())
			  server.removeConnection(user);			
		}
	}
}