package de.fromscratch.api;

import java.util.Map;

public abstract class Patch <ConnectionType>{

	protected Map<String, Object> nodes;
	protected Map<String, ConnectionType> edges;

	public void update (float time) {
		
	}
	
	public void updatePatch(Map<String, Object> theMap) {
		
	}
	
	public String toJSON() {
		return "";
	}

	public Map<String, Object> getMap() {
		return null;
	}
}