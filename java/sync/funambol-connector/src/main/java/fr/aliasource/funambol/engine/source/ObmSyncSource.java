package fr.aliasource.funambol.engine.source;

import java.io.Serializable;
import java.util.List;

import org.obm.sync.auth.AccessToken;
import org.obm.sync.auth.AuthFault;
import org.obm.sync.client.login.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.AbstractSyncSource;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.engine.source.SyncSourceInfo;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.funambol.server.config.Configuration;
import com.google.inject.Injector;

import fr.aliasource.funambol.ObmFunambolGuiceInjector;
import fr.aliasource.obm.items.converter.ISyncItemConverter;
import fr.aliasource.obm.items.manager.SyncSession;

/**
 */
public abstract class ObmSyncSource extends AbstractSyncSource implements
		SyncSource, Serializable, LazyInitBean {

	protected final LoginService loginService;
	protected final ISyncItemConverter syncItemConverter;
	protected SyncSession syncSession;

	private static final Logger logger = LoggerFactory.getLogger(ObmSyncSource.class);

	public ObmSyncSource() {
		Injector injector = ObmFunambolGuiceInjector.getInjector();
		this.loginService = injector.getProvider(LoginService.class).get();
		syncItemConverter = injector.getProvider(ISyncItemConverter.class).get();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(" - {name: ").append(getName());
		sb.append(" type: ").append(getSourceType());
		sb.append(" uri: ").append(getSourceURI());
		sb.append("}");
		return sb.toString();
	}

	@Override
	public void beginSync(SyncContext context) throws SyncSourceException {
		super.beginSync(context);

		try {
			Sync4jDevice device = getDevice(context);
			this.syncSession = new SyncSession(context, device);
			AccessToken token = loginService.login(this.syncSession.getUserLogin(), this.syncSession.getUserPassword());
			this.syncSession.setObmAccessToken(token);	
		} catch (PersistentStoreException e) {
			throw new SyncSourceException("obm : error getting device", e);
		} catch (AuthFault e) {
			throw new SyncSourceException("Error during the login in obm-sync", e);
		}
	}
	
	@Override
	public void endSync() throws SyncSourceException {
		loginService.logout(syncSession.getObmAccessToken());
		this.syncSession = null;
		super.endSync();
	}

	@Override
	public void setOperationStatus(String operation, int statusCode,
			SyncItemKey[] keys) {

		StringBuffer message = new StringBuffer("Received status code '");
		message.append(statusCode).append("' for a '").append(operation)
				.append("'").append(" for this items: ");

		for (int i = 0; i < keys.length; i++) {
			message.append("\n  - " + keys[i].getKeyAsString());
		}

		logger.info(message.toString());
	}

	@Override
	public void commitSync() throws SyncSourceException {
		super.commitSync();
		logger.info("commit sync");
	}

	@Override
	public SyncSourceInfo getInfo() {
		SyncSourceInfo info = super.getInfo();
		return info;
	}

	@Override
	public String getName() {
		String name = super.getName();
		logger.info("getName: " + name);
		return name;
	}

	@Override
	public String getSourceQuery() {
		String ret = super.getSourceQuery();
		logger.info("getsourcequery: " + ret);
		return ret;
	}

	@Override
	public String getSourceURI() {
		String ret = super.getSourceURI();
		logger.info("getsourceuri: " + ret);
		return ret;
	}
	@Override
	public void init() throws BeanInitializationException {
	}
	
	
	public SyncItemKey[] getSyncItemKeysFromKeys(List<String> keys) {
		int nb = 0;
		SyncItemKey[] syncKeys = null;
		if (keys != null) {
			nb = keys.size();
			syncKeys = new SyncItemKey[nb];
			for (int i = 0; i < nb; i++) {
				syncKeys[i] = new SyncItemKey(keys.get(i));
			}
		}

		return syncKeys;
	}
	
	public String getSourceType() {
		if (getInfo() != null && getInfo().getPreferredType() != null) {
			return getInfo().getSupportedTypes()[0].getType();
		} else {
			return "";
		}
	}

	private Sync4jDevice getDevice(SyncContext context)
			throws PersistentStoreException {
		String deviceId = context.getPrincipal().getDeviceId();
		Sync4jDevice device = new Sync4jDevice(deviceId);
		PersistentStore store = Configuration.getConfiguration().getStore();
		store.read(device);
		return device;
	}

	public SyncSession getSyncSession() {
		return syncSession;
	}
	
}
