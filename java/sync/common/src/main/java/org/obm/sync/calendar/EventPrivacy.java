package org.obm.sync.calendar;

import java.security.InvalidParameterException;

public enum EventPrivacy {
	PUBLIC(0), PRIVATE(1);
	
	private final int sqlIntCode;

	private EventPrivacy(int code) {
		this.sqlIntCode = code;
	}

	public int toXmlIntCode() {
		return toSqlIntCode();
	}
	
	public int toSqlIntCode() {
		return sqlIntCode;
	}

	public static EventPrivacy fromXmlIntCode(int code) {
		return fromSqlIntCode(code);
	}
	
	public static EventPrivacy fromSqlIntCode(int code) {
		for (EventPrivacy value: values()) {
			if (value.sqlIntCode == code) {
				return value;
			}
		}
		throw new InvalidParameterException("code " + code + " doesn't match any EventPrivacy value");
	}
}
