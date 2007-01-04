/**
 * DoorEvent.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.controller;

import java.util.Date;

public class DoorEvent
{
	Door door;
	String msg;
	Date date=new Date();
        
	/**
	 * Constructor
	 *
	 * @param    door                a  Door
	 * @param    msg                 a  String
	 */
	public DoorEvent(Door door, String msg)
	{
		this.door = door;
		this.msg = msg;
	}
	
	/**
	 * Sets Door
	 *
	 * @param    Door                a  Door
	 */
	public void setDoor(Door door)
	{
		this.door = door;
	}
	
	/**
	 * Returns Door
	 *
	 * @return    a  Door
	 */
	public Door getDoor()
	{
		return door;
	}
	
	/**
	 * Sets Msg
	 *
	 * @param    Msg                 a  String
	 */
	public void setMsg(String msg)
	{
		this.msg = msg;
	}
        
        public Date getDate()
        {
            return date;
        }
	
	/**
	 * Returns Msg
	 *
	 * @return    a  String
	 */
	public String getMsg()
	{
		return msg;
	}
	
	public String toString()
	{
		return "DoorEvent("+door.getDoorname()+","+msg+")";
	}
	
}

