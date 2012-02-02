package org.obm.push;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.field.address.ParseException;
import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.obm.opush.mail.StreamMailTestsUtils;
import org.obm.push.bean.BackendSession;
import org.obm.push.bean.Credentials;
import org.obm.push.bean.User;
import org.obm.push.utils.Mime4jUtils;

public class ErrorsManagerTest {

	@Test
	public void testPrepareMessage() throws ParseException, FileNotFoundException, IOException {
		User user = User.Factory.create().createUser("test@test", "test@domain", "displayName");
		BackendSession backendSession = new BackendSession(new Credentials(user, "password", null), null, null, null);
		
		Mime4jUtils mime4jUtils = new Mime4jUtils();
		
		ErrorsManager errorsManager = new ErrorsManager(null, null, mime4jUtils);
		Message message = errorsManager.prepareMessage(backendSession, "Subject", "Body", 
				StreamMailTestsUtils.newInputStreamFromString("It's mail content !"));
		mime4jUtils.toInputStream(message);

		Assertions.assertThat(message).isNotNull();
	}

}
