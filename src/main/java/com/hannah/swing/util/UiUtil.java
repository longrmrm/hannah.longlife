package com.hannah.swing.util;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author longrm
 * @date 2012-4-26
 */
public class UiUtil {

	/**
	 * get current layeredPane
	 * @param container
	 * @return
	 */
	public static JLayeredPane getLayeredPane(Container container) {
		JLayeredPane layeredPane = null;
		if (container instanceof JFrame)
			layeredPane = ((JFrame) container).getLayeredPane();
		else if (container instanceof JDialog)
			layeredPane = ((JDialog) container).getLayeredPane();
		else {
			for (int i = 0; i < container.getComponentCount(); i++) {
				Component component = container.getComponent(i);
				if (component instanceof JApplet) {
					layeredPane = ((JApplet) component).getLayeredPane();
				} else if (component instanceof Container) {
					return getLayeredPane((Container) component);
				}
			}
		}
		return layeredPane;
	}

	/**
	 * set c1 location relative to c2
	 * @param c1
	 * @param c2
	 */
	public static void setLocationRelativeTo(Component c1, Component c2) {
		Container root = null;

		if (c2 != null) {
			if (c2 instanceof Window || c2 instanceof Applet) {
				root = (Container) c2;
			} else {
				Container parent;
				for (parent = c2.getParent(); parent != null; parent = parent.getParent()) {
					if (parent instanceof Window || parent instanceof Applet) {
						root = parent;
						break;
					}
				}
			}
		}

		if ((c2 != null && !c2.isShowing()) || root == null || !root.isShowing()) {
			Dimension paneSize = c1.getSize();

			Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
			c1.setLocation(centerPoint.x - paneSize.width / 2, centerPoint.y - paneSize.height / 2);
		} else {
			Dimension invokerSize = c2.getSize();
			Point invokerScreenLocation = c2.getLocation(); // by longrm:
															// c2.getLocationOnScreen();

			Rectangle windowBounds = c1.getBounds();
			int dx = invokerScreenLocation.x + ((invokerSize.width - windowBounds.width) >> 1);
			int dy = invokerScreenLocation.y + ((invokerSize.height - windowBounds.height) >> 1);
			Rectangle ss = root.getGraphicsConfiguration().getBounds();

			// Adjust for bottom edge being offscreen
			if (dy + windowBounds.height > ss.y + ss.height) {
				dy = ss.y + ss.height - windowBounds.height;
				if (invokerScreenLocation.x - ss.x + invokerSize.width / 2 < ss.width / 2) {
					dx = invokerScreenLocation.x + invokerSize.width;
				} else {
					dx = invokerScreenLocation.x - windowBounds.width;
				}
			}

			// Avoid being placed off the edge of the screen
			if (dx + windowBounds.width > ss.x + ss.width) {
				dx = ss.x + ss.width - windowBounds.width;
			}
			if (dx < ss.x)
				dx = ss.x;
			if (dy < ss.y)
				dy = ss.y;

			c1.setLocation(dx, dy);
		}
	}

	public static void showStackTraceDialog(Throwable throwable, String title) {
		String message = throwable.getMessage() == null ? throwable.toString() : throwable.getMessage();
		showStackTraceDialog(throwable, title, message);
	}

	public static void showStackTraceDialog(Throwable throwable, String title, String message) {
		Window window = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
		showStackTraceDialog(throwable, window, title, message);
	}

