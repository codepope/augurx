/**
 * ThreadTable.java
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
import com.runstate.augur.gallery.Msg;
import com.runstate.augur.gallery.models.StrandTreeModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.plaf.TreeUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.text.Position;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

public class StrandTree extends JTree implements MouseListener,ProfileListener,TreeWillExpandListener {
	
	StrandTreeCellRenderer renderer;
	Profile profile=Controller.getProfile();
	Bundle bundle;
	
	public StrandTree() {
		super();
		
		setModel(null);
		
		this.addTreeWillExpandListener(this);
		this.setBorder(new BevelBorder(BevelBorder.RAISED));
		renderer=new StrandTreeCellRenderer();
		
		setCellRenderer(renderer);
		setShowsRootHandles(false);
		setRootVisible(false);
		setVisibleRowCount(16);
		
		TreeUI tui=getUI();
		if(tui instanceof BasicTreeUI) {
			BasicTreeUI ui=(BasicTreeUI)tui;
			ui.setExpandedIcon( null );
			ui.setCollapsedIcon( null );
			ui.setRightChildIndent(4);
			ui.setLeftChildIndent(4);
			putClientProperty("JTree.lineStyle","None");
		}
		setToggleClickCount(2);
		addMouseListener(this);
		
		setLargeModel(true);
		Font f=Font.decode(profile.get(Prefs.UI_PATHTREE_FONT,"Dialog-PLAIN-12"));
		FontMetrics fm=getFontMetrics(f);
		setRowHeight(fm.getHeight());
		profile.addPrefsListener(this);
	}
	
	
	
	public void prefChanged(String key,String newval,String oldval) {
		if(!key.equals(Prefs.UI_THREADTREE_FONT)) return;
		setLargeModel(true);
		Font f=Font.decode(profile.get(Prefs.UI_THREADTREE_FONT,"Dialog-PLAIN-12"));
		FontMetrics fm=getFontMetrics(f);
		setRowHeight(fm.getHeight());
		renderer.fontChanged();
		revalidate();
	}
	
	/**
	 * Invoked when the mouse button has been clicked (pressed
	 * and released) on a component.
	 */
	public void mouseClicked(MouseEvent e) {
		int ICON=10;
		int row=getRowForLocation(e.getX(),e.getY());
		if(row==-1) return;
		TreePath tp=getPathForRow(row);
		int sr=tp.getPathCount()-2;
//		System.out.println("sr "+sr+" e "+e);
//		System.out.println("e.x "+e.getX()+" sr1="+(sr*ICON)+" sr2="+(((sr+1)*ICON)-1));
		
		if(e.getX()>((sr)*ICON) && e.getX()<((sr+1)*ICON)-1) {
			//	System.out.println("On Arrow?");
			if(isExpanded(row)) collapseRow(row);
			else expandRow(row);
		}
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	public void treeWillExpand(TreeExpansionEvent evt) throws ExpandVetoException {
//		System.out.println("TWE "+evt);
//
//		TreePath path = evt.getPath();
//
//		System.out.println("path "+path);
//
//		MsgNode mn=(MsgNode)path.getPathComponent(path.getPathCount()-1);
//
//		System.out.println("Expanding "+mn.getId());
//
//		StrandTreeModel stm=(StrandTreeModel)getModel();
//
//		stm.expanding(mn);
//
//		// Cancel the operation if desired
//		boolean veto = false;
//		if (veto)
//		{
//			throw new ExpandVetoException(evt);
//		}
		
	}
	
	public void treeWillCollapse(TreeExpansionEvent p1) throws ExpandVetoException {
		// TODO
	}
	
	
	
	public void setBundle(Bundle bundle) {
		setModel(null);
		if(this.bundle!=null) {
			this.bundle.releaseStrandTreeModel();
		}
		
		this.bundle=bundle;
		clearSelection();
		setModel(bundle.getStrandTreeModel());
		//	expandUnreadRows();
	}
	
	public Bundle getBundle() {
		return bundle;
	}
	
	/**
	 * Method selectFirst
	 *
	 */
	public void selectFirst() {
		int row=0;
		
		setSelectionRow(row);
		scrollRowToVisible(row);
		
//		fireThreadTableEvent(ThreadTreeEvent.MSGSELECTED);
	}
	
	public void expandRows() {
		Long[] m=getSelectedNodes();
		
		StrandTreeModel stm=((StrandTreeModel)getModel());
		
		expandAll(this,new TreePath(stm.getRoot()),true);
		
		setSelectedNodes(m);
	}
	
	
	private void expandAll(JTree tree, TreePath parent, boolean expand) {
		// Traverse children
		StrandTreeModel stm=(StrandTreeModel)getModel();
		
		Long node = (Long)parent.getLastPathComponent();
		
		if (stm.getChildCount(node) >= 0) {
			for(int i=-0;i<stm.getChildCount(node);i++) {
				Long c=(Long)stm.getChild(node,i);
				TreePath path=parent.pathByAddingChild(c);
				expandAll(tree,path,expand);
			}
		}
		
		// Expansion or collapse must be done bottom-up
		if (expand) {
			tree.expandPath(parent);
		}
		else {
			tree.collapsePath(parent);
		}
    }
	
	public void collapseRows() {
		Long[] m=getSelectedNodes();
		
		int i=0;
		while(i<getRowCount()) {
			collapseRow(i);
			i++;
		}
		setSelectedNodes(m);
	}
	
	public void expandUnreadRows(boolean warmonly) {
		Long[] m=getSelectedNodes();
		
		int i=0;
		StrandTreeModel stm=(StrandTreeModel)getModel();
		
		while(i<getRowCount()) {
			TreePath t=getPathForRow(i);
			Long mn=(Long)t.getLastPathComponent();
			
			if(stm.isUnreadStrand(mn)) {
				if(warmonly)
				{
					if(stm.isWarm(mn)) expandRow(i);
				}
				else expandRow(i);
			}
			i++;
		}
		
		setSelectedNodes(m);
	}
	
	public Msg getSelectedMsg() {
		if(isSelectionEmpty()) {
			return null;
		}
		
		Msg m=bundle.getMsg(((Long)getLastSelectedPathComponent()));
		
		return m;
	}
	
	public Long getSelected() {
		if(isSelectionEmpty()) {
			return null;
		}
		
		return (Long)getLastSelectedPathComponent();
	}
	
	public Long getNextUnread(Long startat,boolean warmonly) {
		if(bundle==null) {
			return null;
		}
		
		StrandTreeModel stm=(StrandTreeModel)getModel();
		
		Long mn=stm.getNextUnread(startat,warmonly);
		
		return mn;
	}
	
	
	public void setSelectedNodes(Long[]  mn) {
		StrandTreeModel stm=(StrandTreeModel)getModel();
		
		//	System.out.println("selecting "+mn);
		clearSelection();
		
		if(mn==null) return;
		
		for(int i=0;i<mn.length;i++) {
			TreePath tp=stm.getTreePath(mn[i]);
			
			makeVisible(tp);
			expandPath(tp);
			addSelectionPath(tp);
			
		}
		
		if(mn.length>0) {
			final TreePath tp=stm.getTreePath(mn[0]);
			int i=getRowForPath(tp);
			scrollRowToVisible(i+(getVisibleRowCount()/4));
			scrollRowToVisible(i-(getVisibleRowCount()/4));
			scrollRowToVisible(i);
		}
	}
	
	public Long[] getSelectedNodes() {
		TreePath[] tp=getSelectionPaths();
		
		if(tp==null) return null;
		
		Long[] nodes=new Long[tp.length];
		
		for(int i=0;i<tp.length;i++) {
			nodes[i]=(Long)(tp[i].getLastPathComponent());
		}
		
		return nodes;
	}
	
	public boolean selectId(Long id) {
		setSelectedNodes(new Long[] { id });
		return true;
	}
	
	
	public TreePath getNextMatch(String prefix, int startingRow,Position.Bias bias) {
		return null;
	}
	
	public void paintComponent(Graphics g) {
		if(profile.getBool(Prefs.UI_TREE_ALIAS,false)) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
								RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
								RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			super.paintComponent(g2);
		}
		else {
			super.paintComponent(g);
		}
	}
	
}

