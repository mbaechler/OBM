package fr.aliasource.obm.items.converter;

import org.obm.sync.book.Contact;

public interface IContactConverter {

	com.funambol.common.pim.contact.Contact obmContactTofoundation(
			Contact obmcontact);
	
	Contact foundationContactToObm(com.funambol.common.pim.contact.Contact funis);

}
