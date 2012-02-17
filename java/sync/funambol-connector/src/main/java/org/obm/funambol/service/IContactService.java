package org.obm.funambol.service;

import java.sql.Timestamp;
import java.util.List;

import org.obm.funambol.exception.OBMException;
import org.obm.funambol.model.SyncSession;

import com.funambol.common.pim.contact.Contact;
import com.funambol.framework.engine.SyncItemKey;


public interface IContactService {

	Contact addItem(SyncSession syncSession, Contact contact) throws OBMException;

	List<SyncItemKey> getAllItemKeys(SyncSession syncSession) throws OBMException;

	List<SyncItemKey> getDeletedItemKeys(SyncSession syncSession, Timestamp since) throws OBMException;

	List<SyncItemKey> getContactTwinKeys(SyncSession syncSession, Contact contact) throws OBMException;

	List<SyncItemKey> getUpdatedItemKeys(SyncSession syncSession, Timestamp since) throws OBMException;

	void removeItem(SyncSession syncSession, SyncItemKey syncItemKey) throws OBMException;

	Contact updateItem(SyncSession syncSession, Contact funisContact) throws OBMException;

	Contact getItemFromId(SyncSession syncSession, SyncItemKey syncItemKey) throws OBMException;

}
