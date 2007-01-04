/**
 * BasicMission.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.missions;



import com.runstate.augur.controller.Door;
import com.runstate.augur.ui.status.BasicStatus;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

public class Mission implements Runnable {

	Door door;
	
	public Mission(Door door) { this.door=door; }
	

	public String getPrefsName() {
		return "Mission";
	}
	
	/**
	 * Method getMenuName
	 *
	 * @return   an Object
	 */
	public String getMenuName()
	{
		return "Empty Mission";
	}
	
	public BasicStatus getStatusUI()
	{
		return new BasicStatus(this);
	}
	
	public String getTitle()
	{
		return "Empty Mission";
	}
	
	public File getPath(final String path,final String title) {
		final Object[] results=new Object[2];
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							JFileChooser jfc=new JFileChooser(new File(path));
							jfc.setSelectedFile(new File(path));
							
							int result=jfc.showDialog(null,title);
							
							if(result!=JFileChooser.APPROVE_OPTION) {
								results[0]=Boolean.FALSE;
							}
							else {
								results[0]=jfc.getSelectedFile();
							}
						}
					});
		} catch (InterruptedException e) {} catch (InvocationTargetException e) {}
		
		if(results[0] instanceof Boolean) {
			return null;
		}
		
		return (File)results[0];
	}
	
	public void setDoor(Door door)
	{
		this.door = door;
	}
	
	public Door getDoor()
	{
		return door;
	}
	
	public void run() {
	}

	boolean cancelled=false;
	
	public boolean isCancelled() {
		return cancelled;
	}

	public void cancelled() {
		cancelled=true;
	}

	public void clearCancelled() {
		cancelled=false;
	}
	
	Vector<MissionListener> missionlisteners=new Vector<MissionListener>();
	
	public void addMissionListener(MissionListener ml) {
		missionlisteners.add(ml);
	}
	
	public void removeMissionListener(MissionListener ml) {
		missionlisteners.remove(ml);
	}
	
	public void fireMissionEvent(MissionEvent me) {
		Iterator<MissionListener> iter = missionlisteners.iterator();
		
		while (iter.hasNext()) {
			MissionListener ml = iter.next();
			ml.missionEvent(me);
		}
	}
	

	public void progress(int i,int max,String desc)
	{
		fireMissionEvent(new MissionEvent(i,max,desc));
	}
	
	public void fireMissionDone() {
		fireMissionDone(null);
	}
	
	public void fireMissionDone(String msg) {
		Iterator<MissionListener> iter = missionlisteners.iterator();
		
		while (iter.hasNext()) {
			MissionListener ml = iter.next();
			ml.missionComplete(msg);
		}
	}
}

