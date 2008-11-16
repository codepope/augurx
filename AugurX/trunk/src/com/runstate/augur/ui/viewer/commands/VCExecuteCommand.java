/**
 * ViewerOpenURL.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;
import com.runstate.augur.ui.viewer.Browser;



public class VCExecuteCommand extends ViewerUICommand
{
	String url;
	
	public VCExecuteCommand(String url)
	{
		this.url=url;
	}
	
	/**
	 * Method execute
	 *
	 * @param    viewer     An instance of a Browser
	 *
	 */
	public void execute(Browser viewer) {
		viewer.cmdOpenURL(url);
	}

}

