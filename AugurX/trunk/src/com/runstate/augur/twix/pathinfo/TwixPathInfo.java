/**
 * TwixPathInfo.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.pathinfo;

import com.runstate.augur.gallery.PathInfo;
import java.io.Serializable;

public class TwixPathInfo extends PathInfo implements Serializable
{
		public TwixPathInfo(Long doorid,Long bundleid)
	{
		super(doorid,bundleid);
	}

		public String getUIClassName() { return "com.runstate.augur.twix.ui.TwixPathInfoUI"; }
}

