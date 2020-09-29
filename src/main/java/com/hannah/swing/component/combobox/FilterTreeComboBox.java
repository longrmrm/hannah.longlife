package com.hannah.swing.component.combobox;

import com.hannah.common.util.StringUtil;
import com.hannah.swing.filter.Filter;
import com.hannah.swing.filter.RegexFilter;
import com.hannah.swing.util.TreeUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.*;

/**
 * @ClassName: FilterTreeComboBox
 * @Description: 可直接录入定位到相应树节点的treeComboBox
 * @date: 2011-4-15 下午03:22:42
 * @version: V1.0
 * @since: 1.0
 * @author: longrm
 * @modify:
 */
public class FilterTreeComboBox extends TreeComboBox {

	private static final long serialVersionUID = 4116861717370209082L;

	private Filter filter = new RegexFilter(".*");

	public FilterTreeComboBox() {
		this(new JTree());
	}

	public FilterTreeComboBox(JTree tree) {
		super(tree);
		setEditor(new MyEditor());
		setEditable(true);
	}

	@Override
	public void setTree(JTree tree) {
		super.setTree(tree);

		ActionListener searchListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("UP"))
					TreeUtil.searchObject(getTree(), filter, -1);
				else if (e.getActionCommand().equals("DOWN"))
					TreeUtil.searchObject(getTree(), filter, 1);
			}
		};
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK, false);
		tree.registerKeyboardAction(searchListener, "UP", ks, JComponent.WHEN_FOCUSED);
		ks = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK, false);
		tree.registerKeyboardAction(searchListener, "DOWN", ks, JComponent.WHEN_FOCUSED);
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public void filter() {
		setPopupVisible(true);
		JTextField text = (JTextField) getEditor().getEditorComponent();
		if (filter != null)
			filter.setFilterObject(StringUtil.escapeRegex(text.getText()) + ".*");
		boolean flag = TreeUtil.searchObject(getTree(), filter,
				(DefaultMutableTreeNode) getTree().getModel().getRoot(), 1);
		if (!flag || text.getText().equals(""))
			getTree().clearSelection();
	}

	private class MyEditor extends AbstractComboBoxEditor {

		public MyEditor() {
			this.getEditorComponent().addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {
				}
				
				@Override
				public void focusGained(FocusEvent e) {
					if (!temporary && !isPopupVisible())
						setPopupVisible(true);
					temporary = false;
				}
			});
			
			text.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					// 下拉列表显示时，向tree转发按键事件
					if (getUI().isPopupVisible(FilterTreeComboBox.this)) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.CTRL_DOWN_MASK
								|| e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN
								|| e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
							getTree().dispatchEvent(e);
						} else
							super.keyPressed(e);
					}
				}
			});
		}

		@Override
		protected String getShowText(Object item) {
			// 显示 树节点 的对象
			if (item instanceof TreePath) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((TreePath) item).getLastPathComponent();
				if (node == null || node.getUserObject() == null)
					return "";
				else
					return node.getUserObject().toString();
			} else
				return super.getShowText(item);
		}

		@Override
		protected void fireTextChanged() {
			filter();
		}
	}

}
