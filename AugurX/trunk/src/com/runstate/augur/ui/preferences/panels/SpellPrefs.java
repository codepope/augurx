/**
 * SpellPrefs.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.preferences.panels;

import com.runstate.augur.controller.Prefs;
import com.runstate.util.swing.LabelledItemPanel;
import java.awt.BorderLayout;
import javax.swing.JCheckBox;


public class SpellPrefs extends ManagedPrefPanel {

	JCheckBox forcespell=new JCheckBox();
	
	public SpellPrefs() {
		super();
		setLayout(new BorderLayout());
			
		LabelledItemPanel lip=new LabelledItemPanel();
		lip.addItem("Louis Mode (force spell checking):",forcespell);
	
		add(BorderLayout.CENTER,lip);
		
		registerPrefItem(forcespell,Prefs.UI_SPELL_FORCE,"false");

	}

}

