/**
 * DoorListCellRenderer.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.doors;
import javax.swing.*;

import com.runstate.augur.controller.Door;
import com.runstate.util.swing.ImageCreator;
import java.awt.Color;
import java.awt.Component;

class DoorListCellRenderer extends DefaultListCellRenderer
{
	ImageIcon active=ImageCreator.getSingleLetterIcon('A',false,Color.GREEN);
	ImageIcon busy=ImageCreator.getSingleLetterIcon('B',false,Color.RED);
	ImageIcon inactive=ImageCreator.getSingleLetterIcon(' ',false,Color.WHITE);
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		JLabel label =
			(JLabel)super.getListCellRendererComponent(list,
													   value,
													   index,
													   isSelected,
													   cellHasFocus);
		if (value instanceof Door)
		{
			Door door = (Door)value;
			if(door.isConnected())
			{
				if(door.isBusy())
				{
					label.setIcon(busy);
				}
				else
				{
					label.setIcon(active);
				}
			}
			else
			{
				label.setIcon(inactive);
			}
			
			label.setText(door.getDoorname());
		}
		
		return(label);
	}
	
}
