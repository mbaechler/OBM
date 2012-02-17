package org.obm.funambol.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NoPermissionException;

import org.obm.configuration.ContactConfiguration;
import org.obm.funambol.converter.ISyncItemKeyConverter;
import org.obm.funambol.converter.ObmContactConverter;
import org.obm.funambol.exception.OBMException;
import org.obm.funambol.model.ContactChanges;
import org.obm.funambol.model.SyncSession;
import org.obm.sync.auth.AccessToken;
import org.obm.sync.auth.ServerFault;
import org.obm.sync.book.AddressBook;
import org.obm.sync.book.Contact;
import org.obm.sync.book.ContactKey;
import org.obm.sync.client.book.BookClient;
import org.obm.sync.exception.ContactAlreadyExistException;
import org.obm.sync.exception.ContactNotFoundException;
import org.obm.sync.items.AddressBookChangesResponse;

import com.funambol.framework.engine.SyncItemKey;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class ContactServiceObmImpl extends ObmService implements IContactService{

	private final BookClient bookClient;
	private final ContactConfiguration contactConfiguration;
	private final ObmContactConverter contactConverter;
	private final ISyncItemKeyConverter syncItemKeyConverter;
	
	@Inject
	private ContactServiceObmImpl(final BookClient bookClient, 
			final ContactConfiguration contactConfiguration, final ObmContactConverter contactConverter, 
			final ISyncItemKeyConverter syncItemKeyConverter) {
		this.bookClient = bookClient;
		this.contactConfiguration = contactConfiguration;
		this.contactConverter = contactConverter;
		this.syncItemKeyConverter = syncItemKeyConverter;
	}

	@Override
	public List<SyncItemKey> getAllItemKeys(SyncSession syncSession) throws OBMException {
		final ContactChanges changes = getSync(syncSession, null);
		final Set<ContactKey> updatedSet = changes.getUpdated().keySet();
		return syncItemKeyConverter.getSyncItemKeysFromContactKeys(updatedSet);
	}

	@Override
	public List<SyncItemKey> getDeletedItemKeys(SyncSession syncSession, Timestamp since) throws OBMException {
		final ContactChanges changes = getSync(syncSession, since);
		final Set<ContactKey> updatedSet = changes.getDeleted();
		return syncItemKeyConverter.getSyncItemKeysFromContactKeys(updatedSet);
	}

	@Override
	public List<SyncItemKey> getUpdatedItemKeys(SyncSession syncSession, Timestamp since) throws OBMException {
		final ContactChanges changes = getSync(syncSession, since);
		final Set<ContactKey> updatedSet = changes.getUpdated().keySet();
		return syncItemKeyConverter.getSyncItemKeysFromContactKeys(updatedSet);
	}

	@Override
	public com.funambol.common.pim.contact.Contact getItemFromId(SyncSession syncSession, SyncItemKey syncItemKey) throws OBMException {
		try {
			final ContactKey key = syncItemKeyConverter.getContactKeyFromSyncItemKey(syncItemKey);
			 final Contact obmContact = bookClient.getContactFromId(syncSession.getObmAccessToken(), key.getAddressBookId(), key.getContactId());
			 return contactConverter.obmContactTofoundation(obmContact);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage(),e);
		} catch (IllegalArgumentException e) {
			throw new OBMException(e.getMessage(),e);
		} catch (ContactNotFoundException e) {
			throw new OBMException(e.getMessage(),e);
		}
	}

	@Override
	public void removeItem(SyncSession syncSession, SyncItemKey syncItemKey) throws OBMException {
		try {
			ContactKey contactKey = syncItemKeyConverter.getContactKeyFromSyncItemKey(syncItemKey);
			bookClient.removeContact(syncSession.getObmAccessToken(), contactKey.getAddressBookId(), contactKey.getContactId());
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage(),e);
		} catch (ContactNotFoundException e) {
			throw new OBMException(e.getMessage(),e);
		} catch (IllegalArgumentException e) {
			throw new OBMException(e.getMessage(),e);
		} catch (NoPermissionException e) {
			throw new OBMException(e.getMessage(),e);
		}
	}

	@Override
	public com.funambol.common.pim.contact.Contact updateItem(SyncSession syncSession, com.funambol.common.pim.contact.Contact contact)
			throws OBMException {
		try {
			Contact convertedContact = contactConverter.foundationContactToObm(contact);
			Contact updatedContact = bookClient.modifyContact(syncSession.getObmAccessToken(), convertedContact.getFolderId(), convertedContact);
			return contactConverter.obmContactTofoundation(updatedContact);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage(),e);
		} catch (NoPermissionException e) {
			throw new OBMException(e.getMessage(),e);
		} catch (ContactNotFoundException e) {
			throw new OBMException(e.getMessage(),e);
		}

		
	}

	@Override
	public com.funambol.common.pim.contact.Contact addItem(SyncSession syncSession,
			com.funambol.common.pim.contact.Contact contact)
			throws OBMException {
		try {
			final Contact funisContact = contactConverter.foundationContactToObm(contact);
			AddressBook defaultAddressBook = getDefaultAddressBook(syncSession.getObmAccessToken());
			Contact c = bookClient.createContact(syncSession.getObmAccessToken(), defaultAddressBook.getUid(), funisContact);
			return contactConverter.obmContactTofoundation(c);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		} catch (NoPermissionException e) {
			throw new OBMException(e.getMessage(),e);
		} catch (ContactAlreadyExistException e) {
			throw new OBMException(e.getMessage(),e);
		}
	}

	@Override
	public List<SyncItemKey> getContactTwinKeys(SyncSession syncSession,
			com.funambol.common.pim.contact.Contact contact) {

		Contact c = contactConverter.foundationContactToObm(contact);
		if (logger.isDebugEnabled()) {
			logger.debug(" look twin of : " + c.getFirstname() + ","
					+ c.getLastname() + "," + c.getCompany());
		}
		c.setUid(null);
		List<ContactKey> contactKeys = bookClient.getContactTwinKeys(syncSession.getObmAccessToken(), c);
		return syncItemKeyConverter.getSyncItemKeysFromContactKeys(contactKeys);
	}

	private AddressBook getDefaultAddressBook(AccessToken token) throws OBMException {
		try {
			List<AddressBook> addrs = bookClient.listAllBooks(token);
			AddressBook defaultAddressBook = null;
			for(AddressBook addr : addrs){
				if(contactConfiguration.getDefaultAddressBookName().equals(addr.getName())){
					defaultAddressBook = addr;
				}
			}
			if(defaultAddressBook == null){
				throw new OBMException("The default address book is unobtainable");
			}
			return defaultAddressBook;
		} catch (ServerFault e) {
			throw new OBMException("The default address book is unobtainable");
		}
		
	}

	private ContactChanges getSync(SyncSession session, Timestamp since) throws OBMException {
		try{
			Date lastSync = getLastSync(since);
			AddressBookChangesResponse sync = bookClient.getAddressBookSync(session.getObmAccessToken(), lastSync);
	
			List<Contact> updated = getListUpdatedContact(sync);
			Set<ContactKey> deleted = getRemovedContacts(sync);
			
			Map<ContactKey, Contact> updatedRest = transformAsFunambolUpdated(updated);
			Set<ContactKey> deletedRest = transformAsFunambolRemoved(deleted);
			
			return new ContactChanges(updatedRest, deletedRest);
		}catch (ServerFault e) {
			throw new OBMException("The default address book is unobtainable");
		}
	}
	
	private Set<ContactKey> transformAsFunambolRemoved(Set<ContactKey> deleteds) {
		ImmutableSet.Builder<ContactKey> mapBuilder = ImmutableSet.builder();
		for (ContactKey removedContact : deleteds) {
			ContactKey key = new ContactKey(removedContact.getAddressBookId(), removedContact.getContactId()); 
			mapBuilder.add(key);
		}
		return mapBuilder.build();
	}
	
	private Map<ContactKey, Contact> transformAsFunambolUpdated(List<Contact> updated) {
		Builder<ContactKey, Contact> mapBuilder = ImmutableMap.builder();
		for (Contact c : updated) {
			ContactKey key = new ContactKey(c.getFolderId(), c.getUid()); 
			mapBuilder.put(key, c);
		}
		return mapBuilder.build();
	}


	private Set<ContactKey> getRemovedContacts(AddressBookChangesResponse sync) {
		return sync.getRemovedContacts() != null ? sync.getRemovedContacts() : ImmutableSet.<ContactKey>of();
	}

	private List<Contact> getListUpdatedContact(AddressBookChangesResponse sync) {
		return sync.getUpdatedContacts() != null ? sync.getUpdatedContacts() : ImmutableList.<Contact>of();
	}

	private Date getLastSync(Timestamp since) {
		return since != null ? new Date(since.getTime()) : null;
	}

}
