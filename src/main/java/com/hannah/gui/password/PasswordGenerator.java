package com.hannah.gui.password;

import com.hannah.swing.component.panel.StepByStepPanel;
import com.hannah.swing.component.panel.StepPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Generate different passwords for different sites to keep password safe
 * @author longrm
 * @date 2012-5-23
 */
public class PasswordGenerator extends JFrame {

	private static final long serialVersionUID = 5594540200276272589L;

	private StepByStepPanel stepsPanel;

	public PasswordGenerator() {
		initStepsPanel();

		this.setTitle("Password Generator");
		this.setLayout(new BorderLayout());
		this.add(stepsPanel, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500, 400);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void initStepsPanel() {
		List<StepPanel> stepPanelList = new ArrayList<StepPanel>();
		stepPanelList.add(new VerificationPanel());
		stepPanelList.add(new GeneratorPanel());
		stepsPanel = new StepByStepPanel(stepPanelList, null);
	}

	public static void main(String[] args) {
		new PasswordGenerator();
	}

}
