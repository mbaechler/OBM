package fr.aliasource.funambol.engine.source;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.obm.configuration.ContactConfiguration;
import org.obm.sync.client.book.BookClient;
import org.obm.sync.client.login.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.funambol.common.pim.contact.Contact;
import com.funambol.common.pim.converter.ContactToVcard;
import com.funambol.common.pim.converter.ConverterException;
import com.funambol.common.pim.vcard.VcardParser;
import com.funambol.framework.engine.InMemorySyncItem;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.google.inject.Injector;

import fr.aliasource.funambol.OBMException;
import fr.aliasource.funambol.ObmFunambolGuiceInjector;
import fr.aliasource.obm.items.converter.ObmContactConverter;
import fr.aliasource.obm.items.manager.ContactSyncBean;

public final class ContactSyncSource extends ObmSyncSource implements
		SyncSource, Serializable, LazyInitBean {

	private static final Logger logger = LoggerFactory.getLogger(ContactSyncSource.class);
	private final BookClient bookClient;
	private final LoginService loginService;
	private final ObmContactConverter contactConverter;
	private final ContactConfiguration contactConfiguration;
	
	private ContactSyncBean currentSyncBean;

	public ContactSyncSource() {
		super();
		Injector injector = ObmFunambolGuiceInjector.getInjector();
		this.bookClient = injector.getProvider(BookClient.class).get();
		this.loginService = injector.getProvider(LoginService.class).get();
		this.contactConverter = injector.getProvider(ObmContactConverter.class).get();
		this.contactConfiguration = injector.getProvider(ContactConfiguration.class).get();
	}

	public void beginSync(SyncContext context) throws SyncSourceException {
		super.beginSync(context);

		logger.info("- Begin an OBM Contact sync -");
		this.currentSyncBean = new ContactSyncBean(loginService, bookClient, contactConfiguration, contactConverter);
		
		try {
			currentSyncBean.logIn(context.getPrincipal().getUser().getUsername(),
					context.getPrincipal().getUser().getPassword());

		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
		currentSyncBean.setDeviceTimeZone(deviceTimezone);
	}

	public void setOperationStatus(String operation, int statusCode,
			SyncItemKey[] keys) {
		super.setOperationStatus(operation, statusCode, keys);
	}

	/**
	 * @see SyncSource
	 */
	public SyncItem addSyncItem(SyncItem syncItem) throws SyncSourceException {

		logger.info("addSyncItem(" + principal + " , "
				+ syncItem.getKey().getKeyAsString() + ")");
		Contact contact = null;
		Contact created = null;
		try {
			contact = getFoundationFromSyncItem(syncItem);
			contact.setUid(null);
			created = currentSyncBean.addItem(contact);
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}

		logger.info(" created with id : " + created.getUid());

		return getSyncItemFromFoundation(created, SyncItemState.SYNCHRONIZED);
	}

	/*
	 * @see SyncSource
	 */
	public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException {
		logger.info("getAllSyncItemKeys(" + principal + ")");
		List<String> keys = null;
		try {
			keys = currentSyncBean.getAllItemKeys();
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
		SyncItemKey[] ret = getSyncItemKeysFromKeys(keys);
		logger.info(" returning " + ret.length + " key(s)");

		return ret;
	}

	/*
	 * @see SyncSource
	 */
	public SyncItemKey[] getDeletedSyncItemKeys(Timestamp since, Timestamp until)
			throws SyncSourceException {
		logger.info("getDeletedSyncItemKeys(" + principal + " , " + since
				+ " , " + until + ")");
		List<String> keys = null;
		try {
			keys = currentSyncBean.getDeletedItemKeys(since);
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
		SyncItemKey[] ret = getSyncItemKeysFromKeys(keys);
		logger.info(" returning " + ret.length + " key(s)");

		return ret;
	}

	/*
	 * @see SyncSource
	 */
	public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp until)
			throws SyncSourceException {
		return null;
	}

	/**
	 * @throws
	 * @see SyncSource
	 */
	public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
			throws SyncSourceException {
		logger.info("getSyncItemKeysFromTwin(" + principal + ")");

		Contact contact = null;

		List<String> keys = null;
		try {
			syncItem.getKey().setKeyValue("");
			contact = getFoundationFromSyncItem(syncItem);
			keys = currentSyncBean.getContactTwinKeys(contact);
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
		SyncItemKey[] ret = getSyncItemKeysFromKeys(keys);

		logger.info(" returning " + ret.length + " key(s)");

		return ret;
	}

	/*
	 * @see SyncSource
	 */
	public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp since, Timestamp until)
			throws SyncSourceException {
		logger.info("getUpdatedSyncItemKeys(" + principal + " , " + since
				+ " , " + until + ")");

		List<String> keys = null;
		try {
			keys = currentSyncBean.getUpdatedItemKeys(since);
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
		SyncItemKey[] ret = getSyncItemKeysFromKeys(keys);
		logger.info(" returning " + ret.length + " key(s)");

		return ret;
	}

	public void removeSyncItem(SyncItemKey syncItemKey, Timestamp time,
			boolean softDelete) throws SyncSourceException {
		logger.info("removeSyncItem(" + principal + " , " + syncItemKey + " , "
				+ time + ")");
		String k = syncItemKey.getKeyAsString();
		if (k == null || k.length() == 0 || "null".equalsIgnoreCase(k)) {
			logger.warn("cannot remove null sync item key, skipping.");
			return;
		}
		try {
			currentSyncBean.removeItem(syncItemKey.getKeyAsString());
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
	}

	/*
	 * @see SyncSource
	 */
	public SyncItem updateSyncItem(SyncItem syncItem)
			throws SyncSourceException {
		logger.info("updateSyncItem(" + principal + " , "
				+ syncItem.getKey().getKeyAsString() + "("+syncItem.getKey()+"))");
		Contact contact = null;
		try {
			contact = getFoundationFromSyncItem(syncItem);
			contact = currentSyncBean.updateItem(contact);
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}

		return getSyncItemFromFoundation(contact, SyncItemState.SYNCHRONIZED);
	}

	/*
	 * @see SyncSource
	 */
	public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
			throws SyncSourceException {
		logger.info("syncItemFromId(" + principal + ", " + syncItemKey + ")");

		try {
			com.funambol.common.pim.contact.Contact contact = null;
			String key = syncItemKey.getKeyAsString();
			contact = currentSyncBean.getItemFromId(key);
			SyncItem ret = getSyncItemFromFoundation(contact,
					SyncItemState.UNKNOWN);
			return ret;
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
	}

	// -------------------- Private methods ----------------------

	private SyncItem getSyncItemFromFoundation(Contact contact, char state) {

		SyncItem syncItem = null;
		String content = null;

		if (MSG_TYPE_VCARD.equals(getSourceType())) {
			content = getVCardFromFoundationContact(contact);
			logger.info("vcardFromFoundation:\n"+content);
			syncItem = new InMemorySyncItem(this, contact.getUid(), state);

			if (this.isEncode()) {
				syncItem.setContent(com.funambol.framework.tools.Base64
						.encode(content.getBytes()));
				syncItem.setType(getSourceType());
				syncItem.setFormat("b64");
			} else {
				syncItem.setContent(content.getBytes());
				syncItem.setType(getSourceType());
			}
		} else {
			logger.error("Only vcard type is supported");
		}
		return syncItem;
	}

	private String getVCardFromFoundationContact(Contact contact) {
		String vcard = null;

		try {
			ContactToVcard c2vcard = new ContactToVcard(deviceTimezone,
					deviceCharset);
			vcard = c2vcard.convert(contact);
		} catch (ConverterException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return vcard;
	}

	private Contact getFoundationFromSyncItem(SyncItem item)
			throws OBMException {

		Contact contact = null;
		String content = null;

		content = getContentOfSyncItem(item);

		if (MSG_TYPE_VCARD.equals(getSourceType())) {
			contact = getFoundationContactFromVCard(content);
			contact.setUid(item.getKey().getKeyAsString());
		} else {
			logger.error("Only vcard type is supported");
		}

		return contact;
	}

	private Contact getFoundationContactFromVCard(String content)
			throws OBMException {

		ByteArrayInputStream buffer = null;
		VcardParser parser = null;
		Contact contact = null;

		// content = SourceUtils.handleLineDelimiting(content);
		logger.info("foundFromCard:\n" + content + "\n");

		try {
			contact = new Contact();
			buffer = new ByteArrayInputStream(content.getBytes());
			if ((content.getBytes()).length > 0) {
				parser = new VcardParser(buffer, deviceTimezoneDescr,
						deviceCharset);
				contact = parser.vCard();
			}
		} catch (Throwable e) {
			logger.error("Error converting following vcard:\n " + content
					+ "\n");
			throw new OBMException(
					"Error converting from Vcard (card dump follows):\n"
							+ content + "\n", e);
		}

		return contact;
	}
	@Override
	public void endSync() throws SyncSourceException {
		currentSyncBean.logout();
		super.endSync();
	}

}
