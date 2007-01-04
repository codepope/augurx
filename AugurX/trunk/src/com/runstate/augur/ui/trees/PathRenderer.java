package com.runstate.augur.ui.trees;

import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.gallery.models.BundleTreeModel;
import com.runstate.augur.gallery.models.BundleTreeNode;
import com.runstate.util.ImageCache;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeCellRenderer;


public class PathRenderer extends DefaultTreeCellRenderer {
    boolean allmode;
    boolean showcounts;
    
    Font treefont = null;
    Font boldfont = null;
    
    ImageIcon readplusicon = ImageCache.get("treeplus");
    ImageIcon readminusicon = ImageCache.get("treeminus");
    ImageIcon readleaficon = ImageCache.get("treeleaf");
    ImageIcon unreadplusicon = ImageCache.get("treeunreadplus");
    ImageIcon unreadminusicon = ImageCache.get("treeunreadminus");
    ImageIcon unreadleaficon = ImageCache.get("treeunreadleaf");
    
    Border focus;
    Border nofocus;
    
    public PathRenderer(boolean allmode,boolean showcounts) {
        this.allmode = allmode;
        this.showcounts=showcounts;
        setOpaque(false);
        focus = BorderFactory.createLineBorder(Color.BLACK,1);
        nofocus = BorderFactory.createEmptyBorder(1,1,1,1);
        setBorder(nofocus);
    }
    
    public void fontChanged() {
        treefont=null;
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        
        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);
        
        if (treefont==null) {
            treefont = Font.decode(Controller.getProfile().get(Prefs.UI_PATHTREE_FONT,"Dialog-PLAIN-11"));
            setFont(treefont);
            boldfont = getFont().deriveFont(Font.BOLD);
        }
        
        if (!(value instanceof BundleTreeNode))return this;
        
        BundleTreeNode node = (BundleTreeNode)value;
        
        setFont(treefont);
        
        if (node.getUnread()>0) {
            if (!selected)
                setForeground(Color.BLACK);
            
            if(node.isLeaf()) {
                setIcon(unreadleaficon);
            } else {
                if(expanded) {
                    setIcon(unreadminusicon);
                } else {
                    setIcon(unreadplusicon);
                }
            }
            
            //	setText(node.getBasename()+" "+node.getWarmunread()+"/"+node.getUnread()+"/"+node.getTotal());
            if(showcounts)
            {
                setText(node.getBasename()+"  "+node.getWarmunread()+"/"+node.getUnread());
                if (allmode) setFont(boldfont);
            }
            else
            {
                setText(node.getBasename());
            }
            
            
            
        } else {
            if (!selected)
                setForeground(Color.GRAY);
            if(node.isLeaf()) {
                setIcon(readleaficon);
            } else {
                if(expanded) {
                    setIcon(readminusicon);
                } else {
                    setIcon(readplusicon);
                }
            }
            
            //	setText(node.getBasename()+" ("+node.getTotal()+")");
            setText(node.getBasename());
        }
        
        if(hasFocus) {
            setBorder(focus);
        } else {
            setBorder(nofocus);
        }
        
        
        return this;
    }

    void setAllMode(boolean b) {
        allmode=b;
    }
    
    void setShowCounts(boolean b) {
        showcounts=b;
    }
    
}