/**
 * TwixConfPathInfoUI.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.twix.ui;

import com.runstate.augur.*;
import com.runstate.augur.twix.pathinfo.*;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.ui.pathinfo.*;
import com.runstate.augur.ui.textpanes.*;
import com.runstate.util.swing.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class TwixConfPathInfoUI extends PathInfoUI {
	JTable topictable;
	TableSorter topictablesorter;
	TopicTableModel topictablemodel;
	JTable parttable;
	TableSorter partlisttablesorter;
	PartTableModel partTableModel;
	
	JSplitPane texttopicsplit;
	
	HTMLPane misctext;
	
	public TwixConfPathInfoUI() {
		super();
	}
	
	public void createUI() {
		topictablemodel = new TopicTableModel();
		topictablesorter = new TableSorter(topictablemodel);
		topictable = new JTable(topictablesorter);
		topictablesorter.setTableHeader(topictable.getTableHeader());
		topictable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setTableColWidth(topictable, 0, 64);
		setTableColWidth(topictable, 1, 160);
		setTableColWidth(topictable, 2, 320);
		
		partTableModel = new PartTableModel();
		partlisttablesorter = new TableSorter(partTableModel);
		parttable = new JTable(partlisttablesorter);
		partlisttablesorter.setTableHeader(parttable.getTableHeader());
		setTableColWidth(parttable, 0, 64);
		setTableColWidth(parttable, 1, 160);
		
		JPanel partlistpanel=new JPanel(new BorderLayout());
		partlistpanel.add(BorderLayout.NORTH, new JLabel("Participants"));
		partlistpanel.add(BorderLayout.CENTER, new JScrollPane(parttable));
		
		misctext = new HTMLPane();
		texttopicsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(misctext), new JScrollPane(topictable));
		texttopicsplit.setResizeWeight(0.75D);
		
		JSplitPane mainsp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, texttopicsplit, partlistpanel);
		mainsp.setResizeWeight(0.75D);
		
		add(BorderLayout.CENTER, mainsp);
	}
	
	private void setTableColWidth(JTable jt, int index, int width) {
		TableColumn tc=jt.getColumnModel().getColumn(index);
		tc.setPreferredWidth(width);
	}
	
	public void updatedPathInfo() {
//		parseText();
		misctext.setHTML(Controller.getController().wrapWithStyle("", ((TwixConfPathInfo)getPathInfo()).getConfnote()));
		misctext.setCaretPosition(0);
		partTableModel.updated();
		topictablemodel.updated();
	}
	
	
	class PartTableModel implements TableModel {
		
		public int getRowCount() {
			if (getPathInfo() == null) return 0;
			if (((TwixConfPathInfo)getPathInfo()).getParticipants() == null) return 0;
			return ((TwixConfPathInfo)getPathInfo()).getParticipants().size();
		}
		
		public int getColumnCount() {
			return 2;
		}
		/**
		 * Returns the value at the specified index.
		 * @param index the requested index
		 * @return the value at <code>index</code>
		 */
		public Object getValueAt(int rowIndex, int colIndex) {
			TwixConfPathInfo.Part p=((TwixConfPathInfo)getPathInfo()).getParticipants().get(rowIndex);
			switch (colIndex) {
				case 0: return p.mod;
				case 1: return p.name;
			}
			
			return null;
		}
		
		public Class getColumnClass(int colIndex) {
			switch (colIndex) {
				case 0: return Boolean.class;
				case 1: return String.class;
			}
			
			return null;
		}
		
		public String getColumnName(int colIndex) {
			switch (colIndex) {
				case 0: return "Mod";
				case 1: return "Name";
			}
			
			return null;
		}
		
		Vector<TableModelListener> listeners=new Vector<TableModelListener>();
		
		public void updated() {
			fireTableChanged();
		}
		public void addTableModelListener(TableModelListener l) {
			listeners.add(l);
		}
		
		public void removeTableModelListener(TableModelListener l) {
			listeners.remove(l);
		}
		
		private void fireTableChanged() {
			TableModelEvent tme=new TableModelEvent(this);
			
			for (TableModelListener tml:listeners) {
				tml.tableChanged(tme);
			}
		}
		
		/**
		 * Returns true if the cell at <code>rowIndex</code> and
		 * <code>columnIndex</code>
		 * is editable.  Otherwise, <code>setValueAt</code> on the cell will not
		 * change the value of that cell.
		 * @param	rowIndex	the row whose value to be queried
		 * @param	columnIndex	the column whose value to be queried
		 * @return	true if the cell is editable
		 * @see #setValueAt
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			// TODO
			return false;
		}
		
		/**
		 * Sets the value in the cell at <code>columnIndex</code> and
		 * <code>rowIndex</code> to <code>aValue</code>.
		 * @param	aValue		 the new value
		 * @param	rowIndex	 the row whose value is to be changed
		 * @param	columnIndex 	 the column whose value is to be changed
		 * @see #getValueAt
		 * @see #isCellEditable
		 */
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// TODO
		}
		
		
		
		public PartTableModel() {
		}
	}
	
	class TopicTableModel implements TableModel {
		/**
		 * Returns the number of rows in the model. A
		 * <code>JTable</code> uses this method to determine how many rows it
		 * should display.  This method should be quick, as it
		 * is called frequently during rendering.
		 *
		 * @return the number of rows in the model
		 * @see #getColumnCount
		 */
		public int getRowCount() {
			if (getPathInfo() == null) return 0;
			if (((TwixConfPathInfo)getPathInfo()).getTopics() == null) return 0;
			return ((TwixConfPathInfo)getPathInfo()).getTopics().size();
		}
		
		/**
		 * Returns the number of columns in the model. A
		 * <code>JTable</code> uses this method to determine how many columns it
		 * should create and display by default.
		 *
		 * @return the number of columns in the model
		 * @see #getRowCount
		 */
		public int getColumnCount() {
			return 3;
		}
		
		/**
		 * Returns the name of the column at <code>columnIndex</code>.  This is used
		 * to initialize the table's column header name.  Note: this name does
		 * not need to be unique; two columns in a table can have the same name.
		 *
		 * @param	columnIndex	the index of the column
		 * @return  the name of the column
		 */
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
				case 0: return "Local";
				case 1: return "Name";
				case 2: return "Description";
			}
			
			return "?!";
		}
		
		/**
		 * Returns the most specific superclass for all the cell values
		 * in the column.  This is used by the <code>JTable</code> to set up a
		 * default renderer and editor for the column.
		 *
		 * @param columnIndex  the index of the column
		 * @return the common ancestor class of the object values in the model.
		 */
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
				case 0: return Boolean.class;
				case 1: return String.class;
				case 2: return String.class;
			}
			
			return String.class;
		}
		
		/**
		 * Returns the value for the cell at <code>columnIndex</code> and
		 * <code>rowIndex</code>.
		 *
		 * @param	rowIndex	the row whose value is to be queried
		 * @param	columnIndex 	the column whose value is to be queried
		 * @return	the value Object at the specified cell
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			TwixConfPathInfo.Topic te=((TwixConfPathInfo)getPathInfo()).getTopics().get(rowIndex);
			switch (columnIndex) {
				case 0:
					return te.indb ?Boolean.TRUE: Boolean.FALSE;
				case 1:
					return te.name;
				case 2:
					return te.desc;
			}
			
			return null;
		}
		
		public TopicTableModel() {
		}
		
		Vector<TableModelListener> listeners=new Vector<TableModelListener>();
		
		public void updated() {
			fireTableChanged();
		}
		
		public void addTableModelListener(TableModelListener l) {
			listeners.add(l);
		}
		
		public void removeTableModelListener(TableModelListener l) {
			listeners.remove(l);
		}
		
		private void fireTableChanged() {
			TableModelEvent tme=new TableModelEvent(this);
			
			for (TableModelListener tml:listeners) {
				tml.tableChanged(tme);
			}
		}
		
		/**
		 * Returns true if the cell at <code>rowIndex</code> and
		 * <code>columnIndex</code>
		 * is editable.  Otherwise, <code>setValueAt</code> on the cell will not
		 * change the value of that cell.
		 * @param	rowIndex	the row whose value to be queried
		 * @param	columnIndex	the column whose value to be queried
		 * @return	true if the cell is editable
		 * @see #setValueAt
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			// TODO
			return false;
		}
		
		/**
		 * Sets the value in the cell at <code>columnIndex</code> and
		 * <code>rowIndex</code> to <code>aValue</code>.
		 * @param	aValue		 the new value
		 * @param	rowIndex	 the row whose value is to be changed
		 * @param	columnIndex 	 the column whose value is to be changed
		 * @see #getValueAt
		 * @see #isCellEditable
		 */
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// TODO
		}
		
		
		
	}
	
}

