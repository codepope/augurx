/**
 * AmeolImport.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.missions;
import com.runstate.augur.AugurX;
import com.runstate.augur.twix.AmeolScratchpadParser;
import com.runstate.augur.twix.ui.ScratchpadImport;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.controller.Profile;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.GalleryException;
import com.runstate.augur.gallery.Msg;
import com.runstate.augur.gallery.missions.Mission;
import com.runstate.augur.ui.status.BasicStatus;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class TwixSimpleImportMission extends Mission {
	public TwixSimpleImportMission(Door door) {
		super(door);
	}
	
	public String getMenuName() {
		return "Scratchpad Import";
	}
	
	public String getTitle() {
		return "Scratchpad Import";
	}
	
	public String getPrefsName()
	{
		return "twiximportmission";
	}
	
	public BasicStatus getStatusUI(BrowserCommandHandler vch) {
		return new ScratchpadImport(this);
	}
	
	public void run() {
		int	cnt=0;
		boolean markread;
		
		clearCancelled();
		
		Profile profile=Controller.getProfile();
		
		String path=profile.get(Prefs.M_S_I_LASTIMPORT,profile.get(Prefs.HOMEDIR)+"scratchpad");
		
		File scf=getPath(path,"Import");
		
		if(scf==null) {
			fireMissionDone("Import Cancelled"); return;
		}
		
		int resq=JOptionPane.showConfirmDialog(null,"Do you want to mark messages as already read when imported?","Scratchpad Import",JOptionPane.YES_NO_OPTION);
		
		if(resq==JOptionPane.YES_OPTION) {
			markread=true;
		}
		else {
			markread=false;
		}
		
		long start=System.currentTimeMillis();
		
		profile.set(Prefs.M_S_I_LASTIMPORT,scf.getAbsolutePath());
		
		AmeolScratchpadParser asp=null;
		
		try {
			asp=new AmeolScratchpadParser(scf);
		}
		catch (FileNotFoundException fnfe) {
			progress(0,0,"File not found");
			asp=null;
		}
		catch (IOException ioe) {
			progress(0,0,"Error reading file");
			asp=null;
		}
		
		if(asp!=null) {
			try {
				Gallery gallery=Controller.getGallery();
				
				progress(0,asp.getEstimatedCount(),"Parsing "+scf.getAbsolutePath());
				
				int modulo=100;
				//asp.getEstimatedCount()>10000?1000:100;
				int newcnt=0;
				
				while(asp.hasNext()) {
					cnt++;
					
					if(isCancelled()) {
						//f.close();
						fireMissionDone("Import cancelled");
						return;
					}
					
					String c=asp.next();
					
					Msg cm=new Msg(getDoor(),c);
					
					cm.setUnread(!markread);
					
					cm.setHot(getDoor().isMsgHot(cm));
					
					
					boolean success=gallery.newMsg(cm,getDoor().getDoorid());
					
					if(success) newcnt++;
					
					if(cnt%modulo==0) {
						int ptime=(int)(System.currentTimeMillis()-start)/1000;
						
						if(ptime!=0) progress(cnt,asp.getEstimatedCount(),"New "+newcnt+" read "+cnt+" in "+ptime+"s at "+(cnt/ptime)+"msgs/s");
					}
				}
				
				int ptime=(int)(System.currentTimeMillis()-start)/1000;
				
				if(ptime!=0) progress(cnt,cnt,"New "+newcnt+" read "+cnt+" in "+ptime+"s at "+(cnt/ptime)+"msgs/s");
				
				asp.close();
			}
			catch (GalleryException e) { e.printStackTrace(); }
			catch (ConcurrentModificationException cme) { cme.printStackTrace(); }
			finally {
				asp.close();
			}
		}
		
		fireMissionDone();
		
	}
}
