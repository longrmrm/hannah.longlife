package com.hannah.gui.separate;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * @author longrm
 * @date 2012-3-30
 */
public class FileSeparate extends JFrame {

	private JFileChooser chooser;
	private JTextField fileField;
	private JTextField outputField;
	private JRadioButton separate;
	private JRadioButton unite;
	private ButtonGroup bg;
	private JTextField lengthField;
	private JButton dealBut;
	private JTextArea ta;
	private ArrayList<File> fileList = new ArrayList<File>();
	private static final int DEFAULT_WIDTH = 500;
	private static final int DEFAULT_HEIGHT = 400;
	private static final byte[] buffer = new byte[1024];

	public FileSeparate() {
		setTitle("FileSeparate v1.0 by longrm");
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setLocation(300, 250);

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
				jdkFrame.setVisible(true);
			}
		});

		helpItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Frame helpFrame = new HelpFrame();
				helpFrame.setVisible(true);
			}
		});

		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String str = "                           Separate";
				str += "\n\nVersion :   1.0";
				str += "\n  Author :   longrm";
				str += "\n  E_Mail :   wzlrm@tom.com";
				str += "\n       Site :   http://hi.baidu.longrm/                    ";
				JOptionPane.showMessageDialog(null, str);
			}
		});

		// File Path
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();

		// Deal button
		separate = new JRadioButton("Separate");
		separate.setSelected(true);
		unite = new JRadioButton("Unite");
		lengthField = new JTextField(8);
		dealBut = new JButton("Deal");
		bg = new ButtonGroup();
		bg.add(separate);
		bg.add(unite);
		panel1.add(separate);
		panel1.add(unite);
		panel1.add(lengthField);
		panel1.add(new JLabel("KB"));
		panel1.add(dealBut);

		ActionListener l = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				lengthField.setEnabled(e.getSource() == separate);
				ta.setText("Operator:    " + e.getActionCommand());
				fileField.setText("");
				outputField.setText("");
				fileList.clear();
			}
		};
		separate.addActionListener(l);
		unite.addActionListener(l);

		lengthField.setHorizontalAlignment(JTextField.RIGHT);
		lengthField.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a)
					throws BadLocationException {
				try {
					Integer.parseInt(str);
				} catch (Exception ex) {
					Toolkit.getDefaultToolkit().beep();
					return;
				}
				super.insertString(offs, str, a);
			}
		});
		
		dealBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new Thread() {
					@Override
					public void run() {
						super.run();
						if (bg.isSelected(unite.getModel())) {
							doUnite();
						} else
							doSeparate();
					}
				}.start();
			}
		});
		
		// File Path
		chooser = new JFileChooser();
		// chooser.setCurrentDirectory( new File("."));

		JLabel fileLabel = new JLabel("       File  Path :   ");
		fileField = new JTextField(25);
		JButton fileBrowseBut = new JButton("Browse");
		fileBrowseBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// show file chooser dialog
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				// unite
				if (bg.isSelected(unite.getModel())) {
					chooser.setMultiSelectionEnabled(true);
					int r = chooser.showOpenDialog(null);
					if (r == JFileChooser.APPROVE_OPTION) {
						File[] files = chooser.getSelectedFiles();
						ta.append("\nFile:");
						fileList.clear();
						for (File f : files) {
							fileList.add(f);
							ta.append("\n    " + f.getPath());
						}
						fileField.setText(fileList.toString());
					}
				}
				// separate
				else {
					chooser.setMultiSelectionEnabled(false);
					int r = chooser.showOpenDialog(null);
					if (r == JFileChooser.APPROVE_OPTION) {
						String fileName = chooser.getSelectedFile().getPath();
						ta.append("\nFile:    " + fileName);
						fileField.setText(fileName);
					}
				}
			}
		});

		panel2.add(fileLabel);
		panel2.add(fileField);
		panel2.add(fileBrowseBut);

		// Output Path
		JLabel outputLabel = new JLabel("Output  Path :   ");
		outputField = new JTextField(25);
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

		// show panel
		ta = new JTextArea();
		ta.setEditable(false);
		JScrollPane showPanel = new JScrollPane(ta);

		// Add all panels to frame
		TitledBorder border;

		JPanel filePanel = new JPanel();
		filePanel.setLayout(new GridLayout(3, 1));
		filePanel.add(panel1);
		filePanel.add(panel2);
		filePanel.add(panel3);
		border = BorderFactory.createTitledBorder("File   Area   :");
		filePanel.setBorder(border);

		border = BorderFactory.createTitledBorder("Console   Area   :");
		showPanel.setBorder(border);

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(filePanel, BorderLayout.NORTH);
		contentPane.add(showPanel, BorderLayout.CENTER);
		separate.doClick();
	}

	public void doSeparate() {
		// get length
		int eachLength;
		try {
			eachLength = Integer.parseInt(lengthField.getText());
			if (eachLength < 1) {
				JOptionPane.showMessageDialog(null, "请输入你要截取的正确长度！");
				return;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "请输入你要截取的正确长度！");
			return;
		}

		if (fileField.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "请输入要处理的文件！");
			return;
		}
		
		File orgFile = new File(fileField.getText());
		String outPath = outputField.getText();
		if (outPath == "") {
			JOptionPane.showMessageDialog(null, "请输入文件输出目录！");
			return;
		}
		if (outPath.charAt(outPath.length() - 1) != '\\')
			outPath += "\\";
		String fileName = orgFile.getName();

		long subFileCount = orgFile.length() / eachLength / buffer.length + 1;
		if (JOptionPane.showConfirmDialog(null, "分割后文件数为：" + subFileCount + "，是否继续？") != JOptionPane.YES_OPTION)
			return;

		FileInputStream fis = null;
		FileOutputStream fos = null;
		ProgressMonitor progress = new ProgressMonitor(this, "Separating " + orgFile.getName() + "...", "Operating...", 0, 100);
		try {
			fis = new FileInputStream(orgFile);
			
			int i = 0;
			int j = 1;
			int k = 1;
			long totalLength = orgFile.length();
			ta.append("\nOutFile:");
			
			File f = null;
			int length = 0;
			int priorPercent = 0;
			while ((length = fis.read(buffer)) != -1) {
				if (progress.isCanceled()) {
					progress.close();
					return;
				}
				
				if (i == 0) {
					f = new File(outPath + fileName + "[" + j + "].jzip");
					if (!f.exists())
						f.createNewFile();
					else {
						JOptionPane.showMessageDialog(null, "文件" + f.getName() + "已存在！");
						return;
					}
					fos = new FileOutputStream(f);
				}
				fos.write(buffer, 0, length);
				
				i++;
				k++;
				if (i == eachLength) {
					fos.close();
					i = 0;
					j++;
					ta.append("\n  " + f.getPath());
					ta.setCaretPosition(ta.getText().length());
					// 显示进度条
					int percent = (int) (100D * k * buffer.length / totalLength);
					if (percent > priorPercent) {
						progress.setProgress(percent);
						progress.setNote("Operation is " + percent + "% complete!");
						priorPercent = percent;
					}
				}
			}
			JOptionPane.showMessageDialog(null, "文件分割成功！");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "文件分割失败！");
			ta.append("\n" + e.toString());
			ta.setCaretPosition(ta.getText().length());
		} finally {
			progress.close();
			try {
				fis.close();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void doUnite() {
		if (fileList.size() == 0) {
			JOptionPane.showMessageDialog(null, "请输入要合并的文件列表！");
			return;
		}

		String outPath = outputField.getText();
		if (outPath == "") {
			JOptionPane.showMessageDialog(null, "请输入文件输出目录！");
			return;
		}
		if (outPath.charAt(outPath.length() - 1) != '\\')
			outPath += "\\";

		FileInputStream fis = null;
		FileOutputStream fos = null;
		ProgressMonitor progress = new ProgressMonitor(this, "Uniting Files...", "Operating...", 0, 100);
		try {
			int length = 0;
			String fileName = fileList.get(0).getName();
			String path = fileList.get(0).getPath();
			if (!fileName.endsWith(".jzip")) {
				JOptionPane.showMessageDialog(null, "文件类型错误，请重新输入要合并的文件列表！");
				return;
			}
			fileName = fileName.replaceAll("\\[.*\\].jzip", "");
			File outFile = new File(outPath + fileName);
			ta.append("\nOutFile:\n  " + outFile.getPath());
			fos = new FileOutputStream(outFile);
			
			int priorPercent = 0;
			for (int i = 1; i <= fileList.size(); i++) {
				if (progress.isCanceled()) {
					progress.close();
					return;
				}
				
				File f = new File(path.replaceAll("\\[.*\\].jzip", "[" + i + "].jzip"));
				if (!(fileList.contains(f)) || !f.exists()) {
					JOptionPane.showMessageDialog(null, "文件" + f.getName() + "不存在");
					return;
				}
				fis = new FileInputStream(f);
				while ((length = fis.read(buffer)) != -1) {
					fos.write(buffer, 0, length);
				}
				fis.close();
				
				ta.append("\n  " + f.getPath());
				ta.setCaretPosition(ta.getText().length());
				// 显示进度条
				int percent = (int) (100D * i / fileList.size());
				if (percent > priorPercent) {
					progress.setProgress(percent);
					progress.setNote("Operation is " + percent + "% complete!");
					priorPercent = percent;
				}
			}
			JOptionPane.showMessageDialog(null, "文件合并成功！");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "文件截取失败！");
			ta.append("\n" + e.toString());
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new FileSeparate();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}