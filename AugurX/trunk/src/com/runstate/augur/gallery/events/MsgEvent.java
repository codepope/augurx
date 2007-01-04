/**
 * MsgEvent.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.events;

import com.runstate.augur.gallery.Msg;

public class MsgEvent extends CoreEvent
{
	public static final int NEW=0;
	public static final int UPDATE=1;
	public static final int DELETE=2;
	
	int type;
	Long bundleid;
	Long knotid;
	
	public MsgEvent(int type,Long pathid,Long knotid)
	{
		this.type=type;
		this.bundleid=pathid;
		this.knotid=knotid;
	}
	
	public Long getBundleid()
	{
		return bundleid;
	}
	
	public Long getKnotid()
	{
		return knotid;
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
	
	public String toString()
	{
		return "MsgEvent("+type+","+bundleid+","+knotid+")";
	}
}

