package de.fromscratch.frontend;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;

import de.fromscratch.frontend.osc.CCOSCMessage;



public class UdpSender {
	
    private DatagramChannel _myDatagramOutputChannel;
	
    private final static int BUFSIZE = 8192;
    private ByteBuffer _mySendBuffer;
    private int port;
    private String ip;
    private List<float[]> data;

    
    //TODO: json to osc
    public void sendRaw (String add, String ... args) {
  
    	try {
    		CCOSCMessage msg = new CCOSCMessage(add);
    		for (String arg: args) {
    			msg.add(arg);
    		}
    		msg.encode(_mySendBuffer);
    		_mySendBuffer.rewind();
    		_myDatagramOutputChannel.send(_mySendBuffer, new InetSocketAddress(ip, port));
    		_mySendBuffer.flip();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    
    public void close() {
    	try {
    		_myDatagramOutputChannel.close();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
	public UdpSender (String theIp, int thePort) {
		
		data = new ArrayList<>();
		for (int i=0; i<175; i++) {
			data.add(new float[2]);
		}
        try {
            ip = theIp;
            port = thePort;
            
            _myDatagramOutputChannel = DatagramChannel.open();
            _mySendBuffer = ByteBuffer.allocate(BUFSIZE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}