package org.obm.push.mail;

import org.minig.imap.StoreClient;
import org.obm.push.bean.BackendSession;
import org.obm.push.bean.MSEmail;
import org.obm.push.exception.UnfetchableMailException;
import org.obm.push.service.EventService;
import org.obm.sync.client.login.LoginService;
import org.obm.sync.services.ICalendar;

import com.google.common.base.Throwables;

public class MessageLoader {

	private final MSEmailDiagnostic.Factory diagnosticMSEmailFactory;
	private MailMessageLoader mailMessageLoader;

	public MessageLoader(MSEmailDiagnostic.Factory diagnosticMSEmailFactory, 
			StoreClient store, ICalendar calendarClient, 
			EventService eventService, LoginService login) {
		mailMessageLoader = new MailMessageLoader(store, calendarClient, eventService, login);
		this.diagnosticMSEmailFactory = diagnosticMSEmailFactory; 
	}
	
	public MSEmail fetch(final Integer collectionId, final long messageId, final BackendSession bs) {
		try {
			return mailMessageLoader.fetch(collectionId, messageId, bs);
		} catch (UnfetchableMailException e) {
			return makeErrorMailDiagnostic(e, bs);
		}
	}
	
	private MSEmail makeErrorMailDiagnostic(UnfetchableMailException e, BackendSession bs) {
		return diagnosticMSEmailFactory.buildDiagnosticMSEmail(e.getFetchingEnvelope(), 
				bs.getUser().getDomain(), Throwables.getStackTraceAsString(e));
	}
	
}
