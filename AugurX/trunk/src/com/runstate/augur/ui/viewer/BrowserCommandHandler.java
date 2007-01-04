/**
 * ViewerCommandHandler.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer;

import com.runstate.augur.ui.viewer.commands.ViewerCommand;
import javax.swing.JComponent;

public interface BrowserCommandHandler
{
	
	/**
	 * Method updateMenus
	 *
	 */
	public void updateMenus();
	
	public void doCommand(ViewerCommand nc);
	public JComponent getMainPane();
	public void updateCommandLine();
}

