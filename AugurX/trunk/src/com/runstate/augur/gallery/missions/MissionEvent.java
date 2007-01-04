/**
 * ProgressMissionEvent.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.missions;



public class MissionEvent {
	int progress;
	int max;
	String desc;

	
	public MissionEvent(int progress,int max,String desc) {
		this.progress=progress;
		this.max=max;
		this.desc=desc;
	}
	
	public int getProgress() {
		return progress;
	}
	
	public int getMax() {
		return max;
	}
	
	public String getDescription() {
		return desc;
	}
	
}

