/**
 * BundleManagerListener.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery;

public interface BundleManagerListener
{
	
	/**
	 * Method updateBundleEvent
	 *
	 * @param    bundle              a  Bundle
	 *
	 */
	public void updateBundleEvent(Bundle bundle);
	
	
	/**
	 * Method newBundleEvent
	 *
	 * @param    bundle              a  Bundle
	 *
	 */
	public void newBundleEvent(Bundle bundle);
	
}

