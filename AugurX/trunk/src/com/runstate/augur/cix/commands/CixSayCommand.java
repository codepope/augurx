/**
 * SayCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.commands;

import com.runstate.augur.cix.sync.CixSync;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.commands.Parameter;
import com.runstate.augur.gallery.commands.commands.SayCommand;
import com.runstate.augur.gallery.commands.parameters.BundleParameter;
import com.runstate.augur.gallery.commands.parameters.TextParameter;
import com.runstate.util.StringW;
import com.runstate.util.ssh.SSHConnection;

public class CixSayCommand extends CixCommand implements SayCommand {
	static final long serialVersionUID = -3875375477194393740L;
	
	String text;
	
	public CixSayCommand(Long doorid,Long bundleid,String text)
    {
		super(doorid,bundleid);
		setText(text);
	}
	
	/**
	 * Sets Text
	 *
	 * @param    text                a  String
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
		return "Cix:Say in "+getBundleName();
	}
	
	public String getCommandType() {
		return "Cix Say";
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
		return sb.toString();
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
		// TODO
		return false;
	}
	
	
	
}
