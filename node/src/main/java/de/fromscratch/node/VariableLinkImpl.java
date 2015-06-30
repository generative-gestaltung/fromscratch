package de.fromscratch.node;


import java.util.HashMap;
import java.util.Map;
import de.fromscratch.api.Node;
import de.fromscratch.api.VariableLink;

public class VariableLinkImpl extends VariableLink {


	String id;
	
	public VariableLinkImpl (Node theStartNode, Node theEndNode, String theSender, String theReceiver) {
		
		startNode = theStartNode;
		endNode = theEndNode;
		
		try {
			sender = theStartNode.getClass().getField(theSender);
			receiver = theEndNode.getClass().getField(theReceiver);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setStartNode (Node theStartNode) {
		startNode = theStartNode;
		try {
			sender = theStartNode.getClass().getField(sender.getName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setEndNode (Node theEndNode) {
		endNode = theEndNode;
		try {
			receiver = theEndNode.getClass().getField(receiver.getName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update() {
		try {
			receiver.set(endNode, sender.get(startNode));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Object> toMap() {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", sender.getType().getSimpleName());
		map.put("start", startNode.getId()+"."+sender.getName());
		map.put("end", endNode.getId()+"."+receiver.getName());
		
		return map;
	}
	
	@Override
	public String getId() {
		return id;
	}
}
