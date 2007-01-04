/**
 * NewStrandTreeModel.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.models;

import com.runstate.augur.gallery.*;
import java.util.*;

import com.runstate.augur.controller.Controller;
import com.runstate.util.swing.AbstractTreeModel;
import javax.swing.tree.TreePath;

public class StrandTreeModel extends AbstractTreeModel implements BundleUser  {
	
	Bundle bundle=null;
	
	public StrandTreeModel(Bundle bundle)  {
		this.bundle=bundle;
		loadStrands();
	}
	
	HashMap<Long, StrandTreeModel.MsgAdapter> msgadapters=new HashMap<Long, StrandTreeModel.MsgAdapter>();
	LinkedList<Strand> strands=new LinkedList<Strand>();
	HashMap<Long, Strand> strandsbyid=new HashMap<Long, Strand>();
	
	Long root=new Long(-99999);
	
	public void coalesced(Long domid, Long subid) {
		// We refer to the message
		Msg dommsg=getMsg(domid);
		// And fromt that get the strand
		
		Strand domstrand=getStrand(dommsg.getRootKnotId());
		
		if(domstrand==null) {
			System.out.println("No dom strand! "+dommsg.getRootKnotId());
		}
		
		Strand substrand=getStrand(subid); 										// Get the strand
		
		
		int pos=getIndexOfChild(root,subid);									// Locate where it is in the root
		
		updateRootid(subid,dommsg.getRootKnotId());
		
		MsgAdapter ma=msgadapters.get(domid);
		
		ma.addChild(subid);
		
		deleteStrand(subid);
		
		//	fireChildRemoved(getTreePath(root),getIndexOfChild(root,subid),subid);
		
		domstrand.setTotal(domstrand.getTotal()+substrand.getTotal());
		domstrand.setUnread(domstrand.getUnread()+substrand.getUnread());
		domstrand.setHot(domstrand.getHot()+substrand.getHot());
		
		updateStrand(domid,domstrand.getTotal(),domstrand.getUnread(),domstrand.getHot());
		
		if(dommsg.getFirstcomment()==null) {
			setFirstComment(domid,subid);
		}
		
		fireChildAdded(getTreePath(domid),getIndexOfChild(domid,subid),subid);
	}
	/**
	 * Method setMsgUnread
	 *
	 * @param    id                  a  Long
	 * @param    b                   a  boolean
	 *
	 */
	public void setMsgUnread(Long id, boolean b)  {
		Msg msg=getMsg(id);
		msg.setUnread(b);
		
		Object pa=getParent(id);
		int i=getIndexOfChild(pa,id);
		
		fireChildChanged(getTreePath((Long)pa),i,id);
	}
	
	public void setMsgHot(Long id, boolean b)  {
		Msg msg=getMsg(id);
		msg.setHot(b);
		
		
		Object pa=getParent(id);
		int i=getIndexOfChild(pa,id);
		
		fireChildChanged(getTreePath((Long)pa),i,id);
	}
	
	public void setMsgIgnore(Long id, boolean b)
	{
		Msg msg=getMsg(id);
		msg.setIgnore(b);
	}
	
	public void setMsgTagged(Long id, boolean b)
	{
		Msg msg=getMsg(id);
		msg.setTagged(b);
	}
	
	/**
	 * Method setFirstComment
	 *
	 * @param    id                  a  Long
	 * @param    fcid                a  Long
	 *
	 */
	public void setFirstComment(Long id, Long fcid)  {
		Msg m=getMsg(id);
		m.setFirstcomment(fcid);
	}
	

