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
	 * @param    nce                 a  ViewerCommandImplementor
	 *
	 */
	public void execute(Browser viewer) {
		viewer.cmdOpenURL(url);
	}

}

