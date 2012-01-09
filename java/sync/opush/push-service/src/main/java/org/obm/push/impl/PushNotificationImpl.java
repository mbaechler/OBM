package org.obm.push.impl;

import org.obm.push.backend.ICollectionChangeListener;
import org.obm.push.service.PushNotification;

public class PushNotificationImpl implements PushNotification {

	private ICollectionChangeListener listener;

	public PushNotificationImpl(ICollectionChangeListener listener) {
		super();
		this.listener = listener;
	}

	@Override
	public void emit() {
		listener.changesDetected();
	}

}
