/**
 * SyncUI.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.controller;

import com.runstate.augur.gallery.Sync;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import javax.swing.JPanel;

public class SyncUI extends JPanel
{
	Sync sync=null;
	
	public SyncUI()
	{
		super();
		this.sync=sync;
	}
	
	public void setSync(Sync sync)
	{
		this.sync=sync;
		initialiseSyncUI();
	}
	
	public Sync getSync()
	{
		return sync;
	}
	
	public void initialiseSyncUI()
	{
	}
}

