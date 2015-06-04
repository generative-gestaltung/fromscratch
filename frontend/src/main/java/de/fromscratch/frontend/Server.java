package de.fromscratch.frontend;


import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import de.fromscratch.api.RemoteControl;




public class Server {


	public static final String resourcePath = "resources";
	private HttpServer _myHTTPServer;
	private RemoteControl remote;
	private ControlUICreator2 creator;
	private ServerSocketChannel _mySocketChannel;

	
	
	private Map<String, Object> layout;
	private JSONObject  presetData;
	private Map<String, Object> data;
	private Map<String, Object> currentData;
	
	private JSONParser parser;

	private String layoutFile = "layout.json";
	private Set<String> presets;
	private String dataFile = "presetdata.json";
	
	
	// TODO: make server ignorant of rootname
	private String rootName = "";

	
	public Server (int theInport, RemoteControl theRemote, String theRootName, String theResourcePath) {

		rootName = theRootName;
		remote = theRemote;
		creator = new ControlUICreator2();
		
		parser = new JSONParser();
		loadLayout();
		loadPresetData();
		loadPreset("default");
		
		try {
			_mySocketChannel = ServerSocketChannel.open();
			_myHTTPServer = HttpServer.create(new InetSocketAddress(theInport),0);
			_myHTTPServer.createContext("/", new DefaultHandler());
			_myHTTPServer.setExecutor(null);
			_myHTTPServer.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateCurrentData (String add, String val) {
		int x = 0;
		String path[] = add.split(Pattern.quote("."));
		Map<String,Object> o = find(currentData, path, 0);
		o.put("@value", val);
	}
	
	// TODO: not only lvl+1 but check root 
	Map<String,Object> find (Map<String, Object> o, String[] p, int lvl) {
		if (lvl<p.length-2) {
			Object obj = o.get(p[lvl+1]);
			Map<String, Object> newO = (Map<String, Object>)obj;
			return find (newO, p, lvl+1);
		}
		else return (Map<String,Object>)o.get(p[lvl+1]);
	}
	
	
	void loadLayout() {
		try {
			layout = (JSONObject)parser.parse(new FileReader(layoutFile));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void copyMap (Map<String, Object> theMap, Map<String, Object> theNewMap) {
		
		for (Map.Entry<String, Object> entry : theMap.entrySet()) {
			if (entry.getValue() instanceof Map) {
				Map<String, Object> v = new HashMap<String, Object>();
				theNewMap.put(entry.getKey(), v);
				copyMap ((Map<String, Object>)entry.getValue(), v);
			}
			else {
				theNewMap.put(entry.getKey(), entry.getValue());
			}
		}
	}
	
	
	void loadPresetData () {
		try {
			presetData = (JSONObject)parser.parse(new FileReader(dataFile));
			
			JSONObject newLayout = new JSONObject();
			copyMap(layout, newLayout);
			
			((Map<String, Object>)presetData.get(rootName)).put("default", newLayout.get(rootName));
			presets = ((Map<String, Object>)presetData.get(rootName)).keySet();
		}
		catch (Exception e) {
			presetData = new JSONObject();
			presetData.put(rootName, new JSONObject());
			
			JSONObject newDat = new JSONObject();
			copyMap((JSONObject)layout.get(rootName), newDat);
			((JSONObject)presetData.get(rootName)).put("default", newDat);
			presets = new HashSet<String>();
			presets.add("default");
		}
	}
	
	void loadPreset (String thePreset) {
		
		Map<String, Object> dat = (Map<String, Object>)presetData.get(rootName);
		if (dat.containsKey(thePreset)) {
			Map<String, Object> presetData = (Map<String, Object>)dat.get(thePreset);
			currentData = new HashMap<String, Object>();
			copyMap(presetData, currentData);
		}
	}
	
	
	void savePreset (String theName) {
		try {
			((JSONObject)presetData.get(rootName)).put(theName, currentData);
			Files.write(Paths.get(dataFile), presetData.toJSONString().getBytes());
			loadPresetData();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	// localhost:8888/control/app0/preset0
	// localhost:8888/shader/app0/shader0
	// localhost:8888/code/app0/myObject0	
	// IP:PORT/FUNCTION/PROCESS/DATA
	
	public String[] shorten (String[] theString) {
		String[] ret = new String[theString.length-4];
		for (int i=0; i<ret.length; i++) {
			ret[i] = theString[i+3];
		}
		
		return ret;
	}
	
	public class DefaultHandler implements HttpHandler {
		
		@Override
		public void handle (HttpExchange t) throws IOException {
			
			String path = t.getRequestURI().toString();
			String[] tokens = path.split("/");
			String output = "";

			if (t.getRequestMethod().equals("GET")) {
					
				switch (tokens[1]) {
					case "control":
						String presetName = "default";
						if (tokens.length<3) break;
						
						if (tokens[tokens.length-1].contains("?")) presetName = tokens[tokens.length-1].substring(1);
						String[] ss = shorten(tokens);
						
						Map<String, Object> m = currentData;
						
						if (ss.length>0) {
							m = find(currentData, ss, 0);
						}
						output = creator.createHtmlDocument (m, presets, tokens[2], presetName, resourcePath);
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
				String val = new String(bytes);
				
				if (path.equals("/load")) {
					loadPreset(val.split("=")[1]);
				}
				else if (path.equals("/save")) {
					savePreset(val.split("=")[1]);
				}
				
				else {
					val = path.split (Pattern.quote("?"))[1];
					String a = val.split("=")[0];
					String v = val.split("=")[1];
					updateCurrentData (a, v);
					remote.setData (currentData);
				}
			}
		}
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
	
	public void output (HttpExchange t, String theVal) throws IOException {
        t.sendResponseHeaders(200, theVal.length());
        OutputStream os = t.getResponseBody();
        os.write(theVal.getBytes());
        os.close();
	}
}
