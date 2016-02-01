/**
 * 
 */
package com.basiscomponents.db;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("rawtypes")
public class BBArrayList extends ArrayList {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public BBArrayList(Collection c) {
		super.addAll(c);
	}

	@SuppressWarnings("unchecked")
	public boolean addItem(Object element) {
		return super.add(element);
	}

	public Object getItem(int index) {
		return super.get(index);
	}

	@SuppressWarnings("unchecked")
	public void insertItem(int index, Object element) {
		super.add(index, element);
	}

	public Object removeItem(int index) {
		return super.remove(index);
	}

	@SuppressWarnings("unchecked")
	public Object setItem(int index, Object element) {
		return super.set(index, element);
	}

}
