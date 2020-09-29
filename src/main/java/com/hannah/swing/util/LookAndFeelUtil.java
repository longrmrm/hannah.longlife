package com.hannah.swing.util;

import com.hannah.common.util.ClassUtil;
import com.hannah.swing.component.dialog.ElementTreeSelectDialog;
import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.synth.SynthLookAndFeel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author longrm
 * @date 2013-4-19
 */
public class LookAndFeelUtil {

	public static List<Class<?>> getSwingLookAndFeels() {
		List<Class<?>> feels = new ArrayList<Class<?>>();
		// javax.swing.plaf
		// feels.add(BasicLookAndFeel.class);
		feels.add(MetalLookAndFeel.class);
		// feelList.add(MultiLookAndFeel.class);
		feels.add(SynthLookAndFeel.class);
		// com.sun.java.swing.plaf
		feels.add(MotifLookAndFeel.class);
		feels.add(NimbusLookAndFeel.class);
		feels.add(WindowsClassicLookAndFeel.class);
		feels.add(WindowsLookAndFeel.class);
		// feelList.add(GTKLookAndFeel.class);
		// feelList.add(SynthLookAndFeel.class);
		return feels;
	}

	public static Class<?> showSwingLookAndFeelDialog() {
		List<Class<?>> feels = getSwingLookAndFeels();
		ElementTreeSelectDialog dialog = new ElementTreeSelectDialog("Swing LookAndFeel", feels);
		dialog.pack();
		dialog.setVisible(true);
		return dialog.isOk() ? (Class<?>) dialog.getSelectedElement() : null;
	}

	public static List<Class<?>> getSubstanceLookAndFeels() {
		List<Class<?>> feels = new ArrayList<Class<?>>();
		Set<Class<?>> subClasses = ClassUtil.getClasses("org.jvnet.substance");
		// subClasses.addAll(ClassUtil.getClasses("org.jvnet.substance.skin"));
		for (Class<?> subClass : subClasses) {
			if (subClass.isAnonymousClass() || subClass.isMemberClass())
				continue;
			if (subClass.getName().endsWith("LookAndFeel"))
				feels.add(subClass);
		}
		return feels;
	}

	public static Class<?> showSubstanceLookAndFeelDialog() {
		List<Class<?>> feels = getSubstanceLookAndFeels();
		ElementTreeSelectDialog dialog = new ElementTreeSelectDialog("Substance LookAndFeel", feels);
		dialog.pack();
		dialog.setVisible(true);
		return dialog.isOk() ? (Class<?>) dialog.getSelectedElement() : null;
	}

}
