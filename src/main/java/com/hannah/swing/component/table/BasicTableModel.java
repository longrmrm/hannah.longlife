package com.hannah.swing.component.table;

import com.hannah.common.model.BaseElement;
import com.hannah.common.util.ObjectUtil;
import com.hannah.common.util.StringUtil;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author longrm
 * @date 2012-12-12
 */
public class BasicTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 2259489459454169375L;

	private List<BasicTableHeader> headerList = null;
	private List dataList = null;

	// record changes
	private List<Object> insertList = new ArrayList<Object>();
	private List<Object> deleteList = new ArrayList<Object>();
	private List<Object> updateList = new ArrayList<Object>();

	// object class
	private Class<?> objectClass;

	private JTable table;

	public BasicTableModel(List<BasicTableHeader> headerList) {
		this(headerList, new ArrayList<Object>());
	}

	public BasicTableModel(List<BasicTableHeader> headerList, List dataList) {
		this.headerList = headerList;
		this.dataList = dataList;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (row >= getRowCount() || column >= getColumnCount())
			return false;
		BasicTableHeader header = headerList.get(column);
		return header.isEditable();
	}

	@Override
	public String getColumnName(int column) {
		if (column >= getColumnCount())
			return null;
		return headerList.get(column).getHeaderValue();
	}

	@Override
	public int getColumnCount() {
		return headerList.size();
	}

	public int getColumnIndex(String identfier) {
		for (int i = 0; i < headerList.size(); i++) {
			BasicTableHeader header = headerList.get(i);
			if (header.getIdentifier().equals(identfier))
				return i;
		}
		return -1;
	}

	@Override
	public int getRowCount() {
		return dataList.size();
	}

	public Class<?> getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(Class<?> objectClass) {
		this.objectClass = objectClass;
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (objectClass != null) {
			try {
				BeanInfo info = Introspector.getBeanInfo(objectClass);
				PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
				for (PropertyDescriptor property : descriptors) {
					String columnIdentifier = headerList.get(columnIndex).getIdentifier();
					if (property.getName().equals(StringUtil.columnToProperty(columnIdentifier))) {
						Class<?> returnType = property.getReadMethod().getReturnType();
						// 基本类型：int、double etc
						if (returnType.isPrimitive())
							return ObjectUtil.getRealClass(returnType.getName());
						else
							return returnType;
					}
				}
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}
		} else if (getRowCount() > 0) {
			Object value = getValueAt(0, columnIndex);
			if (value != null)
				return value.getClass();
		}
		return super.getColumnClass(columnIndex);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= getRowCount() || columnIndex >= getColumnCount())
			return null;
		String identifier = headerList.get(columnIndex).getIdentifier();
		Object row = dataList.get(rowIndex);
		Object value = null;
		if (row instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) row;
			value = map.get(identifier);
		} else
			value = ObjectUtil.getProperty(row, StringUtil.columnToProperty(identifier));
		
		if (table == null || value == null)
			return value;
		
		int viewRow = table.convertRowIndexToView(rowIndex);
		int viewColumn = table.convertColumnIndexToView(columnIndex);
		TableCellEditor cellEditor = table.getColumnModel().getColumn(viewColumn).getCellEditor();
		if (cellEditor instanceof DefaultCellEditor) {
			Component comp = ((DefaultCellEditor)cellEditor).getComponent();
			if (comp instanceof JComboBox) {
				String valueStr = value.toString();
				JComboBox cb = (JComboBox) comp;
				for (int i=0; i<cb.getItemCount(); i++) {
					Object item = cb.getItemAt(i);
					if(item instanceof BaseElement) {
						if (valueStr.equals(((BaseElement)item).getCode()) || valueStr.equals(((BaseElement)item).getName()))
							return item;
					} else if (item != null && item.toString().equals(valueStr))
						return item;
				}
				return null;
			}
		}
		return value;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (rowIndex >= getRowCount() || columnIndex >= getColumnCount())
			return;
		String identifier = headerList.get(columnIndex).getIdentifier();
		Object row = dataList.get(rowIndex);
		// check whether value is changed
		if (ObjectUtil.compareEqual(aValue, getValueAt(rowIndex, columnIndex)))
			return;
		
		update(row);
		
		if (aValue instanceof BaseElement)
			aValue = ((BaseElement)aValue).getCode();
		aValue = ObjectUtil.getValueOfClass(aValue, getColumnClass(columnIndex));
		
		if (row instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) row;
			map.put(identifier, aValue);
		} else {
			ObjectUtil.setProperty(row, StringUtil.columnToProperty(identifier), aValue);
		}
	}


	public List<BasicTableHeader> getHeaderList() {
		return headerList;
	}

	public void setHeaderList(List<BasicTableHeader> headerList) {
		this.headerList = headerList;
	}

	public List getDataList() {
		return dataList;
	}

	public void setDataList(List dataList) {
		this.dataList = dataList;
		clearChanges();
		this.fireTableDataChanged();
	}

	public void clearDatas() {
		dataList.clear();
		clearChanges();
		this.fireTableDataChanged();
	}

	public List<Object> getInsertList() {
		return insertList;
	}

	public List<Object> getDeleteList() {
		return deleteList;
	}

	public List<Object> getUpdateList() {
		return updateList;
	}

	public void clearChanges() {
		insertList.clear();
		deleteList.clear();
		updateList.clear();
	}

	public void insert(Object row) {
		dataList.add(row);
		insertList.add(row);
		this.fireTableDataChanged();
	}

	public void insert(int index, Object row) {
		dataList.add(index, row);
		insertList.add(row);
		this.fireTableDataChanged();
	}

	public void insert(List rows) {
		dataList.addAll(rows);
		insertList.addAll(rows);
		this.fireTableDataChanged();
	}

	public void delete(Object row) {
		if (dataList.contains(row)) {
			dataList.remove(row);
			if (insertList.contains(row))
				insertList.remove(row);
			else {
				deleteList.add(row);
				updateList.remove(row);
			}
			this.fireTableDataChanged();
		}
	}

	public void delete(List rowList) {
		for (int i = 0; i < rowList.size(); i++)
			delete(rowList.get(i));
	}

	public void update(Object row) {
		if (!insertList.contains(row) && !deleteList.contains(row) && !updateList.contains(row))
			updateList.add(row);
		this.fireTableDataChanged();
	}

	public boolean isChanged() {
		return insertList.size() > 0 || deleteList.size() > 0 || updateList.size() > 0;
	}

	public int getRow(Object obj) {
		for (int i = 0; i < dataList.size(); i++) {
			if (obj == dataList.get(i))
				return i;
		}
		return -1;
	}

}
