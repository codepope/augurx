
/**
 * CixDoorUIImpl.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.ui;

import com.runstate.augur.cix.CixDoor;
import com.runstate.augur.controller.DoorEvent;
import com.runstate.augur.controller.DoorListener;
import com.runstate.augur.controller.DoorUI;
import com.runstate.augur.ui.viewer.BrowserCommandHandler;
import com.runstate.augur.ui.viewer.commands.VCSync;
import javax.swing.JPanel;

public class CixDoorUI implements DoorUI,DoorListener {
    CixDoorUIPanel cd2;
    CixDoor door;
    BrowserCommandHandler vch;

    public CixDoorUI(CixDoor door) {
        this.door=door;
        this.cd2=new CixDoorUIPanel(this);

        door.addDoorListener(this);
    }
    
    public void doorEventOccurred(DoorEvent de) {
        cd2.log(de);
    }
    
    public JPanel getDoorPanel() {
        return cd2;
    }

    public void syncNow()
    {
       vch.doCommand(new VCSync("Cix"));
    }



    @Override
    public void setBrowserCommandHandler(BrowserCommandHandler vch) {
        this.vch=vch;
    }
}

