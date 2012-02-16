package fr.aliasource.obm.items.manager;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NoPermissionException;

import org.obm.configuration.ContactConfiguration;
import org.obm.sync.auth.AccessToken;
import org.obm.sync.auth.ServerFault;
import org.obm.sync.book.AddressBook;
import org.obm.sync.book.Contact;
import org.obm.sync.book.RemovedContact;
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

import fr.aliasource.funambol.InvalidFunambolKeyException;
import fr.aliasource.funambol.OBMException;
import fr.aliasource.obm.items.converter.ObmContactConverter;

@Singleton
public class ContactServiceObmImpl extends ObmManager implements IContactService{

	private final BookClient bookClient;
	private final ContactConfiguration contactConfiguration;
	private final ObmContactConverter contactConverter;

	@Inject
	private ContactServiceObmImpl(final BookClient bookClient, 
			final ContactConfiguration contactConfiguration, final ObmContactConverter contactConverter) {
		this.bookClient = bookClient;
		this.contactConfiguration = contactConfiguration;
		this.contactConverter = contactConverter;
	}

	@Override
	public List<String> getAllItemKeys(SyncSession syncSession) throws OBMException {
		final ContactChanges changes = getSync(syncSession, null);
		final Set<String> updatedSet = changes.getUpdated().keySet();
		return ImmutableList.<String>copyOf(updatedSet);
	}

	@Override
	public List<String> getDeletedItemKeys(SyncSession syncSession, Timestamp since) throws OBMException {
		final ContactChanges changes = getSync(syncSession, since);
		return changes.getDeleted();
	}

	@Override
	public List<String> getUpdatedItemKeys(SyncSession syncSession, Timestamp since) throws OBMException {
		final ContactChanges changes = getSync(syncSession, since);
		final Set<String> uids = changes.getUpdated().keySet();
		return ImmutableList.<String>copyOf(uids);
	}

	@Override
	public com.funambol.common.pim.contact.Contact getItemFromId(SyncSession syncSession, SyncItemKey syncItemKey) throws OBMException {
		try {
			final ContactKey key = getContactKeyFromSyncItemKey(syncItemKey);
			 final Contact obmContact = bookClient.getContactFromId(syncSession.getObmAccessToken(), key.getAddressBookId(), key.getContactId());
			 return contactConverter.obmContactTofoundation(obmContact);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage(),e);
		} catch (InvalidFunambolKeyException e) {
			throw new OBMException(e.getMessage(),e);
		} catch (ContactNotFoundException e) {
			throw new OBMException(e.getMessage(),e);
		}
	}

	@Override
	public void removeItem(SyncSession syncSession, SyncItemKey syncItemKey) throws OBMException {
		try {
			ContactKey contactKey = getContactKeyFromSyncItemKey(syncItemKey);
			bookClient.removeContact(syncSession.getObmAccessToken(), contactKey.getAddressBookId(), contactKey.getContactId());
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage(),e);
		} catch (ContactNotFoundException e) {
			throw new OBMException(e.getMessage(),e);
		} catch (InvalidFunambolKeyException e) {
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
	public List<String> getContactTwinKeys(SyncSession syncSession,
			com.funambol.common.pim.contact.Contact contact) {

		Contact c = contactConverter.foundationContactToObm(contact);

		if (logger.isDebugEnabled()) {
			logger.debug(" look twin of : " + c.getFirstname() + ","
					+ c.getLastname() + "," + c.getCompany());
		}
		c.setUid(null);
		return bookClient.getContactTwinKeys(syncSession.getObmAccessToken(), c).getKeys();
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
			Set<RemovedContact> deleted = getRemovedContacts(sync);
			
			Map<String, Contact> updatedRest = transformAsFunambolUpdated(updated);
			List<String> deletedRest = transformAsFunambolRemoved(deleted);
			
			return new ContactChanges(updatedRest, deletedRest);
		}catch (ServerFault e) {
			throw new OBMException("The default address book is unobtainable");
		}
	}
	
	private List<String> transformAsFunambolRemoved(Set<RemovedContact> deleteds) {
		ImmutableList.Builder<String> mapBuilder = ImmutableList.builder();
		for (RemovedContact removedContact : deleteds) {
			ContactKey key = new ContactKey(removedContact.getAddressBookId(), removedContact.getContactId()); 
			mapBuilder.add(key.serialiseAsFunambolKey());
		}
		return mapBuilder.build();
	}

	private Map<String, Contact> transformAsFunambolUpdated(List<Contact> updated) {
		Builder<String, Contact> mapBuilder = ImmutableMap.builder();
		for (Contact c : updated) {
			ContactKey key = new ContactKey(c.getFolderId(), c.getUid()); 
			mapBuilder.put(key.serialiseAsFunambolKey(), c);
		}
		return mapBuilder.build();
	}

	private Set<RemovedContact> getRemovedContacts(AddressBookChangesResponse sync) {
		return sync.getRemovedContacts() != null ? sync.getRemovedContacts() : ImmutableSet.<RemovedContact>of();
	}

	private List<Contact> getListUpdatedContact(AddressBookChangesResponse sync) {
		return sync.getUpdatedContacts() != null ? sync.getUpdatedContacts() : ImmutableList.<Contact>of();
	}

	private Date getLastSync(Timestamp since) {
		return since != null ? new Date(since.getTime()) : null;
	}

	private ContactKey getContactKeyFromSyncItemKey(SyncItemKey syncItemKey) throws InvalidFunambolKeyException {
		final String itemKey = getCheckedSyncItemKeyAsString(syncItemKey);
		return new ContactKey(itemKey);
	}

}
