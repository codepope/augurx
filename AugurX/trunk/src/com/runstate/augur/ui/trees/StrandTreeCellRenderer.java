/**
 * ThreadTreeCRenderer.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.trees;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.gallery.Msg;
import com.runstate.augur.gallery.models.StrandTreeModel;
import com.runstate.util.ImageCache;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class StrandTreeCellRenderer extends DefaultTreeCellRenderer
{
	ImageIcon readplusicon=ImageCache.get("treeplus");
	ImageIcon readminusicon=ImageCache.get("treeminus");
	ImageIcon readleaficon=ImageCache.get("treeleaf");
	ImageIcon unreadplusicon=ImageCache.get("treeunreadplus");
	ImageIcon unreadminusicon=ImageCache.get("treeunreadminus");
	ImageIcon unreadleaficon=ImageCache.get("treeunreadleaf");
	
	Color unreadwarm=new Color(209,97,3);
	Color unreadhot=Color.RED;
	Color unread=Color.BLACK;
	Color read=Color.GRAY;
	Color readwarm=unreadwarm.darker();
	Color readhot=unreadhot.darker();
	Color coldread=read.brighter();
	
	Font treefont=null;
	Font boldtreefont=null;
	
	public StrandTreeCellRenderer()
	{
		super();
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
	}
	
	
	private void setUnreadIcon(boolean value,ImageIcon unread,ImageIcon read)
	{
		if (value)
		{
			setIcon(unread);
		}
		else
		{
			setIcon(read);
		}
	}
	
	public Dimension getPreferredSize()
	{
		Dimension d=super.getPreferredSize();
		//	d.setSize(d.getWidth()+10,d.getHeight());
		return d;
	}
	
	public void fontChanged()
	{
		treefont=null;
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
		
		if(treefont==null)
		{
			treefont=Font.decode(Controller.getProfile().get(Prefs.UI_THREADTREE_FONT,"Dialog-PLAIN-12"));
			boldtreefont=treefont.deriveFont(Font.BOLD);
			System.out.println("bold "+boldtreefont.toString());
		}
		
		//	setFont(treefont);
		
		if(value instanceof Long)
		{
			// We actually have a message id here
			// Right....
			Long id=((Long)value);
			
			StrandTreeModel stm=(StrandTreeModel)tree.getModel();
			
			if(hasFocus)
			{
				setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
			}
			else
			{
				setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
			}
			
			if(stm.isUnread(id))
			{
				setFont(boldtreefont);
			}
			else
			{
				setFont(treefont);
			}
			
//			if(!selected)
//			{
			if(stm.isUnread(id))
			{
				if(stm.isHot(id))
				{
					setForeground(unreadhot);
				}
				else if(stm.isWarm(id))
				{
					setForeground(unreadwarm);
				}
			}
			else
			{
				if(stm.isHot(id))
				{
					setForeground(readhot);
				}
				else if(stm.isWarm(id))
				{
					setForeground(readwarm);
				}
				else
				{
					if(stm.isUnreadStrand(id))
					{
						setForeground(read);
					}
					else
					{
						setForeground(coldread);
					}
				}
			}

			Msg msg=stm.getMsg(id);
			
			if(msg!=null)
			{
				boolean unread=msg.isUnread();
				
				if (leaf) {	setUnreadIcon(unread,unreadleaficon,readleaficon);	}
				else { if(expanded)	{ setUnreadIcon(unread,unreadminusicon,readminusicon);	}
					else	{ setUnreadIcon(unread,unreadplusicon,readplusicon); }
				}

				setText(msg.getAuthor()+" - "+msg.getSubject()+" (#"+msg.getKnotId().toString()+")");
			}
			else
			{
				setText(null);
			}
		}
		else
		{
			
			if (leaf) {	setIcon(readleaficon); }
			else
			{
				if(expanded) {	setIcon(readminusicon);	}
				else
				{
					setIcon(readplusicon);
				}
			}
			
			setText(null);
		}
		
		return this;
	}
	
}

