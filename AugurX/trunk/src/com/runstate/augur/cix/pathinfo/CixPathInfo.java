/**
 * CixPathInfo.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.pathinfo;

import com.runstate.augur.gallery.PathInfo;
import java.io.Serializable;

public class CixPathInfo extends PathInfo implements Serializable
{
		public CixPathInfo(Long doorid,Long bundleid)
	{
		super(doorid,bundleid);
	}

		public String getUIClassName() { return "com.runstate.augur.cix.ui.CixPathInfoUI"; }
}

