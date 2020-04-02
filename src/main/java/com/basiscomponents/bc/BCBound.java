package com.basiscomponents.bc;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public interface BCBound {

    public void setBinder(BCBinder binder);
    
    public Boolean canSetSelection();
    
    public void onSetSelection();
    
    public void onSetData();
    
    public void onSignal(int signal, Object payload);
	
}
