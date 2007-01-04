/**
 * CommandFactory.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.commands;

public interface CommandFactory
{
	public abstract Command getJoinCommand(String path);
	public abstract Command getSayCommand(String path);
	public abstract Command getCommentCommand(String path);
	public abstract Command getPathInfoCommand(String path);
	public abstract Command getUserInfoCommand(String path);
	public abstract Command getResignCommand(String path);
	public abstract Command getGetCommand(String path);
}

