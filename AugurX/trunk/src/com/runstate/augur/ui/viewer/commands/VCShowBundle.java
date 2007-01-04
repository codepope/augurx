/**
 * NavShowBundle.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.ui.viewer.Browser;

public class VCShowBundle extends ViewerUICommand
{
	Long bundleid;
	Long knotid;
	
	public VCShowBundle(Long bundleid)
	{
		this(bundleid,null);
	}
	
	public VCShowBundle(Long bundleid,Long knotid)
	{
		this.bundleid=bundleid;
		this.knotid=knotid;
	}
	
	public void execute(Browser viewer)
	{
		viewer.cmdShowBundle(bundleid,knotid);
	}
}

