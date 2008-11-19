/**
 * SayCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.commands;

import com.runstate.augur.twix.sync.TwixSync;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.commands.Parameter;
import com.runstate.augur.gallery.commands.commands.SayCommand;
import com.runstate.augur.gallery.commands.parameters.BundleParameter;
import com.runstate.augur.gallery.commands.parameters.TextParameter;
import com.runstate.util.StringW;
import com.runstate.util.ssh.SSHConnection;
import java.lang.Long;
import java.lang.Long;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwixSayCommand extends TwixCommand implements SayCommand {
	static final long serialVersionUID = -3875375477194393740L;
	
	String text;
	
	public TwixSayCommand(Long doorid,Long bundleid,String text)
    {
		super(doorid,bundleid);
		setText(text);
	}
	
	/**
	 * Sets Text
	 *
	 * @param    Text                a  String
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Returns Text
	 *
	 * @return    a  String
	 */
	public String getText() {
		return text;
	}
	
	public String toString() {
		return "Twix:Say in "+getBundleName();
	}
	
	public String getCommandType() {
		return "Twix Say";
	}
	
	public Parameter[] getParameters() {
		Parameter[] p=new Parameter[2];
		
		p[0]=new BundleParameter("path","Path",getBundleName());
		p[1]=new TextParameter("text","Text",getText());
		
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
			else if(name.equals("text")) {
				TextParameter tp=(TextParameter)p[i];
				if(!getText().equals(tp.getText())) {
					setText(tp.getText());
					changed=true;
				}
			}
		}
		
		return changed;
	}
	
	public boolean isEditable() { return true; }
	
	public boolean isInfo() { return false; }
	
	public String batchCommand(Door door)
	{
		String path=door.getNativePath(getBundleName());
	
		StringBuffer sb=new StringBuffer();
                
		sb.append("j "+path+"\n");
		sb.append("say\n");
		sb.append(StringW.wordWrap(getText(),70));
		sb.append("\n");
		sb.append(".\n");
		sb.append("q\n");
		sb.append("a\n");
		return sb.toString();
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
                String msgNumber="";
                String path=door.getNativePath(getBundleName());
                
		ssh.write("q\n q\n terse\n");
                ssh.waitFor("M:");
                ssh.write("opt term ec no Mark no q\n");
                ssh.waitFor("M:");
                ssh.write("clear\n");
                ssh.waitFor("M:");
                ssh.write("j "+path+"\n");
                ssh.waitFor("R:");
		ssh.write("say\n");
                ssh.waitFor("TITLE:");
                ssh.write(StringW.wordWrap(getText(),70));
                ssh.write("\n");
                ssh.write(".\n");
                ssh.waitFor("A:");
                ssh.write("a\n");
                String messageDelivered = ssh.waitForAndReturnAll("added.");
                String realMsg = messageDelivered.substring(1, messageDelivered.indexOf("added"));
                //Pattern msgNumberPtn = Pattern.compile("^[^0-9]+([0-9]+)(.*\r\n)^(.*)$",Pattern.MULTILINE);
                Pattern msgNumberPtn = Pattern.compile("^[^0-9]+([0-9]+)(.*)$");
		Matcher m=msgNumberPtn.matcher(realMsg);
                if (m.matches())
                {
                    msgNumber = m.group(1);
                }
                ssh.write("q\n");
                ssh.waitFor("M:");
//                TwixGetCommand tgc = new TwixGetCommand(door.getDoorid(),getBundleid(), new Long(msgNumber)) ;
//                tgc.executeCommand(door, cm, g, ssh);
                    
		return true;
	}
	


//    @Override
//    public boolean executeCommand(Door door, TwixSync cm, Gallery g, SSHConnection ssh)
//    {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//	
}
