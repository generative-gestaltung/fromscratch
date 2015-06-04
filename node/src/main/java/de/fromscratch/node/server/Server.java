package de.fromscratch.node.server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import de.fromscratch.api.RemoteControl;


public class Server {

	private HttpServer _myHTTPServer;
	private ServerSocketChannel _mySocketChannel;

	public Server (int theInport, RemoteControl theRemote, String theResourcePath, List<HttpHandler> theHandlers) {
		
		try {
			_mySocketChannel = ServerSocketChannel.open();
			_myHTTPServer = HttpServer.create(new InetSocketAddress(theInport),0);
			_myHTTPServer.createContext("/", new FileHandler(theResourcePath));
			//_myHTTPServer.createContext("/patch", new PatchEditHandler());
			_myHTTPServer.setExecutor(null);
			_myHTTPServer.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public class DefaultHandler implements HttpHandler {
		
		@Override
		public void handle (HttpExchange t) throws IOException {
			
			System.out.println("DEFAULT");
			/*
			String path = t.getRequestURI().toString();
			String[] tokens = path.split("/");
			String output = "";
			System.out.println(path);
			
			if (t.getRequestMethod().equals("GET")) {
	
				switch (tokens[1]) {
					case "patch":
						System.out.println("PATCH");
						break;
					default:
						output = getFile(resourcePath+"/"+path);
						break;
				}
				output(t, output);
			}
			
			
			else if (t.getRequestMethod().equals("POST")) {
				InputStream stream = t.getRequestBody();
				byte[] bytes = new byte[stream.available()];
				stream.read(bytes);
			}
			*/
		}
	}
}
