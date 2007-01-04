/**
 * FindDialogUser.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.textpanes;
import java.awt.Component;



public interface FindDialogListener
{
	
	/**
	 * Method noselect
	 *
	 */
	public void noselect();
	
	
	/**
	 * Method select
	 *
	 * @param    i                   an int
	 * @param    length              an int
	 *
	 */
	public void select(int i, int length);
	
	
	/**
	 * Method getPlainText
	 *
	 * @return   a  String
	 */
	public String getPlainText();
	
	
	/**
	 * Method getComponentForCentre
	 *
	 * @return   a  Component
	 */
	public Component getComponentForCentre();
	
}

