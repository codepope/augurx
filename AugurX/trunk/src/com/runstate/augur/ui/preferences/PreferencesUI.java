/**
 * NewPreferencesEditor.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.preferences;

import javax.swing.*;

import com.runstate.augur.ui.augurpanel.AugurPanel;
import com.runstate.augur.ui.preferences.panels.PrefPanel;
import com.runstate.util.ImageCache;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

public class PreferencesUI extends AugurPanel implements TreeSelectionListener
{
	
	/**
	 * Method wantsMenubar
	 *
	 * @return   a boolean
	 *
	 */
	public boolean wantsMenubar()
	{
		// TODO
		return false;
	}
	
	/**
	 * Method wantAppMenus
	 *
	 * @return   a boolean
	 *
	 */
	public boolean isApplicationMainWindow()
	{
		// TODO
		return false;
	}
	
	
	/**
	 * Method getPrefsName
	 *
	 * @return   a String
	 *
	 */
	public String getPrefsName()
	{
		return "preferences";
	}
	
	/**
	 * Method getIcon
	 *
	 * @return   an ImageIcon
	 *
	 */
	public ImageIcon getIcon()
	{
		return ImageCache.get("preferences");
	}
	
        public String getTitle()
        {
            return "Preferences";
        }
        
	Object[] treeitems=new Object[]
	{
		"Preferences",
			new Object[] { "Doors", new Item("Cix","CixPrefs") },
			new Object[] { "Gallery", new Item("Database Settings","GalleryDBPrefs") },
			new Object[] { "Desktop Controls",
				new Item("Fonts","FontPrefs"),
				new Item("Spelling","SpellPrefs")
		},
			new Object[] { "Advanced",	new Item("Debug","DebugPrefs") }
	};
	
