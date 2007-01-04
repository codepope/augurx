/**
 * SingleDoorPanel.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.doors;
import com.runstate.augur.controller.Door;
import com.runstate.augur.controller.DoorUI;
import com.runstate.augur.gallery.Sync;
import com.runstate.augur.ui.augurpanel.AugurPanel;
import com.runstate.augur.ui.commands.CommandQueue;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class DoorView extends AugurPanel
{
	JLabel doornamelabel;
	Door currdoor;
	JPanel switchpanel;
	CardLayout cl=new CardLayout();
	HashMap<String, DoorUI> dooruimap=new HashMap<String, DoorUI>();
	JSplitPane sp;
        
	public DoorView()
	{
		super();
		
                sp=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER,sp);
                
		doornamelabel=new JLabel();
		add(BorderLayout.NORTH,doornamelabel);
		
		switchpanel=new JPanel(cl);
		sp.add(switchpanel);
		
		CommandQueue cq=new CommandQueue();
		
                sp.add(cq);
                sp.setResizeWeight(0.5);
                sp.setDividerLocation(0.5D);
                
                
	}

	public void setDoor(Door door)
	{
		doornamelabel.setText(door.getDoorname());
		currdoor=door;
		
		DoorUI sui=dooruimap.get(door.getDoorname());
		
		if(sui==null)
		{
			Sync s=door.getSync();
			sui=door.getDoorUI();
	//		sui.setSync(s);
			dooruimap.put(door.getDoorname(),sui);
			switchpanel.add(sui.getDoorPanel(),door.getDoorname());
			sui.getDoorPanel().invalidate();
		}
		
		cl.show(switchpanel,door.getDoorname());
		
	}

    public void addMenus(ArrayList<JMenu> menus) {
        return;
    }
	
}

