package de.fromscratch.api;

import java.lang.reflect.Field;

public abstract class VariableLink implements Connection {

	protected Object startNode;
	protected Object endNode;
	
	protected Field sender;
	protected Field receiver;
}
