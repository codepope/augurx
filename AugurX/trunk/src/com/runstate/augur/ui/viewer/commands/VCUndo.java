/**
 * ViewerUndo.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.ui.viewer.Browser;

public class VCUndo extends ViewerDBCommand
{
	public VCUndo()
	{
		super();
	}
	
	public void execute(Browser viewer)
	{
		viewer.cmdUndo();
	}
}

