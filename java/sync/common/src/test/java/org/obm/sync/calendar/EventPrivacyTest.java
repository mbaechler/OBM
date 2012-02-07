package org.obm.sync.calendar;

import static org.fest.assertions.Assertions.assertThat;
import org.junit.Test;

public class EventPrivacyTest {

	@Test
	public void testPublicFromSqlInt() {
		EventPrivacy result = EventPrivacy.fromSqlIntCode(0);
		assertThat(result).isEqualTo(EventPrivacy.PUBLIC);
	}

	@Test
	public void testPrivateFromSqlInt() {
		EventPrivacy result = EventPrivacy.fromSqlIntCode(1);
		assertThat(result).isEqualTo(EventPrivacy.PRIVATE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNegativeFromSqlInt() {
		EventPrivacy.fromSqlIntCode(-1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testTwoBigFromSqlInt() {
		EventPrivacy.fromSqlIntCode(2);
	}

	@Test
	public void testPublicFromXmlInt() {
		EventPrivacy result = EventPrivacy.fromXmlIntCode(0);
		assertThat(result).isEqualTo(EventPrivacy.PUBLIC);
	}

	@Test
	public void testPrivateFromXmlInt() {
		EventPrivacy result = EventPrivacy.fromXmlIntCode(1);
		assertThat(result).isEqualTo(EventPrivacy.PRIVATE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNegativeFromXmlInt() {
		EventPrivacy.fromXmlIntCode(-1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testTwoBigFromXmlInt() {
		EventPrivacy.fromXmlIntCode(2);
	}
	
	@Test
	public void testPrivateToXmlInt() {
		int sqlIntCode = EventPrivacy.PRIVATE.toXmlIntCode();
		assertThat(sqlIntCode).isEqualTo(1);
	}
	
	@Test
	public void testPublicToXmlInt() {
		int sqlIntCode = EventPrivacy.PUBLIC.toXmlIntCode();
		assertThat(sqlIntCode).isEqualTo(0);
	}
	
	@Test
	public void testPrivateToSqlInt() {
		int sqlIntCode = EventPrivacy.PRIVATE.toSqlIntCode();
		assertThat(sqlIntCode).isEqualTo(1);
	}
	
	@Test
	public void testPublicToSqlInt() {
		int sqlIntCode = EventPrivacy.PUBLIC.toSqlIntCode();
		assertThat(sqlIntCode).isEqualTo(0);
	}
}
