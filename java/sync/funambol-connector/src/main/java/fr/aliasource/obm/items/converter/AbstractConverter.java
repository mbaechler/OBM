package fr.aliasource.obm.items.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConverter {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
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
		logger.info("date: " + date + " converted to " + utc);
		return utc;
	}
	
	/**
	 * Returns a java.util.Date from the given sDate in utc format.
	 * 
	 * @param sDate
	 *            String
	 * @return Date
	 * @throws Exception
	 */
	protected Date getDateFromUTCString(String sDate) {
		Date date = new Date();

		if (sDate != null) {
			try {
				if (sDate.contains("T")) {
					if (!sDate.endsWith("Z")) {
						date = dateFormatEurope.parse(sDate);
					} else {
						date = dateFormatUTC.parse(sDate);
					}
				} else {
					if (sDate.contains("-")) {
						date = dateFormatTiret.parse(sDate);
					} else {
						date = dateFormat.parse(sDate);
					}
				}
				logger.info("parsed '" + sDate + "' as '" + date + "'");
			} catch (ParseException e) {
				logger.error("cannot parse crappy date: " + sDate);
			}
		}

		return date;
	}
}
