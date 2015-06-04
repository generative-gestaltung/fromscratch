package de.fromscratch.node.test;

import de.fromscratch.api.Node;
import de.fromscratch.node.Input;
import de.fromscratch.node.Output;

public class VCA implements Node {
	
	@Input
	public float amp;
	@Input
	public float in;
	@Output
	public float out;
	
	@Override
	public void update(float time) {
		out = amp*in;
		System.out.println(out);
	}
}
