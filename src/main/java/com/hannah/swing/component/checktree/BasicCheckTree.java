package com.hannah.swing.component.checktree;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class BasicCheckTree extends JTree {

	private static final long serialVersionUID = -5389628385687306873L;

	private String rootName;

	private List<Object> elementList;

	private final int DEFAULT_ROWHEIGHT = 20; // 默认行高

	public BasicCheckTree() {
		super();
		init();
	}

	public BasicCheckTree(String rootName) {
		super();
		setRootName(rootName);
		init();
	}

	private void init() {
		// 点击时改变选中状态
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 取出点击的节点路径，为null时未点中
				int x = e.getX();
				int y = e.getY();
				int row = getRowForLocation(x, y);
				TreePath path = getPathForRow(row);
				if (path == null)
					return;
				CheckTreeNode node = (CheckTreeNode) getLastSelectedPathComponent();
				if (node == null)
					return;
				node.setSelected(!node.isSelected());
				repaint();
			}
		});
		// 单选
		DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
		selectionModel.setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
		this.setSelectionModel(selectionModel);
		// 设置渲染风格
		this.setModel(new DefaultTreeModel(new CheckTreeNode(rootName)));
		this.setCellRenderer(new CheckTreeCellRenderer());
		this.setRowHeight(DEFAULT_ROWHEIGHT);
	}

	public String getRootName() {
		return rootName;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	public List<Object> getElementList() {
		if (elementList == null) {
			elementList = new ArrayList<Object>();
			CheckTreeNode root = (CheckTreeNode) this.getModel().getRoot();
			Enumeration<?> e = root.depthFirstEnumeration();
			while (e.hasMoreElements()) {
				CheckTreeNode node = (CheckTreeNode) e.nextElement();
				if (node == root)
					continue;
				elementList.add(node.getUserObject());
			}
		}
		return elementList;
	}

	public void setElementList(List<Object> elementList) {
		this.elementList = elementList;
	}

	@Override
	public void setModel(TreeModel newModel) {
		super.setModel(newModel);
		elementList = null;
	}

	/**
	 * @return 选中节点列表
	 */
	public List<Object> getSelectedElementList() {
		List<Object> selectedList = new ArrayList<Object>();
		CheckTreeNode root = (CheckTreeNode) this.getModel().getRoot();
		Enumeration<?> e = root.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			CheckTreeNode node = (CheckTreeNode) e.nextElement();
			if (node == root)
				continue;
			if (node.isSelected())
				selectedList.add(node.getUserObject());
		}
		return selectedList;
	}

	/**
	 * @return 选中子节点列表
	 */
	public List<Object> getSelectedLeafElementList() {
		List<Object> selectedList = new ArrayList<Object>();
		CheckTreeNode root = (CheckTreeNode) this.getModel().getRoot();
		CheckTreeNode node = (CheckTreeNode) root.getFirstLeaf();
		while (node != null) {
			if (node.isSelected())
				selectedList.add(node.getUserObject());
			node = (CheckTreeNode) node.getNextLeaf();
		}
		return selectedList;
	}

}
