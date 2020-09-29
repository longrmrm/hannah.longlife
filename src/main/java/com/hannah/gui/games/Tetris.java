package com.hannah.gui.games;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Tetris extends JFrame implements ActionListener {
	private GamePanel gamePanel;
	// 菜单工具组件
	JMenuBar menuBar = new JMenuBar();
	// menuBar.setSize(20, 20);
	JMenu menuFile = new JMenu("游戏");
	JMenuItem newGame = new JMenuItem("新游戏");
	JMenuItem pauseGame = new JMenuItem("暂停");
	JMenuItem overGame = new JMenuItem("结束");
	JMenu menuHelp = new JMenu("帮助");
	//
	JLabel labLevel = new JLabel("选择关卡：");
	JTextField txtLevel = new JTextField();

	public Tetris(GamePanel gp) {
		setBounds(500, 100, 400, 480);
		setTitle("my small game");
		setLayout(new BorderLayout());
		JFrame.setDefaultLookAndFeelDecorated(true);
		// 添加菜单条

		setJMenuBar(createMenuBar());
		this.gamePanel = gp;
		if (gamePanel != null) {
			add(gamePanel);
			addKeyListener(gamePanel);
		}
		labLevel.setBounds(260, 140, 50, 30);
		txtLevel.setBounds(260, 180, 50, 30);
		// add(labLevel);
		// add(txtLevel);
		setVisible(true);
		// setResizable(false);
	}

	// 菜单条
	public JMenuBar createMenuBar() {

		menuFile.add(newGame);
		menuFile.add(pauseGame);
		menuFile.add(overGame);

		menuBar.add(menuFile);
		menuBar.add(menuHelp);
		menuHelp.add(new JMenuItem("版本号"));
		// 添加事件
		newGame.addActionListener(this);
		pauseGame.addActionListener(this);
		overGame.addActionListener(this);
		return menuBar;
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		Tetris game = new Tetris(new GamePanel(new int[22][12], new int[2][2]));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println(e.getActionCommand());
		if (e.getActionCommand().equals("新游戏")) {
			System.out.println("newgame");
			if (gamePanel != null) {
				remove(gamePanel);
				removeKeyListener(gamePanel);
			}
			this.gamePanel = new GamePanel(new int[22][12], new int[2][2]);
			add(gamePanel);
			addKeyListener(gamePanel);
		} else if (e.getActionCommand().equals("暂停")) {
			pauseGame.setText("继续");
			gamePanel.timer.stop();
		} else if (e.getActionCommand().equals("继续")) {
			pauseGame.setText("暂停");
			gamePanel.timer.start();
		} else if (e.getActionCommand().equals("结束")) {
			if (gamePanel != null)
				this.remove(gamePanel);
			gamePanel = null;
		}
	}
}

class GamePanel extends JPanel implements KeyListener {
	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	Random random = new Random();
	JLabel labLevel = new JLabel("选择关卡：");
	JTextField txtLevel = new JTextField();
	int size = 20;// 显示大小
	// 座标
	int x = 4, y;
	int i = 0, j = 0;
	Timer timer = null;
	int level = 1;
	int score = 0;
	int steep = 1000;// 方块下降的速度
	TimerAction timerAction;
	// 绘图颜色
	Color mapColor;
	Color moveShapColor;
	int colors[][] = { { 255, 222, 173 }, { 47, 79, 79 }, { 255, 228, 225 }, { 0, 255, 0 },
			{ 0, 0, 255 }, { 255, 193, 37 }, { 156, 156, 156 }, { 202, 225, 255 },
			{ 171, 130, 255 }, { 224, 102, 255 }, { 255, 62, 150 }, { 255, 0, 0 } };
	// squareType类型和 squareState状态
	int squareType, squareState;
	int nextState = 0;// 每次产生一个新的会自加1并取2的余数赋给自己
	int map[][];// = new int;
	int nextTypeAndState[][];
	// 方块的几种形状态和状态 （每个方块由一个4*4的矩阵构成）
	// I O T Z S L J
	int shape[][][];

	// 构造函数

	public GamePanel(int map[][], int nextTypeAndState[][]) {
		this.nextTypeAndState = nextTypeAndState;
		this.map = map;
		this.shape = this.initShap();
		setBackground(new Color(250, 250, 250));
		initMap();
		initWall();
		createdSquare();
		timerAction = new TimerAction();
		timer = new Timer(steep, timerAction);
		timer.start();
		score = 0;
		initTypeAndState();
		this.mapColor = createColor();
		this.moveShapColor = createColor();
		// setLayout();
		//
		// labLevel.setBounds(260, 140, 80, 30);
		// txtLevel.setBounds(50, 80, 50, 80);
		// txtLevel.setText("111");
		// txtLevel.setSize(10, 10);
		// add(labLevel,BorderLayout.EAST);
		// add(txtLevel,BorderLayout.EAST);
		setSize(400, 480);
	}

