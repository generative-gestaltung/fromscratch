package de.fromscratch.node;

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
import de.fromscratch.api.Node;
import de.fromscratch.api.Patch;

public class ComputePatch extends Patch<VariableLinkImpl> {

	Map<String, RuntimeCompiler> compilers;
	Map<String, List<VariableLinkImpl>> outgoingLinks;
	Map<String, List<VariableLinkImpl>> ingoingLinks;

	/**
	 * create an empty patch
	 * @return
	 */
 	public static ComputePatch create() {
 		
 		Map<String, Object> map = new HashMap<String, Object>();
 		
		map.put("nodes", new HashMap<String, Node>());
		map.put("edges", new HashMap<String, VariableLinkImpl>());
		
		return new ComputePatch (map);
	}
	
 	public Map<String, Node> nodes() {
 		return (Map<String, Node>)map.get("nodes");
 	}
 	
 	public Map<String, VariableLinkImpl> edges() {
 		return (Map<String, VariableLinkImpl>)map.get("edges");
 	}
 	
 	public List<Node> nodesAsList() {
 		return new ArrayList<Node>(nodes().values());
 	}
 	
 	/**
 	 * load patch from json file, patch name also fits as base directory for node source files
 	 * @param theMap
 	 */
 	public static ComputePatch load (String thePath) {
 		
 		try {
 			List<String> lines = Files.readAllLines(Paths.get(thePath));
 			String data = "";
 			for (String line: lines) {
 				data += line+"\n";
 			}
 			
 			JSONParser parser = new JSONParser();
 			JSONObject o = (JSONObject)parser.parse(data);
 			
 			ComputePatch patch = ComputePatch.create();
 			patch.updatePatch(o);
 			
 			return patch;
 		}
 		
 		catch (Exception e) {
 			e.printStackTrace();
 			return null;
 		}
 	}
 	
 	public void clear () {
		map.put("nodes", new HashMap<String, Object>());
		map.put("edges", new HashMap<String, Object>());
 	}

 	
 	public void addNode (Map<String, Object> theNode) {
 		
 		String key = ""+nodes().keySet().size();
 		
 		try {
			
 			String classPath = (String)theNode.get("classPath");
 			String className = classPath.substring(classPath.lastIndexOf(".")+1);
 			
			RuntimeCompiler compiler = new RuntimeCompiler (className, classPath, "TMP", getId());
			compilers.put(key, compiler);
			
			Node newNodeInstance = (Node)compiler.recompile();
			newNodeInstance.setId(key);
			nodes().put(key, newNodeInstance);	
		}
		
		catch (Exception ex) {
			ex.printStackTrace();
			nodes().put(key, null);
		}	
 	}
 	
 	/**
 	 * do not explicitly invoke constructor, use static creation functions
 	 * @param theMap
 	 */
	private ComputePatch (Map<String, Object> theMap) {
		
		map = new HashMap<String, Object>(theMap);
		compilers = new HashMap<String, RuntimeCompiler>();
		
		outgoingLinks = new HashMap<String, List<VariableLinkImpl>>();
		ingoingLinks = new HashMap<String, List<VariableLinkImpl>>();
		
		
		Map<String, Node> nodes = (Map<String,Node>)map.get("nodes");
		for (String key : nodes.keySet()) {
			compilers.put(key, new RuntimeCompiler(key, nodes.get(key).getClass().toString(), "TMP", getId()));
		}
	}
	
	public void reset (Map<String, Object> theMap, String theNewId) {

		Map<String, Object> nodes = (Map<String,Object>)theMap.get("nodes");
		for (String key : nodes.keySet()) {
			compilers.put(key, new RuntimeCompiler(key, (String) ((Map<String,Object>)nodes.get(key)).get("classPath"), "TMP", theNewId));
		}
	}
	
	public String getNodeCode (String theNodeName) {
		return compilers.get(theNodeName).getCode();
	}
	
	public void setNodeCode (String theNodeName, String theCode) {
		compilers.get(theNodeName).setCode(theCode);
	}
	
