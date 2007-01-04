/**
 * GUIPrefs.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.preferences.panels;

import java.awt.BorderLayout;
import javax.swing.JLabel;

public class GUIPrefs extends PrefPanel
{
	public GUIPrefs()
	{
		super();
		setLayout(new BorderLayout());
		JLabel jl=new JLabel("GUI preferences");
		add(BorderLayout.CENTER,jl);
	}
}

