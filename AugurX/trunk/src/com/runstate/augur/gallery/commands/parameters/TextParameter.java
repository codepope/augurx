/**
 * TextParameter.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.commands.parameters;
import com.runstate.augur.gallery.commands.*;

public class TextParameter extends Parameter
{
	String text;
	
	public TextParameter(String name,String label,String text)
	{
		super(name,label);
		this.text=text;
	}
	
	/**
	 * Sets Text
	 *
	 * @param    Text                a  String
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Returns Text
	 *
	 * @return    a  String
	 */
	public String getText() {
		return text;
	}
}

