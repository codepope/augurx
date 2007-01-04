/**
 * CommentCommand.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.commands.commands;


import com.runstate.augur.gallery.commands.*;
import com.runstate.augur.gallery.commands.parameters.*;

public interface CommentCommand
{
	public void setCommentto(Long commentto);
	public Long getCommentto();
	public void setText(String text);
	public String getText();
}

