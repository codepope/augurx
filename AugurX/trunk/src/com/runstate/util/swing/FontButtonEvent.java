/**
 * FontButtonEvent.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util.swing;

import java.awt.Component;
import java.awt.Event;

public class FontButtonEvent
{
	FontButton source;
	String fontname;
	
	public FontButtonEvent(FontButton source,String fontname)
	{
		this.source=source;
		this.fontname=fontname;
	}
	
	/**
	 * Sets Source
	 *
	 * @param    source              a  FontButton
	 */
	public void setSource(FontButton source)
	{
		this.source = source;
	}
	
	/**
	 * Returns Source
	 *
	 * @return    a  FontButton
	 */
	public FontButton getSource()
	{
		return source;
	}
	
	/**
	 * Sets Fontname
	 *
	 * @param    fontname            a  String
	 */
	public void setFontname(String fontname)
	{
		this.fontname = fontname;
	}
	
	/**
	 * Returns Fontname
	 *
	 * @return    a  String
	 */
	public String getFontname()
	{
		return fontname;
	}
	
	
}

