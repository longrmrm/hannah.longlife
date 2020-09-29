package com.hannah.swing.component.panel;

import com.hannah.common.model.BaseElement;
import com.hannah.swing.component.checktree.BasicCheckTree;
import com.hannah.swing.component.checktree.ElementCheckTree;
import com.hannah.swing.filter.RegexFilter;
import com.hannah.swing.util.TreeUtil;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author longrm
 * @date 2012-5-31
 */
public class ElementTreePanel extends JPanel {

	private static final long serialVersionUID = 6233656579636613920L;

	private JPanel searchPanel = new JPanel();
	private JTextField searchTf = new JTextField(25);
	private JTree tree;
	private boolean isCheckTree = false;

	private String rootName;
	private List<?> elementList;

	public ElementTreePanel(String rootName, List<?> elementList) {
		this.rootName = rootName;
		this.elementList = elementList;
		initInterface();
	}

	public ElementTreePanel(String rootName, List<?> elementList, boolean isCheckTree) {
		this.rootName = rootName;
		this.elementList = elementList;
		this.isCheckTree = isCheckTree;
		initInterface();
	}

	private void initInterface() {
		initSearchPanel();
		initTree();
		searchTf.requestFocus();
		this.setLayout(new BorderLayout());
		this.add(searchPanel, BorderLayout.NORTH);
		this.add(new JScrollPane(tree), BorderLayout.CENTER);
	}

	private void initSearchPanel() {
		searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
		searchPanel.add(new JLabel("查找："));
		searchPanel.add(searchTf);

		searchTf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreeUtil.searchObject(tree, new RegexFilter(".*" + searchTf.getText() + ".*"), 1);
			}
		});
	}

	public void setSearchPanelVisible(boolean visible) {
		searchPanel.setVisible(visible);
		this.invalidate();
	}

	private void initTree() {
		if (isCheckTree) {
			TreeNode root = TreeUtil.createTreeRoot(rootName, elementList, true);
			Object obj = elementList.size() > 0 ? elementList.get(0) : null;
			if (obj != null && obj instanceof BaseElement)
				tree = new ElementCheckTree();
			else
				tree = new BasicCheckTree();
			tree.setModel(new DefaultTreeModel(root));
		} else {
			TreeNode root = TreeUtil.createTreeRoot(rootName, elementList);
			tree = new JTree();
			tree.setModel(new DefaultTreeModel(root));
		}
	}

	public JTextField getSearchTf() {
		return searchTf;
	}

	public JTree getTree() {
		return tree;
	}

}
