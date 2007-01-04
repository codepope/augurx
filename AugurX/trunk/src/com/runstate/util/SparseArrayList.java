/**
 * SparseArrayList.java
 *
 * @author Created by Omnicore CodeGuide
 */

package com.runstate.util;

public class SparseArrayList extends java.util.AbstractList {
	
	private static final int PAGE_SIZE = 1024; // page size
	private Object pages[][] = new Object[0][]; // pages
	
	// constructor
	public SparseArrayList() {}
	
	// return total size of allocated pages
	public int size() {
		return pages.length * PAGE_SIZE;
	}
	
	// set an array slot to a given value
	public Object set(int index, Object val) {
		if (index < 0)
			throw new IllegalArgumentException();
		int p = index / PAGE_SIZE;
		if (p >= pages.length) { // expand pages list
			Object newpages[][] = new Object[p + 1][];
			System.arraycopy(pages, 0, newpages, 0,
							 pages.length);
			pages = newpages;
		}
		if (pages[p] == null) // allocate new page
			pages[p] = new Object[PAGE_SIZE];
		Object old = pages[p][index % PAGE_SIZE];
		pages[p][index % PAGE_SIZE] = val;
		return old;
	}
	
	// get the value of a given array slot, null if none
	public Object get(int index) {
		if (index < 0)
			throw new IllegalArgumentException();
		int p = index / PAGE_SIZE;
		if (p >= pages.length || pages[p] == null)
			return null;
		return pages[p][index % PAGE_SIZE];
	}
}
