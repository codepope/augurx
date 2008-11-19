/**
 * Browser.java
 *
 * @author Dj
 */

package com.runstate.augur.ui.viewer;
import com.runstate.augur.gallery.*;
import com.runstate.augur.ui.compose.ViewSourcePanel;
import com.runstate.augur.ui.doors.DoorView;
import com.runstate.augur.ui.doors.DoorsNavigator;
import com.runstate.augur.ui.viewer.commands.*;
import com.runstate.augur.ui.viewer.navigators.*;
import com.runstate.augur.ui.viewer.views.*;
import javax.swing.*;
import com.runstate.augur.cix.commands.CixJoinCommand;
import com.runstate.augur.cix.commands.CixResignCommand;
import com.runstate.augur.twix.commands.TwixJoinCommand;
import com.runstate.augur.twix.commands.TwixResignCommand;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.models.StrandTreeModel;
import com.runstate.augur.ui.augurpanel.AugurPanel;
import com.runstate.augur.ui.augurpanel.AugurPanelManger;
import com.runstate.augur.ui.compose.ComposeComment;
import com.runstate.augur.ui.compose.ComposeSay;
import com.runstate.util.ImageCache;
import com.runstate.util.swing.JToolButton;
import com.runstate.util.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Browser extends AugurPanel implements
        BundleUser,
        BrowserCommandHandler,
        ListSelectionListener,
        UserCommandFieldUser {
    
    JPanel navpanel;
    CardLayout navLayout;
    
    JPanel displayPanel;
    CardLayout displayLayout;
    
    BrowseNavigator browseNavigator;
    SearchNavigator searchNavigator;
    AuthorsNavigator authorsNavigator;
    DoorsNavigator doorsNavigator;
    
    BrowseView browseView;
    SearchView searchView;
    AuthorsView authorsView;
    DoorView doorsView;
    
    JList viewcontrol;
    
    JLabel status;
    
    public static final int NAV_VIEWING=0;
    public static final int NAV_AUTHORS=1;
    public static final int NAV_SEARCH=2;
    public static final int NAV_DOORS=3;
    
    String[] modenames=new String[]{ "Browse","Authors","Search","Doors" };
    
    UserCommandField usercommandfield;
    
    
    public Browser() {
        super();
        
        this.setLayout(new BorderLayout());
        
        JPanel chooserpanel=new JPanel(new BorderLayout());
        usercommandfield=new UserCommandField(this);
        usercommandfield.setFont(getFont().deriveFont(10.0f));
        JToolButton synccixbutton=new JToolButton(sync_action_cix);
        JToolButton synctwixbutton=new JToolButton(sync_action_twix);
        synccixbutton.setFocusable(false);
        synctwixbutton.setFocusable(false);
//		chooserpanel.add(BorderLayout.WEST,viewcontrol);
        chooserpanel.add(BorderLayout.CENTER,usercommandfield);
        chooserpanel.add(BorderLayout.EAST,synccixbutton);
        chooserpanel.add(BorderLayout.EAST,synctwixbutton);
        
        displayLayout=new CardLayout();
        displayPanel=new JPanel(displayLayout);
        
        navLayout=new CardLayout();
        navpanel=new JPanel(navLayout);
        
        viewcontrol=new JList();
        
        viewcontrol.addListSelectionListener(this);
        viewcontrol.setFocusable(false);
        viewcontrol.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        viewcontrol.setVisibleRowCount(4);
        
        JSplitPane navsplit=new JSplitPane(JSplitPane.VERTICAL_SPLIT,navpanel,viewcontrol);
        navsplit.setResizeWeight(1.0);
        
        
        pathpanes=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,navsplit,displayPanel);
        
        browseNavigator=new BrowseNavigator(this);
        searchNavigator=new SearchNavigator(this);
        authorsNavigator=new AuthorsNavigator(this);
        doorsNavigator=new DoorsNavigator(this);
        
        browseView=new BrowseView(this);
        authorsView=new AuthorsView(this);
        searchView=new SearchView(this);
        doorsView=new DoorView();
        
        browseNavigator.setView(browseView);
        authorsView.setView(authorsNavigator);
        doorsNavigator.setView(doorsView);
        searchNavigator.setView(searchView);
        
        displayPanel.add(modenames[NAV_VIEWING],browseView);
        displayPanel.add(modenames[NAV_AUTHORS],authorsView);
        displayPanel.add(modenames[NAV_SEARCH],searchView);
        displayPanel.add(modenames[NAV_DOORS],doorsView);
        
        navpanel.add(modenames[NAV_VIEWING],browseNavigator);
        navpanel.add(modenames[NAV_AUTHORS],authorsNavigator);
        navpanel.add(modenames[NAV_SEARCH],searchNavigator);
        navpanel.add(modenames[NAV_DOORS],doorsNavigator);
        
        viewcontrol.setModel(new PanelModel(new AbstractNavigator[] { browseNavigator,authorsNavigator,searchNavigator,doorsNavigator }));
        
        add(BorderLayout.NORTH,chooserpanel);
        add(BorderLayout.CENTER,pathpanes);
        
        viewcontrol.setSelectedIndex(NAV_VIEWING);
        pathpanes.setDividerLocation(175);
        pathpanes.setOneTouchExpandable(true);
        pathpanes.setFocusable(false);
        
    }
    
    public String getTitle() {
        return "Augur Browser";
    }
    
    public boolean wantsMenubar() {
        return true;
    }
    
    public boolean isApplicationMainWindow() {
        return true;
    }
    
    
    
    public String getName() {
        return "Augur Browser";
    }
    
    
    
    public void updateMenus() {
        if(getContainer()==null) return;
        
        getContainer().updateMenus();
    }
    
    public void valueChanged(ListSelectionEvent e) {
        int newmode=viewcontrol.getSelectedIndex();
        setMode(newmode);
    }
    
    public void actionPerformed(ActionEvent e) {
        int newmode=viewcontrol.getSelectedIndex();
        setMode(newmode);
    }
    
    public void setMode(int newmode) {
        switch(newmode) {
            case NAV_VIEWING:
                cmdSwitchToViewing();
                break;
            case NAV_SEARCH:
                cmdSwitchToSearch();
                break;
            case NAV_AUTHORS:
                cmdSwitchToAuthors();
                break;
            case NAV_DOORS:
                cmdSwitchToDoors();
                break;
            default:
                System.err.println("Tried to set mode "+newmode);
        }
    }
    
