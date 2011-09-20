package com.youku.soku.manage.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.util.StrutsTypeConverter;

public class DateConverter extends StrutsTypeConverter {

	private static final Logger log = Logger.getLogger(DateConverter.class);
	private static final String DEFAULT_DATE_FORMATE = "yyyy-MM-dd";
	
	private static final DateFormat[] ACCEPT_DATE_FORMATS = {
		new SimpleDateFormat(DEFAULT_DATE_FORMATE),
		new SimpleDateFormat("yyyy/MM/dd"),
		new SimpleDateFormat("dd/MM/yyyy")
	};
	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		if(values[0] == null || values[0].trim().equals("")) {
			return null;
		}
		for(DateFormat format : ACCEPT_DATE_FORMATS) {
			try {
				return format.parse(values[0]);
			} catch(ParseException e) {
				continue;
			}
		}
		log.debug("can not format date String: " + values[0]);
		return null;
	}

	@Override
	public String convertToString(Map context, Object o) {
		log.debug("&&&&&&&&& DateConverter convertToString   &&&&&&&&&");
		if(o instanceof Date) {
			SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMATE);
			try {
				return format.format(o);
			} catch(RuntimeException e) {
				return "";
			}
		}
		return "";
	}
	
}
