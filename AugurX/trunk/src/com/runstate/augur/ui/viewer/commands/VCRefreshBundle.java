/**
 * VCRefreshBundle.java
 *
 * @author Created by Omnicore CodeGuide
 */


package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.PathInfo;
import com.runstate.augur.gallery.commands.Command;
import com.runstate.augur.ui.viewer.Browser;

public class VCRefreshBundle extends ViewerDBCommand
{
	Long bundleid;
	
	public VCRefreshBundle(long bundleid)
	{
		super();
		this.bundleid=bundleid;
	}
	
	public void execute(Browser viewer)
	{
		PathInfo ai=Controller.getGallery().getPathInfo(bundleid);
		
		Door door=Controller.getGallery().getDoorByBundleId(bundleid);
		
		if(ai==null)
		{
			ai=door.createPathInfo(bundleid);
			Controller.getGallery().addPoolable(ai);
		}
		
		Command c=door.refresh(ai);
		
		ai.setRefreshpending(true);
		
		Controller.getGallery().updatePoolable(ai);
		
		Controller.getGallery().addCommand(c);
	}
}


