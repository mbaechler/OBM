package org.obm.push;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.obm.push.bean.CalendarBusyStatus;
import org.obm.sync.calendar.EventOpacity;

public class ObmEventToMsEventConverterBusyTest {

	private ObmEventToMsEventConverter converter;

	@Before
	public void setUp() {
		converter = new ObmEventToMsEventConverter();
	}

	@Test(expected=NullPointerException.class)
	public void testNullConversion() {
		converter.busyStatus(null);
	}

	
	@Test
	public void testTransparentConversion() {
		CalendarBusyStatus busyStatus = converter.busyStatus(EventOpacity.TRANSPARENT);
		assertThat(busyStatus).isEqualTo(CalendarBusyStatus.FREE);
	}

	@Test
	public void testBusyConversion() {
		CalendarBusyStatus busyStatus = converter.busyStatus(EventOpacity.OPAQUE);
		assertThat(busyStatus).isEqualTo(CalendarBusyStatus.BUSY);
	}

}
