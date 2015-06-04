package de.fromscratch.frontend;



import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 *
 * @author maxg
 */
public class UdpReceiver implements Runnable{
    
    
    private DatagramChannel _myDatagramInputChannel;
	
    private final static int BUFSIZE = 8192;
    private ByteBuffer _mySendBuffer, _myReceiveBuffer;
    private byte[] rcvbuf = new byte[BUFSIZE];
    private int port;
    private String ip;
    private Thread _myThread;
    private Listener _myListener = null;
    private boolean RUN = false;
	
    public UdpReceiver (String theIp, int thePort) {
		
        try {
            ip = theIp;
            port = thePort;
            
            _myDatagramInputChannel = DatagramChannel.open();
            _myDatagramInputChannel.socket().bind(new InetSocketAddress(port));
            _myDatagramInputChannel.socket().setSoTimeout(1);
            
            _myReceiveBuffer = ByteBuffer.allocate(BUFSIZE);
            _myThread = new Thread(this);
        }
        catch (Exception e) {
            //e.printStackTrace();
		}
    }
    
    public void addListener (Listener theListener) {
    	_myListener = theListener;
    }
    
    public int receive() {
		
        _myReceiveBuffer.clear();
        String ret = null;
        DatagramPacket pkt = new DatagramPacket(rcvbuf, BUFSIZE);
        
		try {
			_myDatagramInputChannel.socket().receive(pkt); 
			_myReceiveBuffer.put(pkt.getData());
			_myReceiveBuffer.rewind();
			return 1;
		}
		catch (Exception e) {
			return 0;
		}
    }

    public void start() {
    	RUN = true;
    	if (_myThread!=null) {
    		_myThread.start();
    	}
    }
    
    public void stop() {
    	RUN = false;
    }
    
	@Override
	public void run() {
		while (RUN) {
			try {
				if (receive()>0) {
					_myListener.onReceive(_myReceiveBuffer);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
