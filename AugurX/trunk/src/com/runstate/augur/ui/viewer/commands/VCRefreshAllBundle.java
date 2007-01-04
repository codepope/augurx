package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.controller.*;
import com.runstate.augur.gallery.*;
import com.runstate.augur.gallery.commands.*;
import com.runstate.augur.ui.viewer.*;
import java.util.*;

public class VCRefreshAllBundle extends ViewerDBCommand {
	
	public VCRefreshAllBundle() {
		super();
	}
	
	public void execute(Browser viewer) {
		ArrayList<Bundle> ab=Controller.getGallery().getBundles();
		
		for (Bundle b:ab) {
			if (b.isContainer()) {
				
				Long bundleid=b.getBundleid();
				
				PathInfo ai=Controller.getGallery().getPathInfo(bundleid);
				
				Door door=Controller.getGallery().getDoorByBundleId(bundleid);
				
				if (ai == null) {
					ai = door.createPathInfo(bundleid);
					Controller.getGallery().addPoolable(ai);
				}
				
				Command c=door.refresh(ai);
				
				ai.setRefreshpending(true);
				
				Controller.getGallery().updatePoolable(ai);
				
				Controller.getGallery().addCommand(c);
			}
		}
	}
}


