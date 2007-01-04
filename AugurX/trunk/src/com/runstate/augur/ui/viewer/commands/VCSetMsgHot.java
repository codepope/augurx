/**
 * VCSetMsgHot.java
 *
 * @author Created by Omnicore CodeGuide
 */


package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.gallery.Bundle;
import com.runstate.augur.gallery.Msg;
import com.runstate.augur.ui.viewer.Browser;
import com.runstate.augur.ui.viewer.commands.ViewerDBCommand;

public class VCSetMsgHot extends ViewerDBCommand
{
	Long bundleid;
	Long msgid;
	boolean setting;
	
	public VCSetMsgHot(Long bundleid,Long msgid,boolean setting)
	{
		super();
		this.msgid=msgid;
		this.bundleid=bundleid;
		this.setting=setting;
	}
	
	public VCSetMsgHot(Msg msg,boolean setting)
	{
		this(msg.getBundleId(),msg.getKnotId(),setting);
	}
	
	public void execute(Browser viewer)
	{
		Bundle bundle=viewer.cmdGetBundle(bundleid);
		bundle.setMsgHot(msgid,setting);
	}
}
