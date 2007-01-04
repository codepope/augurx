/**
 * PotsEvent.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.events;



public class GalleryEvent
{
	public static final int INFORM=1;
	public static final int OPEN=3;
	
	int type;
	String message;
	
	public GalleryEvent(int type, String message)
	{
		this.type = type;
		this.message = message;
	}
	
	public GalleryEvent(String message)
	{
		this.type = INFORM;
		this.message = message;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	
	public int getType()
	{
		return type;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public boolean isOpen()
	{
		return type==OPEN;
	}
	
	public boolean isInform()
	{
		return type==INFORM;
	}

	public String toString()
	{
		switch(type)
		{
			case OPEN:
				return "GalleryInformEvent[Open,"+message+"]";
			case INFORM:
				return "GalleryInformEvent[Inform,"+message+"]";
			default:
				return "GalleryInformEvent[UNKNOWN("+type+"),"+message+"]";
		}
	}
}

