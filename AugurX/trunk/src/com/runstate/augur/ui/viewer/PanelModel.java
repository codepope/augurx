/**
 * PanelModel.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer;

import com.runstate.augur.ui.viewer.navigators.AbstractNavigator;
import java.util.Arrays;
import javax.swing.AbstractListModel;

public class PanelModel extends AbstractListModel
{
	AbstractNavigator[] panels;
	
	public PanelModel(AbstractNavigator[] panels)
	{
		setPanels(panels);
	}
	
	public void setPanels(AbstractNavigator[] panels)
	{
		this.panels=panels;
		for(AbstractNavigator sp:panels)
		{
			sp.setPanelModel(this);
		}
		fireContentsChanged(this,0,panels.length);
	}
	
	public void updated(AbstractNavigator sp,String status)
	{
		System.out.println("Updated fired");
		for(int i=0;i<panels.length;i++)
		{
			if(sp==panels[i])
			{
				fireContentsChanged(this,i,i);
				return;
			}
		}
		System.out.println("Missed!");
	}
	
	/**
	 * Returns the length of the list.
	 * @return the length of the list
	 */
	public int getSize()
	{
		return panels.length;
	}
	
	/**
	 * Returns the value at the specified index.
	 * @param index the requested index
	 * @return the value at <code>index</code>
	 */
	public Object getElementAt(int index)
	{
		return panels[index].toString();
	}
	
}

