/**
 * SyncMissionEvent.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery;

public class SyncEvent {
	
	String description;
	int type;
	String doorname;
	
	public final static int INFOMSG=0;
	public final static int OFFLINE=1;
	public final static int CONNECTING=2;
	public final static int CONNECTED=3;
	public final static int RESYNC=4;
	public final static int DISCONNECTING=5;
	public final static int DISCONNECTED=6;
	
	public final static int SENDING=300;
	public final static int SENDING_DONE=301;
	public final static int COLLECTING=302;
	public final static int COLLECTING_DONE=303;
	public final static int INFO=304;
	public final static int INFO_DONE=305;
	
	public final static int PARSING=200;
	public final static int PARSING_DONE=201;
	
	public final static int SESSION=100;
	public final static int SESSION_DONE=101;

	public final static int FAILED_TO_CONNECT=998;
	
	public final static int ERROR=999;
	
	public SyncEvent(String doorname,int type,String desc) {
		this.doorname=doorname;
		this.type=type;
		this.description=desc;
	}
	
	/**
	 * Sets Description
	 *
	 * @param    Description         a  String
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * Returns Description
	 *
	 * @return    a  String
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Sets Doorname
	 *
	 * @param    Doorname            a  String
	 */
	public void setDoorname(String doorname)
	{
		this.doorname = doorname;
	}
	
	/**
	 * Returns Doorname
	 *
	 * @return    a  String
	 */
	public String getDoorname()
	{
		return doorname;
	}
	


}

