package de.fromscratch.node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.json.simple.JSONObject;


public class LoadSave {
 	
 	public static void savePatch (String theNewId, ComputePatch thePatch) {
 		
 		try {
 			
 			String dirName = "TMP/"+theNewId;
 			if (!Files.exists(Paths.get(dirName))) {
 			    try {
 			        Files.createDirectory(Paths.get(dirName));
 			    } catch (IOException e) {
 			        System.err.println(e);
 			    }
 			}
 			String fileName = dirName+"/patch.json";
 			
 			Map<String, Object> map = thePatch.getAsMap();
 			
 			String oldId = (String)map.get("id");
 			map.put("id", theNewId);
 			
 			for (Object n: ((Map<String,Object>)map.get("nodes")).values()) {
 				String classPath = (String)((Map<String,Object>)n).get("classPath");
 				classPath = classPath.replace(oldId, theNewId);
 				((Map<String,Object>)n).put("classPath", classPath);
 			}
 			
 			Files.write (Paths.get(fileName), new JSONObject(map).toJSONString().getBytes());
 			
 			thePatch.reset(map, theNewId);
 			
 			/*
 			thePatch.setId(theNewId); 			
 			JSONObject newMap = (JSONObject)(new JSONParser()).parse(new FileReader(fileName));
 			//thePatch.updatePatch(newMap);
 			 */
 		}
 		
 		catch (Exception e) {
 			e.printStackTrace();
 		}
 	}
}
