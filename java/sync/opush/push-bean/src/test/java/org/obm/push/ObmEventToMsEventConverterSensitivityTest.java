package org.obm.push;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.obm.push.bean.CalendarSensitivity;
import org.obm.sync.calendar.EventPrivacy;

public class ObmEventToMsEventConverterSensitivityTest {

	private ObmEventToMsEventConverter converter;

	@Before
	public void setUp() {
		converter = new ObmEventToMsEventConverter();
	}

	@Test(expected=NullPointerException.class)
	public void testNullConversion() {
		converter.sensitivity(null);
	}

	
	@Test
	public void testPublicConversion() {
		CalendarSensitivity sensitivity = converter.sensitivity(EventPrivacy.PUBLIC);
		assertThat(sensitivity).isEqualTo(CalendarSensitivity.NORMAL);
	}

	@Test
	public void testPrivateConversion() {
		CalendarSensitivity sensitivity = converter.sensitivity(EventPrivacy.PRIVATE);
		assertThat(sensitivity).isEqualTo(CalendarSensitivity.PRIVATE);
	}

}
