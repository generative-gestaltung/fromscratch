package de.fromscratch.api;

import java.util.Map;

public abstract class MultiNode {
	
	Map<String, Object> inputs;
	Map<String, Object> outputs;
	
	public void update() {
		
	}
	
	public Map<String, Object> getInputs() {
		return inputs;
	}
	
	public Map<String, Object> getOutputs() {
		return outputs;
	}
	
}
