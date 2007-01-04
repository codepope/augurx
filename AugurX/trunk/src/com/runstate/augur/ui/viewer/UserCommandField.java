/**
 * UserCommandField.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer;

import java.awt.event.*;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;

public class UserCommandField extends JLabel implements FocusListener,KeyListener,MouseListener
{
	JTextField editablefield;
	JFrame fieldwindow;
	UserCommandFieldUser user;
	
	public UserCommandField(UserCommandFieldUser user)
	{
		super("");
		this.user=user;
		editablefield=new JTextField("");
		fieldwindow=new JFrame();
		fieldwindow.setUndecorated(true);
		fieldwindow.getContentPane().add(editablefield,BorderLayout.CENTER);
		editablefield.addFocusListener(this);
		editablefield.addKeyListener(this);
		addMouseListener(this);
	}
	
	public void takeFocus()
	{
		editablefield.setText(getText());
		fieldwindow.setSize(getSize());
		editablefield.setSize(getSize());
		Point p=new Point(getLocation());
		SwingUtilities.convertPointToScreen(p,this);
		fieldwindow.setLocation(p);
		fieldwindow.setVisible(true);
		fieldwindow.requestFocus();
		editablefield.requestFocusInWindow();
	
		editablefield.setCaretPosition(editablefield.getText().length());
			editablefield.selectAll();
	}
	
	public void setFont(Font f)
	{
		super.setFont(f);
		if(editablefield!=null) editablefield.setFont(f);
	}
	
	/**
	 * Invoked when the mouse button has been clicked (pressed
	 * and released) on a component.
	 */
	public void mouseClicked(MouseEvent e) {
		takeFocus();
	}
	
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
	public void mousePressed(MouseEvent e) {
		// TODO
	}
	
	/**
	 * Invoked when a mouse button has been released on a component.
	 */
	public void mouseReleased(MouseEvent e) {
		// TODO
	}
	
	/**
	 * Invoked when the mouse enters a component.
	 */
	public void mouseEntered(MouseEvent e) {
		// TODO
	}
	
	/**
	 * Invoked when the mouse exits a component.
	 */
	public void mouseExited(MouseEvent e) {
		// TODO
	}
	
	
	
	public void focusGained(FocusEvent e)
	{
	}
	
	public void focusLost(FocusEvent e)
	{
		fieldwindow.setVisible(false);
	}
	
	public void keyTyped(KeyEvent e)
	{
		// TODO
	}
	
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode()==KeyEvent.VK_ENTER)
		{
			System.out.println("Enter pressed");
			setText(editablefield.getText());
			fieldwindow.setVisible(false);
			user.userCommandEntered(getText());
			e.consume();
		}
		else if(e.getKeyCode()==KeyEvent.VK_ESCAPE)
		{
			System.out.println("Escape pressed");
			fieldwindow.setVisible(false);
			e.consume();
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
		// TODO
	}
	
	
	

}

