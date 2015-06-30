package de.fromscratch.node.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sun.net.httpserver.HttpExchange;

import de.fromscratch.api.Patch;
import de.fromscratch.node.ComputePatch;


public class PatchRESTHandler extends HttpAdapter {

	private URLDecoder decoder = new URLDecoder();
	private static final String DEFAULT_RESPONSE = "REST API\ncommands:\n/REST/patch\n/REST/nodes\n/REST/edges";
	ComputePatch patch;
	
	public PatchRESTHandler (ComputePatch thePatch) {
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
			
			switch (tokens[2]) {
			
			case "patch":
				Map<String, Object> patchMap = patch.getAsMap();
				String ret = patch.getAsJSONString("/");
				
				for (int i=3; i<tokens.length; i++) {
					Object entry = patchMap.get(tokens[i]);
					if (entry instanceof Map<?, ?>) {
						patchMap = (Map<String,Object>)entry;
						ret = (new JSONObject(patchMap)).toJSONString();
					}
					else {
						ret = entry.toString().split("@")[0];
						break;
					}
				}
				
				output(t, ret);
				break;
			//TODO:
			case "nodes":
				if (tokens.length<4) {
					output(t, patch.getAsJSONString("/nodes"));
				}
				else {
					if (tokens[3].contains("?")) {
					}
					else {
						String nodeName = path.substring(tokens[0].length()+tokens[1].length()+tokens[2].length()+3);
						output(t, patch.getNodeCode(nodeName));
					}
				}
				break;
				
			default:
				break;
			}
		}
		
		else if (t.getRequestMethod().equals("POST")) {
			
			String argname = tokens[1].split(Pattern.quote("?"))[1];
			argname = argname.substring(0, argname.indexOf("="));
			String val = URLDecoder.decode(tokens[1].split(Pattern.quote("="))[1], "UTF-8");

			
			if (argname.equals("patch") || argname.equals("save") || argname.equals("addnode")) {

				JSONObject o = null;
				JSONParser parser = new JSONParser();
				try {
					o = (JSONObject) parser.parse(val);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				if (argname.equals("patch")) {
					patch.updatePatch(o);
				}
				else if (argname.equals("save")){
					patch.save (o);
				}
				else if (argname.equals("addnode")) {
					patch.addNode(o);
				}
			}
			
			else {
				patch.setNodeCode(argname, val);
			}
		}
	}
	
	public void output (HttpExchange t, String theVal) throws IOException {
        t.sendResponseHeaders(200, theVal.length());
        OutputStream os = t.getResponseBody();
        os.write(theVal.getBytes());
        os.close();
	}
}
