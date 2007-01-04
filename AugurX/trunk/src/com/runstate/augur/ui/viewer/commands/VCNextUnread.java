/**
 * ViewerNextUnread.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;
import com.runstate.augur.ui.viewer.Browser;



public class VCNextUnread extends ViewerUICommand
{
	boolean scrollcurrent=false;
	
	public VCNextUnread(boolean scrollcurrent)
	{
		super();
		this.scrollcurrent=scrollcurrent;
	}
	

	public void execute(Browser viewer) {
		viewer.cmdNextUnread(scrollcurrent);
	}
	
	
	
}

