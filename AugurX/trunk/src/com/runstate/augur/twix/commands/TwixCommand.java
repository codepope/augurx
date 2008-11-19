/**
 * TwixCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.commands;

import com.runstate.augur.twix.sync.TwixSync;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.commands.Command;
import com.runstate.util.ssh.SSHConnection;


public abstract class TwixCommand extends Command
{
	static final long serialVersionUID = -3875375477194393976L;

	public TwixCommand(Long doorid,Long bundleid)
	{
		super(doorid,bundleid);
	}
	
//	public TwixCommand(BundleRef apr)
//	{
//		super(apr);
//	}
//
//	public TwixCommand(long doorid)
//	{
//		super(doorid);
//	}
//
	public boolean isInfo()
	{
		return false;
	}
	
	public abstract boolean executeCommand(Door door,TwixSync cm,Gallery g,SSHConnection ssh);
	//public abstract String executeCommandMsgNumber(Door door,TwixSync cm,Gallery g,SSHConnection ssh);
	
	public abstract String batchCommand(Door d);

}

