package org.obm.push.monitor;

import java.util.Date;
import java.util.Set;

import org.obm.push.backend.ICollectionChangeListener;
import org.obm.push.backend.IContentsExporter;
import org.obm.push.bean.ChangedCollections;
import org.obm.push.exception.DaoException;
import org.obm.push.service.PushPublishAndSubscribe;
import org.obm.push.store.CollectionDao;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CalendarMonitoringThread extends MonitoringThread {

	@Singleton
	public static class Factory {
		private final CollectionDao collectionDao;
		private final IContentsExporter contentsExporter;
		private final org.obm.push.service.PushPublishAndSubscribe.Factory pubSubFactory;

		@Inject
		private Factory(CollectionDao collectionDao, IContentsExporter contentsExporter,
				PushPublishAndSubscribe.Factory pubSubFactory) {
			this.collectionDao = collectionDao;
			this.contentsExporter = contentsExporter;
			this.pubSubFactory = pubSubFactory;
		}

		public CalendarMonitoringThread createClient(long freqMs,
				Set<ICollectionChangeListener> ccls) {
			
			return new CalendarMonitoringThread(freqMs, ccls,
					this.collectionDao, this.contentsExporter, pubSubFactory);
		}
	}

	private CalendarMonitoringThread(long freqMs,
			Set<ICollectionChangeListener> ccls,
			CollectionDao collectionDao, IContentsExporter contentsExporter,
			PushPublishAndSubscribe.Factory pubSubFactory) {
		super(freqMs, ccls, collectionDao, contentsExporter, pubSubFactory);
	}

	@Override
	public ChangedCollections getChangedCollections(Date lastSync) throws ChangedCollectionsException, DaoException {
		try{
			return collectionDao.getCalendarChangedCollections(lastSync);
		} catch (DaoException e) {
			throw new ChangedCollectionsException(e);
		}
	}

}