	@Override
	public void setId (String theId) {
		map.put("id", theId);
		for (String nodeId: nodes().keySet()) {
			//compilers.get(nodeId).updatePatchPath(theId);
			compilers.put("nodeId", new RuntimeCompiler(nodeId, nodes().get(nodeId).getClass().toString(), "TMP", getId()));
		}
	}
	
	
	@Override
	public void update(float time) {
		

		for (String nodeKey: nodes().keySet()) {
			
			if (compilers.get(nodeKey).checkCodeUpdate()) {
				
				Node newNodeInstance = (Node)compilers.get(nodeKey).recompile();
				newNodeInstance.setId(nodeKey);
				nodes().put(nodeKey, newNodeInstance);
				
				if (outgoingLinks.containsKey(nodeKey)) {
					for (VariableLinkImpl link : outgoingLinks.get(nodeKey)) {
						link.setStartNode (newNodeInstance);
					}
				}
				
				if (ingoingLinks.containsKey(nodeKey)) {
					for (VariableLinkImpl link : ingoingLinks.get(nodeKey)) {
						link.setEndNode (newNodeInstance);
					}
				}
			}
			Node node = nodes().get(nodeKey);
			node.update(time);
		}
		
		for (String edgeKey: edges().keySet()) {
			VariableLinkImpl edge = (VariableLinkImpl)edges().get(edgeKey);
			edge.update();
		}
	}
	
	
	@Override
	public void updatePatch (Map<String, Object> theMap) {

		setId (theMap.get("id").toString());
		
		Map<String, Object> n = (Map<String, Object>) theMap.get("nodes");
		Map<String, Object> e = (Map<String, Object>) theMap.get("edges");
		
		if (e==null) {
			e = new HashMap<String, Object>();
		}
		
		Set<String> nodeKeys = new HashSet<String>();
		nodeKeys.addAll(nodes().keySet());
		
		Set<String> edgeKeys = new HashSet<String>();
		edgeKeys.addAll(edges().keySet());
		
		
		
		for (String key: n.keySet()) {
			
			nodeKeys.remove(key);
			if (nodes().containsKey(key)) continue;
			addNode ((Map<String,Object>)n.get(key));
		}
		
		
		for (String key: nodeKeys) {
			nodes().remove(key);
		}
		
		
		for (String key: e.keySet()) {
			edgeKeys.remove(key);
			if (edges().containsKey(key)) continue;
			String startNode = key.split (Pattern.quote("_"))[0].split(Pattern.quote("."))[0];
			String endNode   = key.split (Pattern.quote("_"))[1].split(Pattern.quote("."))[0];
			String sender    = key.split (Pattern.quote("_"))[0].split(Pattern.quote("."))[1];
			String receiver  = key.split (Pattern.quote("_"))[1].split(Pattern.quote("."))[1];
			
			VariableLinkImpl link = new VariableLinkImpl (nodes().get(startNode), nodes().get(endNode), sender, receiver);
			edges().put(key, link);
			
			if (!outgoingLinks.containsKey(startNode)) {
				outgoingLinks.put(startNode, new ArrayList<VariableLinkImpl>());
			}
			
			if (!ingoingLinks.containsKey(endNode)) {
				ingoingLinks.put(endNode, new ArrayList<VariableLinkImpl>());
			}

			outgoingLinks.get(startNode).add(link);
			ingoingLinks.get(endNode).add(link);
		}
		
		
		for (String key: edgeKeys) {
			edges().remove(key);
			String startNode = key.split (Pattern.quote("_"))[0].split(Pattern.quote("."))[0];
			String endNode   = key.split (Pattern.quote("_"))[1].split(Pattern.quote("."))[0];
			outgoingLinks.remove(startNode);
			ingoingLinks.remove(endNode);
		}
	}
	
	public Map<String, Object> getAsMap () {
		
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> nodesTmp = new HashMap<String, Object>();
		Map<String, Object> edgesTmp = new HashMap<String, Object>();
		
		
		Map<String,Node> nodes = (Map<String,Node>) map.get("nodes");
		for (String key : (nodes).keySet()) {
			nodesTmp.put(key, NodeRegistry.nodeAsMap(nodes.get(key).getClass()));
		}
		ret.put("nodes", nodesTmp);
		
		
		Map<String, VariableLinkImpl> edges = (Map<String, VariableLinkImpl>) map.get("edges");		
		for (String key : edges.keySet()) {
			edgesTmp.put(key, edges.get(key).toMap());
		}		
		ret.put("edges", edgesTmp);
		ret.put("id", map.get("id"));
		return ret;
	}
		
	
	public String getAsJSONString (String path) {
		Map<String, Object> map = getAsMap();
		map.put("id", getId());
		if (path.equals("nodes")) {
			map.remove("edges");
		}
		if (path.equals("edges")) {
			map.remove("nodes");
		}
		return new JSONObject(map).toJSONString();
	}
}