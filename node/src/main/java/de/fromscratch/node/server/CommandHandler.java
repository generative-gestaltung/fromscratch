package de.fromscratch.node.server;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;

public class CommandHandler extends FileHandler {
	
	String _myResourcePath = "";
	
	public CommandHandler (String theResourcePath) {
		super(theResourcePath);
		_myResourcePath = theResourcePath;
	}
	
	@Override
	public void handle (HttpExchange t) throws IOException {
		String path = t.getRequestURI().toString();

		String[] tokens = path.split("/");
		String command = tokens[1];
		String argument = path.substring(tokens[0].length()+tokens[1].length() + 2);
		
		String ret = command+" -> "+argument;

		try {
			output (t, readFile(command+".html"));	
		}
		catch (Exception e) {
			e.printStackTrace();
			output(t,ERROR_FILE);
		}
	}
}