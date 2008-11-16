/**
 * VCOriginal.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.ui.viewer.Browser;

public class VCOriginal extends ViewerUICommand
{
	String url;
	
	public VCOriginal()
	{
		// Arbitrary Original
	}
	
	/**
	 * Method execute
	 *
	 * @param    viewer     An instance of a Browser
	 *
	 */
	public void execute(Browser viewer) {
		viewer.cmdOriginal();
	}

}

