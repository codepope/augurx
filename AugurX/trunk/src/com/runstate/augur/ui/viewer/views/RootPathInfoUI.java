/**
 * RootPathInfoUI.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.viewer.views;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.gallery.Bundle;
import com.runstate.augur.gallery.BundleManager;


public class RootPathInfoUI extends DefaultPathInfoUI {
    public RootPathInfoUI() {
        super();
    }
    
    public void updatedPathInfo() {
        htmlpane.setHTML(buildSystemStats());
        htmlpane.setCaretPosition(0);
    }
    
    private String buildSystemStats() {
        StringBuilder sb=new StringBuilder();
        
        sb.append("<H1>Augur : Final Client</H1>");

        sb.append("");
        sb.append("<TABLE>");
        sb.append(formatPair("Free Memory:",Runtime.getRuntime().freeMemory()));
        sb.append(formatPair("Total Memory:",Runtime.getRuntime().totalMemory()));
        
        BundleManager bm=Controller.getGallery().getBundleManager();
        Bundle root=bm.getBundle(0L);
        
        sb.append(formatPair("Total number of messages",root.getTotal()));
        sb.append(formatPair("Unread:",root.getUnread()));
       
        sb.append("</TABLE>");
        return Controller.getController().wrapWithStyle(null,sb.toString());
    }
    
    private String formatPair(String label,long value) {
        StringBuilder sb=new StringBuilder();
        sb.append("<TR><TD>");
        sb.append(label);
        sb.append("</TD><TD>");
        sb.append(value);
        sb.append("</TD></TR>");
        return sb.toString();
    }
}

