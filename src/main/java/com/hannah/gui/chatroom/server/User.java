/**
 * user bean
 * @author longrm
 * @version 1.0
 * @since 2007-12-22
 */

package com.hannah.gui.chatroom.server;

public class User {
	private String name;
	private String password;
	
	public User() {
		// do nothing
	}
	
	public User(String name) {
		this.name = name;
	}
	
	public User(String name, String password) {
		this.name = name;
		this.password = password;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
