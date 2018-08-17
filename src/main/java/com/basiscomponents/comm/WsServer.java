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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import com.basis.bbj.client.util.BBjException;
import com.basis.bbj.proxies.BBjAPI;
import com.basis.bbj.proxies.BBjNamespace;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class WsServer extends WebSocketServer {
	
	private HashMap<String,ArrayList<WebSocket>> ConList;
	private CrossEventDispatcher EDispatcher;
	public static Boolean DEBUG = false;  

	public WsServer( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
		ConList = new HashMap<>();
	}

	public WsServer( InetSocketAddress address ) {
		super( address );
		ConList = new HashMap<>();
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
		al = ConList.get(tag);
		if (al==null) {
			al = new ArrayList<>();
			ConList.put(tag, al);
		}
		al.add(conn);

		if (DEBUG)
			System.out.println("registered: "+ConList);

	}
	
	public void setEventDispatcher(CrossEventDispatcher ed) {
		this.EDispatcher = ed;
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
		
		if (this.EDispatcher != null) {
			try {
				this.EDispatcher.postPriorityCustomEvent(tag, message);
				
				if (DEBUG)
					System.out.println( "successfully passed message to EventDispatcher" );
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
		
		if (this.EDispatcher != null) {
			try {
				this.EDispatcher.postPriorityCustomEvent(tag, message.toString());
				
				if (DEBUG)
					System.out.println( "successfully passed message to EventDispatcher" );
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	public void send(String tag,String message) {
		
		if (DEBUG)
			System.out.println( "send message to "+tag +": "+ message.toString() );

		
		ArrayList<WebSocket> al;
		al = ConList.get(tag);
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


//	public static void main( String[] args ) throws InterruptedException , IOException {
//		WebSocketImpl.DEBUG = false;
//		int port = 8887; // 843 flash policy port
//		try {
//			port = Integer.parseInt( args[ 0 ] );
//		} catch ( Exception ex ) {
//		}
//		WsServer s = new WsServer( port );
//		s.start();
//		System.out.println( "ChatServer started on port: " + s.getPort() );
//
//		BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
//		while ( true ) {
//			String in = sysin.readLine();
//			s.broadcast( in );
//			if( in.equals( "exit" ) ) {
//				s.stop(1000);
//				break;
//			}
//		}
//	}
	
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
			Object ed = ns.getValue("bdi98273bv98723bv9e72bv9e72bv9e7bv92e7b2e");
			return ed;
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
		
//		// load up the key store
//		String STORETYPE = "JKS";
//		String KEYSTORE = "d:/github/Java-WebSocket/src/main/example/keystore.jks";
//		String STOREPASSWORD = "storepassword";
//		String KEYPASSWORD = "keypassword";
//
//		KeyStore ks = KeyStore.getInstance( STORETYPE );
//		File kf = new File( KEYSTORE );
//		ks.load( new FileInputStream( kf ), STOREPASSWORD.toCharArray() );
//
//		KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
//		kmf.init( ks, KEYPASSWORD.toCharArray() );
//		TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
//		tmf.init( ks );
//
//		SSLContext sslContext = null;
//		sslContext = SSLContext.getInstance( "TLS" );
//		sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );
//
//		ed.setWebSocketFactory( new DefaultSSLWebSocketServerFactory( sslContext ) );
		
		
		ed.start();
		return ed;
	}
	
}
