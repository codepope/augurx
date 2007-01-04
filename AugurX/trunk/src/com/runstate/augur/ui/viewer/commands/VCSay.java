/**
 * VCSay.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.ui.viewer.Browser;

public class VCSay extends ViewerUICommand
{
	Long bundleid;
	
	public VCSay(Long bundleid)
	{
		this.bundleid=bundleid;
	}
	
	public void execute(Browser viewer)
	{
		viewer.cmdSay(bundleid);
	}
}

