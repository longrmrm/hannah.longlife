package com.hannah.swing.util;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

public class FileChooserUtil {

	public static String lastFilePath;

	public static File showFileChooser(Component parent) {
		return showFileChooser(parent, new FileFilter[] {}, false);
	}

	public static File showFileChooser(Component parent, FileFilter fileFilter) {
		return showFileChooser(parent, new FileFilter[] { fileFilter }, false);
	}

	public static File showFileChooser(Component parent, FileFilter[] fileFilters) {
		return showFileChooser(parent, fileFilters, false);
	}

	public static File showFileChooser(Component parent, FileFilter[] fileFilters, boolean acceptAll) {
		JFileChooser fileChooser = null;
		if (lastFilePath != null && !lastFilePath.equals(""))
			fileChooser = new JFileChooser(lastFilePath);
		else
			fileChooser = new JFileChooser();

		fileChooser.setDialogTitle("选择文件");
		for (FileFilter fileFilter : fileFilters)
			fileChooser.addChoosableFileFilter(fileFilter);
		fileChooser.setAcceptAllFileFilterUsed(acceptAll);

		int result = fileChooser.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			lastFilePath = file.getPath();
			return file;
		}
		return null;
	}

	public static File showDirectoryChooser(Component parent) {
		JFileChooser fileChooser = null;
		if (lastFilePath != null && !lastFilePath.equals(""))
			fileChooser = new JFileChooser(lastFilePath);
		else
			fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		fileChooser.setDialogTitle("选择目录");
		int result = fileChooser.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			lastFilePath = file.getPath();
			return file;
		}
		return null;
	}

}
