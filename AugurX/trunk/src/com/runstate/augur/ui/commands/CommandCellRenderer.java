/**
 * CommandCellRenderer.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.commands;

import javax.swing.*;

import com.runstate.augur.gallery.commands.Command;
import java.awt.Component;
import java.awt.Font;
import javax.swing.table.DefaultTableCellRenderer;

public class CommandCellRenderer extends DefaultTableCellRenderer {
	JButton button=new JButton("E");

	
	public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus, int row,int col) {
		Component comp=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,col);
		
		if(value instanceof Command) {
			
			Command c=(Command)value;
			button.setEnabled(c.isEditable());
//			button.setFont(new Font("Dialog",Font.PLAIN,10));
			if(c.isEditable()) return button;
			else {
				setText("");
				return comp;
			}
		}
		
		return comp;
	}
}

