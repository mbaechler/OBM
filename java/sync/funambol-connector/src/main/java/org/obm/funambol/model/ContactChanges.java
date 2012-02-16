package org.obm.funambol.model;

import java.util.List;
import java.util.Map;

import org.obm.sync.book.Contact;

public class ContactChanges {
	
	private Map<String, Contact> updated = null;
	private List<String> deleted = null;
	
	public ContactChanges(Map<String, Contact> updated,
			List<String> deleted) {
		this.updated = updated;
		this.deleted = deleted;
	}

	public Map<String, Contact> getUpdated() {
		return updated;
	}

	public List<String> getDeleted() {
		return deleted;
	}
	
}