//	public Action close_action = new AbstractAction("Close")
//	{
//		{
//			putValue(Action.SHORT_DESCRIPTION, "Close");
//			putValue(Action.LONG_DESCRIPTION, "Close");
//
//		}
//
//		public void actionPerformed(ActionEvent evt)
//		{
//			boolean closenow=true;
//
//			if(currentprefs!=null)
//			{
//
//				if(currentprefs.hasChanged())
//				{
//					int result=JOptionPane.showOptionDialog(PreferencesUI.this,
//															"Unsaved changes! Do you want to save them or discard them?",
//															"Preferences Warning",
//															JOptionPane.DEFAULT_OPTION,
//															JOptionPane.QUESTION_MESSAGE,
//															null,
//															new Object[]{ "Save","Discard" },
//															"Discard");
//					if(result==0)
//					{
//						closenow=currentprefs.saveChanged();
//					}
//					else
//					{
//						currentprefs.discardChanges();
//					}
//				}
//			}
//
//			if(closenow) PreferencesUI.this.setVisible(false);
//		}
//	};
	
	
	public Action apply_action = new AbstractAction("Apply")
	{
		{
			putValue(Action.SHORT_DESCRIPTION, "Apply");
			putValue(Action.LONG_DESCRIPTION, "Apply");
			
		}
		
		public void actionPerformed(ActionEvent evt)
		{
			if(PreferencesUI.this.currentprefs!=null)
			{
				PreferencesUI.this.currentprefs.saveChanged();
			}
		}
		
	};
	
	public Action discard_action = new AbstractAction("Discard")
	{
		{
			putValue(Action.SHORT_DESCRIPTION, "Discard");
			putValue(Action.LONG_DESCRIPTION, "Discard");
			
		}
		
		public void actionPerformed(ActionEvent evt)
		{
			if(PreferencesUI.this.currentprefs!=null)
			{
				System.out.println("prefs is "+currentprefs);
				PreferencesUI.this.currentprefs.discardChanges();
			}
		}
		
	};
	
	JTree tree;
	PrefPanelHolder prefpanelholder;
	PrefPanel currentprefs;
	
	public String getName() { return "Preferences"; }
	
	public PreferencesUI()
	{
		super();
		
		/*getContentPane().*/setLayout(new BorderLayout());
		
		
		DefaultMutableTreeNode root=processHierarchy(treeitems);
		
		tree=new JTree(root);
		
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		KeyStroke ks=KeyStroke.getKeyStroke("ctrl A");
		tree.getInputMap().put(ks,"none");
		
		DefaultTreeCellRenderer r=new DefaultTreeCellRenderer();
		r.setLeafIcon(null);
		r.setOpenIcon(null);
		r.setClosedIcon(null);
		tree.setCellRenderer(r);
		tree.addTreeSelectionListener(this);
		//	tree.setRootVisible(false);
		
		prefpanelholder=new PrefPanelHolder();
		JSplitPane jsp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,new JScrollPane(tree),prefpanelholder);
		jsp.setDividerLocation(150);
		/*getContentPane().*/add(BorderLayout.CENTER,jsp);
		
		JPanel toolpanel=new JPanel(new BorderLayout());
		
		JPanel tools=new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		tools.add(new JButton(apply_action));
		tools.add(new JButton(discard_action));
		
		noChanges();
		
		toolpanel.add(BorderLayout.WEST,tools);
		
		add(BorderLayout.SOUTH,toolpanel);
		
		int i=0;
		while(i<tree.getRowCount())
		{
			tree.expandRow(i++);
		}

	}
	
	public void noChanges()
	{
		apply_action.setEnabled(false);
		discard_action.setEnabled(false);
	}
	
	/**
	 * Method hasChanged
	 *
	 */
	public void hasChanged()
	{
		apply_action.setEnabled(true);
		discard_action.setEnabled(true);
	}
	
	private DefaultMutableTreeNode processHierarchy(Object[] hierarchy)
	{
		DefaultMutableTreeNode node =
			new DefaultMutableTreeNode(hierarchy[0]);
		DefaultMutableTreeNode child;
		for(int i=1; i<hierarchy.length; i++)
		{
			Object nodeSpecifier = hierarchy[i];
			if (nodeSpecifier instanceof Object[])  // Ie node with children
				child = processHierarchy((Object[])nodeSpecifier);
			else
				child = new DefaultMutableTreeNode(nodeSpecifier); // Ie Leaf
			node.add(child);
		}
		return(node);
	}
	
	/**
	 * Called whenever the value of the selection changes.
	 * @param tse the tree selection event that characterizes the change.
	 */
    @Override
	public void valueChanged(TreeSelectionEvent tse)
	{
		if(currentprefs!=null)
		{
			if(currentprefs.hasChanged())
			{
				int result=JOptionPane.showOptionDialog(this,
														"Unsaved changes! Do you want to save them or discard then?",
														"Preferences Warning",
														JOptionPane.DEFAULT_OPTION,
														JOptionPane.QUESTION_MESSAGE,
														null,
														new Object[]{ "Save","Discard" },
														"Discard");
				if(result==0)
				{
					currentprefs.saveChanged();
				}
				else
				{
					currentprefs.discardChanges();
				}
			}
			
			currentprefs=null;
			prefpanelholder.setPrefPanel(null);
		}
		
		if(tse.isAddedPath())
		{
			DefaultMutableTreeNode dmtn=(DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
			Object o=dmtn.getUserObject();
			if(o instanceof Object[])
			{
				prefpanelholder.setPrefPanel(null);
			}
			else if(o instanceof Item)
			{
				// Selected something
				Item i=(Item)o;
				if(i.prefpanel!=null)
				{
					prefpanelholder.setPrefPanel(i.prefpanel);
					currentprefs=i.prefpanel;
				}
				
				String classname=i.classname;
				
				if(classname.indexOf('.')==-1)
				{
					classname="com.runstate.augur.ui.preferences.panels."+classname;
				}
				
				try
				{
					
					PrefPanel p=(PrefPanel)Class.forName(classname).newInstance();
					p.setPrefsEditor(this);
					prefpanelholder.setPrefPanel(p);
					currentprefs=p;
					i.prefpanel=p;
				}
				catch (ClassNotFoundException e) { e.printStackTrace(); } catch (InstantiationException e) { e.printStackTrace(); } catch (IllegalAccessException e) { e.printStackTrace(); }
			}
		}
	}
	
	/**
	 * Method closeRequested
	 *
	 * @return   a boolean
	 *
	 */
	public boolean requestClose(boolean force)
	{
		if(force)
		{
			if(currentprefs.hasChanged()) currentprefs.discardChanges();
			return true;
		}
		
		if(currentprefs!=null)
		{
			
			if(currentprefs.hasChanged())
			{
				int result=JOptionPane.showOptionDialog(PreferencesUI.this,
														"Unsaved changes! Do you want to save them or discard them?",
														"Preferences Warning",
														JOptionPane.DEFAULT_OPTION,
														JOptionPane.QUESTION_MESSAGE,
														null,
														new Object[]{ "Save","Discard","Cancel" },
														"Discard");
				if(result==0)
				{
					currentprefs.saveChanged();
					return true;
				}
				else if(result==1)
				{
					currentprefs.discardChanges();
					return true;
				}
				else return false;
			}
		}
		
		return true;
		
	}
	
	
	
	
	
	
	class Item
	{
		String text;
		String classname;
		PrefPanel prefpanel=null;
		
		Item(String text,String classname)
		{
			this.text=text;
			this.classname=classname;
		}
		
		
		
		public String toString()
		{
			return text;
		}
	}
	
	
	
}

