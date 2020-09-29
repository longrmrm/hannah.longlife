package com.hannah.swing.component.panel;

import com.hannah.swing.component.linenumber.LineNumberTable;
import com.hannah.swing.util.TableUtil;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class FilterTablePanel extends JPanel {

	private static final long serialVersionUID = 761771411282788722L;

	protected JPanel filterPanel = new JPanel();
	protected JTextField filterTf = new JTextField(25);

	protected JScrollPane scrollPane;
	protected JTable table;

	protected JTextArea statusTa = new JTextArea();

	public FilterTablePanel() {
		initInterface();
	}

	protected void initInterface() {
		initFilterPanel();
		initTable();

		scrollPane = new JScrollPane(table);

		statusTa.setLineWrap(true);
		statusTa.setEditable(false);
		statusTa.setOpaque(false);
		JPanel tmpPanel = new JPanel(new BorderLayout());
		tmpPanel.add(statusTa, BorderLayout.CENTER);
		tmpPanel.setVisible(false);

		this.setLayout(new BorderLayout());
		this.add(filterPanel, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(tmpPanel, BorderLayout.SOUTH);
	}

	protected void initFilterPanel() {
		filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
		filterPanel.add(new JLabel("  查找："));
		filterPanel.add(filterTf);

		/*filterTf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RowSorter sorter = table.getRowSorter();
				if (sorter == null)
					return;
				else if (sorter instanceof TableRowSorter) {
					TableRowSorter trSorter = (TableRowSorter) sorter;
					if (filterTf.getText().equals(""))
						trSorter.setRowFilter(null);
					else
						trSorter.setRowFilter(RowFilter.regexFilter(filterTf.getText()));
				}
				scrollPane.getRowHeader().revalidate();
				scrollPane.repaint();
			}
		});*/
		filterTf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				RowSorter sorter = table.getRowSorter();
				if (sorter == null)
					return;
				else if (sorter instanceof TableRowSorter) {
					TableRowSorter trSorter = (TableRowSorter) sorter;
					if (filterTf.getText().equals(""))
						trSorter.setRowFilter(null);
					else
						trSorter.setRowFilter(RowFilter.regexFilter(filterTf.getText()));
				}
				scrollPane.getRowHeader().revalidate();
				scrollPane.repaint();
			}
		});
	}

	protected void initTable() {
		table = TableUtil.createTable();
		table.setAutoCreateRowSorter(true);
	}

	public JPanel getFilterPanel() {
		return filterPanel;
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		if (this.table != null) {
			Container c = (Container) this.table.getParent();
			c.remove(this.table);
			c.add(table);
		} else
			scrollPane.getViewport().add(table);
		this.table = table;
	}

	public void setShowLineNumber(boolean flag) {
		scrollPane.setRowHeaderView(flag ? new LineNumberTable(table) : null);
	}

	public JTextArea getStatusTa() {
		return statusTa;
	}

}
