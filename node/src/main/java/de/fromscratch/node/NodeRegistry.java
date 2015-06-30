package de.fromscratch.node;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;

import de.fromscratch.api.Connection;
import de.fromscratch.api.Node;
import de.fromscratch.node.test.AudioDevice;
import de.fromscratch.node.test.Blank;
import de.fromscratch.node.test.Const;
import de.fromscratch.node.test.Filter;
import de.fromscratch.node.test.Osc;
import de.fromscratch.node.test.VCA;

public class NodeRegistry {
	
	/**
	 * Map that hold all registered node classes
	 */
	private Map<String, Object> classMap = new HashMap<String, Object>();
	
	
	
	/**
	 * function to generate debug data set. to be removed later
	 * @param thePackage
	 */
	public void registerClasses (String thePackage) {
		registerClass (AudioDevice.class);
		registerClass (Const.class);
		registerClass (Filter.class);
		registerClass (Osc.class);
		registerClass (VCA.class);
		registerClass (Blank.class);
	}
	
	/**
	 * Check if class implements the node interface and add it to the current session.
	 * classes are sorted into package name entries
	 * 
	 * @param theClass
	 */
	public void registerClass (Class<?> theClass) {
		
		Map<String, Object> map = nodeAsMap (theClass);

		// add 
		String packageName = theClass.getPackage().getName();
		if (!classMap.containsKey (packageName)) {
			classMap.put (packageName, new HashMap<String, Object>());
		}
		((HashMap<String,Object>)classMap.get(packageName)).put(theClass.getSimpleName(), map);
	}
	
	
	/**
	 * analyze the class and represent it as map with information about the patch relevant fields
	 * @param theClass
	 * @return
	 */
	public static Map<String, Object> nodeAsMap (Class<?> theClass) {
		
		// only register class if implements Node
		boolean implementsNode = false;
		for (Class<?> i : ClassUtils.getAllInterfaces(theClass)) {
			if (i.equals(Node.class)) {
				implementsNode = true;
				break;
			}
		}
	
		if (!implementsNode) return null;
						
		// map to represent the node add input and output names with type
		Analyzer analyzer = new Analyzer();	
		Map<String, Object> map = new HashMap<String, Object>();
				
		map.put ("inputs", analyzer.analyzeByAnnotation (theClass, Input.class));
		map.put ("outputs", analyzer.analyzeByAnnotation (theClass, Output.class));
		map.put ("classPath", theClass.getName());
		
		return map;
	}

	
	
	/**
	 * get all registered classses
	 * @return
	 */
	public Map<String, Object> getClassMap()  {
		return classMap;
	}
}
