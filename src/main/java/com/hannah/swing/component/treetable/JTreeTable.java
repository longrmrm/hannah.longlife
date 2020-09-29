package com.hannah.swing.component.treetable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.EventObject;

public class JTreeTable extends JTable {

	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_HEIGHT = 23;

	/** A subclass of JTree. */
	protected TreeTableCellRenderer tree;

	public JTreeTable() {
		super.setRowHeight(DEFAULT_HEIGHT);
	}

	public JTreeTable(TreeTableModel treeTableModel) {
		super();
		super.setRowHeight(DEFAULT_HEIGHT);
		setTreeTableModel(treeTableModel);
	}

	public void setTreeTableModel(TreeTableModel treeTableModel) {
		// Create the tree. It will be used as a renderer and editor.
		tree = new TreeTableCellRenderer(treeTableModel);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setRowHeight(getRowHeight());
		// Install a tableModel representing the visible rows in the tree.
		super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

		// Force the JTable and JTree to share their row selection models.
		ListToTreeSelectionModelWrapper selectionWrapper = new ListToTreeSelectionModelWrapper();
		tree.setSelectionModel(selectionWrapper);
		setSelectionModel(selectionWrapper.getListSelectionModel());

		// Install the tree editor renderer and editor.
		setDefaultRenderer(TreeTableModel.class, tree);
		setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

		// No grid.
		// setShowGrid(false);

		// No intercell spacing
		// setIntercellSpacing(new Dimension(0, 0));
	}

	public void updateUI() {
		super.updateUI();
		if (tree != null) {
			tree.updateUI();
		}
		// Use the tree's default foreground and background colors in the table.
		LookAndFeel.installColorsAndFont(this, "Tree.background", "Tree.foreground", "Tree.font");
	}

	public int getEditingRow() {
		return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 : editingRow;
	}

	public void setRowHeight(int rowHeight) {
		super.setRowHeight(rowHeight);
		if (tree != null && tree.getRowHeight() != rowHeight) {
			tree.setRowHeight(getRowHeight());
		}
	}

	public TreeTableCellRenderer getTree() {
		return tree;
	}

	public class TreeTableCellRenderer extends JTree implements TableCellRenderer {
		/** Last table/tree row asked to renderer. */
		protected int visibleRow;

		public TreeTableCellRenderer(TreeModel model) {
			super(model);
			setCellRenderer(new MyTreeCellRenderer());
		}

		public void expandAll(boolean expand) {
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getModel().getRoot();
			expandAll(new TreePath(root), expand);
		}

		/**
		 * 遍历parent的所有子节点并展开
		 * 
		 * @param parent
		 * @param expand
		 */
		public void expandAll(TreePath parent, boolean expand) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastPathComponent();
			if (node.getChildCount() >= 0) {
				for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
					DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
					TreePath path = parent.pathByAddingChild(n);
					expandAll(path, expand);
				}
			}
			if (expand)
				this.expandPath(parent);
			else
				this.collapsePath(parent);
		}

		/**
		 * updateUI is overridden to set the colors of the Tree's renderer to
		 * match that of the table.
		 */
		public void updateUI() {
			super.updateUI();
			// Make the tree's cell renderer use the table's cell selection
			// colors.
			TreeCellRenderer tcr = getCellRenderer();
			if (tcr instanceof DefaultTreeCellRenderer) {
				DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
				// For 1.1 uncomment this, 1.2 has a bug that will cause an
				// exception to be thrown if the border selection color is null.
				// dtcr.setBorderSelectionColor(null);
				dtcr.setTextSelectionColor(UIManager.getColor("Table.selectionForeground"));
				dtcr.setBackgroundSelectionColor(UIManager.getColor("Table.selectionBackground"));
			}
		}

		/**
		 * Sets the row height of the tree, and forwards the row height to the
		 * table.
		 */
		public void setRowHeight(int rowHeight) {
			if (rowHeight > 0) {
				super.setRowHeight(rowHeight);
				if (JTreeTable.this != null && JTreeTable.this.getRowHeight() != rowHeight) {
					JTreeTable.this.setRowHeight(getRowHeight());
				}
			}
		}

		/**
		 * This is overridden to set the height to match that of the JTable.
		 */
		public void setBounds(int x, int y, int w, int h) {
			super.setBounds(x, 0, w, JTreeTable.this.getHeight());
		}

		/**
		 * Sublcassed to translate the graphics such that the last visible row
		 * will be drawn at 0,0.
		 */
		public void paint(Graphics g) {
			g.translate(0, -visibleRow * getRowHeight());
			super.paint(g);
		}

		/**
		 * TreeCellRenderer method. Overridden to update the visible row.
		 */
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (isSelected)
				setBackground(table.getSelectionBackground());
			else
				setBackground(table.getBackground());

			visibleRow = row;
			return this;
		}

		class MyTreeCellRenderer extends DefaultTreeCellRenderer {
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
					boolean expanded, boolean leaf, int row, boolean hasFocus) {
				setBackgroundSelectionColor(null);
				setBackgroundNonSelectionColor(null);
				setTextSelectionColor(UIManager.getColor("Table.selectionForeground"));
				return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
						hasFocus);
			}
		}
	}

	public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor {
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int r, int c) {
			return tree;
		}

		public boolean isCellEditable(EventObject e) {
			if (e instanceof MouseEvent) {
				for (int counter = getColumnCount() - 1; counter >= 0; counter--) {
					if (getColumnClass(counter) == TreeTableModel.class) {
						MouseEvent me = (MouseEvent) e;
						MouseEvent newME = new MouseEvent(tree, me.getID(), me.getWhen(),
								me.getModifiers(), me.getX() - getCellRect(0, counter, true).x,
								me.getY(), me.getClickCount(), me.isPopupTrigger());
						tree.dispatchEvent(newME);
						break;
					}
				}
			}
			return false;
		}
	}

	class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel {
		/** Set to true when we are updating the ListSelectionModel. */
		protected boolean updatingListSelectionModel;

		public ListToTreeSelectionModelWrapper() {
			super();
			getListSelectionModel().addListSelectionListener(createListSelectionListener());
		}

		ListSelectionModel getListSelectionModel() {
			return listSelectionModel;
		}

		public void resetRowSelection() {
			if (!updatingListSelectionModel) {
				updatingListSelectionModel = true;
				try {
					super.resetRowSelection();
				} finally {
					updatingListSelectionModel = false;
				}
			}
		}

		protected ListSelectionListener createListSelectionListener() {
			return new ListSelectionHandler();
		}

		protected void updateSelectedPathsFromSelectedRows() {
			if (!updatingListSelectionModel) {
				updatingListSelectionModel = true;
				try {
					// This is way expensive, ListSelectionModel needs an
					// enumerator for iterating.
					int min = listSelectionModel.getMinSelectionIndex();
					int max = listSelectionModel.getMaxSelectionIndex();

					clearSelection();
					if (min != -1 && max != -1) {
						for (int counter = min; counter <= max; counter++) {
							if (listSelectionModel.isSelectedIndex(counter)) {
								TreePath selPath = tree.getPathForRow(counter);

								if (selPath != null) {
									addSelectionPath(selPath);
								}
							}
						}
					}
				} finally {
					updatingListSelectionModel = false;
				}
			}
		}

		class ListSelectionHandler implements ListSelectionListener {
			public void valueChanged(ListSelectionEvent e) {
				updateSelectedPathsFromSelectedRows();
			}
		}
	}
}