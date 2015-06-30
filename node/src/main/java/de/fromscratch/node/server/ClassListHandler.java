package de.fromscratch.node.server;

import java.io.IOException;
import org.json.simple.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import de.fromscratch.node.NodeRegistry;

/**
 * Return a json string that holds all registered nodes.
 * 
 * @author maxg
 *
 */
public class ClassListHandler extends HttpAdapter {

	NodeRegistry nodeRegistry;
	
	public ClassListHandler (NodeRegistry theNodeRegistry) {
		nodeRegistry = theNodeRegistry;
	}
	
	@Override
	public void handle(HttpExchange t) throws IOException {
		output(t, (new JSONObject(nodeRegistry.getClassMap())).toString());
	}
}
