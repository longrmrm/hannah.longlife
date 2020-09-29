package com.hannah.swing.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;

public class ButtonComponentGroup {

	private Map<AbstractButton, List<Component>> buttonMap = new HashMap<AbstractButton, List<Component>>();

	public ButtonComponentGroup() {
	}

	public void add(AbstractButton button) {
		add(button, null);
	}

	public void add(AbstractButton button, Component comp) {
		if (buttonMap.containsKey(button)) {
			List<Component> compList = buttonMap.get(button);
			compList.add(comp);
		} else {
			List<Component> compList = new ArrayList<Component>();
			compList.add(comp);
			buttonMap.put(button, compList);

			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					doButtonClicked((AbstractButton) e.getSource());
				}
			});
		}
	}

	private void doButtonClicked(AbstractButton button) {
		Iterator<AbstractButton> it = buttonMap.keySet().iterator();
		while (it.hasNext()) {
			AbstractButton tmpButton = it.next();
			setComponentStatus(tmpButton, tmpButton == button);
		}
	}

	private void setComponentStatus(AbstractButton button, boolean isSelected) {
		button.setSelected(isSelected);
		List<Component> compList = buttonMap.get(button);
		for (Component comp : compList) {
			if (comp != null)
				comp.setEnabled(isSelected);
		}
	}

	public void remove(AbstractButton button) {
		buttonMap.remove(button);
	}

	public AbstractButton getSelectedButton() {
		Iterator<AbstractButton> it = buttonMap.keySet().iterator();
		while (it.hasNext()) {
			AbstractButton tmpButton = it.next();
			if (tmpButton.isSelected())
				return tmpButton;
		}
		return null;
	}

	public void clear() {
		buttonMap.clear();
	}

	public Map<AbstractButton, List<Component>> getButtonMap() {
		return buttonMap;
	}

	public void setButtonMap(Map<AbstractButton, List<Component>> buttonMap) {
		this.buttonMap = buttonMap;
	}

}
