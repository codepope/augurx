/**
 * NavSetStrandUnread.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;



import com.runstate.augur.gallery.Bundle;
import com.runstate.augur.gallery.Msg;
import com.runstate.augur.ui.viewer.Browser;

public class VCSetStrandUnread extends ViewerDBCommand {
	Long bundleid;
	Long msgid;
	boolean setting;
	
	public VCSetStrandUnread(Long bundleid,Long msgid,boolean setting) {
		super();
		this.msgid=msgid;
		this.bundleid=bundleid;
		this.setting=setting;
	}
	
	public VCSetStrandUnread(Msg msg,boolean setting) {
		this(msg.getBundleId(),msg.getKnotId(),setting);
	}
	
	public void execute(Browser viewer) {
		Bundle bundle=viewer.cmdGetBundle(bundleid);
		bundle.setStrandWithMsgUnread(msgid,setting);
	}
}

