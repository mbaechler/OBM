package org.obm.push.bean;

import static org.junit.Assert.*;

import org.fest.assertions.Assertions;
import org.junit.Test;

public class MSEventTest {

	@Test
	public void testFindReplyTypeNoAttendee() {
		MSEvent msEvent = new MSEvent();
		Assertions.assertThat(msEvent.findReplyType()).isNull();
	}

	@Test
	public void testFindReplyTypeOneAttendee() {
		MSEvent msEvent = new MSEvent();
		msEvent.addAttendee(createAttendee());
		Assertions.assertThat(msEvent.findReplyType()).isNull();
	}

	private MSAttendee createAttendee() {
		return new MSAttendee();
	}
	
	@Test
	public void testFindReplyTypeTwoAttendee() {
		MSEvent msEvent = new MSEvent();
		Assertions.assertThat(msEvent.findReplyType()).isNull();
	}
}
