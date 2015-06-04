package de.fromscratch.frontend;


import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ControlUICreator {

	
//	static final int SLIDER_HEIGHT = 15;
//	static final int SLIDER_WIDTH = 300;
//	
//	String list = "";
//	String presets = "";
//	
//	int cnt;
//	
//	public String createHtmlDocument (Map<String, Object> theMap, Set<String> presetsNames, String theAppName, String thePresetName) {
//		
//		list = "";
//		presets = "";
//		String html = CCNIOUtil.loadString(Paths.get("index_template.html"));
//		cnt = 0;
//		addEntry(0, "", theMap);
//		makePresetSelector(presetsNames, thePresetName);
//		
//		html = html.replaceAll("XXX", "<svg width=\"500px\" height=\"400px\">"+list+"</svg>");
//		html = html.replaceAll("YYY", presets);
//		html = html.replace("APPNAME", theAppName);
//		return html;
//	}
//	
//	private void makePresetSelector (Set<String> presetNames, String thePresetName) {
//		
////		presets += "<option value=\"default\">default</option>\n";
//		for (String preset: presetNames) {
//			if (thePresetName.equals(preset)) {
//				presets += "<option selected=\"selected\"value=\""+preset+"\">"+preset+"</option>\n";
//			}
//			else {
//				presets += "<option value=\""+preset+"\">"+preset+"</option>\n";
//			}
//		}
//	}
//	
//	private void makeSliderEntry (String ID, float theSliderValue, int theLevel, int cnt) {	
//		list += "<rect x=\""+theLevel*20+"\" y=\""+(cnt*SLIDER_HEIGHT)+"\" width=\""+SLIDER_WIDTH+"\" height=\""+(SLIDER_HEIGHT)+"\" id=\""+ID+"\" style=\"fill:rgb(200,200,200);stroke-width:1;stroke:rgb(0,0,0)\" onmousedown=\"CC_WEB_GUI.slide('"+ID+"');\"/>\n";
//		list +=	"<rect x=\""+theLevel*20+"\" y=\""+(cnt*SLIDER_HEIGHT)+"\" width=\""+SLIDER_WIDTH*theSliderValue+"\" height=\""+(SLIDER_HEIGHT)+"\" id=\""+ID+"A\" style=\"fill:rgb(200,250,200);stroke-width:1;stroke:rgb(0,0,0)\" onmousedown=\"CC_WEB_GUI.slide('"+ID+"');\"/>\n";
//	}
//	
//	private void makeCodeEntry (String ID, int theLevel, int cnt) {
//		list += "<text x=\""+theLevel*20+"\" y=\""+cnt*SLIDER_HEIGHT+"\">"+ID+" CODE</text>\n";
//	}
//	
//	private void addEntry (int theLevel, String key, Object value) {
//		
//		if (!(value instanceof Map<?,?>)) return;
//		
//		cnt += 1;
//		list += "<text x=\""+theLevel*20+"\" y=\""+cnt*SLIDER_HEIGHT+"\">"+key+"</text>\n";
//		Map<String, Object> map = (Map<String, Object>)value;
//		
//		// LEAF
//		if (map.containsKey("@type")) {
//			Object o = map.get("@value");
//			
//			String type = map.get("@type").toString();
//			
//			if (type.equals("Float")) {
//				float v = Float.parseFloat(o.toString());
//				makeSliderEntry(key.toString(), v, theLevel, cnt);
//			}
//			else if (type.equals("Code")) {
//				makeCodeEntry(key.toString(), theLevel, cnt);
//			}
//			cnt += 1;
//		}
//		
//		else {
//			for (Map.Entry<String, Object> entry : map.entrySet()) {		
//				// RECURSION
//				if (theLevel==0) addEntry (theLevel+1, entry.getKey(), entry.getValue());
//				else addEntry (theLevel+1, key+"."+entry.getKey(), entry.getValue());
//			}
//		}
//	}
}