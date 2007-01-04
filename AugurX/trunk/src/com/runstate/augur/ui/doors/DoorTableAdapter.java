/**
 * DoorTableAdapter.java
 *
 * @author Created by Omnicore CodeGuide
 */


package com.runstate.augur.ui.doors;

import com.runstate.augur.controller.*;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

public class DoorTableAdapter extends AbstractTableModel implements DoorListener
{
	public DoorTableAdapter()
	{
		Door[] doors=getDoors();
		for(int i=0;i<doors.length;i++)
		{
			doors[i].addDoorListener(this);
		}
	}
	
	public Door[] getDoors()
	{
		return Controller.getGallery().getDoors();
	}
		
	public void doorEventOccurred(DoorEvent de)
	{
		Door nd=de.getDoor();
		Door[] doors=getDoors();
		
		for(int i=0;i<doors.length;i++)
		{
			if(doors[i]==nd)
			{
				// Got it
				//fireTableCellUpdated(i,1);
				fireTableRowsUpdated(i,i);
			}
		}
	}
	
	
	
	/**
	 * Returns the number of rows in the model. A
	 * <code>JTable</code> uses this method to determine how many rows it
	 * should display.  This method should be quick, as it
	 * is called frequently during rendering.
	 *
	 * @return the number of rows in the model
	 * @see #getColumnCount
	 */
	public int getRowCount()
	{
		return getDoors().length;
	}
	
	/**
	 * Returns the number of columns in the model. A
	 * <code>JTable</code> uses this method to determine how many columns it
	 * should create and display by default.
	 *
	 * @return the number of columns in the model
	 * @see #getRowCount
	 */
	public int getColumnCount()
	{
		return 5;
	}
	
	/**
	 * Returns the name of the column at <code>columnIndex</code>.  This is used
	 * to initialize the table's column header name.  Note: this name does
	 * not need to be unique; two columns in a table can have the same name.
	 *
	 * @param	columnIndex	the index of the column
	 * @return  the name of the column
	 */
	public String getColumnName(int columnIndex)
	{
		switch(columnIndex)
		{
			case 0: return "Door";
			case 1: return "Name";
			case 2: return "Last message";
			case 3: return "Connected";
			case 4: return "Busy";
		}
		
		
		return null;
	}
	
	/**
	 * Returns the most specific superclass for all the cell values
	 * in the column.  This is used by the <code>JTable</code> to set up a
	 * default renderer and editor for the column.
	 *
	 * @param columnIndex  the index of the column
	 * @return the common ancestor class of the object values in the model.
	 */
	public Class getColumnClass(int columnIndex)
	{
		switch(columnIndex)
		{
			case 0: return ImageIcon.class;
			case 1: return String.class;
			case 2: return String.class;
			case 3: return Boolean.class;
			case 4: return Boolean.class;
		}
		
		return null;
	}
	
	/**
	 * Returns true if the cell at <code>rowIndex</code> and
	 * <code>columnIndex</code>
	 * is editable.  Otherwise, <code>setValueAt</code> on the cell will not
	 * change the value of that cell.
	 *
	 * @param	rowIndex	the row whose value to be queried
	 * @param	columnIndex	the column whose value to be queried
	 * @return	true if the cell is editable
	 * @see #setValueAt
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		// TODO
		return false;
	}
	
	/**
	 * Returns the value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 *
	 * @param	rowIndex	the row whose value is to be queried
	 * @param	columnIndex 	the column whose value is to be queried
	 * @return	the value Object at the specified cell
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Door[] doors=getDoors();
		
		Door d=doors[rowIndex];
		
		switch(columnIndex)
		{
			case 0:
				return d.getDoorIcon();
			case 1:
				return d.getDoorname();
			case 2:
				return d.getLastLog();
			case 3:
				return new Boolean(d.isConnected());
			case 4:
				return new Boolean(d.isBusy());
		}
		
		return null;
	}
	
}

