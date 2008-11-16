/**
 * CommandListModel.java
 *
 * @author Created by Omnicore CodeGuide
 */


package com.runstate.augur.gallery.models;

import com.runstate.augur.controller.Controller;
import com.runstate.augur.gallery.Gallery;
import com.runstate.augur.gallery.GalleryException;
import com.runstate.augur.gallery.commands.Command;
import com.runstate.augur.gallery.events.CommandEvent;
import com.runstate.augur.gallery.listeners.CommandEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;


public class AllCommandTableModel extends AbstractTableModel implements CommandEventListener {
	
	Gallery gallery;
	ArrayList<Command> commands=new ArrayList<Command>();
	SimpleDateFormat sdf;
	boolean pendingOnly;
	
	public AllCommandTableModel(Gallery g,boolean pendingOnly) {
		gallery=g;
		sdf=Controller.getController().getDateFormat();
		
		try {
			commands=g.getCommands(pendingOnly);
			g.addCommandEventListener(this);
		} catch (GalleryException e) { e.printStackTrace(); }
	}
	
	/**
	 * Sets PendingOnly
	 *
	 * @param    pendingOnly         a  boolean
	 */
	public void setPendingOnly(boolean pendingOnly)
	{
		this.pendingOnly = pendingOnly;
	}
	
	/**
	 * Returns PendingOnly
	 *
	 * @return    a  boolean
	 */
	public boolean isPendingOnly()
	{
		return pendingOnly;
	}
	
	public void close()
	{
		gallery.removeCommandEventListener(this);
	}
	
	public int getRowCount() {
		return commands.size();
	}
	
	public int getColumnCount() {
		return 7;
	}
	
	public String getColumnName(int colIndex) {
		switch(colIndex) {
			case 0: return "Hold";
			case 1: return "Keep";
			case 2: return "Created";
			case 3: return "Done";
			case 4: return "Date Done";
			case 5: return "Command";
			case 6: return "";
		}
		
		return null;
	}
	
	public Class getColumnClass(int colIndex) {
		switch(colIndex) {
			case 0: return Boolean.class;
			case 1: return Boolean.class;
			case 2: return String.class;
			case 3: return Boolean.class;
			case 4: return String.class;
			case 5: return String.class;
		}
		return null;
	}
	
	public Object getValueAt(int rowIndex,int colIndex) {
		Command c=commands.get(rowIndex);
		
		switch(colIndex) {
			case 0:
				return new Boolean(c.isHold());
			case 1:
				return new Boolean(c.isKeep());
			case 2:
				return sdf.format(c.getCreated());
			case 3:
				return new Boolean(c.isDone());
			case 4:
				return c.getWhendone()==null?"":sdf.format(c.getWhendone());
			case 5:
				return c.toString();
			case 6:
				return c;
		}
		
		return null;
	}
	
	public void setValueAt(Object value,int rowIndex,int colIndex) {
		Command c=commands.get(rowIndex);
		
		boolean changed=false;
		switch(colIndex) {
			case 0:
				c.setHold(((Boolean)value).booleanValue());
				changed=true;
				break;
			case 1:
				c.setKeep(((Boolean)value).booleanValue());
				changed=true;
				break;
			case 3:
				c.setDone(((Boolean)value).booleanValue());
				changed=true;
				break;
		}
		
		if(changed) {
			try {
				gallery.updateCommand(c);
			} catch (GalleryException e) { e.printStackTrace(); }
		}
	}
	
	public boolean isCellEditable(int rowIndex,int colIndex) {
		if(colIndex==0 || colIndex==1 || colIndex==3) return true;
		
		if(colIndex==6) {
			Command c=getCommandAt(rowIndex);
			return c.isEditable();
		}
		
		return false;
	}
	public Command getCommandAt(int index) {
		return commands.get(index);
	}
	
	/**
	 * Method commandEventOccurred
	 *
	 * @param    ce                  a  CommandEvent
	 *
	 */
	public void commandEventOccurred(CommandEvent ce) {
		switch(ce.getType()) {
			case CommandEvent.ADDED:
				commands.add(ce.getCommand());
				fireTableRowsInserted(commands.size(),commands.size());
				return;
			case CommandEvent.UPDATED:
				for(int i=0;i<commands.size();i++) {
					Command c=commands.get(i);
					if(c.getId().equals(ce.getId())) {
						commands.set(i,ce.getCommand());
						fireTableRowsUpdated(i,i);
						return;
					}
				}
				return;
			case CommandEvent.DELETED:
				for(int i=0;i<commands.size();i++) {
					Command c=commands.get(i);
					if(c.getId().equals(ce.getId())) {
						commands.remove(i);
						fireTableRowsDeleted(i,i+1);
						return;
					}
				}
				return;
		}
		return;
	}
	
	
}

