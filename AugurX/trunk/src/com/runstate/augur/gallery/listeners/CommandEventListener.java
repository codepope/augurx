/**
 * CommandEventListener.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.listeners;

import com.runstate.augur.gallery.events.CommandEvent;

public interface CommandEventListener
{
	public abstract void commandEventOccurred(CommandEvent ce);
}

