package com.hannah.swing.component.combobox;

import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;

/**
 * @ClassName: TreePopup
 * @Description: 重画treeComboBox下拉列表为树结构
 * @date: 2011-4-15 下午03:35:01
 * @version: V1.0
 * @since: 1.0
 * @author: longrm
 * @modify:
 */
public class TreePopup extends JPopupMenu implements ComboPopup {

	private static final long serialVersionUID = 1L;

	protected TreeComboBox comboBox;

	protected JScrollPane scrollPane;

	protected MouseMotionListener mouseMotionListener;

	protected MouseListener mouseListener;

	public TreePopup(final JComboBox comboBox) {
		this.comboBox = (TreeComboBox) comboBox;
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setLayout(new BorderLayout());
		this.setLightWeightPopupEnabled(comboBox.isLightWeightPopupEnabled());

		JTree tree = this.comboBox.getTree();
		if (tree != null) {
			this.scrollPane = new JScrollPane(tree);
			this.scrollPane.setBorder(null);
			this.scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(100, 10));
			this.scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 100));
			this.add(this.scrollPane, BorderLayout.CENTER);
		}
	}

	public void show() {
		this.updatePopup();
		try {
//			this.hide();
			this.show(comboBox, 0, comboBox.getHeight());
		} catch (IllegalComponentStateException e) {
			// 这里有可能会抛出一个异常，可以不用处理
		}
		this.comboBox.getEditor().getEditorComponent().requestFocus();
	}

	public void hide() {
		this.setVisible(false);
	}

	protected JList list = new JList();

	public JList getList() {
		return list;
	}

	public MouseMotionListener getMouseMotionListener() {
		if (mouseMotionListener == null) {
			mouseMotionListener = new MouseMotionAdapter() {
			};
		}
		return mouseMotionListener;
	}

	public KeyListener getKeyListener() {
		return null;
	}

	public void uninstallingUI() {
	}

	/**
	 * Implementation of ComboPopup.getMouseListener().
	 * 
	 * @return a <code>MouseListener</code> or null
	 * @see ComboPopup#getMouseListener
	 */
	public MouseListener getMouseListener() {
		if (mouseListener == null) {
			mouseListener = new InvocationMouseHandler();
		}
		return mouseListener;
	}

	protected void togglePopup() {
		if (this.isVisible())
			this.hide();
		else
			this.show();
	}

	protected void updatePopup() {
		int width = getPreferredSize().width;
		if (comboBox.getSize().width > width)
			width = comboBox.getSize().width;
		if (comboBox.getTree().getPreferredSize().getWidth() > width)
			width = (int) comboBox.getTree().getPreferredSize().getWidth() + 50;
		this.setPreferredSize(new Dimension(width, getPreferredSize().height));
		Object selectedObj = comboBox.getSelectedItem();
		if (selectedObj != null && selectedObj instanceof TreePath) {
			TreePath tp = (TreePath) selectedObj;
			comboBox.getTree().setSelectionPath(tp);
		}
	}

	@Override
	protected void firePopupMenuWillBecomeVisible() {
		double x = comboBox.getLocationOnScreen().getX();
		double y = comboBox.getLocationOnScreen().getY();
		double height = this.getPreferredSize().getHeight();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// 如果往下弹出树超出了屏幕，则改为往上弹出
		if (y + height > screenSize.getHeight()) {
			Point p = new Point();
			p.setLocation(x, y - height);
			this.setLocation(p);
		}
		super.firePopupMenuWillBecomeVisible();
	}

	protected class InvocationMouseHandler extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (!SwingUtilities.isLeftMouseButton(e) || !comboBox.isEnabled()) {
				return;
			}
			if (comboBox.isEditable()) {
				Component comp = comboBox.getEditor().getEditorComponent();
				if ((!(comp instanceof JComponent)) || ((JComponent) comp).isRequestFocusEnabled()) {
					comp.requestFocus();
				}
			} else if (comboBox.isRequestFocusEnabled()) {
				comboBox.requestFocus();
			}
			togglePopup();
		}
	}

}
