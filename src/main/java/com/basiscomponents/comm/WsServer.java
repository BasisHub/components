package com.basiscomponents.comm;
/*
 * Copyright (c) 2010-2018 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

import com.basis.bbj.proxies.BBjAPI;
import com.basis.bbj.proxies.BBjNamespace;
import com.basis.startup.type.BBjException;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class WsServer extends WebSocketServer {
	
	private final HashMap<String,ArrayList<WebSocket>> conList;
	private CrossEventDispatcher eventDispatcher;
	public static Boolean DEBUG = false;  

	public WsServer( int port ) {
		super( new InetSocketAddress( port ) );
		conList = new HashMap<>();
	}

	public WsServer( InetSocketAddress address ) {
		super( address );
		conList = new HashMap<>();
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		
		if (DEBUG)
			System.out.println("onOpen "+conn.getResourceDescriptor());
		
		registerConnection(conn);
	}

	private void registerConnection(WebSocket conn) {
		String tag = getTag(conn.getResourceDescriptor());
		ArrayList<WebSocket> al;
		al = conList.get(tag);
		if (al==null) {
			al = new ArrayList<>();
			conList.put(tag, al);
		}
		al.add(conn);

		if (DEBUG)
			System.out.println("registered: "+ conList);

	}
	
	public void setEventDispatcher(CrossEventDispatcher ed) {
		this.eventDispatcher = ed;
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		//TODO: add a self-healing cleanup; check all connections for being closed and delete them
		// to keep the ConLinst clean!
		
		if (DEBUG)
			System.out.println("onClose "+conn.getResourceDescriptor());
		
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
//		broadcast( message );
		String tag = getTag(conn.getResourceDescriptor());
		
		if (DEBUG)
			System.out.println( "received message for "+tag +": "+ message );
		
		if (this.eventDispatcher != null) {
			try {
				this.eventDispatcher.postPriorityCustomEvent(tag, message);
				
				if (DEBUG)
					System.out.println( "successfully passed message to EventDispatcher" );
				
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}


	@Override
	public void onMessage( WebSocket conn, ByteBuffer message ) {
//		broadcast( message.array() );
		String tag = getTag(conn.getResourceDescriptor());
		if (DEBUG)
			System.out.println( "received message for "+tag +": "+ message.toString() );
		
		if (this.eventDispatcher != null) {
			try {
				this.eventDispatcher.postPriorityCustomEvent(tag, message.toString());
				
				if (DEBUG)
					System.out.println( "successfully passed message to EventDispatcher" );
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	public void send(String tag,String message) {
		
		if (DEBUG)
			System.out.println( "send message to "+tag +": "+ message.toString() );

		
		ArrayList<WebSocket> al;
		al = conList.get(tag);
		if (al!=null) {
			Iterator<WebSocket> it = al.iterator();
			while (it.hasNext()) {
				WebSocket conn = it.next();
				if (!conn.isClosed() || !conn.isClosing())
					conn.send(message);
				
				if (DEBUG)
					System.out.println( "-sent to "+conn.getResourceDescriptor()+" "+conn.getRemoteSocketAddress().toString() );

				
			}
		}
		
	}

	
	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}

	@Override
	public void onStart() {
		if (DEBUG)
			System.out.println("Server started!");
	}
	
	private String getTag(String resourceDescriptor) {
		String tag = new String(resourceDescriptor);
		if (tag.startsWith("/"))
			tag=tag.substring(1);
		
		if (tag.contains("?"))
			tag=tag.substring(0, tag.indexOf("?"));
		
		return tag;
	}

	public static Object getInstance(BBjAPI api, int port ) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException, KeyManagementException  {
		BBjNamespace ns = api.getGlobalNamespace();
		try {
			return ns.getValue("bdi98273bv98723bv9e72bv9e72bv9e7bv92e7b2e");
		} catch (BBjException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		WsServer ed = new WsServer(port);
		
		
		try {
			ns.setValue("bdi98273bv98723bv9e72bv9e72bv9e7bv92e7b2e",ed);
		} catch (BBjException e) {
			e.printStackTrace();
		}
		ed.start();
		return ed;
	}
	
}
