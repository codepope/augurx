/**
 * AView.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.augurpanel;

import com.runstate.augur.ui.*;
import com.runstate.augur.ui.viewer.navigators.AbstractNavigator;
import com.runstate.augur.ui.viewer.views.AbstractViewPanel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JPanel;

public class AugurPanel extends JPanel
{
	AugurPanelContainer container;
	
	public AugurPanel()
	{
            super();
	}
	
	public void setContainer(AugurPanelContainer container)
	{
		this.container = container;
	}
	
	public AugurPanelContainer getContainer()
	{
		return container;
	}
	
	public boolean wantsMenubar() { return false; }
	
	public boolean isApplicationMainWindow() { return false; };
	
    	public String getTitle() { 
            return "Nameless AView"; 
        }
	
	public ImageIcon getIcon() { return null; }
	
	public ArrayList<JMenu> getMenus() { return new ArrayList<JMenu>(); }
	
	public String getPrefsName()
	{
		
		return "com.runstate.augur.tmp."+getTitle();
	}
	
	public JPanel getNavPanel()
	{
		return null;
	}

	
	public boolean closeRequest(boolean force)
	{
		// Override this to get shutdown options
		return true;
	}
}

