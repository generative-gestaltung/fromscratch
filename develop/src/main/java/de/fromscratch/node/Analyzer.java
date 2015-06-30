package de.fromscratch.node;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fromscratch.api.Control;


/**
 * Util class for reflection access.
 * @author maxg
 *
 */
public class Analyzer {
	
	public static final String ESCAPE_STRING = "@";

	private static Object convertToType (Object o, Class<?> type) {

		if (type==Float.TYPE) {
			return ((Number)o).floatValue();
		}
		if (type==Integer.TYPE) {
			return ((Number)o).intValue();
		}
		if (type==Long.TYPE) {
			return ((Number)o).longValue();
		}
		if (type==Double.TYPE) {
			return ((Number)o).doubleValue();
		}
	
		if (type==String.class) {
			return (String)o;
		}
		
		if (type.isEnum()) {
			for (Object con: type.getEnumConstants()) {
				if (con.toString().equals(o.toString())) {
					return con;
				}
			}
			return null;
		}
		return o;
	}
	
	public static void set (Object o, String startPath, String targetPath, Object value) {
	
		Class<?> cl = o.getClass();
		for (Field f : cl.getFields()) {
			// possible target field?
			if (f.getAnnotation(Control.class)!=null && isControlField(f.getType())) {
				
				String name = f.getAnnotation(Control.class).name();
				if (name==null || name.equals("")) {
					name=f.getName();
				}
				if ((startPath+"."+name).equals(targetPath)) {
					try {
						f.set(o, convertToType(value,f.getType()));
						return;
					}
					catch (Exception e) {
						e.printStackTrace();
						return;
					}
				}
			}	
			
			// not?? browse deeper
			else if (f.isAnnotationPresent(Control.class)){
				try {
					Object nextObject = f.get(o);
					String name = f.getAnnotation(Control.class).name();
					if (name==null || name.equals("")) {
						name=f.getName();
					}
					set(nextObject, startPath+"."+name, targetPath, value);
				}
				catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	private static boolean isControlField (Class<?> theClass) {
		return (theClass.isPrimitive() || theClass.equals(String.class) || theClass.isEnum());
	}
	
	private static Class<?> getControlClass(Object theObject) {
		Class<?> cl = theObject.getClass();
		if (cl.isEnum()) return Enum.class;
		else return cl;
	}

	public static Object getByAnnotationName (Object o, String theName, Class theAnnotation) throws Exception {
		for (Field f: o.getClass().getFields()) {
			if (f.isAnnotationPresent(theAnnotation)) {
				if (f.getName().equals(theName)) {
					return f.get(o);
				}
				else if (!isControlField(f.getType())){
					return getByAnnotationName(f.get(o), theName, theAnnotation);
				}
			}
		}
		return null;
	}
	
	public interface Filter {
		public boolean apply(Field theField);
	}
	
	public class FilterByInterface implements Filter {

		Class<?> implementedClass;
		public FilterByInterface (Class<?> theClass) {
			implementedClass = theClass;
		}
		@Override
		public boolean apply(Field theField) {
			return implementedClass.isAssignableFrom(theField.getType());
		}
	}
	
	public class FilterByAnnotation implements Filter {

		Class<? extends Annotation> annotation;
		public FilterByAnnotation (Class<? extends Annotation> theClass) {
			annotation = theClass;
		}
		@Override
		public boolean apply(Field theField) {
			return theField.isAnnotationPresent(annotation);
		}
	}
	
	public Map<String, Object> analyzeByClass (Object root, Class<?> theClass) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		if (root instanceof Class<?>) {
			analyzeFilteredClassOnly (map, (Class<?>)root, new FilterByInterface(theClass));
		}
		else {
			analyzeFiltered (map, root, new FilterByInterface(theClass));
		}
		return map;
	}
	
	public Map<String, Object> analyzeByAnnotation (Object root, Class<? extends Annotation> theClass) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		if (root instanceof Class<?>) {
			analyzeFilteredClassOnly (map, (Class<?>) root, new FilterByAnnotation(theClass));
		}
		else {
			analyzeFiltered (map, root, new FilterByAnnotation(theClass));
		}
		return map;
	}
	
	
	/**
	 * analyze only fields of class, no runtime
	 * @param theMap
	 * @param root
	 * @param theFilter
	 */
	private void analyzeFilteredClassOnly (Map<String, Object> theMap, Class<?> root, Filter theFilter) {
		
		for (Field f: root.getFields()) {
			if (theFilter.apply(f)) {	
				try {
					if (theFilter.apply(f)) {	
						String name = f.getName();
						if (name==null || name.equals("")) {
							name = f.getName();
						}
						theMap.put (name, f.getType().toString());
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	/**
	 * analyze fields of class and get runtime instances
	 * @param theMap
	 * @param o
	 * @param theFilter
	 */
	private void analyzeFiltered (Map<String, Object> theMap, Object o, Filter theFilter) {
		
		for (Field f: o.getClass().getFields()) {
			if (theFilter.apply(f)) {	
				try {
					Object child = f.get(o);
					if (theFilter.apply(f)) {
						
						String name = f.getName();
						if (name==null || name.equals("")) {
							name = f.getName();
						}
						theMap.put (name, child);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private void analyze (Map<String, Object> theMap, Object o, String path) {
		for (Field f: o.getClass().getFields()) {
			if (f.isAnnotationPresent(Control.class)) {	
				try {
					
					Object child = f.get(o);
					
					if (f.getAnnotation(Control.class)!=null && isControlField(f.getType())) {
						Map<String, Object> entry = new HashMap<String, Object>();	
						entry.put (ESCAPE_STRING+"type", getControlClass(child).getSimpleName());
						entry.put (ESCAPE_STRING+"value",child.toString());
						entry.put (ESCAPE_STRING+"min", f.getAnnotation(Control.class).min());
						entry.put (ESCAPE_STRING+"max", f.getAnnotation(Control.class).max());
						if (child.getClass().isEnum()) {
							Object[] o_a = child.getClass().getEnumConstants();
							String[] array = new String[o_a.length];
							for (int i=0; i<o_a.length; i++) {
								array[i] = o_a[i].toString();
							}
							entry.put(ESCAPE_STRING+"enumerator", array);
						}
						String name = f.getAnnotation(Control.class).name();
						if (name==null || name.equals("")) {
							name = f.getName();
						}
						theMap.put (name, entry);
					}

					if (!isControlField(f.getType())) {
//						System.out.println(f.getAnnotation(Control.class)+" "+f.getAnnotation(Control.class).name());
						String name = f.getAnnotation(Control.class).name();
						if (name==null || name.equals("")) {
							name=f.getName();
						}
						Map<String, Object> childMap = new HashMap<String, Object>();
						theMap.put(name, childMap);
						analyze (childMap, child, path);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
			
	public Map<String, Object>  analyze (Object o, String path) {
		Map<String, Object> myMap = new HashMap<String, Object>();
		analyze(myMap, o, path);
		return myMap;
	}
}