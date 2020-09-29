package com.hannah.swing.component.panel;

import com.hannah.swing.util.AbstractInvokeHandler;
import com.hannah.swing.util.UiUtil;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import java.awt.*;

public class HtmlPagePanel extends JPanel {

	private static final long serialVersionUID = -7822182273002246020L;

	private JEditorPane htmlPane;
	private JToolBar statusBar = new JToolBar();
	private JLabel statusLb = new JLabel();

	public HtmlPagePanel() {
		this("text/html");
		HyperlinkListener hl = new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					JEditorPane pane = (JEditorPane) e.getSource();
					if (e instanceof HTMLFrameHyperlinkEvent) {
						HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
						HTMLDocument doc = (HTMLDocument) pane.getDocument();
						doc.processHTMLFrameHyperlinkEvent(evt);
					} else {
						setPage(e.getURL().toString());
					}
				}
			}
		};
		setHyperlinkListener(hl);
	}

	public HtmlPagePanel(String type) {
		htmlPane = new JEditorPane(type, null);
		htmlPane.setEditable(false);

		statusBar.setFloatable(false);
		statusBar.setVisible(false);
		statusBar.add(statusLb);

		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(htmlPane), BorderLayout.CENTER);
		this.add(statusBar, BorderLayout.SOUTH);
	}

	public HtmlPagePanel(String type, HyperlinkListener hl) {
		this(type);
		setHyperlinkListener(hl);
	}

	public void setPage(final String url) {
		UiUtil.asyncInvoke(new AbstractInvokeHandler<Object>() {

			@Override
			protected void installLoadingComponent(Container container) {
				super.installLoadingComponent(htmlPane.getParent());
			}

			@Override
			protected void unInstallLoadingComponent(Container container) {
				super.unInstallLoadingComponent(container);
				container.add(htmlPane);
			}

			@Override
			public Object execute() throws Exception {
				Thread.sleep(300);
				htmlPane.setPage(url);
				return null;
			}

			@Override
			public void success(Object result) {
				statusLb.setText(url);
			}
		});
	}

	public JEditorPane getHtmlPane() {
		return htmlPane;
	}

	public void setHyperlinkListener(HyperlinkListener hl) {
		for (HyperlinkListener oldHl : htmlPane.getHyperlinkListeners())
			htmlPane.removeHyperlinkListener(oldHl);
		if (hl != null)
			htmlPane.addHyperlinkListener(hl);
	}

	public void setStatusVisible(boolean flag) {
		statusBar.setVisible(flag);
	}

}
