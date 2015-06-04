package de.fromscratch.node.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.sun.net.httpserver.HttpExchange;
import de.fromscratch.api.Patch;


public class PatchRESTHandler extends HttpAdapter {

	private URLDecoder decoder = new URLDecoder();
	private static final String DEFAULT_RESPONSE = "REST API\ncommands:\n/REST/patch\n/REST/nodes\n/REST/edges";
	Patch<?> patch;
	
	public PatchRESTHandler (Patch<?> thePatch) {
		patch = thePatch;
	}
	
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

				Map<String, Object> patchMap = patch.getMap();
				String ret = (new JSONObject(patchMap)).toJSONString();
				
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
			
			case "classes":
				output(t, patch.toJSON());
				break;
				
				default:
					break;
			}
		}
		
		else if (t.getRequestMethod().equals("POST")) {
			String dec = URLDecoder.decode(tokens[1].split("=")[1], "UTF-8");
//			System.out.println(dec);
//			InputStream stream = t.getRequestBody();
//			byte[] bytes = new byte[stream.available()];
//			stream.read(bytes);
//			System.out.println(new String(bytes));
			
			JSONParser parser = new JSONParser();
			try {
				JSONObject o = (JSONObject) parser.parse(dec);
				patch.updatePatch(o);
			}
			catch (Exception e) {
				e.printStackTrace();
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
