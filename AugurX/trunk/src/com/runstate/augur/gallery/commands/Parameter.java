/**
 * Parameter.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.gallery.commands;

public class Parameter
{
	
	String name;
	String label;
	
	public Parameter(String name,String label)
	{
		this.name=name;
		this.label=label;
	}
	
	/**
	 * Sets Name
	 *
	 * @param    name                a  String
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns Name
	 *
	 * @return    a  String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets Label
	 *
	 * @param    label               a  String
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Returns Label
	 *
	 * @return    a  String
	 */
	public String getLabel() {
		return label;
	}
}