//	public ArrayList getStrandMsgs(Long rootid)  {
//		System.out.println("Returning null from getStrandMsgs! BAD!");
//		return null;
//	}
	
	public void updateRootid(Long oldrootid,Long newrootid) {
		synchronized(msgadapters) {
			Iterator<StrandTreeModel.MsgAdapter> i=msgadapters.values().iterator();
			
			while(i.hasNext()) {
				MsgAdapter mi=i.next();
				
				if(mi.getMsg().getRootKnotId().equals(oldrootid)) {
					mi.getMsg().setRootKnotId(newrootid);
				}
			}
		}
		
	}
	
	public ArrayList<Long> getRootids() {
		ArrayList<Long> al=new ArrayList<Long>(strandsbyid.keySet());
		Collections.sort(al);
		return al;
	}
	
	//
	//
	// Methods which equate to write actions on the database, which will modify the cache
	//
	
	public void persistedStrand(Strand s)  {
		putStrand(s);
	}
	
	
	
	public void updateStrand(Long sid,int total,int unread,int hot)  {
		Strand s=getStrand(sid);
		s.setTotal(total);
		s.setUnread(unread);
		s.setHot(hot);
	}
	
	public void setStrandMsgsUnread(Long sid,boolean b) {
		Iterator<StrandTreeModel.MsgAdapter> i=msgadapters.values().iterator();
		
		while(i.hasNext()) {
			MsgAdapter ma=i.next();
			
			if(ma.getMsg().getRootKnotId().equals(sid)) {
				if(b!=ma.getMsg().isUnread()) {
					ma.getMsg().setUnread(b);
					
					Object pa=getParent(ma.getMsg().getKnotId());
					int j=getIndexOfChild(pa,ma.getMsg().getKnotId());
					
					fireChildChanged(getTreePath((Long)pa),j,ma.getMsg().getKnotId());
				}
			}
		}
		
	}
	
	public void persistedMsg(Msg m)  {
		putMsg(m);
	}
	
	
	public Long getNextUnread(Long startat,boolean warmonly) {
		// First descend
		if(startat==null || startat.equals(root)) {
			if(strands.size()==0) return null; // Fast out here
			startat=strands.get(0).getRootid();
		}
		
		if(isUnreadStrand(startat)) {
			// Locate the next unread in this thread
			Long id=descendForUnread(startat,warmonly);
			
			if(id!=null) {
				return id;
			}
		}
		
		// No, don't descend till we've got the strand
		Msg m=getMsg(startat);
		Long stid=m.getRootKnotId();
		Strand s=getStrand(stid);
		
		// Now locate this in the strand table
		
		int startstrand=strands.indexOf(s);
		int stindex=startstrand;
		
		while(stindex<strands.size()) {
			Strand tmpst=strands.get(stindex);
			if(tmpst.getUnread()>0) {
				Long id=descendForUnread(tmpst.getRootid(),warmonly);
				if(id!=null) return id;
			}
			stindex++;
		}
		
		stindex=0;
		
		while(stindex<startstrand) {
			Strand tmpst=strands.get(stindex);
			if(tmpst.getUnread()>0) {
				Long id=descendForUnread(tmpst.getRootid(),warmonly);
				if(id!=null) return id;
			}
			stindex++;
		}
		
		return null;
		
	}
	
	private Long descendForUnread(Long startat,boolean warmonly)  {
		// Descend the tree and exit on u - unread, w - warm
		
		if(!(startat.equals(root)) && isUnread(startat))  {
			if(warmonly)
			{ if(isWarm(startat)) {
					return startat;
				}
			}
			else {
				return startat;
			}
		}
		
		int cc=getChildCount(startat);
		
		if(cc==0)  {
			// No children
			return null;
		}
		
		for(int i=0;i<cc;i++)  {
			Long c=(Long)getChild(startat,i);
			
			Long rs=descendForUnread(c,warmonly);
			
			if(rs!=null) return rs;
		}
		
		// Right thats it, I got nothing....
		
		return null;
	}
	
	
	public Object getRoot()  {
		return root;
	}
	
	public void valueForPathChanged(TreePath path, Object newValue)  {
		// TODO
	}
	
	public Long getParent(Long id)  {
		if(id==root) return null;
		
		//	MsgAdapter mh=((id);
		Msg m=getMsg(id);
		
		if(m.isRoot()) return root;
		
		Long ct=m.getCommentto();
		
		if(ct==null) return root;
		
		return ct;
	}
	
	public boolean isUnreadStrand(Long id)  {
		if(id==root)  {
			return bundle.getUnread()>0;
		}
		
		Strand st=getStrand(id);
		
		if(st!=null)  {
			return (st.getUnread()>0);
		}
		
		Msg m=getMsg(id);
		
		st=getStrand(m.getRootKnotId());
		
		return st.getUnread()>0;
	}
	
	public boolean isWarm(Long id)  {
		Msg m=getMsg(id);
		
		if(m==null) return false;
		
		if(m.isWarm()) return true;
		
		return false;
	}
	
	public boolean isHot(Long id)  {
		if(id==root) return false;
		
		Msg m=getMsg(id);
		
		if(m==null)  {
			return false;
		}
		
		return m.isHot();
	}
	
	public boolean isUnread(Long id)  {
		if(id==root) return false;
		
		Msg m=getMsg(id);
		
		if(m==null)  {
			return false;
		}
		
		return m.isUnread();
	}
	
	public void deleteStrand(Long sid) {
		int i;
		synchronized(strands) {
			Strand s=strandsbyid.get(sid);
			i=strands.indexOf(s);
			Object x=strandsbyid.remove(sid);
			if(x==null) {
				System.out.println("Woah");
			}
			strands.remove(i);
		}
		fireChildRemoved(getTreePath(root),i,sid);
	}
	
	
	
	public TreePath getTreePath(Long id)  {
		ArrayList<Long> buildpath=new ArrayList<Long>();
		Long sid=id;
		
		while(sid!=null)  {
			buildpath.add(0,sid);
			sid=getParent(sid);
		}
		
		return new TreePath((Long[])buildpath.toArray(new Long[buildpath.size()]));
	}
	
	public boolean isLeaf(Object id)  {
		// Right let's get this party started
		
		if(id.equals(root))  {
			// This is the root
			// Look at the strands
			if(strands.size()==0) return true; // No children as no strands
			
			return false;
		}
		
		// Ok, we've handled the root. Now, we try and get the message
		
		Msg m=getMsg((Long)id);
		
		if(m==null) return false;
		
		if(m.isRoot()) {
			Strand s=getStrand((Long)id);
			if(s==null) {
				System.out.println("Woah!");
				return false;
			}
			return !(s.getTotal()>1);
		}
		
		// The adapter should be populated now....
		
		MsgAdapter mh=msgadapters.get((Long)id); // Check the cache
		
		// If it ain't we got bigger problem
		
		return !mh.hasChildren();
	}
	
	public Strand getStrand(Long id)  {
		return strandsbyid.get(id);
	}
	
	
	public void clear()  {
		msgadapters=null;
		strands=null;
		strandsbyid=null;
	}
	
	public void loadStrands()  {
		ArrayList<Strand> al=Controller.getGallery().getStrands(bundle.getBundleid());
		
		strands.clear();
		strandsbyid.clear();
		
		Iterator<Strand> i=al.iterator();
		
		while(i.hasNext())  {
			Strand s=i.next();
			
			putStrand(s);
		}
	}
	
	void putStrand(Strand s) {
		int ia=0;
		
		synchronized(strands) {
			Iterator<Strand> i=strands.iterator();
			
			
			while(i.hasNext()) {
				Strand is=i.next();
				
				if(s.getRootid().longValue()<is.getRootid().longValue()) {
					break;
				}
				
				ia++;
			}
			
			strands.add(ia,s);
			
			strandsbyid.put(s.getRootid(),s);
		}
		
		fireChildAdded(new TreePath(root),ia,s.getRootid());
	}
	
	/**
	 * Method getIndex
	 *
	 * @param    id                  a  long
	 * @param    id2                 a  long
	 *
	 */
	public int getIndexOfChild(Object id, Object id2)  {
		if(id==null) return -1;
		
		if(id.equals(root))  {
			Strand strand=getStrand((Long)id2);
			return strands.indexOf(strand);
		}
		
		MsgAdapter mh=msgadapters.get(id);
		
		if(mh.children==null) return -1;
		
		synchronized(mh.children)  {
			for(int i=0;i<mh.children.size();i++)  {
				Long l=mh.children.get(i);
				if(l.equals(id2)) return i;
			}
		}
		
		return -1;
	}
	
	public int getChildCount(Object id)  {
		if(id.equals(root))  {
			return strands.size();
		}
		
		Msg m=getMsg((Long)id); // Get the message
		
		MsgAdapter mh=msgadapters.get((Long)id);
		
		return mh.getChildCount();
	}
	
	
	public Object getChild(Object id,int p1)  {
		if(id.equals(root))  {
			// Root handler
			synchronized(strands) {
				if(p1>=strands.size()) {
					System.out.println("Hopped sync");
					return null;
				}
				return strands.get(p1).getRootid();
			}
		}
		
		synchronized(msgadapters) {
			MsgAdapter mh=msgadapters.get(id);
			
			if(!mh.hasChildren())  {
				return null;
			}
			
			return mh.children.get(p1);
		}
	}
	
	public Msg getMsg(Long id)  {
		
		
		MsgAdapter mh=msgadapters.get(id);
		
		if(mh!=null) return mh.getMsg();
		
		loadFor(id);
		
		MsgAdapter m2=msgadapters.get(id);
		
		if(m2==null) return null;
		
		return m2.getMsg();
	}
	
	
	private MsgAdapter loadFor(Long id)  {
		Msg m=bundle.daGetMsg(id);
		
		if(m==null)  {
			return null;
		}
		
		if(m.getRootKnotId()!=m.getKnotId())  {
			loadStrand(m.getRootKnotId());
		}
		else  {
			loadStrand(m.getKnotId());
		}
		
		MsgAdapter mh=msgadapters.get(id);
		
		return mh;
	}
	
	private void loadStrand(Long id)  {
		ArrayList<Msg> msgs=bundle.daGetMsgsOnStrand(id);
		
		Iterator<Msg> i=msgs.iterator();
		
		while(i.hasNext())  {
			Msg m=i.next();
			
			putMsg(m);
		}
	}
	
	public void putMsg(Msg m)  {
		Long idl=m.getKnotId();

		MsgAdapter msgadapter=new MsgAdapter(m);
		
		msgadapters.put(idl,msgadapter);
		
		if(!msgadapter.isStrandRoot())  {
			// Find the parent
			Long paid=m.getCommentto();
			
			if(paid==null) return;
			
			MsgAdapter pah=msgadapters.get(paid);
			
			if(pah==null) {
				pah=loadFor(paid);
				if(pah==null) {
					// The parent really doesn't exist!
					return;
				}
			}
			
			try {
				pah.addChild(m.getKnotId());
				fireChildAdded(getTreePath(paid),pah.indexOf(m.getKnotId()),m.getKnotId());
			}
			catch(NullPointerException npe) {
//				System.out.println("npe "+npe);
				npe.printStackTrace();
			}
		}
	}
	
	
	class MsgAdapter  {
		Msg msg;
		ArrayList<Long> children=null;
		boolean root;
		
		private MsgAdapter(Msg msg)  {
			this.msg = msg;
		}
		
		
		public boolean isStrandRoot()  {
			return getStrand(msg.getKnotId())!=null;
		}
		
		public Msg getMsg()  {
			return msg;
		}
		
		public void addChild(Long id)  {
			if(children==null) children=new ArrayList<Long>();
			//		System.out.println("Adding "+id+" as child of "+msg.getKnotId());
			for(int i=0;i<children.size();i++) {
				if(id.longValue()<children.get(i).longValue()) {
					children.add(i,id);
					return;
				}
			}
			
			children.add(id);
		}
		
		public int indexOf(Long id)
		{
			return children.indexOf(id);
		}
		public int getChildCount()  {
			
			if(children==null) return 0;
			
			return children.size();
		}
		
		public boolean hasChildren()  {
//			System.out.println("haschildren");
			return getMsg().getFirstcomment()!=null;
		}
	}
	
	
	
}

