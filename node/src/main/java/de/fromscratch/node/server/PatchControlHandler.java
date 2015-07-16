package de.fromscratch.node.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sun.net.httpserver.HttpExchange;

import de.fromscratch.api.Control;
import de.fromscratch.api.Node;
import de.fromscratch.api.Patch;
import de.fromscratch.node.Analyzer;
import de.fromscratch.node.ComputePatch;
import de.fromscratch.node.LoadSave;


public class PatchControlHandler extends HttpAdapter {

	private URLDecoder decoder = new URLDecoder();
	private static final String DEFAULT_RESPONSE = "CONTROL API";
	ComputePatch patch;
	Analyzer analyzer = new Analyzer();
	Map<String, Object> layout = new HashMap<String, Object>();
	
	public PatchControlHandler (ComputePatch thePatch) {
		patch = thePatch;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void handle(HttpExchange t) throws IOException {
		
		String path = t.getRequestURI().toString();
		String[] tokens = path.split("/");
		
		if (t.getRequestMethod().equals("GET")) {
			
			if (tokens.length<3) {
				output(t,DEFAULT_RESPONSE);
				return;
			}
			
			layout.clear();
			for (String name: patch.nodes().keySet()) {
				layout.put(name, analyzer.analyzeByAnnotation (patch.nodes().get(name), Control.class));
			}
			
			output (t, new JSONObject(layout).toJSONString());
		}
		
		else if (t.getRequestMethod().equals("POST")) {
			
			String argname = tokens[2].split(Pattern.quote("?"))[1];
			argname = argname.substring(0, argname.indexOf("="));
			String val = URLDecoder.decode(tokens[2].split(Pattern.quote("="))[1], "UTF-8");
			
			argname = argname.substring(1);
			String nodeName = argname.split(Pattern.quote("."))[0];
			for (String name: patch.nodes().keySet()) {
				if (name.equals(nodeName)) {
					Node n = patch.nodes().get(name);
					Analyzer.set (n, "", argname.substring(argname.indexOf(".")), val);
				}
			}
//			analyzer.set(o, startPath, targetPath, value);
		}
	}
	
	public void output (HttpExchange t, String theVal) throws IOException {
        t.sendResponseHeaders(200, theVal.length());
        OutputStream os = t.getResponseBody();
        os.write(theVal.getBytes());
        os.close();
	}
}
