package org.obm.sync.book;

import java.util.regex.PatternSyntaxException;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class ContactKey {
	
	protected Integer contactId;
	protected Integer addressBookId;

	public ContactKey(Integer contactId, Integer addressBookId) {
		super();
		this.contactId = contactId;
		this.addressBookId = addressBookId;
	}
	
	public ContactKey(String funambolKey) throws IllegalArgumentException {
		Preconditions.checkNotNull(funambolKey);
		try{
			String[] ids = funambolKey.split(":");
			if(ids == null || ids.length != 2){
				throw new IllegalArgumentException("Invalid funambol key:"+funambolKey);
			}
			addressBookId = Integer.valueOf(ids[0]);
			contactId = Integer.valueOf(ids[1]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid funambol key:"+funambolKey);
		} catch (PatternSyntaxException e) {
			throw new IllegalArgumentException("Invalid funambol key:"+funambolKey);
		}
	}

	protected ContactKey() {
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

	public String serialiseAsFunambolKey(){
		return this.addressBookId+":"+this.contactId;
	}
	
	
}
