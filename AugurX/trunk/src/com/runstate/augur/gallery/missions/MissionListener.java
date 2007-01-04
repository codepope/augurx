/**
 * MissionListener.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.missions;

public interface MissionListener
{
	public void missionTitle(String title);
	public void missionEvent(MissionEvent me);
	public void missionComplete(String msg);
}

