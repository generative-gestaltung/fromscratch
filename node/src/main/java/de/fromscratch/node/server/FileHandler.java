package de.fromscratch.node.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import com.sun.net.httpserver.HttpExchange;

/**
 * file handler with optional arguments support
 * @author maxg
 *
 */
public class FileHandler extends HttpAdapter {
	
	private String _myResourcePath = "";
	protected static final String ERROR_FILE = "404";
	public FileHandler (String theResourcePath) {
		_myResourcePath = theResourcePath;
	}
	
	protected String readFile (String thePath) throws Exception {
		String doc = "";
		List<String>lines = Files.readAllLines(Paths.get(_myResourcePath+"/"+thePath));
		for (String l: lines) {
			doc += l+"\n";
		}
		return doc;
	}
	
	@Override
	public void handle (HttpExchange t) throws IOException {
		
		String path = t.getRequestURI().toString();
		try {
			output (t, readFile(path));
		}
		catch (Exception e) {
			output(t, ERROR_FILE);
		}
	}
}