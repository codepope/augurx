/**
 * ResignCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.commands;

import com.runstate.augur.twix.sync.TwixSync;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.commands.Parameter;
import com.runstate.augur.gallery.commands.commands.ResignCommand;
import com.runstate.augur.gallery.commands.parameters.BundleParameter;
import com.runstate.util.ssh.SSHConnection;

public class TwixResignCommand extends TwixCommand implements ResignCommand
{

	static final long serialVersionUID = -8603595765882584698L;
	
	public TwixResignCommand(Long doorid,Long bundleid)
	{
		super(doorid,bundleid);
	}
	
    @Override
	public String toString()
	{
		return "Twix:Resign "+getBundleName();
	}
	
    @Override
		public String getCommandType()
	{
		return "Twix Resign";
	}
	
    @Override
		public Parameter[] getParameters() {
		Parameter[] p=new Parameter[1];
		
		p[0]=new BundleParameter("path","Path",getBundleName());
		
		return p;
	}
	
    @Override
	public boolean setParameters(Parameter[] p) {
		boolean changed=false;
		
		for(int i=0;i<p.length;i++) {
			String name=p[i].getName();
			
			if(name.equals("path")) {
				BundleParameter pp=(BundleParameter)p[i];
				if(!getBundleName().equals(pp.getBundleName())) {
					//setAugurPathRef(pp.getBundleName());
					//changed=true;
				}
			}
		}
		
		return changed;
	}
	
    @Override
	public boolean isEditable() { return true; }
		
	public String batchCommand(Door door)
	{
		return "resign "+door.getNativePath(getBundleName())+"\n";
	}
	
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
		return false;
	}
	
	
	
}

