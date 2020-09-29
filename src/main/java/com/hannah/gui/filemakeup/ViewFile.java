package com.hannah.gui.filemakeup;

// ViewFile.java
// Version 1.0
// longrm 2008-02-26

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;

public class ViewFile extends JFrame {
	private FileReader readConnToFile;
	private BufferedReader reader;
	private String fileName = null;

	JTextArea content;
	JFileChooser chooser = new JFileChooser();

	public void initial() {
		// Create Menu
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menu1 = new JMenu("File");
		JMenu menu2 = new JMenu("Help");
		menuBar.add(menu1);
		menuBar.add(menu2);

		JMenuItem openItem = new JMenuItem("Open");
		menu1.add(openItem);
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int r = chooser.showOpenDialog(null);
				if (r == JFileChooser.APPROVE_OPTION) {
					String fileName = chooser.getSelectedFile().getPath();
					openFile(fileName);
				}
			}
		});

		JMenuItem exitItem = new JMenuItem("Exit");
		menu1.add(exitItem);
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});

		content = new JTextArea(18, 80);
		content.setFont(new Font("宋体", Font.PLAIN, 15));
		content.setEditable(false);
		content.setLineWrap(false);
		content.setCaretPosition(0);
		JScrollPane pane = new JScrollPane(content);
		Container contentPane = getContentPane();
		contentPane.add(pane);

		setTitle("View File --- No file");
		pack();
		setLocation(200, 170);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public ViewFile() {
		initial();
	}

	public ViewFile(String fileName) {
		initial();
		openFile(fileName);
	}

	public void openFile(String fileName) {
		this.fileName = fileName;
		setTitle("View File --- " + fileName);

		try {
			if (reader != null)
				reader.close();
			readConnToFile = new FileReader(fileName);
			reader = new BufferedReader(readConnToFile);
			content.setText(readFile());
			content.setCaretPosition(0);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "View File failed!");
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String readFile() throws Exception {
		String line = reader.readLine();
		StringBuffer tmpBuf = new StringBuffer();
		while (line != null) {
			tmpBuf.append(line + "\n");
			line = reader.readLine();
		}
		return tmpBuf.toString();
	}

	public static void main(String[] args) {
		new ViewFile();
	}
}