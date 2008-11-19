/**
 * SkipBackCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.commands;

import com.runstate.augur.twix.sync.TwixSync;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.commands.Parameter;
import com.runstate.augur.gallery.commands.commands.SkipBackCommand;
import com.runstate.augur.gallery.commands.parameters.IntParameter;
import com.runstate.util.ssh.SSHConnection;

public class TwixSkipBackCommand extends TwixCommand implements SkipBackCommand {
	
	static final long serialVersionUID = -3875375477194393739L;
	
	private int skipback;
	
	public TwixSkipBackCommand(Long doorid,Long bundleid,int i)
	{
		super(doorid,bundleid);
		skipback=i;
	}
	
	/**
	 * Sets Skipback
	 *
	 * @param    Skipback            an int
	 */
	public void setSkipback(int skipback) {
		this.skipback = skipback;
	}
	
	/**
	 * Returns Skipback
	 *
	 * @return    an int
	 */
	public int getSkipback() {
		return skipback;
	}
	
	public String toString()
	{
		return "Twix:Skipback for "+skipback+" days";
	}
	
	public String getCommandType()
	{
		return "Twix Skip Back";
	}
	
	public Parameter[] getParameters()
	{
		Parameter[] p=new Parameter[1];
		
		p[0]=new IntParameter("skip","Skip back days",skipback,1,5*365,1);
		
		return p;
	}
	
	public boolean setParameters(Parameter[] p)
	{		boolean changed=false;
		
		for(int i=0;i<p.length;i++) {
			String name=p[i].getName();
			
			if(name.equals("skip")) {
				IntParameter ip=(IntParameter)p[i];
				if(getSkipback()!=ip.getIntValue()) {
					setSkipback(ip.getIntValue());
					changed=true;
				}
			}
		}
		
		return changed;
	}
	
	public boolean isEditable() { return true; }
	
	/**
	 * Method executeCommand
	 *
	 * @param    door                a  Door
	 * @param    cm                  a  TwixSync
	 * @param    g                   a  Gallery
	 * @param    ssh                 a  SSHConnection
	 *
	 * @return   a boolean
	 *
	 */
	public boolean executeCommand(Door door, TwixSync cm, Gallery g, SSHConnection ssh)
	{
		// TODO
		return false;
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
		StringBuffer sb=new StringBuffer();
		
		System.out.println("batch command called");
		sb.append("order first twixnews\n");
		sb.append("macro fred file hea skip to back "+getSkipback()+" killsc tnext fred\n");
		sb.append("j twixnews/information\n");
		sb.append("fred\n");
		sb.append("killsc\n");

		return sb.toString();
	}
	
		
		
}

