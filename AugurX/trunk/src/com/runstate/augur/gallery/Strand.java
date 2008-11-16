/**
 * AugurThread.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery;
import com.runstate.augur.AugurX;
import com.runstate.augur.gallery.Msg;
import java.util.Vector;



public class Strand
{
	Long bundleid;
	Long rootid;
	int unread;
	int hot;
	int tagged;
	int keep;
	int ignore;
	int deleted;
	int total;
	Long commentto;
	
	boolean loaded=false;
	

	/**
	 * Constructor
	 *
	 * @param    bundleid              a  long
	 * @param    rootid              a  long
	 * @param    unread              an int
	 * @param    hot                 an int
	 * @param    action              an int
	 * @param    keep                an int
	 * @param    ignore              an int
	 * @param    deleted             an int
	 * @param    total               an int
	 */
	public Strand(Long bundleid, Long rootid, int unread, int hot, int action, int keep, int ignore, int deleted, int total,Long commentto)
	{
		this.bundleid = bundleid;
		this.rootid = rootid;
		this.unread = unread;
		this.hot = hot;
		this.tagged = action;
		this.keep = keep;
		this.ignore = ignore;
		this.deleted = deleted;
		this.total = total;
		this.commentto=commentto;
	}
	
	public Strand(Msg m)
	{
		this.bundleid=m.getBundleId();
		this.rootid=m.getKnotId();
		this.hot=m.isHot()?1:0;
		this.unread=m.isUnread()?1:0;
		this.tagged=m.isTagged()?1:0;
		this.keep=m.isKeep()?1:0;
		this.ignore=m.isIgnore()?1:0;
		this.deleted=m.isDeleted()?1:0;
		this.total=1;
		this.commentto=m.getCommentto();
	}
		
	public Strand(Long bundleid,Long rootid)
	{
		this.bundleid=bundleid;
		this.rootid=rootid;
		this.hot=0;
		this.unread=0;
		this.tagged=0;
		this.keep=0;
		this.ignore=0;
		this.total=0;
		this.commentto=null;
	}
		
	/**
	 * Method mergeStrand
	 *
	 * @param    cstrand             a  Strand
	 *
	 */
	public void mergeStrand(Strand cstrand)
	{
		hot+=cstrand.getHot();
		unread+=cstrand.getUnread();
		tagged+=cstrand.getTagged();
		keep+=cstrand.getKeep();
		ignore+=cstrand.getIgnore();
		deleted+=cstrand.getDeleted();
		total+=cstrand.getTotal();
	}
	


	/**
	 * Sets Deleted
	 *
	 * @param    deleted             an int
	 */
	public void setDeleted(int deleted)
	{
		this.deleted = deleted;
	}
	
	/**
	 * Returns Deleted
	 *
	 * @return    an int
	 */
	public int getDeleted()
	{
		return deleted;
	}
	
	/**
	 * Sets Pathid
	 *
	 * @param    pathid              a  long
	 */
	public void setBundleid(Long pathid)
	{
		this.bundleid = pathid;
	}
	
	/**
	 * Returns Pathid
	 *
	 * @return    a  long
	 */
	public Long getBundleid()
	{
		return bundleid;
	}
	
	/**
	 * Sets Rootid
	 *
	 * @param    rootid              a  long
	 */
	public void setRootid(Long rootid)
	{
		this.rootid = rootid;
	}
	
	/**
	 * Returns Rootid
	 *
	 * @return    a  long
	 */
	public Long getRootid()
	{
		return rootid;
	}
	
	/**
	 * Sets Unread
	 *
	 * @param    unread              an int
	 */
	public void setUnread(int unread)
	{
		this.unread = unread;
	}
	
	/**
	 * Returns Unread
	 *
	 * @return    an int
	 */
	public int getUnread()
	{
		return unread;
	}
	
	/**
	 * Sets Hot
	 *
	 * @param    hot                 an int
	 */
	public void setHot(int hot)
	{
		this.hot = hot;
	}
	
	/**
	 * Returns Hot
	 *
	 * @return    an int
	 */
	public int getHot()
	{
		return hot;
	}
	
	/**
	 * Sets Action
	 *
	 * @param    action              an int
	 */
	public void setTagged(int action)
	{
		this.tagged = action;
	}
	
	/**
	 * Returns Action
	 *
	 * @return    an int
	 */
	public int getTagged()
	{
		return tagged;
	}
	
	/**
	 * Sets Keep
	 *
	 * @param   keep                an int
	 */
	public void setKeep(int keep)
	{
		this.keep = keep;
	}
	
	/**
	 * Returns Keep
	 *
	 * @return    an int
	 */
	public int getKeep()
	{
		return keep;
	}
	
	/**
	 * Sets Ignore
	 *
	 * @param    ignore              an int
	 */
	public void setIgnore(int ignore)
	{
		this.ignore = ignore;
	}
	
	/**
	 * Returns Ignore
	 *
	 * @return    an int
	 */
	public int getIgnore()
	{
		return ignore;
	}
	
	/**
	 * Sets Total
	 *
	 * @param    total               an int
	 */
	public void setTotal(int total)
	{
		this.total = total;
	}
	
	/**
	 * Returns Total
	 *
	 * @return    an int
	 */
	public int getTotal()
	{
		return total;
	}
	
	
	/**
	 * Sets Commentto
	 *
	 * @param    commentto           a  long
	 */
	public void setCommentto(Long commentto)
	{
		this.commentto = commentto;
	}
	
	/**
	 * Returns Commentto
	 *
	 * @return    a  long
	 */
	public Long getCommentto()
	{
		return commentto;
	}
	
	public String toString()
	{
		return "Strand[bundleid="+bundleid+",rootid="+rootid+",total="+total+",unread="+unread+",hot="+hot+",loaded="+loaded+"]";
	}
	
		/**
	 * Sets Loaded
	 *
	 * @param    loaded              a  boolean
	 */
	public void setLoaded(boolean loaded)
	{
		this.loaded = loaded;
	}
	
	/**
	 * Returns Loaded
	 *
	 * @return    a  boolean
	 */
	public boolean isLoaded()
	{
		return loaded;
	}
}

