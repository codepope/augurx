/**
 * RetrieveMessageCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.commands.commands;

import com.runstate.augur.gallery.commands.Parameter;
import com.runstate.augur.gallery.commands.parameters.IntParameter;
import com.runstate.augur.gallery.commands.parameters.BundleParameter;

public interface GetCommand
{
	public void setMsgid(Long msgid);
	public Long getMsgid();
	public void setAll(boolean b);
	public boolean isAll();
}

