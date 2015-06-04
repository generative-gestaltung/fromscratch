package de.fromscratch.node;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

public class ReflectionController {
//
//	public void set (Map<String, Object> theMap, Object root) {
//		
//		Class<?> cl = root.getClass();
//		
//		iterateMap(theMap, cl, root);
//		
//		System.out.println();
//	}
//	
//	private boolean isControlField (Class<?> theClass) {
//		return (theClass.isPrimitive() || theClass.equals(String.class) || theClass.isEnum());
//	}
//	
//	private void iterateMap (Map theMap, Class theClass, Object obj) {
//        
//        Iterator<?> it = theMap.entrySet().iterator();
//        
//        while (it.hasNext()) {
//            
//            Map.Entry entry = (Map.Entry)it.next();
//            String key = entry.getKey().toString();
//            Object val = entry.getValue();
//            
//            if (val instanceof Map) {
//	            try {
//	            	
//	            	Field f = theClass.getField(key);
//	            	Map<String, Object> childMap = (Map<String,Object>)val;
//	            	if (childMap.containsKey("@value")) {
//	            		
//	            		// TODO: switch data types here
//	            		if (childMap.get("@type").equals("Code")) {
//	            			UpdateObject<?> updO = (UpdateObject)f.get(obj);
//	            			updO.setSource(childMap.get("@value").toString());
//	            		}
//	            		else {
//	            			f.set(obj, toType(childMap.get("@value").toString(), childMap.get("@type").toString()));
//	            		}
//	            	}
//	            	
//	            	else if (childMap.containsKey("@obj")) {
//	            		f.set(obj, childMap.get("@obj"));
//	            	}
//	            	
//	            	else {
//	            		Object child = f.get(obj);
//	            		Class cl = f.getType();
//	            		iterateMap(childMap, cl, child);
//	            	}
//	            }
//	            catch (Exception e) {
//	            	e.printStackTrace();
//	            }
//            }
//        }
//    }
//	
//	public Object toType (String value, String type) {
//		switch (type) {
//		case "Float":
//			return Float.parseFloat(value);
//		case "Integer":
//			return Integer.parseInt(value);
//		case "Boolean":
//			if (value.equals("true")) return true;
//			else return false;
//			
//		default: return value;
//		}
//	}
}