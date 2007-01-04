/**
 * AmeolImportView.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.ui;
import com.runstate.augur.gallery.missions.Mission;
import com.runstate.util.ImageCache;
import com.runstate.augur.ui.status.BasicStatus;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import javax.swing.ImageIcon;

public class ScratchpadImport extends BasicStatus {
	
	
	public ScratchpadImport(Mission m) {
		super(m);
	}
	
		public String getPrefsName()
	{
		return "scratchpadimport";
	}
	

	public boolean isAutoClose()
	{
		return false;
	}
	
	public boolean wantsTransfer()
	{
		return false;
	}
	
	public ImageIcon getIcon() {
		return ImageCache.get("scratchimport");
	}
}

