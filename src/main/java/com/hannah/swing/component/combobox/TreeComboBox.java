package com.hannah.swing.component.combobox;

import com.sun.java.swing.plaf.motif.MotifComboBoxUI;
import com.sun.java.swing.plaf.windows.WindowsComboBoxUI;

import javax.swing.*;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

/**
 * @ClassName: TreeComboBox
 * @Description: 下拉树显示的comboBox
 * @date: 2011-4-15 下午03:27:05
 * @version: V1.0
 * @since: 1.0
 * @author: longrm
 * @modify:
 */
public class TreeComboBox extends JComboBox {

	private static final long serialVersionUID = 1L;

	// 显示用的树
	private JTree tree;

	// 是否只选择叶节点
	private boolean onlySelectLeaf = false;

	// 临时焦点转移flag
	protected boolean temporary = false;

	public TreeComboBox() {
		this(new JTree());
	}

	public TreeComboBox(final JTree tree) {
		this.setTree(tree);
	}

	private void doSelectTreeNode() {
		TreePath treePath = tree.getSelectionPath();
		if (treePath == null)
			return;

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
		// 判断是否只有是树叶时才选中这个节点
		if (!isOnlySelectLeaf() || node.isLeaf()) {
			setSelectedItem(treePath);
			temporary = true;
			setPopupVisible(!isPopupVisible());
			MenuSelectionManager.defaultManager().clearSelectedPath();
		}
	}

	public JTree getTree() {
		return tree;
	}

	public void setTree(final JTree tree) {
		this.tree = tree;
		if (tree != null) {
			this.setSelectedItem(tree.getSelectionPath());
			this.setRenderer(new TreeComboBoxRenderer());
		}

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					TreePath path = tree.getPathForLocation(e.getX(), e.getY());
					// 选中节点时触发
					if (path != null)
						doSelectTreeNode();
				}
			}
		});

		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					doSelectTreeNode();
			}
		});
		this.updateUI();
	}

	public boolean isOnlySelectLeaf() {
		return onlySelectLeaf;
	}

	public void setOnlySelectLeaf(boolean onlySelectLeaf) {
		this.onlySelectLeaf = onlySelectLeaf;
	}

	/**
	 * 设置当前选择的树路径
	 * @param path
	 *            TreePath
	 */
	public void setSelectedItem(TreePath path) {
		this.tree.setSelectionPath(path);
		this.getModel().setSelectedItem(path);
	}

	@Override
	public void setSelectedItem(Object obj) {
		if (obj == null || obj instanceof TreePath) {
			setSelectedItem((TreePath) obj);
			return;
		}

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
		Enumeration<?> e = root.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (node == root)
				continue;
			if (obj.toString().equals(node.getUserObject().toString())) {
				setSelectedItem(tree.getPathForRow(0).pathByAddingChild(node));
				return;
			}
		}
	}

	/**
	 * 获得当前选中的节点的值
	 * @return o Object
	 */
	public Object getSelectedObject() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) getTree()
				.getLastSelectedPathComponent();
		if (node == null)
			return null;
		else if (!onlySelectLeaf || node.isLeaf())
			return node.getUserObject();
		else
			return null;
	}

	public void updateUI() {
		ComboBoxUI cui = (ComboBoxUI) UIManager.getUI(this);
		if (cui instanceof MetalComboBoxUI) {
			cui = new MetalTreeComboBoxUI();
		} else if (cui instanceof MotifComboBoxUI) {
			cui = new MotifTreeComboBoxUI();
		} else if (cui instanceof WindowsComboBoxUI) {
			cui = new WindowsTreeComboBoxUI();
			// } else if (cui instanceof UfgovSubstanceComboBoxUI) {
			// cui = new UfgovTreeComboBoxUI(this);
		}
		setUI(cui);
	}

	// 外观UI
	class MetalTreeComboBoxUI extends MetalComboBoxUI {
		@Override
		protected ComboPopup createPopup() {
			return new TreePopup(comboBox);
		}
	}

	class WindowsTreeComboBoxUI extends WindowsComboBoxUI {
		@Override
		protected ComboPopup createPopup() {
			return new TreePopup(comboBox);
		}
	}

	class MotifTreeComboBoxUI extends MotifComboBoxUI {
		@Override
		protected ComboPopup createPopup() {
			return new TreePopup(comboBox);
		}
	}

	// class UfgovTreeComboBoxUI extends UfgovSubstanceComboBoxUI {
	// public UfgovTreeComboBoxUI(JComboBox jcombobox) {
	// super(jcombobox);
	// }
	//
	// @Override
	// protected ComboPopup createPopup() {
	// return new TreePopup(comboBox);
	// }
	// }

	private class TreeComboBoxRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
			if (value != null) {
				TreePath path = (TreePath) value;
				TreeNode node = (TreeNode) path.getLastPathComponent();
				value = node;
				TreeCellRenderer treeCellRenderer = tree.getCellRenderer();
				JLabel lb = (JLabel) treeCellRenderer.getTreeCellRendererComponent(tree, value, isSelected, false,
						node.isLeaf(), index, cellHasFocus);
				return lb;
			}
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}
	}
}
