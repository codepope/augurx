/*
 * MsgTreeCellRenderer.java
 *
 * Created on October 10, 2006, 6:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.runstate.augur.ui.viewer.views;


import com.runstate.augur.gallery.Msg;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author dj
 */
public class MsgTreeCellRenderer extends DefaultTreeCellRenderer {
    
    /** Creates a new instance of MsgTreeCellRenderer */
    public MsgTreeCellRenderer() {
        super();
    }
    
    public Component getTreeCellRendererComponent(
		JTree tree,
		Object value,
		boolean sel,
		boolean expanded,
		boolean leaf,
		int row,
		boolean hasFocus)
	{
		
		super.getTreeCellRendererComponent(
			tree, value, sel,
			expanded, leaf, row,
			hasFocus);
		
		if(value instanceof DefaultMutableTreeNode)
                {
                    DefaultMutableTreeNode dmtn=(DefaultMutableTreeNode)value;
                    Object o=dmtn.getUserObject();
                    if(o instanceof Msg)
                    {
                        Msg msg=(Msg)dmtn.getUserObject();
                        setText(msg.getBundleName()+" "+msg.getAuthor()+" - "+msg.getSubject()+" (#"+msg.getKnotId().toString()+")");
                    }
                    else
                    {
                        setText(o.toString());
                    }
		}
                else
                {
                    setText(value.toString());
                }
		
		return this;
	}
}
