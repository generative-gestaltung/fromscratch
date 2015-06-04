//package de.fromscratch.node;
//
//import java.nio.ByteBuffer;
//import java.nio.channels.ServerSocketChannel;
//import java.nio.channels.SocketChannel;
//import java.util.HashMap;
//import java.util.Map;
//
//import de.fromscratch.frontend.Listener;
//
////import org.json.simple.JSONObject;
////import org.json.simple.parser.JSONParser;
//
//
//
//public class RemoteControl implements Runnable, Listener {
//
////	private Object _myControlObject;
////	private Analyzer _myAnalyzer = new Analyzer();
////	private ReflectionController _myReflectionController = new ReflectionController();	
////	private ByteBuffer _myReceiveBuffer;
////	private JSONParser parser = new JSONParser();
////	private boolean running = false;
////	private JSONObject _myLayout; 
////	private ServerSocketChannel _mySocketChannel;
////	private UdpReceiver udpRecv;
////	private String rootName = "";
////	
////	public RemoteControl (Object theObject, String theRootName, int theInport) {
////		
////		rootName = theRootName;
////		udpRecv = new UdpReceiver("127.0.0.1", 1234);
////		udpRecv.addListener(this);
////		udpRecv.start();
////		
////		_myControlObject = theObject;
////		
////		getLayout();
////	}
////	
////
////
////	@Override
////	public void onReceive(ByteBuffer theData) {
////		
////		CCOSCMessage msg = (CCOSCMessage)new CCOSCPacketCodec().decode(theData);
////		
////		String type = "@Float"; //msg.stringArgument(1);
////		Object val = msg.argument(0);
////		
////		_myAnalyzer.set(_myControlObject, rootName, msg.address(), val);
////	}
////
////	public JSONObject getLayout() {
////		Map<String, Object> map = _myAnalyzer.analyze(_myControlObject, "");
////		_myLayout = new JSONObject(map);
////		return _myLayout;
////	}
////	
////	public String getCode (String theClassPath) {
////		try {
////			Object codeObject = Analyzer.getByAnnotationName (_myControlObject, theClassPath.substring(theClassPath.lastIndexOf("/")+1), Code.class);
////			recompile(codeObject);
////			return codeObject.getClass().toString();
////		}
////		catch (Exception e) {
////			e.printStackTrace();
////			return "NULL";
////		}			
////	}
////	
////	public void recompile (Object theObject) {
////		RuntimeCompiler compiler = new RuntimeCompiler(theObject.getClass().getSimpleName(), theObject.getClass().getCanonicalName().replace(".", "/"), "TMP");
////		theObject = compiler.recompile();
////		
////		Map<String, Object> theMap = new HashMap<String, Object>();
////		theMap.put("renderer", new HashMap<String,Object>());
////		((HashMap<String,Object>)theMap.get("renderer")).put("@obj",theObject);
////		_myReflectionController.set(theMap, _myControlObject);
////	}
////	
////	public void setData (String theData) {
////		try {
////			JSONObject o = (JSONObject)parser.parse(theData);
////			_myReflectionController.set(o, _myControlObject);
////		}
////		catch (Exception e) {
////			e.printStackTrace();
////		}
////	}
////	
////	public void setData (Map<String, Object> theData) {
////		_myReflectionController.set(theData, _myControlObject);
////	}
////	
////	@Override 
////	public void run() {
////		try {
////			while(running) {
////				
////				SocketChannel ch = _mySocketChannel.accept();
////				ch.read(_myReceiveBuffer);
////				_myReceiveBuffer.flip();
////				
////				String payload = new String(_myReceiveBuffer.array());
////				if (payload.equals("GET_LAYOUT")) {
////					
////				}
////				else {
////					JSONObject json = (JSONObject)parser.parse(payload);
////					_myReflectionController.set(json, _myControlObject);
////				}				
////			}
////		}
////		catch (Exception e) {
////			e.printStackTrace();
////		}
////	}
////	
////	public void stop() {
////		running = false;
////	}
//}
