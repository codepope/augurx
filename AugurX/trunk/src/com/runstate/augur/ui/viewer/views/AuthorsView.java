/**
 * UserView.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.views;
import javax.swing.*;

import com.runstate.augur.controller.Controller;
import com.runstate.augur.gallery.AuthorInfo;
import com.runstate.augur.gallery.GalleryException;
import com.runstate.augur.gallery.events.PoolEvent;
import com.runstate.augur.gallery.listeners.PoolEventListener;
import com.runstate.util.ImageCache;
import com.runstate.augur.ui.textpanes.HTMLPane;
import com.runstate.augur.ui.viewer.MessageStrip;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import com.runstate.augur.ui.viewer.commands.VCRefreshUser;
import com.runstate.augur.ui.viewer.navigators.AuthorsNavigator;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

public class AuthorsView extends AbstractViewPanel implements PoolEventListener {
	
	String auguraddress;
	AuthorInfo ui;
	
	MessageStrip usercss;
	HTMLPane htmlpane;
	
	Action refresh = new AbstractAction("Refresh")
	{ {
			putValue(Action.SHORT_DESCRIPTION, "Refresh");
			Icon icon = ImageCache.get("user");
			putValue(Action.SMALL_ICON, icon);
		}
		
		public void actionPerformed(ActionEvent evt) {
			if(auguraddress!=null) doCommand(new VCRefreshUser(auguraddress));
		}
	};
	
	public AuthorsView(BrowserCommandHandler vch) {
		super(vch);
		
		setLayout(new BorderLayout());
		
		usercss=new MessageStrip();
		
//		JPopupMenu usercssmenu=new JPopupMenu();
//
//		usercssmenu.add(refresh);
//
//		usercss.setMenu(usercssmenu);
		
		htmlpane=new HTMLPane();
		
		add(BorderLayout.NORTH,usercss);
		
		add(BorderLayout.CENTER,new JScrollPane(htmlpane));
		
		Controller.getGallery().addPoolEventListener(this);
	}
	
	public void setView(AuthorsNavigator userspanel)
	{
		// TODO
	}
	
	public void poolEventOccurred(PoolEvent ce) {
		if(auguraddress==null) return;
		
		if(ce.getPool().equals(AuthorInfo.getPoolName())) {
			if(auguraddress.equals(ce.getKey())) {
				ui=(AuthorInfo)ce.getPoolable();
				updateUser();
			}
		}
	}
	
	public void showUser(String newaddress) {
		if(newaddress==null) {
			auguraddress=null;
			ui=null;
			return;
		}
		else {
			auguraddress=newaddress;
			ui=Controller.getGallery().getAuthorInfo(auguraddress);
			if(ui==null) {
				int res=JOptionPane.showConfirmDialog(this,"No userinfo for "+auguraddress+"\nDo you want to retrieve it?","Augur",JOptionPane.YES_NO_OPTION);
				
				if(res==JOptionPane.YES_OPTION) {
					try {
						doCommand(new VCRefreshUser(auguraddress));
						ui=Controller.getGallery().getAuthorInfo(auguraddress);
					}
					catch (GalleryException e) {}
				}
			}
		}
		
		updateUser();
	}
	
	private void updateUser() {
		if(auguraddress==null) {
			htmlpane.setHTML("");
			usercss.setMsg("");
			return;
		}
		
		
		if(ui==null) {
			
			
			usercss.setMsg(auguraddress+" not retrieved");
			htmlpane.setHTML("");
		}
		else {
			usercss.setMsg(ui.getHTMLHeading());
			htmlpane.setHTML(ui.getHTML());
			htmlpane.setCaretPosition(0);
		}
	}
	
}
