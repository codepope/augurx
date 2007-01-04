/**
 * ScratchpadExportMission.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.missions;
import com.runstate.augur.gallery.*;
import java.io.*;
import com.runstate.augur.cix.ui.ScratchpadExport;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.controller.Profile;
import com.runstate.augur.gallery.missions.Mission;
import com.runstate.augur.ui.status.BasicStatus;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

public class CixSimpleExportMission extends Mission {
	public CixSimpleExportMission(Door door) {
		super(door);
	}
	
	public String getMenuName() {
		return("Scratchpad Export");
	}
	
	public String getTitle() {
		return "Scratchpad Export";
	}
	
	public String getPrefsName()
	{
		return "cixexportmission";
	}
	
	public BasicStatus getStatusUI(BrowserCommandHandler vch) {
		return new ScratchpadExport(vch,this);
	}

	
	public void run() {
		int	cnt=1;
		
		Profile profile=Controller.getProfile();
		
		String path=profile.get(Prefs.M_S_E_LASTEXPORT,profile.get(Prefs.HOMEDIR)+"scratchpad");
//
//		JFileChooser jfc=new JFileChooser(new File(path));
//		jfc.setSelectedFile(new File(path));
//
//		int result=jfc.showDialog(null,"Export");
		
		File scf=getPath(path,"Export");
		
		if(scf==null) {
			fireMissionDone("Export Cancelled"); return;
		}
		
		
		profile.set(Prefs.M_S_E_LASTEXPORT,scf.getAbsolutePath());
		
		long start=System.currentTimeMillis();
		int msgcnt=0;
		
		//		WritableByteChannel fos=new FileOutputStream(scf).getChannel();
		
		
		
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
				Iterator<Msg> miter=msgs.iterator();
				while (miter.hasNext()) {
					if(isCancelled()) {
						try {
							bw.close();
						}
						catch (IOException e) {}
						fireMissionDone("Export Cancelled");
						return;
					}
					Msg m=miter.next();
					
					bw.write(m.getText());
					bw.write("\n");
					miter.remove();
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
