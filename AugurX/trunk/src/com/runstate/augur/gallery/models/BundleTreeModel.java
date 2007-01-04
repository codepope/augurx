/**
 * PathNodeTreeTableModel.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.models;
import com.runstate.augur.gallery.Bundle;
import com.runstate.augur.gallery.BundleManager;
import com.runstate.augur.gallery.BundleManagerListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class BundleTreeModel extends DefaultTreeModel implements BundleManagerListener,BundleTreeInterface {
	
	BundleManager bundlemanager;
	HashMap<Long, BundleTreeNode> bundleidToTreeMap=new HashMap<Long, BundleTreeNode>();
	
	public final static int ALL=1;
	public final static int UNREAD=2;
	public final static int WARMUNREAD=3;
	
	int mode=ALL;
	
	protected EventListenerList listenerList = new EventListenerList();
	
	public BundleTreeModel(BundleManager bundlemanager) {
		super(null,true);
		
		this.bundlemanager=bundlemanager;
		
		bundlemanager.addBundleManagerListener(this);
		
		mode=ALL;
		
		reloadModel();
	}
	
	
	public Object[] getPathToRoot(BundleTreeNode btn) {
		return super.getPathToRoot(btn);
	}
	
	
	
	/**
	 * Method getTotal
	 *
	 * @return   an int
	 */
	public int getTotal() {
		return ((BundleContainerTreeNode)root).getTotal();
	}
	
	/**
	 * Method getUnread
	 *
	 * @return   an int
	 */
	public int getUnread() {
		return ((BundleContainerTreeNode)root).getUnread();
	}
	
	public void setMode(int newmode) {
		if(newmode==mode) return;
		synchronized(modellock)
		{
			mode=newmode;
			reloadModel();
		}
	}
	
	public void reloadModel() {

			ArrayList<Bundle> pathnodes=bundlemanager.getBundles();
			
			for(Iterator<Bundle> i=pathnodes.iterator();i.hasNext();) {
				Bundle bundle=i.next();
				internalUpdateBundleEvent(bundle);
			}
	}
	
	private boolean shouldBeAdded(Bundle bundle) {
		switch(mode) {
			case ALL:
				return true;
			case UNREAD:
				return bundle.getUnread()>0;
			case WARMUNREAD:
				return bundle.getWarmunread()>0;
		}
		
		return true;
	}
	
	private boolean shouldBeRemoved(Bundle bundle) {
		switch(mode) {
			case ALL:
				return false;
			case UNREAD:
				return bundle.getUnread()==0;
			case WARMUNREAD:
				return bundle.getWarmunread()==0;
		}
		
		return false;
	}
	
	synchronized BundleTreeNode addBundle(Bundle bundle) {
		BundleTreeNode btn=getBundleTreeNode(bundle.getBundleid());
		
		if(btn!=null) {
//			System.out.println("Trying to readd existing bundle");
			return btn;
		}
		
		BundleTreeNode newpathtreenode;
		
		if(bundle.isContainer()) {
			newpathtreenode=new BundleContainerTreeNode(bundle);
		}
		else {
			newpathtreenode=new BundleTreeNode(bundle);
		}
		
		putBundleTreeNode(newpathtreenode);
		
		if(bundle.getBundleid().longValue()==0) {
			// This is the root
			setRoot(newpathtreenode);
			return newpathtreenode;
		}
		
		Long bundleparentid=bundle.getParentid();
		
		BundleTreeNode parentnode=getBundleTreeNode(bundleparentid);
		
		if(parentnode==null) {
			System.out.println("Stop here");
		}
		
		int i=getIndexForSort(parentnode,newpathtreenode);
		
		insertNodeInto(newpathtreenode,parentnode,i);
		
		
		return newpathtreenode;
	}
	
	public synchronized void removeBundle(Bundle bundle) {
		BundleTreeNode btn=getBundleTreeNode(bundle.getBundleid());
//		System.out.println("Removing "+bundle);
		if(btn!=null) {
			if(btn instanceof BundleContainerTreeNode) {
				// This node may have children
				ArrayList<BundleTreeNode> al=new ArrayList<BundleTreeNode>();
				Enumeration<BundleTreeNode> e=(Enumeration<BundleTreeNode>)btn.children();
				while(e.hasMoreElements()) {
					BundleTreeNode cbtn=e.nextElement();
					al.add(cbtn);
				}
				// Now we have a list of the children
				for(int i=0;i<al.size();i++) {
					removeBundleTreeNode(al.get(i).getBundleid());
					removeNodeFromParent(al.get(i));
				}
			}
			
			removeBundleTreeNode(btn.getBundleid());
			if(btn.getParent()!=null) removeNodeFromParent(btn);
			
		}
	}
	
	int getIndexForSort(BundleTreeNode parent, BundleTreeNode newchild) {
		if(parent.getChildCount()==0) return 0;
		
		for(int i=0;i<parent.getChildCount();i++) {
			BundleTreeNode ptn=(BundleTreeNode)parent.getChildAt(i);
			
			if(ptn.getBasename().compareTo(newchild.getBasename())>-1) {
				return i;
			}
		}
		
		return parent.getChildCount();
	}
	
	public Object getChild(Object parent, int index) {
		if(index>=getChildCount(parent)) return null;
		
		return ((DefaultMutableTreeNode)parent).getChildAt(index);
	}
	
	/**
	 * Returns the number of children of <code>parent</code>.
	 * Returns 0 if the node
	 * is a leaf or if it has no children.  <code>parent</code> must be a node
	 * previously obtained from this data source.
	 *
	 * @param   parent  a node in the tree, obtained from this data source
	 * @return  the number of children of the node <code>parent</code>
	 */
	public int getChildCount(Object parent) {
		return ((DefaultMutableTreeNode)parent).getChildCount();
	}
	
	
	public void putBundleTreeNode(BundleTreeNode ptn) {
		bundleidToTreeMap.put(ptn.getBundleid(),ptn);
	}
	
	public synchronized BundleTreeNode getBundleTreeNode(Long bundleid) {
		return bundleidToTreeMap.get(bundleid);
	}
	
	public void removeBundleTreeNode(Long bundleid) {
		bundleidToTreeMap.remove(bundleid);
		return;
	}
	
	/**
	 * Method newBundleEvent
	 *
	 * @param    bundle              a  Bundle
	 *
	 */
	public void newBundleEvent(Bundle bundle) {
		if(shouldBeAdded(bundle)) addBundle(bundle);
	}
	
	/**
	 * Method updateBundleEvent
	 *
	 * @param    bundle              a  Bundle
	 *
	 */
	
	private Object modellock=new Object();
	
	public void updateBundleEvent(Bundle bundle)
	{
		synchronized(modellock)
		{
			internalUpdateBundleEvent(bundle);
		}
	}
	
	void internalUpdateBundleEvent(Bundle bundle) {
		BundleTreeNode btn=getBundleTreeNode(bundle.getBundleid());
		
		if(btn==null && shouldBeAdded(bundle)) {
			addBundle(bundle);
		}
		else
			if(btn!=null && shouldBeRemoved(bundle)) {
				if(bundle.getBundleid().longValue()!=0) {
					removeBundle(bundle);
				}
				else {
					nodeChanged(btn);
				}
			}
			else {
				nodeChanged(btn); // Just flag update
			}
		return;
	}
	
	
	
	public void deregisterListener() {
		bundlemanager.removeBundleManagerListener(this);
	}

}


