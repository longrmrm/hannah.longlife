package com.hannah.gui.bookreader.dialog;

import com.hannah.gui.bookreader.Book;
import com.hannah.gui.bookreader.JBookReader;
import com.hannah.gui.bookreader.config.Constant;
import com.hannah.swing.component.button.ImageButton;
import com.hannah.swing.component.dialog.BasicDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class RecentBooksDialog extends BasicDialog {

	private static final long serialVersionUID = 7782850986244250025L;

	private JButton deleteButton = new ImageButton(JBookReader.langConfig.getTrans("Delete"));

	private JTabbedPane tabPane = new JTabbedPane();
	private JList localBookList = new JList(new DefaultListModel());
	private JList onlineBookList = new JList(new DefaultListModel());

	public RecentBooksDialog(Frame owner, final List<Book> bookList) {
		super(owner, true);

		deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Book book = getBook();
				if (book == null)
					return;

				if (tabPane.getSelectedIndex() == 0)
					((DefaultListModel) localBookList.getModel()).removeElement(book);
				else
					((DefaultListModel) onlineBookList.getModel()).removeElement(book);
				bookList.remove(book);
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		buttonPanel.add(deleteButton);

		Color bgColor = JBookReader.config.getSettingToColor(Constant.BACKGROUND_COLOR);
		localBookList.setBackground(bgColor);
		onlineBookList.setBackground(bgColor);
		for (Book book : bookList) {
			if (book.getSite() == null)
				((DefaultListModel) localBookList.getModel()).addElement(book);
			else
				((DefaultListModel) onlineBookList.getModel()).addElement(book);
		}

		MouseAdapter adapter = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					setOk(true);
					dispose();
					return;
				}
			}
		};
		localBookList.addMouseListener(adapter);
		onlineBookList.addMouseListener(adapter);

		tabPane.addTab(JBookReader.langConfig.getTrans("Local Books"), localBookList);
		tabPane.addTab(JBookReader.langConfig.getTrans("OnLine Books"), onlineBookList);
		tabPane.setSelectedIndex(1);

		this.setLayout(new BorderLayout());
		this.add(buttonPanel, BorderLayout.NORTH);
		this.add(tabPane, BorderLayout.CENTER);

		this.setTitle(JBookReader.langConfig.getTrans("Recent Books"));
		this.setSize(400, 400);
		this.setLocationRelativeTo(owner);
	}

	public Book getBook() {
		if (tabPane.getSelectedIndex() == 0)
			return (Book) localBookList.getSelectedValue();
		else
			return (Book) onlineBookList.getSelectedValue();
	}

}
