package com.basiscomponents.comm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.security.SecureRandom;

import com.basis.bbj.client.datatypes.BBjVector;
import com.basis.bbj.client.util.BBjException;
import com.basis.bbj.proxies.BBjAPI;
import com.basis.bbj.proxies.BBjNamespace;
import com.basis.bbj.proxies.BBjSessionInfo;
import com.basis.bbj.web.gwt.client.InitializationException;


public class CrossEventDispatcher  {
	
	private HashMap<String,BBjAPI> InterpreterMap;
	private HashMap<Integer,String> PIDMap;
	protected static SecureRandom random = new SecureRandom();
	public static Boolean DEBUG = false;
	
	
	public static Object getInstance(BBjAPI api)  {
		BBjNamespace ns = api.getGlobalNamespace();
		try {
			Object ed = ns.getValue("bde2937e9287be9273be9273be90273b0e273b0e273b0e273b0e7"+api.getConfig().getCommandLineObject().getOriginalClasspathName());
			return ed;
		} catch (BBjException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Object ed = new CrossEventDispatcher();
		try {
			ns.setValue("bde2937e9287be9273be9273be90273b0e273b0e273b0e273b0e7"+api.getConfig().getCommandLineObject().getOriginalClasspathName(),ed);
		} catch (BBjException e) {
			e.printStackTrace();
		}
		return ed;
	}
	
	private CrossEventDispatcher() {
		InterpreterMap = new HashMap<>();
		PIDMap = new HashMap<>();
	}
	
	public synchronized String register(BBjAPI api) {

		//first do a self-healing cleanup of garbage
		cleanup(api);
		
        String tag= Long.toString( Math.abs( random.nextLong() ), 256 )+Long.toString( Math.abs( random.nextLong() ), 256 )+System.currentTimeMillis();
		InterpreterMap.put(tag, api);
		try {
			PIDMap.put(api.getCurrentSessionInfo().getSessionID(), tag);
		} catch (BBjException e) {
			System.err.println("could not add session to PID Map!");
			e.printStackTrace();
		}
		
		return tag;
	}
	
	public void postPriorityCustomEvent(String tag, String payload) throws Exception {
		if (DEBUG)
			System.out.println("postPriorityCustomEvent");
		
		BBjAPI api = InterpreterMap.get(tag);

		if (api != null) {
			api.postPriorityCustomEvent(tag, payload);
			if (DEBUG)
				System.out.println("posting Event to "+tag);
		}
	}
	
	public String toString() {
		return PIDMap.size()+" sessions in map";
	}
	
	public void cleanup(BBjAPI api) {
		
		ArrayList<Integer> pidlist = new ArrayList<>();
		ArrayList<Integer> dellist = new ArrayList<>();
		try {
			BBjVector sis = api.getSessionInfos();
			
			Iterator it = sis.iterator();
			while (it.hasNext())
			{
				BBjSessionInfo si = (BBjSessionInfo) it.next();
				if (DEBUG)
					System.out.println("got "+si.getID());
				pidlist.add(si.getID());
			}
			
		} catch (BBjException e) {
			System.err.println("Cleanup failed!");
			e.printStackTrace();
			return;
		}
		
		Iterator<Integer> iit = PIDMap.keySet().iterator();
		while (iit.hasNext()) {
			Integer pid = iit.next();
			if (!pidlist.contains(pid)) {
				dellist.add(pid);
			}
		}
		
		iit = dellist.iterator();
		while(iit.hasNext()) {
			Integer pid = iit.next();
			String tag = PIDMap.get(pid);
			PIDMap.remove(pid);
			InterpreterMap.remove(tag);
		}

	}
	
}
