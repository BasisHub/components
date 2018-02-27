package com.basiscomponents.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.naming.OperationNotSupportedException;

/**
 * Iterator for {@link DataRow}s inside a ResultSet
 */
public class ResultSetIterator implements Iterator<DataRow> {

	private List<DataRow> dataRows = new ArrayList<>();
	private int current;

	ResultSetIterator(List<DataRow> dataRows) {
		this.dataRows = dataRows;
		this.current = 0;
	}

	@Override
	public boolean hasNext() {
		return current < dataRows.size();
	}

	@Override
	public DataRow next() {
		if (!hasNext())
			throw new NoSuchElementException();
		return dataRows.get(current++);
	}


	// FIXME
	@Override
	public void remove() {
		// Choose exception or implementation:
		try {
			throw new OperationNotSupportedException();
		} catch (OperationNotSupportedException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		// or
		// // if (! hasNext()) throw new NoSuchElementException();
		// // if (currrent + 1 < myArray.end) {
		// // System.arraycopy(myArray.arr, current+1, myArray.arr, current,
		// myArray.end - current-1);
		// // }
		// // myArray.end--;
	}
}