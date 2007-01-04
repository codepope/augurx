
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
import javax.swing.JPanel;

public class CixDoorUI implements DoorUI,DoorListener {
    CixDoorUIPanel cd2=new CixDoorUIPanel();
    CixDoor door;
    
    public CixDoorUI(CixDoor door) {
        this.door=door;
        door.addDoorListener(this);
    }
    
    public void doorEventOccurred(DoorEvent de) {
        cd2.log(de);
    }
    
    public JPanel getDoorPanel() {
        return cd2;
    }
}

