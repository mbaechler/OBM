package org.obm;

import java.util.Date;

import org.joda.time.DateTime;

public class DateUtils {

	public static Date date(String dateAsString) {
		return new DateTime(dateAsString).toDate();
	}
	
}
