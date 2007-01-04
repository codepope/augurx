/**
 * SmarterCardLayout.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util.swing;

import java.awt.CardLayout;
import java.awt.Container;

public class SmarterCardLayout extends CardLayout
{
	String showing=null;
	
	public SmarterCardLayout()
	{
		super();
	}
	
	public void show(Container parent,String name)
	{
		super.show(parent,name);
		showing=name;
	}
	
	public String getShowing()
	{
		return showing;
	}
}

