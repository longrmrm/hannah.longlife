package com.hannah.gui.separate;
// HelpFrame.java
// Version 1.0
// longrm 2006-12-16

import javax.swing.*;
import java.awt.*;

public class HelpFrame extends JFrame
{
	public HelpFrame()
	{		
		String str = "----------------------------------------------------";
		str += "----------------------------------------------------";
		str += "\n\n***Separate v1.0***\n\n";
		str += "\tlongrm\n";
		str += "\twzlrm@tom.com\n";
		str += "\thttp://hi.baidu.com/\n\n";
		str += "***主要功能：将文件分割成多个.jzip文件，以及将.jzip文件合并为原来的文件***\n\n";
		str += "----------------------------------------------------";
		str += "----------------------------------------------------";
		str += "\n\n***File Area(文件输入区)***\n\n";
		str += "1. Separate/Unite: 选择 分割/合并\n\n";
		str += "   分割Separate时必须在文本框中输入要分割成的每个文件的大小\n\n";
		str += "2. File Path: 要处理的文件路径\n\n";
		str += "3. Deal：处理！点击之后开始分割或合并文件！\n\n";
		str += "4. Output Path: 输出文件路径(默认为当前路径，路径最末尾请不要带上'\\')\n\n";
		str += "***Console Area(显示区)***\n\n";
		str += "～～～～～～～～～显示处理的信息～～～～～～～～～\n\n";
		str += "----------------------------------------------------";
		str += "----------------------------------------------------";
		
		JTextArea content = new JTextArea(15, 50);
		content.setFont( new Font(null, Font.PLAIN, 14) );
		content.setText(str);
		content.setEditable(false);
		content.setLineWrap(true);
		content.setCaretPosition(0);
		JScrollPane pane = new JScrollPane(content);
		Container contentPane = getContentPane();
		contentPane.add(pane);
		
		setTitle("Separate Help");
		pack();
		setLocation(250, 200);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}