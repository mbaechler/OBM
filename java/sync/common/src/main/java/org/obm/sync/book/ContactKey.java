package org.obm.sync.book;

import com.google.common.base.Objects;

public class ContactKey {
	
	private Integer contactId;
	private Integer addressBookId;

	public ContactKey(Integer contactId, Integer addressBookId) {
		super();
		this.contactId = contactId;
		this.addressBookId = addressBookId;
	}

	public Integer getContactId() {
		return contactId;
	}
	
	public Integer getAddressBookId() {
		return addressBookId;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(contactId, addressBookId);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof ContactKey) {
			ContactKey that = (ContactKey) object;
			
			return Objects.equal(this.contactId, that.contactId) && 
					Objects.equal(this.addressBookId, that.addressBookId) ;
		}
		return false;
	}

	@Override
	public String toString() {
		return "RemovedContact [contactId=" + contactId + ", addressBookId="
				+ addressBookId + "]";
	}
	
	
	
}
