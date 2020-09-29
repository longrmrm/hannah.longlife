package com.hannah.gui.bookreader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hannah.common.util.ColorUtil;
import com.hannah.common.util.FileUtil;
import com.hannah.common.util.StringUtil;
import com.hannah.gui.baidu.BaiduSearchPanel;
import com.hannah.gui.bookreader.bookspider.AbstractBookSpider;
import com.hannah.gui.bookreader.bookspider.BookSpiderFactory;
import com.hannah.gui.bookreader.config.BookReaderConfig;
import com.hannah.gui.bookreader.config.Constant;
import com.hannah.gui.bookreader.config.LanguageConfig;
import com.hannah.gui.bookreader.dialog.AboutDialog;
import com.hannah.gui.bookreader.dialog.ChapterParseDialog;
import com.hannah.gui.bookreader.dialog.RecentBooksDialog;
import com.hannah.http.baidu.BaiduSearchResult;
import com.hannah.http.baidu.BaiduUtil;
import com.hannah.http.baidu.TiebaSearchResult;
import com.hannah.http.baidu.TiebaUtil;
import com.hannah.swing.component.ImageViewport;
import com.hannah.swing.component.button.ImageButton;
import com.hannah.swing.component.dialog.BasicDialog;
import com.hannah.swing.component.dialog.ConfirmDialog;
import com.hannah.swing.component.dialog.SearchDialog;
import com.hannah.swing.component.linenumber.LineNumberTable;
import com.hannah.swing.component.panel.FontPanel;
import com.hannah.swing.component.panel.HtmlPagePanel;
import com.hannah.swing.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 书籍阅读器
 * @author longrm
 * @date 2013-4-11
 */
public class JBookReader extends JFrame {

	private static final long serialVersionUID = 1422244055846012256L;

	// 参数配置
	public static BookReaderConfig config = new BookReaderConfig();
	// 语言
	public static LanguageConfig langConfig = new LanguageConfig();

	private JMenuBar menuBar;

	private JSplitPane splitPane;
	private JList chapterList;
	private JTextArea textArea;
	private JScrollPane scrollPane;

	private JToolBar statusBar;
	private JLabel chapterLabel;
	private JLabel fontLabel;
	private JLabel colorLabel;
	private JLabel skinLabel;
	private JButton previousBut;
	private JButton nextBut;

	private File file;
	private String titleRegex;

	private BasicDialog baiduSearchDialog;
	private AbstractBookSpider bookSpider;
	private Book book;
	private String bookId;

	private String lineSeparator = "\n";

	private List<Book> bookList;
	private String historyFilePath = Constant.CURRENT_PATH + File.separator + Constant.HISTORY_FILE;

	private SystemTray sysTray; // 当前操作系统的托盘对象
	private TrayIcon trayIcon; // 当前对象的托盘