//	public void updateMsg(Msg msg)
//	{
//		Msg oldmsg=Controller.getGallery().getMsg(bundleid,msg.getKnotId());
//
//		internalUpdateMsg(msg,oldmsg);
//	}


//	public void newMsg(Msg msg)
//	{
//		internalUpdateMsg(msg,null);
//	}
//
//
//	public void internalUpdateMsg(Msg msg,Msg oldmsg)
//	{
//
//		if(oldmsg==null || msg.isUnread()!=oldmsg.isUnread())
//		{
//			if(msg.isUnread())
//			{
//				unread++;
//			}
//			else if(oldmsg!=null)
//			{
//				unread--;
//			}
//		}
//
//		if(oldmsg==null || msg.isHot()!=oldmsg.isHot())
//		{
//			if(msg.isHot())
//			{
//				hot++;
//			}
//			else if(oldmsg!=null)
//			{
//				hot--;
//			}
//		}
//
//		if(oldmsg==null || msg.isKeep()!=oldmsg.isKeep())
//		{
//			if(msg.isKeep()) keep++;
//			else keep--;
//		}
//
//		if(oldmsg==null || msg.isIgnore()!=oldmsg.isIgnore())
//		{
//			if(msg.isIgnore()) ignore++;
//			ignore--;
//		}
//
//		if(oldmsg==null || msg.isActionable()!=oldmsg.isActionable())
//		{
//			if(msg.isActionable()) action++;
//			else action--;
//		}
//
//		if(oldmsg==null || msg.isDeleted()!=oldmsg.isDeleted())
//		{
//			if(msg.isDeleted()) deleted++;
//			else deleted--;
//		}
//
//		if(oldmsg==null)
//		{
//			total++;
//		}
//
//	}

