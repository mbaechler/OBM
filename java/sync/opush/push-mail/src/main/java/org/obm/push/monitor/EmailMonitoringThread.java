package org.obm.push.monitor;

import org.minig.imap.IdleClient;
import org.minig.imap.idle.IIdleCallback;
import org.minig.imap.idle.IdleLine;
import org.minig.imap.idle.IdleTag;
import org.obm.push.backend.MonitoringService;
import org.obm.push.backend.PushMonitoringManager;
import org.obm.push.bean.BackendSession;
import org.obm.push.mail.MailboxService;
import org.obm.push.mail.ImapClientProvider;
import org.obm.push.mail.MailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EmailMonitoringThread implements MonitoringService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private class Callback implements IIdleCallback {

		@Override
		public synchronized void receive(IdleLine line) {
			if (line != null) {
			
				if ((IdleTag.EXISTS.equals(line.getTag()) || IdleTag.FETCH
						.equals(line.getTag()))) {
					
					stopIdle();
					pushMonitorManager.emit();
				}
			}
		}

		@Override
		public synchronized void disconnectedCallBack() {
			if(store != null){
				try{
					stopIdle();
				} catch (Throwable e) {
					logger.error(e.getMessage(),e );
				}
			}
			if (remainConnected){
				startIdle();
			}
		}
	}
	
	/**
	 * SynchronizedSet, all accesses should be synchronized
	 */
	protected MailboxService emailManager;
	private BackendSession bs;
	private String collectionName;
	private Boolean remainConnected;  
	private IdleClient store;
	private final ImapClientProvider imapClientProvider;
	private final PushMonitoringManager pushMonitorManager;
	private String mailBoxName;

	public EmailMonitoringThread(
			PushMonitoringManager pushMonitorManager,
			BackendSession bs,
			String collectionName, MailboxService emailManager, 
			ImapClientProvider imapClientProvider) throws MailException {
		
		this.pushMonitorManager = pushMonitorManager;
		this.collectionName = collectionName;
		this.imapClientProvider = imapClientProvider;
		this.remainConnected = false;
		this.emailManager = emailManager;
		this.bs = bs;
		mailBoxName = emailManager.parseMailBoxName(bs, collectionName);
	}

	public synchronized void startIdle() {
		if (store == null) {
			store = imapClientProvider.getImapIdleClient(bs);
			store.login(emailManager.getActivateTLS());
			try {
				store.select(mailBoxName);
				store.startIdle(new Callback());
			} catch (RuntimeException e) {
				logger.error("Error lauching idle", e);
				store.logout();
				throw e;
			}
		}
		remainConnected = true;
		logger.info("Start monitoring for collection : '{}'", collectionName);
	}
	
	public synchronized void stopIdle() {
		if (store != null) {
			store.stopIdle();
			store.logout();
			store = null;
		}
		remainConnected = false;
		logger.info("Stop monitoring for collection : '{}'", collectionName);
	}

	@Override
	public void stopMonitoring() {
		this.stopIdle();
	}

}
