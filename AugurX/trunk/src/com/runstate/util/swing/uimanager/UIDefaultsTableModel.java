///**
// * UIManagerTableModel.java
// *
// * @author Created by Omnicore CodeGuide
// */
//
//package com.runstate.util.swing.uimanager;
//
//import java.awt.Font;
//import java.util.Comparator;
//import java.util.Enumeration;
//import java.util.Vector;
//import javax.swing.UIDefaults;
//import javax.swing.UIManager;
//import javax.swing.table.DefaultTableModel;
//
///**
// * <p>Title: UIDefaults</p>
// * <p>Description: Sample Program demonstrating UI Default functionality</p>
// * <p>Copyright: Copyright (c) 2003 Daniel Horn</p>
// * <p>Company: </p>
// * @author Daniel P. Horn
// * @version 1.0
// */
//
//public class UIDefaultsTableModel
//	extends DefaultTableModel
//	implements Comparator
//{
//
//	public UIDefaultsTableModel()
//	{
//		addColumn("Key");
//		addColumn("Value");
//		addColumn("Value Type");
//		addColumn("More Info");
//
//		GetCurrentUIDefaults();
//	}
//
//	void GetCurrentUIDefaults()
//	{
//		Vector<Vector> vData = new Vector<Vector>();
//
//		UIDefaults uiDefaults = UIManager.getDefaults();
//		Enumeration<Object> enumkeys = uiDefaults.keys();
//		while (enumkeys.hasMoreElements())
//		{
//			Object key = enumkeys.nextElement();
//			Object value = uiDefaults.get(key);
//
//			// Display more useful information for common value types.
//			Object moreInfo = null;
//			try
//			{
//				if (value instanceof String)
//				{
//					moreInfo = value;
//				}
//				else if (value instanceof Integer)
//				{
//					Integer integer = (Integer) value;
//					moreInfo = integer;
//				}
//				else if (value instanceof Boolean)
//				{
//					Boolean bool = (Boolean) value;
//					moreInfo = bool;
//				}
//				else if (value instanceof java.awt.Color)
//				{
//					java.awt.Color color = ( (java.awt.Color) value);
//					moreInfo = "[r=" + color.getRed() + ", g=" + color.getGreen() +
//						", b=" + color.getBlue() + "]";
//				}
//				else if (value instanceof java.awt.Dimension)
//				{
//					java.awt.Dimension dim = ( (java.awt.Dimension) value);
//					moreInfo = "[width=" + dim.width + ", height=" + dim.height +
//						"]";
//				}
//				else if (value instanceof Font)
//				{
//					Font font = (Font) value;
//					moreInfo = font.getFontName();
//				}
//				/**
//				 @todo Add means of displaying image for following;
//				 and similarly, for Color and Font.
//				 */
//				else if (value instanceof javax.swing.Icon)
//				{
//					moreInfo = "Icon";
//				}
//				else if (value instanceof javax.swing.plaf.UIResource)
//				{
//					moreInfo = "UIResource";
//				}
//				else if (value instanceof java.lang.reflect.Method)
//				{
//					moreInfo = ( (java.lang.reflect.Method) value).getName();
//				}
//				else if (value instanceof Class)
//				{
//					// Get short form of class name (ie, name of class without package).
//					String strMoreInfo = ( (Class) value).getName();
//					int index = strMoreInfo.lastIndexOf(".");
//					moreInfo = strMoreInfo.substring(index + 1);
//				}
//				else if (null != value)
//				{
////					System.out.println("[" + key + "][" + value + "]:[" + value.getClass() + "]");
//				}
//				else
//				{
////					System.out.println("[" + key + "][ (null value) ]");
//				}
//			}
//			catch (Exception exc)
//			{
//				System.out.println(exc.toString());
//			}
//
//			Vector<Object> vItem = new Vector<Object>();
//			vItem.add((Object) key);
//			vItem.add((Object) value);
//			if (null != value)
//			{
//				vItem.add(value.getClass());
//			}
//			else
//			{
//				vItem.add("(null)");
//			}
//			vItem.add((Object) moreInfo);
//
//			vData.add(vItem);
//		}
//
//		dataVector = vData;
//		fireTableDataChanged();
//	}
//
//	private int col = -1;
//	private boolean bAscending = true;
//	void sort(int col)
//	{
//		if (this.col == col)
//		{
//			bAscending = !bAscending;
//		}
//		else
//		{
//			bAscending = true;
//		}
//		this.col = col;
//		java.util.Collections.sort(dataVector, this);
//	}
//
//	public int compare(Object o1, Object o2)
//	{
//		Vector v1 = (Vector) o1;
//		Vector v2 = (Vector) o2;
//
//		String str1 = "";
//		String str2 = "";
//
//		try
//		{
//			str1 = v1.get(col).toString();
//		}
//		catch (Exception exc)
//		{
//			// null value is possible, so we need to test for this condition or catch exception.
//		}
//
//		try
//		{
//			str2 = v2.get(col).toString();
//		}
//		catch (Exception exc)
//		{
//		}
//
//		return (bAscending ? 1 : -1) * str1.compareTo(str2);
//	}
//
//	public boolean isCellEditable(int row, int column)
//	{
//		return false;
//	}
//
//}
