/**
 * CixPrefs.java
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

public class CixPrefs extends ManagedPrefPanel {
	JTextField username=new JTextField();
	JPasswordField password=new JPasswordField();
	JCheckBox alwaysrestore=new JCheckBox();
	JCheckBox alwayson=new JCheckBox();
	JTextField msgsize=new JTextField();
	
	String username_val;
	String password_val;
	
	public CixPrefs() {
		super();
		setLayout(new BorderLayout());
		LabelledItemPanel lip=new LabelledItemPanel();
		lip.addItem("Cix User Name:",username);
		lip.addItem("Password:",password);
		lip.addItem("Always Restore",alwaysrestore);
		lip.addItem("Always On Mode",alwayson);
		lip.addItem("Max Message Size",msgsize);
		add(BorderLayout.CENTER,lip);
		
		registerPrefItem(username,Prefs.M_C_USERNAME,"");
		registerPrefItem(password,Prefs.M_C_PASSWORD,"");
		registerPrefItem(alwaysrestore,Prefs.M_C_ALWAYSRESTORE,"false");
		registerPrefItem(alwayson,Prefs.M_C_ALWAYSON,"false");
		registerPrefItem(msgsize,Prefs.M_C_MSGSIZE,"32767");
	}

}

