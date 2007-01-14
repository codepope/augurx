/**
 * GalleryDBPrefs.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.preferences.panels;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Prefs;
import com.runstate.util.swing.LabelledItemPanel;
import java.awt.BorderLayout;
import javax.swing.JCheckBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class GalleryDBPrefs extends ManagedPrefPanel
{
	JTextField dburl=new JTextField();
	JTextField username=new JTextField();
	JPasswordField password=new JPasswordField();
	JCheckBox shutdownremote=new JCheckBox();
	JCheckBox compactdb=new JCheckBox();
	
	public GalleryDBPrefs()
	{
		super();
		setLayout(new BorderLayout());
		LabelledItemPanel lip=new LabelledItemPanel();
		lip.addItem("<HTML><BODY><P ALIGN='RIGHT'>Database<BR>URL:",dburl);
		lip.addItem("<HTML><BODY><P ALIGN='RIGHT'>Database<BR>User Name:",username);
		lip.addItem("<HTML><BODY><P ALIGN='RIGHT'>Database<BR>Password:",password);
		lip.addItem("<HTML><BODY><P ALIGN='RIGHT'>Compact database<BR>at shutdown",compactdb);
		lip.addItem("<HTML><BODY><P ALIGN='RIGHT'>Offer remote server<BR>shutdown on exit",shutdownremote);
		add(BorderLayout.CENTER,lip);
		
		registerPrefItem(dburl,Prefs.G_DBURL,"");
		registerPrefItem(username,Prefs.G_DBUSER,"sa");
		registerPrefItem(password,Prefs.G_DBPASS,"");
		registerPrefItem(shutdownremote,Prefs.G_HSQL_OFFERSHUTDOWN,"false");
		registerPrefItem(compactdb,Prefs.G_HSQL_COMPACT,"false");
	}
		
	public boolean postSaveAction()
	{
		// Now we restart
		Controller.getController().restart();
		
		return true;
	}

}

