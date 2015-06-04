package de.fromscratch.examples;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import de.fromscratch.api.Control;
import de.fromscratch.api.RemoteControl;
import de.fromscratch.frontend.Server;
import de.fromscratch.node.Analyzer;



public class Main {

	public class ColorControl {
		@Control (min = 0, max = 100)
		public float r = 0;

		@Control (min = 0, max = 100)
		public float g = 0;	
		
		@Control (min = 0, max = 100)
		public float b = 0;	
	}
	
	public class VectorControl {
		@Control (min = 0, max = 100)
		public float x = 0;

		@Control (min = 0, max = 100)
		public float y = 0;	
		
		@Control (min = 0, max = 100)
		public float z = 0;			
	}
	
	public class DummyRemote implements RemoteControl {

		@Override
		public void setData(Map<String, Object> theData) {
			for (String entry: theData.keySet()) {
				System.out.println(entry);
			}
		}
	}
	
	Server server;

	
	@Control
	public ColorControl c = new ColorControl();
	
	@Control
	public VectorControl size = new VectorControl();
	
	@Control
	public VectorControl oscillate = new VectorControl();

	
	public Main() {
		
		
		Analyzer analyzer = new Analyzer();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("app0", analyzer.analyze(this, "app0"));
		JSONObject obj = new JSONObject(map);
		
		try {
			Files.write(Paths.get("layout.json"), obj.toJSONString().getBytes());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		server = new Server(8888, new DummyRemote(), "app0", "resources");
		
		while (true) {
			try {
				Thread.sleep(100);
//				updateObject.update(0.1f);
			}
			catch (Exception e) {
			}
		}
	}
	
	
	public static void main (String [] args) {
		Main app = new Main();
	}
}