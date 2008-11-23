/**
 * UsersPanel.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.navigators;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.gallery.models.AuthorInfoListModel;
import com.runstate.augur.ui.viewer.MessageStrip;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import com.runstate.augur.ui.viewer.commands.VCShowAboutUser;
import com.runstate.util.ImageCache;
import com.runstate.util.swing.JMenuButton;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class AuthorsNavigator extends AbstractNavigator implements ListSelectionListener {
  //  MessageStrip usercontrol;
    JList userlist;
    JPanel toolbuttons;
    
    Action get_author_info = new AbstractAction("ShowAuthorInfo") {
        {
            putValue(Action.SHORT_DESCRIPTION, "Show Author");
            putValue(Action.NAME, "Show Author");
            putValue(Action.MNEMONIC_KEY,new Integer('A'));
        }
        
        public void actionPerformed(ActionEvent evt) {
            GetAuthorDialog gad=new GetAuthorDialog((JFrame) SwingUtilities.getRoot((Component)AuthorsNavigator.this),
                    true,
                    AuthorsNavigator.this);
            gad.setLocationRelativeTo((JFrame) SwingUtilities.getRoot((Component)AuthorsNavigator.this));
            gad.setVisible(true);
        }
    };
    Action refresh_author_info = new AbstractAction("RefreshAuthorInfo") {
        {
            putValue(Action.SHORT_DESCRIPTION, "Refresh Author");
            putValue(Action.NAME, "Refresh Author");
        }
        
        public void actionPerformed(ActionEvent evt) {
            
        }
    };
    
    Action delete_author_info = new AbstractAction("DeleteAuthorInfo") {
        {
            putValue(Action.SHORT_DESCRIPTION, "Delete Author");
            putValue(Action.NAME, "Delete Author");
        }
        
        public void actionPerformed(ActionEvent evt) {
            
        }
    };
    
    JPopupMenu popup;
    
    public AuthorsNavigator(BrowserCommandHandler vch) {
        super(vch);
        setLayout(new BorderLayout());
        
        userlist=new JList(new AuthorInfoListModel(Controller.getGallery()));
      
        add(BorderLayout.CENTER,new JScrollPane(userlist));
      
        userlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userlist.addListSelectionListener(this);
    }
    
    public String getNavPanelName() { return "Authors"; }
    
    public void focusOnMe() {
        userlist.requestFocus();
    }
    
    public void unselect() {
        int [] empty={};
        userlist.setSelectedIndices(empty);
    }
    
    public void valueChanged(ListSelectionEvent e) {
        if(e.getValueIsAdjusting()) return;
        String auguraddress=(String)userlist.getSelectedValue();
        vch.doCommand(new VCShowAboutUser(auguraddress));
    }
    
    public void addMenus(ArrayList<JMenu> jmenu) {
        JMenu authorsMenu=new JMenu("Authors");
        authorsMenu.setMnemonic('A');
        
        authorsMenu.add(get_author_info);
        authorsMenu.add(refresh_author_info);
        authorsMenu.add(delete_author_info);
        jmenu.add(authorsMenu);
    }
    
    void getAuthor(String string) {
        vch.doCommand(new VCShowAboutUser(string));
    }
}

