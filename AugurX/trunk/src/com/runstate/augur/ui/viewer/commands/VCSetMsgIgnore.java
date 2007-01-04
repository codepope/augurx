/**
 * VCSetMsgIgnore.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;



import com.runstate.augur.gallery.Bundle;
import com.runstate.augur.gallery.Msg;
import com.runstate.augur.ui.viewer.Browser;

public class VCSetMsgIgnore extends ViewerDBCommand
{
	Long bundleid;
	Long msgid;
	boolean setting;
	boolean andchildren;
	
	public VCSetMsgIgnore(Long bundleid,Long msgid,boolean setting,boolean andchildren)
	{
		super();
		this.msgid=msgid;
		this.bundleid=bundleid;
		this.setting=setting;
		this.andchildren=andchildren;
	}
	
	public VCSetMsgIgnore(Msg msg,boolean setting,boolean children)
	{
		this(msg.getBundleId(),msg.getKnotId(),setting,children);
	}
	
	public void execute(Browser viewer)
	{
		Bundle bundle=viewer.cmdGetBundle(bundleid);
		bundle.setMsgIgnore(msgid,setting);
		if(andchildren)
		{
			System.out.println("Ignore and children requires implementing");
		}
	}
}

