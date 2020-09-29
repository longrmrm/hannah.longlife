package com.hannah.swing.component.dialog;

import com.hannah.swing.component.button.ImageButton;
import com.hannah.swing.component.checktree.BasicCheckTree;
import com.hannah.swing.component.panel.ElementTreePanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ElementTreeSelectDialog extends BasicDialog {

	private static final long serialVersionUID = 8708208890965917917L;

	private JToolBar toolBar;
	private JButton okButton;
	private JButton cancelButton;
	private ElementTreePanel treePanel;

	private boolean onlySelectLeaf = true;
	private boolean allowNull = false; // 允许为空
	private boolean ok = false;

	public ElementTreeSelectDialog(String rootName, List<?> elementList) {
		this(rootName, elementList, false, true);
	}

	public ElementTreeSelectDialog(String rootName, List<?> elementList, boolean isCheckTree) {
		this(rootName, elementList, isCheckTree, true);
	}

	public ElementTreeSelectDialog(String rootName, List<?> elementList, boolean isCheckTree, boolean onlySelectLeaf) {
		this.setTitle("选择" + rootName);
		this.setModal(true);
		treePanel = new ElementTreePanel(rootName, elementList, isCheckTree);
		this.onlySelectLeaf = onlySelectLeaf;
		initInterface();
	}

	private void initInterface() {
		initToolBar();
		treePanel.getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!(treePanel.getTree() instanceof BasicCheckTree) && e.getClickCount() == 2)
					doOk();
			}
		});

		this.setLayout(new BorderLayout());
		this.add(toolBar, BorderLayout.NORTH);
		this.add(treePanel, BorderLayout.CENTER);
		this.setSize(400, 650);
		this.setLocationRelativeTo(null);
	}

	private void initToolBar() {
		toolBar = new JToolBar();
		okButton = new ImageButton("Ok");
		cancelButton = new ImageButton("Cancel");
		toolBar.add(okButton);
		toolBar.add(cancelButton);

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == okButton)
					doOk();
				else if (e.getSource() == cancelButton) {
					getTree().clearSelection();
					ElementTreeSelectDialog.this.dispose();
				}
			}
		};
		okButton.addActionListener(listener);
		cancelButton.addActionListener(listener);
	}

	private void doOk() {
		ok = true;

		if (allowNull || getSelectedElementList().size() > 0)
			this.dispose();
		else {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePanel.getTree().getLastSelectedPathComponent();
			if (node == null || treePanel.getTree() instanceof BasicCheckTree)
				JOptionPane.showMessageDialog(this, "没有选中任何记录！", "选择", JOptionPane.ERROR_MESSAGE);
			else {
				// if (onlySelectLeaf && !node.isLeaf())
				// JOptionPane.showMessageDialog(this, "请选择叶节点！", "选择",
				// JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	@Override
	public void setVisible(boolean b) {
		ok = false;
		super.setVisible(b);
	}

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public boolean isAllowNull() {
		return allowNull;
	}

	public void setAllowNull(boolean allowNull) {
		this.allowNull = allowNull;
	}

	public JTree getTree() {
		return treePanel.getTree();
	}

	public List getSelectedElementList() {
		JTree tree = treePanel.getTree();
		if (tree instanceof BasicCheckTree) {
			if (onlySelectLeaf)
				return ((BasicCheckTree) tree).getSelectedLeafElementList();
			else
				return ((BasicCheckTree) tree).getSelectedElementList();
		} else {
			List selectedElemenetList = new ArrayList();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (node == null)
				return selectedElemenetList;
			else if ((!onlySelectLeaf && !node.isRoot()) || node.isLeaf())
				selectedElemenetList.add(node.getUserObject());
			return selectedElemenetList;
		}
	}

	public Object getSelectedElement() {
		List selectedElemenetList = getSelectedElementList();
		if (selectedElemenetList.size() > 0)
			return selectedElemenetList.get(0);
		else
			return null;
	}

}
