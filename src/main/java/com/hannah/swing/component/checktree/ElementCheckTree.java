package com.hannah.swing.component.checktree;

import com.hannah.common.model.BaseElement;

import java.util.Enumeration;
import java.util.List;

public class ElementCheckTree extends BasicCheckTree {

	private static final long serialVersionUID = -4961553560094858619L;
	
	public ElementCheckTree() {
		super();
	}
	
	public ElementCheckTree(String rootName) {
		super(rootName);
	}

	/**
	 * 设置需要勾选的单位节点
	 * 
	 * @param selectedElementList
	 * @param strict
	 *            是否严格判断（为否时只判断代码）
	 */
	public void setSelectedElementList(List<Object> selectedElementList, boolean strict) {
		CheckTreeNode root = (CheckTreeNode) this.getModel().getRoot();
		Enumeration e = root.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			CheckTreeNode node = (CheckTreeNode) e.nextElement();
			BaseElement element = (BaseElement) node.getUserObject();
			node.setSelectionMode(CheckTreeNode.SINGLE_SELECTION);
			if (selectedElementList == null)
				node.setSelected(false);
			else if (strict) {
				if (selectedElementList.contains(element))
					node.setSelected(true);
				else
					node.setSelected(false);
			} else if (!strict) {
				boolean hasCode = false;
				for (Object o : selectedElementList) {
					BaseElement tmpElement = (BaseElement) o;
					if (element.getCode().equals(tmpElement.getCode())) {
						hasCode = true;
						break;
					}
				}
				node.setSelected(hasCode);
			}
			node.setSelectionMode(CheckTreeNode.DIG_IN_SELECTION);
		}
		repaint();
	}

	/**
	 * 设置需要勾选的叶节点
	 * 
	 * @param selectedElementList
	 * @param strict
	 */
	public void setSelectedLeafElementList(List<Object> selectedElementList, boolean strict) {
		CheckTreeNode root = (CheckTreeNode) this.getModel().getRoot();
		CheckTreeNode node = (CheckTreeNode) root.getFirstLeaf();
		while (node != null) {
			BaseElement element = (BaseElement) node.getUserObject();
			if (selectedElementList == null)
				node.setSelected(false);
			else if (strict) {
				if (selectedElementList.contains(element))
					node.setSelected(true);
				else
					node.setSelected(false);
			} else if (!strict) {
				boolean hasCode = false;
				for (Object o : selectedElementList) {
					BaseElement tmpElement = (BaseElement) o;
					if (element.getCode().equals(tmpElement.getCode())) {
						hasCode = true;
						break;
					}
				}
				node.setSelected(hasCode);
			}
			node = (CheckTreeNode) node.getNextLeaf();
		}
		repaint();
	}

}
