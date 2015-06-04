package de.fromscratch.node;

import java.util.HashMap;
import java.util.Map;

import de.fromscratch.node.test.AudioDevice;
import de.fromscratch.node.test.Const;
import de.fromscratch.node.test.Filter;
import de.fromscratch.node.test.Osc;
import de.fromscratch.node.test.VCA;

public class ClassManager {
	
	private Map<String, Object> classMap = new HashMap<String, Object>();
	
	public void registerClasses (String thePackage) {
		registerClass (AudioDevice.class);
		registerClass (Const.class);
		registerClass (Filter.class);
		registerClass (Osc.class);
		registerClass (VCA.class);
	}

	
	public void registerClass (Class<?> theClass) {
		
		Analyzer analyzer = new Analyzer();	
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("inputs", analyzer.analyzeByAnnotation (theClass, Input.class));
		map.put("outputs", analyzer.analyzeByAnnotation (theClass, Output.class));
		
		String packageName = theClass.getPackage().getName();
		if (!classMap.containsKey(packageName)) {
			classMap.put(packageName, new HashMap<String, Object>());
		}
		((HashMap<String,Object>)classMap.get(packageName)).put(theClass.getSimpleName(), map);
	}

	
	public Map<String, Object> getClassMap()  {
		return classMap;
	}
}
