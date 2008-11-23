/**
 * Door.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.controller;
import com.runstate.augur.gallery.*;

import com.runstate.augur.AugurX;
import com.runstate.augur.gallery.commands.Command;
import com.runstate.augur.gallery.commands.commands.CommentCommand;
import com.runstate.augur.gallery.commands.commands.GetCommand;
import com.runstate.augur.gallery.commands.commands.SayCommand;
import com.runstate.augur.gallery.missions.Mission;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import java.io.BufferedReader;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.ImageIcon;



public abstract class Door implements SyncListener
{
	public static final int NO_ID=-1;
	
	Long doorid;
	String doorname;
	Sync sync;

	public void setDoorid(Long doorid)
	{
		this.doorid = doorid;
	}
	
	public Long getDoorid()
	{
		return doorid;
	}
	
	public void initialiseDoor(Long doorid,String doorname)
	{
		this.doorid=doorid;
		this.doorname=doorname;
		sync=getSyncImpl();
		sync.addSyncListener(this);
		Thread t=new Thread(sync);
		t.start();
	}
	
	public String getURL(Msg p0)
	{
		// TODO
		return null;
	}
		
	public void startDoorSync()
	{
		sync.go();
	}
	
	public void stopDoorSync()
	{
		sync.exit();
		sync.addSyncListener(this);
		Thread t=new Thread(sync);
		t.start();
	}
	
	public Sync getSync()
	{
		return sync;
	}
	
	public abstract Sync getSyncImpl();

//	public abstract SyncUI getSyncUIImpl();
	
	public abstract DoorUI getDoorUI();
	
	public String getNativeUser(String user)
	{
		return user.substring(0,user.indexOf('@'));
	}
	
	public abstract String getMyNativeUser();

	public abstract Command refresh(Object obj);

	public abstract AuthorInfo createUserInfo(String address);

	public abstract PathInfo createPathInfo(Long bundleid);
	
	public abstract GetCommand createGetCommand(Long bundleid,Long msgid,boolean all);
	
	public abstract SayCommand createSayCommand(Long bundleid,String text);
	
	public abstract CommentCommand createCommentCommand(Long bundleid, Long knotId, String text);
	
	
	public abstract boolean isMsgHot(Msg m);

	public void setDoorname(String doorname)
	{
		this.doorname = doorname;
	}
	
	public String getDoorname()
	{
		return doorname;
	}
	
	public String getNativePath(String path)
	{
		int i=path.indexOf('/',1);
		
		if(i==-1)
		{
			return null;
		}
		else
		{
			return path.substring(i+1);
		}
	}

	
	public String getDoornameForPath(String path)
	{
		int i=path.indexOf('/',1);
		
		if(i==-1)
		{
			return path.substring(1);
		}
		else
		{
			return path.substring(1,i);
		}
	}
	
	public Long getPathForDoor()
	{
		return Controller.getController().getGallery().getBundleManager().nameToId("/"+getDoorname());
	}
	
//	public abstract Msg getMsg(String path);
	
//	public abstract SyncUI getSyncUI(ViewerCommandHandler vch);
	
	public abstract Mission[] getMiscMissions();
	

	private String lastlog;
	
	public void log(String log)
	{
		lastlog=log;

		fireDoorEvent(new DoorEvent(this,log));
	}
	
	public String getLastLog()
	{
		return lastlog;
	}

	
	public void syncEventOccurred(SyncEvent sme)
	{
		log(sme.getDescription());
	}

	public boolean isBusy() { return sync.isBusy(); }
	
	public boolean isConnected() { return sync.isConnected(); }
	
	public int session() { return sync.session(); }
	
//	public boolean sendAndCollect() { return sync.sendAndCollect(); }
//
//	public boolean info() { return sync.info(); }
//
//	public boolean collect() { return sync.collect(); }
//
//	public boolean connect() { return sync.connect(); }
//
//	public boolean disconnect() { return sync.disconnect(); }
//
//	public boolean canConnect() { return sync==null?false:sync.canConnect(); }
//
//	public boolean canDisconnect() { return sync==null?false:sync.canDisconnect(); }
//
//	public boolean canCollect() { return sync==null?false:sync.canCollect(); }
//
//	public boolean canSendAndCollect() { return sync==null?false:sync.canSendAndCollect(); }
//
//	public boolean canSession() { return sync==null?false:sync.canSession(); }
//
//	public boolean canInfo() { return sync==null?false:sync.canInfo(); }
//
		Vector<DoorListener> doorlisteners=new Vector<DoorListener>();
	
	public void addDoorListener(DoorListener ml) {
		doorlisteners.add(ml);
	}
	
	public void removeDoorListener(DoorListener ml) {
		doorlisteners.remove(ml);
	}
	
	public void fireDoorEvent(DoorEvent se) {
		Iterator<DoorListener> iter = doorlisteners.iterator();
		
		while (iter.hasNext()) {
			DoorListener ml = iter.next();
			ml.doorEventOccurred(se);
		}
	}
	
	public void fireDoorMsg(String desc)
	{
		DoorEvent se=new DoorEvent(this,desc);
		fireDoorEvent(se);
	}

	public abstract ImageIcon getDoorIcon();
	
	public abstract String getBodyHTML(Msg m);

    public abstract String processText(String s);

	public abstract void parseReader(Msg m,BufferedReader br);
}

