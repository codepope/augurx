/**
 * WebServerPrefs.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.preferences.panels;

import com.runstate.augur.controller.Prefs;
import com.runstate.util.swing.LabelledItemPanel;
import java.awt.BorderLayout;
import javax.swing.JCheckBox;

public class WebServerPrefs extends ManagedPrefPanel
{
	JCheckBox webserveron=new JCheckBox();

	
	public WebServerPrefs()
	{
		super();
		setLayout(new BorderLayout());
		LabelledItemPanel lip=new LabelledItemPanel();
		lip.addItem("Web Server",webserveron);

		add(BorderLayout.CENTER,lip);
		
		registerPrefItem(webserveron,Prefs.A_WEB_ON,"false");
	
	}
		
	public boolean postSaveAction()
	{
		// Now we restart
		return true;
	}

}

