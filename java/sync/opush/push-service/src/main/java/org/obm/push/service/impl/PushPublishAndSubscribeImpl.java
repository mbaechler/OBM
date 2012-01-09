package org.obm.push.service.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import org.obm.push.backend.ICollectionChangeListener;
import org.obm.push.backend.IContentsExporter;
import org.obm.push.backend.MonitoringService;
import org.obm.push.bean.BackendSession;
import org.obm.push.bean.SyncCollection;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.UnknownObmSyncServerException;
import org.obm.push.exception.activesync.CollectionNotFoundException;
import org.obm.push.exception.activesync.ProcessingEmailException;
import org.obm.push.impl.PushNotificationImpl;
import org.obm.push.service.PushNotification;
import org.obm.push.service.PushPublishAndSubscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

public class PushPublishAndSubscribeImpl implements PushPublishAndSubscribe {

	@Singleton
	public static class Factory implements PushPublishAndSubscribe.Factory {
		
		@Inject
		private Factory() {
		}
		
		@Override
		public PushPublishAndSubscribe create(IContentsExporter contentsExporter) {
			return new PushPublishAndSubscribeImpl(contentsExporter);
		}
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final IContentsExporter contentsExporter;
	private MonitoringService monitoringService;

	private PushPublishAndSubscribeImpl(IContentsExporter contentsExporter) {
		this.contentsExporter = contentsExporter;
	}

	
	@Override
	public void emit(final Set<ICollectionChangeListener> ccls) {
		final LinkedList<PushNotification> pushNotifyList = listPushNotification(ccls);
		for (PushNotification pushNotify: pushNotifyList) {
			pushNotify.emit();
		}
	}
	
	@Override
	public LinkedList<PushNotification> listPushNotification(
			Set<ICollectionChangeListener> ccls) {
		
		final LinkedList<PushNotification> pushNotifyList = new LinkedList<PushNotification>();
		for (final ICollectionChangeListener ccl : ccls) {

			final Collection<SyncCollection> monitoredCollections = ccl
					.getMonitoredCollections();
			
			final BackendSession backendSession = ccl.getSession();
			for (SyncCollection syncCollection : monitoredCollections) {
			
				try {
					int count = contentsExporter.getItemEstimateSize(backendSession,
							syncCollection.getOptions().getFilterType(),
							syncCollection.getCollectionId(),
							syncCollection.getSyncState());
					
					if (count > 0) {
						addPushNotification(pushNotifyList, ccl);
					}
				} catch (CollectionNotFoundException e) {
					logger.error(e.getMessage(), e);
				} catch (DaoException e) {
					logger.error(e.getMessage(), e);
				} catch (UnknownObmSyncServerException e) {
					logger.error(e.getMessage(), e);
				} catch (ProcessingEmailException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		return pushNotifyList;
	}

	private void addPushNotification(
			final LinkedList<PushNotification> pushNotifyList,
			final ICollectionChangeListener ccl) {
		if (monitoringService != null) {
			monitoringService.stopMonitoring();
		}
		pushNotifyList.add(new PushNotificationImpl(ccl));
	}

	public void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}
	
}
