/**
 * FindDialog.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.textpanes;

import javax.swing.*;

import com.runstate.augur.ui.textpanes.HTMLPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

public class FindDialog extends JDialog
{
	JTextField target=new JTextField();
	FindDialogListener parent;
	
	Action find_action=new AbstractAction("Find")
	{
		
		
		public void actionPerformed(ActionEvent e)
		{
			FindDialog.this.find();
		}
		
	
	
	};
	
	public FindDialog(FindDialogListener parent)
	{
		super();
		this.parent=parent;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(BorderLayout.CENTER,target);
		
		JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttons.add(new JButton(find_action));
		
		getContentPane().add(BorderLayout.SOUTH,buttons);
		pack();
		setLocationRelativeTo(parent.getComponentForCentre());
	
	}
	
	private void find()
	{
		String text=parent.getPlainText();
		String findtext=target.getText();
		int i=text.indexOf(findtext);
		
		if(i==-1){
			parent.noselect();
			return;
			}
		
		parent.select(i,findtext.length());
	}
}

