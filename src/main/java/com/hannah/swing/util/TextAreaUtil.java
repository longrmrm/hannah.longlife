package com.hannah.swing.util;

import com.hannah.common.util.StringUtil;

import javax.swing.*;

public class TextAreaUtil {

	public static int search(JTextArea textArea, String searchText, int direction) {
		int selectionIndex = direction > 0 ? textArea.getSelectionEnd() : textArea.getSelectionStart();
		int fromIndex = selectionIndex == -1 ? 0 : selectionIndex;
		return search(textArea, searchText, fromIndex, direction);
	}

	/**
	 * search text in textArea
	 * @param textArea
	 * @param searchText
	 * @param fromIndex
	 * @param direction
	 * @return
	 */
	public static int search(JTextArea textArea, String searchText, int fromIndex, int direction) {
		if (StringUtil.isNull(searchText))
			return -1;

		int index = -1;
		if (direction > 0)
			index = textArea.getText().indexOf(searchText, fromIndex);
		else
			index = textArea.getText().substring(0, fromIndex).lastIndexOf(searchText);

		if (index >= 0) {
			textArea.setSelectionStart(index); // 使找到的字符串选中
			textArea.setSelectionEnd(index + searchText.length());
			return index; // 用于查找下一个
		} else
			return -1;
	}

}
