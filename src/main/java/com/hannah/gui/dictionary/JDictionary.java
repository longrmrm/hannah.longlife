package com.hannah.gui.dictionary;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author longrm
 * @date 2012-3-30
 */
public class JDictionary extends JFrame {
	
	JMenuBar menuBar = new JMenuBar();
	JMenu menu = new JMenu("File");
	JMenuItem exitItem = new JMenuItem("Exit");
	JLabel lab = new JLabel("Enter:  ");
	JTextField tf = new JTextField(15);
	JTextArea explain = new JTextArea(10, 30);
	JButton search = new JButton("Search");
	JButton newWord = new JButton("New Word");

	HashMap wordMap = new HashMap();
	String[] words;
	JList wordList;

	public void initial() // 初始化wordMap, wordList
	{
		LinkedList link = new LinkedList();
		try {
			InputStream in = this.getClass().getResourceAsStream("WordList.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String oneWord = reader.readLine();
			while (oneWord != null) {
				int i;
				for (i = 0; i < oneWord.length(); i++) {
					char c = oneWord.charAt(i);
					if (c == ' ')
						break;
				}
				String word = (oneWord.substring(0, i)).trim();
				String expl = word;
				expl += "\n" + (oneWord.substring(i)).trim();
				wordMap.put(word, expl);
				link.add(word);
				oneWord = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Collections.sort(link); // 用collection集的预定方法将单词组排序
		Iterator it = link.iterator();
		int len = wordMap.size();
		words = new String[len];
		for (int i = 0; i < len; i++) {
			words[i] = (String) it.next();
		}

		wordList = new JList(words);
		wordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public JDictionary() { 
		super("Powereord Version 1.0");
		initial();

		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu.add(exitItem);
		menuBar.add(menu);
		
		wordList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				String word = (String) wordList.getSelectedValue();
				explain.setText((String) wordMap.get(word));
			}
		});

		JPanel panel = new JPanel();
		panel.add(lab);
		panel.add(tf);
		panel.add(search);
		panel.add(newWord);

		SearchListener listener = new SearchListener();
		search.addActionListener(listener);
		tf.addActionListener(listener);
		tf.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				int i;
				String word = tf.getText();
				for (i = 0; i < words.length; i++) {
					if (word.compareTo(words[i]) <= 0)
						break;
				}
				wordList.setSelectedIndex(i);
				wordList.ensureIndexIsVisible(i + 8);
			}
		});
		newWord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog nw = new NewWord(JDictionary.this);
				nw.setVisible(true);
			}
		});

		TitledBorder border;
		JScrollPane wordPanel = new JScrollPane(wordList);
		wordPanel.setAutoscrolls(true);
		border = BorderFactory.createTitledBorder("Index:");
		wordPanel.setBorder(border);

		JPanel explainPanel = new JPanel();
		explainPanel.add(explain);
		explain.setEditable(false);
		border = BorderFactory.createTitledBorder("Explain:");
		explainPanel.setBorder(border);

		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(menuBar, BorderLayout.NORTH);
		container.add(panel, BorderLayout.NORTH);
		container.add(wordPanel, BorderLayout.WEST);
		container.add(explainPanel, BorderLayout.CENTER);

		pack();
		Point p = new Point(270, 230);
		setLocation(p);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private class SearchListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String word = tf.getText();
			String selected = (String) wordList.getSelectedValue();
			if (word.equals(selected))
				explain.setText((String) wordMap.get(word));
			else {
				explain.setText(word);
				explain.append("  is not found!");
			}
		}
	};

	public static void main(String[] args) {
		new JDictionary();
	}
}