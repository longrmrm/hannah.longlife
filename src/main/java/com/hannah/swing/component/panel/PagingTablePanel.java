package com.hannah.swing.component.panel;

import com.hannah.swing.component.button.ImageButton;
import com.hannah.swing.component.table.BasicTableModel;
import com.hannah.swing.util.AbstractInvokeHandler;
import com.hannah.swing.util.TableUtil;
import com.hannah.swing.util.UiUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * paging table panel, support cache paging and database paging
 * @author longrm
 * @date 2012-6-25
 */
public class PagingTablePanel extends JPanel {

	private static final long serialVersionUID = 2264905204479936025L;

	private JScrollPane scrollPane;
	private JTable table;

	private JPanel pagingPanel;
	private JSpinner pageSizeSpinner;
	private JLabel pageStatusLb = new JLabel("1/0");

	private JPanel buttonPanel;
	private ImageButton firstButton = new ImageButton("|<");
	private ImageButton previousButton = new ImageButton("<");
	private ImageButton nextButton = new ImageButton(">");
	private ImageButton lastButton = new ImageButton(">|");
	private JSpinner curPageNumSpinner;
	private ImageButton goButton = new ImageButton("Go");

	private int dataCount;
	private List dataList;

	public static final int NO_PAGING = 0;
	public static final int CACHE_PAGING = 1;
	public static final int DB_PAGING = 2;

	private int pagingType = CACHE_PAGING;

	public PagingTablePanel() {
		initInterface();
	}
	
	public PagingTablePanel(JTable table) {
	  this.table = table;
	  initInterface();
	}

	private void initInterface() {
		if (table == null)
			table = TableUtil.createTable();
		scrollPane = new JScrollPane(table);

		initPagingPanel();

		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(pagingPanel, BorderLayout.SOUTH);
		
		TableUtil.initTableHeader(table);
	}

	private void initPagingPanel() {
		SpinnerNumberModel sizeModel = new SpinnerNumberModel(25, 1, 9999, 1);
		pageSizeSpinner = new JSpinner(sizeModel);
		pageSizeSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				setPageStatus();
			}
		});

		JPanel pageSizePanel = new JPanel();
		pageSizePanel.add(new JLabel("每页"));
		pageSizePanel.add(pageSizeSpinner);
		pageSizePanel.add(new JLabel("条"));

		pageStatusLb.setFont(new Font("宋体", Font.BOLD, 12));
		pageStatusLb.setForeground(Color.blue);
//		pageStatusLb.setPreferredSize(new Dimension(50, 25));
//		pageStatusLb.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel pageStatusPanel = new JPanel();
		pageStatusPanel.add(new JLabel("当前页"));
		pageStatusPanel.add(pageStatusLb);

		initButtonPanel();

		pagingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		pagingPanel.add(buttonPanel);
		pagingPanel.add(pageSizePanel);
		pagingPanel.add(pageStatusPanel);
	}

	private void initButtonPanel() {
		ActionListener l = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (pagingType == CACHE_PAGING && dataList == null)
					return;
				else if (pagingType == DB_PAGING && dataCount == -1)
					return;

				int toPageNum = getCurrentPageNum();
				if (e.getSource() == firstButton)
					toPageNum = 1;
				else if (e.getSource() == previousButton)
					toPageNum--;
				else if (e.getSource() == nextButton)
					toPageNum++;
				else if (e.getSource() == lastButton)
					toPageNum = getPageCount();
				else if (e.getSource() == goButton)
					;
				toPage(toPageNum);
			}
		};
		firstButton.addActionListener(l);
		previousButton.addActionListener(l);
		nextButton.addActionListener(l);
		lastButton.addActionListener(l);
		goButton.addActionListener(l);

		SpinnerNumberModel pageNoModel = new SpinnerNumberModel(1, 1, 99999, 1);
		curPageNumSpinner = new JSpinner(pageNoModel);
		curPageNumSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				setPageStatus();
			}
		});

		firstButton.setPreferredSize(new Dimension(40, 23));
		previousButton.setPreferredSize(new Dimension(40, 23));
