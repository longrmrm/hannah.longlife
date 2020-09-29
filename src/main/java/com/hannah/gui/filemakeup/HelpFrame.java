package com.hannah.gui.filemakeup;

// HelpFrame.java
// Version 1.0
// longrm 2006-12-16

import javax.swing.*;
import java.awt.*;

public class HelpFrame extends JFrame {
	public HelpFrame() {
		String str = "----------------------------------------------------";
		str += "----------------------------------------------------";
		str += "\n\n***FileMakeup v1.0***\n";
		str += "***longrm***\n";
		str += "***主要功能：提取文本里面的章节、将文本按章节区分开来***\n\n";
		str += "----------------------------------------------------";
		str += "----------------------------------------------------";
		str += "\n\n***File Area(文件输入区)***\n\n";
		str += "1. File Path: 要处理的文件路径(当文件在当前目录时可以直接输入文件名)\n\n";
		str += "2. key: 分章节的关键词(三个字，默认为第x章，可以自己改成第x篇、第x节或卷x卷)\n\n";
		str += "   词的关键是'x'前面的字符串和后面的字符串，假如你的文本里章节规则为 卷2 第三章 xx，那你可以填第x章或卷x章\n\n";
		str += "3. Output Path: 输出文件路径(默认为当前路径，路径最末尾请不要带上'\\')\n\n";
		str += "***Deal Area(文件处理区)***\n\n";
		str += "*前提：必须先将上面的File Area填好*\n\n";
		str += "1. Title Fetch: 提取文本章节并放入到title.txt里面(每次处理文件时请务必先执行，看看是否能够正确提取章节)\n\n";
		str += "2. Chapter Divide: 处理文本，在章节处增加两个空行，并输出到...(原文件名称)_Edited.txt里面\n\n";
		str += "3. File Divide: 处理文本，将每个章节的文本内容单独生成一个txt文件\n\n";
		str += "   index里面填入要生成文件的起始号码(必须是数字)，例如填入2，那么文本里的第一个章节内容将放到2.txt里面，第二个章节内容将放到3.txt里面......\n\n";
		str += "   注：(如果第一个章节前面还有其它内容，那么这些内容将放到2.txt里面，而第一个章节的内容将放到3.txt里面......)\n\n";
		str += "----------------------------------------------------";
		str += "----------------------------------------------------";

		JTextArea content = new JTextArea(15, 50);
		content.setFont(new Font(null, Font.PLAIN, 14));
		content.setText(str);
		content.setEditable(false);
		content.setLineWrap(true);
		content.setCaretPosition(0);
		JScrollPane pane = new JScrollPane(content);
		Container contentPane = getContentPane();
		contentPane.add(pane);

		setTitle("FileMakeup Help");
		pack();
		setLocation(250, 200);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}