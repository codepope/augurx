/**
 * VCComment.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.ui.viewer.Browser;

public class VCComment extends ViewerUICommand
{
	Long bundleid;
	Long knotid;
	
	public VCComment(Long bundleid,Long knotid)
	{
		this.bundleid=bundleid;
		this.knotid=knotid;
	}
	
	/**
	 * Method execute
	 *
	 * @param    viewer     An instance of a Browser
	 *
	 */
	public void execute(Browser viewer) {
		viewer.cmdComment(bundleid,knotid);
	}

}

