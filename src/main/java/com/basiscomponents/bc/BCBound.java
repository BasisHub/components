package com.basiscomponents.bc;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public interface BCBound {

    public void setBinder(BCBinder binder);
    
    public void onUpdateSelection();
    
    public void onUpdateData();
	
}
