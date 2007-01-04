/**
 * SearchPanel.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.doors;
import com.runstate.augur.ui.viewer.navigators.*;
import com.runstate.augur.controller.Door;
import com.runstate.augur.ui.doors.DoorListAdapter;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import com.runstate.augur.ui.viewer.MessageStrip;
import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class DoorsNavigator extends AbstractNavigator implements ListSelectionListener
{
	JList doorlist;
	MessageStrip css;
	DoorView doorView;
        
	public DoorsNavigator(BrowserCommandHandler navp)
	{
		super(navp);
		
		setLayout(new BorderLayout());
		
		css=new MessageStrip();
		doorlist=new JList(new DoorListAdapter());
		doorlist.setCellRenderer(new DoorListCellRenderer());
		
		add(BorderLayout.NORTH,css);
		add(BorderLayout.CENTER,new JScrollPane(doorlist));
		
		doorlist.addListSelectionListener(this);
                
	}
	
	public String getNavPanelName() { return "Doors"; }
	
        public void setView(DoorView doorView)
        {
            this.doorView=doorView;
            doorlist.setSelectedIndex(0);
        }
        
	public void valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting()) return;

		int i=doorlist.getSelectedIndex();
		
		if(i==-1)
		{
			return;
		}
		
              Door door=(Door)doorlist.getSelectedValue();
              
             doorView.setDoor(door);
	}
	
	public void focusOnMe()
	{
		doorlist.requestFocus();
	}
	
	
}
