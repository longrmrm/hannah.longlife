package com.hannah.swing.component;

import com.hannah.common.util.DateUtil;
import com.hannah.common.util.ImageUtil;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.*;

/**
 * 日期选择控件
 * @author longrm
 * @date 2012-7-11
 */
public class DateChooser extends JLabel {

	private static final long serialVersionUID = -8015295976829454437L;

	private Calendar now = Calendar.getInstance();
	private Calendar select;

	private JPanel monthPanel;// 月历
	private final LabelManager lm = new LabelManager();
	private JP1 jp1;// 四块面板,组成
	private JP2 jp2;
	private JP3 jp3;
	private JP4 jp4;

	// private JLabel showDate;
	private Popup pop;
	private boolean isShow = false;

	private Font font = new Font("宋体", Font.PLAIN, 12);
	private Color bgColor = new Color(160, 185, 215);
	private DateFormat format;

	private JSpinner hourSpinner;
	private boolean showHour = false;

	/**
	 * Creates a new instance of DateChooser
	 */
	public DateChooser() {
		this(new Date(), false);
	}

	public DateChooser(Date date) {
		this(date, false);
	}

	public DateChooser(Date date, boolean showHour) {
		select = Calendar.getInstance();
		select.setTime(date);

		this.showHour = showHour;
		format = showHour ? DateUtil.ssFormat : DateUtil.ddChFormat;

		initPanel();
		initLabel();

		// this.add(showDate);
		// this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		// this.setBackground(Color.WHITE);
	}

