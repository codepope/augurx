/**
 * StringParameter.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.commands.parameters;
import com.runstate.augur.gallery.commands.*;

public class StringParameter extends Parameter
{
	String value;
	
	public StringParameter(String name,String label,String value)
	{
		super(name,label);
		this.value=value;
	}
	
	/**
	 * Sets Value
	 *
	 * @param    Value               a  String
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Returns Value
	 *
	 * @return    a  String
	 */
	public String getValue() {
		return value;
	}
}

