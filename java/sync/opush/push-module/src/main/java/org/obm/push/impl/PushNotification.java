package org.obm.push.impl;

import org.obm.push.backend.ICollectionChangeListener;

public class PushNotification {

	private ICollectionChangeListener listener;

	public PushNotification(ICollectionChangeListener listener) {
		super();
		this.listener = listener;
	}

	public void emit() {
		listener.changesDetected();
	}

}
