/**
 * Msg.java
 *
 * @author Dj
 */
package com.runstate.augur.gallery;

import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import java.io.BufferedReader;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Msg
{
	public static final long NO_ID=-1L;

	// Will be persisted
	
	Long msgId=null;
	Long bundleId=null;
	Long knotId=null;
	Timestamp msgdate;
	String author="";
	String subject="";
	Long commentto=null;
	boolean unread=false;
	boolean keep=false;
	boolean ignore=false;
	boolean hot=false;
	boolean hotthread=false;
	boolean tagged=false;
	Long rootKnotId=null;
	boolean unreadthread=false;
	String sourceid=null;
	int bodystart=-1;
	boolean deleted=false;
	Long firstcomment=null;
	
	String text;

//	BundleRef bundleRef=null;
	Door door=null;
	Long doorid=null;
	boolean isParsed=false;
	String textpath=null;
	

	public Msg(Door door,String text)
	{
		this.text=text;
		door.parseReader(this,new BufferedReader(new StringReader(text)));
		this.door=door;
		this.doorid=door.getDoorid();
	}
	
	public Msg(Door door)
	{
		this.door=door;
	}
	
        public Door getDoor()
        {
            return this.door;
        }
	/**
	 * Sets Textpath
	 *
	 * @param    textpath            a  String
	 */
	public void setTextpath(String textpath)
	{
		this.textpath = textpath;
	}
	
	/**
	 * Returns Textpath
	 *
	 * @return    a  String
	 */
	public String getTextpath()
	{
		return textpath;
	}
	
	/**
     * returns the name of the bundle
     * @return bundle name
     */
    public String getBundleName()
	{
		return Controller.getController().getGallery().getBundleManager().getBundle(bundleId).getBundlename();
	}
	
	/**
	 * Sets Firstcomment
	 *
	 * @param    firstcomment        a  long
	 */
	public void setFirstcomment(Long firstcomment)
	{
		this.firstcomment = firstcomment;
	}
	
	/**
	 * Returns Firstcomment
	 *
	 * @return    a  long
	 */
	public Long getFirstcomment()
	{
		return firstcomment;
	}
	

	
	/**
	 * Sets Deleted
	 *
	 * @param    deleted             a  boolean
	 */
	public void setDeleted(boolean deleted)
	{
		this.deleted = deleted;
	}
	
	/**
	 * Returns Deleted
	 *
	 * @return    a  boolean
	 */
	public boolean isDeleted()
	{
		return deleted;
	}
	
	/**
	 * Method isRoot
	 *
	 * @return   a  boolean
	 */
	public boolean isRoot()
	{
		return knotId.equals(rootKnotId);
	}

	/**
	 * Sets Rootid
	 *
	 * @param    rootid              a  long
	 */
	public void setRootKnotId(Long rootid)
	{
		this.rootKnotId = rootid;
	}
	
	/**
	 * Returns Rootid
	 *
	 * @return    a  long
	 */
	public Long getRootKnotId()
	{
		return rootKnotId;
	}
	
	/**
	 * Returns Unreadthread
	 *
	 * @return    a  boolean
	 */
	public boolean isUnreadthread()
	{
		if(isUnread()) return true;
		
		Strand at=Controller.getGallery().getStrand(bundleId,rootKnotId);

//		System.out.println("unread at "+at);
		
		return at.getUnread()>0;
	}
	
	/**
	 * Sets Hot
	 *
	 * @param    hot                 a  boolean
	 */
	public void setHot(boolean hot)
	{
		this.hot = hot;
	}
	
	/**
	 * Returns Hot
	 *
	 * @return    a  boolean
	 */
	public boolean isHot()
	{
		return hot;
	}
	
	/**
	 * Returns Hotthread
	 *
	 * @return    a  boolean
	 */
	public boolean isWarm()
	{
		if(isHot()) return true;
		
//		System.out.println("Getting "+pathid+" "+rootid);
		Strand at=Controller.getController().getGallery().getBundleManager().getBundle(bundleId).getStrand(rootKnotId);
//		System.out.println("hothread at"+at);
		
		if(at==null) return false;
		
		return at.getHot()>0;
	}
	
	/**
	 * Sets Tagged
	 *
	 * @param    tagged          a  boolean
	 */
	public void setTagged(boolean tagged)
	{
		this.tagged = tagged;
	}
	
	/**
	 * Returns Actionable
	 *
	 * @return    a  boolean
	 */
	public boolean isTagged()
	{
		return tagged;
	}
	

	/**
     * Sets msg id
     * @param msgid
     */
    public void setMsgId(Long msgid)
	{
		this.msgId = msgid;
	}
	
	/**
	 * Returns Msgid
	 *
	 * @return    a  long
	 */
	public Long getMsgId()
	{
		return msgId;
	}
	
	/**
     * Set Bundleid
     * @param bundleid a long
     */public void setBundleId(Long bundleid)
	{
		this.bundleId = bundleid;
	}
	
	/**
	 * Returns Pathid
	 *
	 * @return    a  long
	 */
	public Long getBundleId()
	{
		return bundleId;
	}
	
	/**
	 * Sets Spid
	 *
	 * @param    knotId                a  long
	 */
	public void setKnotId(Long knotId)
	{
		this.knotId = knotId;
	}
	
	/**
	 * Returns Spid
	 *
	 * @return    a  long
	 */
	public Long getKnotId()
	{
		return knotId;
	}
	
	/**
	 * Sets Msgdate
	 *
	 * @param    msgdate             a  Timestamp
	 */
	public void setMsgdate(Timestamp msgdate)
	{
		this.msgdate = msgdate;
	}
	
	/**
	 * Returns Msgdate
	 *
	 * @return    a  Timestamp
	 */
	public Timestamp getMsgdate()
	{
		return msgdate;
	}
	
	/**
	 * Sets Author
	 *
	 * @param    author              a  String
	 */
	public void setAuthor(String author)
	{
		this.author = author;
	}
	
	/**
	 * Returns Author
	 *
	 * @return    a  String
	 */
	public String getAuthor()
	{
		return author;
	}
	
	/**
	 * Sets Subject
	 *
	 * @param    subject             a  String
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
	}
	
	/**
	 * Returns Subject
	 *
	 * @return    a  String
	 */
	public String getSubject()
	{
		return subject;
	}
	


	/**
	 * Sets Keep
	 *
	 * @param    keep                a  boolean
	 */
	public void setKeep(boolean keep)
	{
		this.keep = keep;
	}
	
	/**
	 * Returns Keep
	 *
	 * @return    a  boolean
	 */
	public boolean isKeep()
	{
		return keep;
	}
	
	/**
	 * Sets Ignore
	 *
	 * @param    ignore              a  boolean
	 */
	public void setIgnore(boolean ignore)
	{
		this.ignore = ignore;
	}
	
	/**
	 * Returns Ignore
	 *
	 * @return    a  boolean
	 */
	public boolean isIgnore()
	{
		return ignore;
	}
	
	/**
	 * Sets Sourceid
	 *
	 * @param    sourceid            a  String
	 */
	public void setSourceid(String sourceid)
	{
		this.sourceid = sourceid;
	}
	
	/**
	 * Returns Sourceid
	 *
	 * @return    a  String
	 */
	public String getSourceid()
	{
		return sourceid;
	}
	
	/**
	 * Sets Bodystart
	 *
	 * @param    bodystart           an int
	 */
	public void setBodystart(int bodystart)
	{
		this.bodystart = bodystart;
	}
	
	/**
	 * Returns Bodystart
	 *
	 * @return    an int
	 */
	public int getBodystart()
	{
		return bodystart;
	}


	public String getAugurAddress()
	{
	//	Door door=Augur.getController().getDoorByPath(bundleRef);
		return getAuthor()+"@"+door.getDoorname();
	}
	

	

	
	public void setText(String text)
	{
		this.text=text;
	}
	
	public String getText()
	{
		if(text==null)
		{
			text=Controller.getGallery().getText(msgId);
		}
		
		return text;
	}
	
	public void setMsgDate(Timestamp msgdate)
	{
		this.msgdate = msgdate;
	}
	
	public Timestamp getMsgDate()
	{
		return msgdate;
	}
	
	public void setCommentto(Long commentto)
	{
		this.commentto = commentto;
	}
	
	public Long getCommentto()
	{
		return commentto;
	}
		
	public String toString()
	{
		return "Msg(msgid="+getMsgId()+",pathid="+getBundleId()+",spid="+getKnotId()+","+getAuthor()+","+getCommentto()+","+isUnread()+"]";
	}
	

	public boolean isUnread()
	{
		return unread;
	}
	
	public void setUnread(boolean f)
	{
		unread=f;
	}
	
	
	public String getBody()
	{
		if(text==null)
		{
			getText();
		}
		
		return text.substring(bodystart);
	}

	
	public String getDisplayHTML()
	{
//		if(door==null)
//		{
//			door=Augur.getController().getDoorByPath(getBundleRef());
//		}
//
		return door.getBodyHTML(this);
	}
	
	public String getURL()
	{
		return door.getURL(this);
	}
	
//	public boolean isWarmUnread()
//	{
//		return (isWarm() && isUnread());
//	}
	
//	public boolean isRootMessage()
//	{
//		return getCommentto()==null;
//	}
	
	boolean loaded=false;
	
	ArrayList<Long> children=null;
	
	public void addChild(Long id)
	{
		if(children==null) children=new ArrayList<Long>();
		
		children.add(id);
	}
	
	public int getChildCount()
	{
		if(children==null) return 0;
		
		return children.size();
	}
	
	public Long getChildAt(int i)
	{
		if(children==null) return null;
		return children.get(i);
	}
	
	public boolean isLoaded()
	{
		return loaded;
	}
	
	public void setLoaded(boolean loaded)
	{
		this.loaded=loaded;
	}
}

