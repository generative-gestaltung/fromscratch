package de.fromscratch.frontend;


import java.nio.ByteBuffer;

public interface Listener {
	public void onReceive(ByteBuffer theData);
}
