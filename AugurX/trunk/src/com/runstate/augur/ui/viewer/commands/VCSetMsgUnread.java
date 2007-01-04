/**
 * VCSetMsgUnread.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.gallery.Bundle;
import com.runstate.augur.gallery.Msg;
import com.runstate.augur.ui.viewer.Browser;
import java.util.ArrayList;
import java.util.Iterator;

public class VCSetMsgUnread extends ViewerDBCommand
{
	Long bundleid;
	Long msgid;
	boolean setting;
	boolean children;
	
	public VCSetMsgUnread(Long bundleid,Long msgid,boolean setting,boolean children)
	{
		super();
		this.msgid=msgid;
		this.bundleid=bundleid;
		this.setting=setting;
		this.children=children;
	}
	
	public VCSetMsgUnread(Msg msg,boolean setting,boolean children)
	{
		this(msg.getBundleId(),msg.getKnotId(),setting,children);
	}
	
	public void execute(Browser viewer)
	{
		Bundle bundle=viewer.cmdGetBundle(bundleid);
		setMsgUnread(bundle,msgid,setting,children);
		
	}
	
	private void setMsgUnread(Bundle bundle,Long msgid,boolean value,boolean descendchildren)
	{
		bundle.setMsgUnread(msgid,setting);
		
		if(children)
		{
			ArrayList<Long> al=bundle.getChildIds(msgid);
			Iterator<Long> i=al.iterator();
			while(i.hasNext())
			{
				Long l=i.next();
				setMsgUnread(bundle,l,value,descendchildren);
			}
		}
	}
}
