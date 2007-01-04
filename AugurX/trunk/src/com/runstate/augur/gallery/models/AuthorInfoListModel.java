/**
 * UserInfoListModel.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.models;

import com.runstate.augur.gallery.AuthorInfo;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.events.PoolEvent;
import com.runstate.augur.gallery.listeners.PoolEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.AbstractListModel;

public class AuthorInfoListModel extends AbstractListModel implements PoolEventListener
{
	
	ArrayList<String> authoraddresses;
	Gallery gallery;
	
	public AuthorInfoListModel(Gallery gallery)
	{
		super();
		this.gallery=gallery;
		authoraddresses=new ArrayList<String>(gallery.getAuthorInfoAddresses());
		gallery.addPoolEventListener(this);
	}
	
	public void close()
	{
		gallery.removePoolEventListener(this);
	}
	
	public void poolEventOccurred(PoolEvent ce)
	{
		if(!ce.getPool().equals(AuthorInfo.getPoolName())) return;
		
		String auguraddress=ce.getKey();
		
		switch(ce.getType())
		{
			case PoolEvent.ADDED:
				int index = Collections.binarySearch(authoraddresses, auguraddress);
				if (index < 0)
				{
					authoraddresses.add(-index-1, auguraddress);
					fireIntervalAdded(this,-index-1,-index);
				}
			case PoolEvent.UPDATED:
				// Keys do not change
				break;
			case PoolEvent.DELETED:
				int j=0;
				Iterator<String> i=authoraddresses.iterator();
				while(i.hasNext())
				{
					String iui=i.next();
					j++;
					if(iui.equals(auguraddress))
					{
						i.remove();
						fireIntervalRemoved(this,j,j);
						return;
					}
				}
				break;
		}
		
	}
	
	/**
	 * Returns the length of the list.
	 * @return the length of the list
	 */
	public int getSize()
	{
		return authoraddresses.size();
	}
	
	/**
	 * Returns the value at the specified index.
	 * @param index the requested index
	 * @return the value at <code>index</code>
	 */
	public Object getElementAt(int index)
	{
		return authoraddresses.get(index);
	}
	
//	public int indexOf(String username)
//	{
//		int i=0;
//
//		while(i<authoraddresses.size())
//		{
//			AuthorInfo ui=(AuthorInfo)authoraddresses.get(i);
//			if(ui.getAugurAddress().equals(username))
//			{
//				return i;
//			}
//
//			i++;
//		}
//
//		return -1;
//
//	}
}

