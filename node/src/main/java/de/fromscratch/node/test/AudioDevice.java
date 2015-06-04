package de.fromscratch.node.test;

import de.fromscratch.api.Node;
import de.fromscratch.node.Input;

public class AudioDevice implements Node {
	@Input
	public float in;
	
	@Override
	public void update(float time) {
//		System.out.println(in);
	}
}