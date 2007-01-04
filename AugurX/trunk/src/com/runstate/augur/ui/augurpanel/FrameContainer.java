/**
 * AugurFrame.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.augurpanel;
import com.runstate.augur.controller.Profile;
import com.runstate.augur.ui.*;
import java.awt.event.*;
import javax.swing.*;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.missions.Mission;
import com.runstate.util.ImageCache;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

public class FrameContainer extends JFrame implements ComponentListener,PropertyChangeListener,AugurPanelContainer,AugurPanelManagerListener,ActionListener {
	
	public boolean closeView(boolean force) {
		return augurpanelmanager.closeView(augurpanel,force);
	}
	
	public void showView(AugurPanel view)
	{
		augurpanelmanager.showView(view);
	}
	
	AugurPanel augurpanel;
	AugurPanelManger augurpanelmanager;
	private JMenuBar menubar;
	
	public FrameContainer(AugurPanelManger aviewmanager,AugurPanel aview) {
		super();
		
		this.augurpanel=aview;
		this.augurpanelmanager=aviewmanager;
		
		aviewmanager.addAugurPanelManagerListener(this);
		aview.setContainer(this);
		
		if(aview.getNavPanel()!=null) {
			setLayout(new BorderLayout());
			add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,aview.getNavPanel(),aview),BorderLayout.CENTER);
		}
		else {
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(aview,BorderLayout.CENTER);
		}
		
		windowMenu=new JMenu("Window");
		windowMenu.setMnemonic('W');
		
		ArrayList<AugurPanel> avlist=aviewmanager.getAugurPanels();
		
		for(AugurPanel av:avlist)
		{
			createWindowMenuItem(av);
		}
			
		setMenuBarForAugurPanel();
		
		addComponentListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		setTitle(aview.getTitle());
		
		Profile profile=Controller.getProfile();
		
		Dimension apps=aview.getPreferredSize();
		Dimension wh=new Dimension((int)profile.getDouble(getPrefsName("w"),apps.getWidth()),
                                            (int)profile.getDouble(getPrefsName("h"),apps.getHeight()));
		Point xy=new Point((int)profile.getDouble(getPrefsName("x"),100),
                                    (int)profile.getDouble(getPrefsName("y"),100));
		
		setSize(wh);
		setLocation(xy);
		
		addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent we) {
//						AViewManager.getAViewSupport().closeView(APanelFrame.this.aview,false);
						close(false);
					}
				}
						 );
	}
	
	HashMap<AugurPanel, JMenuItem> winmenu=new HashMap<AugurPanel,JMenuItem>();
	
	private void createWindowMenuItem(AugurPanel av)
	{
		JMenuItem jm=new JMenuItem(av.getName());
		
		if(av==augurpanel)
		{
			jm.setEnabled(false);
		}
	
		jm.addActionListener(this);
		winmenu.put(av,jm);
		
		windowMenu.add(jm);
		
		windowMenu.updateUI();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		for(AugurPanel av:winmenu.keySet())
		{
			if(winmenu.get(av)==e.getSource())
			{
				augurpanelmanager.showView(av);
				return;
			}
		}
	}
	
	JMenu windowMenu=null;
	
	public void augurPanelOpened(AugurPanelManger avm, AugurPanel av) {
		createWindowMenuItem(av);
	}
	
	public void augurPanelClosed(AugurPanelManger avm, AugurPanel av) {
		JMenuItem jm=winmenu.get(av);
		
		windowMenu.remove(jm);
		
		windowMenu.updateUI();
	}
	

	public void updateTitle(String missionTitle) {
		setTitle(missionTitle);
	}
	
	public void setVisible(boolean b)
	{
		super.setVisible(b);
//		aview.becomingVisible(b);
	}
	
	
	private void setMenuBarForAugurPanel() {
		if(menubar!=null)
		{
			menubar.removeAll();
			setJMenuBar(null);
		}
		
		if(augurpanel.wantsMenubar()) {
			ArrayList<JMenu> jm=augurpanel.getMenus();
			
			if(augurpanel.isApplicationMainWindow()) {
				menubar=augurpanelmanager.createMenubar(jm,windowMenu);
			}
			else {
				menubar=new JMenuBar();
				
				for(JMenu j:jm) {
					menubar.add(j);
				}
				
				menubar.add(windowMenu);
			}
			
			setJMenuBar(menubar);
			
			menubar.updateUI();
		}
	}
	
	public void updateMenus() {
		setMenuBarForAugurPanel();
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getSource()==augurpanel) {
			System.out.println("Got prop change for "+augurpanel.getName());
		}
	}
	
	public void close(boolean force) {
		AugurPanelManger.getManager().closeView(augurpanel,force);
	}
	
	public AugurPanel getDesktopPanel() {
		return augurpanel;
	}
	
	private String getPrefsName(String pref) {
		return "ui."+augurpanel.getPrefsName()+"."+pref;
	}
	
	public void componentResized(ComponentEvent p1) {
		Dimension d=getSize();
                Profile profile=Controller.getProfile();
		profile.setDouble(getPrefsName("w"),d.getWidth());
		profile.setDouble(getPrefsName("h"),d.getHeight());
	}
	
	public void componentMoved(ComponentEvent p1) {
		Point p=getLocation();
                Profile profile=Controller.getProfile();
		profile.setDouble(getPrefsName("x"),p.getX());
		profile.setDouble(getPrefsName("y"),p.getY());
	}
	
	public void componentShown(ComponentEvent p1) {
	}
	
	public void componentHidden(ComponentEvent p1) {
	}
	
	JMenu doorsMenu=null;
	
	
	private void buildDoorsMenu() {
		if(doorsMenu==null) {
			doorsMenu=new JMenu("Doors");
		}
		else {
			doorsMenu.removeAll();
		}
		
		buildDoorsMenu(doorsMenu);
		doorsMenu.updateUI();
	}
	
	private void buildDoorsMenu(JMenu jm) {
		
		if(Controller.getGallery()==null) return;
		
//		jm.add(new JMenuItem(sync_all_action));
//		jm.add(new JSeparator());
		
		Door[] doors=Controller.getGallery().getDoors();
		
		for(int i=0;i<doors.length;i++) {
			Door d=doors[i];
			JMenu dm=new JMenu(d.getDoorname());
			dm.setIcon(d.getDoorIcon());
			
			Mission[] ms=d.getMiscMissions();
			for(int j=0;j<ms.length;j++) {
				MissionAction msm=new MissionAction(augurpanelmanager,ms[j]);
				dm.add(new JMenuItem(msm));
			}
			jm.add(dm);
		}
	}
	
}

