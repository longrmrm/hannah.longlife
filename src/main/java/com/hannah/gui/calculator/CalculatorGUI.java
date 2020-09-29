// CalculatorGUI.java
package com.hannah.gui.calculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CalculatorGUI {

	// 定义按钮
	private JButton key0, key1, key2, key3, key4;
	private JButton key5, key6, key7, key8, key9;
	private JButton keyequal, keyplus, keyminus;
	private JButton keyperiod, keymult, keydiv;

	// 定义存放按钮的区域
	private Panel JButtonArea;

	// 定义存放运算结果的区域
	private JLabel answer;

	// 用来实现运算功能的类的对象实例化
	private Calculator calculator;
	private boolean readyForNextNumber;

	public CalculatorGUI() {
		calculator = new Calculator();
		readyForNextNumber = true;

		answer = new JLabel("0.0", JLabel.RIGHT);

		key0 = new JButton("0");
		key1 = new JButton("1");
		key2 = new JButton("2");
		key3 = new JButton("3");
		key4 = new JButton("4");
		key5 = new JButton("5");
		key6 = new JButton("6");
		key7 = new JButton("7");
		key8 = new JButton("8");
		key9 = new JButton("9");
		keyequal = new JButton("=");
		keyplus = new JButton("+");
		keyminus = new JButton("-");
		keymult = new JButton("*");
		keydiv = new JButton("/");
		keyperiod = new JButton(".");
		JButtonArea = new Panel();
	}

	public void launchFrame() {
		JButtonArea.setLayout(new GridLayout(4, 4));

		JButtonArea.add(key7);
		JButtonArea.add(key8);
		JButtonArea.add(key9);
		JButtonArea.add(keyplus);
		JButtonArea.add(key4);
		JButtonArea.add(key5);
		JButtonArea.add(key6);
		JButtonArea.add(keyminus);
		JButtonArea.add(key1);
		JButtonArea.add(key2);
		JButtonArea.add(key3);
		JButtonArea.add(keymult);
		JButtonArea.add(key0);
		JButtonArea.add(keyperiod);
		JButtonArea.add(keyequal);
		JButtonArea.add(keydiv);

		// 设置事件接收句柄
		OpJButtonHanlder op_handler = new OpJButtonHanlder();
		NumberJButtonHanlder number_handler = new NumberJButtonHanlder();
		key0.addActionListener(number_handler);
		key1.addActionListener(number_handler);
		key2.addActionListener(number_handler);
		key3.addActionListener(number_handler);
		key4.addActionListener(number_handler);
		key5.addActionListener(number_handler);
		key6.addActionListener(number_handler);
		key7.addActionListener(number_handler);
		key8.addActionListener(number_handler);
		key9.addActionListener(number_handler);
		keyperiod.addActionListener(number_handler);
		keyplus.addActionListener(op_handler);
		keyminus.addActionListener(op_handler);
		keymult.addActionListener(op_handler);
		keydiv.addActionListener(op_handler);
		keyequal.addActionListener(op_handler);

		// 新建一个帧，并且加上消息监听
		Frame f = new Frame("Calculator");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		f.setSize(200, 200);

		f.add(answer, BorderLayout.NORTH);
		f.add(JButtonArea, BorderLayout.CENTER);
		f.setVisible(true);
	}

	// 响应按钮事件，并根据不同的按钮事件进行不同的运算
	private class OpJButtonHanlder implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			char operator = event.getActionCommand().charAt(0);
			String result = "";
			switch (operator) {
			case '+':
				result = calculator.opAdd(answer.getText());
				break;
			case '-':
				result = calculator.opSubtract(answer.getText());
				break;
			case '*':
				result = calculator.opMultiply(answer.getText());
				break;
			case '/':
				result = calculator.opDivide(answer.getText());
				break;
			case '=':
				result = calculator.opEquals(answer.getText());
				break;
			}
			answer.setText(result);
			readyForNextNumber = true;
		}
	}

	// 处理连续运算时主界面的显示
	private class NumberJButtonHanlder implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if (readyForNextNumber) {
				answer.setText(event.getActionCommand());
				readyForNextNumber = false;
			} else {
				answer.setText(answer.getText() + event.getActionCommand().charAt(0));
			}
		}
	}

	// 主函数，也是该应用程序的执行入口处
	public static void main(String args[]) {
		CalculatorGUI calcGUI = new CalculatorGUI();
		calcGUI.launchFrame();
	}
}