//    public String go(Object source, String s) {
//        cmdOpenURL(s);
//        return null;
//    }
    
    public String getPrefsName() {
        return "bundleview";
    }
    
    public String getWindowName() { return "Viewer"; }
    
    public boolean shutdown() {
        return true;
    }
    
    Bundle bundle=null;
    JPanel pathPanel;
    JToolBar tools;
    
    JSplitPane msgpanes;
    JSplitPane pathpanes;
    
    Stack<Browser.ReadMsg> readstack=new Stack<Browser.ReadMsg>();
    
    public Action next_action = new AbstractAction("Next View") {
        {
            putValue(Action.SHORT_DESCRIPTION, "Next View");
            putValue(Action.LONG_DESCRIPTION, "Next View");
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
        
        public void actionPerformed(ActionEvent evt) {
            doCommand(new VCShow(VCShow.NEXT));
        }
        
    };
    
    
    public Action prev_action = new AbstractAction("Previous View") {
        {
            putValue(Action.SHORT_DESCRIPTION, "Previous View");
            putValue(Action.LONG_DESCRIPTION, "Previous View");
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_UP,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
        
        public void actionPerformed(ActionEvent evt) {
            doCommand(new VCShow(VCShow.PREV));
        }
        
    };
    
    public Action browse_action = new AbstractAction("Browse") {
        {
            putValue(Action.SHORT_DESCRIPTION, "Browse");
            putValue(Action.LONG_DESCRIPTION, "Browse");
            putValue(Action.MNEMONIC_KEY,new Integer('B'));
//			putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_B,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
        
        public void actionPerformed(ActionEvent evt) {
            doCommand(new VCShow(VCShow.VIEWING));
        }
        
    };
    
    public Action search_action = new AbstractAction("Search") {
        {
            putValue(Action.SHORT_DESCRIPTION, "Search");
            putValue(Action.LONG_DESCRIPTION, "Search");
            putValue(Action.MNEMONIC_KEY,new Integer('S'));
            
//			putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_S,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
        
        public void actionPerformed(ActionEvent evt) {
            doCommand(new VCShow(VCShow.SEARCH));
        }
    };
    
    
    public Action authors_action = new AbstractAction("Authors") {
        {
            putValue(Action.SHORT_DESCRIPTION, "Authors");
            putValue(Action.LONG_DESCRIPTION, "Authors");
            putValue(Action.MNEMONIC_KEY,new Integer('A'));
            
//			putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_A,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
        
        public void actionPerformed(ActionEvent evt) {
            doCommand(new VCShow(VCShow.AUTHORS));
        }
    };
    
    public Action doors_action = new AbstractAction("Doors") {
        {
            putValue(Action.SHORT_DESCRIPTION, "Doors");
            putValue(Action.LONG_DESCRIPTION, "Doors");
            putValue(Action.MNEMONIC_KEY,new Integer('D'));
            
//			putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_A,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
        
        public void actionPerformed(ActionEvent evt) {
            doCommand(new VCShow(VCShow.DOORS));
        }
    };
    
    public Action usercommand_action = new AbstractAction("UserCommand") {
        {
            putValue(Action.SHORT_DESCRIPTION, "UserCommand");
            putValue(Action.LONG_DESCRIPTION, "UserCommand");
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("SLASH"));
        }
        
        public void actionPerformed(ActionEvent evt) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    usercommandfield.takeFocus();
                }
            });
        }
    };
    
    
    
    
    
    private Action sync_action_cix = new AbstractAction("Sync Cix") {
        {
            putValue(Action.SHORT_DESCRIPTION, "Sync Cix");
            putValue(Action.LONG_DESCRIPTION, "Sync Cix");
            Icon icon = ImageCache.get("synccix");
            putValue(Action.SMALL_ICON, icon);
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("F5"));
        }
        
        public void actionPerformed(ActionEvent evt) {
            doCommand(new VCSync("Cix"));
            updateMenus();
        }
    };
    private Action sync_action_twix = new AbstractAction("Sync Twix") {
        {
            putValue(Action.SHORT_DESCRIPTION, "Sync Twix");
            putValue(Action.LONG_DESCRIPTION, "Sync Twix");
            Icon icon = ImageCache.get("synctwix");
            putValue(Action.SMALL_ICON, icon);
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("F6"));
        }
        
        public void actionPerformed(ActionEvent evt) {
            doCommand(new VCSync("Twix"));
            updateMenus();
        }
    };
    
    
    
    private void setUnreadAndDescend(Long id,boolean b) {
        bundle.setMsgUnread(id,b);
        
        StrandTreeModel stm=bundle.getStrandTreeModel();
        
        int cnt=stm.getChildCount(id);
        
        for(int i=0;i<cnt;i++) {
            Long cid=(Long)stm.getChild(id,i);
            setUnreadAndDescend(cid,b);
        }
    }
    
    public void cmdOriginal() {
        browseView.original();
    }
    
