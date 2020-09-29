package com.hannah.gui.small;

import com.hannah.common.util.FileUtil;
import com.hannah.common.util.StringUtil;
import com.hannah.http.util.HttpFileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageWebSpider extends JFrame {

	private static final long serialVersionUID = -4022539511855648657L;
	
	private final static int MAX_DIG_DEPTH = 5;
	
	private JTextField httpUrlTf = new JTextField(50);
	private JButton startBut = new JButton("开始");
	private JButton stopBut = new JButton("停止");
	
	private JCheckBox isDugCb = new JCheckBox("挖掘");
	private JTextField hrefRegexTf = new JTextField(50);
	private JSpinner hrefDepthSp = new JSpinner();
	private JCheckBox onlyCb = new JCheckBox("Only");
	private JSpinner threadSp = new JSpinner();
	
	private JTextField imgRegexTf = new JTextField(50);
	
	private JTextField directPathTf = new JTextField(50);
	private JButton directBut = new JButton("选择");
	private JCheckBox isAutoFocus = new JCheckBox("自动焦点");
	
	private JTextArea messageTa = new JTextArea(20, 100);
	private JTextArea statusTa = new JTextArea();
	
	private Thread fetchThread;
	private ExecutorService es;
	
	private String statusText;
	private int animateIndex = 0;
	private Timer statusAnimate;
	
	
	public ImageWebSpider() {
		initInterface();
		
		// 定时更新statusTa的文本值，模拟进程动作
		statusAnimate = new Timer(300, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String animateText = ".";
				for (int i=0; i<animateIndex; i++)
					animateText += ".";
				statusTa.setText(statusText + animateText);
				animateIndex++;
				animateIndex = animateIndex % 6;
			}
		});
		
		this.setTitle("File Web Spider");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	private void initInterface() {
		JPanel urlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		
		urlPanel.add(new JLabel("抓取网址"));
		httpUrlTf.setFont(new Font("Courier New", Font.PLAIN, 14));
		httpUrlTf.setText("http://forum.xitek.com/forum-48-1.html");
		urlPanel.add(httpUrlTf);

		urlPanel.add(startBut);
		urlPanel.add(stopBut);
		startBut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doStart();
			}
		});
		stopBut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doStop();
			}
		});
		stopBut.setEnabled(false);
		
		JPanel digPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 5));
		digPanel.add(isDugCb);
		isDugCb.setSelected(true);
		isDugCb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setDigEnable();
			}
		});
		
		hrefRegexTf.setFont(new Font("Courier New", Font.PLAIN, 14));
		hrefRegexTf.setText("http://forum.xitek.com/thread.*");
		digPanel.add(hrefRegexTf);
		
		digPanel.add(new JLabel("  深度"));
		digPanel.add(hrefDepthSp);
		digPanel.add(onlyCb);
		hrefDepthSp.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int value = (Integer) hrefDepthSp.getValue();
				if (value < 0)
					hrefDepthSp.setValue(0);
				else if (value > MAX_DIG_DEPTH)
					hrefDepthSp.setValue(MAX_DIG_DEPTH);
			}
		});
		
		threadSp.setValue(1);
		digPanel.add(new JLabel("  线程"));
		digPanel.add(threadSp);
		threadSp.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int value = (Integer) threadSp.getValue();
				if (value < 0)
					threadSp.setValue(0);
			}
		});
		
		JPanel imgRegexPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		imgRegexPanel.add(new JLabel("图片匹配"));
		imgRegexTf.setFont(new Font("Courier New", Font.PLAIN, 14));
		imgRegexTf.setText(".*/pics/.*");
		imgRegexPanel.add(imgRegexTf);
		
		JPanel directPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		directPanel.add(new JLabel("保存路径"));
		directPathTf.setFont(new Font("Courier New", Font.PLAIN, 14));
		directPathTf.setText("D:\\1");
		directPanel.add(directPathTf);
		directPanel.add(directBut);
		directBut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser("D:\\");
				if(!directPathTf.getText().equals(""))
					fileChooser.setCurrentDirectory(new File(directPathTf.getText()));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(fileChooser.showDialog(ImageWebSpider.this, null)==JFileChooser.APPROVE_OPTION)
					directPathTf.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		});
		isAutoFocus.setSelected(true);
		directPanel.add(isAutoFocus);
		
		JPanel settingPanel = new JPanel();
		BoxLayout lo = new BoxLayout(settingPanel, BoxLayout.Y_AXIS);
		settingPanel.setLayout(lo);
		settingPanel.add(urlPanel);
		settingPanel.add(digPanel);
		settingPanel.add(imgRegexPanel);
		settingPanel.add(directPanel);
		
		messageTa.setFont(new Font("Courier New", 1, 12));
		messageTa.setAutoscrolls(true);
		statusTa.setFont(new Font("Courier New", 1, 12));
		statusTa.setEditable(false);
		
		this.setLayout(new BorderLayout());
		this.add(settingPanel, BorderLayout.NORTH);
		this.add(new JScrollPane(messageTa), BorderLayout.CENTER);
		this.add(statusTa, BorderLayout.SOUTH);
	}
	
	private void doStart() {
		if(!checkSetting()) return;
		
		messageTa.setText("");
		setComponentEnable(true);
		setDigEnable(false);
		statusAnimate.start();
		
		fetchThread = new Thread(new Runnable() {
			@Override
			public void run() {
				doFetch(httpUrlTf.getText(), imgRegexTf.getText(), 
						isDugCb.isSelected() ? hrefRegexTf.getText() : "", 
						isDugCb.isSelected() ? (Integer)hrefDepthSp.getValue() : 0, 
						0, directPathTf.getText());
				if (es != null) {
					es.shutdown();
					waitThreadStop();
				}
				setStopStatus();
			}
		});
		fetchThread.start();
	}

	private ExecutorService getExecutorService() {
		int value = (Integer) threadSp.getValue();
		if (value == 0)
			es = Executors.newCachedThreadPool();
		else if (value == 1)
			es = Executors.newSingleThreadExecutor();
		else
			es = Executors.newFixedThreadPool(value);
		return es;
	}
	
	private boolean checkSetting() {
		if(!StringUtil.isHttpUrl(httpUrlTf.getText())) {
			JOptionPane.showMessageDialog(this, httpUrlTf.getText() + " 不是一个有效的网址，请重新输入！", 
					"检查", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		File file = new File(directPathTf.getText());
		if(!file.exists()) {
			int choose = JOptionPane.showConfirmDialog(this, "保存路径 " + directPathTf.getText() + " 不存在，是否创建？",
					"检查", JOptionPane.YES_NO_OPTION);
			if (choose != JOptionPane.YES_OPTION)
				return false;
			return file.mkdirs();
		}
		return true;
	}
	
	private void doFetch(final String httpUrl, final String imgRegex, final String hrefRegex, 
			final int hrefDepth, final int currentDepth, final String directPath) {
		try {
			if (!StringUtil.isHttpUrl(httpUrl))
				return;
			
			String message = "Fetching Depth[" + currentDepth + "] Url: " + httpUrl;
			appendMessage(message + "\n\n");
			statusText = message;
			
			Document doc = Jsoup.connect(httpUrl).get();
			// 判断是否只抓取当前深度
			if(!onlyCb.isSelected() || currentDepth == hrefDepth) {
				ExecutorService es = getExecutorService();
				Elements imgElements = doc.select("img");
				for(Element imgElement : imgElements) {
					final String imgUrl = imgElement.attr("abs:src").trim();
					// 判断url是否合法
					if (StringUtil.isNull(imgUrl))
						continue;
					// 下载图片提交到线程池里执行
					es.submit(new Runnable() {
						@Override
						public void run() {
							downloadFile(imgUrl, imgRegex, directPath);
						}
					});
				}
				es.shutdown();
				waitThreadStop();
				appendMessage("\n");
			}
			// 翻墙：深度抓取
			if(currentDepth < hrefDepth) {
				Elements hrefElements = doc.select("a[href]");
				for(Element hrefElement : hrefElements) {
					String hrefUrl = hrefElement.attr("abs:href");
					if(!hrefRegex.equals("") && !hrefUrl.matches(hrefRegex))
						continue;
					String addDirectPath = hrefElement.text().length()>30 ? 
							hrefElement.text().substring(0, 30) : hrefElement.text();
					if(addDirectPath.equals(""))
						continue;
					String path = directPath + "\\" + StringUtil.replaceIllegalFileChars(addDirectPath, " ");
					String uniquePath = FileUtil.getUniqueFilePath(path);
					doFetch(hrefUrl, imgRegex, hrefRegex, hrefDepth, currentDepth+1, uniquePath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void downloadFile(final String imgUrl, final String imgRegex, final String directPath) {
		if (!StringUtil.isHttpUrl(imgUrl)) {
			appendMessage(" # " + imgUrl + "\t[Not_HttpUrl!] " + Thread.currentThread().getName() + "\n");
			return;
		}
		if(!imgRegex.equals("") && !imgUrl.matches(imgRegex)) {
			appendMessage(" # " + imgUrl + "\t[Not_Matched!] " + Thread.currentThread().getName() + "\n");
			return;
		}
		
		String imgName = imgUrl.substring(imgUrl.lastIndexOf("/")+1);
		imgName = StringUtil.replaceIllegalFileChars(imgName, " ");
		try {
			String imgPath = FileUtil.getUniqueFilePath(directPath + "\\" + imgName);
			File file = HttpFileUtil.downloadFileByHttpGet(imgPath, imgUrl);
			if (file != null)
				appendMessage(" + " + imgUrl + "\t[Success!] " + Thread.currentThread().getName() + "\n");
			else
				appendMessage(" - " + imgUrl + "\t[Failed!] " + Thread.currentThread().getName() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			appendMessage(" - " + imgUrl + "\t[Failed!] " + Thread.currentThread().getName() + "\n");
		}
		if (isAutoFocus.isSelected())
			messageTa.setCaretPosition(messageTa.getDocument().getLength()-1);
	}
	
	/**
	 * 多线程同步输出信息
	 * @param message
	 */
	private synchronized void appendMessage(String message) {
		messageTa.append(message);
	}
	
	private void doStop() {
		// 结束主线程
		if (fetchThread != null && fetchThread.isAlive())
			fetchThread.stop();
		//结束子线程
		if (es != null) {
			es.shutdownNow();
			waitThreadStop();
		}
		setStopStatus();
	}

	private void waitThreadStop() {
		// 等待线程执行完毕
		while (!es.isTerminated()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setStopStatus() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setComponentEnable(false);
				setDigEnable();
				statusAnimate.stop();
				statusTa.setText(null);
			}
		});
	}

	private void setDigEnable() {
		setDigEnable(isDugCb.isSelected());
	}
	
	private void setDigEnable(boolean flag) {
		hrefRegexTf.setEnabled(flag);
		threadSp.setEnabled(flag);
		hrefDepthSp.setEnabled(flag);
		onlyCb.setEnabled(flag);
	}
	
	private void setComponentEnable(boolean isAlive) {
		startBut.setEnabled(!isAlive);
		stopBut.setEnabled(isAlive);
		httpUrlTf.setEnabled(!isAlive);
		isDugCb.setEnabled(!isAlive);
		imgRegexTf.setEnabled(!isAlive);
		directPathTf.setEnabled(!isAlive);
		directBut.setEnabled(!isAlive);
	}
	
	public static void main(String[] args) throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				new ImageWebSpider().setVisible(true);
			}
		});;
	}

}
