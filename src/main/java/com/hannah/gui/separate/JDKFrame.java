package com.hannah.gui.separate;
// JDKFrame.java
// Version 1.0
// longrm 2006-12-16

import javax.swing.*;
import java.awt.*;

public class JDKFrame extends JFrame
{
	public JDKFrame()
	{		
		String str = "----------------------------------------------------";
		str += "----------------------------------------------------";
		str += "\n\n使用环境：jdk1.4.0 / jdk 1.5.0\n\n";
		str += "如果没有安装jdk请先到网上下载jdk后安装\n\n";
		str += "安装完后需要配置环境变量：\n\n";
		str += "path：在原来path后面添加 ;%JAVA_HOME%\\bin;%JAVA_HOME%\\jre\\bin;\n";
		str += "JAVA_HOME：C:\\Program Files\\Java\\jdk1.5.0 (你的jdk安装路径，自己改)\n";
		str += "CLASSPATH： ;%JAVA_HOME%\\lib;%JAVA_HOME%\\lib\\tools.jar\n\n";
		str += "----------------------------------------------------";
		str += "----------------------------------------------------";
		
		JTextArea content = new JTextArea(str);
		content.setEditable(false);
		content.setFont( new Font(null, Font.PLAIN, 14) );
		Container contentPane = getContentPane();
		contentPane.add(content);
		
		setTitle("JDK deployment");
		pack();
		setLocation(270, 250);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}