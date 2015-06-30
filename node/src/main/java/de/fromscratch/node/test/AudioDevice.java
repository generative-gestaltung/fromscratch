package de.fromscratch.node.test;

import de.fromscratch.node.Input;
import de.fromscratch.node.NodeImpl;

public class AudioDevice extends NodeImpl {
	
	@Input
	public float in;
	
	@Override
	public void update(float time) {
	
	}
}