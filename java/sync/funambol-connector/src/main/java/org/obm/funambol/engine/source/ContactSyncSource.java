package org.obm.funambol.engine.source;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.obm.funambol.ObmFunambolGuiceInjector;
import org.obm.funambol.exception.ConvertionException;
import org.obm.funambol.exception.OBMException;
import org.obm.funambol.service.ContactServiceObmImpl;
import org.obm.funambol.service.IContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.funambol.common.pim.contact.Contact;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.google.inject.Injector;


public final class ContactSyncSource extends ObmSyncSource implements
		SyncSource, Serializable, LazyInitBean {

	private static final Logger logger = LoggerFactory.getLogger(ContactSyncSource.class);
	
	private final IContactService contactService;

	public ContactSyncSource() {
		super();
		Injector injector = ObmFunambolGuiceInjector.getInjector();
		this.contactService = injector.getInstance(ContactServiceObmImpl.class);
	}

	@Override
	public void beginSync(SyncContext context) throws SyncSourceException {
		logger.info("- Begin an OBM Contact sync -");
		super.beginSync(context);
		logger.info("beginSync end.");
	}

	@Override
	public void setOperationStatus(String operation, int statusCode,
			SyncItemKey[] keys) {
		super.setOperationStatus(operation, statusCode, keys);
	}

	@Override
	public SyncItem addSyncItem(SyncItem syncItem) throws SyncSourceException {
		logger.info("addSyncItem(" + syncSession.getUserLogin() + " , "
				+ syncItem.getKey().getKeyAsString() + ")");
		try {
			Contact contact = syncItemConverter.getFunambolContactFromSyncItem(syncSession, syncItem, getSourceType());
			contact.setUid(null);
			Contact created = contactService.addItem(syncSession, contact);
			
			logger.info(" created with id : " + created.getUid());
			return syncItemConverter.getSyncItemFromFunambolContact(syncSession, this, contact, SyncItemState.SYNCHRONIZED, getSourceType());
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		} catch (ConvertionException e) {
			throw new SyncSourceException(e);
		}
	}

	@Override
	public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException {
		logger.info("getAllSyncItemKeys(" + syncSession.getUserLogin() + ")");
		try {
			List<SyncItemKey> keys = contactService.getAllItemKeys(syncSession);
			logger.info(" returning " + keys.size() + " key(s)");
			return tranformAsSyncItemKeyArray(keys);
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
		
	}

	@Override
	public SyncItemKey[] getDeletedSyncItemKeys(Timestamp since, Timestamp until)
			throws SyncSourceException {
		logger.info("getDeletedSyncItemKeys(" + syncSession.getUserLogin() + " , " + since
				+ " , " + until + ")");
		try {
			List<SyncItemKey> keys = contactService.getDeletedItemKeys(syncSession, since);
			logger.info(" returning " + keys.size() + " key(s)");
			return tranformAsSyncItemKeyArray(keys);
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
	}

	@Override
	public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp until)
			throws SyncSourceException {
		return null;
	}

	@Override
	public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
			throws SyncSourceException {
		try {
			logger.info("getSyncItemKeysFromTwin(" + syncSession.getUserLogin() + ")");
			//FIXME USEFULL ??
			syncItem.getKey().setKeyValue("");
			
			Contact contact = syncItemConverter.getFunambolContactFromSyncItem(syncSession, syncItem, getSourceType());
			List<SyncItemKey> keys = contactService.getContactTwinKeys(syncSession, contact);
			logger.info(" returning " + keys.size() + " key(s)");
			return tranformAsSyncItemKeyArray(keys);
		} catch (ConvertionException e) {
			throw new SyncSourceException(e.getMessage(), e);
		} catch (OBMException e) {
			throw new SyncSourceException(e.getMessage(), e);
		}
		
	}

	@Override
	public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp since, Timestamp until)
			throws SyncSourceException {
		logger.info("getUpdatedSyncItemKeys(" + syncSession.getUserLogin() + " , " + since
				+ " , " + until + ")");
		try {
			List<SyncItemKey> keys = contactService.getUpdatedItemKeys(syncSession, since);
			logger.info(" returning " + keys.size() + " key(s)");
			return tranformAsSyncItemKeyArray(keys);
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
	}

	@Override
	public void removeSyncItem(SyncItemKey syncItemKey, Timestamp time,
			boolean softDelete) throws SyncSourceException {
		try {
			logger.info("removeSyncItem(" + syncSession.getUserLogin() + " , " + syncItemKey + " , "
					+ time + ")");
			contactService.removeItem(syncSession, syncItemKey);
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
	}

	@Override
	public SyncItem updateSyncItem(SyncItem syncItem)
			throws SyncSourceException {
		logger.info("updateSyncItem(" + syncSession.getUserLogin() + " , "
				+ syncItem.getKey().getKeyAsString() + "("+syncItem.getKey()+"))");
		try {
			final Contact funisContact = syncItemConverter.getFunambolContactFromSyncItem(syncSession, syncItem, getSourceType());
			final Contact updatedContact = contactService.updateItem(syncSession, funisContact);
			return syncItemConverter.getSyncItemFromFunambolContact(syncSession, this, updatedContact, SyncItemState.SYNCHRONIZED, getSourceType());
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		} catch (ConvertionException e) {
			throw new SyncSourceException(e);
		}
	}

	@Override
	public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
			throws SyncSourceException {
		logger.info("syncItemFromId(" + syncSession.getUserLogin() + ", " + syncItemKey + ")");

		try {
			final com.funambol.common.pim.contact.Contact  contact = contactService.getItemFromId(syncSession, syncItemKey);
			SyncItem ret = syncItemConverter.getSyncItemFromFunambolContact(syncSession, this, contact, SyncItemState.UNKNOWN, getSourceType());
			return ret;
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		} catch (ConvertionException e) {
			throw new SyncSourceException(e);
		}
	}

	@Override
	public void endSync() throws SyncSourceException {
		super.endSync();
	}

}
