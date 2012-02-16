package org.obm.funambol.model;

import java.util.regex.PatternSyntaxException;

import org.obm.funambol.exception.InvalidFunambolKeyException;

import com.google.common.base.Preconditions;


public class ContactKey {

	private Integer addressBookId; 
	private Integer contactId;
	
	public ContactKey(Integer addressBookId, Integer contactId){
		this.addressBookId = addressBookId;
		this.contactId = contactId;
	}
	
	public ContactKey(String funambolKey) throws InvalidFunambolKeyException{
		Preconditions.checkNotNull(funambolKey);
		try{
			String[] ids = funambolKey.split(":");
			if(ids == null || ids.length != 2){
				throw new InvalidFunambolKeyException("Invalid funambol key:"+funambolKey);
			}
			addressBookId = Integer.valueOf(ids[0]);
			contactId = Integer.valueOf(ids[1]);
		} catch (NumberFormatException e) {
			throw new InvalidFunambolKeyException("Invalid funambol key:"+funambolKey);
		} catch (PatternSyntaxException e) {
			throw new InvalidFunambolKeyException("Invalid funambol key:"+funambolKey);
		}
	}

	public Integer getAddressBookId() {
		return addressBookId;
	}

	public Integer getContactId() {
		return contactId;
	}
	
	public String serialiseAsFunambolKey(){
		return this.addressBookId+":"+this.contactId;
	}

}
