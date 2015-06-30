package de.fromscratch.node;


import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

import org.json.simple.JSONObject;

import de.fromscratch.node.server.ClassListHandler;
import de.fromscratch.node.server.CommandHandler;
import de.fromscratch.node.server.FileHandler;
import de.fromscratch.node.server.PatchRESTHandler;



public class Main {

	private ComputePatch patch;
	private HttpServer server;
	private int INPORT = 8888;
	private String RESOURCE_PATH = "resources";
	private NodeRegistry nodeRegistry = new NodeRegistry();
	
	
	public void sleep (long millis) {
		try {
			Thread.sleep(millis);
		}
		catch (Exception e) {}
	}
	
	
	public Main() {
		
		System.out.println("running");
		patch = ComputePatch.create();
		patch.setId("new_patch");
		
		nodeRegistry.registerClasses("dummy");
		
		try {
			server = HttpServer.create(new InetSocketAddress(INPORT),0);
			server.createContext("/", new FileHandler(RESOURCE_PATH));
			
			// rest api
			server.createContext("/REST", new PatchRESTHandler(patch));
			server.createContext("/CLASSES", new ClassListHandler(nodeRegistry));
			
			// custom controller
			server.createContext("/PatchEdit", new CommandHandler(RESOURCE_PATH));
			server.createContext("/NodeEdit",  new CommandHandler(RESOURCE_PATH));
			
			server.setExecutor(null);
			server.start();
		}
		
		catch (Exception e) {
			e.printStackTrace(); 
		}
			
		float time = 0;
		while(true) {
			patch.update(time);
			time += 0.1;
			sleep(100);
		}
	}
	
	
	public static void main (String[] args) {
		new Main();	
	}
}