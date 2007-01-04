/**
 */

package com.runstate.augur.ui.augurpanel;
import com.runstate.augur.ui.*;
import java.util.*;
import javax.swing.*;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.missions.Mission;
import com.runstate.augur.ui.commands.CommandQueue;
import com.runstate.augur.ui.preferences.PreferencesUI;
import com.runstate.augur.ui.viewer.Browser;
import com.runstate.util.ImageCache;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;


public class AugurPanelManger {
	
	private static Logger log=Logger.getLogger("AugurPanelManager");
	
	public Action new_browser_action = new AbstractAction("Browser")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Browser");
			putValue(Action.LONG_DESCRIPTION, "Browser");
			Icon icon = ImageCache.get("thread");
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N,getCommandMask()));
		}
		
		public void actionPerformed(ActionEvent evt) {
			Browser tv=new Browser();
			addToDesktop(tv);
		}
	};
	
	CommandQueue commandqueueview=null;
	
	public Action commandqueue_action = new AbstractAction("Command Queue")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Command Queue");
			putValue(Action.LONG_DESCRIPTION, "Command Queue");
			Icon icon = ImageCache.get("commandqueue");
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C,getCommandMask()|KeyEvent.SHIFT_MASK));
		}
		
		public void actionPerformed(ActionEvent evt) {
			if(commandqueueview==null)
			{
				commandqueueview=new CommandQueue();
				addToDesktop(commandqueueview);
			}
			else
			{
				showView(commandqueueview);
			}
		}
	};
	
	public Action bshconsole_action = new AbstractAction("BeanShell Console")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "BeanShell Console");
			putValue(Action.LONG_DESCRIPTION, "BeanShell Console");
			Icon icon = ImageCache.get("console");
			putValue(Action.SMALL_ICON, icon);
//			putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_B));
		}
		
		public void actionPerformed(ActionEvent evt) {
			BeanShellConsoleAugurPanel bc=new BeanShellConsoleAugurPanel();
			addToDesktop(bc);
		}
	};
	
	
	public Action exit_action = new AbstractAction("Quit Augur")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Quit Augur");
			putValue(Action.LONG_DESCRIPTION, "Quit Augur");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}
		
		public void actionPerformed(ActionEvent evt) {
			Controller.getController().shutdown();
			
		}
	};
	
	
	public Action about_action = new AbstractAction("About Augur")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "About Augur");
			putValue(Action.LONG_DESCRIPTION, "About Augur");