//		curPageNumSpinner.setPreferredSize(new Dimension(40, 20));
		nextButton.setPreferredSize(new Dimension(40, 23));
		lastButton.setPreferredSize(new Dimension(40, 23));
		goButton.setPreferredSize(new Dimension(40, 23));
		
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		buttonPanel.add(firstButton);
		buttonPanel.add(previousButton);
		buttonPanel.add(curPageNumSpinner);
		buttonPanel.add(nextButton);
		buttonPanel.add(lastButton);
		buttonPanel.add(goButton);
	}

	/**
	 * go to page
	 * @param toPageNum
	 */
	public void toPage(final int toPageNum) {
		UiUtil.asyncInvoke(new AbstractInvokeHandler() {
			@Override
			public void success(Object result) {
				BasicTableModel model = (BasicTableModel) table.getModel();
				model.setDataList((List) result);
				table.updateUI();
				setPageStatus(toPageNum);
			}

			@Override
			public Object execute() throws Exception {
				return getPagingDataList((toPageNum - 1) * getPageSize(), getPageSize());
			}
		});
	}

	/**
	 * get paging data list which rownum between startRow and (startRow + count)
	 * @param startRow first row is 0
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public List getPagingDataList(int startRow, int count) throws Exception {
		if (pagingType == NO_PAGING)
			return dataList;
		else if (pagingType == CACHE_PAGING) {
			Thread.currentThread().sleep(100);
			int endRow = startRow + count;
			int toIndex = endRow > dataList.size() ? dataList.size() : endRow;
			return dataList.subList(startRow, toIndex);
		} else
			// TODO database paging
			return null;
	}

	public void setPageStatus() {
		setPageStatus(getCurrentPageNum());
	}

	public void setPageStatus(int toPageNum) {
		int pageCount = getPageCount();
		if (toPageNum < 1)
			toPageNum = 1;
		if (toPageNum > getPageCount())
			toPageNum = getPageCount();

		pageStatusLb.setText(toPageNum + "/" + pageCount);
		int width = pageStatusLb.getFontMetrics(pageStatusLb.getFont()).stringWidth(
				pageStatusLb.getText());
		curPageNumSpinner.setValue(toPageNum);
		setButtonStatus(toPageNum);
	}

	private void setButtonStatus(int toPageNum) {
		firstButton.setEnabled(toPageNum > 1);
		previousButton.setEnabled(toPageNum > 1);
		nextButton.setEnabled(toPageNum < getPageCount());
		lastButton.setEnabled(toPageNum < getPageCount());
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		if (this.table != null) {
			Container c = (Container) this.table.getParent();
			c.remove(this.table);
			c.add(table);
		} else
			scrollPane.getViewport().add(table);
		this.table = table;
	}

	public int getDataCount() {
		return dataCount;
	}

	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}

	public List getDataList() {
		return dataList;
	}

	public void setDataList(List dataList) {
		this.dataList = dataList;
	}

	public int getPagingType() {
		return pagingType;
	}

	public void setPagingType(int pagingType) {
		this.pagingType = pagingType;
		pagingPanel.setVisible(pagingType != NO_PAGING);
	}

	public int getPageSize() {
		return (Integer) pageSizeSpinner.getValue();
	}

	public void setPageSize(int pageSize) {
		pageSizeSpinner.setValue(pageSize);
	}

	public int getCurrentPageNum() {
		return (Integer) curPageNumSpinner.getValue();
	}

	public int getPageCount() {
		// if pagingType is DB_PAGING, count is dataCount
		int count = pagingType == DB_PAGING ? dataCount : dataList.size();
		int pageSize = getPageSize();
		return count / pageSize + (count % pageSize == 0 ? 0 : 1);
	}

}
