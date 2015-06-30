package de.fromscratch.api;

import java.util.Map;

public abstract class Patch <ConnectionType>{

	protected Map<String, Object> map;

	public void setId (String theId) {
		map.put("id", theId);
	}
	
	public String getId () {
		return map.get("id").toString();
	}
	
	public void update (float time) {
		
	}
	
	public void updatePatch(Map<String, Object> theMap) {
		
	}
	
	public String toJSON() {
		return "";
	}
}