//			putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_A));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A,KeyEvent.SHIFT_MASK | getCommandMask()));
		}
		
		public void actionPerformed(ActionEvent evt) {
			AboutAugur aa=new AboutAugur();
			addToDesktop(aa);
		}
	};
	
	public Action preferences_action = new AbstractAction("Preferences")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Preferences");
			putValue(Action.LONG_DESCRIPTION, "Preferences");
			Icon icon = ImageCache.get("preferences");
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_COMMA,getCommandMask()));
		}
		
		public void actionPerformed(ActionEvent evt) {
			PreferencesUI pv=new PreferencesUI();
			addToDesktop(pv);
//			Augur.getController().showUI();
		}
	};
	
	public Action checkpoint_action = new AbstractAction("Checkpoint")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Checkpoint");
			putValue(Action.LONG_DESCRIPTION, "Checkpoint");
		}
		
		public void actionPerformed(ActionEvent evt) {
			Controller.getGallery().checkpoint();
		}
	};
	
	public JFrame getFirstFrame() {
		// TODO
		return null;
	}
	
	private int getCommandMask() {
		return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	}
	
	static AugurPanelManger aViewManager;
	
	public static AugurPanelManger getManager() {
		return aViewManager;
	}
	
	public static void createManager() {
		if(aViewManager!=null) {
			log.warning("This single manager has been created");
		}
		aViewManager=new AugurPanelManger();
		return;
	}
	
	
	private AugurPanelManger() {
	}
	
	JMenu doorsMenu;
	
	public JMenuBar createMenubar(ArrayList<JMenu> jmenus,JMenu windowmenu) {
		JMenuBar menuBar=new JMenuBar();
		
		
		JMenu fileMenu=new JMenu("File");
		
		fileMenu.setMnemonic('f');
		fileMenu.add(new_browser_action);
		fileMenu.add(commandqueue_action);
		fileMenu.add(preferences_action);
		fileMenu.add(new JSeparator(JSeparator.HORIZONTAL));
		
		doorsMenu=new JMenu("Doors");
		buildDoorsMenu(doorsMenu);
		fileMenu.add(doorsMenu);
		
		fileMenu.add(new JSeparator(JSeparator.HORIZONTAL));
	
		JMenu debugMenu=new JMenu("Debug");
		debugMenu.add(new JMenuItem(bshconsole_action));
		fileMenu.add(debugMenu);
		
		fileMenu.add(exit_action);
		
		menuBar.add(fileMenu);
		
		if(jmenus!=null) {
			for(JMenu jm:jmenus) menuBar.add(jm);
		}
		
		menuBar.add(windowmenu);
		
		JMenu helpMenu=new JMenu("Help");
		helpMenu.add(about_action);
		
		menuBar.add(Box.createHorizontalGlue());
		
		menuBar.add(helpMenu);

		return menuBar;
	}
	
	public void buildDoorsMenu() {
		doorsMenu.removeAll();
		buildDoorsMenu(doorsMenu);
		doorsMenu.updateUI();
	}
	
	public void buildDoorsMenu(JMenu jm) {
		
		if(Controller.getGallery()==null) return;
		
//		jm.add(new JMenuItem(sync_all_action));
//		jm.add(new JSeparator());
		
		Door[] doors=Controller.getGallery().getDoors();
		
		if(doors.length==1) {
			for(Mission m:doors[0].getMiscMissions()) {
				MissionAction msm=new MissionAction(this,m);
				doorsMenu.add(new JMenuItem(msm));
			}
		}
		else {
			for(Door d:doors) {
				JMenu dm=new JMenu(d.getDoorname());
				dm.setIcon(d.getDoorIcon());
				
				for(Mission m:d.getMiscMissions()) {
					MissionAction msm=new MissionAction(this,m);
					dm.add(new JMenuItem(msm));
				}
				jm.add(dm);
			}
		}
	}
	
	
	public boolean shutdown() {
		boolean b=closeAll();
		
		if(!b) {
//			JOptionPane.showMessageDialog(DesktopManager.this,"Window busy");
			return false;
		}
		
		//Augur.getController().shutdown();
		
		//status("Shutting down - please wait...");
		
		return true;
	}
	
	public ArrayList<AugurPanel> getAugurPanels()
	{
		return new ArrayList<AugurPanel>(displayedviews.keySet());
	}
	
	
	LinkedHashMap<AugurPanel, FrameContainer> displayedviews=new LinkedHashMap<AugurPanel, FrameContainer>();
	HashMap<FrameContainer, AugurPanel> adapters=new HashMap<FrameContainer, AugurPanel>();
	
	public void addToDesktop(final AugurPanel aview) {
		
		SwingUtilities.invokeLater(new Runnable() {
					
					public void run() {
						
						FrameContainer aviewframe;
						
						aviewframe=new FrameContainer(AugurPanelManger.this,aview);
						
						displayedviews.put(aview,aviewframe);
						adapters.put(aviewframe,aview);
						
						aviewframe.setVisible(true);
						aviewframe.toFront();
						
						fireAugurPanelOpened(aview);
				
					}
				});
	}
	
	public void showView(final AugurPanel view) {
		SwingUtilities.invokeLater(new Runnable() {
					
					public void run() {
						
						FrameContainer aviewframe=displayedviews.get(view);
						aviewframe.toFront();
					}
				});
	}
	
	public void propertyChange(PropertyChangeEvent p1) {
		System.out.println(p1.getSource().getClass().getName()+"-"+p1.getPropertyName()+" "+p1.getOldValue()+"->"+p1.getNewValue());
		
		if(p1.getPropertyName().equals("title")) {
			FrameContainer apa=displayedviews.get(p1.getSource());
			
			if(apa instanceof FrameContainer) {
				apa.setTitle(apa.getDesktopPanel().getTitle());
			}
		}
		else if(p1.getPropertyName().equals("menus")) {
		}
	}
	
	public boolean closeAll() {
		HashMap<AugurPanel, Object> originalpanels=new HashMap<AugurPanel, Object>(displayedviews);
		Iterator<AugurPanel> i=originalpanels.keySet().iterator();
		
		while(i.hasNext()) {
			AugurPanel ap=i.next();
			
			if(!closeView(ap,false)) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean closeView(final AugurPanel aview,final boolean force) {
	
		if(SwingUtilities.isEventDispatchThread()) {
			if(aview.closeRequest(force)) {
				removeFromDesktop(aview);
				fireAugurPanelClosed(aview);
				return true;
			}
			return false;
		}
		
		// Now bugger about to do this on the event thread. Sigh.
		try {
			final Boolean[] result={ Boolean.FALSE };
			
			SwingUtilities.invokeAndWait(new Runnable() { public void run() {
							if(aview.closeRequest(force)) {
								removeFromDesktop(aview);
								fireAugurPanelClosed(aview);
								result[0]=Boolean.TRUE;
							}
							else {
								result[0]=Boolean.FALSE;
							}
						}
					});
			
			return result[0].booleanValue();
		}
		catch (InvocationTargetException e) {}
		catch (InterruptedException e) {}
		
		return false;
	}
	
	public void removeFromDesktop(AugurPanel aview) {
		FrameContainer desktopframe=displayedviews.get(aview);
		displayedviews.remove(aview);
		if(desktopframe instanceof FrameContainer) {
			FrameContainer af=desktopframe;
			af.setVisible(false);
		}
		
		if(aview==commandqueueview) commandqueueview=null;
	}
	
	private ArrayList<AugurPanelManagerListener> listeners=new ArrayList<AugurPanelManagerListener>();
	
	public void addAugurPanelManagerListener(AugurPanelManagerListener avl) {
		listeners.add(avl);
	}
	
	public void removeAugurPanelManagerListener(AugurPanelManagerListener avl) {
		listeners.remove(avl);
	}
	
	private void fireAugurPanelOpened(AugurPanel av) {
		synchronized(listeners) {
			for(AugurPanelManagerListener avl:listeners) {
				avl.augurPanelOpened(this,av);
			}
		}
	}
	
		private void fireAugurPanelClosed(AugurPanel av) {
		synchronized(listeners) {
			for(AugurPanelManagerListener avl:listeners) {
				avl.augurPanelClosed(this,av);
			}
		}
	}
}

