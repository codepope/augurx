/**
 * FontPrefs.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.preferences.panels;


import com.runstate.augur.controller.Prefs;
import com.runstate.util.swing.FontButton;
import com.runstate.util.swing.LabelledItemPanel;
import java.awt.BorderLayout;
import javax.swing.JCheckBox;


public class FontPrefs extends ManagedPrefPanel {
	FontButton htmlfont=new FontButton();
	FontButton pathtreefont=new FontButton();
	FontButton threadtreefont=new FontButton();
	FontButton composefont=new FontButton();
	FontButton sshconsole=new FontButton();
	
	JCheckBox textalias=new JCheckBox();
	JCheckBox treealias=new JCheckBox();
	
	public FontPrefs() {
		super();
		setLayout(new BorderLayout());
		
		LabelledItemPanel lip=new LabelledItemPanel();
		lip.addItem("HTML View Font",htmlfont);
		lip.addItem("Path Tree Font:",pathtreefont);
		lip.addItem("Thread Tree Font:",threadtreefont);
		lip.addItem("Compose Font:",composefont);
		lip.addItem("Antialias Text:",textalias);
		lip.addItem("Antialias Trees:",treealias);
	
		add(BorderLayout.CENTER,lip);
		
		registerPrefItem(htmlfont,Prefs.UI_HTML_FONT,"SanSerif-PLAIN-11");
		registerPrefItem(pathtreefont,Prefs.UI_PATHTREE_FONT,"Dialog-PLAIN-12");
		registerPrefItem(threadtreefont,Prefs.UI_THREADTREE_FONT,"Dialog-PLAIN-10");
		registerPrefItem(composefont,Prefs.UI_COMPOSE_FONT,"Monospaced-PLAIN-12");
		registerPrefItem(textalias,Prefs.UI_TEXT_ALIAS,"false");
		registerPrefItem(treealias,Prefs.UI_TREE_ALIAS,"false");
	}

}


