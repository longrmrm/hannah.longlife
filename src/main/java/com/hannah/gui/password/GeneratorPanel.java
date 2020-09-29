package com.hannah.gui.password;

import com.hannah.common.file.LiteIni;
import com.hannah.common.util.BaseCoder;
import com.hannah.swing.component.panel.StepPanel;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class GeneratorPanel extends StepPanel {

	private static final long serialVersionUID = -6312125889934461713L;

	private JComboBox siteCb;
	private JComboBox userCb;
	private JPasswordField passwordField;
	private JLabel originalLb;
	private JTextField newPassTf;

	private LiteIni li = new LiteIni();
	private File dataFile;
	private static final String SITE = "site";
	private static final String USER = "user";
	private List<String> siteList;
	private List<String> userList;

	@Override
	public void loadInterface() {
		loadUserData();
		initComboBox();
		initInterface();
	}

	@Override
	public void initFocus() {
		passwordField.requestFocus();
	}

	private void loadUserData() {
		siteList = new ArrayList<String>();
		userList = new ArrayList<String>();
		try {
			dataFile = new File("PasswordGenerator.data");
			if (!dataFile.exists())
				dataFile.createNewFile();
			InputStream input = new FileInputStream(dataFile);
			li.load(input);
			// get site and user history list
			siteList = li.getSectionContext(SITE);
			if (siteList == null) {
				li.addSection(SITE);
				siteList = li.getSectionContext(SITE);
			}
			userList = li.getSectionContext(USER);
			if (userList == null) {
				li.addSection(USER);
				userList = li.getSectionContext(USER);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initComboBox() {
		siteCb = new JComboBox();
		siteCb.setEditable(true);
		for (String site : siteList)
			siteCb.addItem(site);

		userCb = new JComboBox();
		userCb.setEditable(true);
		for (String user : userList)
			userCb.addItem(user);

		siteCb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("comboBoxEdited"))
					userCb.requestFocus();
			}
		});
		userCb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("comboBoxEdited"))
					passwordField.requestFocus();
			}
		});
	}

	private void initInterface() {
		int hgap = 10;
		int vgap = 20;
		JPanel sitePanel = new JPanel(new BorderLayout(hgap, vgap));
		sitePanel.add(new JLabel("           Site:"), BorderLayout.WEST);
		sitePanel.add(siteCb, BorderLayout.CENTER);

		JPanel userPanel = new JPanel(new BorderLayout(hgap, vgap));
		userPanel.add(new JLabel("          User:"), BorderLayout.WEST);
		userPanel.add(userCb, BorderLayout.CENTER);

		JPanel passPanel = new JPanel(new BorderLayout(hgap, vgap));
		passPanel.add(new JLabel("Password:"), BorderLayout.WEST);
		passwordField = new JPasswordField(20);
		passwordField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nextButton.doClick();
			}
		});
		passPanel.add(passwordField, BorderLayout.CENTER);

		originalLb = new JLabel();

		JPanel newPassPanel = new JPanel(new BorderLayout(hgap, vgap));
		newPassPanel.add(new JLabel("New Pass:"), BorderLayout.WEST);
		newPassTf = new JTextField();
		newPassTf.setEditable(false);
		newPassTf.setFont(new Font("Courier New", Font.BOLD, 14));
		newPassTf.setForeground(Color.RED);
		newPassPanel.add(newPassTf, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(5, 1, hgap, vgap));
		panel.add(sitePanel);
		panel.add(userPanel);
		panel.add(passPanel);
		// panel.add(originalLb);
		panel.add(newPassPanel);
		this.add(panel);
	}

	@Override
	public String getStepTitle() {
		return "Create New Password";
	}

	@Override
	public boolean finish() {
		String site = (String) siteCb.getSelectedItem();
		String user = (String) userCb.getSelectedItem();
		String password = new String(passwordField.getPassword());
		if (site.trim().length() == 0 || user.trim().length() == 0 || password.length() == 0) {
			JOptionPane.showMessageDialog(null, "Please enter site, user and password!", "Create New Password",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (!site.matches(".+(\\.com|\\.cn|\\.net|\\.org)")) {
			JOptionPane.showMessageDialog(null, "Site is illegal!", "Create New Password", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		try {
			String original = site + "@" + user + "/" + password;
			originalLb.setText(original);
			String newPassword = createNewPassword(original, stepObject.toString());
			newPassTf.setText(newPassword);
			li.addSectionLine(SITE, site);
			li.addSectionLine(USER, user);
			li.store(new FileOutputStream(dataFile), null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Create new password: MD5 -> HMAC
	 * @param original
	 * @param uKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InvalidKeyException
	 */
	private String createNewPassword(String original, String uKey) throws Exception {
		byte[] bytes = BaseCoder.encryptMD5(original.getBytes());
		SecretKey secretKey = BaseCoder.generateSecretKey(BaseCoder.ALGORITHM_HMAC, 512, uKey.getBytes());
		bytes = BaseCoder.encryptHMAC(bytes, secretKey);
		String newPassword = BaseCoder.encodeBASE64(bytes);
		return newPassword;
	}

}
