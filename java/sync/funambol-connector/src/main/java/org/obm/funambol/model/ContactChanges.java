package org.obm.funambol.model;

import java.util.Map;
import java.util.Set;

import org.obm.sync.book.Contact;
import org.obm.sync.book.ContactKey;

public class ContactChanges {
	
	private Map<ContactKey, Contact> updated = null;
	private Set<ContactKey> deleted = null;
	
	public ContactChanges(Map<ContactKey, Contact> updated,
			Set<ContactKey> deleted) {
		this.updated = updated;
		this.deleted = deleted;
	}

	public Map<ContactKey, Contact> getUpdated() {
		return updated;
	}

	public Set<ContactKey> getDeleted() {
		return deleted;
	}
	
}
