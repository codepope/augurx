/**
 * PathTreeNode.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.models;

import com.runstate.augur.gallery.Bundle;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class BundleTreeNode extends DefaultMutableTreeNode {
	
	public BundleTreeNode(Bundle p) {
		setUserObject(p);
	}
	
	public int getUnread() {
		return getBundle().getUnread();
	}
	
	public int getTotal() {
		return getBundle().getTotal();
	}
	
	public int getWarmunread() {
		return getBundle().getWarmunread();
	}
	
	public String getBasename() {
		String name=getBundle().getBasename();
		if(name.length()==0) return "/";
		else
			return name;
	}
	
	public String getFullPath() {
		return getBundle().getBundlename();
	}
	
	public Bundle getBundle() {
		return (Bundle)getUserObject();
	}
	
	public Long getBundleid()
	{
		return ((Bundle)getUserObject()).getBundleid();
	}
	public boolean isLeaf() {
		return true;
	}
	
	public boolean allowsChildren() {
		return false;
	}
	
	public boolean isUnread(boolean warmonly) {
		if(warmonly) return getWarmunread()>0;
		else return getUnread()>0;
	}
	
	public String toString() {
		return "BundleTreeNode["+getBundle()+"]";
	}
}

