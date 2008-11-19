
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
import javax.swing.JPanel;

public class TwixDoorUI implements DoorUI,DoorListener {
    TwixDoorUIPanel cd2=new TwixDoorUIPanel();
    TwixDoor door;
    
    public TwixDoorUI(TwixDoor door) {
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

