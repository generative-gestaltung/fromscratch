package de.fromscratch.node.test;

import de.fromscratch.node.Input;
import de.fromscratch.node.NodeImpl;
import de.fromscratch.node.Output;

public class VCA extends NodeImpl {
	
	@Input
	public float amp;
	@Input
	public float in;
	@Output
	public float out;
	
	@Override
	public void update(float time) {
		out = amp*in;
	}
}
