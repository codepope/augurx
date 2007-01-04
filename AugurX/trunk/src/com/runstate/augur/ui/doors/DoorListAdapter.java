/**
 * DoorListAdapter.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.doors;

import com.runstate.augur.controller.*;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.AbstractListModel;

public class DoorListAdapter extends AbstractListModel implements DoorListener
{
	
	ArrayList<Door> doors;
	
	// This adapter creates an All element which returns itself
	
	public DoorListAdapter()
	{
		doors=new ArrayList<Door>(Arrays.asList(Controller.getGallery().getDoors()));
		for(int i=0;i<doors.size();i++)
		{
			Door d=doors.get(i);
			d.addDoorListener(this);
		}
	}
	
	
	public int getSize()
	{
		return doors.size();
	}
	
	/**
	 * Returns the value at the specified index.
	 * @param index the requested index
	 * @return the value at <code>index</code>
	 */
	public Object getElementAt(int index)
	{
		return doors.get(index);
	}
	
	/**
	 * Method doorEventOccurred
	 *
	 * @param    de                  a  DoorEvent
	 *
	 */
	public void doorEventOccurred(DoorEvent de)
	{
		int i=doors.indexOf(de.getDoor());
		
		fireContentsChanged(this,i,i);
	}
	
	
	

}

