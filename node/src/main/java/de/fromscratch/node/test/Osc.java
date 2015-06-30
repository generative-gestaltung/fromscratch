package de.fromscratch.node.test;

import de.fromscratch.api.Node;
import de.fromscratch.node.Input;
import de.fromscratch.node.NodeImpl;
import de.fromscratch.node.Output;

public class Osc extends NodeImpl {
	
	@Input
	public float phase;
	@Input
	public float freq = 1;
	@Output
	public float saw;
	@Output
	public float sin;
	
	@Override
	public void update(float time) {
		sin = (float)Math.sin(time*freq);
		saw = time%2 - 1;
	}
}