/**
 * PathTreeNode.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.models;



import com.runstate.augur.gallery.Bundle;
import java.util.Iterator;
import javax.swing.tree.MutableTreeNode;

public class BundleContainerTreeNode extends BundleTreeNode {
	
	public BundleContainerTreeNode(Bundle p) {
		super(p);
	}
	
	public boolean isLeaf() {
		return false;
	}
	
	public String toString() {
		return "BundleContainerTreeNode["+getBundle()+"]";
	}
	
	
}
