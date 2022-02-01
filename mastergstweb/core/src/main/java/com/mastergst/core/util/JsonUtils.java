package com.mastergst.core.util;

import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class JsonUtils {
	
	public static String insertJsonField(String jsonObj, Map<String, String> fieldMappingMap) {
		DocumentContext json = JsonPath.parse(jsonObj);
		for (Map.Entry<String, String> entry : fieldMappingMap.entrySet()) {
			String value = entry.getValue();
			try {
				if(value.contains("{")) { // for object
					if (null == json.read(entry.getKey())) {
						jsonObj = json.add(entry.getKey(), JsonPath.parse(value).json()).jsonString();
					} else {
						jsonObj = json.set(entry.getKey(), JsonPath.parse(value).json()).jsonString();
						json = JsonPath.parse(jsonObj);
					}
				} else { // for array
					if (null == json.read(entry.getKey())) {
						jsonObj = json.add(entry.getKey(), value).jsonString();
					} else {
						jsonObj = json.set(entry.getKey(), value).jsonString();
						json = JsonPath.parse(jsonObj);
					}
				}
			} catch (PathNotFoundException e) {
				try {
					String path = entry.getKey().substring(0, entry.getKey().lastIndexOf("."));
					String key = entry.getKey().substring(entry.getKey().lastIndexOf(".") + 1);
					if(value.contains("{")) {
						jsonObj = json.put(path, key, JsonPath.parse(value).json()).jsonString();
					} else {
						jsonObj = json.put(path, key, value).jsonString();
					}
				} catch (PathNotFoundException ex) {
				}
			}
		}
		return jsonObj;
	}
	
	public static String getJsonValue(String json,String key) {
		try {
			return JsonPath.parse(json).read(key).toString();
		} catch (Exception e) {
			return null;
		}
	}
}
