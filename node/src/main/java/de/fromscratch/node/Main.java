package de.fromscratch.node;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

import com.sun.net.httpserver.HttpServer;

import de.fromscratch.api.Patch;
import de.fromscratch.node.server.ClassListHandler;
import de.fromscratch.node.server.FileHandler;
import de.fromscratch.node.server.PatchRESTHandler;
import de.fromscratch.node.test.ComputePatchCreator;
import de.fromscratch.node.test.Filter;


public class Main {

	ComputePatch patch;
	HttpServer server;
	ServerSocketChannel socketChannel;
	int INPORT = 8887;
	String RESOURCE_PATH = ".";
	
	int frameCnt = 0;
	
	ClassManager classManager = new ClassManager();
	
	public void sleep (long millis) {
		try {
			Thread.sleep(millis);
		}
		catch (Exception e) {}
	}
	
	
	public Main() {
		System.out.println("running");
		patch = ComputePatch.create();
		
		classManager.registerClasses("dummy");
		
		try {
			socketChannel = ServerSocketChannel.open();
			server = HttpServer.create(new InetSocketAddress(INPORT),0);
			server.createContext("/", new FileHandler(RESOURCE_PATH));
			
			server.createContext("/REST", new PatchRESTHandler(patch));
			server.createContext("/CLASSES", new ClassListHandler(classManager));
			
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
			frameCnt += 1;
		}
	}
	
	public static void main (String[] args) {
		new Main();	
	}
}
