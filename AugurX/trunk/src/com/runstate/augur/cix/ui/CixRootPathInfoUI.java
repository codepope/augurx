/**
 * CixRootPathInfoUI.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.augur.cix.ui;

import com.runstate.augur.cix.commands.CixJoinCommand;
import com.runstate.augur.cix.pathinfo.CixRootPathInfo;
import com.runstate.augur.controller.Controller;
import com.runstate.augur.controller.Door;
import com.runstate.augur.gallery.GalleryException;
import com.runstate.augur.ui.pathinfo.PathInfoUI;
import com.runstate.util.swing.TableSorter;
import java.awt.BorderLayout;
import java.awt.FontMetrics;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class CixRootPathInfoUI extends PathInfoUI implements TableModel {
	JTable roottable;
	Vector<TableModelListener> listeners;
//	ArrayList<String> sections;
//	ArrayList<ConfEntry> confentries;
	TableSorter tablesorter;
	CixRootPathInfo crpi=null;
	
	public CixRootPathInfoUI() {
		super();
	}
	
	public void createUI() {
		listeners=new Vector<TableModelListener>();
//		sections=new ArrayList<String>();
//		confentries=new ArrayList<ConfEntry>();
		tablesorter=new TableSorter(this);
		roottable=new JTable(tablesorter);
		roottable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setHeaderSize(roottable,0);
		setHeaderSize(roottable,1);
		setHeaderSize(roottable,2);
		setHeaderSizeByChars(roottable,3,16);
	//	setTableColWidth(roottable,3,160);
		setTableColWidth(roottable,4,320);
		setTableColWidth(roottable,5,320);
		
		
		tablesorter.setTableHeader(roottable.getTableHeader());
		add(BorderLayout.CENTER,new JScrollPane(roottable));
	}
	
	private void setTableColWidth(JTable jt,int index,int width) {
		TableColumn tc=jt.getColumnModel().getColumn(index);
		tc.setPreferredWidth(width);
	}
	
	/**Sets the header and column size as per the Header text
	 */
	public void setHeaderSize(JTable jt,int pColumn){
		//Get the column name of the given column.
		String value =  jt.getColumnName(pColumn);
		//Calculate the width required for the column.
		FontMetrics metrics = getFontMetrics(getFont());
		int width = metrics.stringWidth(value+"--") +
			(2*jt.getColumnModel().getColumnMargin());
		//Set the width.
		setTableColWidth(jt,pColumn, width);
		jt.getColumnModel().getColumn(pColumn).setResizable(false);
	}
	
	public void setHeaderSizeByChars(JTable jt,int pColumn,int chars){
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<chars;i++) sb.append("a");
		
		FontMetrics metrics = getFontMetrics(getFont());
		int width = metrics.stringWidth(sb.toString()) +
			(2*jt.getColumnModel().getColumnMargin());
		setTableColWidth(jt,pColumn, width);
	}
	
	public void updatedPathInfo() {
		if(getPathInfo()==null) return;
		
		CixRootPathInfo crpi=(CixRootPathInfo)getPathInfo();
		
//		parseRootPathInfo(crpi);
		
		fireTableChanged();
	}
	
	
