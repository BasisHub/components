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
    	this.onUpdateData();
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
        onUpdateData();
    }
    
    public void onUpdateData() {
        Iterator<BCBound> it = boundComponents.iterator();
        while (it.hasNext()) {
            BCBound o = it.next();
            o.onUpdateData();
        }
    }

    public void onUpdateSelection() {
        Iterator<BCBound> it = boundComponents.iterator();
        while (it.hasNext()) {
            BCBound o = it.next();
            o.onUpdateSelection();
        }
    }
    
    public List getSelection() {
		return this.selection;
	}
    
    public void setSelection(ArrayList<String> sel) {
        this.selection = sel;
        onUpdateSelection();
	}
    
    public void setSelection(String rowKey) {
    	selection.clear();
    	selection.add(rowKey);
    	onUpdateSelection();
	}

    
}