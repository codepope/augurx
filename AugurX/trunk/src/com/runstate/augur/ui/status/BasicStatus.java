/**
 * BasicStatusView.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.status;


import javax.swing.*;

import com.runstate.augur.gallery.missions.Mission;
import com.runstate.augur.gallery.missions.MissionEvent;
import com.runstate.augur.gallery.missions.MissionListener;
import com.runstate.augur.ui.augurpanel.AugurPanelManger;
import com.runstate.augur.ui.augurpanel.AugurPanel;
import com.runstate.util.swing.LabelledItemPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

public class BasicStatus extends AugurPanel implements Runnable,MissionListener {
	
	public JPanel basicpanel;
	public JTextField basicmsgtextfield;
	public JProgressBar basicprogressbar;
	
	LabelledItemPanel basiclabelledpanel;
	
	String basictitle;
	
	Thread missionthread=null;
	Mission mission=null;
	
	
	public BasicStatus(Mission mission) {
		super();
		this.basictitle=mission.getTitle();
		this.mission=mission;
		
		basicpanel=new JPanel(new BorderLayout());
		
		basiclabelledpanel=new LabelledItemPanel();
		basicmsgtextfield=new JTextField(25);
		basicmsgtextfield.setBorder(BorderFactory.createEtchedBorder());
		basicmsgtextfield.setEditable(false);
		basicprogressbar=new JProgressBar(JProgressBar.HORIZONTAL);
		basicprogressbar.setStringPainted(true);
		basiclabelledpanel.addItem("Current Stage:",basicmsgtextfield);
		basiclabelledpanel.addItem("Overall Progress:",basicprogressbar);
		
		basicpanel.add(BorderLayout.CENTER,basiclabelledpanel);
		
		completeUI();
		
		JPanel cancelpanel=new JPanel();
		cancelpanel.add(new JButton(cancel_action));
		
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER,basicpanel);
		add(BorderLayout.SOUTH,cancelpanel);
		
		new Thread( this ).start();
		
		requestFocus();
	}
	
	public boolean wantsMenubar() {
		return false;
	}
	
	
	public boolean isApplicationMainWindow() {
		return false;
	}
	
	public String getTitle() { return mission.getTitle(); }
	
	boolean done=false;
	
	public String getPrefsName() {
		return mission.getDoor().getDoorname()+".basicstatus"+mission.getPrefsName();
	}
	
	Action cancel_action=new AbstractAction("Cancel") {
		public void actionPerformed(ActionEvent e) {
			BasicStatus.this.cancelled();
		}
	};
	
	public void completeUI() {
	}
	
	public void run() {
		mission.removeMissionListener(this);
		mission.addMissionListener(this);
		missionthread=new Thread(mission);
		missionthread.start();
	}
	
//	public void update(TransferProtocolEvent tse) {
//		//if(tsp!=null) tsp.update(tse);
//		if(basictransferstatus==null) {
//			basictransferstatus=new TransferStatus("Transfer",ResMan.getIcon("gui/redflag.gif"),null);
//			AugurDesktop.getInstance().addToDesktop(basictransferstatus);
//		}
//		boolean b=basictransferstatus.update(tse);
//		if(b) {
//			basictransferstatus.close();
//			basictransferstatus=null;
//		}
//	}
	
	
	public boolean requestClose(boolean force) {
		if(force) {
			if(!done) {
				System.out.println("Closing running mission "+this);
			}
			
			return true;
		}
		
		if(!done) {
			return false;
		}
		
		return true;
	}
	
	public void cancelled() {
		if(done) {
			getContainer().closeView(true);
			return;
		}
		if(mission!=null) {
			mission.cancelled();
		}
	}
	
	String missionTitle=null;
	
	public void missionTitle(String s)
	{
		missionTitle=s;
		getContainer().updateTitle(missionTitle);
	}
	
	public void missionEvent(final MissionEvent me) {
		SwingUtilities.invokeLater(new Runnable() { public void run() {
						basicprogressbar.setMaximum(me.getMax());
						basicprogressbar.setValue(me.getProgress());
						basicmsgtextfield.setText(me.getDescription());
					}});
	}
	
	public boolean isAutoClose() {
		return true;
	}
	
	public void missionComplete(String msg) {
		done=true;
		
		if(isAutoClose()) {
			getContainer().closeView(true);
		}
		else {
			cancel_action.putValue(Action.NAME,"Done");
			done=true;
		}
	}
	
}

