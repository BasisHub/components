package com.basiscomponents.bc;
import com.basiscomponents.db.DataRow;;

public interface IBCBound {

    public void setBinder(BCBinder binder);

    public Boolean canSetSelection();

    public Boolean canTerminate();
    
    public void onSetSelection();
    
    public void onSetData();
    
    public void onSignal(int signal, Object payload);
    
    public DataRow getFieldsForWrite();
	
}
