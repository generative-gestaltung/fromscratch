package de.fromscratch.node.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import com.sun.net.httpserver.HttpExchange;

public class FileHandler extends HttpAdapter {
	
	String _myResourcePath = "";
	
	public FileHandler (String theResourcePath) {
		_myResourcePath = theResourcePath;
	}
	
	@Override
	public void handle (HttpExchange t) throws IOException {
		System.out.println("FILE");
		String path = t.getRequestURI().toString();
		String output = getFile(_myResourcePath+"/"+path);
		output(t, output);
	}
	

	private String getFile(String pathToFile) {
		
		String ret = "";
		try {
			List<String>lines = Files.readAllLines(Paths.get(pathToFile));
			for (String l: lines) {
				ret += l+"\n";
			}
			return ret;
		}
		catch (Exception e) {
			e.printStackTrace();
			return ret;
		}
	}
}