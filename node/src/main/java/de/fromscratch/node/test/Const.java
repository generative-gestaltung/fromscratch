package de.fromscratch.node.test;

import de.fromscratch.node.NodeImpl;
import de.fromscratch.node.Output;

public class Const extends NodeImpl {
	@Output
	public float out;
	
	@Override
	public void update(float time) {
		out = 10f;
	}
}