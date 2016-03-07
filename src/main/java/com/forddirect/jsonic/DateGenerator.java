package com.forddirect.jsonic;

import java.util.Calendar;
import java.util.Date;

public class DateGenerator implements ValueGenerator<Date> {

	//
	// } else if (type.equals(Date.class)) {
	// value = Calendar.getInstance().getTime();

	
	public Object getValue(Date date) throws Exception {
		return Calendar.getInstance().getTime();
	}
	
}
