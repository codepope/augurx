/**
 * AllMessagesPanel.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.navigators;
import com.runstate.augur.controller.Controller;
import javax.swing.*;

import com.runstate.augur.AugurX;
import com.runstate.augur.gallery.Bundle;
import com.runstate.augur.gallery.BundleManager;
import com.runstate.augur.gallery.BundleManagerListener;
import com.runstate.augur.ui.trees.BundleTree;
import com.runstate.augur.ui.viewer.BrowseConstants;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import com.runstate.augur.ui.viewer.MessageStrip;
import com.runstate.augur.ui.viewer.commands.VCNextUnread;
import com.runstate.augur.ui.viewer.commands.VCShowBundle;
import com.runstate.augur.ui.viewer.commands.VCUndo;
import com.runstate.augur.ui.viewer.views.BrowseView;
import com.runstate.util.ImageCache;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class BrowseNavigator
	extends AbstractNavigator
	implements TreeSelectionListener,BundleManagerListener {
	
	
	BundleTree bundletree;
	BundleManager bundlemanager;
	
	MessageStrip toolstrip;
	
	Action expand_bundles = new AbstractAction("Expand")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Expand All Bundles");
			
			Icon icon = ImageCache.get("expand");
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt RIGHT"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			bundletree.expandRows();
		}
	};
	
	Action collapse_bundles = new AbstractAction("Collapse")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Collape Bundles");
			Icon icon = ImageCache.get("collapse");
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt LEFT"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			bundletree.collapseRows();
		}
	};
	

	
	Action nextunreadscrolled_action = new AbstractAction("Scroll/Next Unread")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Scroll/Next Unread");
			Icon icon = ImageCache.get("nextunread");
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("SPACE"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			doCommand(new VCNextUnread(true));
		}
	};
	
	Action nextunread_action = new AbstractAction("Next Unread")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Next Unread");
			Icon icon = ImageCache.get("nextunread");
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift SPACE"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			doCommand(new VCNextUnread(false));
		}
	};
	
	
	
	public Action warmunread_action = new AbstractAction("Warm Unread")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Warm Unread");
			putValue(Action.LONG_DESCRIPTION, "Warm Unread");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("3"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			setMode(BrowseConstants.WARMUNREAD);
		}
	};
	
	public Action unread_action = new AbstractAction("Unread")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Unread");
			putValue(Action.LONG_DESCRIPTION, "Unread");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("2"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			setMode(BrowseConstants.UNREAD);
		}
	};
	
	public Action all_action = new AbstractAction("All")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "All");
			putValue(Action.LONG_DESCRIPTION, "All");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("1"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			setMode(BrowseConstants.ALL);
		}
	};
	
	public Action sharpest_action = new AbstractAction("Sharpest View")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Sharpest");
			putValue(Action.LONG_DESCRIPTION, "Sharpest");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("0"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			Bundle rootbundle=Controller.getController().getGallery().getBundleManager().getBundle(Controller.getController().getGallery().getBundleManager().getRootBundleId());
			
			if(rootbundle.getWarmunread()>0) {
				setMode(BrowseConstants.WARMUNREAD);
			}
			else if(rootbundle.getUnread()>0) {
				setMode(BrowseConstants.UNREAD);
			}
			else {
				setMode(BrowseConstants.ALL);
			}
		}
	};
	
	Action undo_action = new AbstractAction("Undo")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Undo");
			Icon icon = ImageCache.get("undo");
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("BACK_SPACE"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			System.out.println("Undo");
			doCommand(new VCUndo());
		}
	};
	
	int mode;
	
	
	public BrowseNavigator(BrowserCommandHandler vch) {
		super(vch);
		setLayout(new BorderLayout());
		
		bundlemanager=Controller.getController().getGallery().getBundleManager();
		
		JPanel modepanel=new JPanel(new BorderLayout());
		
		//	viewmode=new JComboBox(modenames);
		
		//	viewmode.setFocusable(false);
		
		bundletree=new BundleTree();
		
		//	selectedbundleid=null;
		
		undo_action.setEnabled(false); // Disable Undo till I am told there is something to undo
		
		toolstrip=new MessageStrip();
	
		add(BorderLayout.NORTH,toolstrip);
		add(BorderLayout.CENTER,new JScrollPane(bundletree));
		
		setToolstripStatusLine(bundlemanager.getBundle(bundlemanager.getRootBundleId()));
		setMode(BrowseConstants.ALL);
		
		bundletree.addTreeSelectionListener(this);
		
		bundlemanager.addBundleManagerListener(this);

		Long rootid=Controller.getController().getGallery().getBundleManager().getRootBundleId();
		setSelectedBundleID(rootid);
		
		
	}
	
	public void addMenus(ArrayList<JMenu> menus) {
		JMenu toolmenu=new JMenu("Browse");
		toolmenu.setMnemonic('R');
		toolmenu.add(new JMenuItem(sharpest_action));
		toolmenu.add(new JMenuItem(all_action));
		toolmenu.add(new JMenuItem(unread_action));
		toolmenu.add(new JMenuItem(warmunread_action));
		toolmenu.add(new JMenuItem(expand_bundles));
		toolmenu.add(new JMenuItem(collapse_bundles));
		toolmenu.add(new JSeparator());
		toolmenu.add(new JMenuItem(nextunreadscrolled_action));
		toolmenu.add(new JMenuItem(nextunread_action));
		toolmenu.add(new JSeparator());
		toolmenu.add(new JMenuItem(undo_action));
		
		
		menus.add(toolmenu);
	}
	
	/**
	 * Method setUndoEnabled
	 *
	 * @param    p0                  a  boolean
	 *
	 */
	public void setUndoEnabled(boolean p0) {
//		System.out.println("Undo set to "+p0);
		undo_action.setEnabled(p0);
	}
	
	public String getNavPanelName() { return "Browse"; }
	
	BrowseView bundleview;
	
	public void setView(BrowseView bundleview) {
		this.bundleview=bundleview;
	}
	
	public void setMode(int mode) {
		
		if(this.mode==mode) return;
		
		Long tmpid=bundletree.getSelectedBundleId();
		
		this.mode=mode;
		
		switch(mode) {
			case BrowseConstants.ALL: bundletree.setModelAll(); setStatus("All"); break;
			case BrowseConstants.UNREAD: bundletree.setModelUnread(); setStatus("Unread"); break;
			case BrowseConstants.WARMUNREAD: bundletree.setModelWarmUnread(); setStatus("Warm Unread"); break;
		}
		
		bundletree.expandRows();
		
		if(tmpid==null) return;
		
		BundleTree.SelectionResult sr=bundletree.setSelectedBundleId(tmpid);
		
		if(sr!=BundleTree.SelectionResult.SUCCESS) {
			System.out.println("Failed to reselect "+tmpid);
			bundletree.setSelectedBundleId(new Long(0));
		}
		
	}
	
	public boolean isSeekingWarm() {
		switch(mode) {
			case BrowseConstants.ALL:return false;
			case BrowseConstants.UNREAD: return false;
			case BrowseConstants.WARMUNREAD: return true;
		}
		
		return false;
	}
	public void focusOnMe() {
		bundletree.requestFocus();
	}
