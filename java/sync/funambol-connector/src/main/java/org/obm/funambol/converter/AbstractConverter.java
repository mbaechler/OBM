package org.obm.funambol.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.obm.funambol.exception.ConvertionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.funambol.common.pim.common.PropertyWithTimeZone;


public abstract class AbstractConverter {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String DATE_UTC_PATTERN = "yyyyMMdd'T'HHmmss'Z'";
	private static final String DATE_FORMAT = "yyyyMMdd";
	private static final String DATE_FORMAT_T = "yyyy-MM-dd";
	private static final String DATE_FORMAT_EU = "yyyyMMdd'T'HHmmss";

	private final DateFormat dateFormat;
	private final DateFormat dateFormatEurope;
	private final DateFormat dateFormatTiret;
	private final DateFormat dateFormatUTC;
	
	protected AbstractConverter() {
		dateFormat = new SimpleDateFormat(DATE_FORMAT);
		dateFormatTiret = new SimpleDateFormat(DATE_FORMAT_T);

		dateFormatUTC = new SimpleDateFormat(DATE_UTC_PATTERN);
		dateFormatUTC.setTimeZone(TimeZone.getTimeZone("GMT"));

		dateFormatEurope = new SimpleDateFormat(DATE_FORMAT_EU);
		dateFormatEurope.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
	}

	protected String getUTCFormat(Date date) {
		String utc = null;
		if (date != null) {
			utc = dateFormatUTC.format(date);
		}
		return utc;
	}
	
	/**
	 * Returns a java.util.Date from the given sDate in utc format.
	 */
	protected Date getDateFromUTCString(String sDate) throws ConvertionException {
		try {
			if (sDate.contains("T")) {
				if (!sDate.endsWith("Z")) {
					return dateFormatEurope.parse(sDate);
				} else {
					return dateFormatUTC.parse(sDate);
				}
			} else {
				if (sDate.contains("-")) {
					return dateFormatTiret.parse(sDate);
				} else {
					return dateFormat.parse(sDate);
				}
			}
		} catch (ParseException e) {
			throw new ConvertionException("The date["+sDate+"] cannot be parsed", e);
		}
	}
	
	protected Date getDateFromProperty(PropertyWithTimeZone date) throws ConvertionException{
		TimeZone tz = getTimeZone(date);
		Calendar cal = Calendar.getInstance(tz);
		Date utcDate = getDateFromUTCString(date.getPropertyValueAsString());
		cal.setTime(utcDate);
		return cal.getTime();
	}

	private TimeZone getTimeZone(PropertyWithTimeZone date) {
		String tzName = "GMT";
		if(StringUtils.trimToNull(date.getTimeZone()) != null){
			tzName = date.getTimeZone();
		}
		return TimeZone.getTimeZone(tzName);
	}
}
