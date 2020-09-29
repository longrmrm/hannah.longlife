package com.hannah.swing.util;

import com.hannah.common.util.StringUtil;
import com.hannah.swing.component.dialog.SearchDialog;
import com.hannah.swing.component.table.BasicTableCellRenderer;
import com.hannah.swing.component.table.BasicTableHeader;
import com.hannah.swing.component.table.BasicTableModel;
import com.hannah.swing.component.table.CheckBoxTableCellRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TableUtil {

	public static JTable createTable() {
		JTable table = new JTable();
		initTable(table);
		// initTableHeader(table);
		return table;
	}

	public static void initTable(final JTable table) {
		// model改变时，tableColumn重新设置identifier
		table.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("model".equals(evt.getPropertyName()))
					setTableColumnIdentifier(table);
			}
		});
		// table.setAutoCreateRowSorter(true);
		table.setRowHeight(23);
		setTableCellRender(table);
		registerSearchKeyAction(table);
		registerCopyKeyAction(table);
    	registerPasteKeyAction(table);
	}

	public static void initTableHeader(JTable table) {
		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setPreferredSize(new Dimension(tableHeader.getPreferredSize().width, 30));
		tableHeader.setReorderingAllowed(false); // 表格列不可移动
		// tableHeader.setFont(new Font("黑体", Font.PLAIN, 12)); // 列名字体
		DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
		renderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER); // 列名居中
	}

	public static Object[] getHideColumns(List<BasicTableHeader> headerList) {
		List<String> hideColumns = new ArrayList<String>();
		for (BasicTableHeader header : headerList) {
			if (header.isHide())
				hideColumns.add(header.getHeaderValue());
		}
		return hideColumns.toArray();
	}

	public static BasicTableHeader getTableHeaderByIdentifier(List<BasicTableHeader> headerList,
			String identifier) {
		for (BasicTableHeader header : headerList) {
			if (header.getIdentifier().equals(identifier))
				return header;
		}
		return null;
	}

	public static BasicTableHeader getTableHeaderByHeaderValue(List<BasicTableHeader> headerList,
			String headerValue) {
		for (BasicTableHeader header : headerList) {
			if (header.getHeaderValue().equals(headerValue))
				return header;
		}
		return null;
	}

	/**
	 * get table column by headerValue or identifier
	 * @param table
	 * @param header
	 * @return
	 */
	private static TableColumn getTableColumn(JTable table, BasicTableHeader header) {
		TableColumn tableColumn = null;
		try {
			tableColumn = table.getColumn(header.getHeaderValue());
		} catch (IllegalArgumentException e) {
			tableColumn = table.getColumn(header.getIdentifier());
		}
		return tableColumn;
	}

	/**
	 * 将tableColumn的identifier命名为headerList里的identifier（原先为null）
	 * @param table
	 */
	public static void setTableColumnIdentifier(JTable table) {
		BasicTableModel model = (BasicTableModel) table.getModel();
		List<BasicTableHeader> headerList = model.getHeaderList();
		for (BasicTableHeader header : headerList)
			getTableColumn(table, header).setIdentifier(header.getIdentifier());
	}

	/**
	 * set table column preferred width
	 * @param table
	 */
	public static void setTableColumnWidth(JTable table) {
		BasicTableModel model = (BasicTableModel) table.getModel();
		List<BasicTableHeader> headerList = model.getHeaderList();
		for (BasicTableHeader header : headerList) {
			if (header.getWidth() >= 0) {
				TableColumn tableColumn = getTableColumn(table, header);
				tableColumn.setPreferredWidth(header.getWidth());
			}
		}
	}

	public static void setCheckBoxTableHeader(final JTable table, final String identifier) {
		setCheckBoxTableHeader(table, identifier, null);
	}

	/**
	 * set checkbox table header
	 * @param table
	 * @param identifier the column identifier
	 * @param headerName the header name which is shown
	 */
	public static void setCheckBoxTableHeader(final JTable table, final String identifier, String headerName) {
		table.getColumn(identifier).setHeaderRenderer(new CheckBoxTableCellRenderer(headerName));
		table.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 点击第一列表头，同步 选中/取消选中 所有行
				int column = table.getColumnModel().getColumnIndex(identifier);
				if (table.getColumnModel().getColumnIndexAtX(e.getX()) == column) {
					JCheckBox check = (JCheckBox) table.getColumn(identifier).getHeaderRenderer();
					boolean b = !check.isSelected();
					check.setSelected(b);

					BasicTableModel model = (BasicTableModel) table.getModel();
					for (int i = 0; i < table.getRowCount(); i++) {
						int row = table.convertRowIndexToModel(i);
						table.setValueAt(b, row, column);
					}

					table.getTableHeader().repaint();
					model.fireTableDataChanged();
				}
			}
		});
	}

	/**
	 * set table default cell renderer
	 * @param table
	 */
	public static void setTableCellRender(JTable table) {
		BasicTableCellRenderer tableCellRenderer = new BasicTableCellRenderer();
		table.setDefaultRenderer(Object.class, tableCellRenderer);
		table.setDefaultRenderer(Integer.class, tableCellRenderer);
		table.setDefaultRenderer(BigDecimal.class, tableCellRenderer);
		table.setDefaultRenderer(Double.class, tableCellRenderer);
		table.setDefaultRenderer(Float.class, tableCellRenderer);
		table.setDefaultRenderer(Date.class, tableCellRenderer);
	}

	public static BigDecimal getTableSum(JTable table, String identifier) {
		BigDecimal sum = new BigDecimal(0);
		int column = table.getColumnModel().getColumnIndex(identifier);
		for (int row = 0; row < table.getRowCount(); row++) {
			Object value = table.getValueAt(row, column);
			if (value instanceof BigDecimal)
				sum = sum.add((BigDecimal) value);
			else if (value instanceof Double)
				sum = sum.add(new BigDecimal((Double) value));
			else if (value instanceof String)
				sum = sum.add(new BigDecimal(value.toString().trim().replaceAll(",", "")));
		}
		return sum;
	}

	public static List getSelectedRecords(JTable table) {
		return getSelectedRecords(table, 0);
	}

	/**
	 * get selected records from table
	 * @param table
	 * @param columnIndex the header is checkbox
	 * @return
	 */
	public static List getSelectedRecords(JTable table, int columnIndex) {
		List selectedRecords = new ArrayList();
		BasicTableModel model = (BasicTableModel) table.getModel();

		// 第columnIndex列为checkbox选中列
		if (table.getColumnModel().getColumn(columnIndex).getHeaderRenderer() instanceof JCheckBox) {
			for (int i = 0; i < table.getRowCount(); i++) {
				Boolean selected = (Boolean) table.getValueAt(i, 0);
				if (selected != null && selected == true) {
					int row = table.convertRowIndexToModel(i);
					selectedRecords.add(model.getDataList().get(row));
				}
			}
		} else {
			int[] rows = table.getSelectedRows();
			for (int row : rows) {
				row = table.convertRowIndexToModel(row);
				selectedRecords.add(model.getDataList().get(row));
			}
		}
		return selectedRecords;
	}

	/**
	 * 给table注册查询热键（Ctrl+F）
	 * @param table
	 */
	public static void registerSearchKeyAction(final JTable table) {
		ActionListener searchListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog d = new SearchDialog() {
					@Override
					protected boolean search(int direction) {
						return TableUtil.search(table, getSearchText(), direction);
					}
				};
				d.setVisible(true);
			}
		};

		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK, false);
		table.registerKeyboardAction(searchListener, ks, JComponent.WHEN_FOCUSED);
	}

	public static boolean search(JTable table, String searchText, int direction) {
		int startRow = table.getSelectedRow() == -1 ? 0 : table.getSelectedRow();
		return search(table, searchText, startRow, direction);
	}

	/**
	 * search text in table
	 * @param table
	 * @param searchText
	 * @param startRow
	 * @param direction
	 * @return
	 */
	public static boolean search(JTable table, String searchText, int startRow, int direction) {
		if (StringUtil.isNull(searchText))
			return true;
		// down to search
		if (direction > 0) {
			int row = startRow + 1;
			while (row != startRow) {
				if (row == table.getRowCount())
					row = 0;
				for (int column = 0; column < table.getColumnCount(); column++) {
					if (table.getValueAt(row, column) == null)
						continue;
					String value = table.getValueAt(row, column).toString();
					if (value.indexOf(searchText) != -1) {
						table.getSelectionModel().setSelectionInterval(row, row);
						Rectangle rect = table.getCellRect(row, column, true);
						table.scrollRectToVisible(rect);
						return true;
					}
				}
				if (table.getRowCount() > 1)
					row++;
			}
		} else {
			// up to search
			int row = startRow - 1;
			while (row != startRow) {
				if (row == -1)
					row = table.getRowCount() - 1;
				for (int column = 0; column < table.getColumnCount(); column++) {
					if (table.getValueAt(row, column) == null)
						continue;
					String value = table.getValueAt(row, column).toString();
					if (value.indexOf(searchText) != -1) {
						table.getSelectionModel().setSelectionInterval(row, row);
						Rectangle rect = table.getCellRect(row, column, true);
						table.scrollRectToVisible(rect);
						return true;
					}
				}
				if (table.getRowCount() > 1)
					row--;
			}
		}
		return false;
	}

	/**
	 * 给table注册复制热键（Ctrl+C）
	 * @param table
	 */
	public static void registerCopyKeyAction(final JTable table) {
		ActionListener searchListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String data = copyData(table);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					StringSelection contents = new StringSelection(data);
					clipboard.setContents(contents, contents);
				} catch (Exception e1) {
					UiUtil.showStackTraceDialog(e1, table, "复制", "从table中复制数据时出错！");
					return;
				}
			}
		};

		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK, false);
		table.registerKeyboardAction(searchListener, "copy", ks, JComponent.WHEN_FOCUSED);
	}

	public static String copyData(JTable table) {
		StringBuffer sb = new StringBuffer();
		int[] rows = table.getSelectedRows();
		int[] columns = table.getSelectedColumns();
		for (int row : rows) {
			for (int j = 0; j < columns.length; j++) {
				Object value = table.getValueAt(row, columns[j]);
				sb.append(value == null ? "" : value);
				if (j != columns.length - 1)
					sb.append("\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * 给table注册粘贴热键（Ctrl+V）
	 * @param table
	 */
	public static void registerPasteKeyAction(final JTable table) {
		ActionListener searchListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					String pasteStr = (String) (clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor));
					pasteData(table, pasteStr);
				} catch (Exception e1) {
					UiUtil.showStackTraceDialog(e1, table, "粘贴", "往table中粘贴数据时出错！");
					return;
				}
			}
		};

		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK, false);
		table.registerKeyboardAction(searchListener, "paste", ks, JComponent.WHEN_FOCUSED);
	}

	public static void pasteData(JTable table, String data) {
		int[] rows = table.getSelectedRows();
		int[] columns = table.getSelectedColumns();
		String[] lines = data.split("\\n");
		for (int i = 0; i < rows.length && i < lines.length; i++) {
			String[] columnValues = lines[i].split("\\t");
			for (int j = 0; j < columns.length && j < columnValues.length; j++) {
				if (table.isCellEditable(rows[i], columns[j]))
					table.setValueAt(columnValues[j], rows[i], columns[j]);
			}
		}
	}

}
