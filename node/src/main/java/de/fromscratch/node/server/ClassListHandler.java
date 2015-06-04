package de.fromscratch.node.server;

import java.io.IOException;

import org.json.simple.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import de.fromscratch.node.ClassManager;

public class ClassListHandler extends HttpAdapter {

	ClassManager classManager;
	
	public ClassListHandler(ClassManager theManager) {
		classManager = theManager;
	}
	
	@Override
	public void handle(HttpExchange t) throws IOException {
		output(t, (new JSONObject(classManager.getClassMap())).toString());
	}
}
