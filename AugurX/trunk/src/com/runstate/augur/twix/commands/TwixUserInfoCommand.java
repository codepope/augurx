/**
 * ShowResumeCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.commands;

import com.runstate.augur.AugurX;
import com.runstate.augur.twix.sync.TwixSync;
import com.runstate.augur.twix.authorinfo.TwixAuthorInfo;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.Bundle;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.commands.commands.UserInfoCommand;
import com.runstate.util.ssh.SSHConnection;
import java.util.Date;

public class TwixUserInfoCommand  extends TwixCommand implements UserInfoCommand
{
	static final long serialVersionUID = -2598971382516199267L;
	
	String address;
	
	public TwixUserInfoCommand(Long doorid,String address)
	{
		super(doorid,null);
		setAddress(address);
	}
	
	/**
	 * Sets Address
	 *
	 * @param    Address             a  String
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}
	
	/**
	 * Returns Address
	 *
	 * @return    a  String
	 */
	public String getAddress()
	{
		return address;
	}
	
	public String toString()
	{
		return "Twix:Show Resume "+getAddress();
	}
	public boolean isInfo() { return true; }
	
	public boolean executeCommand(Door door,TwixSync cm,Gallery gallery,SSHConnection sshconnection)
	{
		String user=getAddress();
		
		sshconnection.write("kills\n");
		sshconnection.waitFor("M:");
		sshconnection.write("file show resume "+door.getNativeUser(user)+"\n");
		sshconnection.waitFor("M:");
		sshconnection.write("download\n");
		
		byte[] download=cm.download();
		
		sshconnection.waitFor("OK to delete the downloaded scratchpad-file? (y/n)? N"+(char)0x08);
		
		sshconnection.write("y\n");
		
		sshconnection.waitFor("M:");
	
		TwixAuthorInfo ui=(TwixAuthorInfo)gallery.getAuthorInfo(getAddress());
		
		if(ui==null)
		{
			ui=new TwixAuthorInfo(address);
			gallery.addAuthorInfo(ui);
		}
		
		ui.setResume(new String(download));
		ui.setUpdateDate(new Date());
		
		gallery.updateAuthorInfo(ui);
		
		
		return true;
	}
	
	/**
	 * Method batchCommand
	 *
	 * @param    d                   a  Door
	 *
	 * @return   a String
	 *
	 */
	public String batchCommand(Door d)
	{
		// TODO
		return null;
	}
	
	
	
}
