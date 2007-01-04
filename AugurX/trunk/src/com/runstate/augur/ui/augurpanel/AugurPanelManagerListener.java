/**
 * AViewManagerListener.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.augurpanel;

import com.runstate.augur.ui.*;

public interface AugurPanelManagerListener
{
	public void augurPanelOpened(AugurPanelManger avm,AugurPanel av);
	public void augurPanelClosed(AugurPanelManger avm,AugurPanel av);
}

