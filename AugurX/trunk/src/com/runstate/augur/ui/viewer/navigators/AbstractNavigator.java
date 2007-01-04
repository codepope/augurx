/**
 * SubNavPanel.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.navigators;
import javax.swing.*;

import com.runstate.augur.ui.viewer.PanelModel;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import com.runstate.augur.ui.viewer.commands.ViewerCommand;
import java.util.ArrayList;

public class AbstractNavigator extends JPanel {
	BrowserCommandHandler vch;
	
	public String getNavPanelName()
	{
		return "SubNavPanel";
	}
	
	public AbstractNavigator(BrowserCommandHandler navp) {
		super();
		
		this.vch=navp;
	}
	
	public void doCommand(ViewerCommand nc) {
		vch.doCommand(nc);
	}
	
	public void focusOnMe() {
	}
	
	public void setKey(String key,String name,Action a) {
		JComponent jc=vch.getMainPane();
		KeyStroke k=KeyStroke.getKeyStroke(key);
		InputMap imap=jc.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		imap.put(k,name);
		ActionMap amap=jc.getActionMap();
		amap.put(name,a);
	}
	
	PanelModel panelmodel;
	String status;
	
	public void setPanelModel(PanelModel pm)
	{
		this.panelmodel=pm;
	}
	
	public void setStatus(String s)
	{
		status=s;
		if(panelmodel!=null) panelmodel.updated(this,s);
	}
	
	public String toString()
	{
		if(status!=null)
		{
			return getNavPanelName()+" "+status;
		}
		
		return getNavPanelName();
	}
	
	public void addMenus(ArrayList<JMenu> jmenu)
	{
		return;
	}
}

