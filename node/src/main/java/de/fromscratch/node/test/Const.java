package de.fromscratch.node.test;

import de.fromscratch.api.Node;
import de.fromscratch.node.Output;

public class Const implements Node {
	@Output
	public float out;
	
	@Override
	public void update(float time) {
		out = 1000f;
	}
}