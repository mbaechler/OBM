package fr.aliasource.obm.items.manager;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.naming.NoPermissionException;

import org.obm.configuration.ContactConfiguration;
import org.obm.sync.auth.ServerFault;
import org.obm.sync.book.AddressBook;
import org.obm.sync.book.Contact;
import org.obm.sync.book.RemovedContact;
import org.obm.sync.client.book.BookClient;
import org.obm.sync.client.login.LoginService;
import org.obm.sync.exception.ContactAlreadyExistException;
import org.obm.sync.exception.ContactNotFoundException;
import org.obm.sync.items.AddressBookChangesResponse;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

import fr.aliasource.funambol.InvalidFunambolKeyException;
import fr.aliasource.funambol.OBMException;
import fr.aliasource.obm.items.converter.ObmContactConverter;

public class ContactSyncBean extends ObmManager {

	protected Map<String, Contact> updatedRest = null;
	protected List<String> deletedRest = null;

	private final BookClient bookClient;
	private final ContactConfiguration contactConfiguration;
	private final ObmContactConverter contactConverter;

	private TimeZone deviceTimeZone;
	
	public ContactSyncBean(final LoginService loginService, final BookClient bookClient, 
			final ContactConfiguration contactConfiguration, final ObmContactConverter contactConverter) {
		super(loginService);
		this.bookClient = bookClient;
		this.contactConfiguration = contactConfiguration;
		this.contactConverter = contactConverter;
	}

	public List<String> getAllItemKeys() throws OBMException {

		if (!syncReceived) {
			getSync(null);
		}

		List<String> keys = new LinkedList<String>();
		keys.addAll(updatedRest.keySet());

		return keys;
	}

	public List<String> getDeletedItemKeys(Timestamp since) throws OBMException {
		Calendar d = Calendar.getInstance();
		d.setTime(since);
		if (!syncReceived) {
			getSync(since);
		}
		return deletedRest;
	}

	public List<String> getUpdatedItemKeys(Timestamp since) throws OBMException {

		Calendar d = Calendar.getInstance();
		d.setTime(since);

		if (!syncReceived) {
			getSync(since);
		}

		List<String> keys = new LinkedList<String>();
		keys.addAll(updatedRest.keySet());

		return keys;
	}

	public com.funambol.common.pim.contact.Contact getItemFromId(String key) throws OBMException {

		Contact contact = updatedRest.get(key);

		if (contact == null) {
			logger.info(" item " + key
					+ " not found in updated -> get from sever");
			try {
				ContactKey contactKey = new ContactKey(key);
				contact = bookClient.getContactFromId(token, contactKey.getAddressBookId(), contactKey.getContactId());
			} catch (ServerFault e) {
				throw new OBMException(e.getMessage(),e);
			} catch (InvalidFunambolKeyException e) {
				throw new OBMException(e.getMessage(),e);
			} catch (ContactNotFoundException e) {
				throw new OBMException(e.getMessage(),e);
			}
		}

		com.funambol.common.pim.contact.Contact ret = contactConverter.obmContactTofoundation(contact);

		return ret;
	}

	public void removeItem(String key) throws OBMException {

		try {
			ContactKey contactKey = new ContactKey(key);
			bookClient.removeContact(token, contactKey.getAddressBookId(), contactKey.getContactId());
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

	public com.funambol.common.pim.contact.Contact updateItem(
			com.funambol.common.pim.contact.Contact contact)
			throws OBMException {

		Contact c = null;
		try {
			Contact obmContact = contactConverter.foundationContactToObm(contact);
			c = bookClient.modifyContact(token, obmContact.getFolderId(), obmContact);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage(),e);
		} catch (NoPermissionException e) {
			throw new OBMException(e.getMessage(),e);
		} catch (ContactNotFoundException e) {
			throw new OBMException(e.getMessage(),e);
		}

		return contactConverter.obmContactTofoundation(c);
	}

	public com.funambol.common.pim.contact.Contact addItem(
			com.funambol.common.pim.contact.Contact contact)
			throws OBMException {

		try {
			AddressBook defaultAddressBook = getDefaultAddressBook();
			Contact c = bookClient.createContact(token, defaultAddressBook.getUid(), contactConverter.foundationContactToObm(contact));
			return contactConverter.obmContactTofoundation(c);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		} catch (NoPermissionException e) {
			throw new OBMException(e.getMessage(),e);
		} catch (ContactAlreadyExistException e) {
			throw new OBMException(e.getMessage(),e);
		}

		
	}

	public List<String> getContactTwinKeys(
			com.funambol.common.pim.contact.Contact contact) {

		Contact c = contactConverter.foundationContactToObm(contact);

		if (logger.isDebugEnabled()) {
			logger.debug(" look twin of : " + c.getFirstname() + ","
					+ c.getLastname() + "," + c.getCompany());
		}
		c.setUid(null);
		return bookClient.getContactTwinKeys(token, c).getKeys();
	}

	private AddressBook getDefaultAddressBook() throws OBMException {
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

	private void getSync(Timestamp since) throws OBMException {
		try{
			Date lastSync = getLastSync(since);
			AddressBookChangesResponse sync = bookClient.getAddressBookSync(token, lastSync);
	
			List<Contact> updated = getListUpdateContact(sync);
			Set<RemovedContact> deleted = getRemovedContacts(sync);
			
			updatedRest = transformAsFunambolUpdated(updated);
			deletedRest = transformAsFunambolRemoved(deleted);
		}catch (ServerFault e) {
			throw new OBMException("The default address book is unobtainable");
		}
		syncReceived = true;
	}
	
	private List<String> transformAsFunambolRemoved(Set<RemovedContact> deleteds) {
		com.google.common.collect.ImmutableList.Builder<String> mapBuilder = ImmutableList.builder();
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

	private List<Contact> getListUpdateContact(AddressBookChangesResponse sync) {
		return sync.getUpdatedContacts() != null ? sync.getUpdatedContacts() : ImmutableList.<Contact>of();
	}

	private Date getLastSync(Timestamp since) {
		return since != null ? new Date(since.getTime()) : null;
	}

	public void setDeviceTimeZone(TimeZone deviceTimeZone) {
		this.deviceTimeZone = deviceTimeZone;
		if (deviceTimeZone == null) {
			this.deviceTimeZone = TimeZone.getTimeZone("Europe/Paris");
		}
		logger.info("device timezone set to: "+this.deviceTimeZone);
	}

}