//
//	private void setKey(JComponent jc,int key,String name,Action a) {
//		InputMap imap=jc.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//		imap.put(KeyStroke.getKeyStroke(key,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),name);
//		ActionMap amap=jc.getActionMap();
//		amap.put(name,a);
//	}
    
    public JComponent getMainPane() {
        return pathpanes;
    }
    
    public void doCommand(final ViewerCommand nc) {
        
        if(nc instanceof ViewerUICommand) {
            // Execute in the event dispatch thread
            if(SwingUtilities.isEventDispatchThread()) {
                nc.execute(this);
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        nc.execute(Browser.this);
                    }
                });
            }
        } else if(nc instanceof ViewerDBCommand) {
            // Execute in background, always a SwingWorker
            SwingWorker worker=new SwingWorker() {
                public Object construct() {
                    nc.execute(Browser.this);
                    return null;
                }
            };
            worker.start();
        }
    }
    
    public Bundle cmdGetBundle(Long bundleid) {
        return Controller.getController().getGallery().getBundleManager().getBundle(bundleid);
    }
    
    public void cmdShowBundle(Long bundleid,Long knotid) {
        cmdSwitchToViewing();
        
        if(browseNavigator==null) return;
        
        Long current=browseNavigator.getSelectedBundleID();
        
        if(bundleid!=null && current!=null && !current.equals(bundleid)) {
            browseNavigator.setSelectedBundleID(bundleid);
        }
        
        if(browseView==null) return;
        
        browseView.setBundleView(bundleid,knotid);
        
        updateCommandField();
    }
    
    public void cmdNextUnread(boolean scrollcurrent) {
        Bundle bundle=browseView.getCurrentBundle();
        
        if(bundle!=null && !bundle.isContainer()) {
            if(scrollcurrent) {
                boolean b=browseView.scrollCurrentMsg();
                if(b) return;
            }
            
            // We are about to make an undoable change
            Long knotid=browseView.getKnotId();
            
            if(knotid!=null) {
                bundle.setMsgUnread(knotid,false);
                pushUndoStack(new ReadMsg(bundle.getBundleid(),knotid));
            }
            
//			bundleview.setCurrentMsgUnread(false);
            
            if(!browseView.nextUnreadMsg(browseNavigator.isSeekingWarm())) {
                browseNavigator.nextUnreadBundle();
                browseView.expandUnread(browseNavigator.isSeekingWarm());
                browseView.nextUnreadMsg(browseNavigator.isSeekingWarm());
            }
        } else {
            browseNavigator.nextUnreadBundle();
            browseView.expandUnread(browseNavigator.isSeekingWarm());
            browseView.nextUnreadMsg(browseNavigator.isSeekingWarm());
        }
        
        updateCommandField();
        
    }
    
    public void updateCommandLine() {
        updateCommandField();
    }
    
    private void updateCommandField() {
        Bundle b=browseView.getCurrentBundle();
        
        if(b==null) {
            usercommandfield.setText("/");
        } else
            if(b.isContainer()) {
            usercommandfield.setText(b.getBundlename());
            } else {
            if(browseView.getKnotId()==null) {
                usercommandfield.setText(browseView.getCurrentBundle().getBundlename());
            } else {
                usercommandfield.setText(browseView.getCurrentBundle().getBundlename()+":"+browseView.getKnotId());
            }
            }
    }
    
    public void cmdComment(Long bundleid,Long knotid) {
        if(bundleid==null) {
            // No bundleid given; try and work out if a bundle is selected
            
            Bundle bundle=browseView.getCurrentBundle();
            
            if(bundle==null || bundle.isContainer()) {
                System.out.println("Can't comment to a container/root");
                return;
            }
            
            bundleid=bundle.getBundleid();
            
        }
        
        if(knotid==null) {
            // No knotid, same again, try and figure it out
            
            knotid=browseView.getKnotId();
            
            if(knotid==null) {
                System.out.println("No message selected");
                return;
            }
        }
        
        Msg m=Controller.getGallery().getMsg(bundleid,knotid);
        
        
        if(m==null) {
            System.out.println("Hey! No message Wassaup!");
            return;
        }
        
        ComposeComment c=new ComposeComment(m);
        
        AugurPanelManger.getManager().addToDesktop(c);
    }
    
    
    public void cmdSay(Long bundleid) {
        if(bundleid==null) {
            // No bundleid given; try and work out if a bundle is selected
            
            Bundle bundle=browseView.getCurrentBundle();
            
            if(bundle==null || bundle.isContainer()) {
                System.out.println("Can't comment to a container/root");
                return;
            }
            
            bundleid=bundle.getBundleid();
            
        }
        
        ComposeSay c=new ComposeSay(bundleid);
        
        AugurPanelManger.getManager().addToDesktop(c);
    }
    
    public void cmdSyncCixStarted() {
        sync_action_cix.setEnabled(false);
    }
    
    public void cmdSyncCixDone(int i) {
        sync_action_cix.setEnabled(true);
        if(i>0) JOptionPane.showMessageDialog(this,"Sync done with "+i+" new messages");
    }
    
    public void cmdSyncCixError() {
        sync_action_cix.setEnabled(true);
    }
    public void cmdSyncTwixStarted() {
        sync_action_twix.setEnabled(false);
    }
    
    public void cmdSyncTwixDone(int i) {
        sync_action_twix.setEnabled(true);
        if(i>0) JOptionPane.showMessageDialog(this,"Sync done with "+i+" new messages");
    }
    
    public void cmdSyncTwixError() {
        sync_action_twix.setEnabled(true);
    }
    
    int currentview=-1;
    
    public void cmdSwitchToViewing() {
        if(currentview==NAV_VIEWING) return;
        currentview=NAV_VIEWING;
        viewcontrol.setSelectedIndex(NAV_VIEWING);
        navLayout.show(navpanel,modenames[NAV_VIEWING]);
        displayLayout.show(displayPanel,modenames[NAV_VIEWING]);
        navpanel.requestFocus();
        updateMenus();
    }
    
    public void cmdSwitchToSearch() {
        if(currentview==NAV_SEARCH) return;
        
        currentview=NAV_SEARCH;
        viewcontrol.setSelectedIndex(NAV_SEARCH);
        navLayout.show(navpanel,modenames[NAV_SEARCH]);
        displayLayout.show(displayPanel,modenames[NAV_SEARCH]);
        navpanel.requestFocus();
        updateMenus();
    }
    
    public void cmdSwitchToAuthors() {
        if(currentview==NAV_AUTHORS) return;
        
        currentview=NAV_AUTHORS;
        viewcontrol.setSelectedIndex(NAV_AUTHORS);
        navLayout.show(navpanel,modenames[NAV_AUTHORS]);
        displayLayout.show(displayPanel,modenames[NAV_AUTHORS]);
        navpanel.requestFocus();
        authorsNavigator.unselect();
        updateMenus();
    }
    
    public void cmdSwitchToDoors() {
        if(currentview==NAV_DOORS) return;
        currentview=NAV_DOORS;
        viewcontrol.setSelectedIndex(NAV_DOORS);
        navLayout.show(navpanel,modenames[NAV_DOORS]);
        displayLayout.show(displayPanel,modenames[NAV_DOORS]);
        navpanel.requestFocus();
        updateMenus();
    }
    
    public int getCurrentView() { return currentview; }
    
    private void pushUndoStack(ReadMsg rm) {
        readstack.push(rm);
        browseNavigator.setUndoEnabled(true);
    }
    
    private ReadMsg popUndoStack() {
        if(readstack.isEmpty()) {
            browseNavigator.setUndoEnabled(false);
            return null;
        }
        
        ReadMsg rm=readstack.pop();
        
        return rm;
    }
    
    public void cmdShowUser(String auguraddress) {
        if (authorsView.showUser(auguraddress)) {
            cmdSwitchToAuthors();
        }
    }
    
    public void cmdUndo() {
        ReadMsg rm=popUndoStack();
        
        if(rm!=null) {
            Bundle tmp=Controller.getController().getGallery().getBundleManager().getBundle(rm.getBundleid());
            tmp.setMsgUnread(rm.getId(),true);
            doCommand(new VCShowBundle(rm.getBundleid(),rm.getId()));
        } else {
            JOptionPane.showMessageDialog(Browser.this,"No more to undo","Augur",JOptionPane.INFORMATION_MESSAGE);
        }
        
    }
    
    public void cmdViewSource(Long bundleid,Long knotid) {
           if(bundleid==null) {
            // No bundleid given; try and work out if a bundle is selected
            
            Bundle bundle=browseView.getCurrentBundle();
            
            if(bundle==null || bundle.isContainer()) {
                System.out.println("Can't comment to a container/root");
                return;
            }
            
            bundleid=bundle.getBundleid();
            
        }
        
        if(knotid==null) {
            // No knotid, same again, try and figure it out
            
            knotid=browseView.getKnotId();
            
            if(knotid==null) {
                System.out.println("No message selected");
                return;
            }
        }
        
        Msg m=Controller.getGallery().getMsg(bundleid,knotid);
        
        
        if(m==null) {
            System.out.println("Hey! No message Wassaup!");
            return;
        }
        
        ViewSourcePanel v=new ViewSourcePanel(m);
        
        
        AugurPanelManger.getManager().addToDesktop(v);
    }
    public ImageIcon getIcon() {
        return ImageCache.get("thread");
    }
    
    ArrayList<JMenu> menus=null;
    
    public ArrayList<JMenu> getMenus() {
        menus=new ArrayList<JMenu>();
        
        switch(currentview) {
            case NAV_VIEWING:
                browseNavigator.addMenus(menus);
                browseView.addMenus(menus);
                break;
            case NAV_SEARCH:
                searchNavigator.addMenus(menus);
                searchView.addMenus(menus);
                break;
            case NAV_AUTHORS:
                authorsNavigator.addMenus(menus);
                authorsView.addMenus(menus);
                break;
            case NAV_DOORS:
                doorsNavigator.addMenus(menus);
                doorsView.addMenus(menus);
                break;
            default:
                System.err.println("Tried to change menus");
        }
        
        JMenu jm=new JMenu("View");
        jm.setMnemonic('V');
        jm.add(browse_action);
        jm.add(search_action);
        jm.add(authors_action);
        jm.add(doors_action);
        jm.add(new JSeparator());
        jm.add(next_action);
        jm.add(prev_action);
        jm.add(new JSeparator());
        jm.add(usercommand_action);
        jm.add(sync_action_cix);
        jm.add(sync_action_twix);
        menus.add(jm);
        
        
        return menus;
    }
    
