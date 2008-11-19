/**
 * RetrieveMessageCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.commands;

import com.runstate.augur.AugurX;
import com.runstate.augur.twix.AmeolScratchpadParser;
import com.runstate.augur.twix.sync.TwixSync;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.commands.Parameter;
import com.runstate.augur.gallery.commands.commands.GetCommand;
import com.runstate.augur.gallery.commands.parameters.BooleanParameter;
import com.runstate.augur.gallery.commands.parameters.BundleParameter;
import com.runstate.augur.gallery.commands.parameters.IntParameter;
import com.runstate.augur.gallery.commands.parameters.LongParameter;
import com.runstate.augur.gallery.Msg;
import com.runstate.util.ssh.SSHConnection;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class TwixGetCommand extends TwixCommand implements GetCommand {
	
	static final long serialVersionUID = 7729438380879749009L;

	private Long msgid;
	private boolean all;
	
	public TwixGetCommand(Long doorid,Long bundleid,Long msgid) {
		super(doorid,bundleid);
		this.msgid=msgid;
	}
	
	public void setMsgid(Long msgid) {
		this.msgid = msgid;
	}
	
	/**
	 * Returns Skipback
	 *
	 * @return    an int
	 */
	public Long getMsgid() {
		return msgid;
	}
	
	/**
	 * Method setAll
	 *
	 */
	public void setAll(boolean newall)
	{
		all=newall;
	}
	
	/**
	 * Method isAll
	 *
	 */
	public boolean isAll()
	{
		return all;
	}
	
	
	public String toString() {
		return "Twix:Retrieve "+getBundleName()+":"+msgid;
	}
	
	public String getCommandType() {
		return "Twix Retrive Message";
	}
	
	public Parameter[] getParameters() {
		Parameter[] p=new Parameter[3];
		
		p[0]=new BundleParameter("path","Path",getBundleName());
		p[1]=new LongParameter("msgid","Message to retrieve",msgid.longValue(),1,10000,1);
		p[2]=new BooleanParameter("all","All",all);
		return p;
	}
	
	public boolean setParameters(Parameter[] p)
	{		boolean changed=false;
		
		for(int i=0;i<p.length;i++) {
			String name=p[i].getName();
			
			if(name.equals("msgid")) {
				LongParameter ip=(LongParameter)p[i];
				if(getMsgid().longValue()!=ip.getLongValue()) {
					setMsgid(new Long(ip.getLongValue()));
					changed=true;
				}
			} else if(name.equals("path")) {
				BundleParameter pp=(BundleParameter)p[i];
				if(!getBundleName().equals(pp.getBundleName())) {
				//	setAugurPathRef(pp.getBundleName());
				//	changed=true;
				}
			} else if(name.equals("all")) {
				BooleanParameter bp=(BooleanParameter)p[i];
				if(isAll()!=bp.getBooleanValue()) {
					setAll(bp.getBooleanValue());
					changed=true;
				}
			}
		}
		
		return changed;
	}
	
//	public String batchCommand()
//	{
//		return "join "+getPrefs().getDoorByPath(getPath()).getNativePath(getPath())+"\n"+"file "+msgid+"\nq\n";
//	}
	
    @Override
	public boolean isEditable() { return true; }
	
    @Override
		public boolean isInfo() { return true; }
	
	public boolean executeCommand(Door door,TwixSync cm,Gallery gallery,SSHConnection sshconnection)
	{
		String twixpath=door.getNativePath(getBundleName());
                sshconnection.write("q\n q\n");
                sshconnection.write("terse\n");
                sshconnection.waitFor("M:");
                sshconnection.write("opt term ec no term pag 0 term width 255 q\n");
                sshconnection.waitFor("M:");
		sshconnection.write("kills\n");
		sshconnection.waitFor("M:");
		sshconnection.write("join "+twixpath+" y\n");
		sshconnection.waitFor( new String[]{ "R:","Rf:" });
			if(isAll())
			{
				sshconnection.write("file first to last q\n");
			}
			else
			{
				sshconnection.write("file "+msgid+" q\n");
			}
			

        
       int rslt=sshconnection.waitFor(new String[] { "Scratchpad is 53 bytes.", "M:", "R:" });
        
        File downloadedFile=null;
        
        if(rslt==0) {
            sshconnection.write("kills\n");
            sshconnection.waitFor("M:");
        } else {
            if(rslt==2) {
                sshconnection.write("q\n");
                sshconnection.waitFor("M:");
            }
            
            sshconnection.write("show scratchpad echo #####AUGURBREAK#####\n");
            
           String scratchpad = sshconnection.waitForAndReturnAll("#####AUGURBREAK#####");
            
            try {
                downloadedFile=new File(Controller.getProfile().get(Prefs.HOMEDIR)+"lastscratchtwix");
                
                DataOutputStream dos=new DataOutputStream(new FileOutputStream(downloadedFile));
                dos.writeChars(scratchpad);
                
                
                dos.close();
                
		parseFile(door,gallery, downloadedFile);
            } catch (IOException e) {}
        }
            sshconnection.write("clear\n");
            sshconnection.waitFor("M:");
		
		
		return true;
	}
	
	/**
	 * Method parseFile
	 *
	 * @param    door                a  Door
	 * @param    gallery             a  Gallery
	 * @param    f                   a  File
	 *
	 */
	private void parseFile(Door door, Gallery gallery, File f)
	{
		AmeolScratchpadParser asp=null;
		int ncnt=0;
		int cnt=0;
		
		try
		{
			asp=new AmeolScratchpadParser(f);
		}
		catch (IOException e) { return; }
		
		ArrayList a=new ArrayList();
		
		while(asp.hasNext())
		{
			String c=asp.next();
			Msg cm=new Msg(door,c);
			cm.setUnread(true);
			gallery.newMsg(cm,door.getDoorid());
		}
	}
	
	public String batchCommand(Door d)
	{
		return null;
	}
	
}

