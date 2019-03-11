package com.basiscomponents.db.constants;

import java.util.Iterator;

import com.basis.bbj.client.datatypes.BBjVector;
import com.basis.bbj.client.util.BBjException;
import com.basis.bbj.proxies.BBjNamespace;

public class BBjNamespaceConstantsResolver implements ConstantsResolver{

	private BBjNamespace ns;
	
	@SuppressWarnings("unused")
	private BBjNamespaceConstantsResolver() {
	}
	
	public BBjNamespaceConstantsResolver(BBjNamespace ns){
		this.ns = ns;
	}
	
	@Override
	public String resolveConstants(String in)  {
		if (in.matches(".*\\[\\[.*\\]\\].*")){
			String out = in;
			
			BBjVector keys=null;
			try {
				keys = ns.getKeys();
				@SuppressWarnings("rawtypes")
				Iterator it = keys.iterator();
				while (it.hasNext()){
					String o = (String) it.next();
					String v = ns.getValue(o).toString(); 
					out = out.replaceAll("\\[\\["+o+"\\]\\]" ,v);
				}				
			} catch (BBjException e) {
				e.printStackTrace();
			}
			return out;
		}
		else 
			return in;
	}

}

