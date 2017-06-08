/**
 * 
 */
package com.basiscomponents.db;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A java.util.ArrayList providing the same method names as the BBjVector class.
 * The class doesn't add any additional features to the list itself. 
 * <br><br>
 * This class was originally created to replace the BBjVector usage in the basiscomponents. 
 * 
 * @see <a href="http://documentation.basis.com/BASISHelp/WebHelp/gridctrl/bbjvector_bbj.htm">BBjVector documentation</a>
 */
@SuppressWarnings("rawtypes")
public class BBArrayList extends ArrayList {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public BBArrayList(Collection c) {
		super.addAll(c);
	}

	/**
	 * Calls the {@link java.util.ArrayList#add(Object) ArrayList.add(Object)} method.
	 * 
	 * @param element The element to be appended to this list
	 * @return true (as specified by {@link java.util.Collection#add(Object) Collection.add(E)})
	 */
	@SuppressWarnings("unchecked")
	public boolean addItem(Object element) {
		return super.add(element);
	}

	/**
	 * Calls the {@link java.util.ArrayList#get(int) ArrayList.get(int)} method.
	 * 
	 * @param index The index of the element to return.
	 * @return the element at the specified position in this list.
	 */
	public Object getItem(int index) {
		return super.get(index);
	}

	/**
	 * Calls the {@link java.util.ArrayList#add(int, Object) ArrayList.add(int, Object)} method.
	 * 
	 * @param index The index at which the specified element is to be inserted.
	 * @param element The object to be inserted.
	 */
	@SuppressWarnings("unchecked")
	public void insertItem(int index, Object element) {
		super.add(index, element);
	}

	/**
	 * Calls the {@link java.util.ArrayList#remove(int) ArrayList.remove(int)} method.
	 * 
	 * @param index The index of the element to be removed.
	 * @return element The element that was removed from the list.
	 */
	public Object removeItem(int index) {
		return super.remove(index);
	}

	/**
	 * Calls the {@link java.util.ArrayList#set(int, Object) ArrayList.set(int, Object)} method.
	 * 
	 * @param index The index of the element to replace.
	 * @param element The element to be stored at the specified position
	 * 
	 * @return the element previously at the specified position.
	 */
	@SuppressWarnings("unchecked")
	public Object setItem(int index, Object element) {
		return super.set(index, element);
	}

}
