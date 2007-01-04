/**
 * RootPathInfo.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.views;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.gallery.PathInfo;

public class RootPathInfo extends PathInfo
{
	public RootPathInfo()
	{
		super(null,Controller.getController().getGallery().getBundleManager().getRootBundleId());
	}
	
	public String getUIClassName() { return "com.runstate.augur.ui.viewer.views.RootPathInfoUI"; }
	
	public String getBundleName() { return "/"; }
	
}

