package de.fromscratch.api;

public interface Connection <NodeType, FieldType> {
	
	public String getId();
	public void update();
	public FieldType getSender();
	public FieldType getReceiver();
	public NodeType getStartNode();
	public NodeType getEndNode();
}
