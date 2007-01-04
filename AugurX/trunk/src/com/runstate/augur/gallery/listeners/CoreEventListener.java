/**
 * MsgEventListener.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.listeners;

import com.runstate.augur.gallery.events.CoreEvent;

public interface CoreEventListener
{
	public abstract void coreEventOccurred(CoreEvent ce);
}

