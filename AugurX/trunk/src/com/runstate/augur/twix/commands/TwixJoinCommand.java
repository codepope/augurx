/**
 * JoinCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.commands;

import com.runstate.augur.twix.sync.TwixSync;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.commands.Parameter;
import com.runstate.augur.gallery.commands.commands.JoinCommand;
import com.runstate.augur.gallery.commands.parameters.BundleParameter;
import com.runstate.augur.gallery.commands.parameters.StringParameter;
import com.runstate.util.ssh.SSHConnection;

public class TwixJoinCommand extends TwixCommand implements JoinCommand
{
	static final long serialVersionUID = -3875375477194393741L;
	
	String path;
	
	public TwixJoinCommand(Long doorid,String path)
	{
		super(doorid,null);
		this.path=path;
	}
	
    @Override
	public String toString()
	{
		return "Twix:Join "+path;
	}
	
    @Override
	public String getCommandType()
	{
		return "Twix Join";
	}
	
    @Override
		public Parameter[] getParameters() {
		Parameter[] p=new Parameter[1];
		
		p[0]=new BundleParameter("path","Path",path);
		
		return p;
	}
	
    @Override
	public boolean setParameters(Parameter[] p) {
		boolean changed=false;
		
		for(int i=0;i<p.length;i++) {
			String name=p[i].getName();
			
			if(name.equals("path")) {
				StringParameter pp=(StringParameter)p[i];
				if(!path.equals(pp.getValue())) {
					path=pp.getValue();
					changed=true;
				}
			}
		}
		
		return changed;
	}
	
	public String batchCommand(Door door)
	{
		return "join "+door.getNativePath(path)+"\n";
	}
	
    @Override
	public boolean isEditable() { return true; }
	
	public boolean executeCommand(Door door, TwixSync cm, Gallery g, SSHConnection ssh)
	{
		// TODO
		return false;
	}

}

