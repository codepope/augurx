/**
 * VCSync.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.ui.viewer.Browser;

public class VCSync extends ViewerDBCommand
{
	public void VCSync()
	{
	}
	
	public void execute(Browser viewer)
	{
		viewer.cmdSyncStarted();
		int result=Controller.getController().syncAll();
		if(result==-1)
		{
			viewer.cmdSyncError();
		}
		else
		{
			viewer.cmdSyncDone(result);
		}
	}
}

