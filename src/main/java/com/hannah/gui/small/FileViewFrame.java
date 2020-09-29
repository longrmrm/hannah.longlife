package com.hannah.gui.small;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author longrm
 * @date 2007-11-11
 */
public class FileViewFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private JTextArea content;
	private JTextField searchTf;
	private JFileChooser chooser = new JFileChooser();
	private JColorChooser colorChooser = new JColorChooser(Color.BLACK);;
	private static int size = 16;
	private static int style = Font.PLAIN;
	private FileReader readConnToFile;
	private BufferedReader reader;
	private String fileName;
	private int index;
	private final int lines = 250;

	public FileViewFrame() {
		setTitle("FileViewFrame --- No file");
		initial();
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(100, 200);
	}

	public FileViewFrame(String fileName) throws Exception {
		initial();
		pack();
		setLocation(100, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		openFile(fileName);
	}

	public void openFile(String fileName) throws Exception {
		this.fileName = fileName;
		setTitle("FileViewFrame --- " + fileName);
		if (reader != null)
			reader.close();
		readConnToFile = new FileReader(fileName);
		reader = new BufferedReader(readConnToFile);
		index = 0;
		content.setText(readFile());
		content.select(0, 0);
	}

	public void initial() {
		content = new JTextArea(15, 50);
		content.setFont(new Font(null, style, size));
		content.setLineWrap(true);

		// Create Menu
		JMenuBar menuBar = new JMenuBar();
		// setJMenuBar(menuBar);

		JMenu menu1 = new JMenu("File");
		JMenu menu2 = new JMenu("Help");
		menuBar.add(menu1);
		menuBar.add(menu2);

		ActionListener openListener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int r = chooser.showOpenDialog(null);
				if (r == JFileChooser.APPROVE_OPTION) {
					String fileName = chooser.getSelectedFile().getPath();
					try {
						openFile(fileName);
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "File open failed!", "Open File...",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		};

		ActionListener closeListener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				content.setText("");
				searchTf.setText("");
				try {
					if (reader != null)
						reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		JMenuItem openItem = new JMenuItem("Open");
		menu1.add(openItem);
		openItem.addActionListener(openListener);

		JMenuItem closeItem = new JMenuItem("Close");
		menu1.add(closeItem);
		closeItem.addActionListener(closeListener);

		JMenuItem exitItem = new JMenuItem("Exit");
		menu1.add(exitItem);
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});

		// Create ToolBar
		JToolBar toolBar = new JToolBar();

		// Open file
		JButton openBut = new JButton("Open");
		openBut.addActionListener(openListener);

		// Font style: PLAIN/BOLD
		ActionListener fontStyleListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				if (cb.isSelected()) {
					style = Font.BOLD;
					content.setFont(new Font(null, style, size));
				} else {
					style = Font.PLAIN;
					content.setFont(new Font(null, style, size));
				}
			}
		};
		JCheckBox boldCb = new JCheckBox("BOLD");
		boldCb.addActionListener(fontStyleListener);

		// Font color
		ActionListener colorListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(null, "Color Chooser", Color.BLACK);
				if (c != null) {
					if (e.getActionCommand().equals("Font Color"))
						content.setForeground(c);
					else if (e.getActionCommand().equals("Background Color"))
						content.setBackground(c);
				}
			}
		};
		JButton fontColorBut = new JButton("Font Color");
		fontColorBut.addActionListener(colorListener);
		JButton backColorBut = new JButton("Background Color");
		backColorBut.addActionListener(colorListener);

		// Font size
		JButton reduceBut = new JButton("－");
		JButton enlargeBut = new JButton("＋");
		ActionListener fontSizeListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton but = (JButton) e.getSource();
				String command = but.getActionCommand();
				if (command == "－")
					content.setFont(new Font(null, style, --size));
				else if (command == "＋")
					content.setFont(new Font(null, style, ++size));
			}
		};
		reduceBut.addActionListener(fontSizeListener);
		enlargeBut.addActionListener(fontSizeListener);

		// LineWrap and Editable
		ActionListener readOptionListener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JCheckBox cb = (JCheckBox) evt.getSource();
				String command = cb.getActionCommand();
				if (command == "Line wrapp")
					content.setLineWrap(cb.isSelected());
				else if (command == "Editable")
					content.setEditable(cb.isSelected());
			}
		};

		JCheckBox lineWrap = new JCheckBox("Line wrapp");
		JCheckBox editable = new JCheckBox("Editable");
		lineWrap.setSelected(true);
		editable.setSelected(true);
		lineWrap.addActionListener(readOptionListener);
		editable.addActionListener(readOptionListener);

		// Read lines
		JButton readBut = new JButton("Continue...");
		readBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					if (reader == null)
						return;
					String contStr = readFile();
					if (contStr != "")
						content.append("\n" + contStr);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// Direct: GotoLine and Search
		searchTf = new JTextField(5);
		JButton searchBut = new JButton("Search");
		searchBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					if (reader == null)
						return;
					String searStr = searchTf.getText();
					if (searStr.equalsIgnoreCase(""))
						return;
					int lineNum = 0;
					try {
						lineNum = Integer.parseInt(searStr);
						if (lineNum < 0) {
							JOptionPane.showMessageDialog(null, "Line number illegal!",
									"GotoLine...", JOptionPane.WARNING_MESSAGE);
							return;
						}
					} catch (Exception e) {
						lineNum = 0;
					}
					if (lineNum != 0)
						content.append(gotoLine(lineNum));
					else
						content.append(search(searStr));
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Search failed!", "Search...",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// Clear
		JButton clearBut = new JButton("Clear");
		clearBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				content.setText("");
			}
		});

		// Reset
		JButton resetBut = new JButton("Reset");
		resetBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (reader == null)
					return;
				int choose = JOptionPane.showConfirmDialog(null, "Are you sure to reset?", "Reset",
						JOptionPane.YES_NO_OPTION);
				if (choose != JOptionPane.YES_OPTION)
					return;
				try {
					readConnToFile = new FileReader(fileName);
					reader = new BufferedReader(readConnToFile);
					index = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// drag file listener
		new DropTarget(content, new DropTargetListener() {
			public void dragEnter(DropTargetDragEvent event) {
			}

			public void dragExit(DropTargetEvent event) {
			}

			public void dragOver(DropTargetDragEvent event) {
			}

			public void dropActionChanged(DropTargetDragEvent event) {
			}

			public void drop(DropTargetDropEvent event) {
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
							java.util.List<File> fileList = (java.util.List<File>) transferable
									.getTransferData(d);
							File f = fileList.get(0);
							openFile(f.getPath());
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
		});

		toolBar.add(openBut);
		toolBar.add(fontColorBut);
		toolBar.add(backColorBut);
		toolBar.add(reduceBut);
		toolBar.add(enlargeBut);
		toolBar.add(boldCb);
		toolBar.add(lineWrap);
		toolBar.add(editable);
		toolBar.add(readBut);
		toolBar.add(clearBut);
		toolBar.add(resetBut);
		toolBar.add(searchTf);
		toolBar.add(searchBut);

		JScrollPane pane = new JScrollPane(content);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(toolBar, BorderLayout.NORTH);
		contentPane.add(pane, BorderLayout.CENTER);
	}

	// Read file
	public String readFile() throws Exception {
		String line = reader.readLine();
		if (line == null) {
			JOptionPane.showMessageDialog(null, "File End!", "Continue...",
					JOptionPane.WARNING_MESSAGE);
			return "";
		}
		index++;
		StringBuffer tmpBuf = new StringBuffer("\n" + index + ":\n");
		while (line != null && index % lines != 0) {
			if (index % 50 == 0)
				tmpBuf.append("\n" + index + ":\n");
			tmpBuf.append(line + "\n");
			line = reader.readLine();
			index++;
		}
		return tmpBuf.toString();
	}

	// GotoLine
	public String gotoLine(int lineNum) throws Exception {
		if (index >= lineNum) {
			JOptionPane.showMessageDialog(null, "Line already onShow!", "GotoLine...",
					JOptionPane.WARNING_MESSAGE);
			return "";
		}
		String line = "";
		String tmpStr = "";
		if (index < lineNum - 5) {
			for (; line != null && index < lineNum - 5; index++) {
				line = reader.readLine();
			}
		}
		if (index >= lineNum - 5) {
			while ((line = reader.readLine()) != null && ++index <= lineNum + 5) {
				if (index == lineNum)
					tmpStr += "\n" + index + ":\n";
				tmpStr += line + "\n";
			}
		}
		if (index < lineNum) {
			JOptionPane.showMessageDialog(null, "Line " + lineNum + " notFound!", "GotoLine...",
					JOptionPane.ERROR_MESSAGE);
			return "";
		}
		return tmpStr;
	}

	// Search
	public String search(String searStr) throws Exception {
		String tmpStr = "";
		int tmp = 0;
		boolean found = false;
		String line = "";
		index++;
		while ((line = reader.readLine()) != null) {
			if (line.indexOf(searStr) != -1) {
				found = true;
				break;
			}
			if (tmp >= 5) {
				tmp--;
				tmpStr = tmpStr.substring(tmpStr.indexOf("\n") + 1);
				tmpStr += line + "\n";
			} else
				tmpStr += line + "\n";
			index++;
			tmp++;
		}
		if (found) {
			tmpStr += "\n" + index + ":\n";
			for (int i = 0; i < 5 && line != null; i++) {
				tmpStr += line + "\n";
				line = reader.readLine();
				index++;
			}
		} else {
			JOptionPane.showMessageDialog(null, searStr + " notFound!", "Search...",
					JOptionPane.ERROR_MESSAGE);
			return "";
		}
		return tmpStr;
	}

	// Run local
	public static void main(String[] args) {
		new FileViewFrame().show();
	}
}