	/**
	 * show stack trace dialog when exception throws
	 * @param throwable
	 * @param parentComponent
	 * @param title
	 * @param message
	 */
	public static void showStackTraceDialog(Throwable throwable, Component parentComponent, String title, String message) {
		final String more = "More";
		// create stack strace panel
		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel label = new JLabel(more + ">>");
		labelPanel.add(label);

		JTextArea straceTa = new JTextArea();
		final JScrollPane taPane = new JScrollPane(straceTa);
		taPane.setPreferredSize(new Dimension(360, 240));
		taPane.setVisible(false);
		// print stack trace into textarea
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		throwable.printStackTrace(new PrintStream(out));
		straceTa.setForeground(Color.RED);
		straceTa.setText(new String(out.toByteArray()));

		final JPanel stracePanel = new JPanel(new BorderLayout());
		stracePanel.add(labelPanel, BorderLayout.NORTH);
		stracePanel.add(taPane, BorderLayout.CENTER);

		label.setForeground(Color.BLUE);
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JLabel tmpLab = (JLabel) e.getSource();
				if (tmpLab.getText().equals(more + ">>")) {
					tmpLab.setText("<<" + more);
					taPane.setVisible(true);
				} else {
					tmpLab.setText(more + ">>");
					taPane.setVisible(false);
				}
				SwingUtilities.getWindowAncestor(taPane).pack();
			};
		});

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(message), BorderLayout.NORTH);
		panel.add(stracePanel, BorderLayout.CENTER);

		JOptionPane pane = new JOptionPane(panel, JOptionPane.ERROR_MESSAGE);
		JDialog dialog = pane.createDialog(parentComponent, title);
		int maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width * 2 / 3;
		if (dialog.getWidth() > maxWidth) {
			dialog.setSize(new Dimension(maxWidth, dialog.getHeight()));
			setLocationRelativeTo(dialog, parentComponent);
		}
		dialog.setResizable(true);
		dialog.setVisible(true);
		dialog.dispose();
	}

	public static boolean isAsync() {
		return "true".equals(System.getProperty("async", "true"));
	}

	public static void setAsync(boolean async) {
		System.setProperty("async", async + "");
	}

	public static void asyncInvoke(final InvokeHandler invokeHandler) {
		asyncInvoke(invokeHandler, 0, TimeUnit.MILLISECONDS);
	}

	/**
	 * invoke in EDT, restart a new thread to excute some busy operation
	 * @param invokeHandler
	 * @param timeout
	 * @param unit
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public static void asyncInvoke(final InvokeHandler invokeHandler, final long timeout, final TimeUnit unit) {
		final boolean async = isAsync();
		if (timeout <= 0) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						if (async)
							SwingUtilities.invokeAndWait(new Runnable() {
								@Override
								public void run() {
									try {
										invokeHandler.before();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						else
							invokeHandler.before();

						long l1 = System.currentTimeMillis();
						final Object result = invokeHandler.execute();
						long l2 = System.currentTimeMillis();
						// record execute time in invokeHandler
						if (invokeHandler instanceof AbstractInvokeHandler)
							((AbstractInvokeHandler) invokeHandler).setExecuteTime(l2 - l1);

						if (async)
							SwingUtilities.invokeAndWait(new Runnable() {
								@Override
								public void run() {
									try {
										invokeHandler.success(result);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						else
							invokeHandler.success(result);
					} catch (final Exception e) {
						e.printStackTrace();
						if (async)
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									invokeHandler.failure(e);
								}
							});
						else
							invokeHandler.failure(e);
					} finally {
						if (async)
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									invokeHandler.after();
								}
							});
						else
							invokeHandler.after();
					}
				}
			};
			new Thread(runnable).start();
			return;
		}

		// use java.util.concurrent to handle timeout exception
		Runnable target = new Runnable() {
			Callable<Object> callable = new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					long l1 = System.currentTimeMillis();
					Object result = invokeHandler.execute();
					long l2 = System.currentTimeMillis();
					// record execute time in invokeHandler
					if (invokeHandler instanceof AbstractInvokeHandler)
						((AbstractInvokeHandler) invokeHandler).setExecuteTime(l2 - l1);
					return result;
				}
			};

			@Override
			public void run() {
				ExecutorService es = Executors.newSingleThreadExecutor();
				Future<Object> future = es.submit(callable);
				es.shutdown();
				try {
					if (async)
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								try {
									invokeHandler.before();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					else
						invokeHandler.before();
					final Object result = future.get(timeout, unit);
					if (async)
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								try {
									invokeHandler.success(result);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					else
						invokeHandler.success(result);
				} catch (final TimeoutException et) {
					es.shutdownNow();
					et.printStackTrace();
					if (async)
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								invokeHandler.failure(et);
							}
						});
					else
						invokeHandler.failure(et);
				} catch (final Exception e) {
					e.printStackTrace();
					if (async)
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								invokeHandler.failure(e);
							}
						});
					else
						invokeHandler.failure(e);
				} finally {
					if (async)
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								invokeHandler.after();
							}
						});
					else
						invokeHandler.after();
				}
			}
		};
		new Thread(target).start();
	}

	/**
	 * get sub componenets of container which matches regular expression
	 * @param container
	 * @param nameRegex
	 * @return
	 */
	public static List<Component> getComponentsByName(Container container, String nameRegex) {
		List<Component> compList = new ArrayList<Component>();
		Component[] components = container.getComponents();
		for (Component comp : components) {
			if (comp.getName() != null && comp.getName().matches(nameRegex))
				compList.add(comp);
		}
		return compList;
	}

	/**
	 * remove sub componenets of container which matches regular expression
	 * @param parent
	 * @param nameRegex
	 */
	public static void removeComponentsByName(Container parent, String nameRegex) {
		Component[] components = parent.getComponents();
		for (Component comp : components) {
			if (comp.getName() != null && comp.getName().matches(nameRegex))
				parent.remove(comp);
		}
	}

	public static void removeTabByTitle(JTabbedPane itemPane, String title) {
		for (int i = 0; i < itemPane.getTabCount(); i++) {
			if (itemPane.getTitleAt(i).equals(title)) {
				itemPane.removeTabAt(i);
				break;
			}
		}
	}

	public static void addLineComponents(JPanel panel, List<Component[]> lineList, GridBagConstraints gbc,
			int leftInset, int midInset, int rightInset) {
		for (Component[] line : lineList)
			addLineComponents(panel, line, gbc, leftInset, midInset, rightInset);
	}

	/**
	 * Add one line components to panel use GridBagLayout
	 * @param panel
	 * @param line
	 * @param gbc
	 * @param leftInset
	 * @param midInset
	 * @param rightInset
	 */
	public static void addLineComponents(JPanel panel, Component[] line, GridBagConstraints gbc, int leftInset,
			int midInset, int rightInset) {
		Component leftComp = line[0];
		Component rightComp = line[line.length - 1];
		// add only one component
		if (line.length == 1) {
			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.insets.left = leftInset;
			gbc.insets.right = rightInset;
			panel.add(leftComp, gbc);
		} else {
			gbc.weightx = 1.0;
			gbc.gridwidth = 1;
			gbc.insets.left = leftInset;
			gbc.insets.right = midInset;
			panel.add(leftComp, gbc);
			// add middle components
			for (int i = 1; i < line.length - 1; i++) {
				gbc.weightx = 1.0;
				gbc.gridwidth = 1;
				gbc.insets.left = 0;
				gbc.insets.right = midInset;
				Component midComp = line[i];
				panel.add(midComp, gbc);
			}
			// add right component
			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.insets.left = 0;
			gbc.insets.right = rightInset;
			panel.add(rightComp, gbc);
		}
	}

	public static void setComponentsEnabled(Container c, boolean flag) {
		setComponentsEnabled(c, flag, false);
	}

	public static void setComponentsEnabled(Container c, boolean flag, boolean recursive) {
		Component[] components = c.getComponents();
		for (Component comp : components) {
			if (comp instanceof AbstractButton || comp instanceof JTextComponent || comp instanceof JComboBox
				|| comp instanceof JTabbedPane)
				comp.setEnabled(flag);
			if (recursive && comp instanceof Container)
				setComponentsEnabled((Container) comp, flag, recursive);
		}
	}

	public static Frame getParentFrame(Component comp) {
		Container c = comp.getParent();
		while (c != null) {
			if (c instanceof Frame)
				return (Frame) c;
			c = c.getParent();
		}
		return null;
	}

	/**
	 * get selected button from ButtonGroup
	 * @param bg
	 * @return
	 */
	public static AbstractButton getSelectedButton(ButtonGroup bg) {
		Enumeration<AbstractButton> elements = bg.getElements();
		while (elements.hasMoreElements()) {
			AbstractButton button = elements.nextElement();
			if (button.isSelected())
				return button;
		}
		return null;
	}

}
