/**
 * ImageCache.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util;

import java.net.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;

public class ImageCache {
    static HashMap<String,ImageIcon> images=new HashMap<String, ImageIcon>();
    
    static Logger log=Logger.getLogger("com.runstate.util.ImageCache");
    
    static public ImageIcon get(String name) {
        ImageIcon im=images.get(name);
        ClassLoader cm=null;
        
        try {
            cm=Class.forName("com.runstate.augur.support.ResourceAnchor").getClassLoader();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1); }
        
        if(im==null) {
            String path="com/runstate/augur/support/gui/"+name+".gif";
            URL resource=cm.getResource(path);
            
            if(resource==null) {
                log.warning("Unable to locate resource "+name);
                return null;
            }
            
            im=new ImageIcon(resource);
            
            images.put(name,im);
        }
        
        return im;
    }
    
    
}

