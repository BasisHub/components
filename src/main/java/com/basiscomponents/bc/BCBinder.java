package com.basiscomponents.bc;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.basis.startup.type.BBjVector;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public class BCBinder {

    private ArrayList<BCBound> boundComponents = new ArrayList<>();
    private BusinessComponent bc;
    
    private ResultSet rs;
    private DataRow attributes_rec    ;

	
	private ArrayList<String> selection = new ArrayList<>();

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
    
    public void register(BCBound component) {
        boundComponents.add(component);
        component.setBinder(this);
    }

    public void retrieve() throws Exception {
        this.rs = this.bc.retrieve();
        this.rs.createIndex();
        onSetData();
    }
    
    public void onSetData() {
        Iterator<BCBound> it = boundComponents.iterator();
        while (it.hasNext()) {
            BCBound o = it.next();
            o.onSetData();
        }
    }

    public void onSetSelection() {
        Iterator<BCBound> it = boundComponents.iterator();
        while (it.hasNext()) {
            BCBound o = it.next();
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

    public Boolean canSetSelection() {
    	Boolean can=true;

        Iterator<BCBound> it = boundComponents.iterator();
        while (it.hasNext()) {
            BCBound o = it.next();
            if (!o.canSetSelection())
            	return false;
        }
    	
		return true;
    	
    }
    
}