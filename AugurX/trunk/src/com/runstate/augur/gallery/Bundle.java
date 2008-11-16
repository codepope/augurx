/**
 * PathNode.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery;

import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.models.StrandTreeModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

public class Bundle
{
	private static Logger log=Logger.getLogger("Bundle");
	
	Long bundleid=null;
	Long parentid=null;
	Long doorid=null;
	boolean container=false;
	String bundlename;
	int unread;
	int total;
	int warmunread;
	
	BundleManager bundlemanager=null;
	
	HashSet<Bundle> children=null;
	
	
	public Bundle(String path)
	{
		this.bundleid=null;
		this.doorid=null;
		this.bundlename = path;
		this.unread = 0;
		this.total = 0;
		this.warmunread = 0;
		this.container=false;
		this.parentid=null;
	}
	
	public Bundle(Long doorid,Long bundleid,String path,int total,int unread,int warmunread,boolean container,Long parentid)
	{
		this.doorid=doorid;
		this.bundleid=bundleid;
		this.bundlename=path;
		this.total=total;
		this.unread=unread;
		this.warmunread=warmunread;
		this.container=container;
		this.parentid=parentid;
		if(container)
		{
			children=new HashSet<Bundle>();
		}
	}
	
	/**
	 * Method delete
	 *
	 */
	public void delete()
	{
		// TODO
	}
	
	
	/**
	 * Method setMsgUnread
	 *
	 * @param id msg id
	 * @param b boolean
	 */
	public void setMsgUnread(Long id,boolean b)
	{
		Msg m=getMsg(id);
		
		if(m.isUnread()==b)
		{
			return; // Already unread
		}
		
		// Ok, it's a change, lets write it through
		
		if(strandtreemodel!=null)
		{
			strandtreemodel.setMsgUnread(id,b);
		}
		
		Controller.getGallery().setMsgUnread(bundleid,id,b);
		
		Strand s=getStrand(m.getRootKnotId());
		
		// Now we update the strand and bundle values
		
		if(b)
		{
			// Right, we have gone from read to unread
			s.setUnread(s.getUnread()+1);
			unread++;
			if(s.getHot()>0)
			{
				warmunread++;
			}
			
		}
		else
		{
			s.setUnread(s.getUnread()-1);
			unread--;
			if(s.getHot()>0)
			{
				warmunread--;
			}
		}
		
		updateStrand(s.getRootid(),s.getTotal(),s.getUnread(),s.getHot(),s.getIgnore(),s.getTagged());
		
		if(strandtreemodel!=null)
		{
			strandtreemodel.updateStrand(s.getRootid(),s.getTotal(),s.getUnread(),s.getHot());
		}
		
		bundlemanager.updateBundle(this);
	}
	
	public void setMsgHot(Long id,boolean b)
	{
		Msg m=getMsg(id);
		
		if(m.isHot()==b)
		{
			return; // Already unread
		}
		
		
		Strand s=getStrand(m.getRootKnotId());
		
		// Now we update the strand and bundle values
		
		if(b)
		{
			// Ok, a message has gone hot
			if(s.getHot()>0)
			{
				// There were already hot messages so we only bump the hot count
				s.setHot(s.getHot()+1);
			}
			else
			{
				s.setHot(s.getHot()+1);
				warmunread=warmunread+s.getUnread();
			}
		}
		else
		{
			s.setHot(s.getHot()-1);
			if(s.getHot()==0)
			{
				warmunread=warmunread-s.getUnread();
			}
		}
		
		updateStrand(s.getRootid(),s.getTotal(),s.getUnread(),s.getHot(),s.getIgnore(),s.getTagged());
		
		if(strandtreemodel!=null)
		{
			strandtreemodel.updateStrand(s.getRootid(),s.getTotal(),s.getUnread(),s.getHot());
		}
		
		// Ok, it's a change, lets write it through
		
		if(strandtreemodel!=null)
		{
			strandtreemodel.setMsgHot(id,b);
		}
		
		Controller.getGallery().setMsgHot(bundleid,id,b);
		
		bundlemanager.updateBundle(this);
	}
	
	public void setMsgTagged(Long id,boolean b)
	{
		Msg m=getMsg(id);
		
		if(m.isTagged()==b)
		{
			return; // Already set
		}
	
		Strand s=getStrand(m.getRootKnotId());
		
		// Now we update the strand and bundle values
		
		if(b)
		{
			s.setTagged(s.getTagged()+1);
		}
		else
		{
			s.setTagged(s.getTagged()-1);
		}
		
		updateStrand(s.getRootid(),s.getTotal(),s.getUnread(),s.getHot(),s.getIgnore(),s.getTagged());
		
		if(strandtreemodel!=null)
		{
			strandtreemodel.updateStrand(s.getRootid(),s.getTotal(),s.getUnread(),s.getHot());
		}
		
		// Ok, it's a change, lets write it through
		
		if(strandtreemodel!=null)
		{
			strandtreemodel.setMsgTagged(id,b);
		}
		
		Controller.getGallery().setMsgTagged(bundleid,id,b);
		
		bundlemanager.updateBundle(this);
	}
	/**
	 * Method setMsgIgnore
	 *
	 * @param    id           a  long
	 * @param     b           a boolean
	 */
	public void setMsgIgnore(Long id,boolean b)
	{
		Msg m=getMsg(id);
		
		if(m.isIgnore()==b)
		{
			return; // Already ignored
		}
		
		// Ok, it's a change, lets write it through
		Strand s=getStrand(m.getRootKnotId());
		
		// Now we update the strand and bundle values
		
		if(b)
		{
			s.setIgnore(s.getIgnore()+1);
		}
		else
		{
			s.setIgnore(s.getIgnore()-1);
		}
		
		updateStrand(s.getRootid(),s.getTotal(),s.getUnread(),s.getHot(),s.getIgnore(),s.getTagged());
		
		if(strandtreemodel!=null)
		{
			strandtreemodel.updateStrand(s.getRootid(),s.getTotal(),s.getUnread(),s.getHot());
		}
		
		Controller.getGallery().setMsgIgnore(bundleid,id,b);
		
		if(strandtreemodel!=null)
		{
			strandtreemodel.setMsgIgnore(id,b);
		}
		
		
		
	}
	
	/**
	 * Sets Doorid
	 *
	 * @param    doorid              a  long
	 */
	public void setDoorid(Long doorid)
	{
		this.doorid = doorid;
	}
	
	/**
	 * Returns Doorid
	 *
	 * @return    a  long
	 */
	public Long getDoorid()
	{
		return doorid;
	}
	
	public void addChild(Bundle b)
	{
		if(!container)
		{
			log.severe("Attempted to add child to non-container");
			return;
		}
		
		children.add(b);
		
		recalculate();
	}
	
	public void removeChild(Bundle b)
	{
		if(!container)
		{
			log.severe("Attempted to remove child from non-container");
			return;
		}
		children.remove(b);
	}
	
	public HashSet<Bundle> getChildren()
	{
		return children;
	}
	
	/**
	 * Sets Bundlemanager
	 *
	 * @param    bundlemanager       a  BundleManager
	 */
	public void setBundlemanager(BundleManager bundlemanager)
	{
		this.bundlemanager = bundlemanager;
	}
	
	/**
	 * Returns Bundlemanager
	 *
	 * @return    a  BundleManager
	 */
	public BundleManager getBundlemanager()
	{
		return bundlemanager;
	}
	
	/**
	 * Sets Parentid
	 *
	 * @param    parentid            a  long
	 */
	public void setParentid(Long parentid)
	{
		this.parentid = parentid;
	}
	
	/**
	 * Returns Parentid
	 *
	 * @return    a  long
	 */
	public Long getParentid()
	{
		return parentid;
	}
	
	/**
	 * Sets Container
	 *
	 * @param    container           a  boolean
	 */
	public void setContainer(boolean container)
	{
		this.container = container;
	}
	
	/**
	 * Returns Container
	 *
	 * @return    a  boolean
	 */
	public boolean isContainer()
	{
		return container;
	}
	
	/**
	 * Method setWarmunread
	 *
	 * @param    warmunread          an int
	 *
	 */
	public void setWarmunread(int warmunread)
	{
		this.warmunread = warmunread;
	}
	
	/**
	 * Returns Warmunread
	 *
	 * @return    an int
	 */
	public int getWarmunread()
	{
		return warmunread;
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
	 * Sets Path
	 *
	 * @param    bundlename                a  String
	 */
	public void setBundlename(String bundlename)
	{
		this.bundlename = bundlename;
	}
	
	/**
	 * Returns Path
	 *
	 * @return    a  String
	 */
	public String getBundlename()
	{
		return bundlename;
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
	 * Method getParentPath
	 *
	 * @return   an AugurPathRef
	 *
	 */
	public String getParentPath()
	{
		int i=bundlename.lastIndexOf('/');
		if(i==-1 || i==0) return "";
		String parentpath=bundlename.substring(0,i);
		
		return parentpath;
	}
	
	
	public String getBasename()
	{
		int i=bundlename.lastIndexOf('/');
		if(i==-1) return "";
		return bundlename.substring(i+1);
	}
	
	
	public String toString()
	{
		return "Bundle["+bundlename+","+bundleid+"("+parentid+"),unread="+unread+",warmunread="+warmunread+",total="+total+"]";
	}
	
	public synchronized boolean newMsg(Msg newmsg)
	{
		
		try
		{
			
			// Check that this message does not already exist
			
			Msg oldmsg=daGetMsg(newmsg.getKnotId());
			
			if(oldmsg!=null)
			{
				return false;
			}
			
			// Now we can start saving
			
			// First let us save the full text
			
			Long msgid=Controller.getGallery().saveText(newmsg.getText());
			
			newmsg.setMsgId(msgid);
			
			newmsg.setBundleId(getBundleid());
			
			// Here we will have to write in the message
			// ourselves
			Strand currentstrand=null;
			Msg parentmsg;
			Long commentto=newmsg.getCommentto();
			
			// First check if we are a new root
			
			if(commentto==null || (parentmsg=getMsg(commentto))==null)
			{
				// Create new strand for root
				newmsg.setRootKnotId(newmsg.getKnotId());
				currentstrand=new Strand(getBundleid(),newmsg.getKnotId());
				if(commentto!=null)
				{
					currentstrand.setCommentto(commentto);
				}
				persistStrand(currentstrand);
			}
			else
			{
				// Ok, we have a parent message...
				
				if(parentmsg.getFirstcomment()==null)
				{
					// No first comment. Update the first comment flag to point to this message
					parentmsg.setFirstcomment(newmsg.getKnotId());
					// and write back the updated version
					setFirstComment(parentmsg.getKnotId(),newmsg.getKnotId());
				}
				
				if(parentmsg.isIgnore())
				{
					newmsg.setUnread(false);
					newmsg.setIgnore(true);
				}
				
				// Write in the details
				newmsg.setRootKnotId(parentmsg.getRootKnotId());
				
				// Ok, let's see, we have a parent message but now we need to find the strand....
				// Let's look it up
				currentstrand=getStrand(parentmsg.getRootKnotId());
			}
			
			
			// Here we would calculate the change to the strand
			
			int stotal=currentstrand.getTotal();
			int sunread=currentstrand.getUnread();
			int shot=currentstrand.getHot();
			int signore=currentstrand.getIgnore();
			int stagged=currentstrand.getTagged();
			int swarm=currentstrand.getHot()>0?currentstrand.getUnread():0;
			int sowarm=swarm;
			
			stotal++;
			
			if(newmsg.isHot())
			{
				if(shot==0)
				{
					swarm=sunread;
//					heatedstrand=true;
				}
				shot++;
			}
			
			if(newmsg.isUnread())
			{
				sunread++;
				if(shot>0)
				{
					swarm++;
				}
			}
			
			if(newmsg.isIgnore())
			{
				signore++;
			}
			
			if(newmsg.isTagged())
			{
				stagged++;
			}
			
//			if(newmsg.isHot()) {
//				if(shot==0) {
//					heatedstrand=true;
//				}
//				shot++;
//			}
//
			persistMsg(newmsg); // Right, we can write this out
			
			updateStrand(currentstrand.getRootid(),stotal,sunread,shot,signore,stagged);
			
			
			// Update our copy of the strand
			currentstrand.setTotal(stotal);
			currentstrand.setUnread(sunread);
			currentstrand.setHot(shot);
			currentstrand.setTagged(stagged);
			currentstrand.setIgnore(signore);
			
			
			// And we move on to coalesce
			
			Long currrootid=currentstrand.getRootid();
			
			int ctotal=0;
			int cunread=0;
			int chot=0;
			int cwunread=0;
			int cignore=0;
			int ctagged=0;
	
			ArrayList<Strand> childstrands=getStrandsWhichAreACommentTo(newmsg.getKnotId());
			
			Iterator<Strand> i=childstrands.iterator();
			
			while(i.hasNext())
			{
				
				Strand childstrand=i.next();
				
				ctotal=ctotal+childstrand.getTotal();
				cunread=cunread+childstrand.getUnread();
				chot=chot+childstrand.getHot();
				if(childstrand.getHot()>0)
				{
					cwunread=cwunread+childstrand.getUnread();
				}
				ctagged=ctagged+childstrand.getTagged();
				cignore=cignore+childstrand.getIgnore();
				coalesce(currrootid,childstrand.getRootid());
			}
			
			total++;
			
			if(newmsg.isUnread()) unread++;
			
			if(shot>0 || chot>0) warmunread=warmunread+(sunread-sowarm)+(cunread-cwunread);
			
			bundlemanager.updateBundle(this);
			
			return true;
			
		}
		catch(NullPointerException npe)
		{
			System.out.println("npe:"+npe);
			npe.printStackTrace();
			return false;
		}
	}
	
	public synchronized void setBundleUnread(boolean b)
	{
		if(isContainer())
		{
			// Iterate children
			Iterator<Bundle> i=getChildren().iterator();
			
			while(i.hasNext())
			{
				Bundle cb=i.next();
				cb.setBundleUnread(b);
			}
		}
		else
		{
			ArrayList<Long> al=getRootids();
			
			// We can now iterate this list
			Iterator<Long> i=al.iterator();
			while(i.hasNext())
			{
				Long sb=i.next();
				setStrandUnread(sb,b);
			}
			
		}
		
		
	}
	
			
	public synchronized void setStrandWithMsgUnread(Long sb,boolean b)
	{
		Msg msg=getMsg(sb);
		setStrandUnread(msg.getRootKnotId(),b);
	}
	
	public synchronized void setStrandUnread(Long sb,boolean b)
	{
		// For a strand, we need to
		// (a) set all the message flags quickly
		// (b) set the strand values
		//         b - if(s.isHot) b.warmunread-=s.
		//				unreads.unread=total
		//				if(s.isHot) b.warmunread+=total
		//         !b   s.unread=0
		
		Strand s=getStrand(sb);
		
		if(b && s.getUnread()==s.getTotal()) return; // Already all unread
		
		if(!b && s.getUnread()==0) return; // Already all read
		
		// Now we need to make the warmunread adjustment
		
		unread=unread-s.getUnread();
		
		if(s.getHot()>0) warmunread=warmunread-s.getUnread();
		
		daSetStrandMsgsUnread(sb,b);
		
		if(b)
		{
			s.setUnread(s.getTotal());
		}
		else
		{
			s.setUnread(0);
		}
		
		updateStrand(s.getRootid(),s.getTotal(),s.getUnread(),s.getHot(),s.getIgnore(),s.getTagged());
		
		unread=unread+s.getUnread();
		
		if(s.getHot()>0) warmunread=warmunread+s.getUnread();
		
		bundlemanager.updateBundle(this);
		
	}
	
	public void daSetStrandMsgsUnread(Long sb,boolean b)
	{
		Controller.getGallery().setStrandMsgsUnread(bundleid,sb,b);
		
		if(strandtreemodel!=null) strandtreemodel.setStrandMsgsUnread(sb,b);
	}
	
	public synchronized void recalculate()
	{
		if(!container) return;
		
		int nunread=0;
		int ntotal=0;
		int nwarmunread=0;
		
		Iterator<Bundle> i=children.iterator();
		
		while(i.hasNext())
		{
			Bundle b=i.next();
			nunread=nunread+b.getUnread();
			ntotal=ntotal+b.getTotal();
			nwarmunread=nwarmunread+b.getWarmunread();
		}
		
		if(nunread!=unread || ntotal!=total || warmunread!=nwarmunread)
		{
			unread=nunread;
			total=ntotal;
			warmunread=nwarmunread;
			
			bundlemanager.updateBundle(this);
		}
		
		
	}
	
	
	public Door getDoor()
	{
		return bundlemanager.getDoor(doorid);
	}
	
	
	
	/******************************************************************
	 *
	 * This is the public API for the tree model and others to talk to
	 *
	 ******************************************************************/
	
	
	StrandTreeModel strandtreemodel;
	int users=0;
	
	public synchronized StrandTreeModel getStrandTreeModel()
	{
		if(strandtreemodel==null)
		{
			strandtreemodel=new StrandTreeModel(this);
		}
		
		users++;
		
		return strandtreemodel;
	}
	
	public synchronized void releaseStrandTreeModel()
	{
		users--;
		
		if(users==0)
		{
			strandtreemodel.clear();
			strandtreemodel=null;
		}
		
		
		
	}
	
	public Msg getMsg(Long spid)
	{
		if(strandtreemodel!=null)
		{
			return strandtreemodel.getMsg(spid);
		}
		
		return Controller.getGallery().getMsg(bundleid,spid);
	}
	
	public ArrayList<Long> getChildIds(Long spid)
	{
		return daGetChildIds(spid);
	}
	
	public ArrayList<Long> getStrandIds(Long spid)
	{
//		if(strandtreemodel!=null)
//		{
//			return strandtreemodel.getStrandIds(spid);
//		}
//
		return Controller.getGallery().getStrandIds(bundleid,spid);
	}
//
	// Wrappers for direct access
//
	
	public Msg daGetMsg(Long spid)
	{
//		System.out.println("Getting msg "+spid);
		return Controller.getGallery().getMsg(bundleid,spid);
	}
	
	public ArrayList<Long> daGetChildIds(Long rootid)
	{
//		System.out.println("Getting child msgs "+rootid);
		return Controller.getGallery().getChildIds(bundleid,rootid);
	}
	
	public ArrayList<Msg> daGetMsgsOnStrand(Long rootid)
	{
//		System.out.println("Getting strand msgs "+rootid);
		return Controller.getGallery().getMsgsOnStrand(bundleid,rootid);
	}
	
	public ArrayList<Long> getRootids()
	{
		if(strandtreemodel!=null)
		{
			return strandtreemodel.getRootids();
		}
		
		return Controller.getGallery().getRootids(bundleid);
	}
	
	
//	// Modifying
//
	public void setFirstComment(Long id,Long fcid)
	{
		Controller.getGallery().setFirstComment(bundleid,id,fcid);
		
		if(strandtreemodel!=null)
		{
			strandtreemodel.setFirstComment(id,fcid);
		}
	}
	
	public void persistMsg(Msg m)
	{
		Controller.getGallery().persistMsg(m);
		
		if(strandtreemodel!=null)
		{
			strandtreemodel.persistedMsg(m);
		}
	}
	
	public void coalesce(Long domid,Long subid)
	{
		Controller.getGallery().coalesce(bundleid,domid,subid);
		
		if(strandtreemodel!=null)
		{
			strandtreemodel.coalesced(domid,subid);
		}
	}
	
	public void persistStrand(Strand s)
	{
		Controller.getGallery().persistStrand(s);
		if(strandtreemodel!=null)
		{
			strandtreemodel.persistedStrand(s);
		}
	}
	
	public void updateStrand(Long strandid,int total,int unread,int hot,int ignore,int tagged)
	{
		Controller.getGallery().updateStrand(bundleid,strandid,total,unread,hot,ignore,tagged);
		
		if(strandtreemodel!=null)
		{
			strandtreemodel.updateStrand(strandid,total,unread,hot);
		}
	}
	
	public void deleteStrand(Long sid)
	{
		Controller.getGallery().deleteStrand(bundleid,sid);
		if(strandtreemodel!=null)
		{
			strandtreemodel.deleteStrand(sid);
		}
	}
	
	// Accessors
	
	public Strand getStrand(Long strandid)
	{
		if(strandtreemodel!=null)
		{
			return strandtreemodel.getStrand(strandid);
		}
		
		return Controller.getGallery().getStrand(bundleid,strandid);
	}
	
//	public ArrayList daGetStrandMsgs(Long rootid,boolean cached) {
//		if(!cached && strandtreemodel!=null) {
//			return strandtreemodel.getStrandMsgs(rootid);
//		}
//
//		return Controller.getGallery().getMsgsOnStrand(bundleid,rootid);
//	}
	
	public ArrayList<Strand> getStrandsWhichAreACommentTo(Long msgid)
	{
//		if(strandtreemodel!=null)
//		{
//			return strandtreemodel.getStrandsWhichAreACommentTo(msgid);
//		}
//
		return Controller.getGallery().getStrandsWhichAreACommentTo(bundleid,msgid);
	}
	
//	public void updateRootid(Long newrootid,Long oldrootid)
//	{
//		Controller.getGallery().updateRootid(bundleid,newrootid,oldrootid);
//		if(strandtreemodel!=null) strandtreemodel.updateRootid(newrootid,oldrootid);
//		return;
//	}
	
	public Long getParent(Long id)
	{
		if(strandtreemodel!=null) return strandtreemodel.getParent(id);
		
		return Controller.getGallery().getCommentTo(bundleid,id);
	}
	
	
	public boolean hasComments(Long id)
	{
		Msg m=Controller.getGallery().getMsg(bundleid,id);
		return m.getFirstcomment()!=null;
	}
	
	public boolean isUnread(Long id)
	{
		if(strandtreemodel!=null)
		{
			return strandtreemodel.isUnread(id);
		}
		
		return Controller.getGallery().isUnread(bundleid,id);
	}
	
	public boolean isHot(Long id)
	{
		if(strandtreemodel!=null)
		{
			return strandtreemodel.isHot(id);
		}
		
		return Controller.getGallery().isHot(bundleid,id);
	}

    public ArrayList<Msg> search(String searchTerm) {
       if(isContainer())
       {
           ArrayList<Msg> results=new ArrayList<Msg>();
           
           for(Bundle b:getChildren())
           {
               results.addAll(b.search(searchTerm));
           }
           
           return results;
       }
       
       return Controller.getGallery().searchStrand(bundleid,searchTerm);
    }
	
	
}


//		try {
//
//			// Check that this message does not already exist
//
//			Msg oldmsg=daGetMsg(newmsg.getKnotId());
//
//			if(oldmsg!=null) {
//				return false;
//			}
//
//			// Now we can start saving
//
//			// First let us save the full text
//
//			Long msgid=Controller.getGallery().saveText(newmsg.getText());
//
//			newmsg.setMsgId(msgid);
//
//			newmsg.setBundleId(getBundleid());
//
//			// Here we will have to write in the message
//			// ourselves
//			Strand currentstrand=null;
//			boolean heatedstrand=false;
//			Msg parentmsg;
//			Long commentto=newmsg.getCommentto();
//
//			if(commentto==null || (parentmsg=getMsg(commentto))==null) {
//				// Create new strand for root
//				newmsg.setRootKnotId(newmsg.getKnotId());
//				currentstrand=new Strand(newmsg);
//				persistMsg(newmsg);
//				persistStrand(currentstrand);
//			}
//			else {
//				// Ok, we have a parent message...
//
//				if(parentmsg.getFirstcomment()==null) {
//					// No first comment. Update the first comment flag to point to this message
//					parentmsg.setFirstcomment(newmsg.getKnotId());
//					// and write back the updated version
//					setFirstComment(parentmsg.getKnotId(),newmsg.getKnotId());
//				}
//
//				// Ok, let's see, we have a parent message but now we need to find the strand....
//
//				// Let's look it up
//				currentstrand=getStrand(parentmsg.getRootKnotId());
//
//				if (currentstrand==null) {
//					System.out.println("Woah");
//				}
//
//				// Here we would calculate the change to the strand
//
//				int stotal=currentstrand.getTotal();
//				int sunread=currentstrand.getUnread();
//				int shot=currentstrand.getHot();
//
//				stotal++;
//
//				if(newmsg.isUnread()) {
//					sunread++;
//				}
//
//				if(newmsg.isHot()) {
//					if(shot==0) heatedstrand=true;
//					shot++;
//				}
//
//				newmsg.setRootKnotId(parentmsg.getRootKnotId());
//
//				persistMsg(newmsg);
//				updateStrand(parentmsg.getRootKnotId(),stotal,sunread,shot);
//
//				currentstrand.setTotal(stotal);
//				currentstrand.setUnread(sunread);
//				currentstrand.setHot(shot);
//
//			}
//
//			int cstotal=currentstrand.getTotal();
//			int csunread=currentstrand.getUnread();
//			int cshot=currentstrand.getHot();
//
//			int ctotal=0;
//			int cunread=0;
//			int chot=0;
//
//			boolean changed=false;
//
//			ArrayList childmsgs=getStrandsWhichAreACommentTo(newmsg.getKnotId());
//			//ArrayList childmsgs=new ArrayList();
//
//			Iterator i=childmsgs.iterator();
//
//			Long currrootid=currentstrand.getRootid();
//
//			while(i.hasNext()) {
//
//				Strand childstrand=(Strand)i.next();
//
//				ctotal=ctotal+childstrand.getTotal();
//				cunread=cunread+childstrand.getUnread();
//				chot=chot+childstrand.getHot();
//
//				coalesce(currrootid,childstrand.getRootid());
//			}
//
//			total++; // Whatever, this is a new message
//
//			if(newmsg.isUnread()) {
//				unread++;
//
//				// Special circs here. Msg may be first hot added to strand, so
//
//				if(cshot==0 && chot>0) // this strand has been heated
//				{
//					warmunread=warmunread+cunread+1;
//					//currentstrand.getUnread(); // Add all the unread to warm unread
//				} else if(cshot>0 && chot==0)
//				{
//					warmunread=warmunread+csunread+1;
//				}
//				else {
//					if((chot+cshot)>0) warmunread++; // If strand is already hot, just bump the warmunread
//				}
//			}
//
//			bundlemanager.updateBundle(this);
//
//			return true;
//
//		}
//		catch(NullPointerException npe) {
//			System.out.println("npe:"+npe);
//			npe.printStackTrace();
//			return false;
//		}
