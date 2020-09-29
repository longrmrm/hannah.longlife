package com.hannah.gui.filemakeup;

// AboutDialog.java
// Version 1.0
// longrm 2006-12-16

import javax.swing.*;
import java.awt.*;

public class AboutDialog extends JDialog {
	public AboutDialog(JFrame owner) {
		super(owner, "About...", true);

		String str1 = "FileMakeup Version :   1.0";
		String str2 = " Author :   longrm";
		String str3 = "   Site :   http://www.npzw.com/(涅盘中文论坛)";

		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		panel1.add(new JLabel(str1));
		panel2.add(new JLabel(str2));
		panel3.add(new JLabel(str3));

		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(panel1, BorderLayout.NORTH);
		container.add(panel2, BorderLayout.CENTER);
		container.add(panel3, BorderLayout.SOUTH);

		pack();
		setLocation(new Point(270, 250));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}