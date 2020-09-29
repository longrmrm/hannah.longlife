package com.hannah.swing.util;

import com.hannah.common.model.BaseElement;
import com.hannah.common.util.StringUtil;
import com.hannah.swing.component.checktree.CheckTreeNode;
import com.hannah.swing.filter.AbstractFilter;
import com.hannah.swing.filter.Filter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class TreeUtil {

	public static DefaultMutableTreeNode createTreeRoot(String rootName, List<?> elementList) {
		return createTreeRoot(rootName, elementList, false);
	}

	public static DefaultMutableTreeNode createTreeRoot(String rootName, List<?> elementList, boolean isCheckTree) {
		Object obj = elementList.size() > 0 ? elementList.get(0) : null;
		DefaultMutableTreeNode root = isCheckTree ? new CheckTreeNode() : new DefaultMutableTreeNode();
		if (obj != null && obj instanceof BaseElement) {
			BaseElement elementRoot = new BaseElement();
			elementRoot.setName(rootName);
			root.setUserObject(elementRoot);
		} else
			root.setUserObject(rootName);

		try {
			return createTreeRoot(root, elementList);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 生成树根节点
	 * @param root
	 * @param elementList 所有元素list
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static DefaultMutableTreeNode createTreeRoot(DefaultMutableTreeNode root, List<?> elementList)
			throws InstantiationException, IllegalAccessException {
		DefaultMutableTreeNode priorNode = root;
		for (Object obj : elementList) {
			// 如果选中，加到树结构中
			DefaultMutableTreeNode node = root.getClass().newInstance(); //new DefaultMutableTreeNode(obj);
			node.setUserObject(obj);
			if (obj instanceof BaseElement) {
				BaseElement element = (BaseElement) obj;
				BaseElement priorElement = (BaseElement) priorNode.getUserObject();
				if (StringUtil.isNull(element.getParentCode()))
					root.add(node);
				else if (element.getParentCode().equals(priorElement.getCode()))
					priorNode.add(node);
				else {
					// 循环取上个节点的父节点，从树深处退出
					while (priorNode.getParent() != null) {
						priorNode = (DefaultMutableTreeNode) priorNode.getParent();
						priorElement = (BaseElement) priorNode.getUserObject();
						if (element.getParentCode().equals(priorElement.getCode())) {
							priorNode.add(node);
							break;
						}
					}
					if (!priorNode.isNodeChild(node))
						  root.add(node);
				}
				priorNode = node;
			} else
				root.add(node);
		}
		return root;
	}

	public static void expandAll(JTree tree, boolean expand) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root), expand);
	}

	/**
	 * 遍历parent的所有子节点并展开
	 * @param tree
	 * @param parent
	 * @param expand
	 */
	public static void expandAll(JTree tree, TreePath parent, boolean expand) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}
		if (expand)
			tree.expandPath(parent);
		else
			tree.collapsePath(parent);
	}

	/**
	 * 展开到某个节点
	 * @param tree
	 * @param node
	 */
	public static void expandNode(JTree tree, DefaultMutableTreeNode node) {
		TreePath path = getTreePath(tree, node);
		expandAll(tree, false);
		tree.setSelectionPath(path);
		tree.expandPath(path);
		tree.scrollPathToVisible(path);
	}

	public static boolean searchObject(JTree tree, final Object obj, int direction) {
		Filter filter = new AbstractFilter() {
			@Override
			public boolean accept(Object obj2) {
				if (obj == null)
					return false;
				return obj.equals(obj2);
			}
		};
		return searchObject(tree, filter, direction);
	}

	public static boolean searchObject(JTree tree, Filter filter, int direction) {
		return searchObject(tree, filter, (DefaultMutableTreeNode) tree.getLastSelectedPathComponent(), direction);
	}

	public static boolean searchObject(JTree tree, Filter filter, DefaultMutableTreeNode startNode, int direction) {
		DefaultMutableTreeNode node = getTreeNode(tree, filter, startNode, direction);
		if (node == null)
			return false;

		expandNode(tree, node);
		return true;
	}

	public static TreePath getTreePath(JTree tree, DefaultMutableTreeNode node) {
		TreeNode[] nodes = node.getPath();
		TreePath treePath = null;
		for (TreeNode tmpNode : nodes)
			treePath = treePath == null ? new TreePath(tmpNode) : treePath.pathByAddingChild(tmpNode);
		return treePath;
	}

	public static DefaultMutableTreeNode getTreeNode(JTree tree, final Object obj, int direction) {
		Filter filter = new AbstractFilter() {
			@Override
			public boolean accept(Object obj2) {
				if (obj == null)
					return false;
				return obj.equals(obj2);
			}
		};
		return getTreeNode(tree, filter, direction);
	}

	public static DefaultMutableTreeNode getTreeNode(JTree tree, Filter filter, int direction) {
		if (filter == null)
			return null;

		// 起始位置
		DefaultMutableTreeNode startNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (startNode == null)
			startNode = (DefaultMutableTreeNode) tree.getModel().getRoot();

		if (filter.accept(startNode.getUserObject()))
			return startNode;

		return getTreeNode(tree, filter, startNode, direction);
	}

	/**
	 * 取出obj所在的树节点
	 * @param tree
	 * @param filter 查找规则器
	 * @param startNode 起始节点
	 * @param direction 查找方向（大于0往下找，否则往上找）
	 * @return
	 */
	public static DefaultMutableTreeNode getTreeNode(JTree tree, Filter filter, DefaultMutableTreeNode startNode,
			int direction) {
		if (startNode == null)
			startNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
		// 往下找
		if (direction > 0) {
			DefaultMutableTreeNode node = startNode.getNextNode();
			if (node == null)
				node = (DefaultMutableTreeNode) tree.getModel().getRoot();
			while (node != startNode) {
				if (filter.accept(node.getUserObject()))
					return node;
				node = node.getNextNode();
				if (node == null)
					node = (DefaultMutableTreeNode) tree.getModel().getRoot();
			}
		} else {
			DefaultMutableTreeNode node = startNode.getPreviousNode();
			if (node == null)
				node = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) tree.getModel().getRoot()).getLastChild();
			while (node != startNode) {
				if (filter.accept(node.getUserObject()))
					return node;
				node = node.getPreviousNode();
				if (node == null)
					node = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) tree.getModel().getRoot()).getLastChild();
			}
		}
		if (filter.accept(startNode.getUserObject()))
			return startNode;
		return null;
	}

	/**
	 * 往上追溯父节点，取出来放在list里
	 * @param tree
	 * @param obj
	 * @return
	 */
	public static List<Object> getParentObjectList(JTree tree, Object obj) {
		List<Object> objList = new ArrayList<Object>();
		DefaultMutableTreeNode node = getTreeNode(tree, obj, 1);
		while (node != null) {
			objList.add(0, node.getUserObject());
			node = (DefaultMutableTreeNode) node.getParent();
		}
		return objList;
	}

}
