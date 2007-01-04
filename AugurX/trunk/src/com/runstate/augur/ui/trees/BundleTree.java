/**
 * PathTree.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.trees;

import com.runstate.augur.controller.Profile;
import java.awt.*;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.ProfileListener;
import com.runstate.augur.controller.Prefs;
import com.runstate.augur.gallery.Bundle;
import com.runstate.augur.gallery.models.BundleTreeInterface;
import com.runstate.augur.gallery.models.BundleTreeModel;
import com.runstate.augur.gallery.models.BundleTreeNode;
import com.runstate.util.ImageCache;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.TreeUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.text.Position;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class BundleTree extends JTree implements MouseListener,ProfileListener,TreeSelectionListener
{
	public BundleTree()
	{
		
		super();
	
		this.mode=BundleTreeModel.ALL;
		
		setModel(new BundleTreeModel(Controller.getController().getGallery().getBundleManager()));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		profile.addPrefsListener(this);
		
		plr=new PathRenderer(true,true);
		setCellRenderer(plr);
		
		getSelectionModel().setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
		
		TreeUI tui=getUI();
		if(tui instanceof BasicTreeUI)
		{
			BasicTreeUI ui=(BasicTreeUI)tui;
			ui.setExpandedIcon( null );
			ui.setCollapsedIcon( null );
			ui.setRightChildIndent(4);
			ui.setLeftChildIndent(4);
			putClientProperty("JTree.lineStyle","None");
		}
		else
		{
			System.out.println("Unable to assert icon styles on tree");
		}
		
		setToggleClickCount(2);
		setScrollsOnExpand(true);
		setExpandsSelectedPaths(true);
		setEditable(false);
		setSelectionRow(0);
		addMouseListener(this);
		setShowsRootHandles(false);
		
		setLargeModel(true);
		Font f=Font.decode(profile.get(Prefs.UI_PATHTREE_FONT,"Dialog-PLAIN-12"));
		FontMetrics fm=getFontMetrics(f);
		setRowHeight(fm.getHeight());
		addTreeSelectionListener(this);
	}
	
	Profile profile=Controller.getProfile();
	
	PathRenderer plr;
	
	public void mouseClicked(MouseEvent e)
	{
		int ICON=10;
		int row=getRowForLocation(e.getX(),e.getY());
		if(row==-1) return;
		TreePath tp=getPathForRow(row);
		int sr=tp.getPathCount()-2;

		if(e.getX()>((sr)*ICON) && e.getX()<((sr+1)*ICON)-1)
		{
			//	System.out.println("On Arrow?");
			if(isExpanded(row)) collapseRow(row);
			else expandRow(row);
		}
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	int mode;
	
	public int getMode()
	{
		return mode;
	}
	
	public void setModelAll() { setMode(BundleTreeModel.ALL); }
	public void setModelUnread() { setMode(BundleTreeModel.UNREAD); }
	public void setModelWarmUnread() { setMode(BundleTreeModel.WARMUNREAD); }
	
	private void setMode(int mode)
	{
		if(this.mode==mode) return;
		
		this.mode=mode;
		
                plr.setAllMode(mode==BundleTreeModel.ALL);
                
          
		// Svae the selection and clear it
		
		Long tmpsel=getSelectedBundleId();
		
		((BundleTreeModel)getModel()).setMode(mode);
		
		if(tmpsel!=null) setSelectedBundleId(tmpsel);
		else setSelectedBundleId(Controller.getController().getGallery().getBundleManager().getRootBundleId());
	}
		
	public void prefChanged(String key,String newval,String oldval)
	{
		if(!key.equals(Prefs.UI_PATHTREE_FONT)) return;
		setLargeModel(true);
		Font f=Font.decode(profile.get(Prefs.UI_PATHTREE_FONT,"Dialog-PLAIN-12"));
		FontMetrics fm=getFontMetrics(f);
		setRowHeight(fm.getHeight());
		plr.fontChanged();
		revalidate();
	}
	
	
	public boolean selectNextUnread(boolean warmonly)
	{
		Long bid=getSelectedBundleId();
		BundleTreeNode start;
		
		if(bid!=null) start=((BundleTreeModel)getModel()).getBundleTreeNode(bid);
		else start=(BundleTreeNode)getModel().getRoot();
		return selectNextUnread(start,warmonly);
	}
	
	public boolean selectNextUnread(BundleTreeNode start,boolean warmonly)
	{
		
		BundleTreeNode nextunreadnode=findUnread(start,warmonly);
		
		if(nextunreadnode==null)
		{
			return false;
		}
		
		setSelectedBundleId(nextunreadnode.getBundleid());
		
		return true;
	}
	
	private BundleTreeNode findUnread(BundleTreeNode start,boolean warmonly)
	{
		BundleTreeNode curr=start;
		
//		System.out.println("Starting "+start);
		
		while(curr!=null)
		{
			if(curr.isUnread(warmonly))
			{
				if(curr.isLeaf()) return curr;
				
				curr=(BundleTreeNode)curr.getFirstChild();
			}
			else
			{
				BundleTreeNode ncurr=(BundleTreeNode)curr.getNextLeaf();
				
				if(ncurr==null) curr=(BundleTreeNode)curr.getNextSibling();
				else curr=ncurr;
			}
		}
		
		if(start.equals((BundleTreeNode)getModel().getRoot())) return null;
		
		return findUnread((BundleTreeNode)getModel().getRoot(),warmonly);
	}
	
	
	public String convertValueToText(Object value, boolean selected,
									 boolean expanded, boolean leaf, int row,
									 boolean hasFocus)
	{
		if(value instanceof BundleTreeNode)
		{
			if(((BundleTreeNode)value).getBundle()==null)
			{
				return "NULL BUNDLE";
			}
			
			return ((BundleTreeNode)value).getFullPath();
		}
		
		if(value != null)
			return value.toString();
		return "";
    }
	
	
	public void expandUnreadRows()
	{
		int row=0;
		while (row < getRowCount())
		{
			BundleTreeNode ptn=(BundleTreeNode)getPathForRow(row).getLastPathComponent();
			
			if(ptn.getUnread()>0)
			{
				expandRow(row);
			}
			else
			{
				collapseRow(row);
			}
			
			row++;
		}
	}
	
	public void expandRows()
	{
		int row=0;
		while (row < getRowCount())
		{
			expandRow(row);
			row++;
		}
	}
	
	
	
	public void collapseRows()
	{
		int row=0;
		while (row < getRowCount() )
		{
			collapseRow(row);
			row++;
		}
	}
	
	public Long getSelectedBundleId()
	{
		TreePath tp=getSelectionPath();
		if(tp==null) return null;
		
		BundleTreeNode btn=(BundleTreeNode)(tp.getLastPathComponent());
		return btn.getBundleid();
	}
	
	public enum SelectionResult { SUCCESS, FAIL_BUT_PRESENT, FAIL }
	
	
	public SelectionResult setSelectedBundleId(Long bundleid)
	{

		if(bundleid==null)
		{
			System.out.println("ATTEMPTING TO SET NULL BUNDLEID");
			return SelectionResult.FAIL;
		}
		
		BundleTreeInterface ptm=(BundleTreeInterface)getModel();
//		if(ptm==null) return false;
		
		BundleTreeNode ptn=ptm.getBundleTreeNode(bundleid);
		
		if(ptn==null)
		{
			Bundle bm=Controller.getGallery().getBundleManager().getBundle(bundleid);
			if(bm==null) return SelectionResult.FAIL;
			return SelectionResult.FAIL_BUT_PRESENT;
		}
		
		final TreePath tp=new TreePath(ptm.getPathToRoot(ptn));
		
		this.getSelectionModel().setSelectionPath(tp);
		
		int i=getRowForPath(tp);
		scrollRowToVisible(i-5);
		scrollRowToVisible(i+5);
		scrollPathToVisible(tp);
		
		
		return SelectionResult.SUCCESS;
	}
	
	public TreePath getNextMatch(String prefix, int startingRow,Position.Bias bias)
	{
		return null;
	}
	
	public void paintComponent(Graphics g)
	{
		if(profile.getBool(Prefs.UI_TREE_ALIAS,false))
		{
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
								RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
								RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			super.paintComponent(g2);
		}
		else
		{
			super.paintComponent(g);
		}
	}
	
	Long lastselectedbundleid;
	Long nextbundleid;
	
	public void valueChanged(TreeSelectionEvent e)
	{
		if(e.getNewLeadSelectionPath()==null&&e.getOldLeadSelectionPath()!=null)
		{
			SelectionResult sr=setSelectedBundleId(lastselectedbundleid);
			if(sr!=SelectionResult.SUCCESS)
			{
				setSelectedBundleId(nextbundleid);
			}
		}
		else
		{
			BundleTreeNode btn=((BundleTreeNode)e.getNewLeadSelectionPath().getLastPathComponent());
			BundleTreeNode nbtn=((BundleTreeNode)btn.getNextSibling());
			
			lastselectedbundleid=btn.getBundleid();
			if(nbtn!=null)
			{
				nextbundleid=nbtn.getBundleid();
			}
			else
			{
				BundleTreeNode pabtn=(BundleTreeNode)btn.getParent();
				if(pabtn!=null)
				{
					BundleTreeNode npabtn=(BundleTreeNode)pabtn.getNextSibling();
					
					if(npabtn!=null)
					{
						nextbundleid=npabtn.getBundleid();
					}
					else
					{
						
						nextbundleid=Controller.getController().getGallery().getBundleManager().getRootBundleId();
					}
				}
				else
				{
					nextbundleid=lastselectedbundleid;
				}
				
			}
		}
	}
	
}

