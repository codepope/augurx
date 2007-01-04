/**
 * BooleanParameter.java
 *
 * @author Created by Omnicore CodeGuide
 */


package com.runstate.augur.gallery.commands.parameters;
import com.runstate.augur.gallery.commands.Parameter;

public class BooleanParameter extends Parameter
{
	boolean value;
	
	public BooleanParameter(String name,String label,boolean value)
	{
		super(name,label);
		this.value=value;
	}
	
	/**
	 * Method setBooleanValue
	 *
	 * @param    b                   a  boolean
	 *
	 */
	public void setBooleanValue(boolean b)
	{
		value=b;
	}
	
	public boolean getBooleanValue()
	{
		return value;
	}
	/**
	 * Sets Value
	 *
	 * @param    Value               a  String
	 */
	public void setValue(String value) {
		this.value = new Boolean(value).booleanValue();
	}
	
	/**
	 * Returns Value
	 *
	 * @return    a  String
	 */
	public String getValue() {
		return Boolean.toString(value);
	}
}

