/**
 * TwixPrefs.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.preferences.panels;



import com.runstate.augur.controller.Prefs;
import com.runstate.util.swing.LabelledItemPanel;
import java.awt.BorderLayout;
import javax.swing.JCheckBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class TwixPrefs extends ManagedPrefPanel {
	JTextField username=new JTextField();
	JPasswordField password=new JPasswordField();
	JCheckBox alwaysrestore=new JCheckBox();
	JCheckBox alwayson=new JCheckBox();
	JTextField msgsize=new JTextField();
	JTextField timeout=new JTextField();
	
	String username_val;
	String password_val;
	
	public TwixPrefs() {
		super();
		setLayout(new BorderLayout());
		LabelledItemPanel lip=new LabelledItemPanel();
		lip.addItem("Twix User Name:",username);
		lip.addItem("Password:",password);
		lip.addItem("Always Restore",alwaysrestore);
		lip.addItem("Always On Mode",alwayson);
		lip.addItem("Max Message Size",msgsize);
		add(BorderLayout.CENTER,lip);
		
		registerPrefItem(username,Prefs.M_T_USERNAME,"");
		registerPrefItem(password,Prefs.M_T_PASSWORD,"");
		registerPrefItem(alwaysrestore,Prefs.M_T_ALWAYSRESTORE,"false");
		registerPrefItem(alwayson,Prefs.M_T_ALWAYSON,"false");
		registerPrefItem(msgsize,Prefs.M_T_MSGSIZE,"32767");
		//registerPrefItem(timeout,Prefs.M_T_TIMEOUT,"120");

	}

}

