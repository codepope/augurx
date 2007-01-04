/**
 * prefsListener.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.controller;

public interface ProfileListener
{
	public void prefChanged(String key,String newval,String oldval);
}

