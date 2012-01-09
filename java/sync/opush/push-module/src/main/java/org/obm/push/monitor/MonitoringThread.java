package org.obm.push.monitor;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.obm.push.backend.ICollectionChangeListener;
import org.obm.push.backend.IContentsExporter;
import org.obm.push.bean.ChangedCollections;
import org.obm.push.exception.DaoException;
import org.obm.push.service.PushNotification;
import org.obm.push.service.PushPublishAndSubscribe;
import org.obm.push.store.CollectionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

public abstract class MonitoringThread implements Runnable {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final CollectionDao collectionDao;
	private final Set<ICollectionChangeListener> ccls;
	private final long freqMillisec;
	private final PushPublishAndSubscribe pushPublishAndSubscribe;
	private boolean stopped;
	
	protected abstract ChangedCollections getChangedCollections(Date lastSync) throws ChangedCollectionsException, DaoException;
	
	protected MonitoringThread(long freqMillisec,
			Set<ICollectionChangeListener> ccls,
			CollectionDao collectionDao, IContentsExporter contentsExporter,
			PushPublishAndSubscribe.Factory pubSubFactory) {
		super();
		
		this.pushPublishAndSubscribe = pubSubFactory.create(contentsExporter);
		this.freqMillisec = freqMillisec;
		this.stopped = false;
		this.ccls = ccls;
		this.collectionDao = collectionDao;
	}
	
	@Override
	public void run() {
		try {
			Date lastSync = getBaseLastSync();
			logger.info("Starting monitoring thread with reference date {}", lastSync);
			while (!stopped) {
				try {
					try {
						Thread.sleep(freqMillisec);
					} catch (InterruptedException e) {
						stopped = true;
						continue;
					}
						
					synchronized (ccls) {
						if (ccls.isEmpty()) {
							continue;
						}
						
						ChangedCollections changedCollections = getChangedCollections(lastSync);
						
						if (changedCollections.hasChanges()) {
							logger.info("changes detected : {}", changedCollections.toString());
						}
						
						List<PushNotification> toNotify = pushPublishAndSubscribe.listPushNotification(selectListenersToNotify(changedCollections, ccls));
						notifyListeners(toNotify);
						
						lastSync = changedCollections.getLastSync();
					}

				} catch (ChangedCollectionsException e1) {
					logger.error(e1.getMessage(), e1);
				} catch (DaoException e) {
					logger.error(e.getMessage(), e);
				}
			}
		} catch (ChangedCollectionsException e1) {
			logger.error(e1.getMessage(), e1);
		} catch (DaoException e) {
			logger.error(e.getMessage(), e);
		}	
	}

	private void notifyListeners(List<PushNotification> toNotify) {
		for (PushNotification listener: toNotify) {
			listener.emit();
		}
	}

	private Set<ICollectionChangeListener> selectListenersToNotify(ChangedCollections changedCollections,
			Set<ICollectionChangeListener> ccls) {
		
		if (changedCollections.getChanges().isEmpty()) {
			return ImmutableSet.<ICollectionChangeListener>of();
		}
		
		HashSet<ICollectionChangeListener> listeners = new HashSet<ICollectionChangeListener>();
		for (ICollectionChangeListener listener: ccls) {
			if (listener.monitorOneOf(changedCollections)) {
				listeners.add(listener);
			}
		}
		
		return listeners;
		
	}

	private Date getBaseLastSync() throws ChangedCollectionsException, DaoException {
		ChangedCollections collections = getChangedCollections(new Date(0));
		return collections.getLastSync();
	}
	
	protected class ChangedCollectionsException extends Exception {
		public ChangedCollectionsException(Throwable cause) {
			super(cause);
		}
	}
	
}
