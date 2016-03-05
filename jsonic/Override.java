package com.forddirect.jsonic;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Override {
	private final static Pattern p = Pattern.compile("^((is)|(get)).*");
	
	/**
	 * Creates a method that outputs the object's property values.
	 * 
	 * @param	Object	Object on which to run toString method
	 * @return			Returns toString() method for the object
	 */
	public static String toStringHelper(Object object) {
		StringBuffer sb = new StringBuffer();
		
		for (Method method : object.getClass().getMethods()) {
			try {
				Matcher m = p.matcher(method.getName());
				sb.append(m.matches() ? method.getName().substring(m.group(1).length()) + "=" + method.invoke(object) + ";" : "");
			} catch (Exception e) {
			}
		}
		return object.getClass() + ": " + sb.toString();
	}
}
