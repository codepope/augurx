/**
 * AViewSupport.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.augurpanel;
import com.runstate.augur.ui.*;
import com.runstate.augur.ui.status.BasicStatus;



public interface AugurPanelContainer {

	public void updateTitle(String title);
	public boolean closeView(boolean force);
	public void updateMenus();
}

