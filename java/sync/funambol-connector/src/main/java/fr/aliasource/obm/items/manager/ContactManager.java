package fr.aliasource.obm.items.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.obm.sync.auth.ContactNotFoundException;
import org.obm.sync.auth.ServerFault;
import org.obm.sync.book.BookType;
import org.obm.sync.book.Contact;
import org.obm.sync.client.ISyncClient;
import org.obm.sync.client.book.BookClient;
import org.obm.sync.items.ContactChangesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.aliasource.funambol.OBMException;
import fr.aliasource.obm.items.converter.ObmContactConverter;

public class ContactManager extends ObmManager {

	protected Map<String, Contact> updatedRest = null;
	protected List<String> deletedRest = null;

	private BookClient binding;
	private ObmContactConverter contactConverter;
	private BookType book;

	private static final Logger logger = LoggerFactory.getLogger(ContactManager.class);
	private TimeZone deviceTimeZone;

	public ContactManager(BookClient binding, ObmContactConverter contactConverter) {
		this.binding = binding;
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

	public BookType getBook() {
		return book;
	}

	public void setBook(BookType book) {
		this.book = book;
	}

	public BookClient getBinding() {
		return binding;
	}

	public String[] getNewItemKeys(Timestamp since) {

		Calendar d = Calendar.getInstance();
		d.setTime(since);
		String[] keys = null;

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
				contact = binding.getContactFromId(token, book, key);
			} catch (ServerFault e) {
				throw new OBMException(e.getMessage());
			}
		}

		com.funambol.common.pim.contact.Contact ret = contactConverter.obmContactTofoundation(contact);

		return ret;
	}

	public void removeItem(String key) throws OBMException {

		try {
			binding.removeContact(token, book, key);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		} catch (ContactNotFoundException e) {
			throw new OBMException(e.getMessage());
		}
	}

	public com.funambol.common.pim.contact.Contact updateItem(
			com.funambol.common.pim.contact.Contact contact)
			throws OBMException {

		Contact c = null;
		try {
			c = binding.modifyContact(token, book, contactConverter.foundationContactToObm(contact));
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		}

		return contactConverter.obmContactTofoundation(c);
	}

	public com.funambol.common.pim.contact.Contact addItem(
			com.funambol.common.pim.contact.Contact contact)
			throws OBMException {

		Contact c = null;

		try {
			c = binding.createContactWithoutDuplicate(token, book,
					contactConverter.foundationContactToObm(contact));
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		}

		return contactConverter.obmContactTofoundation(c);
	}

	public List<String> getContactTwinKeys(
			com.funambol.common.pim.contact.Contact contact)
			throws OBMException {

		Contact c = contactConverter.foundationContactToObm(contact);

		if (logger.isDebugEnabled()) {
			logger.debug(" look twin of : " + c.getFirstname() + ","
					+ c.getLastname() + "," + c.getCompany());
		}

		try {
			c.setUid(null);
			return binding.getContactTwinKeys(token, book, c).getKeys();
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		}
	}

	// ---------------- Private methods ----------------------------------

	private void getSync(Timestamp since) throws OBMException {
		Date d = null;
		if (since != null) {
			d = new Date(since.getTime());
		}

		ContactChangesResponse sync = null;
		// get modified items
		try {
			sync = binding.getSync(token, book, d);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		}

		List<Contact> updated = new LinkedList<Contact>();
		if (sync.getUpdated() != null) {
			updated = sync.getUpdated();
		}
		Set<Integer> deleted = new HashSet<Integer>();
		if (sync.getRemoved() != null) {
			deleted = sync.getRemoved();
		}
		// apply restriction(s)
		updatedRest = new HashMap<String, Contact>();
		deletedRest = new ArrayList<String>();
		// String owner = "";
		// String user = token.getUser();
		for (Contact c : updated) {
			updatedRest.put("" + c.getUid(), c);
		}

		for (Integer i : deleted) {
			deletedRest.add(i.toString());
		}

		syncReceived = true;
	}
	
	@Override
	protected ISyncClient getSyncClient() {
		return binding;
	}

	public void setDeviceTimeZone(TimeZone deviceTimeZone) {
		this.deviceTimeZone = deviceTimeZone;
		if (deviceTimeZone == null) {
			this.deviceTimeZone = TimeZone.getTimeZone("Europe/Paris");
		}
		logger.info("device timezone set to: "+this.deviceTimeZone);
	}

}
