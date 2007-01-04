/**
 * VCShowViewing.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.commands;

import com.runstate.augur.ui.viewer.Browser;

public class VCShow extends ViewerUICommand {
    public final static int VIEWING=1001;
    public final static int AUTHORS=1002;
    public final static int SEARCH=1003;
    public final static int DOORS=1004;
    public final static int NEXT=1005;
    public final static int PREV=1006;
    
    int type;
    
    
    public VCShow(int type) {
        this.type=type;
    }
    
    public void execute(Browser viewer) {
        int i=viewer.getCurrentView();
        
        switch(type) {
            case VIEWING:
                viewer.cmdSwitchToViewing();
                break;
            case AUTHORS:
                viewer.cmdSwitchToAuthors();
                break;
            case SEARCH:
                viewer.cmdSwitchToSearch();
                break;
            case DOORS:
                viewer.cmdSwitchToDoors();
                break;
            case NEXT:
                
                switch(i) {
                    case Browser.NAV_VIEWING:
                        viewer.cmdSwitchToAuthors();
                        break;
                    case Browser.NAV_AUTHORS:
                        viewer.cmdSwitchToSearch();
                        break;
                    case Browser.NAV_SEARCH:
                        viewer.cmdSwitchToDoors();
                        break;
                    case Browser.NAV_DOORS:
                        viewer.cmdSwitchToViewing();
                        break;
                }
                break;
            case PREV:
                
                switch(i) {
                    case Browser.NAV_VIEWING:
                        viewer.cmdSwitchToDoors();
                        break;
                    case Browser.NAV_AUTHORS:
                        viewer.cmdSwitchToViewing();
                        break;
                    case Browser.NAV_SEARCH:
                        viewer.cmdSwitchToAuthors();
                        break;
                    case Browser.NAV_DOORS:
                        viewer.cmdSwitchToSearch();
                        break;
                }
                break;
                
            default:
                System.out.println("VCShow got a bad value of "+type);
        }
    }
}

