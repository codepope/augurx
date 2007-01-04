/**
 * MissionAction.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui;

import com.runstate.augur.gallery.missions.Mission;
import com.runstate.augur.ui.augurpanel.AugurPanelManger;
import com.runstate.augur.ui.status.BasicStatus;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

public class MissionAction extends AbstractAction
{
	AugurPanelManger adesktop=null;
	Mission mission=null;
	
	public MissionAction(AugurPanelManger ad,Mission m)
	{
		adesktop=ad;
		mission=m;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		BasicStatus bs=mission.getStatusUI();
		
		adesktop.addToDesktop(bs);
	}
	
	public Object getValue(String key)
	{
		if(key.equals(Action.NAME)) return mission.getMenuName();
		if(key.equals(Action.SMALL_ICON)) return mission.getDoor().getDoorIcon();
		return super.getValue(key);
	}
	
	
}

