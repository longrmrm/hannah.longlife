package com.hannah.swing.component.checktree;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

public class CheckTreeNode extends DefaultMutableTreeNode {

	public final static int SINGLE_SELECTION = 0; // 勾选时只选当前节点
	public final static int DIG_IN_SELECTION = 4; // 勾选时同时勾选当前节点的所有子节点

	protected int selectionMode;
	protected boolean isSelected;
	protected boolean expanded;
	protected boolean showCheck = true;
	private boolean parentSelected = true;

	private boolean childSelected = true;

	public boolean isShowCheck() {
		return showCheck;
	}

	public void setShowCheck(boolean showCheck) {
		this.showCheck = showCheck;
	}

	/**
	 * @return the childSelected
	 */
	public boolean isChildSelected() {
		return childSelected;
	}

	/**
	 * @param childSelected
	 *            the childSelected to set
	 */
	public void setChildSelected(boolean childSelected) {
		this.childSelected = childSelected;
	}

	/**
	 * @return the parentSelected
	 */
	public boolean isParentSelected() {
		return parentSelected;
	}

	/**
	 * @param parentSelected
	 *            the parentSelected to set
	 */
	public void setParentSelected(boolean parentSelected) {
		this.parentSelected = parentSelected;
	}

	public CheckTreeNode() {
		this(null);
	}

	public CheckTreeNode(Object userObject) {
		this(userObject, true, false);
	}

	public CheckTreeNode(Object userObject, boolean allowsChildren, boolean isSelected) {
		super(userObject, allowsChildren);
		this.isSelected = isSelected;
		setSelectionMode(DIG_IN_SELECTION);
	}

	public void setSelectionMode(int mode) {
		selectionMode = mode;
	}

	public int getSelectionMode() {
		return selectionMode;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;

		if (isChildSelected() && (selectionMode == DIG_IN_SELECTION) && (children != null)) {
			Enumeration e = children.elements();
			while (e.hasMoreElements()) {
				CheckTreeNode node = (CheckTreeNode) e.nextElement();
				node.setSelected(isSelected);
			}
		}

		if (selectionMode == DIG_IN_SELECTION && isParentSelected()) {
			// 循环递归取父节点，判断是否该选中
			CheckTreeNode parentNode = (CheckTreeNode) parent;
			while (parentNode != null) {
				Enumeration e = parentNode.children();
				// 标志当前父节点是否有选中的子节点
				boolean hasSelectedChild = false;
				while (e.hasMoreElements()) {
					CheckTreeNode node = (CheckTreeNode) e.nextElement();
					if (node.isSelected) {
						hasSelectedChild = true;
						break;
					}
				}
				parentNode.setSelectionMode(SINGLE_SELECTION);
				parentNode.setSelected(hasSelectedChild);
				parentNode.setSelectionMode(DIG_IN_SELECTION);
				parentNode = (CheckTreeNode) parentNode.parent;
			}
		}
	}

	public void setExpanded(boolean isExpanded) {
		this.expanded = isExpanded;

		if ((selectionMode == DIG_IN_SELECTION) && (children != null)) {
			Enumeration e = children.elements();
			while (e.hasMoreElements()) {
				CheckTreeNode node = (CheckTreeNode) e.nextElement();
				node.setExpanded(isExpanded);
			}
		}
	}

	public boolean isExpanded() {
		return expanded;
	}

	public boolean isSelected() {
		return isSelected;
	}
}