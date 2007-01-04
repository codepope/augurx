/**
 * DebugPrefs.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.preferences.panels;




import com.runstate.augur.controller.Prefs;
import com.runstate.util.swing.LabelledItemPanel;
import java.awt.BorderLayout;
import javax.swing.JCheckBox;

public class DebugPrefs extends ManagedPrefPanel {

	JCheckBox sshdebug=new JCheckBox();
	JCheckBox ymodemdebug=new JCheckBox();
	
	String username_val;
	String password_val;
	
	public DebugPrefs() {
		super();
		setLayout(new BorderLayout());
		LabelledItemPanel lip=new LabelledItemPanel();

		lip.addItem("SSH Debug:",sshdebug);
		lip.addItem("YModem Debug",ymodemdebug);
		
		add(BorderLayout.CENTER,lip);
		
		registerPrefItem(sshdebug,Prefs.DEBUG_SSH,"false");
		registerPrefItem(ymodemdebug,Prefs.DEBUG_YMODEM,"false");
	}

}

