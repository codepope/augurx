
/**
 * TwixDoorUIImpl.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.ui;


import com.runstate.augur.twix.TwixDoor;
import com.runstate.augur.controller.DoorEvent;
import com.runstate.augur.controller.DoorListener;
import com.runstate.augur.controller.DoorUI;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import com.runstate.augur.ui.viewer.commands.VCSync;
import javax.swing.JPanel;

public class TwixDoorUI implements DoorUI,DoorListener {
    TwixDoorUIPanel twixDoorUIPanel;
    TwixDoor door;
    BrowserCommandHandler vch;

    public TwixDoorUI(TwixDoor door) {
        this.door=door;
        this.twixDoorUIPanel=new TwixDoorUIPanel(this);
        door.addDoorListener(this);
    }
    
    public void doorEventOccurred(DoorEvent de) {
        twixDoorUIPanel.log(de);
    }
    
    public JPanel getDoorPanel() {
        return twixDoorUIPanel;
    }

    public void syncNow()
    {
        vch.doCommand(new VCSync("Twix"));
    }

    @Override
    public void setBrowserCommandHandler(BrowserCommandHandler vch) {
       this.vch=vch;
    }
}