	public int[][][] initShap() {
		return new int[][][] {
				{ { 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
						{ 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0 },
						{ 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
						{ 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0 } },
				// s
				{ { 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
						{ 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
						{ 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
						{ 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 } },
				// z
				{ { 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
						{ 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
						{ 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
						{ 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 } },
				// j
				{ { 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 },
						{ 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
						{ 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
						{ 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 } },
				// o
				{ { 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
						{ 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
						{ 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
						{ 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } },
				// l
				{ { 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 },
						{ 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
						{ 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
						{ 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 } },
				// t
				{ { 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
						{ 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
						{ 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

						{ 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0 } } };
	}

	// 初始化地图
	public void initMap() {
		for (i = 0; i < map.length; i++) {
			for (j = 0; j < map[0].length; j++) {
				map[i][j] = 0;
			}
		}
	}

	// 初始化围墙
	public void initWall() {
		for (i = 0; i < map.length; i++) {
			map[i][0] = 2; // 两边为2 （1.表示普通2 表示围墙）
			map[i][11] = 2;
		}
		// 底部
		for (j = 0; j < 12; j++) {
			map[map.length - 1][j] = 2;
		}
	}

	// 产生新方块方法
	public void createdSquare() {
		x = 4;
		y = 0;
		nextState = (nextState + 1) % 2;
		getTypeAndState(nextState);

	}

	// 获取当前方块数据，并计算下一个方块数据
	public void getTypeAndState(int index) {
		squareType = nextTypeAndState[0][index];
		nextTypeAndState[0][index] = (int) (Math.random() * 100) % 7;
		squareState = nextTypeAndState[1][index];
		nextTypeAndState[1][index] = (int) (Math.random() * 100) % 4;
	}

	// 初始化当前方块和下一个方块
	public void initTypeAndState() {

		for (int i = 0; i < nextTypeAndState[0].length; i++) {
			nextTypeAndState[0][i] = (int) (Math.random() * 100) % 7;
		}
		for (int i = 0; i < nextTypeAndState[1].length; i++) {
			nextTypeAndState[1][i] = (int) (Math.random() * 100) % 4;
		}
	}

	// 随机产生颜色方法
	public Color createColor() {
		int temp[] = colors[random.nextInt(colors.length)];// 此处不用减1
		return new Color(temp[0], temp[1], temp[2]);
	}

	// 是否超出边界
	public int isOverstep(int x, int y, int squareType, int squareState) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (shape[squareType][squareState][i * 4 + j] == 1) {
					if (map[y + i][j + x] > 0) {
						return 0;
					}
				}
			}
		}
		// 0 1 0 0 0 1 0 0
		// 1 1 1 0 0 1 0 0
		// 0 0 0 0 0 1 0 0
		// 0 0 0 0 0 1 0 0
		return 1;
	}

	// 四个移动方法
	public void leftMove() {
		// 合法没有超出边界
		if (isOverstep(x - 1, y, squareType, squareState) == 1) {
			x -= 1;
		}
		// System.out.println(x);
		repaint();
	}

	public void rightMove() {
		if (isOverstep(x + 1, y, squareType, squareState) == 1) {
			x += 1;
		}
		repaint();
	}

	public void dowmMove() {
		if (isOverstep(x, y + 1, squareType, squareState) == 1) {
			y += 1;
		}
		// destroyLine();// 销行得分
		repaint();
	}

	public void trunMove() {
		if (isOverstep(x, y, squareType, (squareState + 1) % 4) == 1) {
			squareState = (squareState + 1) % 4;
		}
		repaint();
	}

	// 按键临听
	@Override
	public void keyPressed(KeyEvent e) {
		// System.out.println(e.getKeyCode());
		switch (e.getKeyCode()) {
		case 65:
		case 37:
			leftMove();
			break;
		case 68:
		case 39:
			rightMove();
			break;
		case 83:
		case 40:
			dowmMove();
			break;
		case 87:
		case 38:
			trunMove();
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	// 添加到 map中
	public void addSqu(int x, int y, int squareType, int squareState) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (shape[squareType][squareState][i * 4 + j] == 1) {
					map[i + y][x + j] = 1;
				}
			}
		}
	}

	// 重绘调用的方法
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// 正在下落的方块
		for (j = 0; j < 16; j++) {
			if (shape[squareType][squareState][j] == 1) {
				g.setColor(new Color(40, 40, 80));
				g.drawRect((j % 4 + x) * size, (j / 4 + y) * size, size, size);
				g.setColor(moveShapColor);
				g.fillRect((j % 4 + x) * size + 2, (j / 4 + y) * size + 2, size - 4, size - 4);
				// System.out.println("移动中的方块");
			}
		}
		// 已经放在map中的方块2固定的围墙 1是随机产生后下落的
		for (i = 0; i < map.length; i++) {
			for (j = 0; j < map[0].length; j++) {
				if (map[i][j] == 1) {
					g.setColor(new Color(40, 100, 220));
					g.drawRect(j * size, i * size, size, size);
					g.setColor(mapColor);
					g.fillRect(j * size + 2, i * size + 2, size - 4, size - 4);
				} else if (map[i][j] == 2) {
					g.setColor(createColor());
					g.fillRect(j * size, i * size, size, size);
				}
			}
		}
		g.drawString("分数:" + score, 260, 100);
		g.drawString("下一个方块:", 260, 120);
		g.drawString("当前等级：" + level, 260, 140);
		// 显示下一个图象
		int type = nextTypeAndState[0][(nextState + 1) % 2];
		int state = nextTypeAndState[1][(nextState + 1) % 2];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (shape[type][state][i * 4 + j] == 1) {
					g.setColor(mapColor);
					g.drawRect(j * 20 + 260, i * 20 + 170, size, size);
				}
			}
		}
	}

	// 销行得分方法
	public void destroyLine() {
		int destroyCount = 0;
		for (int i = map.length - 2; i > 0; i--) {
			int count = 0;
			// for循环除掉两边的围墙
			for (int j = 1; j < map[0].length - 1; j++) {
				count += map[i][j];
			}
			if (count == map[0].length - 2) {
				destroyCount += 1;
				for (int x = i; x > 0; x--) {
					for (int j = 1; j < map[0].length - 1; j++) {
						map[x][j] = map[x - 1][j];
					}

				}
				i = i + 1;
			}
		}
		// 是否结束游戏
		if (isOver()) {
			this.size = 20;// 显示大小
			// 座标
			this.x = 4;
			this.score = 0;
			this.steep = 1000;// 方块下降的速度
			// this.map[][] = new int[22][12];
			this.nextState = 0;// 每次产生一个新的会自加1并取2的余数赋给自己
			this.nextTypeAndState = new int[2][2];
			this.map = new int[22][12];
			this.shape = initShap();
			setBackground(new Color(250, 250, 250));
			initMap();
			initWall();
			createdSquare();
			timer.removeActionListener(timerAction);
			this.timerAction = new TimerAction();
			timer = new Timer(steep, timerAction);
			timer.start();
			score = 0;
			level = 0;
			initTypeAndState();
			setSize(400, 480);
			return;
		}
		countScore(destroyCount);// 计算分数
	}

	// 统计得分计算下一关
	public void countScore(int destroyCount) {
		switch (destroyCount) {
		case 0:
			return;
		case 1:
			score += 1;
			break;
		case 2:
			score += 3;
			break;
		case 3:
			score += 5;
			break;
		case 4:
			score += 10;
			break;
		}
		if (score >= level * 20) {
			System.out.println("下一关");
			level += 1;
			mapColor = createColor();// 设置一下关的颜色
			moveShapColor = createColor();
			JLabel labNextLevel = new JLabel("wellcome to " + score / 100 + "level good lock");
			labNextLevel.setBounds(50, 100, 200, 30);
			this.add(labNextLevel);
			// timer.stop();
			// labNextLevel=null;
			this.remove(labNextLevel);
			steep = steep * 80 / 100;
			timer.setDelay(steep);
			// timer.start();
		}
	}

	// //根据分数判断是否进入下一关
	// public void nextLevel(int score){
	// if()
	// }
	// 判断游戏结束
	public boolean isOver() {
		// map的第一行是否为1
		for (int i = 1; i < map[0].length - 1; i++) {
			if (map[0][i] == 1)
				return true;
		}
		return false;
	}

	// timer 刷新调用的动作侦听器
	class TimerAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (isOverstep(x, y + 1, squareType, squareState) == 1) {
				y = y + 1;
				repaint();
			} else if (isOverstep(x, y + 1, squareType, squareState) == 0) {
				y = y + 1;
				repaint();
				try {
					Thread.sleep(steep * 50 / 100);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				addSqu(x, y - 1, squareType, squareState);
				createdSquare();
				repaint();
				destroyLine();// 销行得分
			}
		}
	}

}
