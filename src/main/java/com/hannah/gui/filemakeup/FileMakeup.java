package com.hannah.gui.filemakeup;

// FileMakeup.java
// Version 1.0
// longrm  2006-12-15

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileMakeup {
	public static void main(String[] args) {
		JFrame frame = new MainFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.show();
	}
}

class MainFrame extends JFrame {
	public MainFrame() {
		setTitle("FileMakeup v1.0");
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setLocation(280, 220);

		// Create Menu
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menu1 = new JMenu("File");
		JMenu menu2 = new JMenu("Help");
		menuBar.add(menu1);
		menuBar.add(menu2);

		JMenuItem exitItem = new JMenuItem("Exit");
		menu1.add(exitItem);
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});

		JMenuItem jdkItem = new JMenuItem("JDK deployment");
		JMenuItem helpItem = new JMenuItem("Help");
		JMenuItem aboutItem = new JMenuItem("About...");
		menu2.add(jdkItem);
		menu2.add(helpItem);
		menu2.add(aboutItem);

		jdkItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Frame jdkFrame = new JDKFrame();
				jdkFrame.show();
			}
		});

		helpItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Frame helpFrame = new HelpFrame();
				helpFrame.show();
			}
		});

		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String str = "                                      FileMakeup";
				str += "\n\nVersion :   1.0";
				str += "\n  Author :   longrm";
				str += "\n       Site :   http://www.npzw.com/    (涅盘中文论坛)        ";
				JOptionPane.showMessageDialog(null, str);
			}
		});

		// File Path
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();

		chooser = new JFileChooser();
		// chooser.setCurrentDirectory( new File("."));

		JLabel fileLabel = new JLabel("  File  Path :   ");
		fileField = new JTextField(24);
		JButton fileBrowseBut = new JButton("Browse");
		fileBrowseBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// show file chooser dialog
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int r = chooser.showOpenDialog(null);
				if (r == JFileChooser.APPROVE_OPTION) {
					String fileName = chooser.getSelectedFile().getPath();
					fileField.setText(fileName);
				}
			}
		});

		panel1.add(fileLabel);
		panel1.add(fileField);
		panel1.add(fileBrowseBut);

		// key
		JLabel keyLabel = new JLabel("key :   ");
		keyField = new JTextField("第x章", 10);
		keyField.setFont(new Font(null, Font.PLAIN, 14));

		JButton folderBut = new JButton("Create Folder");
		folderBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String path = outputField.getText();
				File tmpFile = new File(path);
				if (!tmpFile.isDirectory()) {
					if (tmpFile.mkdirs())
						JOptionPane.showMessageDialog(null, "Create Folder successful!");
					else
						JOptionPane.showMessageDialog(null, "Create Folder failed!");
				}
			}
		});

		panel2.add(keyLabel);
		panel2.add(keyField);
		panel2.add(folderBut);

		// Output Path
		JLabel outputLabel = new JLabel("Output  Path :   ");
		outputField = new JTextField(24);
		JButton outputBrowseBut = new JButton("Browse");
		outputBrowseBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// show file chooser dialog
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int r = chooser.showOpenDialog(null);
				if (r == JFileChooser.APPROVE_OPTION) {
					String fileName = chooser.getSelectedFile().getPath();
					outputField.setText(fileName);
				}
			}
		});

		panel3.add(outputLabel);
		panel3.add(outputField);
		panel3.add(outputBrowseBut);

		// TitleFetch and ChapterDivide
		JPanel panels = new JPanel();
		JButton indexBut = new JButton("Title Fetch");
		JButton viewBut = new JButton("View Title");
		JButton dealBut = new JButton("Chapter Divide");

		indexBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String fileName = fileField.getText();
				String key = keyField.getText();
				String output = outputField.getText();
				try {
					if (!output.equals(""))
						output += "\\";
					new TitleFetch(fileName, key, output);
					JOptionPane.showMessageDialog(null, "TitleFetch successful!");
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "TitleFetch failed!");
				}
			}
		});

		viewBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String output = outputField.getText();
				if (output.equalsIgnoreCase(""))
					new ViewFile();
				else
					new ViewFile(output + "\\title.txt");
			}
		});

		dealBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String fileName = fileField.getText();
				String key = keyField.getText();
				String output = outputField.getText();
				try {
					if (!output.equals(""))
						output += "\\";
					new ChapterDivide(fileName, key, output);
					JOptionPane.showMessageDialog(null, "ChapterDivide successful!");
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "ChapterDivide failed!");
				}
			}
		});

		panels.add(indexBut);
		panels.add(viewBut);
		panels.add(dealBut);

		// FileDivide
		JPanel indexPanel = new JPanel();
		JLabel indexLabel = new JLabel("index :   ");
		indexField = new JTextField(5);
		JButton fileDivideBut = new JButton("File Divide");

		fileDivideBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String fileName = fileField.getText();
				String key = keyField.getText();
				String output = outputField.getText();
				try {
					if (!output.equals(""))
						output += "\\";
					int index = Integer.parseInt(indexField.getText());
					new FileDivide(fileName, key, output, index, "000");
					JOptionPane.showMessageDialog(null, "FileDivide successful!");
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "FileDivide failed!");
				}
			}
		});

		indexPanel.add(indexLabel);
		indexPanel.add(indexField);
		indexPanel.add(fileDivideBut);

		// Add all panels to frame
		TitledBorder border;

		JPanel filePanel = new JPanel();
		filePanel.setLayout(new GridLayout(3, 1));
		filePanel.add(panel1);
		filePanel.add(panel2);
		filePanel.add(panel3);
		border = BorderFactory.createTitledBorder("File   Area   :");
		filePanel.setBorder(border);

		JPanel dealPanel = new JPanel();
		dealPanel.setLayout(new GridLayout(2, 1));
		dealPanel.add(panels);
		dealPanel.add(indexPanel);
		border = BorderFactory.createTitledBorder("Deal   Area   :");
		dealPanel.setBorder(border);

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(filePanel, BorderLayout.NORTH);
		contentPane.add(dealPanel, BorderLayout.SOUTH);
	}

	private JFileChooser chooser;
	private TitledBorder border;
	private JTextField fileField;
	private JTextField keyField;
	private JTextField outputField;
	private JTextField indexField;
	private static final int DEFAULT_WIDTH = 500;
	private static final int DEFAULT_HEIGHT = 320;
}