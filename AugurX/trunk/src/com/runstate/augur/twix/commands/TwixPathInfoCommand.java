/**
 * ShowAllCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.commands;

import com.runstate.augur.controller.Controller;
import com.runstate.augur.twix.pathinfo.TwixConfPathInfo;
import com.runstate.augur.twix.pathinfo.TwixRootPathInfo;
import com.runstate.augur.twix.sync.TwixSync;
import com.runstate.augur.controller.Door;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.commands.commands.PathInfoCommand;
import com.runstate.util.ssh.SSHConnection;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class TwixPathInfoCommand extends TwixCommand implements PathInfoCommand
{
	static final long serialVersionUID = -3875375477194391234L;
	
	public TwixPathInfoCommand(Long doorid,Long bundleid)
	{
		super(doorid,bundleid);
	}
	
	public String toString()
	{
		return "Twix:Get Path Info "+getBundleName();
	}
	
    @Override
	public boolean isInfo() { return true; }
    
    @Override	
	public boolean executeCommand(Door door,TwixSync cm,Gallery gallery,SSHConnection sshconnection)
	{
		final String nativepath=door.getNativePath(getBundleName());
                String scratchpad = "";
               	sshconnection.write("q\nq\nterse\n");
                sshconnection.write("opt term ec no term pag 0 term width 255 q\n");
		sshconnection.waitFor("M:");
		sshconnection.write("kills\n");
		sshconnection.waitFor("M:");
		
		if(nativepath==null)
		{
			sshconnection.write("file show\n");
			sshconnection.waitFor("M:");
                        sshconnection.write("file echo  \n");
			sshconnection.waitFor("M:");
                        sshconnection.write("file echo #####AUGURBREAK#####\n");
			sshconnection.waitFor("M:");
			sshconnection.write("file show all\n");
			//sshconnection.waitFor("M:");
		}
		else
		{
                    
			sshconnection.write("file show "+nativepath+"\n");
			sshconnection.waitFor("M:");
                        sshconnection.write("file echo  \n");
			sshconnection.waitFor("M:");
			sshconnection.write("file echo #####AUGURBREAK#####\n");
			sshconnection.waitFor("M:");
			sshconnection.write("file show part "+nativepath+"\n");
			sshconnection.waitFor("M:");
		}
                sshconnection.write("show scratchpad echo #####AUGURBREAK2#####\n");
                scratchpad = sshconnection.waitForAndReturnAll("#####AUGURBREAK2#####");
                try 
                {
                    File download=new File(Controller.getProfile().get(Prefs.HOMEDIR)+"twixparlist");
                    DataOutputStream dos=new DataOutputStream(new FileOutputStream(download));
                    dos.writeChars(scratchpad);
                    dos.close();
                } 
                catch
                (IOException e) {}
        
                sshconnection.write("clear\n");
                sshconnection.waitFor("M:");
		
		if(nativepath==null)
		{
        		TwixRootPathInfo pi=(TwixRootPathInfo)gallery.getPathInfo(getBundleid());
			if(pi==null)
			{
				pi=new TwixRootPathInfo(getDoorid(),getBundleid());
				gallery.addPathInfo(pi);
			}
			pi.setConflist(scratchpad);
			pi.setUpdateDate(new Date());
			pi.setRefreshpending(false);
			gallery.updatePathInfo(pi);
		}
		else
		{
			TwixConfPathInfo pi=(TwixConfPathInfo)gallery.getPathInfo(getBundleid());
			if(pi==null)
			{
				pi=new TwixConfPathInfo(getDoorid(),getBundleid());
				gallery.addPathInfo(pi);
			}
			pi.setConftext(scratchpad);
			pi.setUpdateDate(new Date());
			pi.setRefreshpending(false);
			gallery.updatePathInfo(pi);
		}
		
		
		return true;
	}
	
	public String batchCommand(Door d)
	{
		return null;
	}
	

	
	
}

