package de.fromscratch.node.test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import de.fromscratch.api.Node;
import de.fromscratch.node.Analyzer;
import de.fromscratch.node.ComputePatch;
import de.fromscratch.node.VariableLinkImpl;


public class ComputePatchCreator {

	
	public Osc osc0 = new Osc();
	public Osc osc1 = new Osc();
	public Filter filt0 = new Filter();
	public VCA vca0 = new VCA();
	public Const const0 = new Const();
	public AudioDevice audio_dev0 = new AudioDevice();
	
	
	
	public String nodeString = ""
	 + "osc0 0\n"
	 + "osc1 0\n"
	 + "filt0 1\n"
	 + "vca0 1\n"
	 + "c0 3\n"
	 + "audio_dev0 2\n"; 
	 
	public String conString = ""
			  
	 + "const0.out osc0.freq 2\n"
	 + "osc0.sin filt0.freq 0\n"     
	 + "osc1.saw filt0.in 1\n"      
	 + "filt0.out vca0.in 1\n"    
	 + "vca0.out audio_dev0.in 1\n";
	
	
	
	Map<String, Object> map = new HashMap<String, Object>();
	
	public Object getNodeFromMap (Map<String, Object> theMap, String theName) {
		for (String key: theMap.keySet()) {
			if (key.equals(theName)) {
				return theMap.get(key);
			}
		}
		return null;
	}
	
	public void run() {

		Analyzer analyzer = new Analyzer();
		Map<String, Object> nodes = analyzer.analyzeByClass (this, Node.class);
		
		Map<String, Object> edges = new HashMap<String, Object>();
		
		
		for (String line: conString.split(Pattern.quote("\n"))) {
			Object startNode = getNodeFromMap(nodes, line.split(Pattern.quote(" "))[0].split(Pattern.quote("."))[0]);
			Object endNode = getNodeFromMap(nodes, line.split(Pattern.quote(" "))[1].split(Pattern.quote("."))[0]);
			
			if (startNode!=null && endNode!=null) {
				VariableLinkImpl link = new VariableLinkImpl (startNode, endNode, line.split(Pattern.quote(" "))[0].split(Pattern.quote("."))[1], line.split(Pattern.quote(" "))[1].split(Pattern.quote("."))[1]);
				edges.put(line.split(Pattern.quote(" "))[0]+"_"+line.split(Pattern.quote(" "))[1], link);
			}
		}
		map.put ("nodes", nodes);
		map.put ("edges", edges);
	}
	
	public Map<String, Object> getMap() {
		return map;
	}
	
	public static ComputePatch createPatch() {
		ComputePatchCreator creator = new ComputePatchCreator();
		creator.run();
		ComputePatch patch = new ComputePatch(creator.getMap());
		System.out.println(new JSONObject(creator.getMap()));
		return patch;
	}
}
