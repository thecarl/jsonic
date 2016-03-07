package com.forddirect.jsonic;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Spit {
	private static final Logger logger = LogManager.getLogger(Spit.class);

	private final ClassUtil classUtil = new ClassUtil();
	private final String packageName = "com.fordDirect.dre.deal.domain";
	private final String className = "Deal";
	private final Set<Type> domainsBeingCreated = new HashSet<Type>();
	private final Stack<Object> objectPath = new Stack<Object>();

	@Test
	public void testJavaToJson() throws Exception {
		classUtil.addClassesFromPackage(packageName);
		String json = javaToJson(populateObject(classUtil.instantiate(packageName + "." + className)));
		assertThat(json.length(), not(equalTo(0)));
	}

	private Object populateObject(Object object) throws Exception {
		objectPath.push(object);
		logger.debug(objectPath);
		for (Method method : object.getClass().getDeclaredMethods()) {
			if (isSetter(method)) {
				invokeSetter(object, method);
			}
		}
		objectPath.pop();
		return object;
	}

	private boolean isSetter(Method method) {
		return method.getName().startsWith("set");
	}
	
	private void invokeSetter(Object object, Method setter) throws Exception {
		Type parameterType = setter.getGenericParameterTypes()[0];
		setter.invoke(object, getValue(parameterType));
	}

	private Object getValue(Type type) throws Exception {
		Object value = null;

		if (type instanceof ParameterizedType) {
			value = getValues((ParameterizedType) type);
		} else if (type.equals(Date.class)) {
			value = Calendar.getInstance().getTime();
		} else if (type.equals(String.class)) {
			value = RandomStringUtils.randomAlphabetic(12).toUpperCase();
		} else if (type.equals(Integer.class)) {
			value = RandomUtils.nextInt(10000, 99999);
		} else if (type.equals(BigDecimal.class)) {
			value = BigDecimal.valueOf(RandomUtils.nextInt(10000, 99999));
		} else if (type.equals(Boolean.class)) {
			value = true;
		} else if (type.equals(int.class)) {
			value = RandomUtils.nextInt(10000, 99999);
		} else if (type.equals(double.class)) {
			value = RandomUtils.nextInt(10000, 99999);
		} else {
			value = getDomainValue(type);
		}

		return value;
	}
	
	private Collection<Object> getValues(ParameterizedType type) throws Exception {
		Type typeArgument = type.getActualTypeArguments()[0];

		Collection<Object> set = new HashSet<Object>();
		for (int i = 0; i < 3; i++) {
			set.add(getValue((Class<?>) typeArgument));
		}
		return set;
	}

	private synchronized Object getDomainValue(Type type) throws Exception {
		Object value = null;
		if (!domainsBeingCreated.contains(type)) {
			domainsBeingCreated.add(type);
			value = classUtil.instantiate(type);
			populateObject(value);
			domainsBeingCreated.remove(type);
		}
		return value;
	}
	
	public String javaToJson(Object object) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
	}
}