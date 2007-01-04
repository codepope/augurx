/**
 * PathEvent.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.events;



public class BundleEvent extends CoreEvent
{
	public static final int NEW=0;
	public static final int UPDATE=1;
	public static final int DELETE=2;
	
	int type;
	Long bundleid;
	
	public BundleEvent(int type,Long bundleid)
	{
		this.type=type;
		this.bundleid=bundleid;
	}
	
	public Long getBundleid()
	{
		return bundleid;
	}

	public boolean isNew()
	{
		return type==NEW;
	}
	
	public boolean isDelete()
	{
		return type==DELETE;
	}
	
	public boolean isUpdate()
	{
		return type==UPDATE;
	}
}

