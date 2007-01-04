/**
 * CixCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.commands;

import com.runstate.augur.cix.sync.CixSync;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.commands.Command;
import com.runstate.util.ssh.SSHConnection;


public abstract class CixCommand extends Command
{
	static final long serialVersionUID = -3875375477194393976L;

	public CixCommand(Long doorid,Long bundleid)
	{
		super(doorid,bundleid);
	}
	
//	public CixCommand(BundleRef apr)
//	{
//		super(apr);
//	}
//
//	public CixCommand(long doorid)
//	{
//		super(doorid);
//	}
//
	public boolean isInfo()
	{
		return false;
	}
	
	public abstract boolean executeCommand(Door door,CixSync cm,Gallery g,SSHConnection ssh);
	
	public abstract String batchCommand(Door d);

}

