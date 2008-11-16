/**
 * SyncMission.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery;
import com.runstate.augur.controller.Door;
import com.runstate.augur.ui.augurpanel.AugurPanel;
import java.util.Iterator;
import java.util.Vector;

abstract public class Sync implements Runnable
{
	Door door;
	
	public Sync(Door d)
	{
		door=d;
	}
	
	
	public int connectionTimeOut=0;
	
	/**
	 * Method go
	 *
	 */
	public void go()
	{
		// TODO
	}
	
	/**
	 * Method exit
	 *
	 */
	public void exit()
	{
		// TODO
	}
	
	/**
	 * Method getMenuName
	 *
	 * @return   an Object
	 */
	public String getMenuName()
	{
		// TODO
		return null;
	}
	
	/**
	 * Method getStatusUI
	 *
	 * @return   an AugurPanel
	 */
	public AugurPanel getStatusUI()
	{
		// TODO
		return null;
	}
	
	/**
	 * Sets Door
	 *
	 * @param    door                a  Door
	 */
	public void setDoor(Door door)
	{
		this.door = door;
	}
	
	/**
	 * Returns Door
	 *
	 * @return    a  Door
	 */
	public Door getDoor()
	{
		return door;
	}
	
	
	/**
	 * Sets Mode
	 *
	 * @param    mode                an int
	 */
	public void setMode(int mode)
	{
		this.mode = mode;
	}
	
	/**
	 * Returns Mode
	 *
	 * @return    an int
	 */
	public int getMode()
	{
		return mode;
	}
	
	/**
	 * Sets ConnectionTimeOut
	 *
	 * @param    connectionTimeOut   an int
	 */
	public void setConnectionTimeOut(int connectionTimeOut)
	{
		this.connectionTimeOut = connectionTimeOut;
	}
	
	/**
	 * Returns ConnectionTimeOut
	 *
	 * @return    an int
	 */
	public int getConnectionTimeOut()
	{
		return connectionTimeOut;
	}
	
	Vector<SyncListener> synclisteners=new Vector<SyncListener>();
	
	public void addSyncListener(SyncListener ml) {
		synclisteners.add(ml);
	}
	
	public void removeSyncListener(SyncListener ml) {
		synclisteners.remove(ml);
	}
	
	public void fireSyncEvent(SyncEvent se) {
		Iterator<SyncListener> iter = synclisteners.iterator();
		
		while (iter.hasNext()) {
			SyncListener ml = iter.next();
			ml.syncEventOccurred(se);
		}
	}
	
	/**
	 * Method fireSyncMsg
	 *
	 * @param    desc                a  String
	 *
	 */
	public void fireSyncMsg(int synctype,String desc)
	{
//		System.out.println("SYNC "+desc);
		SyncEvent se=new SyncEvent(getDoor().getDoorname(),synctype,desc);
		fireSyncEvent(se);
	}
		
	public static final int MODE_ALWAYSON=1;
	public static final int MODE_ONCOMMAND=2;

	public int mode=MODE_ALWAYSON;
	
	public abstract boolean isBusy();
	public abstract boolean isConnected();
	
	public abstract int session();
	
	public abstract boolean sendAndCollect();
	public abstract boolean info();
	public abstract boolean collect();
	public abstract boolean connect();
	public abstract boolean disconnect();
	
	public abstract boolean canConnect();
	public abstract boolean canSession();
	public abstract boolean canDisconnect();
	public abstract boolean canInfo();
	public abstract boolean canCollect();
	public abstract boolean canSendAndCollect();
	
}

