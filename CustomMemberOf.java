package com.line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sourceid.saml20.adapter.attribute.AttributeValue;

public class CustomMemberOf {
	
	private final Log logger = LogFactory.getLog(getClass());
	private final String className = "customMemberOf::";
	
	private final String ou = "OU";
	private final String cn = "CN";
	private final String ouEnt = "OU=Entitlements";
	
	public AttributeValue customMemberOfResult(AttributeValue attrValue, String replaceChar) {
		String methodName = "customMemberOfResult::";
		logger.debug(className + methodName + "Start");

		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<String> arrayList = new ArrayList<String>();
		StringBuilder result = new StringBuilder();
		
		try{
			if (attrValue != null) {
	            for (String value : attrValue.getValues()) {
	            	logger.debug(className + methodName + "value ::" + value);
	            	value = value.replace(replaceChar, "");
	            	logger.debug(className + methodName + "value after replace ::" + value);
	            	arrayList.add(value);
	            }

	            logger.debug(className + methodName + "arrayList::" + arrayList.size());

	    		Map<String, String> ouCnValue = null;
	    		for (int i = 0; i < arrayList.size(); i++) {
	    			String[] stringarray = arrayList.get(i).split(",");
	    			for (int k = stringarray.length - 1; k >= 0; k--) {

	    				String mainkey = stringarray[stringarray.length - 2].substring(3);
	    				if(stringarray.length==4) {
		    				if (resultMap.containsKey(mainkey)) {
		    					ouCnValue = (Map<String, String>) resultMap.get(mainkey);
		    				} else {
		    					ouCnValue = new HashMap<String, String>();
		    				}
	    				}
	    				logger.debug(className + methodName + "ouCnValue::" + ouCnValue);

	    				int j = k;
	    				j--;
	    				if (stringarray[k].equals(ouEnt.trim())) {

	    				} else if (stringarray[k].contains(ou.trim()) && stringarray[j].contains(ou.trim())) {
	    					String key = stringarray[k].substring(3);
	    					if (resultMap.containsKey(key)) {

	    						Map<String, String> value = (Map<String, String>) resultMap.get(key);
	    						if (value.containsKey(stringarray[j].substring(3))) {
	    							// String getValue=value.get(ouCnValue);
	    						} else {
	    							ouCnValue.put(stringarray[j].substring(3), "");
	    							resultMap.put(key, ouCnValue);
	    						}
	    					} else {
	    						ouCnValue.put(stringarray[j].substring(3), "");
	    						resultMap.put(key, ouCnValue);
	    					}
	    				} else if (stringarray[k].contains(ou.trim()) && stringarray[j].contains(cn.trim())) {
	    					if(stringarray.length==4) {
		    					Map<String, String> value = (Map<String, String>) resultMap.get(mainkey);
		    					if (value.containsKey(stringarray[k].substring(3))) {
		    						String Value = value.get(stringarray[k].substring(3));
		    						if (Value.contentEquals("")) {
		    							Value = "\""+stringarray[j].substring(3)+"\"";
		    						} else {
		    							Value = Value + ",\"" + stringarray[j].substring(3)+"\"";
		    						}
		    						value.put(stringarray[k].substring(3), Value);
		    						resultMap.put(mainkey, value);
		    					} else {
		    						ouCnValue.put(stringarray[k].substring(3), "\""+stringarray[j].substring(3)+"\"");
		    						resultMap.put(mainkey, ouCnValue);
		    					}
	    					}
	    					else {
	    						if(resultMap.containsKey(stringarray[k].substring(3))) {
	    							String Value=(String) resultMap.get(stringarray[k].substring(3));
	    							Value=Value+",\""+stringarray[j].substring(3)+"\"";
	    							resultMap.put(stringarray[k].substring(3), Value);
	    						}else {
	    							resultMap.put(stringarray[k].substring(3), "\""+stringarray[j].substring(3)+"\"");
	    						}
	    					}
	    				}
	    			}
	    		}
	    		logger.debug(className + methodName + "resultMap::" + resultMap);
	    		
	    		result.append("{[");
	    		
	    		for(Map.Entry<String,Object> entry: resultMap.entrySet()) {
	    			
	    			if(entry.getValue() instanceof String) 
	    			{result.append(""+entry.getKey()+":");
	    				result.append("{["+entry.getValue()+"]}");	
	    				result.append(",");
	    			}else if(entry.getValue() instanceof Map<?,?>) {
	    				result.append(entry.getKey()+":");
	    				Map<String,String> innerValue=( Map<String,String>) entry.getValue();
	    				result.append("{");
	    				for(String key:innerValue.keySet() ) {
	    					result.append("\""+key+"\":");
	    					result.append("{["+innerValue.get(key)+"]},");
	    				}
	    				result.deleteCharAt(result.length()-1);
	    				result.append("},");
	    			}
	    		}
	    		result.deleteCharAt(result.length()-1);
	    		result.append("]}");
	    		logger.debug(className + methodName + "result::" + result);
	        }
			
		} catch (Exception ex){
			logger.debug(className + methodName + "Exception is::" + ex + ", with message: " + ex.getMessage());
		} finally {
			logger.debug(className + methodName + "End");
		}
				
		return new AttributeValue(result.toString());
	}
}