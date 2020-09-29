package com.hannah.gui.small;

import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 健康小精灵：上班时，定时提醒
 * @author longrm
 * @date 2013-5-31
 */
public class HealthSpirit extends JFrame {

	private static final long serialVersionUID = 869053947481361908L;

	private JSpinner timeSp = new JSpinner();
	private JButton startBut = new JButton("开始");
	private JButton stopBut = new JButton("停止");
	
	private JTextField urlTf = new JTextField("http://ww.xitek.com/");

	private JTextArea remindTa = new JTextArea();
	private final String text = "长期坐办公室，要注意修身养性~   O(∩_∩)O~\n\n" + "1、坐姿要端正，保持正确的坐姿。\n"
			+ "2、不宜坐太长时间，超过半小时到1小时要做一些活动。\n" + "3、多喝水，促排泄，经常上wc有助运动 O(∩_∩)O哈哈~\n" + "4、休息时间多眺望远处，活动眼睛瞳孔，保养~";

	private JToolBar statusBar = new JToolBar();;
	private JLabel timeLb = new JLabel();
	private JLabel leftTimeLb = new JLabel();
	private Timer statusAnimate;
	private Date startTime;

	private DateFormat ssFormat = new SimpleDateFormat("HH:mm:ss");

	private SystemTray sysTray; // 当前操作系统的托盘对象
	private TrayIcon trayIcon; // 当前对象的托盘

	public HealthSpirit() {
		timeSp.setValue(new Integer(30*60));
		timeSp.setPreferredSize(new Dimension(80, 25));

		timeLb.setText("Now: " + ssFormat.format(new Date()));
		timeLb.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		leftTimeLb.setOpaque(true);
		leftTimeLb.setForeground(Color.red);
		leftTimeLb.setFont(new Font("Times New Roman", Font.BOLD, 14));

		statusBar.addSeparator();
		statusBar.add(timeLb);
		statusBar.add(new JLabel("     "));
		statusBar.addSeparator();
		statusBar.add(leftTimeLb);
		statusBar.setFloatable(false);

		statusAnimate = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Date now = new Date();
				timeLb.setText("Start: " + ssFormat.format(startTime) + "    Now: " + ssFormat.format(now));
				long leftTime = ((Integer) timeSp.getValue()) * 1000 - now.getTime() + startTime.getTime();
				leftTimeLb.setText("Left Time: " + leftTime / 1000 + "s");
				if (leftTime <= 0) {
					try {
//						Desktop.getDesktop().browse(new URI(urlTf.getText()));
						Runtime.getRuntime().exec("cmd.exe /c start pause");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
//					JOptionPane.showMessageDialog(HealthSpirit.this, remindTa.getText(), "时间到了",
//							JOptionPane.WARNING_MESSAGE,
//							new ImageIcon(this.getClass().getResource("/images/loading.gif")));
//					statusAnimate.stop();
//					setComponentStatus(false);
					startTime = new Date();
				}
			}
		});

		ActionListener buttonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == startBut) {
					startTime = new Date();
					statusAnimate.start();
					setComponentStatus(true);
				} else if (e.getSource() == stopBut) {
					statusAnimate.stop();
					setComponentStatus(false);
				}
			}
		};
		startBut.addActionListener(buttonListener);
		stopBut.addActionListener(buttonListener);

		remindTa.setText(text);
		remindTa.setEditable(false);

		JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		panel1.add(new JLabel("每隔(s)："));
		panel1.add(timeSp);
		panel1.add(new JLabel("  "));
		panel1.add(startBut);
		panel1.add(stopBut);
		
		urlTf.setPreferredSize(new Dimension(300, 30));
		JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		panel2.add(new JLabel("弹出："));
		panel2.add(urlTf);
		
		JPanel panel = new JPanel(new GridLayout(2, 1));
		panel.add(panel1);
		panel.add(panel2);

		this.setLayout(new BorderLayout());
		this.add(panel, BorderLayout.NORTH);
		this.add(new JScrollPane(remindTa), BorderLayout.CENTER);
		this.add(statusBar, BorderLayout.SOUTH);

		createTrayIcon();
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowIconified(WindowEvent e) {
				addTrayIcon();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				addTrayIcon();
			}
		});
		setComponentStatus(false);

		this.setTitle("Health Spirit v1.0");
		this.setSize(400, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
	}
	
	private void setComponentStatus(boolean isStart) {
		timeSp.setEnabled(!isStart);
		startBut.setEnabled(!isStart);
		stopBut.setEnabled(isStart);
	}

	public void createTrayIcon() {
		sysTray = SystemTray.getSystemTray();// 获得当前操作系统的托盘对象
		PopupMenu popupMenu = new PopupMenu();// 弹出菜单
		MenuItem showMi = new MenuItem("Show");
		MenuItem exitMi = new MenuItem("Exit");
		popupMenu.add(showMi);
		popupMenu.add(new MenuItem("-"));
		popupMenu.add(exitMi);
		// 为弹出菜单项添加事件
		showMi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(true);
				sysTray.remove(trayIcon);
			}
		});
		exitMi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		trayIcon = new TrayIcon(new ImageIcon(this.getClass().getResource("icon.png")).getImage(),
				"Health Spirit v1.0", popupMenu);
	}

	private void addTrayIcon() {
		try {
			sysTray.add(trayIcon); // 将托盘添加到操作系统的托盘
			setVisible(false); // 使得当前的窗口隐藏
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		new HealthSpirit().setVisible(true);
	}

}
