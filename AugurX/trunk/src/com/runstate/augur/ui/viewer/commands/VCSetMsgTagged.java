/**
 * VCSetMsgTagged.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.gallery.Bundle;
import com.runstate.augur.ui.viewer.Browser;

public class VCSetMsgTagged extends ViewerDBCommand
{
	Long bundleid;
	Long msgid;
	boolean setting;
	
	public VCSetMsgTagged(Long bundleid,Long msgid,boolean setting)
	{
		super();
		this.msgid=msgid;
		this.bundleid=bundleid;
		this.setting=setting;
	}
	
	public void execute(Browser viewer)
	{
		Bundle bundle=viewer.cmdGetBundle(bundleid);
		bundle.setMsgTagged(msgid,setting);
	}
}
