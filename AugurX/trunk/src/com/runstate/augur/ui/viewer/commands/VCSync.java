/**
 * VCSync.java
 *
 * @author Dj
 */
package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.ui.viewer.Browser;

public class VCSync extends ViewerDBCommand {

    private String cosyType = "";

    public VCSync(String cosytype) {
        cosyType = cosytype;
    }

    public void VCSync(String cosytype) {
        cosyType = cosytype;
    }

    public void execute(Browser viewer) {
        if (cosyType.equals("Cix") || cosyType.equals("All")) {
            viewer.cmdSyncCixStarted();
            int result = Controller.getController().syncDoor("Cix");
            if (result == -1) {
                viewer.cmdSyncCixError();
            } else {
                viewer.cmdSyncCixDone(result);
            }
        }

        if (cosyType.equals("Twix") || cosyType.equals("All")) {
            viewer.cmdSyncTwixStarted();
            int result = Controller.getController().syncDoor("Twix");
            if (result == -1) {
                viewer.cmdSyncTwixError();
            } else {
                viewer.cmdSyncTwixDone(result);
            }
        }
    }
}

