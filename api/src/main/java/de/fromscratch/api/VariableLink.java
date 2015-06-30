package de.fromscratch.api;

import java.lang.reflect.Field;
import java.util.Map;

public abstract class VariableLink implements Connection<Node, Field> {

	protected Node startNode;
	protected Node endNode;
	
	protected Field sender;
	protected Field receiver;

	@Override
	public Field getSender() {
		return sender;
	}
	
	@Override
	public Field getReceiver() {
		return receiver;
	}
	
	@Override
	public Node getStartNode() {
		return startNode;
	}
	
	@Override
	public Node getEndNode() {
		return endNode;
	}	
	
	public Map<String, Object> toMap() {
		return null;
	}
}
