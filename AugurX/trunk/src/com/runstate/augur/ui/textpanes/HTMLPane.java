/**
 * HTMLPane.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.textpanes;

import com.runstate.augur.controller.Controller;
import java.lang.String;
import javax.swing.*;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Prefs;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class HTMLPane extends JTextPane {
	
	Font defaultfont;
	
	public HTMLPane() {
		super();
//		setEnabled(false);
		setEditable(false);
		InputMap imap=getInputMap(JComponent.WHEN_FOCUSED);
		imap.put(KeyStroke.getKeyStroke("ENTER"),"none");
		imap.put(KeyStroke.getKeyStroke("BACK_SPACE"),"none");
	}
	
	public void setHTML(String text) {
		
		if(getContentType().equals("text/plain")) {
			setContentType("text/html");
			setFont(defaultfont);
		}
		
                /* Map the characters that are not handled by Java's HTMLEditorKit
                 * current sources can only generate characters that are part of
                 * CP-1252, the HTMLEditorKit only maps those that are in 8859-1
                 * so we need to correct the few that are in CP1252 but not 8859-1
                 * here.
                 *
                 * Don't touch this code unless your editor is set to CP 1252
                 *
                 * References for the HTML codes and the content of CP1252 are
                 * http://www.w3.org/2000/07/8378/xhtml/entities/entities.xml
                 * http://www.microsoft.com/typography/unicode/1252.htm
                 */
                text=text.replaceAll("\\&euro;","€");       // 80
                // 81 - Undefined
                text=text.replaceAll("\\&sbquo;","‚");      // 82
                text=text.replaceAll("\\&fnof;","ƒ");       // 83
                text=text.replaceAll("\\&bdquo;","„");      // 84
                text=text.replaceAll("\\&hellip;","…");     // 85
                text=text.replaceAll("\\&dagger;","†");     // 86
                text=text.replaceAll("\\&Dagger;","‡");     // 87
                text=text.replaceAll("\\&circ;","ˆ");       // 88
                text=text.replaceAll("\\&permil;","‰");     // 89
                text=text.replaceAll("\\&Scaron;","Š");     // 8A
                text=text.replaceAll("\\&lsaquo;","‹");     // 8B
                text=text.replaceAll("\\&OElig;","Œ");      // 8C
                // 8D - Undefined
                // 8E - No HTML escape
                // 8F - Undefined
                // 90 - Undefined
                text=text.replaceAll("\\&lsquo;","‘");      // 91
                text=text.replaceAll("\\&rsquo;","’");      // 92
                text=text.replaceAll("\\&ldquo;","“");      // 93
                text=text.replaceAll("\\&rdquo;","”");      // 94
                text=text.replaceAll("\\&bull;","•");       // 95
                text=text.replaceAll("\\&ndash;","–");      // 96
                text=text.replaceAll("\\&mdash;","—");      // 97
                text=text.replaceAll("\\&tilde;","˜");      // 98
                text=text.replaceAll("\\&trade;","™");      // 99
                text=text.replaceAll("\\&scaron;","š");     // 9A
                text=text.replaceAll("\\&rsaquo;","›");     // 9B
                text=text.replaceAll("\\&oelig;","œ");      // 9C
                // 9D - Undefined
                // 9E - No HTML escape
                text=text.replaceAll("\\&Yuml;","Ÿ");      // 9F
     
		super.setText(text);
	}
	
	public void setPlain(String text) {
		if(getContentType().equals("text/html")) {
			setContentType("text/plain");
			setFont(new Font("Courier",Font.PLAIN,11));
		}
		super.setText(text);
		
	}
	
	public void setText(String text) {
		super.setText(text);
		System.out.println("Alert:SetText being used");
	}
	
	
	protected void processKeyEvent(KeyEvent e)
	{
		// STOP THE FREAKING BEEPING... NO BEEPING.... WE DON'T NEED NO STEENKING BEEPING

		if(e.getKeyCode()==0) { e.consume(); return; }

		super.processKeyEvent(e);
	}
	
	public void paintComponent(Graphics g) {
		if(Controller.getProfile().getBool(Prefs.UI_TEXT_ALIAS,false)) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
								RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
								RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			super.paintComponent(g2);
		}
		else {
			super.paintComponent(g);
		}
	}
	
	public class MyDefaultAction extends AbstractAction {
		Action defAction;
		public MyDefaultAction(Action a) {
			super("My Default Action");
			defAction = a;
		}
		public void actionPerformed(ActionEvent e) {
			System.out.println("Doing nuttin boss");
		}
	}
	
}


