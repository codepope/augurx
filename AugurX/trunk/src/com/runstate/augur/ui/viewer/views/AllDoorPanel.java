/**
 * AllDoorPanel.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.views;
import com.runstate.augur.ui.viewer.*;

import com.runstate.augur.ui.doors.DoorTableAdapter;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class AllDoorPanel extends JPanel
{
	MessageStrip alldoorstatus;
	JTable doortable;
	
	public AllDoorPanel()
	{
		super();
		
		setLayout(new BorderLayout());
	
		alldoorstatus=new MessageStrip();
		doortable=new JTable(new DoorTableAdapter());
		
		add(BorderLayout.NORTH,alldoorstatus);
		add(BorderLayout.CENTER,new JScrollPane(doortable));
	}
	
}

