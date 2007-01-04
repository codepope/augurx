/**
 * CommandListModel.java
 *
 * @author Created by Omnicore CodeGuide
 */


package com.runstate.augur.gallery.models;

import com.runstate.augur.AugurX;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.gallery.GalleryException;
import com.runstate.augur.gallery.commands.Command;
import com.runstate.augur.gallery.events.CommandEvent;
import com.runstate.augur.gallery.listeners.CommandEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;


public class CommandTableModel extends AbstractTableModel implements CommandEventListener {
	
	ArrayList<String> commandids=new ArrayList<String>();
	SimpleDateFormat sdf;
	boolean pendingOnly=true;
	Long doorfilter=null;
	
	public CommandTableModel() {
		sdf=Controller.getController().getDateFormat();
		
		try {
			commandids=Controller.getGallery().getCommandIds(doorfilter);
			Controller.getGallery().addCommandEventListener(this);
		} catch (GalleryException e) { e.printStackTrace(); }
	}
	
	/**
	 * Sets PendingOnly
	 *
	 * @param    PendingOnly         a  boolean
	 */
	public void setPendingOnly(boolean pendingOnly)
	{
		if(this.pendingOnly==pendingOnly) return;
		
		this.pendingOnly = pendingOnly;
		// Reload commands
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
		Controller.getGallery().removeCommandEventListener(this);
	}
	
	public int getRowCount() {
		return commandids.size();
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
		Command c=Controller.getGallery().getCommand(commandids.get(rowIndex));
		
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
		Command c=Controller.getGallery().getCommand(commandids.get(rowIndex));
		
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
				Controller.getGallery().updateCommand(c);
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
		Command c=Controller.getGallery().getCommand(commandids.get(index));
		return c;
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
				commandids.add(ce.getId());
				fireTableRowsInserted(commandids.size(),commandids.size());
				return;
			case CommandEvent.UPDATED:
				for(int i=0;i<commandids.size();i++) {
					String c=commandids.get(i);
					if(c.equals(ce.getId())) {
						fireTableRowsUpdated(i,i);
						return;
					}
				}
				return;
			case CommandEvent.DELETED:
				for(int i=0;i<commandids.size();i++) {
					String c=commandids.get(i);
					if(c.equals(ce.getId())) {
						commandids.remove(i);
						fireTableRowsDeleted(i,i+1);
						return;
					}
				}
				return;
		}
		return;
	}
	
	
}