	/**
	 * 得到当前选择框的日期
	 */
	public Date getDate() {
		try {
			return format.parse(this.getText());
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
	}

	public void setDate(Date date) {
		select.setTime(date);
		this.setText(format.format(select.getTime()));
		if (showHour)
			hourSpinner.setValue(date);
		refresh();
	}

	public void setDateFormat(DateFormat format) {
		this.format = format;
		this.setText(format.format(select.getTime()));
	}

	// 根据初始化的日期,初始化面板
	private void initPanel() {
		monthPanel = new JPanel(new BorderLayout());
		monthPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		JPanel up = new JPanel(new BorderLayout());
		up.add(jp1 = new JP1(), BorderLayout.NORTH);
		up.add(jp2 = new JP2(), BorderLayout.CENTER);
		monthPanel.add(jp3 = new JP3(), BorderLayout.CENTER);
		monthPanel.add(up, BorderLayout.NORTH);
		monthPanel.add(jp4 = new JP4(), BorderLayout.SOUTH);
		this.addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) {

			}

			public void ancestorRemoved(AncestorEvent event) {

			}

			// 只要祖先组件一移动,马上就让popup消失
			public void ancestorMoved(AncestorEvent event) {
				hidePanel();
			}

		});
	}

	// 初始化标签
	private void initLabel() {
		// showDate = new JLabel(format.format(select.getTime()));
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.setText(format.format(select.getTime()));
		this.setRequestFocusEnabled(true);

		this.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent me) {
				if (isEnabled()) {
					setCursor(new Cursor(Cursor.HAND_CURSOR));
					setForeground(Color.RED);
				}
			}

			public void mouseExited(MouseEvent me) {
				if (isEnabled()) {
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					setForeground(Color.BLACK);
				}
			}

			public void mousePressed(MouseEvent me) {
				if (isEnabled()) {
					setForeground(Color.CYAN);
					if (isShow) {
						hidePanel();
					} else {
						showPanel(DateChooser.this);
					}
				}
			}

			public void mouseReleased(MouseEvent me) {
				if (isEnabled()) {
					setForeground(Color.BLACK);
				}
			}
		});

		this.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				hidePanel();
			}

			public void focusGained(FocusEvent e) {

			}
		});
	}

	// 根据新的日期刷新
	private void refresh() {
		jp1.updateDate();
		jp3.updateDate();
		SwingUtilities.updateComponentTreeUI(this);
	}

	// 提交日期
	private void commit() {
		if (showHour) {
			Calendar c = Calendar.getInstance();
			c.setTime((Date) hourSpinner.getValue());
			select.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
			select.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
			select.set(Calendar.SECOND, c.get(Calendar.SECOND));
		}
		this.setText(format.format(select.getTime()));
		hidePanel();
	}

	private void hidePanel() {
		if (pop != null) {
			isShow = false;
			pop.hide();
			pop = null;
		}
	}

	private void showPanel(Component owner) {
		if (pop != null) {
			pop.hide();
		}
		Point show = new Point(0, this.getHeight());
		SwingUtilities.convertPointToScreen(show, this);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		int x = show.x;
		int y = show.y;
		if (x < 0) {
			x = 0;
		}
		if (x > size.width - 295) {
			x = size.width - 295;
		}
		if (y < size.height - 170) {
		} else {
			y -= 188;
		}
		pop = PopupFactory.getSharedInstance().getPopup(owner, monthPanel, x, y);
		pop.show();
		isShow = true;
	}

	private class JP1 extends JPanel {

		JLabel lastYear, nextYear, lastMonth, nextMonth, center;

		public JP1() {
			// super(new BorderLayout());
			this.setBackground(bgColor);
			initJP1();
		}

		private void initJP1() {
			lastYear = new JLabel(" << ", JLabel.CENTER);
			nextYear = new JLabel(" >> ", JLabel.CENTER);
			lastYear.setToolTipText("上一年");
			nextYear.setToolTipText("下一年");
			lastMonth = new JLabel(" < ", JLabel.CENTER);
			lastMonth.setToolTipText("上一月");
			nextMonth = new JLabel(" > ", JLabel.CENTER);
			nextMonth.setToolTipText("下一月");
			center = new JLabel("", JLabel.CENTER);
			updateDate();

			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			this.setLayout(gridbag);
			c.weightx = 0.5;
			gridbag.setConstraints(lastYear, c);
			this.add(lastYear);
			c.weightx = 0.5;
			gridbag.setConstraints(lastMonth, c);
			this.add(lastMonth);

			c.weightx = 8.0;
			gridbag.setConstraints(center, c);
			this.add(center);

			c.weightx = 0.5;
			gridbag.setConstraints(nextMonth, c);
			this.add(nextMonth);
			c.weightx = 0.5;
			gridbag.setConstraints(nextYear, c);
			this.add(nextYear);

			// this.add(left,BorderLayout.WEST);
			// this.add(center,BorderLayout.CENTER);
			// this.add(right,BorderLayout.EAST);
			this.setPreferredSize(new Dimension(295, 25));

			lastYear.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent me) {
					lastYear.setCursor(new Cursor(Cursor.HAND_CURSOR));
					lastYear.setForeground(Color.RED);
				}

				public void mouseExited(MouseEvent me) {
					lastYear.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					lastYear.setForeground(Color.BLACK);
				}

				public void mousePressed(MouseEvent me) {
					select.add(Calendar.YEAR, -1);
					lastYear.setForeground(Color.WHITE);
					refresh();
				}

				public void mouseReleased(MouseEvent me) {
					lastYear.setForeground(Color.BLACK);
				}
			});

			nextYear.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent me) {
					nextYear.setCursor(new Cursor(Cursor.HAND_CURSOR));
					nextYear.setForeground(Color.RED);
				}

				public void mouseExited(MouseEvent me) {
					nextYear.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					nextYear.setForeground(Color.BLACK);
				}

				public void mousePressed(MouseEvent me) {
					select.add(Calendar.YEAR, 1);
					nextYear.setForeground(Color.WHITE);
					refresh();
				}

				public void mouseReleased(MouseEvent me) {
					nextYear.setForeground(Color.BLACK);
				}
			});

			lastMonth.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent me) {
					lastMonth.setCursor(new Cursor(Cursor.HAND_CURSOR));
					lastMonth.setForeground(Color.RED);
				}

				public void mouseExited(MouseEvent me) {
					lastMonth.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					lastMonth.setForeground(Color.BLACK);
				}

				public void mousePressed(MouseEvent me) {
					select.add(Calendar.MONTH, -1);
					lastMonth.setForeground(Color.WHITE);
					refresh();
				}

				public void mouseReleased(MouseEvent me) {
					lastMonth.setForeground(Color.BLACK);
				}
			});

			nextMonth.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent me) {
					nextMonth.setCursor(new Cursor(Cursor.HAND_CURSOR));
					nextMonth.setForeground(Color.RED);
				}

				public void mouseExited(MouseEvent me) {
					nextMonth.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					nextMonth.setForeground(Color.BLACK);
				}

				public void mousePressed(MouseEvent me) {
					select.add(Calendar.MONTH, 1);
					nextMonth.setForeground(Color.WHITE);
					refresh();
				}

				public void mouseReleased(MouseEvent me) {
					nextMonth.setForeground(Color.BLACK);
				}
			});
		}

		private void updateDate() {
			center.setText(select.get(Calendar.YEAR) + "年" + (select.get(Calendar.MONTH) + 1) + "月");
		}
	}

	private class JP2 extends JPanel {

		public JP2() {
			this.setPreferredSize(new Dimension(295, 20));
		}

		protected void paintComponent(Graphics g) {
			g.setFont(font);
			g.drawString("星期日 星期一 星期二 星期三 星期四 星期五 星期六", 5, 11);
			g.drawLine(0, 15, getWidth(), 15);
		}
	}

	private class JP3 extends JPanel {

		public JP3() {
			super(new GridLayout(6, 7));
			this.setPreferredSize(new Dimension(295, 100));
			initJP3();
		}

		private void initJP3() {
			updateDate();
		}

		public void updateDate() {
			this.removeAll();
			lm.clear();
			Date temp = select.getTime();
			Calendar select = Calendar.getInstance();
			select.setTime(temp);
			select.set(Calendar.DAY_OF_MONTH, 1);
			int index = select.get(Calendar.DAY_OF_WEEK);
			int sum = (index == 1 ? 8 : index);
			select.add(Calendar.DAY_OF_MONTH, 0 - sum);
			for (int i = 0; i < 42; i++) {
				select.add(Calendar.DAY_OF_MONTH, 1);
				lm.addLabel(new MyLabel(select.get(Calendar.YEAR), select.get(Calendar.MONTH), select
						.get(Calendar.DAY_OF_MONTH)));
			}
			for (MyLabel my : lm.getLabels()) {
				this.add(my);
			}
			select.setTime(temp);
		}
	}

	private class MyLabel extends JLabel implements Comparator<MyLabel>, MouseListener, MouseMotionListener {

		private int year, month, day;
		private boolean isSelected;

		public MyLabel(int year, int month, int day) {
			super("" + day, JLabel.CENTER);
			this.year = year;
			this.day = day;
			this.month = month;
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
			this.setFont(font);
			if (month == select.get(Calendar.MONTH)) {
				this.setForeground(Color.BLACK);
			} else {
				this.setForeground(Color.LIGHT_GRAY);
			}
			if (day == select.get(Calendar.DAY_OF_MONTH)) {
				this.setBackground(bgColor);
			} else {
				this.setBackground(Color.WHITE);
			}
		}

		public boolean getIsSelected() {
			return isSelected;
		}

		public void setSelected(boolean b, boolean isDrag) {
			isSelected = b;
			if (b && !isDrag) {
				int temp = select.get(Calendar.MONTH);
				select.set(year, month, day);
				if (temp == month) {
					SwingUtilities.updateComponentTreeUI(jp3);
				} else {
					refresh();
				}
			}
			this.repaint();
		}

		protected void paintComponent(Graphics g) {
			if (day == select.get(Calendar.DAY_OF_MONTH) && month == select.get(Calendar.MONTH)) {
				// 如果当前日期是选择日期,则高亮显示
				g.setColor(bgColor);
				g.fillRect(0, 0, getWidth(), getHeight());
			}
			if (year == now.get(Calendar.YEAR) && month == now.get(Calendar.MONTH)
					&& day == now.get(Calendar.DAY_OF_MONTH)) {
				// 如果日期和当前日期一样,则用红框
				Graphics2D gd = (Graphics2D) g;
				gd.setColor(Color.RED);
				Polygon p = new Polygon();
				p.addPoint(0, 0);
				p.addPoint(getWidth() - 1, 0);
				p.addPoint(getWidth() - 1, getHeight() - 1);
				p.addPoint(0, getHeight() - 1);
				gd.drawPolygon(p);
			}
			if (isSelected) {// 如果被选中了就画出一个虚线框出来
				Stroke s = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1.0f, new float[] {
						2.0f, 2.0f }, 1.0f);
				Graphics2D gd = (Graphics2D) g;
				gd.setStroke(s);
				gd.setColor(Color.BLACK);
				Polygon p = new Polygon();
				p.addPoint(0, 0);
				p.addPoint(getWidth() - 1, 0);
				p.addPoint(getWidth() - 1, getHeight() - 1);
				p.addPoint(0, getHeight() - 1);
				gd.drawPolygon(p);
			}
			super.paintComponent(g);
		}

		public boolean contains(Point p) {
			return this.getBounds().contains(p);
		}

		private void update() {
			repaint();
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			isSelected = true;
			update();
		}

		public void mouseReleased(MouseEvent e) {
			// 双击时选定
			if (e.getClickCount() == 2) {
				commit();
				return;
			}
			Point p = SwingUtilities.convertPoint(this, e.getPoint(), jp3);
			lm.setSelect(p, false);
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseDragged(MouseEvent e) {
			Point p = SwingUtilities.convertPoint(this, e.getPoint(), jp3);
			lm.setSelect(p, true);
		}

		public void mouseMoved(MouseEvent e) {
		}

		public int compare(MyLabel o1, MyLabel o2) {
			Calendar c1 = Calendar.getInstance();
			c1.set(o1.year, o2.month, o1.day);
			Calendar c2 = Calendar.getInstance();
			c2.set(o2.year, o2.month, o2.day);
			return c1.compareTo(c2);
		}
	}

	private class LabelManager {

		private List<MyLabel> list;

		public LabelManager() {
			list = new ArrayList<MyLabel>();
		}

		public List<MyLabel> getLabels() {
			return list;
		}

		public void addLabel(MyLabel my) {
			list.add(my);
		}

		public void clear() {
			list.clear();
		}

		public void setSelect(MyLabel my, boolean b) {
			for (MyLabel m : list) {
				if (m.equals(my)) {
					m.setSelected(true, b);
				} else {
					m.setSelected(false, b);
				}
			}
		}

		public void setSelect(Point p, boolean b) {
			// 如果是拖动,则要优化一下,以提高效率
			if (b) {
				// 表示是否能返回,不用比较完所有的标签,能返回的标志就是把上一个标签和
				// 将要显示的标签找到了就可以了
				boolean findPrevious = false, findNext = false;
				for (MyLabel m : list) {
					if (m.contains(p)) {
						findNext = true;
						if (m.getIsSelected()) {
							findPrevious = true;
						} else {
							m.setSelected(true, b);
						}
					} else if (m.getIsSelected()) {
						findPrevious = true;
						m.setSelected(false, b);
					}
					if (findPrevious && findNext) {
						return;
					}
				}
			} else {
				MyLabel temp = null;
				for (MyLabel m : list) {
					if (m.contains(p)) {
						temp = m;
					} else if (m.getIsSelected()) {
						m.setSelected(false, b);
					}
				}
				if (temp != null) {
					temp.setSelected(true, b);
				}
			}
		}

	}

	private class JP4 extends JPanel {

		public JP4() {
			final JLabel jl = new JLabel("今天： " + DateUtil.dateToDdChString(new Date()));
			jl.setToolTipText("点击回到今天日期");

			jl.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent me) {
					jl.setCursor(new Cursor(Cursor.HAND_CURSOR));
					jl.setForeground(Color.RED);
				}

				public void mouseExited(MouseEvent me) {
					jl.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					jl.setForeground(Color.BLACK);
				}

				public void mousePressed(MouseEvent me) {
					jl.setForeground(Color.WHITE);
					select.setTime(new Date());
					refresh();
					commit();
					jl.setForeground(Color.BLACK);
				}

				public void mouseReleased(MouseEvent me) {
					jl.setForeground(Color.BLACK);
				}
			});

			this.setBackground(bgColor);
			this.setLayout(new FlowLayout(FlowLayout.LEFT));
			this.add(jl);
			this.setPreferredSize(new Dimension(295, 25));

			if (showHour) {
				hourSpinner = new JSpinner();
				hourSpinner.setModel(new SpinnerDateModel());
				JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(hourSpinner, "HH时mm分ss秒");
				hourSpinner.setEditor(dateEditor);
				hourSpinner.setValue(select.getTime());

				JLabel commitLb = new JLabel(ImageUtil.getImageIcon("/ok.png"));
				commitLb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				commitLb.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						try {
							hourSpinner.commitEdit();
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
						commit();
					}
				});

				this.add(hourSpinner);
				this.add(commitLb);
				this.setPreferredSize(new Dimension(295, 28));
			}
		}

	}

	public static void main(String[] args) {
		final DateChooser mp = new DateChooser(new Date(), true);
		mp.setDate(DateUtil.ddStringToDate("2014-02-08"));
		JFrame jf = new JFrame("test");
		jf.add(mp, BorderLayout.CENTER);
		jf.add(new JButton("测试用的"), BorderLayout.NORTH);
		jf.pack();
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
