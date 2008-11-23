/**
 * DoorUI.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.controller;

import com.runstate.augur.ui.viewer.BrowserCommandHandler;

import javax.swing.JPanel;

public interface DoorUI 
{
       public JPanel getDoorPanel();
       public void setBrowserCommandHandler(BrowserCommandHandler vch);
}

