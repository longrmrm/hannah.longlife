package com.hannah.gui.baidu;

import com.hannah.http.baidu.BaiduSearchResult;
import com.hannah.http.baidu.BaiduUtil;
import com.hannah.swing.component.button.ImageButton;
import com.hannah.swing.util.AbstractInvokeHandler;
import com.hannah.swing.util.UiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Baidu Search Panel
 * @author longrm
 * @date 2013-4-22
 */
public class BaiduSearchPanel extends JPanel {

	private static final long serialVersionUID = -3639525553609397145L;

	protected JPanel searchPanel;
	protected JTextField searchTf;
	protected JButton searchButton;

	private JScrollPane scrollPane;
	private JPanel resultPanel;

	private JPanel buttonPanel;
	private ImageButton firstButton = new ImageButton("|<");
	private ImageButton previousButton = new ImageButton("<");
	private ImageButton nextButton = new ImageButton(">");
	private JSpinner curPageNumSpinner;
	private ImageButton goButton = new ImageButton("Go");

	public BaiduSearchPanel() {
		initSearchPanel();
		initResultPanel();
		initButtonPanel();
		scrollPane = new JScrollPane(resultPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);

		this.setLayout(new BorderLayout());
		this.add(searchPanel, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.setPreferredSize(new Dimension(550, 550));
	}

	protected void initSearchPanel() {
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curPageNumSpinner.setValue(1);
				search();
			}
		};
		searchTf = new JTextField();
		searchTf.setPreferredSize(new Dimension(200, 25));
		searchButton = new JButton("Search By Baidu");
		searchTf.addActionListener(listener);
		searchButton.addActionListener(listener);

		searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		searchPanel.add(searchTf);
		searchPanel.add(searchButton);
	}

	private void initButtonPanel() {
		ActionListener l = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int toPageNum = getCurrentPageNum();
				if (e.getSource() == firstButton)
					toPageNum = 1;
				else if (e.getSource() == previousButton)
					toPageNum--;
				else if (e.getSource() == nextButton)
					toPageNum++;
				else if (e.getSource() == goButton)
					;
				if (toPageNum < 1)
					toPageNum = 1;
				curPageNumSpinner.setValue(toPageNum);
				search();
			}
		};
		firstButton.addActionListener(l);
		previousButton.addActionListener(l);
		nextButton.addActionListener(l);
		goButton.addActionListener(l);

		SpinnerNumberModel pageNoModel = new SpinnerNumberModel(1, 1, 99999, 1);
		curPageNumSpinner = new JSpinner(pageNoModel);

		buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		buttonPanel.add(firstButton);
		buttonPanel.add(previousButton);
		buttonPanel.add(curPageNumSpinner);
		buttonPanel.add(nextButton);
		buttonPanel.add(goButton);
	}

	private void initResultPanel() {
		resultPanel = new JPanel();
	}

	public int getCurrentPageNum() {
		return (Integer) curPageNumSpinner.getValue();
	}

	protected void search() {
		search(searchTf.getText());
	}

	public void search(final String searchWord) {
		if (searchWord == null || searchWord.trim().isEmpty())
			return;

		UiUtil.asyncInvoke(new AbstractInvokeHandler<List<BaiduSearchResult>>() {

			@Override
			public void before() {
				resultPanel.removeAll();
				super.before();
			}

			@Override
			public List<BaiduSearchResult> execute() throws Exception {
				return BaiduUtil.search(searchWord, getCurrentPageNum());
			}

			@Override
			public void success(List<BaiduSearchResult> results) {
				showResults(results);
			}

			@Override
			public void after() {
				super.after();
				scrollPane.getVerticalScrollBar().setValue(0);
			}
		});

	}

	public void showResults(List<BaiduSearchResult> results) {
		resultPanel.setLayout(new GridLayout(results.size(), 1, 0, 20));
		for (BaiduSearchResult result : results)
			resultPanel.add(new BaiduResultPanel(result));
		resultPanel.revalidate();
	}

	/**
	 * 点击链接，触发事件
	 * @param e
	 * @param result
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	protected void clickLink(MouseEvent e, BaiduSearchResult result) throws IOException, URISyntaxException {
		Desktop.getDesktop().browse(new URI(result.getUrl()));
	}

	class BaiduResultPanel extends JPanel {

		private BaiduSearchResult result;

		public BaiduResultPanel(BaiduSearchResult result) {
			this.result = result;
			initInterface();
		}

		public BaiduSearchResult getResult() {
			return result;
		}

		public void setResult(BaiduSearchResult result) {
			this.result = result;
		}

		private void initInterface() {
			JLabel titleLb = new JLabel(result.getTitle());
			titleLb.setFont(new Font("微软雅黑", Font.PLAIN, 14));
			titleLb.setForeground(Color.blue);
			titleLb.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.blue));
			titleLb.setCursor(new Cursor(Cursor.HAND_CURSOR));
			titleLb.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					try {
						clickLink(e, result);
					} catch (Exception e1) {
						e1.printStackTrace();
						UiUtil.showStackTraceDialog(e1, "Something Wrong");
					}
				}
			});

			JTextArea briefTa = new JTextArea(0, 50);
			briefTa.setText(result.getBrief());
			briefTa.setLineWrap(true);
			briefTa.setEditable(false);
			briefTa.setBackground(Color.lightGray);

			JLabel commentLb = new JLabel(result.getShortRealUrl() + "  " + result.getUpdateDate());
			commentLb.setForeground(Color.darkGray);

			this.setLayout(new BorderLayout());
			this.add(titleLb, BorderLayout.NORTH);
			this.add(briefTa, BorderLayout.CENTER);
			this.add(commentLb, BorderLayout.SOUTH);
		}

	}

}
