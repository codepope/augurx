/**
 * NavCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.ui.viewer.Browser;

public abstract class ViewerCommand
{
	public ViewerCommand()
	{
		// Nothing here yet
	}
	
	public void execute(Browser viewer)
	{
		System.out.println("*** Unimplemented Command Exectuted :"+this.getClass().getName());
	}

}

