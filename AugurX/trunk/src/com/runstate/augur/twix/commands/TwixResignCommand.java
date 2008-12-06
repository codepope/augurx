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
import com.runstate.augur.gallery.commands.parameters.StringParameter;
import com.runstate.util.ssh.SSHConnection;

public class TwixResignCommand extends TwixCommand implements ResignCommand
{
	static final long serialVersionUID = -8603595765882584698L;

        String path;

	public TwixResignCommand(Long doorid, String path)
	{
            	super(doorid,null);
                if (path.startsWith("/twix/")){path=path.replace("/twix/", "");}
		this.path=path;
	}
        
    @Override
	public String toString()
	{
		return "Twix:Resign "+path;
	}
	
    @Override
		public String getCommandType()
	{
		return "Twix Resign";
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
	
    @Override
	public boolean isEditable() { return true; }
		
	public String batchCommand(Door door)
	{
		return "resign "+door.getNativePath(path)+"\n";
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
            	ssh.write("q\n q\n terse\n");
                ssh.waitFor("M:");
                ssh.write("opt term ec no Mark no q\n");
                ssh.waitFor("M:");
                ssh.write("clear\n");
                ssh.waitFor("M:");
                ssh.write("res "+path+"\n");
                int rslt = ssh.waitFor(new String[]{"(y/n)?", "You are not a member of conference","Topic?","is closed."});
                switch(rslt){
                        case 0 : // Normal resignation
                            ssh.write("y\n");
                            ssh.waitFor("Resigning from conference");
                            break;
                       case 1 : 
                            // Not a member of the conf - same message if the conf doesn't exist
                            door.fireDoorMsg("Can't resign conference "+ path +". Not currently a member");
                            break;
                            
                }

		return true;
	}
	
	
	
}

