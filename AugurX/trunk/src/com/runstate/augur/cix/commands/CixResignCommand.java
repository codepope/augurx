/**
 * ResignCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.commands;

import com.runstate.augur.cix.sync.CixSync;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.commands.Parameter;
import com.runstate.augur.gallery.commands.commands.ResignCommand;
import com.runstate.augur.gallery.commands.parameters.BundleParameter;
import com.runstate.util.ssh.SSHConnection;

public class CixResignCommand extends CixCommand implements ResignCommand
{

	static final long serialVersionUID = -8603595765882584698L;
	
	public CixResignCommand(Long doorid,Long bundleid)
	{
		super(doorid,bundleid);
	}
	
	public String toString()
	{
		return "Cix:Resign "+getBundleName();
	}
	
		public String getCommandType()
	{
		return "Cix Resign";
	}
	
		public Parameter[] getParameters() {
		Parameter[] p=new Parameter[1];
		
		p[0]=new BundleParameter("path","Path",getBundleName());
		
		return p;
	}
	
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
	
	public boolean isEditable() { return true; }
		
	public String batchCommand(Door door)
	{
		return "resign "+door.getNativePath(getBundleName())+"\n";
	}
	
	/**
	 * Method executeCommand
	 *
	 * @param    door                a  Door
	 * @param    cm                  a  CixSync
	 * @param    g                   a  Gallery
	 * @param    ssh                 a  SSHConnection
	 *
	 * @return   a boolean
	 *
	 */
	public boolean executeCommand(Door door, CixSync cm, Gallery g, SSHConnection ssh)
	{
		return false;
	}
	
	
	
}