//
//	public void setUndoEnabled(boolean b)
//	{
//		undo_action.setEnabled(b);
//	}
//
	/**
	 * Called whenever the value of the selection changes.
	 * @param e the event that characterizes the change.
	 */
	public void valueChanged(TreeSelectionEvent e) {
		if(e.getNewLeadSelectionPath()!=null) {
			Long selected=bundletree.getSelectedBundleId();
			
			//		selectedbundleid=selected;
			
			//		if(selectedbundleid!=null)
			doCommand(new VCShowBundle(selected));
		}
	}
	
	public void setSelectedBundleID(Long selected) {
//		selectedbundleid=selected;
		BundleTree.SelectionResult rs=bundletree.setSelectedBundleId(selected);
		if(rs==BundleTree.SelectionResult.FAIL_BUT_PRESENT)
		{
			JOptionPane.showConfirmDialog(this,"Not in view, go to all view");
			setMode(BrowseConstants.ALL);
			rs=bundletree.setSelectedBundleId(selected);
		}
	}
	
	public Long getSelectedBundleID() {
		return bundletree.getSelectedBundleId();
	}
	
//	boolean messageWhenNoMore=true;
	
	public void nextUnreadBundle() {
		boolean found;
		
//		Long currbundleid=bundletree.getSelectedBundleId();
		
		switch(mode) {
			case BrowseConstants.WARMUNREAD: found=bundletree.selectNextUnread(true); break;
			default: found=bundletree.selectNextUnread(false);
		}
		
		if(!found) {
			// Right we have run out of messages in this mode...
			switch(mode) {
				case BrowseConstants.ALL: // There's nothing to do by tell them they are out of messages
//					if(messageWhenNoMore)
//					{
					JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(this),"No more unread messages","No More",JOptionPane.INFORMATION_MESSAGE);
//						messageWhenNoMore=false;
//					}
					return;
				case BrowseConstants.UNREAD:
//					if(messageWhenNoMore)
//					{
					int i=JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(this),
														"No more unread messages, switch to all view?","No More",JOptionPane.YES_NO_OPTION);
					
					if(i==JOptionPane.YES_OPTION) {
						setMode(BrowseConstants.ALL);
						Bundle b=bundleview.getCurrentBundle();
						if(b!=null) {
							bundletree.setSelectedBundleId(b.getBundleid());
						}
					}
//						messageWhenNoMore=false;
//					}
					
					return;
				case BrowseConstants.WARMUNREAD:
//					if(messageWhenNoMore)
//					{
					int j=JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(this),
														"No more warm unread messages, switch to unread view?","No More",JOptionPane.YES_NO_OPTION);
					
					if(j==JOptionPane.YES_OPTION) {
						setMode(BrowseConstants.UNREAD);
						Bundle b=bundleview.getCurrentBundle();
						if(b!=null) {
							bundletree.setSelectedBundleId(b.getBundleid());
						}
					}
					
