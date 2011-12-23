package org.obm.push.mail;

import org.minig.imap.Envelope;
import org.obm.push.Messages;
import org.obm.push.bean.MSAddress;
import org.obm.push.bean.MSEmail;
import org.obm.push.bean.MSEmailBody;
import org.obm.push.bean.MSEmailBodyType;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

public class MSEmailDiagnostic extends MSEmail {

	@Singleton
	public static class Factory {

		private final Messages messages;
		
		@Inject
		private Factory(Messages messages) {
			this.messages = messages;
		}
		
		public MSEmailDiagnostic buildDiagnosticMSEmail(Envelope originalEnvelope, String serverDomain, String diagnosticMessage) {
			if (isValidEnvelopeForDiagnostic(originalEnvelope)) {
				return new DetailedMSEmailDiagnostic(originalEnvelope, serverDomain, diagnosticMessage, messages);
			} else {
				return new SimpleMSEmailDiagnostic(serverDomain, diagnosticMessage, messages);
			}
		}
	
		private boolean isValidEnvelopeForDiagnostic(Envelope originalEnvelope) {
			boolean isValidEnvelope = true;
			isValidEnvelope &= !Strings.isNullOrEmpty(originalEnvelope.getSubject());
			isValidEnvelope &= originalEnvelope.getFrom() != null;
			isValidEnvelope &= !Strings.isNullOrEmpty(originalEnvelope.getFrom().getMail());
			return isValidEnvelope;
		}
	}
	
	private MSEmailDiagnostic(String subject, String from, String bodyContent) {
		super();
		setSubject(subject);
		setBody(new MSEmailBody(MSEmailBodyType.PlainText, bodyContent));
		setFrom(new MSAddress(from, from));
	}
	
	public static class SimpleMSEmailDiagnostic extends MSEmailDiagnostic { 
	
		private SimpleMSEmailDiagnostic(String noReplyMailDomain, String diagnosticMessage, Messages messages) {
			super(
					messages.mailNotSynchronizableSubject(),
					messages.mailNotSynchronizableFrom(noReplyMailDomain),
					messages.mailNotSynchronizableBody(diagnosticMessage));
		}
	}

	public static class DetailedMSEmailDiagnostic extends MSEmailDiagnostic { 
	
		private DetailedMSEmailDiagnostic(Envelope originalEnvelope, String noReplyMailDomain,
				String diagnosticMessage, Messages messages) {
			super(
					messages.mailNotSynchronizableWithHeadersSubject(originalEnvelope.getSubject()),
					messages.mailNotSynchronizableWithHeadersFrom(noReplyMailDomain),
					messages.mailNotSynchronizableWithHeadersBody(originalEnvelope.getFrom().getMail(),
							diagnosticMessage));
		}
	}
}
