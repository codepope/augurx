/**
 * TwixRandomExportMission.java
 *
 * @author Created by Omnicore CodeGuide
 */
/**
 * ScratchpadExportMission.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.missions;
import com.runstate.augur.twix.ui.ScratchpadExport;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.controller.Profile;
import com.runstate.augur.gallery.Bundle;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.GalleryException;
import com.runstate.augur.gallery.Msg;
import com.runstate.augur.gallery.missions.Mission;
import com.runstate.augur.ui.status.BasicStatus;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JFileChooser;

public class TwixRandomExportMission extends Mission {
	public TwixRandomExportMission(Door door) {
		super(door);
	}
	
    @Override
	public String getMenuName() {
		return("Random Scratchpad Export");
	}
	
    @Override
	public String getTitle() {
		return "Random Scratchpad Export";
	}
	
    @Override
	public String getPrefsName()
	{
		return "twixrandomexportmission";
	}
	
	public BasicStatus getStatusUI(BrowserCommandHandler vch) {
		return new ScratchpadExport(vch,this);
	}
	
    @Override
	public void run() {
		int	cnt=1;
		
		Profile profile=Controller.getProfile();
		
		String path=profile.get(Prefs.M_S_E_LASTEXPORT,profile.get(Prefs.HOMEDIR)+"scratchpad");
		
		JFileChooser jfc=new JFileChooser(new File(path));
		jfc.setSelectedFile(new File(path));
		
		File scf=getPath(path,"Random Export");
		
		if(scf==null) {
			fireMissionDone("Random Export Cancelled"); return;
		}
		
		profile.set(Prefs.M_S_E_LASTEXPORT,scf.getAbsolutePath());
		
		long start=System.currentTimeMillis();
		int msgcnt=0;

		Charset cs=Charset.forName("cp1252");
		
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(scf));
			Gallery gallery=Controller.getGallery();
			
			progress(0,0,"Exporting to "+scf.getAbsolutePath());
			
			ArrayList<Bundle> pathnames=gallery.getBundles();
			
			int i=0;
			
			
			Iterator<Bundle> iter = pathnames.iterator();
			
			while (iter.hasNext()) {
				Bundle currentpath=iter.next();
				ArrayList<Msg> msgs=gallery.getMsgs(currentpath.getBundleid());
				progress(i++,pathnames.size(),"Exporting "+currentpath.getBundlename()+" ("+msgcnt+" exported in "+(System.currentTimeMillis()-start)/1000+" seconds)");
				
				
				while (msgs.size()>0) {
					if(isCancelled()) {
						try {
							bw.close();
						}
						catch (IOException e) {}
						fireMissionDone("Export Cancelled");
						return;
					}
					int ind=(int)(Math.random()*msgs.size());
					Msg m=msgs.get(ind);
					
					bw.write(m.getText());
					bw.write("\n");
					msgs.remove(ind);
					msgcnt++;
					
				}
			}
			
			progress(i,pathnames.size(),"Wrote "+msgcnt+" in "+(System.currentTimeMillis()-start)/1000+" seconds");
			
			
			bw.close();
		}
		catch (java.io.FileNotFoundException e) { progress(0,0,"File not found"); }
		catch (IOException e) { e.printStackTrace(); }
		catch (GalleryException e) { e.printStackTrace(); }
		
		
		fireMissionDone("Exported "+msgcnt+" messgages\nin "+(System.currentTimeMillis()-start)/1000+" seconds");
	}
}
