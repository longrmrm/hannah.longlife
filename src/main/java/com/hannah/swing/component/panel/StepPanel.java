package com.hannah.swing.component.panel;

import javax.swing.*;

/**
 * @author longrm
 * @date 2012-5-23
 */
public abstract class StepPanel extends JPanel {

	private static final long serialVersionUID = 3811308843879183239L;

	protected Object stepObject;
	protected Object nextStepObject;	// to store next step object
	protected JButton nextButton;

	public abstract void loadInterface();
	
	public void reloadInterface() {
		super.removeAll();
		loadInterface();
	}
	
	public void initFocus() {
	}

	public abstract String getStepTitle();

	public Object getStepObject() {
		return stepObject;
	}

	public void setStepObject(Object stepObject) {
		this.stepObject = stepObject;
	}

	public Object getNextStepObject() {
		return nextStepObject;
	}

	public void setNextStepObject(Object nextStepObject) {
		this.nextStepObject = nextStepObject;
	}

	public void setNextButton(JButton nextButton) {
		this.nextButton = nextButton;
	}

	public abstract boolean finish();

}