//						messageWhenNoMore=false;
//					}
//					viewmode.setSelectedIndex(ViewerConstants.UNREAD);
//					if(currbundleid==null) bundletree.setSelectedBundleId(new Long(0));
					return;
			}
		}
		else {
//			messageWhenNoMore=true;
		}
	}
	
	/**
	 * Method updateBundleEvent
	 *
	 * @param    bundle              a  Bundle
	 *
	 */
	public void updateBundleEvent(Bundle bundle) {
		if(bundle.getBundleid().equals(bundlemanager.getRootBundleId())) {
			setToolstripStatusLine(bundle);
		}
	}
	
	public void setToolstripStatusLine(Bundle bundle) {
		//toolstrip.setMsg("<HTML><HEAD>"+Augur.getController().getHTMLStyle()+"</HEAD><BODY>"+bundle.getWarmunread()+"/"+bundle.getUnread()+"/"+bundle.getTotal());
		StringBuilder msg=new StringBuilder();
		
		if(bundle.getWarmunread()>0) {
			msg.append(bundle.getWarmunread());
			msg.append(" warm ");
		}
		
		msg.append(bundle.getUnread());
		msg.append(" unread ");
		
                msg.append(bundle.getTotal());
                msg.append(" total");
		toolstrip.setMsg(msg.toString());
		
	}
	
	/**
	 * Method newBundleEvent
	 *
	 * @param    bundle              a  Bundle
	 *
	 */
	public void newBundleEvent(Bundle bundle) {
		// Do nothing, we'll be updated on the updateBundleEvent for root.
	}
	
//	class PathPopupListener extends MouseAdapter
//	{
//		public void mousePressed(MouseEvent e)
//		{
//			maybeShowPopup(e);
//		}
//
//		public void mouseReleased(MouseEvent e)
//		{
//			maybeShowPopup(e);
//		}
//
//		private void maybeShowPopup(MouseEvent e)
//		{
//			if (e.isPopupTrigger())
//			{
//				//TreePath tp=bundletree.getPathForLocation(e.getX(),e.getY());
//				//bundletree.setSelectionPath(tp);
//				toolmenu.show(e.getComponent(),
//							  e.getX(), e.getY());
//			}
//		}
//	}
	
}

