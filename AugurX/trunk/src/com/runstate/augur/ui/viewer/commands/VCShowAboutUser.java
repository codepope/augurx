/**
 * VCShowAboutUser.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.controller.Controller;
import com.runstate.augur.gallery.AuthorInfo;
import com.runstate.augur.gallery.GalleryException;
import com.runstate.augur.ui.viewer.Browser;
import javax.swing.JOptionPane;

public class VCShowAboutUser extends ViewerUICommand {
	String auguraddress;
	
	public VCShowAboutUser(String auguraddress) {
		super();
		this.auguraddress=auguraddress;
	}
	
	public void execute(Browser v) {
		AuthorInfo ui=Controller.getGallery().getAuthorInfo(auguraddress);
		if(ui==null) {
			int res=JOptionPane.showConfirmDialog(v,"No userinfo for "+auguraddress+"\nDo you want to retrieve it?","Augur",JOptionPane.YES_NO_OPTION);
			
			if(res==JOptionPane.YES_OPTION) {
				try {
					v.doCommand(new VCRefreshUser(auguraddress));
				}
				catch (GalleryException e) {}
			}
		}
		else {
			v.cmdShowUser(auguraddress);
		}
	}
}

