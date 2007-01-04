/**
 * BundleTreeInterface.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.models;

public interface BundleTreeInterface
{
	public BundleTreeNode getBundleTreeNode(Long bundleid);
	public Object[] getPathToRoot(BundleTreeNode btn);
}

