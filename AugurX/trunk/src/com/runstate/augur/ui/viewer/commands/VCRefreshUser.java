/**
 * VCRefreshUser.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.AuthorInfo;
import com.runstate.augur.gallery.commands.Command;
import com.runstate.augur.ui.viewer.Browser;

public class VCRefreshUser extends ViewerDBCommand
{
	String auguraddress;
	
	public VCRefreshUser(String auguraddress)
	{
		super();
		this.auguraddress=auguraddress;
	}
	
	public void execute(Browser viewer)
	{
		AuthorInfo ai=Controller.getGallery().getAuthorInfo(auguraddress);
		
		Door door=Controller.getGallery().getDoorForAddress(auguraddress);
		
		if(ai==null)
		{
			ai=door.createUserInfo(auguraddress);
			Controller.getGallery().addPoolable(ai);
		}
		
		Command c=door.refresh(ai);
		Controller.getGallery().addCommand(c);
	}
}