//	/**
//	 * Method getActions
//	 *
//	 * @return   an Action[]
//	 *
//	 */
//	public Action[] getActions() {
//		ArrayList toolbar=new ArrayList();
//		return (Action [])toolbar.toArray<Action>(new Action[] {});
//	}
    
    public boolean requestClose(boolean force) {
        return true;
    }
    
    
    
    
    Pattern basicconf=Pattern.compile("^([tw|c]ix):([^0-9$/:]*)$");
    Pattern numref=Pattern.compile("^([tw|c]ix):([0-9]+)$");
    Pattern fullref=Pattern.compile("^([tw|c]ix):([^/]+)/([^:$]*):([0-9]+)$");
    Pattern topicref=Pattern.compile("^([tw|c]ix):([^/]+)/([^$]*)$");
    
    public String convertToAugurPath(String url) {
        
        Pattern pat=Pattern.compile("^([tw|c])ix:((([^/:]+)/([^:]+):([0-9]+))|([^$]+))$");
        
        Matcher mat=basicconf.matcher(url);
        if(mat.matches()) {
            String path="/"+mat.group(1)+"/"+mat.group(2);
            return path;
        }
        
        mat=numref.matcher(url);
        if(mat.matches()) {
           return mat.group(2);
         }
        
        mat=fullref.matcher(url);
        if(mat.matches()) {
            
            return "/"+mat.group(1)+"/"+mat.group(2)+"/"+mat.group(3)+":"+mat.group(4);
        }
        
        mat=topicref.matcher(url);
        if(mat.matches()) {
            return "/"+mat.group(1)+"/"+mat.group(2)+"/"+mat.group(3);
        }
        
        System.out.println("Unmatched");
        return "";
        
    }
    
    public void cmdOpenURL(String sourcecommand) {
        boolean isurl=false;
        boolean isnum=false;
        int intval=0;
        String parsablecommand=null;
        
        
        
        if ((sourcecommand.startsWith("cix:")) || (sourcecommand.startsWith("twix:"))) {
            parsablecommand=convertToAugurPath(sourcecommand);
        } else {
            parsablecommand=sourcecommand;
        }
        
        if(parsablecommand.equals("")) {
            // TODO, replace this nextUnread(true,false);
            //return null;
            return;
        }
        
        try {
            URL u=new URL(parsablecommand);
            isurl=true;
        } catch (MalformedURLException e) {
            isurl=false;
            try {
                intval=Integer.parseInt(parsablecommand);
                isnum=true;
            } catch (NumberFormatException e2) { isnum=false;}
        }
        
        if(isurl) {
            
           try {
                final URI uri= new URI(parsablecommand);
                Runnable r=new Runnable() {
                    public void run() {
                        try {
                            Desktop.getDesktop().browse(uri);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                Thread t=new Thread(r);
                t.start();
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                return;
            }
            
            
            
            //BrowserLauncher.openURL(parsablecommand);
            //  } catch (IOException e) { e.printStackTrace(); }
            return; // null;
        }
        
        if(isnum) {
            browseView.setKnotId(new Long(intval));
        }
        
        if(parsablecommand.startsWith("/")) {
            String parsepath=parsablecommand;
            
            String[] parts=parsepath.split(":");
            
            Long newbundleid=Controller.getController().getGallery().getBundleManager().nameToId(parts[0]);
            
            if(newbundleid==null) {
                int res=JOptionPane.showConfirmDialog(this,"Join "+parts[0]+"?","Augur",JOptionPane.YES_NO_OPTION);
                
                if(res==JOptionPane.YES_OPTION) {
                    try {
                        Long doorid=Controller.getController().getGallery().getDoorForPath(parts[0]).getDoorid();
                        Controller.getController().getGallery().addCommand(new CixJoinCommand(doorid,parts[0]));
                    } catch (GalleryException e) {}
                }
                return; // null;
            } else {
                browseNavigator.setSelectedBundleID(newbundleid);
            }
            
//			if(newbundleid==null || !viewingpanel.getSelectedBundleID().equals(newbundleid)) {
//				viewingpanel.setSelectedBundleID(newbundleid);
//
//				//Augur.getController().getBundleByPath(parts[0]).getBundleRef());
//				moved=true;
//				if(viewingpanel.getSelectedBundleID()==null) {
//					int res=JOptionPane.showConfirmDialog(this,"Join "+parts[0]+"?","Augur",JOptionPane.YES_NO_OPTION);
//
//					if(res==JOptionPane.YES_OPTION) {
//						try {
//							Long bundleid=Augur.getGallery().getBundleManager().nameToId(parts[0]);
//							Long doorid=Augur.getGallery().getDoorByBundleId(bundleid).getDoorid();
//							Augur.getGallery().addCommand(new CixJoinCommand(doorid,parts[0]));
//						}
//						catch (GalleryException e) {}
//					}
//					return; // null;
//				}
//			}
//
            
            if(parts.length==2) {
                Long l=null;
                try {
                    l=Long.decode(parts[1]);
                } catch(NumberFormatException nfe) { return; } // "Unable to parse messages number"; }
                
                Long selectedid=browseView.getKnotId();
                
                if((selectedid==null)||(selectedid!=null) && !l.equals(selectedid)) {
                    browseView.setKnotId(l);
                }
            }
            
            return; // null;
        } else if(parsablecommand.startsWith(".")) {
            return; //"Relative Path Navigation Not Implemented";
        } else if(parsablecommand.startsWith("@")) {
            // Let's do our command set first
            if(parsablecommand.startsWith("@join(")) {
                // Join
                String path=parsablecommand.substring("@join(".length(),parsablecommand.indexOf(')'));
                
                int res=JOptionPane.showConfirmDialog(this,"Join "+path+"?","Augur",JOptionPane.YES_NO_OPTION);
                
                if(res==JOptionPane.YES_OPTION) {
                    try {
                        Door d=Controller.getGallery().getDoorForPath(path);
                        
                        Controller.getController().getGallery().addCommand(new CixJoinCommand(d.getDoorid(),path));
                    } catch (GalleryException e) {}
                }
                return; // null;
                
            }
            if(parsablecommand.startsWith("@resign(")) {
                String path=parsablecommand.substring("@resign(".length(),parsablecommand.indexOf(')'));
                int res=JOptionPane.showConfirmDialog(this,"Resign "+path+"?","Augur",JOptionPane.YES_NO_OPTION);
                
                if(res==JOptionPane.YES_OPTION) {
                    try {
                        Door d=Controller.getGallery().getDoorByName(path);
                        Long bundleid=Controller.getController().getGallery().getBundleManager().nameToId(path);
                        
                        Controller.getController().getGallery().addCommand(new CixResignCommand(d.getDoorid(),bundleid));
                    } catch (GalleryException e) {}
                }
                return; // null;
            }
            if (parsablecommand.startsWith("@author(")) {
                String useraddress=parsablecommand.substring("@author(".length(),parsablecommand.indexOf(')'));
		cmdShowUser(useraddress);
            } else {
                //	if(source instanceof ViewerPathInfo) {
                //		((ViewerPathInfo)source).doCommand(parsablecommand);
                //		return null;
                //	}
            }
            return; // "Augur commands not implemented";
        }
        
        
        return; // "Did not understand "+parsablecommand;
    }
    
    public void goError(String text) {
        JOptionPane.showMessageDialog(this,text);
        //status(text);
    }
    
    public boolean userCommandEntered(String command) {
        System.out.println("Command was "+command);
        
        
        
        
        cmdOpenURL(command);
        return true;
    }
    
    class ReadMsg {
        Long bundleid;
        Long id;
        
        ReadMsg(Long bundleid,Long id) {
            this.bundleid=bundleid;
            this.id=id;
        }
        
        /**
         * Sets Bundleid
         *
         * @param    Bundleid            a  long
         */
        public void setBundleid(Long bundleid) {
            this.bundleid = bundleid;
        }
        
        /**
         * Returns Bundleid
         *
         * @return    a  long
         */
        public Long getBundleid() {
            return bundleid;
        }
        
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public Long getId() {
            return id;
        }
    }
}

