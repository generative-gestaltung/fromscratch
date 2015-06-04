package de.fromscratch.node;


import de.fromscratch.api.VariableLink;

public class VariableLinkImpl extends VariableLink {


	
	public VariableLinkImpl (Object theStartNode, Object theEndNode, String theSender, String theReceiver) {
		
		startNode = theStartNode;
		endNode = theEndNode;
		
		try {
			sender = theStartNode.getClass().getField(theSender);
			receiver = theEndNode.getClass().getField(theReceiver);
		}
		catch (Exception e) {
			
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
}
