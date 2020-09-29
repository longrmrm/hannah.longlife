package com.hannah.swing.component.combobox;

import com.hannah.common.util.StringUtil;
import com.hannah.swing.filter.Filter;
import com.hannah.swing.filter.RegexFilter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName: FilterComboBox
 * @Description: 可直接录入过滤的combobox
 * @date: 2011-4-15 下午03:21:54
 * @version: V1.0
 * @since: 1.0
 * @author: longrm
 * @modify:
 */
public class FilterComboBox extends JComboBox {

	private static final long serialVersionUID = 389144608932512217L;

	private Filter filter = new RegexFilter(".*");	// 默认包含过滤

	public FilterComboBox() {
		setModel(new FilterComboBoxModel(new ArrayList()));
		setEditor(new MyEditor());
		setEditable(true);
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	private class MyEditor extends AbstractComboBoxEditor {

		@Override
		protected void fireTextChanged() {
			FilterComboBox.this.setPopupVisible(true);
			FilterComboBoxModel model = (FilterComboBoxModel) FilterComboBox.this.getModel();
			if (filter != null)
				filter.setFilterObject(".*" + StringUtil.escapeRegex(text.getText()) + ".*");
			model.updateFilteredItems();
//			FilterComboBox.this.setSelectedIndex(0);
		}
	}

	public class FilterComboBoxModel extends AbstractListModel implements MutableComboBoxModel {

		private static final long serialVersionUID = -1222709151723343204L;

		private List items;
		private List filteredItems;
		private Object selectedItem;

		public FilterComboBoxModel(List items) {
			this.items = new ArrayList(items);
			filteredItems = new ArrayList(items.size());
			updateFilteredItems();
		}

		public void addElementList(ArrayList list) {
			if (list == null || list.size() == 0) {
				return;
			}
			items.addAll(list);
			updateFilteredItems();
		}

		public void addElement(Object obj) {
			items.add(obj);
			updateFilteredItems();
		}

		public void removeElement(Object obj) {
			items.remove(obj);
			updateFilteredItems();
		}

		public void removeElementAt(int index) {
			items.remove(index);
			updateFilteredItems();
		}

		public void insertElementAt(Object obj, int index) {
			items.add(index, obj);
			updateFilteredItems();
		}

		protected void updateFilteredItems() {
			fireIntervalRemoved(this, 0, filteredItems.size());
			filteredItems.clear();

			if (filter == null)
				filteredItems.addAll(items);
			else {
				for (Iterator iterator = items.iterator(); iterator.hasNext();) {
					Object item = iterator.next();
					if (filter.accept(item)) {
						filteredItems.add(item);
					}
				}
			}
			fireIntervalAdded(this, 0, filteredItems.size());
		}

		public int getSize() {
			return filteredItems.size();
		}

		public Object getElementAt(int index) {
			return filteredItems.get(index);
		}

		public Object getSelectedItem() {
			return selectedItem;
		}

		public void setSelectedItem(Object val) {
			if ((selectedItem == null) && (val == null))
				return;
			else if ((selectedItem != null) && selectedItem.equals(val))
				return;
			else if ((val != null) && val.equals(selectedItem))
				return;

			selectedItem = val;
			fireContentsChanged(this, -1, -1);
		}
	}

}