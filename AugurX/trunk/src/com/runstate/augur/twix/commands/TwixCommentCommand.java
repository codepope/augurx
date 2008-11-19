/**
 * CommentCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.commands;



import com.runstate.augur.twix.sync.TwixSync;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.commands.Parameter;
import com.runstate.augur.gallery.commands.commands.CommentCommand;
import com.runstate.augur.gallery.commands.parameters.BundleParameter;
import com.runstate.augur.gallery.commands.parameters.LongParameter;
import com.runstate.augur.gallery.commands.parameters.TextParameter;
import com.runstate.util.StringW;
import com.runstate.util.ssh.SSHConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.Pattern;

public class TwixCommentCommand extends TwixCommand implements CommentCommand
{
	static final long serialVersionUID = -3875375477194393742L;
	
	String text;
	Long commentto;
	
	public TwixCommentCommand(Long doorid,Long bundleid,Long commentto,String text)
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
	
    @Override
	public String toString()
	{
		return "Twix:Comment to "+getBundleName()+":"+commentto;
	}
	
    @Override
		public String getCommandType()
	{
		return "Twix Comment";
	}
	
    @Override
	public Parameter[] getParameters()
	{
		Parameter[] p=new Parameter[3];
		
		p[0]=new BundleParameter("path","Path",getBundleName());
		p[1]=new LongParameter("commentto","Comment To",getCommentto().longValue(),1,10000,1);
		p[2]=new TextParameter("text","Text",getText());
		
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
	
    @Override
	public boolean isEditable() { return true; }
	
    @Override
	public boolean isInfo()
	{
		return false;
	}
	
	public String batchCommand(Door door)
	{
		return null;
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
            //Why this next line, and not door.getNativePath(getBundleName()) as used by TwixSayCommand?? TODO
		String path=door.getNativePath(Controller.getController().getGallery().getBundleManager().idToName(getBundleid()));
                String msgNumber="";

       		ssh.write("q\n q\n terse\n");
                ssh.waitFor("M:");
                ssh.write("opt term ec no Mark no q\n");
                ssh.waitFor("M:");
                ssh.write("clear\n");
                ssh.waitFor("M:");
                ssh.write("j "+path+"\n");
                ssh.waitFor("R:");
                ssh.write("com "+getCommentto()+"\n");
                ssh.waitFor(".<CR>'");
                ssh.write(StringW.wordWrap(getText(),70));
		ssh.write("\n");
		ssh.write(".\n");
                ssh.waitFor("A:");
                ssh.write("a\n");
                String messageDelivered = ssh.waitForAndReturnAll("added.");
                String realMsg = messageDelivered.substring(1, messageDelivered.indexOf("added"));
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

}

