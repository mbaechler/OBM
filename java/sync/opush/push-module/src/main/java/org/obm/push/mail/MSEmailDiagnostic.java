package org.obm.push.mail;

import java.io.InputStream;

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
		
		public MSEmailDiagnostic buildDiagnosticMSEmail(long uid, Envelope originalEnvelope, String serverDomain, String diagnosticMessage) {
			if (isValidEnvelopeForDiagnostic(originalEnvelope)) {
				return new DetailedMSEmailDiagnostic(uid, originalEnvelope, serverDomain, diagnosticMessage, messages);
			} else {
				return new SimpleMSEmailDiagnostic(uid, serverDomain, diagnosticMessage, messages);
			}
		}
	
		private boolean isValidEnvelopeForDiagnostic(Envelope originalEnvelope) {
			return originalEnvelope != null
					&& !Strings.isNullOrEmpty(originalEnvelope.getSubject())
					&& originalEnvelope.getFrom() != null
					&& !Strings.isNullOrEmpty(originalEnvelope.getFrom().getMail());
		}
	}
	
	private MSEmailDiagnostic(long uid, String subject, String from, String bodyContent) {
		super();
		setUid(uid);
		setSubject(subject);
		setBody(new MSEmailBody(MSEmailBodyType.PlainText, bodyContent));
		setFrom(new MSAddress(from, from));
	}

	public static class SimpleMSEmailDiagnostic extends MSEmailDiagnostic { 
	
		private SimpleMSEmailDiagnostic(long uid, String noReplyMailDomain, String diagnosticMessage, Messages messages) {
			super(uid,
					messages.mailNotSynchronizableSubject(),
					messages.mailNotSynchronizableFrom(noReplyMailDomain),
					messages.mailNotSynchronizableBody(diagnosticMessage));
		}
	}

	public static class DetailedMSEmailDiagnostic extends MSEmailDiagnostic { 
	
		private DetailedMSEmailDiagnostic(long uid, Envelope originalEnvelope, String noReplyMailDomain,
				String diagnosticMessage, Messages messages) {
			super(uid,
					messages.mailNotSynchronizableWithHeadersSubject(originalEnvelope.getSubject()),
					messages.mailNotSynchronizableWithHeadersFrom(noReplyMailDomain),
					messages.mailNotSynchronizableWithHeadersBody(originalEnvelope.getFrom().getMail(),
							diagnosticMessage));
		}
	}
}
