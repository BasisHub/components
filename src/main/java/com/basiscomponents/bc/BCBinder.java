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
    
    
    //selection constants
    
    public static final int SEL_FIRST 	        = 501;
                                                
    public static final int SEL_PREVIOUS        = 502;
                                                
    public static final int SEL_NEXT 	        = 503;
                                                
    public static final int SEL_LAST 	        = 504;
    
    public static final int SEL_DESELECT		= 505;
                                                
    //signals
    
    //signal that data should be saved
    public static final int SIGNAL_SAVE         = 901;
                                                
    //signal that a new record should be added 
    public static final int SIGNAL_NEW          = 902;
    
    //signal that delete should occur (on current selection)
    public static final int SIGNAL_DELETE       = 903;

    //signal that components should render themselved blank
    public static final int SIGNAL_BLANK        = 904;

    //to announce that there is dirty (unsaved) data
    public static final int SIGNAL_DIRTY        = 904;
	
	

    private BCBinder() {}
    
    public BCBinder(BusinessComponent bc) {
    	this.bc = bc;
    	this.attributes_rec = bc.getAttributesRecord();
    }
    
    public BusinessComponent getBC() {
    	return this.bc;
    }
    
    public ResultSet getRS() {
		return this.rs;
	}
    
    private void setRS(ResultSet rs) {
    	this.rs = rs;
    	this.onSetData();
	}

    public DataRow getAttributesRecord() {
		return attributes_rec;
	}

	private void setAttributesRecord(DataRow attributes_rec) {
		this.attributes_rec = attributes_rec;
	}
    
    public void register(IBCBound component) {
        boundComponents.add(component);
        component.setBinder(this);
    }

    public void retrieve() throws Exception {
        this.rs = this.bc.retrieve();
        this.rs.createIndex();
        updateSelection();
        onSetData();
    }
    
    /**
     * checks if the items in the selection are still present in the resultset and removes 
     * them from the selection if not
     */
    protected void updateSelection() {
    	ResultSet rs = getRS();
    	ArrayList<String> selection = (ArrayList<String>)getSelection();
    	ArrayList<String> selectionNew = (ArrayList<String>)selection.clone();
    	for (String rowkey : selection) {
			try {
				//selected row no longer in result set; remove it
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
    
    public void onSetData() {
        Iterator<IBCBound> it = boundComponents.iterator();
        while (it.hasNext()) {
            IBCBound o = it.next();
            o.onSetData();
        }
    }

    public void onSetSelection() {
        Iterator<IBCBound> it = boundComponents.iterator();
        while (it.hasNext()) {
            IBCBound o = it.next();
            o.onSetSelection();
        }
    }
    
    public List<String> getSelection() {
		return this.selection;
	}
    
    public void setSelection(ArrayList<String> sel) {
    	if (canSetSelection()) {
    		this.selection = sel;
    		onSetSelection();
    	}
	}
    
    public void setSelection(String rowKey) {
    	if (canSetSelection()) {
	    	selection.clear();
	    	selection.add(rowKey);
	    	onSetSelection();
    	}
	}
    
    public void setSelection(int direction) {

    	if (getRS() != null && getRS().size()>0) {
    		
    	int i;
    	
    	switch (direction) {
	    	case SEL_FIRST:
	            setSelection(getRS().get(0).getRowKey());
	    		break;
	    	case SEL_PREVIOUS:
	            //select first if nothing was selected
	            if (getSelection() == null || getSelection().size()==0)  {
	            	setSelection(getRS().get(0).getRowKey());
	            	break;
	            }
	            i = Math.max(0,getRS().indexOf((String)getSelection().get(0))-1);
	            setSelection(getRS().get(i).getRowKey());
	    		
	    		break;
	    	case SEL_NEXT:
	            //select first if nothing was selected
	            if (getSelection() == null || getSelection().size()==0) {
	            	setSelection(getRS().get(0).getRowKey());
	            	break;
	            }
	            i = Math.min(getRS().size()-1,getRS().indexOf((String)getSelection().get(0))+1);
	            setSelection(getRS().get(i).getRowKey());	            
	            
	    		break;
	    	case SEL_LAST:
	            setSelection(getRS().get(rs.size()-1).getRowKey());
	    		break;
	    	case SEL_DESELECT:
	    		setSelection(new ArrayList<String>());
	    		break;
   			}
    	}    	
   	}

    public Boolean canSetSelection() {
    	Boolean can=true;

        Iterator<IBCBound> it = boundComponents.iterator();
        while (it.hasNext()) {
            IBCBound o = it.next();
            if (!o.canSetSelection())
            	return false;
        }
    	
		return true;
		
    }

    
    public void sendSignal(int signal) {
    	sendSignal(signal,null);
    }
    
    public void sendSignal(int signal, Object payload) {
        Iterator<IBCBound> it = boundComponents.iterator();
        while (it.hasNext()) {
            IBCBound o = it.next();
            o.onSignal(signal, payload);  
        }
        try {
			handleSignal(signal, payload);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
    }
    
    /**
     * the bc binder will handle the different signals after all bound components handled it.
     * @param signal
     * @param payload
     * @throws Exception
     */
    protected void handleSignal(int signal, Object payload) throws Exception {
    	switch (signal) {
		case SIGNAL_SAVE:
			write();
			break;
		case SIGNAL_DELETE:
			deleteSelectedRows();
			break;
		case SIGNAL_NEW:
			setSelection(SEL_DESELECT);
			break;
		default:
			break;
		}
    }
    
    protected DataRow getDataRowForWrite() throws Exception {
    	List<String> selection = getSelection();
    	DataRow writeDR;
    	//if exactly one item is selected, that item is overwritten, else a new entry is being assembled
    	if (selection.size() == 1) {
			writeDR = getRS().get(selection.get(0));
		} else {
			writeDR = new DataRow();
		}
    	Iterator<IBCBound> it = boundComponents.iterator();
    	//collect fields from all bound components that provide such; merge them with data row
        while (it.hasNext()) {
            IBCBound o = it.next();
            DataRow fields = o.getFieldsForWrite();
            if (fields == null || fields.isEmpty()) {
				continue;
			}
            writeDR.mergeRecord(fields, true);
        }
        return writeDR;
    }
    
    /**
     * called by handleSignal(int signal, Object payload) on SIGNAL_SAVE
     * @param dr data row to be saved
     * @throws Exception 
     */
    protected void write() throws Exception {
    	DataRow dr = getDataRowForWrite();
    	if (dr.isEmpty()) {
			return;
		}
    	DataRow newDR = bc.write(dr);
    	//if selection size is 1 that means record was overwritten. remove the old one
    	if (selection.size() == 1) {
    		rs.remove(getSelection().get(0));
    	}
    	selection.clear();
    	rs.add(newDR);
    	String newRowID = rs.get(rs.indexOf(newDR)).getRowKey();
    	selection.add(newRowID);
    	setRS(rs);
    	setSelection(newRowID);
    }
    
    /**
     * called by handleSignal(int signal, Object payload) on SIGNAL_DELETE
     * @throws Exception on validation failure or unexpected Exception 
     */
    protected void deleteSelectedRows() throws Exception  {
    	//only remove if selection can be set and any rows are selected
    	if (!canSetSelection()) {
    		return;
    	}
    	List<String> selectedRows = getSelection();
    	if (selectedRows.isEmpty()) {
			return;
		}
    	//first validate remove
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
    	//now actual remove
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
        //remove selection and refresh result set
        setSelection(new ArrayList<>());
        setRS(rs);
    }
    
}