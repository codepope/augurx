/**
 * AmeolImportView.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.ui;
import com.runstate.augur.gallery.missions.Mission;
import com.runstate.util.ImageCache;
import com.runstate.augur.ui.status.BasicStatus;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import javax.swing.ImageIcon;

public class ScratchpadExport extends BasicStatus {
	
	
	public ScratchpadExport(BrowserCommandHandler vch,Mission m) {
		super(m);
	}
	
		public String getPrefsName()
	{
		return "scratchpadexport";
	}
	

	public boolean prefersTab()
	{
		// TODO
		return false;
	}
	
	

	public boolean isAutoClose()
	{
		return false;
	}
	
	public boolean wantsTransfer()
	{
		return false;
	}
	
	public ImageIcon getIcon()
	{
		return ImageCache.get("scratchexport");
	}
}

