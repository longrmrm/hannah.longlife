package com.hannah.swing.component.panel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.text.Collator;
import java.util.List;
import java.util.*;

public class FontPanel extends JPanel {

	private static final long serialVersionUID = -4024925230614039434L;

	private JPanel namePanel;
	private JLabel nameLabel;
	private JList nameList;

	private JPanel stylePanel;
	private JLabel styleLabel;
	private JList styleList;
	private String[] styles = new String[] { "PLAIN", "BOLD", "ITALIC", "BOLD|ITALIC" };

	private JPanel sizePanel;
	private JLabel sizeLabel;
	private JList sizeList;

	private JTextArea previewTa;
	private final String previewText = "中国是一个具有五千年悠久历史的国度。"
			+ "\nChina is a country with long history of five thousand years." 
			+ "\n1 23 456 7890.123";

	public FontPanel() {
		initJList();
		initNamePanel();
		initStylePanel();
		initSizePanel();

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(namePanel, BorderLayout.WEST);
		centerPanel.add(stylePanel, BorderLayout.CENTER);
		centerPanel.add(sizePanel, BorderLayout.EAST);

		previewTa = new JTextArea();
		previewTa.setBorder(new TitledBorder("Preview"));
		previewTa.setText(previewText);
		previewTa.setLineWrap(true);
		previewTa.setPreferredSize(new Dimension(300, 150));
		setSelectedFont(previewTa.getFont());

		this.setLayout(new BorderLayout(10, 10));
		this.add(centerPanel, BorderLayout.CENTER);
		this.add(previewTa, BorderLayout.SOUTH);
	}

	public FontPanel(Font font) {
		this();
		setSelectedFont(font);
	}

	private void initJList() {
		ListSelectionListener listener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				nameLabel.setText((String) nameList.getSelectedValue());
				styleLabel.setText((String) styleList.getSelectedValue());
				sizeLabel.setText((String) sizeList.getSelectedValue());

				Font font = getSelectedFont();
				if (font != null)
					previewTa.setFont(font);
			}
		};

		// 取出所有字体
		Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		List<String> fontNames = new ArrayList<String>();
		for (Font font : fonts) {
			String fontName = font.getFontName();
			// 去掉family相同的重复字体
			if (fontNames.contains(fontName))
				continue;
			fontNames.add(fontName);
		}
		// 按首字母排序（中文拼音）
		Comparator<Object> comparator = Collator.getInstance(Locale.CHINA);
		String[] nameArray = fontNames.toArray(new String[] {});
		Arrays.sort(nameArray, comparator);
		nameList = new JList(nameArray);
		nameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		nameList.getSelectionModel().addListSelectionListener(listener);

		// 字形
		styleList = new JList(styles);
		styleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		styleList.getSelectionModel().addListSelectionListener(listener);

		// 字体大小
		String[] sizes = new String[] { "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28",
				"36", "48", "72" };
		sizeList = new JList(sizes);
		sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sizeList.getSelectionModel().addListSelectionListener(listener);
	}

	private void initNamePanel() {
		nameLabel = new JLabel();
		nameLabel.setFont(new Font(getFont().getFontName(), Font.BOLD, 12));
		nameLabel.setForeground(Color.darkGray);
		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelPanel.add(new JLabel("Name:"));
		labelPanel.add(nameLabel);

		namePanel = new JPanel(new BorderLayout());
		namePanel.add(labelPanel, BorderLayout.NORTH);
		namePanel.add(new JScrollPane(nameList), BorderLayout.CENTER);
	}

	private void initStylePanel() {
		styleLabel = new JLabel();
		styleLabel.setFont(new Font(getFont().getFontName(), Font.BOLD, 12));
		styleLabel.setForeground(Color.darkGray);
		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelPanel.add(new JLabel("Style:"));
		labelPanel.add(styleLabel);

		stylePanel = new JPanel(new BorderLayout());
		stylePanel.add(labelPanel, BorderLayout.NORTH);
		stylePanel.add(new JScrollPane(styleList), BorderLayout.CENTER);
		stylePanel.setPreferredSize(new Dimension(140, 0));
	}

	private void initSizePanel() {
		sizeLabel = new JLabel();
		sizeLabel.setFont(new Font(getFont().getFontName(), Font.BOLD, 12));
		sizeLabel.setForeground(Color.darkGray);
		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelPanel.add(new JLabel("Size:"));
		labelPanel.add(sizeLabel);

		sizePanel = new JPanel(new BorderLayout());
		sizePanel.add(labelPanel, BorderLayout.NORTH);
		sizePanel.add(new JScrollPane(sizeList), BorderLayout.CENTER);
		sizePanel.setPreferredSize(new Dimension(80, 0));
	}

	/**
	 * 设置预览文本
	 * @param text
	 */
	public void setPreviewText(String text) {
		previewTa.setText(text);
	}

	/**
	 * 获得选中的字体
	 * @return
	 */
	public Font getSelectedFont() {
		String name = (String) nameList.getSelectedValue();
		String style = (String) styleList.getSelectedValue();
		String size = (String) sizeList.getSelectedValue();
		if (name == null || style == null || size == null)
			return null;

		int s = Integer.parseInt(size);
		// 字形正好与styles数组的位置一一对应
		for (int i = 0; i < styles.length; i++) {
			if (style.equals(styles[i]))
				return new Font(name, i, s);
		}
		return null;
	}

	/**
	 * 设置选中的字体
	 * @param font
	 */
	public void setSelectedFont(Font font) {
		if (font == null)
			return;

		previewTa.setFont(font);
		// 设置选中字体
		for (int i = 0; i < nameList.getModel().getSize(); i++) {
			if (font.getFontName().equals(nameList.getModel().getElementAt(i))) {
				nameList.setSelectedIndex(i);
				nameList.scrollRectToVisible(nameList.getCellBounds(i, i));
			}
		}
		styleList.setSelectedIndex(font.getStyle());
		for (int i = 0; i < sizeList.getModel().getSize(); i++) {
			if ((font.getSize() + "").equals(sizeList.getModel().getElementAt(i)))
				sizeList.setSelectedIndex(i);
		}
	}

}
