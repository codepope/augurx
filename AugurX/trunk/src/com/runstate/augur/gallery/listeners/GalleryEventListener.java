/**
 * PotsListener.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.listeners;

import com.runstate.augur.gallery.events.*;

public interface GalleryEventListener
{
	public abstract void galleryEventRecieved(GalleryEvent pe);
}

