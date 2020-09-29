package com.hannah.swing.component.panel;

import com.hannah.swing.component.button.ImageButton;
import com.hannah.swing.util.UiUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author longrm
 * @date 2012-5-23
 */
public class StepByStepPanel extends JPanel {

	private static final long serialVersionUID = -607947967284360991L;

	private List<StepPanel> stepPanelList;

	private JPanel stepInstructPanel;
	private int currentStep = 0;
	private StepPanel currentStepPanel;
	private JPanel buttonPanel;

	private JButton previousButton;
	private JButton nextButton;
	private boolean previousVisible = true;

	public StepByStepPanel(List<StepPanel> stepPanelList, Object fisrtStepObject) {
		this.stepPanelList = stepPanelList;

		initStepInstructPanel();
		initButtonPanel();
		nextStep();

		this.setLayout(new BorderLayout());
		this.add(stepInstructPanel, BorderLayout.NORTH);
		this.add(currentStepPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.validate();
	}

	private void initStepInstructPanel() {
		stepInstructPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
		for (int i = 0; i < stepPanelList.size(); i++) {
			if (i > 0)
				stepInstructPanel.add(new JLabel("→"));

			StepPanel stepPanel = stepPanelList.get(i);
			JLabel label = new JLabel("Step " + (i + 1) + " : " + stepPanel.getStepTitle());
			label.setName("STEP" + (i + 1) + "_LABEL");
			label.setBorder(new TitledBorder(""));
			label.setOpaque(true);

			Dimension d = label.getPreferredSize();
			d.setSize(d.getWidth() + 10, d.getHeight() + 10);
			label.setPreferredSize(d);
			stepInstructPanel.add(label);
		}
	}

	private void initButtonPanel() {
		previousButton = new ImageButton("Previous");
		nextButton = new ImageButton("Next");

		ActionListener l = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == previousButton) {
					previousStep();
				} else if (e.getSource() == nextButton) {
					boolean finish = currentStepPanel.finish();
					if (finish && currentStep != stepPanelList.size())
						nextStep();
				}
			}
		};
		previousButton.addActionListener(l);
		nextButton.addActionListener(l);

		buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 15));
		buttonPanel.add(previousButton);
		buttonPanel.add(nextButton);
	}

	public boolean isPreviousVisible() {
		return previousVisible;
	}

	public void setPreviousVisible(boolean previousVisible) {
		this.previousVisible = previousVisible;
		previousButton.setVisible(previousVisible);
	}

	private void previousStep() {
		currentStep--;
		Container parent = currentStepPanel.getParent();
		parent.remove(currentStepPanel);
		currentStepPanel = this.stepPanelList.get(currentStep - 1);
		parent.add(currentStepPanel);
		setStepInterface();
	}

	private void nextStep() {
		Object nextStepObject = currentStepPanel == null ? null : currentStepPanel
				.getNextStepObject();
		currentStep++;
		// init current stepPanel
		Container parent = null;
		if (currentStepPanel != null) {
			parent = currentStepPanel.getParent();
			parent.remove(currentStepPanel);
		}
		currentStepPanel = this.stepPanelList.get(currentStep - 1);
		currentStepPanel.setBorder(new TitledBorder(""));
		currentStepPanel.setStepObject(nextStepObject);
		currentStepPanel.setNextButton(nextButton);
		currentStepPanel.reloadInterface();
		if (parent != null)
			parent.add(currentStepPanel);
		setStepInterface();
		currentStepPanel.initFocus();
	}

	private void setStepInterface() {
		// make current step label clear
		List<Component> comps = UiUtil.getComponentsByName(stepInstructPanel, "STEP.*_LABEL");
		for (Component comp : comps) {
			JLabel label = (JLabel) comp;
			if (label.getName().equals("STEP" + currentStep + "_LABEL")) {
				label.setBackground(Color.BLUE);
				label.setForeground(Color.WHITE);
			} else {
				label.setBackground(Color.LIGHT_GRAY);
				label.setForeground(Color.BLACK);
			}
		}
		// set button status
		previousButton.setVisible(previousVisible);
		previousButton.setEnabled(currentStep > 1);
		if (currentStep != stepPanelList.size())
			nextButton.setText("Next");
		else
			nextButton.setText("Finish");
		// this.validate();
		this.updateUI();
	}

}
