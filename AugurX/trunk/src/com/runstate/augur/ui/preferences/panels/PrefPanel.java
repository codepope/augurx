/**
 * PrefPanel.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.preferences.panels;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Profile;
import com.runstate.augur.ui.preferences.PreferencesUI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class PrefPanel extends JPanel
{
	Profile profile=Controller.getProfile();
	
	public PrefPanel()
	{
		super(new BorderLayout());
		setPreferredSize(new Dimension(512,256));
		setBorder(BorderFactory.createEtchedBorder());
	}
	
	PreferencesUI prefseditor;
	
	public void setPrefsEditor(PreferencesUI p0) {
		prefseditor=p0;
	}

	public void fireHasChanged()
	{
		prefseditor.hasChanged();
	}
	
	public void fireNoChange()
	{
		prefseditor.noChanges();
	}
	
	public boolean hasChanged()
	{
		return false;
	}
	
	public boolean saveChanged()
	{
		return true;
	}
	
	public void discardChanges()
	{
	}
	
	
	
}

