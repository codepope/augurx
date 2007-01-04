/**
 * AbstractView.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.views;

import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import com.runstate.augur.ui.viewer.commands.ViewerCommand;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPanel;

public class AbstractViewPanel extends JPanel implements BrowserCommandHandler
{
	BrowserCommandHandler vch;
	
	public AbstractViewPanel(BrowserCommandHandler vch)
	{
		this.vch=vch;
	}
	
	public void doCommand(ViewerCommand nc)
	{
		vch.doCommand(nc);
	}
	
	public JComponent getMainPane()
	{
		return vch.getMainPane();
	}
	
	public void addMenus(ArrayList<JMenu> menus)
	{
	}
	
	public void updateCommandLine()
	{
		vch.updateCommandLine();
	}
	
	public void updateMenus()
	{
		vch.updateMenus();
	}
}

