package de.fromscratch.node;

import java.util.HashMap;
import java.util.Map;

public class PiManager {
	
	private static final String[] ips = {
		"10.0.0.1",
		"10.0.0.2",
		"10.0.0.3",
	};
	
	private Map<String, Object> getDummyPi() {
		Map<String, Object> pi = new HashMap<String, Object>();
		
		Map<String, Object> inputs = new HashMap<String, Object>();
		Map<String, Object> outputs = new HashMap<String, Object>();
		
		for (int i=0; i<(int)(Math.random()*4); i++) {
			inputs.put("in"+i, "float");
		}
		
		for (int i=0; i<(int)(Math.random()*4); i++) {
			outputs.put("out"+i, "float");
		}

		pi.put("inputs", inputs);
		pi.put("outputs", outputs);
		
		return pi;
	}
	
	
	public PiManager() {
		
	}
	
	public Map<String, Object> getPatch() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		for (String ip: ips) {
			map.put (ip, getDummyPi());
		}
		return map;
	}
}