//	private void parseRootPathInfo(CixRootPathInfo crpi) {
//		sections.clear();
//		confentries.clear();
//
//		if(crpi.getConflist()==null)
//		{
//			return;
//		}
//
//		String[] lines=crpi.getConflist().split("\n");
//		Pattern confp=Pattern.compile("([oc]) ([^ ]+)[ ]*([^$]+)");
//		Pattern subp=Pattern.compile("Conference: ([^$]+)");
//		StringBuffer confnote=new StringBuffer();
//		int sectionid=-1;
//		boolean sublist=true;
//		HashSet<String> subs=new HashSet<String>();
//
//		for(String line:lines) {
//			if(sublist) {
//				if(line.equals(" #####AUGURBREAK#####")) {
//					sublist=false;
//				}
//				else {
//					Matcher m=subp.matcher(line);
//					if(m.matches()) {
//						subs.add(m.group(1));
//					}
//				}
//
//			}
//			else {
//				Matcher m=confp.matcher(line);
//				if(m.matches()) {
//					String oc=m.group(1);
//					String name=m.group(2);
//					String desc="";
//
//					if(m.groupCount()==3) desc=m.group(3);
//					boolean exists=Augur.getBundleManager().getBundleExists("/cix/"+name);
//					boolean joined=subs.contains(name);
//
//					ConfEntry ce=new ConfEntry(oc,sectionid,name,desc,exists,joined);
//
//					confentries.add(ce);
//				} else if(!line.startsWith("--")) {
//					if(!line.equals("-")) {
//						if(line.startsWith("-")) {
//							confnote.append(line);
//							confnote.append("\n");
//						}
//						else {
//							if(!line.equals("") && !line.equals("(o=open, c=closed)")) {
//								sections.add(line);
//								sectionid++;
//							}
//						}
//					}
//
//
//				}
//			}
//		}
//	}
//
	
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
		if(getPathInfo()==null) return 0;
		return ((CixRootPathInfo)getPathInfo()).getConfentries().size();
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
		return 6;
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
		switch(columnIndex) {
			case 0: return "Open";
			case 1: return "Local";
			case 2: return "Joined";
			case 3: return "Name";
			case 4: return "Description";
			case 5: return "Section";
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
		switch(columnIndex) {
			case 0: return Boolean.class;
			case 1: return Boolean.class;
			case 2: return Boolean.class;
			case 3: return String.class;
			case 4: return String.class;
			case 5: return String.class;
		}
		
		return String.class;
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
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 2)
                {
                    // Joined Column
                    return true;
                }
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
	public Object getValueAt(int rowIndex, int columnIndex) {
		CixRootPathInfo.ConfEntry ce=((CixRootPathInfo)getPathInfo()).getConfentries().get(rowIndex);
		switch(columnIndex) {
			case 0:
				return ce.isOpen()?Boolean.TRUE:Boolean.FALSE;
			case 1:
				return ce.existslocally?Boolean.TRUE:Boolean.FALSE;
			case 2:
				return ce.joined?Boolean.TRUE:Boolean.FALSE;
			case 3:
				return ce.name;
			case 4:
				return ce.description;
			case 5:
				return ((CixRootPathInfo)getPathInfo()).getSections().get(ce.id);
		}
		
		return null;
	}
	
	/**
	 * Sets the value in the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code> to <code>aValue</code>.
	 *
	 * @param	aValue		 the new value
	 * @param	rowIndex	 the row whose value is to be changed
	 * @param	columnIndex 	 the column whose value is to be changed
	 * @see #getValueAt
	 * @see #isCellEditable
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (columnIndex==2)
                {
                    //Joined column
                    if ((Boolean)aValue.equals(true)) //join Conf
                    {
                        int res=JOptionPane.showConfirmDialog(this,"Join " + getValueAt(rowIndex, 3) +"?","Augur",JOptionPane.YES_NO_OPTION);
                        if(res==JOptionPane.YES_OPTION)
                        {
                            try
                            {
                                Door door=Controller.getController().getGallery().getDoorForPath("/cix");
                                Controller.getController().getGallery().addCommand(new CixJoinCommand(door.getDoorid(),"/cix/"+(String)getValueAt(rowIndex, 3)));
                            }
                            catch (GalleryException e) {}
                        }
                    }
                    else  //resign conf
                    {}
                }
	}
	
	/**
	 * Adds a listener to the list that is notified each time a change
	 * to the data model occurs.
	 *
	 * @param	l		the TableModelListener
	 */
	
	
	
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}
	
	/**
	 * Removes a listener from the list that is notified each time a
	 * change to the data model occurs.
	 *
	 * @param	l		the TableModelListener
	 */
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}
	
	private void fireTableChanged() {
		TableModelEvent tme=new TableModelEvent(this);
		
		for(TableModelListener tml:listeners) {
			tml.tableChanged(tme);
		}
	}
	

}

