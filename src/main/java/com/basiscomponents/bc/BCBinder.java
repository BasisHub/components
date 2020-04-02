package com.basiscomponents.bc;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.basis.startup.type.BBjVector;
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
        onSetData();
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
    
    public List getSelection() {
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
	            if (getSelection() == null || getSelection().size()==0) 
	            	setSelection(getRS().get(0).getRowKey());

	            i = Math.max(0,getRS().indexOf((String)getSelection().get(0))-1);
	            setSelection(getRS().get(i).getRowKey());
	    		
	    		break;
	    	case SEL_NEXT:
	            //select first if nothing was selected
	            if (getSelection() == null || getSelection().size()==0) 
	            	setSelection(getRS().get(0).getRowKey());

	            i = Math.min(getRS().size()-1,getRS().indexOf((String)getSelection().get(0))+1);
	            setSelection(getRS().get(i).getRowKey());	            
	            
	    		break;
	    	case SEL_LAST:
	            setSelection(getRS().get(rs.size()-1).getRowKey());
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
    }
    
}