package org.obm.sync.book;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.obm.sync.items.AbstractItemsWriter;
import org.obm.sync.items.AddressBookChangesResponse;
import org.obm.sync.items.ContactChanges;
import org.obm.sync.items.ContactChangesResponse;
import org.obm.sync.items.FolderChanges;
import org.obm.sync.items.FolderChangesResponse;
import org.obm.sync.utils.DOMUtils;
import org.obm.sync.utils.DateHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Serializes address book items to XML
 */
public class BookItemsWriter extends AbstractItemsWriter {

	public void appendContact(Element root, Contact contact) {
		Element c = root;
		if (!"contact".equals(root.getNodeName())) {
			c = DOMUtils.createElement(root, "contact");
		}

		if (contact.getUid() != null) {
			c.setAttribute("uid", "" + contact.getUid());
		}

		c.setAttribute("collected", "" + contact.isCollected());

		createIfNotNull(c, "commonname", contact.getCommonname());
		createIfNotNull(c, "first", contact.getFirstname());
		createIfNotNull(c, "last", contact.getLastname());
		createIfNotNull(c, "service", contact.getService());
		createIfNotNull(c, "title", contact.getTitle());
		createIfNotNull(c, "aka", contact.getAka());
		createIfNotNull(c, "comment", contact.getComment());
		createIfNotNull(c, "company", contact.getCompany());

		createIfNotNull(c, "middlename", contact.getMiddlename());
		createIfNotNull(c, "suffix", contact.getSuffix());
		createIfNotNull(c, "manager", contact.getManager());
		createIfNotNull(c, "assistant", contact.getAssistant());
		createIfNotNull(c, "spouse", contact.getSpouse());
		if(contact.getFolderId() != null){
			createIfNotNull(c, "addressbookid", String.valueOf(contact.getFolderId()));
		}
		
		String bday = null;
		if (contact.getBirthday() != null) {
			bday = DateHelper.asString(contact.getBirthday());
		}
		createIfNotNull(c, "birthday", bday);

		String anni = null;
		if (contact.getAnniversary() != null) {
			anni = DateHelper.asString(contact.getAnniversary());
		}
		createIfNotNull(c, "anniversary", anni);
		createIfNotNull(c, "caluri", contact.getCalUri());
		
		addPhones(c, contact.getPhones());
		addAddress(c, contact.getAddresses());
		addWebsite(c, contact.getWebsites());
		addEmail(c, contact.getEmails());
		addIM(c, contact.getImIdentifiers());
	}

	public void appendAddressBook(Element root, AddressBook book) {
		Element c = root;
		if (!"book".equals(root.getNodeName())) {
			c = DOMUtils.createElement(root, "book");
		}

		c.setAttribute("uid", Integer.toString(book.getUid()));
		c.setAttribute("name", book.getName());
		c.setAttribute("readonly", String.valueOf(book.isReadOnly()));
	}

	
	private void addIM(Element root,
			Map<String, InstantMessagingId> imIdentifiers) {
		Element e = DOMUtils.createElement(root, "instantmessaging");
		for (Entry<String, InstantMessagingId> entry: imIdentifiers.entrySet()) {
			Element c = DOMUtils.createElement(e, "im");
			InstantMessagingId p = entry.getValue();
			c.setAttribute("label", entry.getKey());
			c.setAttribute("protocol", p.getProtocol());
			c.setAttribute("address", p.getId());
		}
	}

	private void addEmail(Element root, Map<String, Email> emails) {
		Element e = DOMUtils.createElement(root, "emails");
		for (Entry<String, Email> entry: emails.entrySet()) {
			Element c = DOMUtils.createElement(e, "mail");
			Email p = entry.getValue();
			c.setAttribute("label", entry.getKey());
			c.setAttribute("value", p.getEmail());
		}
	}

	private void addWebsite(final Element root, final HashSet<Website> websites) {
		final Element e = DOMUtils.createElement(root, "websites");
		for (final Website website: websites) {
			Element c = DOMUtils.createElement(e, "site");
			c.setAttribute("label", website.getLabel());
			c.setAttribute("url", website.getUrl());
		}
	}

