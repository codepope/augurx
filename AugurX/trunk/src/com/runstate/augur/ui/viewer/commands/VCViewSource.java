/*
 * VCViewSource.java
 *
 * Created on October 28, 2006, 12:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.ui.viewer.Browser;

public class VCViewSource extends ViewerUICommand
{
	Long bundleid;
	Long knotid;
	
	public VCViewSource(Long bundleid,Long knotid)
	{
		this.bundleid=bundleid;
		this.knotid=knotid;
	}
	
	/**
	 * Method execute
	 *
	 * @param    nce                 a  ViewerCommandImplementor
	 *
	 */
	public void execute(Browser viewer) {
		viewer.cmdViewSource(bundleid,knotid);
	}

}

