package com.runstate.augur.gallery.events;

import com.runstate.augur.gallery.commands.Command;

public class CommandEvent
{
	public final static int ADDED=1;
	public final static int UPDATED=2;
	public final static int DELETED=3;
	
	Command command;
	String id;
	int type;
	
	/**
	 * Constructor
	 *
	 * @param    command             a  Command
	 */
	public CommandEvent(int type,Command command) {
		this.type=type;
		this.id=command.getId();
		this.command = command;
	}
	
	public CommandEvent(String id)
	{
		this.type=DELETED;
		this.id=id;
		this.command=null;
	}
	
	/**
	 * Sets Id
	 *
	 * @param    Id                  a  String
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Returns Id
	 *
	 * @return    a  String
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets Type
	 *
	 * @param    Type                an int
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * Returns Type
	 *
	 * @return    an int
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Sets Command
	 *
	 * @param    Command             a  Command
	 */
	public void setCommand(Command command) {
		this.command = command;
	}
	
	/**
	 * Returns Command
	 *
	 * @return    a  Command
	 */
	public Command getCommand() {
		return command;
	}

	public String toString()
	{
		return "CommandEvent[type="+type+",id="+id+",command="+command+"]";
	}
}