	public JBookReader() {
		langConfig.setLanguage(config.getSetting(Constant.LANGUAGE));

		initMenuBar();
		initSplitPane();
		initStatusBar();

		readBookList();
		setDefaultValue();

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

		this.setJMenuBar(menuBar);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		this.getContentPane().add(statusBar, BorderLayout.SOUTH);

		this.setSize(1000, 600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 退出时保存所有记录
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				try {
					config.saveAllSettings();
					saveBookList();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void initMenuBar() {
		JMenu fileMenu = new JMenu(langConfig.getTrans("File"));
		fileMenu.setMnemonic('F');
		// 打开书籍文件(txt)
		JMenuItem openMenuItem = new JMenuItem(langConfig.getTrans("Open File"));
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		openMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (FileChooserUtil.lastFilePath == null)
					FileChooserUtil.lastFilePath = "D:\\";
				FileFilter filter = new FileNameExtensionFilter("txt file", "txt");
				File choose = FileChooserUtil.showFileChooser(JBookReader.this, filter);
				if (choose != null) {
					file = choose;
					book = new Book();
					book.setTitle(file.getPath());
					bookList.add(book);

					textArea.setText(null);
					chapterLabel.setText("No Chapter");
					chapterParse();
					JBookReader.this.setTitle(file == null ? Constant.JBOOKREADER + " - No Book" : Constant.JBOOKREADER
							+ " - " + file.getPath());
				}
			}
		});
		fileMenu.add(openMenuItem);
		// 解析章节目录
		JMenuItem parseMenuItem = new JMenuItem(langConfig.getTrans("Chapter Parse"));
		parseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
		parseMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (file == null) {
					JOptionPane.showMessageDialog(JBookReader.this, langConfig.getTrans("Please Open File First!"),
							langConfig.getTrans("Chapter Parse"), JOptionPane.WARNING_MESSAGE);
					return;
				}
				chapterParse();
			}
		});
		fileMenu.add(parseMenuItem);
		fileMenu.addSeparator();

		// 在线书籍
		BaiduSearchPanel searchPanel = new BaiduSearchPanel() {
			@Override
			public void clickLink(MouseEvent e, final BaiduSearchResult result) throws IOException, URISyntaxException {
				if (e.getButton() != MouseEvent.BUTTON1) {
					super.clickLink(e, result);
					return;
				}

				UiUtil.asyncInvoke(new AbstractInvokeHandler<String>() {
					@Override
					public String execute() throws Exception {
						Document doc = Jsoup.connect(result.getUrl()).get();
						String realUrl = doc.baseUri();
						// 判断网址
						String site = StringUtil.getWebSite(realUrl);
						int index = site.indexOf(".");
						site = "www" + site.substring(index);

						bookSpider = BookSpiderFactory.getInstance().getBookSpider(site);
						if (bookSpider == null)
							throw new Exception("Site: " + site + " Has No bookSpider!");
						JBookReader.this.setTitle(Constant.JBOOKREADER + " - "
								+ StringUtil.toEllipsis(result.getTitle(), 20));
						return bookSpider.getBookId(realUrl);
					}

					@Override
					public void success(String result) {
						if (result != null) {
							bookId = result;
							baiduSearchDialog.setOk(true);
							baiduSearchDialog.setVisible(false);
						}
					}
				});
			}
		};
		baiduSearchDialog = new BasicDialog(this, langConfig.getTrans("Search Book Online"), true);
		baiduSearchDialog.add(searchPanel);
		baiduSearchDialog.pack();

		JMenuItem onlineMenuItem = new JMenuItem(langConfig.getTrans("Search Book Online"));
		onlineMenuItem.setMnemonic('S');
		onlineMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		onlineMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				baiduSearchDialog.setLocationRelativeTo(JBookReader.this);
				baiduSearchDialog.setOk(false);
				baiduSearchDialog.setVisible(true);
				if (!baiduSearchDialog.isOk())
					return;

				searchBook(null);
			}
		});
		fileMenu.add(onlineMenuItem);
		// 在线刷新章节目录
		JMenuItem refreshMenuItem = new JMenuItem(langConfig.getTrans("Refresh Book Chapter Online"));
		refreshMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (book != null && book.getSite() != null)
					searchBook(book);
			}
		});
		fileMenu.add(refreshMenuItem);
		fileMenu.addSeparator();

		// 历史阅读记录
		JMenuItem recentMenuItem = new JMenuItem(langConfig.getTrans("Recent Books"));
		recentMenuItem.setMnemonic('R');
		recentMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		recentMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RecentBooksDialog bookDialog = new RecentBooksDialog(JBookReader.this, bookList);
				bookDialog.setVisible(true);
				if (bookDialog.isOk()) {
					book = bookDialog.getBook();
					if (book.getSite() == null) {
						file = new File(book.getTitle());

						bookSpider = null;
						bookId = null;
					} else {
						bookSpider = BookSpiderFactory.getInstance().getBookSpider(book.getSite());
						bookId = book.getBookId();

						file = null;
						titleRegex = null;
					}
					JBookReader.this.setTitle(Constant.JBOOKREADER + " - " + book.toString());
					textArea.setText(null);
					chapterLabel.setText("No Chapter");
					chapterList.setListData(book.getChapters().toArray());
					chapterList.setSelectedIndex(book.getChapterIndex());
					chapterList.scrollRectToVisible(chapterList.getCellBounds(book.getChapterIndex(),
							book.getChapterIndex()));
				}
			}
		});
		fileMenu.add(recentMenuItem);

		// 退出
		JMenuItem exitMenuItem = new JMenuItem(langConfig.getTrans("Exit"));
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JBookReader.this.dispose();
			}
		});
		fileMenu.add(exitMenuItem);

		JMenu editMenu = new JMenu(langConfig.getTrans("Edit"));
		editMenu.setMnemonic('E');
		// 自动换行
		final JCheckBoxMenuItem lineWrapMenuItem = new JCheckBoxMenuItem(langConfig.getTrans("Line Wrap"));
		lineWrapMenuItem.setSelected(config.getSettingToBoolean(Constant.IS_LINE_WRAP));
		lineWrapMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.setLineWrap(lineWrapMenuItem.isSelected());
				config.updateSetting(Constant.IS_LINE_WRAP, lineWrapMenuItem.isSelected());
			}
		});
		editMenu.add(lineWrapMenuItem);
		// 设置文本是否可编辑
		final JCheckBoxMenuItem editableMenuItem = new JCheckBoxMenuItem(langConfig.getTrans("Editable"));
		editableMenuItem.setSelected(config.getSettingToBoolean(Constant.IS_EDITABLE));
		editableMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.setEditable(editableMenuItem.isSelected());
				config.updateSetting(Constant.IS_EDITABLE, editableMenuItem.isSelected());
			}
		});
		editMenu.add(editableMenuItem);
		editMenu.addSeparator();
		// 增加空行
		JMenuItem blankLineMenuItem = new JMenuItem(langConfig.getTrans("Insert Blank Line"));
		blankLineMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		blankLineMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = textArea.getText();
				if (text == null)
					return;

				String[] lines = text.replaceAll("\r\n", lineSeparator).split(StringUtil.escapeRegex(lineSeparator));
				StringBuffer textBf = new StringBuffer();
				for (String line : lines)
					textBf.append(line + lineSeparator + lineSeparator);
				textArea.setText(textBf.toString());
				textArea.setCaretPosition(0);
			}
		});
		editMenu.add(blankLineMenuItem);
		// 每行开头增加空格
		JMenuItem spaceMenuItem = new JMenuItem(langConfig.getTrans("Insert Blank Space"));
		spaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK));
		spaceMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = textArea.getText();
				if (StringUtil.isNull(text))
					return;

				String input = JOptionPane.showInputDialog(JBookReader.this,
						langConfig.getTrans("Please Enter Blank Space Numbers："), 4);
				if (StringUtil.isNull(input))
					return;

				String[] lines = text.replaceAll("\r\n", lineSeparator).split(StringUtil.escapeRegex(lineSeparator));
				StringBuffer textBf = new StringBuffer();
				for (String line : lines) {
					line = line.trim() + lineSeparator;
					line = StringUtil.toFixedLength(line, line.length() + Integer.parseInt(input), ' ');
					textBf.append(line);
				}
				textArea.setText(textBf.toString());
				textArea.setCaretPosition(0);
			}
		});
		editMenu.add(spaceMenuItem);
		editMenu.addSeparator();
		// 查找
		JMenuItem searchMenuItem = new JMenuItem(langConfig.getTrans("Search Text"));
		searchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		searchMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog d = new SearchDialog(JBookReader.this) {
					@Override
					protected boolean search(int direction) {
						return (TextAreaUtil.search(textArea, getSearchText(), direction) != -1);
					}
				};
				d.setVisible(true);
			}
		});
		editMenu.add(searchMenuItem);

		JMenu viewMenu = new JMenu(langConfig.getTrans("View"));
		viewMenu.setMnemonic('V');
		// 行号显示
		final JCheckBoxMenuItem lineNumberMenuItem = new JCheckBoxMenuItem(langConfig.getTrans("Show Line Number"));
		lineNumberMenuItem.setSelected(config.getSettingToBoolean(Constant.SHOW_LINE_NUMBER));
		lineNumberMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scrollPane.setRowHeaderView(lineNumberMenuItem.isSelected() ? new LineNumberTable(textArea) : null);
				config.updateSetting(Constant.SHOW_LINE_NUMBER, lineNumberMenuItem.isSelected());
			}
		});
		viewMenu.add(lineNumberMenuItem);
		// 状态栏显示
		final JCheckBoxMenuItem statusMenuItem = new JCheckBoxMenuItem(langConfig.getTrans("Show Status Bar"));
		statusMenuItem.setSelected(config.getSettingToBoolean(Constant.SHOW_STATUS_BAR));
		statusMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				statusBar.setVisible(statusMenuItem.isSelected());
				config.updateSetting(Constant.SHOW_STATUS_BAR, statusMenuItem.isSelected());
			}
		});
		viewMenu.add(statusMenuItem);
		viewMenu.addSeparator();

		// 字体改变事件
		ActionListener fontListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = textArea.getFont().getName();
				int style = textArea.getFont().getStyle();
				int size = textArea.getFont().getSize();

				Font font = null;
				String actionCommand = e.getActionCommand();
				if (langConfig.getTrans("Larger Font").equals(actionCommand))
					font = new Font(name, style, size + 1);
				else if (langConfig.getTrans("Smaller Font").equals(actionCommand))
					font = new Font(name, style, size - 1);
				else
					font = new Font(actionCommand, style, size);

				textArea.setFont(font);
				setFontStatus();
				config.updateSetting(Constant.TEXT_FONT, font);
			}
		};
		// 中文字体和英文字体
		JMenu chFontMenu = new JMenu(langConfig.getTrans("Chinese Fonts"));
		JMenu enFontMenu = new JMenu(langConfig.getTrans("English Fonts"));
		// 取出所有字体
		Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		HashSet<String> fontNames = new HashSet<String>();
		for (Font font : fonts) {
			String fontName = font.getFontName();
			// 去掉family相同的重复字体
			if (fontNames.contains(fontName))
				continue;
			fontNames.add(fontName);

			JMenuItem menuItem = new JMenuItem(fontName);
			menuItem.addActionListener(fontListener);
			if (StringUtil.isChinese(fontName))
				chFontMenu.add(menuItem);
			else
				enFontMenu.add(menuItem);
		}
		viewMenu.add(chFontMenu);
		viewMenu.add(enFontMenu);
		// 增大字体
		final JMenuItem largerMenuItem = new JMenuItem(langConfig.getTrans("Larger Font"));
		largerMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
		largerMenuItem.addActionListener(fontListener);
		viewMenu.add(largerMenuItem);
		// 减小字体
		final JMenuItem smallerMenuItem = new JMenuItem(langConfig.getTrans("Smaller Font"));
		smallerMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));
		smallerMenuItem.addActionListener(fontListener);
		viewMenu.add(smallerMenuItem);
		// 设置字体
		JMenuItem setFontMenuItem = new JMenuItem(langConfig.getTrans("Set Text Font ......"));
		setFontMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final FontPanel fontPanel = new FontPanel(textArea.getFont());
				ConfirmDialog d = new ConfirmDialog(JBookReader.this, langConfig.getTrans("Set Text Font ......"), true);
				d.setCenterPanel(fontPanel);
				d.registerEscapeKeyAction(true);
				d.setVisible(true);
				if (d.isOk()) {
					Font font = fontPanel.getSelectedFont();
					textArea.setFont(font);
					setFontStatus();
					config.updateSetting(Constant.TEXT_FONT, font);
				}
			}
		});
		viewMenu.add(setFontMenuItem);
		viewMenu.addSeparator();

		// 颜色改变事件
		ActionListener colorListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] colorTexts = colorLabel.getText().split("&");
				JMenuItem colorItem = (JMenuItem) e.getSource();
				Color color = colorItem.getBackground();
				if (colorItem.getName().indexOf("Background") != -1) {
					textArea.setOpaque(true);
					textArea.setBackground(color);
					chapterList.setBackground(color);
					colorLabel.setText(e.getActionCommand() + " & " + colorTexts[1].trim());
					config.updateSetting(Constant.BACKGROUND_COLOR, color);
				} else if (colorItem.getName().indexOf("Foreground") != -1) {
					textArea.setForeground(color);
					colorLabel.setText(colorTexts[0].trim() + " & " + e.getActionCommand());
					config.updateSetting(Constant.FOREGROUND_COLOR, color);
				}
			}
		};
		// 背景颜色
		JMenu backgroundMenu = new JMenu(langConfig.getTrans("Background Color"));
		for (int i = 0; i < ColorUtil.SOFT_COLORS.length; i++) {
			Color color = ColorUtil.SOFT_COLORS[i];
			JMenuItem menuItem = new JMenuItem(ColorUtil.SOFT_COLOR_NAMES[i]);
			menuItem.setOpaque(true);
			menuItem.setBackground(color);
			menuItem.setName("Background " + ColorUtil.SOFT_COLOR_NAMES[i]);
			menuItem.addActionListener(colorListener);
			backgroundMenu.add(menuItem);
		}
		for (int i = 0; i < ColorUtil.COLORS.length; i++) {
			Color color = ColorUtil.COLORS[i];
			JMenuItem menuItem = new JMenuItem(ColorUtil.COLOR_NAMES[i]);
			menuItem.setOpaque(true);
			menuItem.setBackground(color);
			menuItem.setName("Background " + ColorUtil.COLOR_NAMES[i]);
			menuItem.addActionListener(colorListener);
			backgroundMenu.add(menuItem);
		}
		viewMenu.add(backgroundMenu);
		// 前景字体颜色
		JMenu foregroundMenu = new JMenu(langConfig.getTrans("Foreground Color"));
		for (int i = 0; i < ColorUtil.COLORS.length; i++) {
			Color color = ColorUtil.COLORS[i];
			JMenuItem menuItem = new JMenuItem(ColorUtil.COLOR_NAMES[i]);
			menuItem.setOpaque(true);
			menuItem.setBackground(color);
			menuItem.setName("Foreground " + ColorUtil.COLOR_NAMES[i]);
			menuItem.addActionListener(colorListener);
			foregroundMenu.add(menuItem);
		}
		viewMenu.add(foregroundMenu);
		viewMenu.addSeparator();

		// 背景图片
		JMenuItem backgroundImageMenuItem = new JMenuItem(langConfig.getTrans("Background Image"));
		backgroundImageMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String url = JOptionPane.showInputDialog(JBookReader.this,
						langConfig.getTrans("Please Enter Image Url:"));
				if (StringUtil.isNull(url))
					return;

				ImageViewport viewport = (ImageViewport) scrollPane.getViewport();
				try {
					viewport.setBackground(new URL(url));
					String[] colorTexts = colorLabel.getText().split("&");
					colorLabel.setText(StringUtil.toEllipsis(url, 50) + " & " + colorTexts[1].trim());
					config.updateSetting(Constant.BACKGROUND_IMAGE, url);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		viewMenu.add(backgroundImageMenuItem);

		JMenu skinMenu = new JMenu(langConfig.getTrans("Skin"));
		skinMenu.setMnemonic('S');
		// LookAndFeel改变事件
		ActionListener feelListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String lookAndFeel = e.getActionCommand();
				try {
					UIManager.setLookAndFeel(lookAndFeel);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				SwingUtilities.updateComponentTreeUI(JBookReader.this);
				SwingUtilities.updateComponentTreeUI(baiduSearchDialog);
				skinLabel.setText(lookAndFeel);
				config.updateSetting(Constant.LOOK_AND_FEEL, lookAndFeel);
			}
		};
		// Swing LookAndFeel
		JMenu swingSkinMenu = new JMenu(langConfig.getTrans("Swing LookAndFeel"));
		List<Class<?>> feels = LookAndFeelUtil.getSwingLookAndFeels();
		for (Class<?> feel : feels) {
			JMenuItem feelMenuItem = new JMenuItem(feel.getName());
			feelMenuItem.addActionListener(feelListener);
			swingSkinMenu.add(feelMenuItem);
		}
		skinMenu.add(swingSkinMenu);
		// Substance LookAndFeel
		JMenu substanceSkinMenu = new JMenu(langConfig.getTrans("Substance LookAndFeel"));
		feels = LookAndFeelUtil.getSubstanceLookAndFeels();
		for (Class<?> feel : feels) {
			JMenuItem feelMenuItem = new JMenuItem(feel.getName());
			feelMenuItem.addActionListener(feelListener);
			substanceSkinMenu.add(feelMenuItem);
		}
		skinMenu.add(substanceSkinMenu);
		skinMenu.addSeparator();

		// 语言
		ActionListener l = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals(langConfig.getTrans("Chinese")))
					config.updateSetting(Constant.LANGUAGE, "Chinese");
				else if (e.getActionCommand().equals(langConfig.getTrans("English")))
					config.updateSetting(Constant.LANGUAGE, "English");
				JOptionPane.showMessageDialog(JBookReader.this,
						langConfig.getTrans("Please Restart BookReader For This Change To Take Effect!"),
						langConfig.getTrans("Languages"), JOptionPane.WARNING_MESSAGE);
			}
		};
		JMenuItem chineseMenuItem = new JMenuItem(langConfig.getTrans("Chinese"));
		JMenuItem englishMenuItem = new JMenuItem(langConfig.getTrans("English"));
		chineseMenuItem.addActionListener(l);
		englishMenuItem.addActionListener(l);
		JMenu languageMenu = new JMenu(langConfig.getTrans("Languages"));
		languageMenu.add(chineseMenuItem);
		languageMenu.add(englishMenuItem);
		skinMenu.add(languageMenu);

		JMenu helpMenu = new JMenu(langConfig.getTrans("Help"));
		helpMenu.setMnemonic('H');
		// Help
		ActionListener htmlListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				HtmlPagePanel pagePanel = new HtmlPagePanel();
				JFrame f = new JFrame(e.getActionCommand());
				f.add(pagePanel);
				f.setSize(600, 500);
				f.setLocationRelativeTo(JBookReader.this);
				f.setVisible(true);

				String helpPageUrl = "file:" + Constant.CURRENT_PATH;
				if (e.getActionCommand().equals(langConfig.getTrans("Help Contents")))
					helpPageUrl += "/help.html";
				else if (e.getActionCommand().equals(langConfig.getTrans("FAQ")))
					helpPageUrl += "/faq.html";
				pagePanel.setPage(helpPageUrl);
			}
		};
		JMenuItem helpMenuItem = new JMenuItem(langConfig.getTrans("Help Contents"));
		helpMenuItem.addActionListener(htmlListener);
		helpMenu.add(helpMenuItem);
		JMenuItem faqMenuItem = new JMenuItem(langConfig.getTrans("FAQ"));
		faqMenuItem.addActionListener(htmlListener);
		helpMenu.add(faqMenuItem);
		helpMenu.addSeparator();
		// About
		JMenuItem aboutMenuItem = new JMenuItem(langConfig.getTrans("About JBookReader"));
		aboutMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new AboutDialog(JBookReader.this).setVisible(true);
			}
		});
		helpMenu.add(aboutMenuItem);

		menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(viewMenu);
		menuBar.add(skinMenu);
		menuBar.add(helpMenu);
	}

	private void initSplitPane() {
		chapterList = new JList();
		chapterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		chapterList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() != 2)
					return;

				readChapterContent(chapterList.getSelectedIndex());
			}
		});

		textArea = new JTextArea(30, 50);
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int selectedIndex = chapterList.getSelectedIndex();
				if (book == null || selectedIndex == -1 || config.getSettingToBoolean(Constant.IS_EDITABLE))
					return;

				if (e.getKeyCode() == KeyEvent.VK_LEFT)
					readChapterContent(selectedIndex - 1);
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					readChapterContent(selectedIndex + 1);
				else if (e.getKeyCode() == KeyEvent.VK_U) {
					JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
					int inc = scrollBar.getBlockIncrement(1);
					scrollBar.setValue(scrollBar.getValue() - inc + inc / 10);
				} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
					int inc = scrollBar.getBlockIncrement(1);
					scrollBar.setValue(scrollBar.getValue() + inc - inc/10);
				}
			}
		});
		// 增加右键菜单
		final JMenuItem menuItem1 = new JMenuItem(langConfig.getTrans("Read Chapter By Baidu Tieba"));
		final JMenuItem menuItem2 = new JMenuItem(langConfig.getTrans("Search By Baidu"));
		final JMenuItem menuItem3 = new JMenuItem(langConfig.getTrans("Search By Baidu Tieba"));
		final JMenuItem menuItem4 = new JMenuItem(langConfig.getTrans("Go To The Site"));
		ActionListener l = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == menuItem1) {
					ListModel model = chapterList.getModel();
					final Chapter chapter = (Chapter) model.getElementAt(chapterList.getSelectedIndex());

					UiUtil.asyncInvoke(new AbstractInvokeHandler<String>() {

						@Override
						protected void installLoadingComponent(Container container) {
							super.installLoadingComponent(textArea.getParent());
						}

						@Override
						protected void unInstallLoadingComponent(Container container) {
							super.unInstallLoadingComponent(container);
							container.add(textArea);
						}

						@Override
						public String execute() throws Exception {
							return readChapterContentByTieba(chapter);
						}

						@Override
						public void success(String result) {
							textArea.setText(chapter.getTitle() + "    [更新时间：" + chapter.getUpdateTime() + "  字数："
									+ result.length() + "]\n\n" + result);
						}

					});
					return;
				}

				String text = textArea.getSelectedText();
				try {
					String url = null;
					if (e.getSource() == menuItem2)
						url = BaiduUtil.getSearchUrl(text);
					else if (e.getSource() == menuItem3)
						url = TiebaUtil.getSearchUrl(book.getTitle(), text);
					else if (e.getSource() == menuItem4)
						url = text;
					Desktop.getDesktop().browse(new URI(url));
				} catch (Exception e1) {
					e1.printStackTrace();
					UiUtil.showStackTraceDialog(e1, "Something Wrong");
				}
			}
		};
		menuItem1.addActionListener(l);
		menuItem2.addActionListener(l);
		menuItem3.addActionListener(l);
		menuItem4.addActionListener(l);

		final JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(menuItem1);
		popupMenu.addSeparator();
		popupMenu.add(menuItem2);
		popupMenu.add(menuItem3);
		popupMenu.addSeparator();
		popupMenu.add(menuItem4);
		// 加入右键事件
		textArea.add(popupMenu);
		textArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3 && book != null) {
					int index = chapterList.getSelectedIndex();
					menuItem1.setEnabled(index >= 0 && index < chapterList.getModel().getSize());
					boolean flag = StringUtil.hasText(textArea.getSelectedText());
					menuItem2.setEnabled(flag);
					menuItem3.setEnabled(book.getSite() != null && flag);
					menuItem4.setEnabled(flag);

					popupMenu.show(textArea, e.getX(), e.getY());
				}
			}
		});

		scrollPane = new JScrollPane();
		scrollPane.setViewport(new ImageViewport(textArea));

		splitPane = new JSplitPane();
		splitPane.setLeftComponent(new JScrollPane(chapterList));
		splitPane.setRightComponent(scrollPane);
		splitPane.setDividerLocation(200);
		// splitPane.setDividerSize(10);
		splitPane.setOneTouchExpandable(true);
	}

	private void initStatusBar() {
		chapterLabel = new JLabel();
		fontLabel = new JLabel();
		colorLabel = new JLabel();
		skinLabel = new JLabel();

		ActionListener l = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = chapterList.getSelectedIndex();
				if (e.getSource() == previousBut)
					readChapterContent(selectedIndex - 1);
				else if (e.getSource() == nextBut)
					readChapterContent(selectedIndex + 1);
			}
		};
		previousBut = new ImageButton("上一章");
		previousBut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		previousBut.setFont(new Font("微软雅黑", Font.BOLD, 12));
		previousBut.setForeground(Color.BLUE);
		previousBut.addActionListener(l);
		nextBut = new ImageButton("下一章");
		nextBut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		nextBut.setFont(new Font("微软雅黑", Font.BOLD, 12));
		nextBut.setForeground(Color.BLUE);
		nextBut.addActionListener(l);

		statusBar = new JToolBar();
		statusBar.setFloatable(false);
		statusBar.addSeparator(new Dimension(20, 15));
		statusBar.add(chapterLabel);
		statusBar.addSeparator(new Dimension(20, 15));
		statusBar.add(fontLabel);
		statusBar.addSeparator(new Dimension(20, 15));
		statusBar.add(colorLabel);
		statusBar.addSeparator(new Dimension(20, 15));
		statusBar.add(skinLabel);
		statusBar.addSeparator(new Dimension(20, 15));

		statusBar.add(previousBut);
		statusBar.add(nextBut);
	}

	/**
	 * 读取book列表（历史记录）
	 */
	private void readBookList() {
		try {
			File history = new File(historyFilePath);
			if (history.exists()) {
				String text = FileUtil.readText(history);
				if (StringUtil.hasText(text)) {
					bookList = JSON.parseObject(text, List.class);
					for (int i = bookList.size() - 1; i >= 0; i--) {
						if (bookList.get(i) == null)
							bookList.remove(i);
					}
				} else
					bookList = new ArrayList<Book>();
			} else {
				FileUtil.createNewFile(historyFilePath);
				bookList = new ArrayList<Book>();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存book列表（历史记录）
	 */
	private void saveBookList() {
		File history = new File(historyFilePath);
		String text = JSON.toJSONString(bookList, SerializerFeature.WriteClassName);
		try {
			boolean isUtf = FileUtil.isUtf(history);
			FileUtil.writeText(history, text, isUtf ? "UTF-8" : "GBK");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void searchBook(final Book oldBook) {
		file = null;
		titleRegex = null;

		UiUtil.asyncInvoke(new AbstractInvokeHandler<Book>() {

			@Override
			public void before() {
				textArea.setText(null);
				chapterLabel.setText("No Chapter");
				chapterList.setListData(new Object[] {});
				super.before();
			}

			@Override
			public Book execute() throws Exception {
				return bookSpider.searchBook(bookId);
			}

			@Override
			public void success(Book result) {
				book = result;
				if (oldBook != null) {
					book.setChapterIndex(oldBook.getChapterIndex());
					bookList.remove(oldBook);
				}
				bookList.add(0, book);

				JBookReader.this.setTitle(Constant.JBOOKREADER + " - " + book.toString());
				chapterList.setListData(book.getChapters().toArray());
				chapterList.setSelectedIndex(book.getChapterIndex());
				chapterList.scrollRectToVisible(chapterList.getCellBounds(book.getChapterIndex(),
						book.getChapterIndex()));
			}
		});
	}

	/**
	 * 解析章节目录
	 */
	private void chapterParse() {
		ChapterParseDialog dialog = new ChapterParseDialog(JBookReader.this);
		if (book.getCharsetName() != null)
			dialog.setCharsetName(book.getCharsetName());
		else {
			try {
				boolean isUtf = FileUtil.isUtf(file);
				String charsetName = isUtf ? "UTF-8" : "GBK";
				book.setCharsetName(charsetName);
				dialog.setCharsetName(charsetName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (titleRegex != null)
			dialog.setTitleRegex(titleRegex);
		dialog.setVisible(true);
		if (!dialog.isOk())
			return;

		bookSpider = null;
		bookId = null;

		titleRegex = dialog.getTitleRegex();
		UiUtil.asyncInvoke(new AbstractInvokeHandler<List<Chapter>>() {

			@Override
			protected void installLoadingComponent(Container container) {
				super.installLoadingComponent(chapterList.getParent());
			}

			@Override
			protected void unInstallLoadingComponent(Container container) {
				super.unInstallLoadingComponent(container);
				container.add(chapterList);
			}

			@Override
			public void before() {
				chapterList.setListData(new Object[] {});
				super.before();
			}

			@Override
			public List<Chapter> execute() throws Exception {
				return ChapterUtil.parseFile(file, book.getCharsetName(), titleRegex);
			}

			@Override
			public void success(List<Chapter> result) {
				chapterList.setListData(result.toArray());
				book.setChapters(result);
			}
		});
	}

	/**
	 * 读取章节内容
	 */
	private void readChapterContent(final int chapterIndex) {
		if (chapterIndex < 0 || chapterIndex >= chapterList.getModel().getSize())
			return;

		book.setChapterIndex(chapterIndex);
		ListModel model = chapterList.getModel();
		final Chapter chapter = (Chapter) model.getElementAt(chapterIndex);

		UiUtil.asyncInvoke(new AbstractInvokeHandler<String>() {

			@Override
			protected void installLoadingComponent(Container container) {
				super.installLoadingComponent(textArea.getParent());
			}

			@Override
			protected void unInstallLoadingComponent(Container container) {
				super.unInstallLoadingComponent(container);
				container.add(textArea);
			}

			@Override
			public void before() {
				chapterList.scrollRectToVisible(chapterList.getCellBounds(chapterIndex, chapterIndex));
				textArea.setText(null);
				super.before();
			}

			@Override
			public String execute() throws Exception {
				if (chapter.isLocal()) {
					byte[] bytes = FileUtil.readBytes(file, chapter.getStartFilePointer(), chapter.getEndFilePointer());
					return new String(bytes, book.getCharsetName());
				} else if (!chapter.isVip()) {
					return bookSpider.readChapterContent(chapter);
				} else if (chapter.isVip()) {
					return readChapterContentByTieba(chapter);
				} else
					return null;
			}

			@Override
			public void success(String result) {
				chapterList.setSelectedIndex(chapterIndex);
				textArea.setText(chapter.getTitle() + "    [更新时间：" + chapter.getUpdateTime() + "  字数："
						+ result.length() + "]\n\n" + result);
				chapterLabel.setText(((Chapter) chapterList.getSelectedValue()).getTitle());
			}

			@Override
			public void after() {
				super.after();
				textArea.setCaretPosition(0);
				textArea.requestFocusInWindow();
			}
		});
	}

	/**
	 * 从百度贴吧获取手打章节内容
	 * @param chapter
	 * @return
	 * @throws IOException
	 */
	private String readChapterContentByTieba(Chapter chapter) throws IOException {
		String chapterUrl = bookSpider.getChapterUrl(chapter);
		String searchUrl = TiebaUtil.getSearchUrl(book.getTitle(), chapter.getTitle());
		List<TiebaSearchResult> results = TiebaUtil.fetchSearchResult(searchUrl);
		// 转为半角字符串
		String chapterTitle = StringUtil.fullToHalf(chapter.getTitle());
		String searchResult = searchUrl;
		String contents = "";
		for (TiebaSearchResult result : results) {
			searchResult += "\n\n" + result.getTitle() + " " + result.getUrl();
			// 搜索结果标题转为半角字符串，比较是否包含章节标题
			if (result.getAuthor() == null || StringUtil.fullToHalf(result.getTitle()).indexOf(chapterTitle) == -1)
				continue;

			String url = TiebaUtil.getSeeLzUrl(result.getUrl());
			List<String> contentList = TiebaUtil.fetchPostContent(url);
			for (String content : contentList) {
				contents += content + "\n  ";
				for (int i = 0; i < 100; i++)
					contents += "-";
				contents += "\n";
			}
			return contents + "\n\n\n----------------------------------------------\n" + url + "\n\n" + chapterUrl;
		}
		return searchResult + "\n\n" + chapterUrl;
	}

	private void setFontStatus() {
		Font font = textArea.getFont();
		fontLabel.setText(font.getFontName() + "  " + font.getSize() + "pt");
	}

	/**
	 * 设置默认值
	 */
	private void setDefaultValue() {
		textArea.setLineWrap(config.getSettingToBoolean(Constant.IS_LINE_WRAP));
		textArea.setEditable(config.getSettingToBoolean(Constant.IS_EDITABLE));
		textArea.setFont(config.getSettingToFont(Constant.TEXT_FONT));

		Color bgColor = config.getSettingToColor(Constant.BACKGROUND_COLOR);
		Color fgColor = config.getSettingToColor(Constant.FOREGROUND_COLOR);
		textArea.setBackground(bgColor);
		textArea.setForeground(fgColor);
		chapterList.setBackground(bgColor);

		JBookReader.this.setTitle(Constant.JBOOKREADER);
		chapterLabel.setText("No Chapter");
		colorLabel.setText(ColorUtil.getHexColorValue(bgColor) + " & " + ColorUtil.getHexColorValue(fgColor));
		skinLabel.setText(config.getSetting(Constant.LOOK_AND_FEEL));
		setFontStatus();

		boolean showLineNumber = config.getSettingToBoolean(Constant.SHOW_LINE_NUMBER);
		scrollPane.setRowHeaderView(showLineNumber ? new LineNumberTable(textArea) : null);
		statusBar.setVisible(config.getSettingToBoolean(Constant.SHOW_STATUS_BAR));
	}

	/**
	 * 创建系统托盘的对象 步骤: 1,获得当前操作系统的托盘对象 2,创建弹出菜单popupMenu 3,创建托盘图标icon
	 * 4,创建系统的托盘对象trayIcon
	 */
	public void createTrayIcon() {
		sysTray = SystemTray.getSystemTray();// 获得当前操作系统的托盘对象
		PopupMenu popupMenu = new PopupMenu();// 弹出菜单
		MenuItem showMi = new MenuItem(langConfig.getTrans("Show"));
		MenuItem exitMi = new MenuItem(langConfig.getTrans("Exit"));
		popupMenu.add(showMi);
		popupMenu.addSeparator();
		popupMenu.add(exitMi);
		// 为弹出菜单项添加事件
		showMi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sysTray.remove(trayIcon);
				setVisible(true);
			}
		});
		exitMi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		trayIcon = new TrayIcon(new ImageIcon(this.getClass().getResource("book.png")).getImage(),
				Constant.JBOOKREADER, popupMenu);
		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sysTray.remove(trayIcon);
					setVisible(true);
				}
			}
		});
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
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JBookReader.config.switchSection(Constant.GLOBAL_SETTING);
					// 设置皮肤
					String lookAndFeel = JBookReader.config.getSetting(Constant.LOOK_AND_FEEL);
					UIManager.setLookAndFeel(lookAndFeel);

					new JBookReader().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		});
	}

}
