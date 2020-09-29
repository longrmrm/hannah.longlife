package com.hannah.swing.util;

import com.hannah.common.util.ImageUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author longrm
 * @date 2012-4-27
 */
public abstract class AbstractInvokeHandler<T> implements InvokeHandler<T> {

	private static ImageIcon loadingIcon = ImageUtil.getImageIcon("/loading.gif");

	private JComponent loadingComp;

	private long executeTime = -1L;

	public static void setLoadingIcon(ImageIcon imageIcon) {
		if (imageIcon == null)
			throw new IllegalArgumentException("image icon can't be null");
		else
			loadingIcon = imageIcon;
	}

	@Override
	public void before() {
		Container c = getParentOfLoadingComponent();
		if (c != null)
			installLoadingComponent(c);
	}

	protected Container getParentOfLoadingComponent() {
		Window window = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
		JLayeredPane layeredPane = UiUtil.getLayeredPane(window);
		return layeredPane;
	}

	protected void installLoadingComponent(Container container) {
		loadingComp = new JLabel(loadingIcon);
		loadingComp.setBounds(new Rectangle(100, 100, 100, 100));
		container.add(loadingComp);
		UiUtil.setLocationRelativeTo(loadingComp, container);
		if (container instanceof JComponent)
			((JComponent) container).revalidate();
		container.repaint();
	}

	@Override
	public void after() {
		unInstallLoadingComponent(loadingComp.getParent());
	}

	protected void unInstallLoadingComponent(Container container) {
		if (container == null)
			return;
		else {
			container.remove(loadingComp);
			container.invalidate();
			container.repaint();
			return;
		}
	}

	@Override
	public void failure(Exception exception) {
		UiUtil.showStackTraceDialog(exception, "Invoke Error");
	}

	public long getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(long executeTime) {
		this.executeTime = executeTime;
	}

}
