/**
 * CommentCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.commands;


import com.runstate.augur.AugurX;
import com.runstate.augur.cix.sync.CixSync;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.commands.Parameter;
import com.runstate.augur.gallery.commands.commands.CommentCommand;
import com.runstate.augur.gallery.commands.parameters.BundleParameter;
import com.runstate.augur.gallery.commands.parameters.IntParameter;
import com.runstate.augur.gallery.commands.parameters.LongParameter;
import com.runstate.augur.gallery.commands.parameters.TextParameter;
import com.runstate.util.StringW;
import com.runstate.util.ssh.SSHConnection;

public class CixCommentCommand extends CixCommand implements CommentCommand
{
	static final long serialVersionUID = -3875375477194393742L;
	
	String text;
	Long commentto;
	
	public CixCommentCommand(Long doorid,Long bundleid,Long commentto,String text)
	{
		super(doorid,bundleid);
		this.text=text;
		this.commentto=commentto;
	}


	
	/**
	 * Sets Commentto
	 *
	 * @param    Commentto           an int
	 */
	public void setCommentto(Long commentto) {
		this.commentto = commentto;
	}
	
	/**
	 * Returns Commentto
	 *
	 * @return    an int
	 */
	public Long getCommentto() {
		return commentto;
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
	
	public String toString()
	{
		return "Cix:Comment to "+getBundleName()+":"+commentto;
	}
	
		public String getCommandType()
	{
		return "Cix Comment";
	}
	
	public Parameter[] getParameters()
	{
		Parameter[] p=new Parameter[3];
		
		p[0]=new BundleParameter("path","Path",getBundleName());
		p[1]=new LongParameter("commentto","Comment To",getCommentto().longValue(),1,10000,1);
		p[2]=new TextParameter("text","Text",getText());
		
		return p;
	}
	
		public boolean setParameters(Parameter[] p) {
		boolean changed=false;
		
		for(int i=0;i<p.length;i++) {
			String name=p[i].getName();
			
			if(name.equals("path")) {
				BundleParameter pp=(BundleParameter)p[i];
				if(!getBundleName().equals(pp.getBundleName())) {
					//setBundleName(pp.getBundleName());
					// TODO: setBundle(pp.getBundleName());
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
			else if(name.equals("commentto")) {
				LongParameter ip=(LongParameter)p[i];
				if(getCommentto().longValue()!=ip.getLongValue()) {
					setCommentto(new Long(ip.getLongValue()));
					changed=true;
				}
			}
		}
		
		return changed;
	}
	
	public boolean isEditable() { return true; }
	
	public boolean isInfo()
	{
		return false;
	}
	
	public String batchCommand(Door door)
	{
		String path=door.getNativePath(Controller.getController().getGallery().getBundleManager().idToName(getBundleid()));
		
		StringBuffer sb=new StringBuffer();
		sb.append("j "+path+"\n");
		sb.append("com "+getCommentto()+"\n");
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