	private void addAddress(Element root, Map<String, Address> addresses) {
		Element e = DOMUtils.createElement(root, "addresses");
		for (Entry<String, Address> entry: addresses.entrySet()) {
			Element c = DOMUtils.createElement(e, "address");
			Address p = entry.getValue();
			c.setAttribute("label", entry.getKey());
			c.setAttribute("zip", p.getZipCode());
			c.setAttribute("town", p.getTown());
			c.setAttribute("country", p.getCountry());
			c.setAttribute("expressPostal", p.getExpressPostal());
			c.setAttribute("state", p.getState());
			c.setTextContent(p.getStreet());
		}
	}

	private void addPhones(Element root, Map<String, Phone> phones) {
		Element e = DOMUtils.createElement(root, "phones");
		for (Entry<String, Phone> entry: phones.entrySet()) {
			Element c = DOMUtils.createElement(e, "phone");
			Phone p = entry.getValue();
			c.setAttribute("label", entry.getKey());
			c.setAttribute("number", p.getNumber());
		}
	}

	public Document writeChanges(ContactChangesResponse cc) {
		Document doc = null;
		try {
			doc = DOMUtils.createDoc(
					"http://www.obm.org/xsd/sync/contact-changes.xsd",
					"contact-changes");
			Element root = doc.getDocumentElement();
			root.setAttribute("lastSync", DateHelper.asString(cc.getLastSync()));

			createContactChanges(cc.getChanges(), root);

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		return doc;
	}

	private void createContactChanges(ContactChanges cc, Element root) {
		
		Element removed = DOMUtils.createElement(root, "removed");
		for (int eid : cc.getRemoved()) {
			Element e = DOMUtils.createElement(removed, "contact");
			e.setAttribute("uid", "" + eid);
		}

		Element updated = DOMUtils.createElement(root, "updated");
		for (Contact ev : cc.getUpdated()) {
			appendContact(updated, ev);
		}
	}

	public String getContactAsString(Contact contact) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Document doc = DOMUtils.createDoc(
					"http://www.obm.org/xsd/sync/contact.xsd", "contact");
			Element root = doc.getDocumentElement();
			appendContact(root, contact);
			DOMUtils.serialise(doc, out);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return out.toString();
	}

	public Document writeFolderChanges(FolderChangesResponse fc) {
		Document doc = null;
		try {
			doc = DOMUtils.createDoc(
					"http://www.obm.org/xsd/sync/folder-changes.xsd",
					"folder-changes");
			Element root = doc.getDocumentElement();
			root.setAttribute("lastSync", DateHelper.asString(fc.getLastSync()));

			createFolderChanges(fc.getFolderChanges(), root);

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		return doc;
	}

	private void createFolderChanges(FolderChanges fc, Element root) {
		Element removed = DOMUtils.createElement(root, "removed");
		for (int eid : fc.getRemoved()) {
			Element e = DOMUtils.createElement(removed, "folder");
			e.setAttribute("uid", "" + eid);
		}

		Element updated = DOMUtils.createElement(root, "updated");
		for (Folder ev : fc.getUpdated()) {
			appendFolder(updated, ev);
		}
	}

	public void appendFolder(Element root, Folder folder) {
		Element f = root;
		if (!"folder".equals(root.getNodeName())) {
			f = DOMUtils.createElement(root, "folder");
		}

		if (folder.getUid() != null) {
			f.setAttribute("uid", "" + folder.getUid());
		}

		createIfNotNull(f, "name", folder.getName());
		createIfNotNull(f, "ownerDisplayName", folder.getOwnerDisplayName());
	}

	public Document writeAddressBookChanges(AddressBookChangesResponse response) {
		Document doc = null;
		try {
			doc = DOMUtils.createDoc(
					"http://www.obm.org/xsd/sync/folder-changes.xsd",
					"addressbook-changes");
			Element root = doc.getDocumentElement();
			root.setAttribute("lastSync", DateHelper.asString(response.getLastSync()));

			Element addressbooks = DOMUtils.createElement(root, "addressbooks");
			createFolderChanges(response.getBooksChanges(), addressbooks);
			Element contacts = DOMUtils.createElement(root, "contacts");
			createContactChanges(response.getContactChanges(), contacts);

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		return doc;
	}

}
