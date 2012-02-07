package org.obm.push;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.obm.push.bean.AttendeeType;
import org.obm.sync.calendar.ParticipationRole;

public class ObmEventToMsEventConverterParticipationRoleTest {

	private ObmEventToMsEventConverter converter;

	@Before
	public void setUp() {
		converter = new ObmEventToMsEventConverter();
	}

	@Test(expected=NullPointerException.class)
	public void testNullParticipationRole() {
		converter.participationRole(null);
	}

	@Test
	public void testChairParticipationRole() {
		AttendeeType role = converter.participationRole(ParticipationRole.CHAIR);
		assertThat(role).isEqualTo(AttendeeType.REQUIRED);
	}

	@Test
	public void testNonParticipationRole() {
		AttendeeType role = converter.participationRole(ParticipationRole.NON);
		assertThat(role).isEqualTo(AttendeeType.OPTIONAL);
	}
	
	@Test
	public void testOptionalParticipationRole() {
		AttendeeType role = converter.participationRole(ParticipationRole.OPT);
		assertThat(role).isEqualTo(AttendeeType.OPTIONAL);
	}
	
	@Test
	public void testRequiredParticipationRole() {
		AttendeeType role = converter.participationRole(ParticipationRole.REQ);
		assertThat(role).isEqualTo(AttendeeType.REQUIRED);
	}

}
