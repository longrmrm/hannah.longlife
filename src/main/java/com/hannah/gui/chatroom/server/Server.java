/**
 * server main thread to create one serverthread for each client
 * @author longrm
 * @version 1.0
 * @since 2007-12-21
 */

package com.hannah.gui.chatroom.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

public class Server implements Runnable{
	// server socket
	private ChatServer cs;
	private ServerSocket ss;
	private int port;
	private Thread t;

	// Mapping from users to sockets
	private Hashtable<String, Socket> userSockets = new Hashtable<String, Socket>();
	
	public Server(ChatServer cs, int port) {
		this.cs = cs;
		this.port = port;
	}
	
	// start thread
	public void start() {
		t = new Thread(this);
		t.start();		
		cs.debug("Start Server!");
	}
	
	// check whether the thread runs
	public boolean isRunning() {
		if(ss==null)
			return false;
		return !(ss.isClosed());
	}
	
	// Just listen
	public void run() {
		try {
			listen(port);
		}
		catch(Exception e) {
			cs.debug(e.toString() + "\n");
		}
	}
	
	// stop server thread
	public void stop() {
		cs.debug("Stop Listening on " + ss);
		try {
			ss.close();
		}
		catch(Exception e) {
			cs.debug(e.toString() + "\n");
		}
		t.stop();
		cs.debug("Stop Server!\n");
	}

	private void listen(int port) throws IOException {

		// Create the ServerSocket
		ss = new ServerSocket(port);
		cs.debug("Listening on " + ss + "\n");

		// Keep accepting connections forever
		while (true) {

			// Grab the next incoming connection
			Socket s = ss.accept();
			cs.debug("Connection to " + s);

			// Create a DataOutputStream for writing data to the other side
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			DataInputStream din = new DataInputStream(s.getInputStream());

			// get user information
			String userInfo = din.readUTF();
			int i = userInfo.indexOf("}");
			String type = userInfo.substring(1, i);
			userInfo = userInfo.substring(i+1);
			i = userInfo.indexOf("@");
			User user = new User(userInfo.substring(0,i), userInfo.substring(i+1));

			// check user and password
			String checkResult = cs.checkUser(user, type);
			dout.writeUTF(checkResult);
			
			// register
			if(type.equals("register")) {
				cs.debug("Register from " + s + " with information:  " + userInfo);
				//user check success
				if(checkResult.equals("success")) {
					cs.debug(user.getName() + " register success!\n");
					
					// Save mapping
					userSockets.put(user.getName(), s);
					cs.updateUserList(user, "connect");

					// Create a new thread for this connection, and then forget about it
					new ServerThread(this, s, user);
				}
				//user check failed
				else if(checkResult.equals("repeat")) {
					cs.debug("Register from " + s + " failed:  The user already exits");
					cs.debug("Removing connection to " + s + "\n");
					s.close();
				}
			}
			// login
			else if(type.equals("login")) {
				cs.debug("Login from " + s + " with information:  " + userInfo);
				// user check success
				if(checkResult.equals("success")) {
					cs.debug(user.getName() + " login success!\n");
					
					// Save mapping
					userSockets.put(user.getName(), s);
					cs.updateUserList(user, "connect");
					
					// Create a new thread for this connection, and then forget about it
					new ServerThread(this, s, user);
				}
				// user check failed
				else if(checkResult.equals("failed")) {
					cs.debug("Login from " + s + " failed:  Wrong name or password");
					cs.debug("Removing connection to " + s + "\n");
					s.close();
				}
				else if(checkResult.equals("repeat")) {
					cs.debug("Login from " + s + " failed:  The user has already login!");
					cs.debug("Removing connection to " + s + "\n");
					s.close();
				}
			}
		}
	}
	
	// Handle orginal message and send it
	public void sendMessage(String orgMessage) {
		// handle message like {message from longrm to life}message
		// or {fileList from longrm:7001 to life}file1*length1?file2*length2?..
		// or {userList}[sa, life, longrm...]
		int i = orgMessage.indexOf("}");
		String type = orgMessage.substring(1, i);
		String message = orgMessage.substring(i+1);
		String[] mes = type.split(" ");
		// System date
		Date d = new Date();
		
		if(type.indexOf("message")!=-1) {
			// send public message to all
			i = mes.length;
			if(mes[i-1].equals("@all")) {
				// {public message from longrm at 2007-11-3 10:47:39}message
				message = "{public message from " + mes[i-3] + " at " +
						d.toLocaleString() + "}" + message;
				sendToAll(message);
			}
			// send private message to user
			else {
				// {private message from longrm to life at 2007-11-3 10:47:39}message
				message = "{private message from " + mes[i-3] + " to " + mes[i-1] +
						" at " + d.toLocaleString() + "}" + message;
				sendToUser(message, mes[i-3]);
				sendToUser(message, mes[i-1]);
			}			
		}
		else if(type.indexOf("fileList")!=-1) {
			// mes[2] like: longrm:7001 --> longrm@10.48.202.4:7001
			i = mes[2].indexOf(":");
			String sender = mes[2].substring(0, i);
			String receiver = mes[4];
			Socket s = (Socket)userSockets.get(sender);
			// get socket's ip address
			String server = s.getInetAddress().getHostAddress();
			sendToUser(orgMessage.replaceFirst(":", "@" + server + ":"), receiver);
		}
		else if(type.indexOf("userList")!=-1) {
			sendToAll(orgMessage);
		}
	}

	// Send a message to client (utility routine)
	private void sendToUser(String message, String name) {

		synchronized (userSockets) {
			// find outputStream and send message
			try {
			    Socket s = (Socket)userSockets.get(name);
			    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
				dout.writeUTF(message);
			} catch (IOException ie) {
				cs.debug(ie.toString() + "\n");
			}
		}
	}

	// Send a message to all clients (utility routine)
	private void sendToAll(String message) {

		// We synchronize on this because another thread might be
		// calling removeConnection() and this would screw us up
		// as we tried to walk through the list
		synchronized (userSockets) {
			
			// For each client, get the output stream and send the message
			for (Enumeration e = userSockets.elements(); e.hasMoreElements();) {
				try {
				    DataOutputStream dout = new DataOutputStream(
				    		((Socket) e.nextElement()).getOutputStream());
					dout.writeUTF(message);
				} catch (IOException ie) {
					cs.debug(ie.toString() + "\n");
				}
			}
		}
	}

	// Remove a socket, and it's corresponding output stream, from our
	// list. This is usually called by a connection thread that has
	// discovered that the connectin to the client is dead.
	public void removeConnection(User user) {

		// Synchronize so we don't mess up sendToAll() while it walks
		// down the list of all output streams
		synchronized (userSockets) {
			String name = user.getName();
			Socket s = (Socket)userSockets.get(name);

			// Remove outputStreams and user
			cs.debug("Removing user:\t" + name);
			cs.debug("Removing connection to " + s + "\n");
			userSockets.remove(name);
			cs.updateUserList(user, "disconnect");

			// Make sure it's closed
			try {
				s.close();
			} catch (IOException ie) {
				cs.debug("Error closing " + s + "\n");
			}
		}
	}
	
	// remove all connections when server stopped
	public void removeAllConnections() {
		for(Enumeration e = userSockets.keys(); e.hasMoreElements();) {
			String name = (String)e.nextElement();
			removeConnection(new User(name));
		}
	}
}
