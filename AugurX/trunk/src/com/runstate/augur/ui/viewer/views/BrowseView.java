/**
 * BundleView.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.views;
import com.runstate.augur.gallery.*;
import com.runstate.augur.ui.viewer.commands.*;
import javax.swing.*;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.commands.Command;
import com.runstate.augur.gallery.events.CoreEvent;
import com.runstate.augur.ui.textpanes.MsgPane;
import com.runstate.augur.ui.textpanes.MsgPaneListener;
import com.runstate.augur.ui.trees.StrandTree;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import com.runstate.augur.ui.viewer.MessageStrip;
import com.runstate.augur.ui.viewer.StatusListener;
import com.runstate.util.ImageCache;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

//
// This class is responsible for displaying a bundle
//

public class BrowseView extends AbstractViewPanel implements BundleManagerListener,TreeSelectionListener,StatusListener,FocusListener,MsgPaneListener {
	CardLayout cardlayout;
	
	JPanel strandviewpanel;
	MessageStrip strandstrip;
	JPopupMenu strandtoolmenu;
	
	JPanel msgviewpanel;
	JPopupMenu msgtoolmenu;
	MessageStrip msgstrip;
	
	ViewerPathInfo infopanel;
	
	JPanel switchingpanel;
	CardLayout switchinglayout;
	
	Bundle currentBundle;
	
	Long mybundleid;
	Long knotid;
	
	StrandTree strandtree;
	
	JToggleButton hotbutton;
	JToggleButton tagbutton;
	JToggleButton ignorebutton;
	JToggleButton unreadbutton;
	
	
	MsgPane msgview;
	Action aboutauthor = new AbstractAction("About Author")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "About Author");
			Icon icon = ImageCache.get("user");
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("A"));
			
		}
		
		public void actionPerformed(ActionEvent evt) {
			doCommand(new VCShowAboutUser(getMsg().getAugurAddress()));
		}
	};
	
	Action expandunread = new AbstractAction("Expand Unread")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Expand Unread");
//			Icon icon = ImageCache.get("user");
//			putValue(Action.SMALL_ICON, icon);
//			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("A"));
			
		}
		
		public void actionPerformed(ActionEvent evt) {
			strandtree.expandUnreadRows(false);
		}
	};
//	Action setignore = new AbstractAction("Set Ignore")
//	{
//		{
//			putValue(Action.SHORT_DESCRIPTION, "Set Ignore");
//			Icon icon = ImageCache.get("setignore");
//			putValue(Action.SMALL_ICON, icon);
//		}
//
//		public void actionPerformed(ActionEvent evt)
//		{
//			doCommand(new VCSetMsgIgnore(getBundleid(),getKnotid(),true));
//		}
//	};
	
	Action original = new AbstractAction("Origin")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Original");
			Icon icon = ImageCache.get("original");
			putValue(Action.SMALL_ICON, icon);
		}
		
		public void actionPerformed(ActionEvent evt) {
			doCommand(new VCOriginal());
		}
	};
	
	Action unreadaction = new AbstractAction("Toggle Read/Unread")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Toggle Read/Unread");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("U"));
			
//			Icon icon = ImageCache.get("setignore");
//			putValue(Action.SMALL_ICON, icon);
		}
		
		public void actionPerformed(ActionEvent evt) {
			System.out.println("Toggle Read");
//			boolean shifted=(evt.getModifiers()&KeyEvent.SHIFT_MASK)!=0;
			Msg msg=getMsg();
			doCommand(new VCSetMsgUnread(getMsg(),!getMsg().isUnread(),false));
		}
	};
	
	Action markStrandRead = new AbstractAction("Mark Strand Read")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Mark Strand Read");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control R"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			Msg msg=getMsg();
			doCommand(new VCSetStrandUnread(msg.getBundleId(),msg.getKnotId(),false));
		}
	};
	
	Action markStrandUnread = new AbstractAction("Mark Strand Unread")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Mark Strand Unread");
//			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control U"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			Msg msg=getMsg();
			doCommand(new VCSetStrandUnread(msg.getBundleId(),msg.getKnotId(),true));
		}
	};
	
	Action chillStrand = new AbstractAction("Chill Strand")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Chill Strand");
//			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control U"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			Msg msg=getMsg();
			doCommand(new VCChillStrand(msg.getBundleId(),msg.getRootKnotId()));
		}
	};
	
	Action ignoreaction = new AbstractAction("Toggle Ignore")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Toggle Ignore");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("I"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			doCommand(new VCSetMsgIgnore(getMsg(),!getMsg().isIgnore(),false));
		}
	};
	
	Action hotaction = new AbstractAction("Toggle Hot")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Toggle Hot");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("H"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			doCommand(new VCSetMsgHot(getMsg(),!getMsg().isHot()));
		}
	};
	
	Action taggedaction = new AbstractAction("Toggle Tagged")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Toggle Tagged");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("T"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			System.out.println("Toggle Tagged");
		}
	};
	
	Action setbundleread = new AbstractAction("Set Read")
	{ {
			putValue(Action.SHORT_DESCRIPTION,"Sets the selected path (and sub paths) as read");
			Icon icon = ImageCache.get("markreadpath");
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift R"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			if(currentBundle==null) return;
			Long id=currentBundle.getBundleid();
			doCommand(new VCSetBundleUnread(id,false));
		}
	};
	
	Action setbundleunread = new AbstractAction("Set Unread")
	{ {
			putValue(Action.SHORT_DESCRIPTION,"Set the selected path (and sub paths) as unread");
			Icon icon = ImageCache.get("markunreadpath");
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift U"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			if(currentBundle==null) return;
			Long id=currentBundle.getBundleid();
			doCommand(new VCSetBundleUnread(id,true));
		}
	};
	
	
	public Action comment_action = new AbstractAction("Comment")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Comment");
			putValue(Action.LONG_DESCRIPTION, "Comment to the currently selected message");
			Icon icon = ImageCache.get("comment");
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("C"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			doCommand(new VCComment(null,null));
		}
	};
	
        	public Action source_action = new AbstractAction("View Source")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "View Source");
			putValue(Action.LONG_DESCRIPTION, "View Source of the currently selected message");
			Icon icon = ImageCache.get("source");
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("V"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			doCommand(new VCViewSource(null,null));
		}
	};
	public Action newmsg_action = new AbstractAction("Say")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Say");
			putValue(Action.LONG_DESCRIPTION, "Say in the current path");
			Icon icon = ImageCache.get("say");
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("N"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			doCommand(new VCSay(null));
		}
	};
	
	public Action original_action = new AbstractAction("Original")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Original");
			putValue(Action.LONG_DESCRIPTION, "Original");
			Icon icon = ImageCache.get("original");
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("O"));
		}
		
		public void actionPerformed(ActionEvent evt) {
			doCommand(new VCOriginal());
		}
	};
	
	public Action refresh_action = new AbstractAction("Refresh")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Refresh");
			putValue(Action.LONG_DESCRIPTION, "Refresh");
			Icon icon = ImageCache.get("refreshpathinfo");
			putValue(Action.SMALL_ICON, icon);
		}
		
		public void actionPerformed(ActionEvent evt) {
			if(currentBundle==null) return;
			
			doCommand(new VCRefreshBundle(currentBundle.getBundleid()));
			
			infopanel.updateTitle();
		}
	};
	
	public Action refreshall_action = new AbstractAction("Refresh All")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Refresh All");
			putValue(Action.LONG_DESCRIPTION, "Refresh All");
			Icon icon = ImageCache.get("refreshpathinfo");
			putValue(Action.SMALL_ICON, icon);
		}
		
		public void actionPerformed(ActionEvent evt) {
			
			
			doCommand(new VCRefreshAllBundle());
			
			infopanel.updateTitle();
		}
	};
	
	public void focusGained(FocusEvent e) {
		System.out.println("gained with "+e);
	}
	
	/**
	 * Invoked when a component loses the keyboard focus.
	 */
	public void focusLost(FocusEvent e) {
		System.out.println("lost with "+e);
	}
	
	JMenu bundlemenu;
	JMenu strandmenu;
	JMenu messagesmenu;
	
	static String STRANDMSG="strandmsg";
	static String INFO="info";
	
	public BrowseView(BrowserCommandHandler vch) {
		super(vch);
		
		setLayout(new BorderLayout(0,0));
		setBorder(BorderFactory.createEmptyBorder());
		strandviewpanel=new JPanel(new BorderLayout(0,0));
		strandviewpanel.setBorder(BorderFactory.createEmptyBorder());
		
		strandtree=new StrandTree();
		strandtree.setLargeModel(true);
		strandtree.setRowHeight(16);
		
		strandstrip=new MessageStrip();
		
		strandviewpanel.add(BorderLayout.CENTER,new JScrollPane(strandtree,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS));
		strandviewpanel.add(BorderLayout.NORTH,strandstrip);
		
		msgviewpanel=new JPanel(new BorderLayout(0,0));
		msgviewpanel.setBorder(BorderFactory.createEmptyBorder());
		
		msgview=new MsgPane(this,false);
		
		msgviewpanel.add(BorderLayout.CENTER,msgview);
		
		JSplitPane msgpanes=new JSplitPane(JSplitPane.VERTICAL_SPLIT,strandviewpanel,msgviewpanel);
		msgpanes.setBorder(BorderFactory.createEmptyBorder());
		
		msgpanes.setOneTouchExpandable(true);
		msgpanes.setDividerLocation(200);
		msgpanes.setRequestFocusEnabled(false);
		msgpanes.setFocusable(false);
		msgpanes.setDividerSize(3);
		
		JPanel msgthreadPanel=new JPanel(new BorderLayout());
		msgthreadPanel.add(msgpanes,BorderLayout.CENTER);
		
		infopanel=new ViewerPathInfo(vch);
		
		switchinglayout=new CardLayout();
		switchingpanel=new JPanel(switchinglayout);
		
		switchingpanel.add(STRANDMSG,msgthreadPanel);
		switchingpanel.add(INFO,infopanel);
		
		bundlemenu=new JMenu("Bundle");
		bundlemenu.setMnemonic('B');
		bundlemenu.add(new JMenuItem(refresh_action));
		bundlemenu.add(new JMenuItem(refreshall_action));
		bundlemenu.add(new JMenuItem(setbundleread));
		bundlemenu.add(new JMenuItem(setbundleunread));
		
		
		strandmenu=new JMenu("Strands");
		strandmenu.setMnemonic('S');
		strandmenu.add(new JMenuItem(expandunread));
		strandmenu.add(new JMenuItem(markStrandRead));
		strandmenu.add(new JMenuItem(markStrandUnread));
		strandmenu.add(new JMenuItem(chillStrand));
		
		messagesmenu=new JMenu("Message");
		messagesmenu.setMnemonic('M');
		messagesmenu.add(new JMenuItem(aboutauthor));
		messagesmenu.add(new JMenuItem(original_action));
		messagesmenu.add(new JSeparator());
		messagesmenu.add(new JMenuItem(newmsg_action));
		messagesmenu.add(new JMenuItem(comment_action));
		messagesmenu.add(new JSeparator());
		messagesmenu.add(new JMenuItem(unreadaction));
		messagesmenu.add(new JMenuItem(hotaction));
		messagesmenu.add(new JMenuItem(taggedaction));
		messagesmenu.add(new JMenuItem(ignoreaction));
                messagesmenu.add(new JSeparator());
                messagesmenu.add(new JMenuItem(source_action));
		
		switchToInfo();
		
		add(BorderLayout.CENTER,switchingpanel);
		
		strandtree.addTreeSelectionListener(this);
		
		Controller.getController().getGallery().getBundleManager().addBundleManagerListener(this);
		
	}
	
	public void newBundleEvent(Bundle b) {
	}
	
	public void updateBundleEvent(Bundle b) {
		if(b==currentBundle) {
			updateTitle();
		}
	}
	
	private void updateTitle() {
		strandstrip.setMsg(currentBundle.getBundlename()+" ["+currentBundle.getWarmunread()+"/"+currentBundle.getUnread()+"/"+currentBundle.getTotal()+"]");
	}
	
	
	public void addMenus(ArrayList<JMenu> jmenu) {
		jmenu.add(bundlemenu);
		if(showinginfo) {
			if(infopanel.getMenu()!=null) jmenu.add(infopanel.getMenu());
		}
		else {
			jmenu.add(strandmenu);
			jmenu.add(messagesmenu);
		}
	}
	
	public void status(JComponent src,String status) {
		if(src==msgview) {
//			msgstrip.setMsg(status);
		}
		else if(src==strandtree) {
			strandstrip.setMsg(status);
		}
	}
	
	public void URLActivated(String anchor) {
		doCommand(new VCExecuteCommand(anchor));
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		TreePath tp=e.getNewLeadSelectionPath();
		if(tp==null) {
			currentknotid=null;
			msgview.setMsg(null);
		}
		else {
			Long msgid=(Long)e.getNewLeadSelectionPath().getLastPathComponent();
			currentknotid=msgid;
			Msg m=currentBundle.getMsg(msgid);
			msgview.setMsg(m);
		}
		updateCommandLine();
	}
	
	boolean showinginfo=false;
	
	private void switchToInfo() {
		if(showinginfo) return;
		switchinglayout.show(switchingpanel,INFO);
		refresh_action.setEnabled(true);
		strandmenu.setEnabled(false);
		messagesmenu.setEnabled(false);
		showinginfo=true;
		updateMenus();
		
	}
	
	private void switchToStrandMsg() {
		if(!showinginfo) return;
		switchinglayout.show(switchingpanel,STRANDMSG);
		refresh_action.setEnabled(false);
		strandmenu.setEnabled(true);
		messagesmenu.setEnabled(true);
		showinginfo=false;
		updateMenus();
		
	}
	
	public void setBundleView(Long bundleid) {
		setBundleView(bundleid,null);
	}
	
	public void setBundleView(Long bundleid,Long knotid) {
		if(currentBundle!=null && mybundleid.equals(bundleid)) {
			// No change in bundle...
			//System.out.println(mybundleid+" "+bundleid);
		}
		else {
			this.mybundleid=bundleid;
			
			if(bundleid==null) {
				currentBundle=null;
				strandstrip.setMsg("No bundle");
			}
			else {
				currentBundle=Controller.getController().getGallery().getBundleManager().getBundle(bundleid);
			}
			
			if(currentBundle==null || currentBundle.isContainer()) {
				switchToInfo();
				infopanel.setBundleid(bundleid);
			}
			else {
				switchToStrandMsg();
				strandtree.setBundle(currentBundle);
				strandstrip.setMsg(currentBundle.getBundlename()+" ["+currentBundle.getWarmunread()+"/"+currentBundle.getUnread()+"/"+currentBundle.getTotal()+"]");
			}
		}
		
		this.knotid=knotid;
		
		if(knotid!=null) {
			strandtree.setSelectedNodes(new Long[]{ knotid });
		}
		else {
			strandtree.setSelectedNodes(new Long[]{});
		}
	}
	
	Long currentknotid;
	
	public void setKnotId(Long knotid) {
		if(knotid==null) {
			currentknotid=null;
			strandtree.clearSelection();
			msgview.setMsg(null);
			return;
		}
		
		Msg m=currentBundle.getMsg(knotid);
		
		if(m==null) {
			// Msg not present error
			int ret=JOptionPane.showConfirmDialog(this,"Message "+knotid+" is not Augur's database\nDo you want to retrieve it?","Unavailable",JOptionPane.YES_NO_OPTION);
			
			if(ret==JOptionPane.YES_OPTION) {
				try {
					Door d=currentBundle.getDoor();
					Command c=(Command)d.createGetCommand(currentBundle.getBundleid(),knotid,false);
					
					Controller.getGallery().addCommand(c);
				}
				catch (GalleryException e) { e.printStackTrace(); }
			}
			
			return;
		}
		
		
		strandtree.setSelectedNodes(new Long[]{knotid});
		
	}
	
	public Long getKnotId() {
		return currentknotid;
	}
	
	public Bundle getCurrentBundle() {
		return currentBundle;
	}
	
	public void original() {
		Long cur=strandtree.getSelected();
		
		if(cur==null) {
			return;
		}
		
		Msg m=currentBundle.getMsg(cur);
		
		Long original=m.getCommentto();
		
		if(original==null) {
			JOptionPane.showMessageDialog(this,"This is the root message","Original",JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		setKnotId(original);
	}

	public void expandUnread(boolean warmonly)
	{
		strandtree.expandUnreadRows(warmonly);
	}
	
	public boolean nextUnreadMsg(boolean warmonly) {
		if(currentBundle==null) return false;
		
		if(currentBundle.isContainer()) return false;
		
		Long starting=strandtree.getSelected();
		Long nextunread=strandtree.getNextUnread(starting,warmonly);
		
		if(nextunread==null) {
			return false; // Outa bullets
		}
		
		strandtree.setSelectedNodes(new Long[] { nextunread });
		
		knotid=nextunread;
		
		return true;
	}
	

	public boolean scrollCurrentMsg() {
		if(currentBundle==null) return false;
		
		if(currentBundle.isContainer()) return false;
		
		if(knotid==null) return false;
		
		if(msgview.getMsg() != null && msgview.getMsg().isUnread()) return msgview.pagedown();
		
		return false;
	}
	
	public Msg getMsg() {
		return msgview.getMsg();
	}
	
}

