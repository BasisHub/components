package com.basiscomponents.bc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public class BCBinder {

	private ArrayList<IBCBound> boundComponents = new ArrayList<>();
	private BusinessComponent bc;

	private ResultSet rs;
	private DataRow attributes_rec;
	private ArrayList<String> selection = new ArrayList<>();
	
	private int DebugLevel = 0;

	// direction constants

	/**
	 * Direction directive to move selection to the first record
	 */
	public static final int SEL_FIRST = 501;

	/**
	 * Direction directive to move selection to the previous record
	 */
	public static final int SEL_PREVIOUS = 502;

	/**
	 * Direction directive to move selection to the next record
	 */
	public static final int SEL_NEXT = 503;

	/**
	 * Direction directive to move selection to the last record
	 */
	public static final int SEL_LAST = 504;

	/**
	 * Direction directive to deselect all
	 */
	public static final int SEL_DESELECT = 505;

	// signal constants

	/**
	 * Signal directive that data should be saved
	 */
	public static final int SIGNAL_SAVE = 901;

	/**
	 * Signal directive that a new record should be added
	 */
	public static final int SIGNAL_NEW = 902;

	/**
	 * Signal directive that delete should occur (on current selection)
	 */
	public static final int SIGNAL_DELETE = 903;

	/**
	 * Signal directive that components should render themselves blank
	 */
	public static final int SIGNAL_BLANK = 904;

	/**
	 * Signal directive that there is dirty (unsaved) data
	 */
	public static final int SIGNAL_DIRTY = 905;
	
	/**
	 * Signal directive that an error has occured in the BC
	 */
	public static final int SIGNAL_ERROR = 906;
	

	
	/**
	 * Signal directive that the program wants to terminate
	 */
	public static final int SIGNAL_INITIATE_TERMINATE = 907;
	
	/**
	 * Signal directive that the program wants to terminate
	 */
	public static final int SIGNAL_TERMINATE = 908;
	
	/**
	 * Signal directive that the program WILL terminate
	 */
	public static final int SIGNAL_DESTROY = 909;

	@SuppressWarnings("unused")
	private BCBinder() {
	}

	/**
	 * Sets the working business component and its attributes record
	 *
	 * @param bc Business Component
	 */
	public BCBinder(BusinessComponent bc) {
		this.bc = bc;
		this.attributes_rec = bc.getAttributesRecord();
	}

	/**
	 * 
	 * @return the debug level which is currently set
	 */
	public int getDebugLevel() {
		return DebugLevel;
	}

	/**
	 * 
	 * set the debug level of operations done by the BCBinder
	 * @param debugLevel: 0=no debug, 1=moderate debug 
	 * 
	 */
	public void setDebugLevel(int debugLevel) {
		if (debugLevel>=0 && debugLevel<2)
		DebugLevel = debugLevel;
	}

	
	/**
	 *
	 * @return bound BC
	 */
	public BusinessComponent getBC() {
		return this.bc;
	}

	/**
	 *
	 * @return working result set
	 */
	public ResultSet getRS() {
		return this.rs;
	}

	/**
	 * Sets the working result set and invokes the onSetData() of all bound
	 * components
	 *
	 * @param rs result set
	 */
	private void setRS(ResultSet rs) {
		this.rs = rs;
		this.onSetData();
	}

	/**
	 *
	 * @return DataRow containing all attributes
	 */
	public DataRow getAttributesRecord() {
		return attributes_rec;
	}

	/**
	 *
	 * @param attributes_rec DataRow containing all attributes to be set
	 */
	private void setAttributesRecord(DataRow attributes_rec) {
		this.attributes_rec = attributes_rec;
	}

	/**
	 * Registers a component with this binder and sets the component's binder to
	 * this one
	 *
	 * @param component
	 */
	public void register(IBCBound component) {
		boundComponents.add(component);
		component.setBinder(this);
	}

	/**
	 * Performs the retrieve of the bound BC, gets the corresponding attributes record that describes what's in the result set,
	 * creates indexes in the result set, checks keys in current selection list are in the result set,
	 * and invokes the onSetData() of all bound components so they can react.
	 *
	 * @throws Exception
	 */
	public void retrieve() throws Exception {
		this.rs = this.bc.retrieve();
		this.attributes_rec = this.bc.getAttributesRecord();
		this.rs.createIndex();
		updateSelection();
		onSetData();
	}

	/**
	 * Checks if the items in the selection are still present in the result set and
	 * removes them from the selection if not.
	 */
	protected void updateSelection() {
		ResultSet rs = getRS();
		ArrayList<String> selection = (ArrayList<String>) getSelection();
		ArrayList<String> selectionNew = (ArrayList<String>) selection.clone();
		for (String rowkey : selection) {
			try {
				// selected row no longer in result set, so remove it
				DataRow row = rs.get(rowkey);
				if (row == null || row.isEmpty()) {
					selectionNew.remove(rowkey);
				}
			} catch (Exception e) {
				selectionNew.remove(rowkey);
			}
		}
		setSelection(selectionNew);
	}

	/**
	 * Invokes the onSetData() of all bound components
	 */
	public void onSetData() {
		Iterator<IBCBound> it = boundComponents.iterator();
		while (it.hasNext()) {
			IBCBound o = it.next();
			o.onSetData();
		}
	}

	/**
	 * Invokes the onSetSelection() of all bound components
	 */
	public void onSetSelection() {
		Iterator<IBCBound> it = boundComponents.iterator();
		while (it.hasNext()) {
			IBCBound o = it.next();
			o.onSetSelection();
		}
	}

	/**
	 *
	 * @return list of one or more selections
	 */
	public List<String> getSelection() {
		return this.selection;
	}

	/**
	 * Sets one or more selections, after asking all bound components if it is ok
	 *
	 * @param sel a list of row keys for setting one or more selections (any empty
	 *            list means to deselect all)
	 */
	public void setSelection(ArrayList<String> sel) {
		if (canSetSelection()) {
			this.selection = sel;
			onSetSelection();
		}
	}

	/**
	 * Sets selection to the given row key, after asking all bound components if it
	 * is ok
	 *
	 * @param rowKey the row key for setting the selection
	 */
	public void setSelection(String rowKey) {
		if (canSetSelection()) {
			selection.clear();
			selection.add(rowKey);
			onSetSelection();
		}
	}

	/**
	 * Moves row pointer in result set based on the passed direction and extracts
	 * the row's key, which is then used to set the selection, after asking all
	 * bound components if it is ok. If the direction is set to SEL_DESELECT, then
	 * an empty list is used to deselect all.
	 *
	 * @param direction of movement in result set
	 */
	public void setSelection(int direction) {

		if (getRS() != null && getRS().size() > 0) {

			int i;

			switch (direction) {
			case SEL_FIRST:
				setSelection(getRS().get(0).getRowKey());
				break;
			case SEL_PREVIOUS:
				// select first if nothing was selected
				if (getSelection() == null || getSelection().size() == 0) {
					setSelection(getRS().get(0).getRowKey());
					break;
				}
				i = Math.max(0, getRS().indexOf((String) getSelection().get(0)) - 1);
				setSelection(getRS().get(i).getRowKey());

				break;
			case SEL_NEXT:
				// select first if nothing was selected
				if (getSelection() == null || getSelection().size() == 0) {
					setSelection(getRS().get(0).getRowKey());
					break;
				}
				i = Math.min(getRS().size() - 1, getRS().indexOf((String) getSelection().get(0)) + 1);
				setSelection(getRS().get(i).getRowKey());

				break;
			case SEL_LAST:
				setSelection(getRS().get(rs.size() - 1).getRowKey());
				break;
			case SEL_DESELECT:
				setSelection(new ArrayList<String>());
				break;
			}
		}
	}

	/**
	 *
	 * @return boolean whether the selection can be set
	 */
	public Boolean canSetSelection() {
		Boolean can = true;

		Iterator<IBCBound> it = boundComponents.iterator();
		while (it.hasNext()) {
			IBCBound o = it.next();
			if (!o.canSetSelection())
				return false;
		}

		return true;

	}

	/**
	 * Send signal without a payload to all bound components
	 *
	 * @param signal
	 */
	public void sendSignal(int signal) {
		sendSignal(signal, null);
	}

	/**
	 * Send signal with a payload to all bound components
	 *
	 * @param signal
	 * @param payload
	 */
	public void sendSignal(int signal, Object payload) {
		Iterator<IBCBound> it = boundComponents.iterator();
		while (it.hasNext()) {
			IBCBound o = it.next();
			o.onSignal(signal, payload);
		}
		handleSignal(signal, payload);
	}

	/**
	 * This BC binder will handle the different signals after all bound components
	 * have had their chance
	 *
	 * @param signal
	 * @param payload
	 * @throws Exception
	 */
	protected void handleSignal(int signal, Object payload)  {
		try {
			switch (signal) {
			case SIGNAL_SAVE:
				write();
				break;
			case SIGNAL_DELETE:
				deleteSelectedRows();
				break;
			case SIGNAL_NEW:
			case SIGNAL_BLANK:
				setSelection(SEL_DESELECT);
				break;
			case SIGNAL_INITIATE_TERMINATE:
				terminate();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			sendSignal(BCBinder.SIGNAL_ERROR, e);
		}
	}
	
	protected void terminate() {
		Iterator<IBCBound> it = boundComponents.iterator();
		// collect fields from all bound components that provide such; merge them with
		// data row
		while (it.hasNext()) {
			IBCBound o = it.next();
			if (!o.canTerminate()) {
				return;
			}
		}
		sendSignal(BCBinder.SIGNAL_TERMINATE);
		sendSignal(BCBinder.SIGNAL_DESTROY);
	}

	/**
	 * Called by write()
	 *
	 * @return DataRow
	 * @throws Exception
	 */
	protected DataRow getDataRowForWrite() throws Exception {
		DataRow writeDR = new DataRow();
		Iterator<IBCBound> it = boundComponents.iterator();
		
		// collect fields from all bound components that provide such
		// merge them with data row
		while (it.hasNext()) {
			IBCBound o = it.next();
			DataRow fields = o.getFieldsForWrite();
			if (DebugLevel>0)
				writeDebug(o.toString()+" getFieldsForWrite returned "+fields.toJson());
			
			if (fields == null || fields.isEmpty()) {
				continue;
			}
			writeDR.mergeRecord(fields, true);
		}
		
		return writeDR;
	}

	/**
	 * Called by handleSignal(int signal, Object payload) on SIGNAL_SAVE
	 * Writes data from components to the bc. components provide data with the getDataRowForWrite() method.
	 * after that, the new datarow is being integrated into the result set of the binder, either via addition or replacement.
	 *
	 * @throws Exception
	 */
	protected void write() throws Exception {
		DataRow dr = getDataRowForWrite();
		if (dr.isEmpty()) {
			return;
		}
		if (DebugLevel>0)
			writeDebug(" write "+dr.toJson());
		
		DataRow newDR = bc.write(dr);
		String rowID;
		// if selection size is 1 that means record was overwritten. merge old with new. Else add dr to resultset
		if (selection.size() == 1) {
			rowID = getSelection().get(0);
			DataRow oldDr = rs.get(rowID);
			oldDr.mergeRecord(newDR, true);
		} else {
			selection.clear();
			rs.add(newDR);
			rowID = rs.get(rs.indexOf(newDR)).getRowKey();
			selection.add(rowID);
		}
		setRS(rs);
		setSelection(rowID);
	}

	/**
	 * Called by handleSignal(int signal, Object payload) on SIGNAL_DELETE
	 *
	 * @throws Exception on validation failure or unexpected Exception
	 */
	protected void deleteSelectedRows() throws Exception {
		// only remove if selection can be set and any rows are selected
		if (!canSetSelection()) {
			return;
		}
		List<String> selectedRows = getSelection();
		if (selectedRows.isEmpty()) {
			return;
		}
		// first validate remove
		Iterator<String> it = selectedRows.iterator();
		ResultSet errors = new ResultSet();
		while (it.hasNext()) {
			String rowKey = it.next();
			DataRow dr;
			dr = getRS().get(rowKey);
			ResultSet currentErrors = bc.validateRemove(dr);
			for (DataRow error : currentErrors) {
				errors.add(error);
			}
		}
		if (errors.size() > 0) {
			throw new Exception(errors.get(0).getFieldAsString("MESSAGE"));
		}
		// now actual remove
		it = selectedRows.iterator();
		while (it.hasNext()) {
			String rowKey = it.next();
			DataRow dr;
			try {
				dr = getRS().get(rowKey);
				bc.remove(dr);
				rs.remove(rowKey);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// remove selection and refresh result set
		setSelection(new ArrayList<>());
		setRS(rs);
	}

	/**
	 * 
	 * write to debug logging
	 * 
	 * @param the debug output
	 */
	private void writeDebug(String string) {
		string = "BCBinder: "+bc.getClass()+" "+string;
		System.out.println(string);
	}

	
}