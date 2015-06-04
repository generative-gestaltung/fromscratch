package de.fromscratch.node;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import de.fromscratch.api.Connection;
import de.fromscratch.api.Node;
import de.fromscratch.api.Patch;

public class ComputePatch extends Patch<VariableLinkImpl> {

	
	public static ComputePatch create() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nodes", new HashMap<String, Object>());
		map.put("edges", new HashMap<String, Object>());
		return new ComputePatch(map);
	}
	
	public ComputePatch (Map<String, Object> theMap) {
		nodes = (Map<String, Object>) theMap.get("nodes");
		edges = (Map<String, VariableLinkImpl>) theMap.get("edges");
	}
	
	@Override
	public void update(float time) {
		for (String nodeKey: nodes.keySet()) {
			Node node = (Node)nodes.get(nodeKey);
			node.update(time);
		}
		
		for (String edgeKey: edges.keySet()) {
			VariableLinkImpl edge = (VariableLinkImpl)edges.get(edgeKey);
			edge.update();
		}
	}
	
	@Override
	public void updatePatch(Map<String, Object> theMap) {

		Map<String, Object> n = (Map<String, Object>) theMap.get("nodes");
		Map<String, Object> e = (Map<String, Object>) theMap.get("connections");
		
		Set<String> nodeKeys = new HashSet<String>();
		nodeKeys.addAll(nodes.keySet());
		
		Set<String> edgeKeys = new HashSet<String>();
		edgeKeys.addAll(edges.keySet());
		

		for (String key: n.keySet()) {
			nodeKeys.remove(key);
			if (nodes.containsKey(key)) continue;
			try {
				Map<String, Object> node = (Map<String, Object>)n.get(key);
				Object o = ClassLoader.getSystemClassLoader().loadClass((String)node.get("class")).newInstance();
				nodes.put(key, o);
			}
			catch (Exception ex) {
				ex.printStackTrace();
				nodes.put(key, null);
			}
		
		}
		
		for (String key: nodeKeys) {
			nodes.remove(key);
		}
		
		for (String key: e.keySet()) {
			edgeKeys.remove(key);
			if (edges.containsKey(key)) continue;
			String startNode = key.split (Pattern.quote("_"))[0].split(Pattern.quote("."))[0];
			String endNode   = key.split (Pattern.quote("_"))[1].split(Pattern.quote("."))[0];
			String sender    = key.split (Pattern.quote("_"))[0].split(Pattern.quote("."))[1];
			String receiver  = key.split (Pattern.quote("_"))[1].split(Pattern.quote("."))[1];
			edges.put(key, new VariableLinkImpl (nodes.get(startNode), nodes.get(endNode), sender, receiver));
		}
		
		for (String key: nodeKeys) {
			nodes.remove(key);
		}
	}
	
	public String toJSON() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nodes", nodes);
		map.put("edges", edges);
		
		return (new JSONObject(map)).toJSONString();
	}
	
	public Map<String, Object> getMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nodes", nodes);
		map.put("edges", edges);
		return map;
	}
}