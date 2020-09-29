// NewWord.java     longrm   2005-12-3

package com.hannah.gui.dictionary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class NewWord extends JDialog {
	JLabel lab1 = new JLabel("Word:     "), lab2 = new JLabel("Explain:  ");
	JTextField tf1 = new JTextField(15), tf2 = new JTextField(15);
	JButton ok = new JButton("Ok"), cancel = new JButton("Cancel");

	public NewWord(JFrame owner) {
		super(owner, "New Word", true);
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		panel1.add(lab1);
		panel1.add(tf1);
		panel2.add(lab2);
		panel2.add(tf2);
		panel3.add(ok);
		panel3.add(cancel);

		NewListener listener = new NewListener();
		ok.addActionListener(listener);
		cancel.addActionListener(listener);

		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(panel1, BorderLayout.NORTH);
		container.add(panel2, BorderLayout.CENTER);
		container.add(panel3, BorderLayout.SOUTH);

		pack();
		setLocation(new Point(350, 280));
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void write() {
		try {
			FileWriter writerConnToFile = new FileWriter("WordList.txt", true);
			PrintWriter printer = new PrintWriter(new BufferedWriter(
					writerConnToFile));
			String word = tf1.getText() + " " + tf2.getText();
			printer.println();
			printer.print(word);
			printer.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private class NewListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == cancel)
				NewWord.this.hide();
			else if (e.getSource() == ok) {
				if (tf1.getText().trim().equals("")
						|| tf2.getText().trim().equals(""))
					JOptionPane.showMessageDialog(null, "Please enter data!",
							"Data Lacked", JOptionPane.ERROR_MESSAGE);
				else {
					write();
					NewWord.this.hide();
				}
			}
		}
	};
}
