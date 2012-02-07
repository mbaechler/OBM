package org.obm.push;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.obm.push.bean.AttendeeStatus;
import org.obm.sync.calendar.ParticipationState;

public class ObmEventToMsEventConverterParticipationStateTest {

	private ObmEventToMsEventConverter converter;

	@Before
	public void setUp() {
		converter = new ObmEventToMsEventConverter();
	}

	@Test
	public void testAcceptedParticipationState() {
		AttendeeStatus status = converter.status(ParticipationState.ACCEPTED);
		assertThat(status).isEqualTo(AttendeeStatus.ACCEPT);
	}

	@Test
	public void testCompletedParticipationState() {
		AttendeeStatus status = converter.status(ParticipationState.COMPLETED);
		assertThat(status).isEqualTo(AttendeeStatus.RESPONSE_UNKNOWN);
	}

	@Test
	public void testDeclinedParticipationState() {
		AttendeeStatus status = converter.status(ParticipationState.DECLINED);
		assertThat(status).isEqualTo(AttendeeStatus.DECLINE);
	}

	@Test
	public void testDelegatedParticipationState() {
		AttendeeStatus status = converter.status(ParticipationState.DELEGATED);
		assertThat(status).isEqualTo(AttendeeStatus.RESPONSE_UNKNOWN);
	}

	@Test
	public void testInProgressParticipationState() {
		AttendeeStatus status = converter.status(ParticipationState.INPROGRESS);
		assertThat(status).isEqualTo(AttendeeStatus.RESPONSE_UNKNOWN);
	}

	@Test
	public void testNeedActionParticipationState() {
		AttendeeStatus status = converter.status(ParticipationState.NEEDSACTION);
		assertThat(status).isEqualTo(AttendeeStatus.NOT_RESPONDED);
	}

	@Test
	public void testTentativeParticipationState() {
		AttendeeStatus status = converter.status(ParticipationState.TENTATIVE);
		assertThat(status).isEqualTo(AttendeeStatus.TENTATIVE);
	}

}
