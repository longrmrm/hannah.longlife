package com.hannah.swing.component;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author longrm
 * @date 2012-5-31
 */
public abstract class ButtonTextField extends JTextField {

	private static final long serialVersionUID = -6359125331512517315L;

	public List selectedValues;

	public JButton chooseButton = new JButton(" ") {
		
		private static final long serialVersionUID = -7456400999981871987L;

		{
			this.setCursor(Cursor.getDefaultCursor());
			this.setFocusable(false);
		}

		@Override
		public Insets getInsets() {
			return new Insets(0, 0, 0, 0);
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension size = ButtonTextField.super.getPreferredSize();
			size.height -= 6;
			size.width = size.height;
			return size;
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			int width = 3;
			int height = 3;

			Rectangle rect = g.getClipBounds();
			int x = rect.width / 4;
			int y = rect.height / 2;

			for (int i = 0; i < 3;) {
				g.fillOval(++i * x, y, width, height);
			}
		}

	};

	public ButtonTextField(Document doc, String text, int columns) {
		super(doc, text, columns);
		initButtonTextField();
	}

	public ButtonTextField(int columns) {
		super(columns);
		initButtonTextField();
	}
	
	public ButtonTextField(String text, int columns) {
		super(text, columns);
		initButtonTextField();
	}
	
	public ButtonTextField(String text) {
		super(text);
		initButtonTextField();
	}
	
	public ButtonTextField() {
		super();
		initButtonTextField();
	}

	@Override
	public void setEnabled(boolean enabled) {
		// super.setEnabled(enabled);
		chooseButton.setEnabled(enabled);
		super.setEnabled(enabled);
		chooseButton.setVisible(enabled);
	}

	protected void initButtonTextField() {
		chooseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ButtonTextField.this.handleClick(ButtonTextField.this);
			}
		});
		this.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		this.add(chooseButton);
	}

	public abstract void handleClick(ButtonTextField buttonTextField);

	@Override
	public void addNotify() {
		super.addNotify();
//		if (chooseButton != null)
//			add(chooseButton);
	}

	public List getSelectedValues() {
		return selectedValues;
	}

	public void setSelectedValues(List values) {
		this.selectedValues = values;
		if (values != null && !values.isEmpty()) {
			StringBuffer vString = new StringBuffer("");
			for (Object o : values) {
				vString.append(o.toString() + ";");
			}
			this.setText(vString.toString().substring(0, vString.toString().lastIndexOf(";")));
			this.setToolTipText(vString.toString()
					.substring(0, vString.toString().lastIndexOf(";")));
		} else {
			this.setText("");
			this.setToolTipText(null);
		}
	}

}