package com.hannah.gui.password;

import com.hannah.common.util.BaseCoder;
import com.hannah.swing.component.panel.StepPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;

public class VerificationPanel extends StepPanel {

	private static final long serialVersionUID = -8984563225491840434L;

	private final String vKey = "yR6uV/p/bsLGfOQVroKMiQ==\r\n";

	private JTextField vkTf;

	@Override
	public void loadInterface() {
		vkTf = new JTextField(20);
		vkTf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nextButton.doClick();
			}
		});

		JPanel panel = new JPanel();
		panel.add(new JLabel("在这个特殊的日子："));
		panel.add(vkTf);

		this.setLayout(new BorderLayout());
		this.add(panel, BorderLayout.CENTER);
	}

	@Override
	public String getStepTitle() {
		return "Secure Verification";
	}

	@Override
	public boolean finish() {
		try {
			byte[] bytes = BaseCoder.encryptMD5(vkTf.getText().getBytes());
			String key = BaseCoder.encodeBASE64(bytes);
			if (!vKey.equals(key)) {
				JOptionPane.showMessageDialog(this, "The verification is incorrect!", "Secure Verification",
						JOptionPane.ERROR_MESSAGE);
				return false;
			} else {
				setNextStepObject(vkTf.getText());
				return true;
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
