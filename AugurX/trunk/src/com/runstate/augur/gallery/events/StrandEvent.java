/**
 * ThreadEvent.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.events;

public class StrandEvent extends CoreEvent
{
	public static final int NEW=0;
	public static final int UPDATE=1;
	public static final int UPDATESINGLE=2;
	public static final int DELETE=3;
	
	Long bundleid;
	Long rootid;
	int type;
	
	/**
	 * Constructor
	 *
	 * @param    pathid              a  long
	 * @param    rootid              a  long
	 * @param    type                an int
	 */
	public StrandEvent(Long pathid, Long rootid, int type)
	{
		this.bundleid = pathid;
		this.rootid = rootid;
		this.type = type;
	}
	
	public Long getBundleid()
	{
		return bundleid;
	}
	
	public void setRootid(Long rootid)
	{
		this.rootid = rootid;
	}
	
	public Long getRootid()
	{
		return rootid;
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

	public boolean isUpdateSingle()
	{
		return type==UPDATESINGLE;
	}
	
	public String toString()
	{
		return "StrandEvent("+type+","+bundleid+","+rootid+")";
	}
}

