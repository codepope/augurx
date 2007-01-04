/**
 * PathIndexEventListener.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.listeners;

import com.runstate.augur.gallery.events.BundleEvent;

public interface PathEventListener
{
	public abstract void pathIndexEventOccurred(BundleEvent pie);
}

