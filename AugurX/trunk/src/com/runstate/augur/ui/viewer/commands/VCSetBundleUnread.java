/**
 * NavSetBundleUnread.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.gallery.Bundle;
import com.runstate.augur.ui.viewer.Browser;

public class VCSetBundleUnread extends ViewerDBCommand
{
	Long bundleid;
	boolean setting;
	
	public VCSetBundleUnread(Long bundleid,boolean setting)
	{
		super();
		this.bundleid=bundleid;
		this.setting=setting;
	}
	
	public void execute(Browser viewer)
	{
		Bundle b=viewer.cmdGetBundle(bundleid);
		b.setBundleUnread(setting);
	}
}

