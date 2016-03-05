package com.forddirect.jsonic;

import java.io.File;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class ClassUtil {
	private final Set<Class<?>> classes = new HashSet<Class<?>>();
	
	public void addClassesFromPackage(String packageName) throws ClassNotFoundException {
		for (File sourceFile : new File(getSourceDir(packageName)).listFiles()) {
			classes.add(Class.forName(packageName + "." + sourceFile.getName().split("\\.")[0]));
		}
	}
	
	public Object instantiate(String className) throws Exception {
		Class<?> clazz = Class.forName(className);
		return instantiate(clazz);
	}
	
	public Object instantiate(Type type) throws Exception {
		return instantiate((Class<?>) type);
	}
	
	private String getSourceDir(String packageName) {
		return System.getProperty("user.dir") + "\\src\\main\\java\\" + packageName.replace(".", "\\");
	}
	
	private Object instantiate(Class<?> clazz) throws Exception {
		Object object = null;
		
		if (Modifier.isAbstract(clazz.getModifiers())) {
			object = getConcreteClass(clazz).newInstance();
		} else if (clazz.isEnum()) {
			object = clazz.getEnumConstants()[0];
		} else {
			object = clazz.newInstance();
		}
		
		return object;
	}
	
	private Class<?> getConcreteClass(Class<?> clazz) {
		Class<?> concreteClass = null;
		for (Class<?> superclass : classes) {
			if (clazz.isAssignableFrom(superclass) && !superclass.equals(clazz)) {
				concreteClass = superclass;
				break;
			}
		}
		
		return concreteClass;
	}
}