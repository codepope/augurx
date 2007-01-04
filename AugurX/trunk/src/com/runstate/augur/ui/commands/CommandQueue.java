/**
 * CommandQueue.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.ui.commands;


import javax.swing.*;

import com.runstate.augur.controller.Controller;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.GalleryException;
import com.runstate.augur.gallery.commands.Command;
import com.runstate.augur.gallery.models.AllCommandTableModel;
import com.runstate.augur.gallery.models.CommandTableModel;
import com.runstate.augur.ui.augurpanel.AugurPanel;
import com.runstate.util.ImageCache;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.table.TableColumn;

public class CommandQueue extends AugurPanel {
	JTable command_table;
	ArrayList commands;
	Gallery gallery;
	JPanel toolbar=new JPanel();
	
	public String getPrefsName() {
		return "commandqueue";
	}
	
	public String getTitle() {
		return "Command Queue";
	}
	
	public boolean wantsMenubar() {
		return true;
	}

	public boolean isApplicationMainWindow() {
		return false;
	}
	
	public ImageIcon getIcon() {
		return ImageCache.get("commandqueue");
	}
	
	ArrayList<JMenu> jmenu;
	
	public ArrayList<JMenu> getMenus() {
		if(jmenu!=null) return jmenu;
		
		jmenu=new ArrayList<JMenu>();
		JMenu jm=new JMenu("Commands");
		jm.setMnemonic('C');;
		jm.add(new JMenuItem(delete_action));
		jm.add(new JMenuItem(purge_action));
		jmenu.add(jm);
		
		return jmenu;
	}
	
	
	
	
	public Action delete_action = new AbstractAction("Delete") { {
			putValue(Action.SHORT_DESCRIPTION, "Delete");
			putValue(Action.LONG_DESCRIPTION, "Delete");
			putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_D));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("d"));
		}
		
		// This method is called when the action is invoked
		public void actionPerformed(ActionEvent evt) {
			CommandTableModel ctm=(CommandTableModel)command_table.getModel();
			int i[]=command_table.getSelectedRows();
			if(i==null) return;
			
			ArrayList<Command> todel=new ArrayList<Command>();
			
			for(int j=0;j<i.length;j++) {
				todel.add(ctm.getCommandAt(i[j]));
			}
			
			try {
				Iterator<Command> k=todel.iterator();
				while(k.hasNext()) {
					Command c=k.next();
					gallery.deleteCommand(c);
				}
			} catch (GalleryException e) { e.printStackTrace(); }
		}
	};
	
	public Action purge_action = new AbstractAction("Purge") { {
			putValue(Action.SHORT_DESCRIPTION, "Purge");
			putValue(Action.LONG_DESCRIPTION, "Purge Done Items");
			putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_P));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("p"));
		}
		
		// This method is called when the action is invoked
		public void actionPerformed(ActionEvent evt) {
			CommandTableModel ctm=(CommandTableModel)command_table.getModel();
			
			
			
			ArrayList<Command> todel=new ArrayList<Command>();
			
			for(int j=0;j<ctm.getRowCount();j++) {
				Command c=ctm.getCommandAt(j);
				if(c.isDone() && !c.isKeep()) {
					todel.add(c);
				}
			}
			
			try {
				Iterator<Command> k=todel.iterator();
				while(k.hasNext()) {
					Command c=k.next();
					gallery.deleteCommand(c);
				}
			} catch (GalleryException e) { e.printStackTrace(); }
		}
	};
	
	private void setColumnFixedWidth(JTable jt,int col,int width) {
		TableColumn tc=jt.getColumnModel().getColumn(col);
		tc.setPreferredWidth(width);
		tc.setMinWidth(width);
		tc.setMaxWidth(width);
	}
	
	public String getName() { return "Commands"; }
	
	public CommandQueue() {
		super();
		
		gallery=Controller.getGallery();
		
		
		setLayout(new BorderLayout());
		command_table=new JTable(new CommandTableModel());
//		command_table.setFont(new Font("Dialog",Font.PLAIN,12));
		command_table.setRowSelectionAllowed(true);
		command_table.setColumnSelectionAllowed(false);
		
		setColumnFixedWidth(command_table,0,40);
		setColumnFixedWidth(command_table,1,40);
		setColumnFixedWidth(command_table,2,120);
		setColumnFixedWidth(command_table,3,40);
		setColumnFixedWidth(command_table,4,120);
		
		TableColumn tc=command_table.getColumnModel().getColumn(5);
		tc.setMinWidth(240);
		tc.setPreferredWidth(1024);
		
		tc=command_table.getColumnModel().getColumn(6);
		tc.setCellRenderer(new CommandCellRenderer());
		tc.setCellEditor(new CommandCellEditor(JOptionPane.getFrameForComponent(this)));
		setColumnFixedWidth(command_table,6,20);
		
		add(BorderLayout.CENTER,new JScrollPane(command_table));
		toolbar.add(new JButton(delete_action));
		toolbar.add(new JButton(purge_action));
		add(BorderLayout.SOUTH,toolbar);
	}
	
	public boolean requestClose(boolean force) {
		((AllCommandTableModel)command_table.getModel()).close();
		return true;
	}
}

