/**
 * JMutedTextPane.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.textpanes;

import com.runstate.augur.controller.Controller;
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


