package de.fromscratch.node.test;

import de.fromscratch.api.Node;
import de.fromscratch.node.Input;
import de.fromscratch.node.Output;

public class Filter implements Node {
	@Input
	public float freq;
	@Input
	public float in;
	@Output
	public float out;
	
	@Override
	public void update(float time) {
		out = in*10;
	}
}
