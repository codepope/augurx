/**
 * ShowAllCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.commands;

import com.runstate.augur.cix.pathinfo.CixConfPathInfo;
import com.runstate.augur.cix.pathinfo.CixRootPathInfo;
import com.runstate.augur.cix.sync.CixSync;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.commands.commands.PathInfoCommand;
import com.runstate.util.ssh.SSHConnection;
import java.util.Date;

public class CixPathInfoCommand extends CixCommand implements PathInfoCommand
{
	static final long serialVersionUID = -3875375477194391234L;
	
	public CixPathInfoCommand(Long doorid,Long bundleid)
	{
		super(doorid,bundleid);
	}
	
	public String toString()
	{
		return "Cix:Get Path Info "+getBundleName();
	}
	
	public boolean isInfo() { return true; }
	
	public boolean executeCommand(Door door,CixSync cm,Gallery gallery,SSHConnection sshconnection)
	{
		final String nativepath=door.getNativePath(getBundleName());
		
		if(nativepath==null)
		{
			sshconnection.write("kills\n");
			sshconnection.waitFor("M:");
			sshconnection.write("file show\n");
			sshconnection.waitFor("M:");
			sshconnection.write("file echo #####AUGURBREAK#####\n");
			sshconnection.waitFor("M:");
			sshconnection.write("file show all\n");
			sshconnection.waitFor("M:");
			
			sshconnection.write("download\n");
		}
		else
		{
			sshconnection.write("kills\n");
			sshconnection.waitFor("M:");
			sshconnection.write("file show "+nativepath+"\n");
			sshconnection.waitFor("M:");
			sshconnection.write("file echo #####AUGURBREAK#####\n");
			sshconnection.waitFor("M:");
			sshconnection.write("file show part "+nativepath+"\n");
			sshconnection.waitFor("M:");
			sshconnection.write("download\n");
		}
		
		sshconnection.waitFor("Sending in Batch Mode");
		
		byte[] download=cm.download();
		
		if(nativepath==null)
		{
			
			CixRootPathInfo pi=(CixRootPathInfo)gallery.getPathInfo(getBundleid());
			if(pi==null)
			{
				pi=new CixRootPathInfo(getDoorid(),getBundleid());
				gallery.addPathInfo(pi);
			}
			pi.setConflist(new String(download));
			pi.setUpdateDate(new Date());
			pi.setRefreshpending(false);
			gallery.updatePathInfo(pi);
		}
		else
		{
			CixConfPathInfo pi=(CixConfPathInfo)gallery.getPathInfo(getBundleid());
			if(pi==null)
			{
				pi=new CixConfPathInfo(getDoorid(),getBundleid());
				gallery.addPathInfo(pi);
			}
			pi.setConftext(new String(download));
			pi.setUpdateDate(new Date());
			pi.setRefreshpending(false);
			gallery.updatePathInfo(pi);
		}
		
		sshconnection.waitFor("OK to delete the downloaded scratchpad-file? (y/n)? N"+(char)0x08);
		
		sshconnection.write("y\n");
	
		sshconnection.waitFor("M:");
		
		return true;
	}
	
	public String batchCommand(Door d)
	{
		return null;
	}
	
	
	
}

