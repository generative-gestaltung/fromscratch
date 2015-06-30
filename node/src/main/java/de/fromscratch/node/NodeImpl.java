package de.fromscratch.node;

import de.fromscratch.api.Node;

public abstract class NodeImpl implements Node {

	protected String id;
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId (String theId) {
		id = theId;
	}
}
