package de.fromscratch.frontend;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ControlUICreator2 {

	
	static final int SLIDER_HEIGHT = 15;
	static final int SLIDER_WIDTH = 300;
	
	String list = "";
	String presets = "";
	String editors = "";
	
	int cnt;
	
	public String createHtmlDocument (Map<String, Object> theMap, Set<String> presetsNames, String theAppName, String thePresetName, String theResourcePath) {
		
		list = "";
		presets = "";
		String html = "";
		
		try {
			List<String> lines = Files.readAllLines(Paths.get(theResourcePath+"/"+"index_template.html"));
			
			for (String line: lines) {
				html += line+"\n";
			}
			cnt = 0;
			addEntry(0, theAppName, theAppName, theMap);
			makePresetSelector(presetsNames, thePresetName);
			
			html = html.replaceAll("XXX", list);
			html = html.replaceAll("YYY", presets);
	        
			html = html.replace("APPNAME", theAppName);
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return html;
	}
	
	
	private void makePresetSelector (Set<String> presetNames, String thePresetName) {
		for (String preset: presetNames) {
			if (thePresetName.equals(preset)) {
				presets += "<option selected=\"selected\"value=\""+preset+"\">"+preset+"</option>\n";
			}
			else {
				presets += "<option value=\""+preset+"\">"+preset+"</option>\n";
			}
		}
	}
	
	String indent (int level) {
		return new String(new char[level]).replace("\0", "\t");
	}
	
	private void addEntry (int level, String key, String fullKey, Object value) {
		
		if (!(value instanceof Map<?,?>)) return;
	

	    		
		Map<String, Object> map = (Map<String, Object>)value;
		// LEAF
		if (map.containsKey("@type")) {
			
			String type = map.get("@type").toString();
			
			if (type.equals("Float")) {
				
				//list += indent(level)+"<div>"+key+" slider</div>\n";
				float min = Float.parseFloat(map.get("@min").toString());
				float max = Float.parseFloat(map.get("@max").toString());
				float val = Float.parseFloat(map.get("@value").toString());
				float step = max/100f;
				
				list += "<input id=\""+fullKey+"\" type=\"range\" min=\"0\" max=\""+max+"\" value=\""+val+"\" step=\""+step+"\" onchange=\"CC_WEB_GUI.changeValue(this.id)\"/> "+fullKey+"\n";
				list += "<form>\n"
						+"<input type=\"text\" id=\""+("@"+fullKey)+"\">\n"
						+"</form>\n";
				list += "<br>\n";
			}
			
			
			
			else if (type.equals("Code")) {
				
				String theSource = map.get("@value").toString();
				list += indent(level)+"<div id=\"head\" data-collapse>\n"
						+indent(level)+"<h3>"+fullKey+"</h3>\n";
				
				list += "<div>"
						+"<form>\n"
						+ "<textarea id=\""+fullKey+"\" name=\"code\">"
					    + theSource
					    + "</textarea></form>"
					    + "    <script>"
					    + "      var editor = CodeMirror.fromTextArea(document.getElementById(\""+fullKey+"\"), {"
					    + "        lineNumbers: true,"
					    + "        viewportMargin: Infinity"
					    + "      }); CC_WEB_GUI.registerEditor(\""+fullKey+"\", editor);"
					    + "    </script>"
					    + "</div><button name=\""+fullKey+"\" onclick=\"CC_WEB_GUI.updateCode(this.name);\">update</update>\n";
			}
		}
		
		// RECURSION
		else {

			list += indent(level)+"<div id=\"head\" data-collapse>\n"
					+indent(level)+"<h3>"+key+"</h3>\n"
					+indent(level)+"<div id=\"content\">\n";
			
			for (Map.Entry<String, Object> entry : map.entrySet()) {	
				if (level==0) addEntry (level+1, entry.getKey(), fullKey+"."+entry.getKey(), entry.getValue());
				else {			
					addEntry (level+1, entry.getKey(), fullKey+"."+entry.getKey(), entry.getValue());
				}
			}
			list += indent(level)+"</div>\n";
			list += indent(level)+"</div>\n";
		}
	}
}