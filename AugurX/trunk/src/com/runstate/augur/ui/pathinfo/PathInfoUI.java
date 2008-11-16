/**
 * PathInfoUI.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.pathinfo;

import com.runstate.augur.gallery.PathInfo;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;

public class PathInfoUI extends JPanel
{
	PathInfo pathInfo;
	PathInfoUIContainer pathInfoUIContainer;
	
	public PathInfoUI()
	{
		super(new BorderLayout());
		this.pathInfo=null;
		this.pathInfoUIContainer=null;
	}
	
	public void createUI()
	{
		add(BorderLayout.CENTER,new JLabel("No PathInfoUI implemented"));
	}
	
	/**
	 * Sets Pathinfo
	 *
	 * @param    pathinfo            a  PathInfo
	 */
	public void setPathInfo(PathInfo pathinfo) {
		this.pathInfo = pathinfo;
		updatedPathInfo();
	}
	
	/**
	 * Returns Pathinfo
	 *
	 * @return    a  PathInfo
	 */
	public PathInfo getPathInfo() {
		return pathInfo;
	}
	
	public void updatedPathInfo()
	{
	}
	
	public void setPathInfoUIContainer(PathInfoUIContainer piuic)
	{
		pathInfoUIContainer=piuic;
	}
	
	public void openURL(String url)
	{
		assert(pathInfoUIContainer!=null);
		
		pathInfoUIContainer.openURL(url);
	}
	
	public JMenu getMenu()
	{
		return null;
	}
}

