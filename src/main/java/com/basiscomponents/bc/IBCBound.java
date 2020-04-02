package com.basiscomponents.bc;

public interface IBCBound {

    public void setBinder(BCBinder binder);
    
    public Boolean canSetSelection();
    
    public void onSetSelection();
    
    public void onSetData();
    
    public void onSignal(int signal, Object payload);
	
}
