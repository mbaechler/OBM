/* ***** BEGIN LICENSE BLOCK *****
 *
 * %%
 * Copyright (C) 2000 - 2011 Linagora
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK ***** */
package org.obm.push.monitor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

import org.minig.imap.IMAPException;
import org.minig.imap.IdleClient;
import org.minig.imap.idle.IIdleCallback;
import org.minig.imap.idle.IdleLine;
import org.minig.imap.idle.IdleTag;
import org.obm.push.backend.ICollectionChangeListener;
import org.obm.push.backend.IContentsExporter;
import org.obm.push.bean.BackendSession;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.activesync.CollectionNotFoundException;
import org.obm.push.impl.ObmSyncBackend;
import org.obm.push.impl.PushNotification;
import org.obm.push.mail.IEmailManager;

public class EmailMonitoringThread extends OpushMonitoringThread implements IIdleCallback {
	
	/**
	 * SynchronizedSet, all accesses should be synchronized
	 */
	private Set<ICollectionChangeListener> ccls;
	protected ObmSyncBackend backend;
	protected IEmailManager emailManager;
	
	private BackendSession bs;
	private String collectionName;
	private Boolean remainConnected;  

	private IdleClient store;

	public EmailMonitoringThread(ObmSyncBackend cb,
			Set<ICollectionChangeListener> ccls, BackendSession bs,
			Integer collectionId, IEmailManager emailManager, 
			IContentsExporter contentsExporter) throws CollectionNotFoundException, DaoException {
		
		super(contentsExporter);
		
		remainConnected = false;
		this.ccls = Collections.synchronizedSet(ccls);
		this.backend = cb;
		this.emailManager = emailManager;
		this.bs = bs;
		collectionName = backend.getCollectionPathFor(collectionId);
	}

	public synchronized void startIdle() throws IMAPException {
		if (store == null) {
			store = getIdleClient(bs);
			store.login(emailManager.getActivateTLS());
			store.select(emailManager.parseMailBoxName(bs,
					collectionName));
			store.startIdle(this);
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
	public synchronized void receive(IdleLine line) {
		if (line != null) {
		
			if ((IdleTag.EXISTS.equals(line.getTag()) || IdleTag.FETCH
					.equals(line.getTag()))) {
				
				stopIdle();
				emit(ccls);
			}
		}
	}

	@Override
	protected void addPushNotification(
			final LinkedList<PushNotification> pushNotifyList,
			final ICollectionChangeListener ccl) {

		this.stopIdle();
		pushNotifyList.add(new PushNotification(ccl));
	}
	
	private IdleClient getIdleClient(BackendSession bs) {
		String login = bs.getLoginAtDomain();
		boolean useDomain = emailManager.getLoginWithDomain();
		if (!useDomain) {
			int at = login.indexOf("@");
			if (at > 0) {
				login = login.substring(0, at);
			}
		}
		logger.debug("Creating idleClient with login: {}, (useDomain {})", login, useDomain);
		IdleClient idleCli = new IdleClient(emailManager.locateImap(bs), 143, login, bs
				.getPassword());
		return idleCli;
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
		if(remainConnected){
			try {
				startIdle();
			} catch (IMAPException e) {
				logger.error("SEND ERROR TO PDA",e );
				//TODO SEND ERROR TO PDA
			}	
		}
	}
	
}
