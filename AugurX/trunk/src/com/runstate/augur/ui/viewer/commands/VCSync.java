/**
 * VCSync.java
 *
 * @author Dj
 */
package com.runstate.augur.ui.viewer.commands;

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
        if(cosyType.equals("All")) {
            viewer.cmdSyncAllStarted();
            int cixresult = syncCix();
            int twixresult= syncTwix();

            if (cixresult == -1 || twixresult==-1) {
                viewer.cmdSyncAllError();
            }
            else {
                viewer.cmdSyncAllDone(cixresult,twixresult);
            }

            return;
        }

        if (cosyType.equals("Cix")) {
            viewer.cmdSyncCixStarted();
            int result=syncCix();
             if (result == -1) {
                viewer.cmdSyncCixError();
            } else {
                viewer.cmdSyncCixDone(result);
            }
            return;
        }

        if (cosyType.equals("Twix")) {
            viewer.cmdSyncTwixStarted();
            int result=syncTwix();
            if (result == -1) {
                viewer.cmdSyncTwixError();
            } else {
                viewer.cmdSyncTwixDone(result);
            }
            return;
        }
    }

    private int syncCix() {
        return Controller.getController().syncDoor("Cix");
    }

    private int syncTwix() {
        return Controller.getController().syncDoor("Twix");
    }
}

